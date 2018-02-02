package io.osoon.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import io.osoon.domain.Meeting;
import io.osoon.domain.User;

/**
 * @author 김제준 (dosajun@gmail.com)
 * @since 2017-09-19
 */
@Repository
public interface MeetingRepository extends Neo4jRepository<Meeting, Long> {
	@Query(value = "MATCH (u:User)-[:ATTEND]->(m:Meeting) WHERE id(m)={0} RETURN u"
		, countQuery = "MATCH (u:User)-[:ATTEND]->(m:Meeting) WHERE id(m)={0} RETURN count(*)")
	Page<User> getUsersThatJoined(long meetingId, Pageable page);

	@Query("MATCH (u:User)-[:MAKE]->(m:Meeting) WHERE id(m)={0} and id(u)={1} RETURN count(m) > 0 as c")
	boolean isOwner(long meetingId, long userId);

	/**
	 * 가장 최근에 생성된 PUBLISHED 모임을 title contains 로 찾아서 조회
	 * @TODO 참여자 수를 검색 조건에 넣을 것인가.
	 * @param title
	 * @param status
	 * @param pageable
	 * @return
	 */
	Page<Meeting> findByTitleContainsAndMeetingStatus(String title, Meeting.MeetingStatus status, Pageable pageable);

	/**
	 * 참여자 수 기준
	 * @TODO 북마크, 댓글 수(disquss 쓰면 좀 힘듬), 모임 생성자의 상태(모임 개설 횟수나 기타 등등, stack overflow 처럼 뱃지 만드는것도 괜찮을 듯. )을 추후에 넣을 것
	 * @return
	 */
	@Query(value = "MATCH (m:Meeting)-[r:ATTEND]-(u:User) WHERE m.meetingStatus = 'PUBLISHED' RETURN m ORDER BY COUNT(r) DESC LIMIT 10")
	List<Meeting> listPopular();

	@Query(value = "MATCH (m:Meeting) WHERE m.meetingStatus = 'PUBLISHED' RETURN m ORDER BY m.createdAt DESC LIMIT 10")
	List<Meeting> listRecent();

}