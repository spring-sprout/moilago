package com.moilago.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.moilago.server.sample.domain.Meeting;
import com.moilago.server.sample.repository.MeetingRepository;
import com.moilago.server.sample.domain.User;
import com.moilago.server.sample.repository.UserRepository;
import com.moilago.server.sample.service.MeetingService;

/**
 * @author 김제준 (reperion.kim@navercorp.com)
 * @since 2017-09-19
 */
@RestController
@RequestMapping("/samples/meeting/")
public class SampleMeetingController {

	@Autowired private MeetingRepository repository;
	@Autowired private MeetingService service;
	@Autowired private UserRepository userRepository;

	@PutMapping("make")
	public Meeting make(long userId, String title, String contents) {
		Meeting meeting = new Meeting(title, contents);

		User user = userRepository.findById(userId).get();
		user.make(meeting);
		userRepository.save(user);
		return meeting;
	}

	@PostMapping("delete")
	public User delete(long userId, long meetingId) {
		Optional<User> user = userRepository.findById(userId);
		Optional<Meeting> meeting = repository.findById(meetingId);

		User existUser = user.get();
		existUser.deleteMeeting(meeting.get());

		userRepository.save(existUser);
		return existUser;
	}

	@GetMapping("show")
	public Meeting show(long id) {
		return repository.findById(id).get();
	}

	@GetMapping("list")
	public Page<Meeting> list() {
		return repository.findAll(PageRequest.of(0, 10));
	}

	@PostMapping("join")
	public User join(long userId, long meetingId) {
		Optional<User> user = userRepository.findById(userId);
		Optional<Meeting> meeting = repository.findById(meetingId);

		User existUser = user.get();
		existUser.attendTo(meeting.get());

		//userRepository.save(existUser);
		service.join(meeting.get(), user.get());


		return existUser;
	}

	@PostMapping("leave")
	public User leave(long userId, long meetingId) {
		Optional<User> user = userRepository.findById(userId);
		Optional<Meeting> meeting = repository.findById(meetingId);

		User existUser = user.get();
		existUser.leaveMoim(meeting.get());

		userRepository.save(existUser);

		return existUser;
	}

	@GetMapping("attendees")
	public Page<User> attendees(long meetingId) {
		return repository.getUsersThatJoined(meetingId, PageRequest.of(0, 10));
	}
}
