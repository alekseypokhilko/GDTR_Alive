package org.happysanta.gdtralive.game.mod;

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
}
