package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.Color;

import java.util.ArrayList;
import java.util.List;

public class LeagueTheme {
    private String name;
    private Color backWheelDotColor;
    private Color frontWheelDotColor;
    private Color backWheelsColor = new Color(0, 0, 0);
    private Color backWheelsSpokeColor = new Color(0, 0, 0);
    private Color frontWheelsColor = new Color(0, 0, 0);
    private Color frontWheelsSpokeColor = new Color(0, 0, 0);
    private Color forkColor = new Color(128, 128, 128);
    private boolean drawWheelLines = false;
    private boolean drawWheelSprite = true;
    private Color bikeLinesColor = new Color(50, 50, 50);
    private Color bikeColor = new Color(170, 0, 0);
    private Color bikerHeadColor = new Color(156, 0, 0);
    private Color bikerLegColor = new Color(0, 0, 0);
    private Color bikerBodyColor = new Color(0, 0, 128);
    private Color steeringColor = new Color(0, 0, 0);

    public String getName() {
        return name;
    }

    public Color getBackWheelDotColor() {
        return backWheelDotColor;
    }

    public Color getFrontWheelDotColor() {
        return frontWheelDotColor;
    }

    public Color getBackWheelsColor() {
        return backWheelsColor;
    }

    public Color getBackWheelsSpokeColor() {
        return backWheelsSpokeColor;
    }

    public Color getFrontWheelsColor() {
        return frontWheelsColor;
    }

    public Color getFrontWheelsSpokeColor() {
        return frontWheelsSpokeColor;
    }

    public Color getForkColor() {
        return forkColor;
    }

    public boolean isDrawWheelLines() {
        return drawWheelLines;
    }

    public boolean isDrawWheelSprite() {
        return drawWheelSprite;
    }

    public Color getBikeLinesColor() {
        return bikeLinesColor;
    }

    public Color getBikeColor() {
        return bikeColor;
    }

    public Color getBikerHeadColor() {
        return bikerHeadColor;
    }

    public Color getBikerLegColor() {
        return bikerLegColor;
    }

    public Color getBikerBodyColor() {
        return bikerBodyColor;
    }

    public Color getSteeringColor() {
        return steeringColor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBackWheelDotColor(Color backWheelDotColor) {
        this.backWheelDotColor = backWheelDotColor;
    }

    public void setFrontWheelDotColor(Color frontWheelDotColor) {
        this.frontWheelDotColor = frontWheelDotColor;
    }

    public void setBackWheelsColor(Color backWheelsColor) {
        this.backWheelsColor = backWheelsColor;
    }

    public void setBackWheelsSpokeColor(Color backWheelsSpokeColor) {
        this.backWheelsSpokeColor = backWheelsSpokeColor;
    }

    public void setFrontWheelsColor(Color frontWheelsColor) {
        this.frontWheelsColor = frontWheelsColor;
    }

    public void setFrontWheelsSpokeColor(Color frontWheelsSpokeColor) {
        this.frontWheelsSpokeColor = frontWheelsSpokeColor;
    }

    public void setForkColor(Color forkColor) {
        this.forkColor = forkColor;
    }

    public void setDrawWheelLines(boolean drawWheelLines) {
        this.drawWheelLines = drawWheelLines;
    }

    public void setDrawWheelSprite(boolean drawWheelSprite) {
        this.drawWheelSprite = drawWheelSprite;
    }

    public void setBikeLinesColor(Color bikeLinesColor) {
        this.bikeLinesColor = bikeLinesColor;
    }

    public void setBikeColor(Color bikeColor) {
        this.bikeColor = bikeColor;
    }

    public void setBikerHeadColor(Color bikerHeadColor) {
        this.bikerHeadColor = bikerHeadColor;
    }

    public void setBikerLegColor(Color bikerLegColor) {
        this.bikerLegColor = bikerLegColor;
    }

    public void setBikerBodyColor(Color bikerBodyColor) {
        this.bikerBodyColor = bikerBodyColor;
    }

    public void setSteeringColor(Color steeringColor) {
        this.steeringColor = steeringColor;
    }

    public static List<LeagueTheme> getDefaultLeagueProperties() {
        LeagueTheme league100cc = new LeagueTheme();
        league100cc.setName("100cc");

        LeagueTheme league175cc = new LeagueTheme();
        league175cc.setBackWheelDotColor(new Color(255, 0, 0));
        league175cc.setName("175cc");

        LeagueTheme league220cc = new LeagueTheme();
        league220cc.setBackWheelDotColor(new Color(255, 0, 0));
        league220cc.setFrontWheelDotColor(new Color(255, 0, 0));
        league220cc.setName("220cc");

        LeagueTheme league325cc = new LeagueTheme();
        league325cc.setBackWheelDotColor(new Color(100, 100, 255));
        league325cc.setName("325cc");

        List<LeagueTheme> leagueProperties = new ArrayList<>();
        leagueProperties.add(league100cc);
        leagueProperties.add(league175cc);
        leagueProperties.add(league220cc);
        leagueProperties.add(league325cc);
        return leagueProperties;
    }
}
