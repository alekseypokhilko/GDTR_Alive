package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.dto.GameProperties;
import org.happysanta.gdtralive.game.api.dto.PackLevel;
import org.happysanta.gdtralive.game.api.dto.TrackReference;

import java.util.ArrayList;
import java.util.List;

public class Mod {
	private String guid;
	private String name;
	private String author;
	private String date;
	private GameProperties properties;
	private List<PackLevel> levels = new ArrayList<>();


	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PackLevel> getLevels() {
		return levels;
	}

	public String getAuthor() {
		return author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String[] getLevelTrackNames(int level) {
		List<String> names = new ArrayList<>();
		for (TrackReference track : levels.get(level).getTracks()) {
			names.add(track.getName());
		}
		return names.toArray(new String[0]);
	}

	public List<Integer> getTrackCounts() {
		List<Integer> counts = new ArrayList<>();
		for (PackLevel level : levels) {
			counts.add(level.getTracks().size());
		}
		return counts;
	}
}
