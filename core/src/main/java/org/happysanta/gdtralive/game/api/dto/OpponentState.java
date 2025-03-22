package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.EngineStateRecord;

public class OpponentState {
    private String name;
    private String status;
    private EngineStateRecord state;
    private Long count = System.currentTimeMillis();

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
