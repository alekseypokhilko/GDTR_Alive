package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ModEntity {

	private long id = 0;
	private String guid;
	private String name;
	private List<Integer> trackCountsByLevel = new ArrayList<>();
	private List<Integer> unlockedTracks = new ArrayList<>();
	private int selectedTrack = 0;
	private int selectedLevel = 0;
	private int selectedLeague = 0;
	private int unlockedLevels = 0;
	private int unlockedLeagues = 0;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTracksCount(int level) {
		//todo refactoring related methods
		return this.trackCountsByLevel.get(level);
	}

	public int getLevelsCount() {
		return trackCountsByLevel.size();
	}

	public void setTrackCountsByLevel(String counts) {
		this.trackCountsByLevel = Utils.parseIntList(counts);
	}

	public int getUnlockedTracksCount(int level) {
		return unlockedTracks.get(level);
	}

	public String getUnlockedTracksString() {
		return Utils.toJson(unlockedTracks);
	}

	public void setUnlockedTracks(String counts) {
		try {
			this.unlockedTracks = Utils.parseIntList(counts);
		} catch (Exception e) {
			e.printStackTrace();
			this.unlockedTracks = Utils.parseIntList(String.format("[%s]", counts));
		}
	}

	public void unlockAllTracks() {
		for (int i = 0; i < trackCountsByLevel.size(); i++) {
			unlockedTracks.set(i, trackCountsByLevel.get(i) - 1);
		}
	}

	public void setUnlockedTracks(int level, int value) {
		unlockedTracks.set(level, value);
	}

	public int getSelectedTrack() {
		return selectedTrack;
	}

	public int getSelectedLevel() {
		return selectedLevel;
	}

	public int getSelectedLeague() {
		return selectedLeague;
	}

	public void setSelectedTrack(int selectedTrack) {
		this.selectedTrack = selectedTrack;
	}

	public void setSelectedLevel(int selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public void setSelectedLeague(int selectedLeague) {
		this.selectedLeague = selectedLeague;
	}

	public int getUnlockedLevels() {
		return unlockedLevels;
	}

	public int getUnlockedLeagues() {
		return unlockedLeagues;
	}

	public void setUnlockedLevels(int unlockedLevels) {
		this.unlockedLevels = unlockedLevels;
	}

	public void setUnlockedLeagues(int unlockedLeagues) {
		this.unlockedLeagues = unlockedLeagues;
	}

	public void initIfClear() {
		boolean clear = true;
		for (Integer i : unlockedTracks) {
			if (i > 0) {
				clear = false;
				break;
			}
		}
		if (unlockedLevels == 0 && clear) {
			setUnlockedLeagues(0);
			setUnlockedLevels(1);
			for (int i = 0; i < unlockedTracks.size(); i++) {
				if (i < 2) {
					unlockedTracks.set(i, 0);
				} else {
					unlockedTracks.set(i, -1);
				}
			}
		}
	}
}
