package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.TrackParams;

import java.util.LinkedHashMap;

public class TrackReference {
    private String name;
    private String guid;
    private LinkedHashMap<String, String> gameProperties;
    private LinkedHashMap<String, String> leagueProperties;
    private TrackParams data;

    public TrackParams getData() {
        return data;
    }

    public void setData(TrackParams data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public LinkedHashMap<String, String> getGameProperties() {
        return gameProperties;
    }

    public void setGameProperties(LinkedHashMap<String, String> gameProperties) {
        this.gameProperties = gameProperties;
    }

    public LinkedHashMap<String, String> getLeagueProperties() {
        return leagueProperties;
    }

    public void setLeagueProperties(LinkedHashMap<String, String> leagueProperties) {
        this.leagueProperties = leagueProperties;
    }
}
