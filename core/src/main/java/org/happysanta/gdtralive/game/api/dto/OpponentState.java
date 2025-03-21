package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.EngineStateRecord;

public class OpponentState {
    private String name;
    private EngineStateRecord state;

    public OpponentState() {
    }

    public OpponentState(String name, EngineStateRecord state) {
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EngineStateRecord getState() {
        return state;
    }

    public void setState(EngineStateRecord state) {
        this.state = state;
    }
}
