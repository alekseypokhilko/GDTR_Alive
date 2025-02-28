package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.Color;

public class GameTheme {
    private Color gameBackgroundColor = new Color(255, 255, 255);
    private Color trackLineColor = new Color(0, 255, 0);
    private Color perspectiveColor = new Color(0, 170, 0);
    private Color startFlagColor = new Color(0, 0, 0);
    private Color finishFlagColor = new Color(0, 0, 0);

    public Color getGameBackgroundColor() {
        return gameBackgroundColor;
    }

    public Color getTrackLineColor() {
        return trackLineColor;
    }

    public Color getPerspectiveColor() {
        return perspectiveColor;
    }

    public Color getStartFlagColor() {
        return startFlagColor;
    }

    public Color getFinishFlagColor() {
        return finishFlagColor;
    }

    public void setGameBackgroundColor(Color gameBackgroundColor) {
        this.gameBackgroundColor = gameBackgroundColor;
    }

    public void setTrackLineColor(Color trackLineColor) {
        this.trackLineColor = trackLineColor;
    }

    public void setPerspectiveColor(Color perspectiveColor) {
        this.perspectiveColor = perspectiveColor;
    }

    public void setStartFlagColor(Color startFlagColor) {
        this.startFlagColor = startFlagColor;
    }

    public void setFinishFlagColor(Color finishFlagColor) {
        this.finishFlagColor = finishFlagColor;
    }
}
