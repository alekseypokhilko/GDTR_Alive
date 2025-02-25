package org.happysanta.gdtralive.game.api.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ModEntity {

	private long id = 0;
	private String guid;
	private String name;
	private List<Integer> count = new ArrayList<>();
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
		return this.count.get(level);
	}

	public void setCount(String counts) {
		this.count = new Gson().fromJson(counts, new TypeToken<List<Integer>>() {});
	}

	public int getUnlockedTracksCount(int level) {
		return unlockedTracks.get(level);
	}

	public String getUnlockedTracks() {
		return new Gson().toJson(unlockedTracks);
	}

	public void setUnlockedTracks(String counts) {
		try {
			this.unlockedTracks = new Gson().fromJson(counts, new TypeToken<List<Integer>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			this.unlockedTracks = new Gson().fromJson("["+counts+"]", new TypeToken<List<Integer>>() {
			});
		}
	}

	public void unlockAllTracks() {
		for (int i = 0; i < count.size(); i++) {
			unlockedTracks.set(i, count.get(i) - 1);
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
