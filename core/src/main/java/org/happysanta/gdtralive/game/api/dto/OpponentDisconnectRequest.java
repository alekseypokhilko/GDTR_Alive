package org.happysanta.gdtralive.game.api.dto;

public class OpponentDisconnectRequest {
    private String id;
    private String roomId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
