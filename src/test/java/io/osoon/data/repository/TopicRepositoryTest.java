package io.osoon.data.repository;

import io.osoon.data.domain.queryresult.TopicView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 김제준 (dosajun@gmail.com)
 * @since 2017-10-27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TopicRepositoryTest {
	@Autowired TopicRepository repository;

	@Test
	public void findAllWithCount() {
		Page<TopicView> allWithCount = repository.findAllWithCount(PageRequest.of(0, 10));
		allWithCount.forEach(System.out::println);
	}
}