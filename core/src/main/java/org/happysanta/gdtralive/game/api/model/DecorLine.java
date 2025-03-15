package org.happysanta.gdtralive.game.api.model;

public class DecorLine {
    private Color color;
    private Color perspectiveColor;
    private Boolean perspective;
    private int[][] points;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int[][] getPoints() {
        return points;
    }

    public void setPoints(int[][] points) {
        this.points = points;
    }

    public Boolean getPerspective() {
        return perspective;
    }

    public void setPerspective(Boolean perspective) {
        this.perspective = perspective;
    }

    public Color getPerspectiveColor() {
        return perspectiveColor;
    }

    public void setPerspectiveColor(Color perspectiveColor) {
        this.perspectiveColor = perspectiveColor;
    }
}
