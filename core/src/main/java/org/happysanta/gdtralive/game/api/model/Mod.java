package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.dto.GameTheme;
import org.happysanta.gdtralive.game.api.dto.LeagueTheme;
import org.happysanta.gdtralive.game.api.dto.LevelPack;
import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Mod {
    private String guid;
    private String name;
    private String author;
    private String date;
    private GameTheme gameTheme;
    private List<String> levelNames;
    private List<LeagueTheme> leagueThemes;
    private List<LevelPack> levels = new ArrayList<>();

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

    public List<LevelPack> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelPack> levels) {
        this.levels = levels;
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

    public GameTheme getGameTheme() {
        return gameTheme;
    }

    public void setGameTheme(GameTheme gameTheme) {
        this.gameTheme = gameTheme;
    }

    public List<String> getLevelNames() {
        return levelNames;
    }

    public void setLevelNames(List<String> levelNames) {
        this.levelNames = levelNames;
    }

    public List<LeagueTheme> getLeagueThemes() {
        return leagueThemes;
    }

    public void setLeagueThemes(List<LeagueTheme> leagueThemes) {
        this.leagueThemes = leagueThemes;
    }


    public List<Integer> getTrackCounts() {
        List<Integer> counts = new ArrayList<>();
        for (LevelPack level : levels) {
            counts.add(level.getTracks().size());
        }
        return counts;
    }

    public LeagueTheme getLeagueThemes(int league) {
        return leagueThemes.get(league);
    }

    public Mod pack() {
        return Utils.packMod(this);
    }

    public Mod unpack() {
        return Utils.unpackMod(this);
    }
}
