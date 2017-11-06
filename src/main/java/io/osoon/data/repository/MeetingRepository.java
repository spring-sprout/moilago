package io.osoon.data.repository;

import io.osoon.data.domain.AttendMeeting;
import io.osoon.data.domain.Meeting;
import io.osoon.data.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author 김제준 (dosajun@gmail.com)
 * @since 2017-09-19
 */
@Repository
public interface MeetingRepository extends Neo4jRepository<Meeting, Long> {
	@Query(value = "MATCH (u:User)-[:ATTEND]->(m:Meeting) WHERE id(m)={0} RETURN u"
		, countQuery = "MATCH (u:User)-[:ATTEND]->(m:Meeting) WHERE id(m)={0} RETURN count(*)")
	Page<User> getUsersThatJoined(long meetingId, Pageable page);

	@Query("MATCH (u:User)-[:ATTEND]->(m:Meeting) WHERE id(m)={0} and id(u)={1} RETURN count(m) > 0 as c")
	boolean isJoinMeeting(long meetingId, long userId);

	@Query("MATCH (u:User)-[:MAKE]->(m:Meeting) WHERE id(m)={0} and id(u)={1} RETURN count(m) > 0 as c")
	boolean isOwner(long meetingId, long userId);

	@Query("MATCH (u:User)-[r:ATTEND]-(m:Meeting) WHERE id(u) = {0} AND id(m) = {1} RETURN u,r,m")
	Optional<AttendMeeting> getAttendMeetingFromUserIdAndMeetingId(long userId, long meetingId);

//	@Query("MATCH (m:Meeting)\n" +
//            "OPTIONAL MATCH (m)-[r1:MEET_AT]-(l:MeetingLocation)\n" +
//            "OPTIONAL MATCH (m)-[r2:IS_ABOUT]-(t:Topic)\n" +
//            "OPTIONAL MATCH (m)-[r3:MANAGED_BY]-(u:User)\n" +
//            "WHERE id(m) = {0} \n" +
//            "RETURN *")
//	Optional<Meeting> findById(long id);

}
