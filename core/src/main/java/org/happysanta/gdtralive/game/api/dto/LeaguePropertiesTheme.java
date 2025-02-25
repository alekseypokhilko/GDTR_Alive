package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeaguePropertiesTheme implements ITheme {
    public static String backWheelDotColor = "backWheelDotColor"; //
    public static String frontWheelDotColor = "frontWheelDotColor"; //
    public static String backWheelsColor = "backWheelsColor"; // new Color(0, 0, 0);
    public static String backWheelsSpokeColor = "backWheelsSpokeColor"; // new Color(0, 0, 0);
    public static String frontWheelsColor = "frontWheelsColor"; // new Color(0, 0, 0);
    public static String frontWheelsSpokeColor = "frontWheelsSpokeColor"; // new Color(0, 0, 0);
    public static String forkColor = "forkColor"; // new Color(128, 128, 128);
    public static String drawWheelLines = "drawWheelLines"; // false;
    public static String bikeLinesColor = "bikeLinesColor"; // new Color(50, 50, 50);
    public static String bikeColor = "bikeColor"; // new Color(170, 0, 0);
    public static String bikerHeadColor = "bikerHeadColor"; // new Color(156, 0, 0);
    public static String bikerLegColor = "bikerLegColor"; // new Color(0, 0, 0);
    public static String bikerBodyColor = "bikerBodyColor"; // new Color(0, 0, 128);
    public static String steeringColor = "steeringColor"; // new Color(0, 0, 0);

    public static String name = "name";//;

    private final Map<String, Object> props = new LinkedHashMap<>();

    public LeaguePropertiesTheme() {
        initDefaults();
    }

    public LeaguePropertiesTheme(Map<String, String> properties) {
        initDefaults();
        ITheme.mergeProperties(properties, props);
    }

    private void initDefaults() {
        props.put(backWheelDotColor, null);
        props.put(frontWheelDotColor, null);
        props.put(backWheelsColor, new Color(0, 0, 0));
        props.put(backWheelsSpokeColor, new Color(0, 0, 0));
        props.put(frontWheelsColor, new Color(0, 0, 0));
        props.put(frontWheelsSpokeColor, new Color(0, 0, 0));
        props.put(forkColor, new Color(128, 128, 128));
        props.put(drawWheelLines, false);
        props.put(bikeLinesColor, new Color(50, 50, 50));
        props.put(bikeColor, new Color(170, 0, 0));
        props.put(bikerHeadColor, new Color(156, 0, 0));
        props.put(bikerLegColor, new Color(0, 0, 0));
        props.put(bikerBodyColor, new Color(0, 0, 128));
        props.put(steeringColor, new Color(0, 0, 0));
    }

    @Override
    public LinkedHashMap<String, String> getProps() {
        return ITheme.convertProperties(props);
    }

    @Override
    public void setProp(String key, Object prop) {
        props.put(key, prop);
    }

    public Color getBackWheelDotColor() {
        return (Color) props.get(backWheelDotColor);
    }

    public Color getFrontWheelDotColor() {
        return (Color) props.get(frontWheelDotColor);
    }

    public Color getBackWheelsColor() {
        return (Color) props.get(backWheelsColor);
    }

    public Color getBackWheelsSpokeColor() {
        return (Color) props.get(backWheelsSpokeColor);
    }

    public Color getFrontWheelsColor() {
        return (Color) props.get(frontWheelsColor);
    }

    public Color getFrontWheelsSpokeColor() {
        return (Color) props.get(frontWheelsSpokeColor);
    }

    public Color getForkColor() {
        return (Color) props.get(forkColor);
    }

    public boolean isDrawWheelLines() {
        return (boolean) props.get(drawWheelLines);
    }

    public Color getBikeLinesColor() {
        return (Color) props.get(bikeLinesColor);
    }

    public Color getBikeColor() {
        return (Color) props.get(bikeColor);
    }

    public Color getBikerHeadColor() {
        return (Color) props.get(bikerHeadColor);
    }

    public Color getBikerLegColor() {
        return (Color) props.get(bikerLegColor);
    }

    public Color getBikerBodyColor() {
        return (Color) props.get(bikerBodyColor);
    }

    public Color getSteeringColor() {
        return (Color) props.get(steeringColor);
    }

    public String getName() {
        return (String) props.get(name);
    }

    public static List<LeaguePropertiesTheme> getDefaultLeagueProperties() {
        LeaguePropertiesTheme league100cc = new LeaguePropertiesTheme();
        league100cc.setProp(name, "100cc");

        LeaguePropertiesTheme league175cc = new LeaguePropertiesTheme();
        league175cc.setProp(backWheelDotColor, new Color(255, 0, 0));
        league175cc.setProp(name, "175cc");

        LeaguePropertiesTheme league220cc = new LeaguePropertiesTheme();
        league220cc.setProp(backWheelDotColor, new Color(255, 0, 0));
        league220cc.setProp(frontWheelDotColor, new Color(255, 0, 0));
        league220cc.setProp(name, "220cc");

        LeaguePropertiesTheme league325cc = new LeaguePropertiesTheme();
        league325cc.setProp(backWheelDotColor, new Color(100, 100, 255));
        league325cc.setProp(name, "325cc");

        List<LeaguePropertiesTheme> leagueProperties = new ArrayList<>();
        leagueProperties.add(league100cc);
        leagueProperties.add(league175cc);
        leagueProperties.add(league220cc);
        leagueProperties.add(league325cc);
        return leagueProperties;
    }
}
