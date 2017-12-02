package io.osoon.service;

import io.osoon.config.properties.OSoonProperties;
import io.osoon.domain.Meeting;
import io.osoon.domain.User;
import io.osoon.domain.UserFile;
import io.osoon.repository.UserFileRepository;
import io.osoon.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author whiteship
 */
@Service
public class UserFileService {

    private Logger logger = LoggerFactory.getLogger(UserFileService.class);

    @Autowired
    private OSoonProperties properties;

    @Autowired
    private UserFileRepository userFileRepository;

    public UserFile store(User user, MultipartFile file) {
        Path uploadFileRootPath = Paths.get(properties.getUploadFileRootPath());
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new StorageException("Name of the file should not be null");
        }

        String filename = StringUtils.cleanPath(originalFilename);

        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) { // This is a security check
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }

            UserFile userFile = UserFile.of(file, user);
            Path resolvedPath = uploadFileRootPath.resolve(userFile.getPath());

            boolean mkdirs = resolvedPath.getParent().toFile().mkdirs();
            if (!mkdirs) {
                throw new StorageException("Failed to create directory for " + resolvedPath);
            }

            Files.copy(file.getInputStream(), resolvedPath, StandardCopyOption.REPLACE_EXISTING);

            return userFileRepository.save(userFile);
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    public UserFile store(User user, MultipartFile file, Meeting meeting) {
        UserFile userFile = this.store(user, file);
        userFile.setMeeting(meeting);
        if (userFile.getFileType() == UserFile.FileType.IMAGE) {
            this.createThumbnail(userFile);
        }

        return userFileRepository.save(userFile);
    }

    public void delete(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return;
        }

        UserFile userFile = userFileRepository.findByPath(imagePath, 0).orElseThrow(
                () -> new StorageException("UserFile not found with '" + imagePath + "'"));

        userFileRepository.delete(userFile);

        try {
            Files.deleteIfExists(Paths.get(properties.getUploadFileRootPath(), imagePath));
        } catch (IOException e) {
            logger.error("Failed to delete '{}", imagePath);
        }
    }

    @Async
    public String createThumbnail(UserFile userFile) {
        String path = userFile.getPath();
        Path originalImagePath = Paths.get(properties.getUploadFileRootPath(), path);

        try {
            BufferedImage originalImage = ImageIO.read(originalImagePath.toFile());
            if (originalImage != null) {
                BufferedImage thumbnailImage = this.createThumbnailImage(originalImage, 300, 200);
                String ext = path.substring(path.lastIndexOf(".") + 1);
                Path thumbNailPath = Paths.get(properties.getUploadFileRootPath(), userFile.getThumbnailPath());
                ImageIO.write(thumbnailImage, ext, Files.newOutputStream(thumbNailPath));
                return thumbNailPath.toString();
            }
        } catch (IOException e) {
            logger.error("Failed to create thumbnail of '{}'", path);
        }

        return "";
    }

    private BufferedImage createThumbnailImage(BufferedImage in, int w, int h) {
        // scale w, h to keep aspect constant
        double outputAspect = 1.0 * w / h;
        double inputAspect = 1.0 * in.getWidth() / in.getHeight();

        if (outputAspect < inputAspect) {
            // width is limiting factor; adjust height to keep aspect
            h = (int)(w/inputAspect);
        } else {
            // height is limiting factor; adjust width to keep aspect
            w = (int)(h*inputAspect);
        }

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(in, 0, 0, w, h, null);
        g2.dispose();
        return bi;
    }

    public Resource loadAsResource(String path) {
        Path filePath = Paths.get(properties.getUploadFileRootPath(), path);
        Resource resource = new FileSystemResource(filePath.toFile());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        else {
            throw new StorageException("Could not read file: " + path);
        }
    }

}
