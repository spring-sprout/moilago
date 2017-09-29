package com.moilago.server.sample.domain;

import java.time.LocalDateTime;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 김제준 (reperion.kim@navercorp.com)
 * @since 2017-09-18
 */
@NodeEntity
@Setter @Getter @ToString
@NoArgsConstructor
public class Meeting {
	@GraphId Long id;
	String title;
	String contents;
	String titleImage;
	int maxAttendees;

	LocalDateTime meetAt;
	String locationText;
	double latitude;
	double longitude;

	LocalDateTime attendStartAt;
	LocalDateTime attendEndAt;

	public Meeting(String title, String contents) {
		this.title = title;
		this.contents = contents;
	}
}
