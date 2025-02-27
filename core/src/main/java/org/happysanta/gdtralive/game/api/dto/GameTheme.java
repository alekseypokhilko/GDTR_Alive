package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameTheme implements ITheme {
    public static String scaledDensity = "scaledDensity"; // Helpers.getGDActivity().getResources().getDisplayMetrics().density; //todo getter with calculation
    public static String spriteDensity = "spriteDensity"; // Helpers.getGDActivity().getResources().getDisplayMetrics().density; //todo getter with calculation
    public static String gameBackgroundColor = "gameBackgroundColor"; // new Color(255, 255, 255);
    public static String trackLineColor = "trackLineColor"; // new Color(0, 255, 0);
    public static String perspectiveColor = "perspectiveColor"; // new Color(0, 170, 0);
    public static String startFlagColor = "startFlagColor"; // new Color(0, 0, 0);
    public static String finishFlagColor = "finishFlagColor"; // new Color(0, 0, 0);

    private final LinkedHashMap<String, Object> props = new LinkedHashMap<>();

    public GameTheme() {
        initDefaults();
    }

    public GameTheme(Map<String, String> properties) {
        initDefaults();
        ITheme.mergeProperties(properties, props);
    }

    private void initDefaults() {
        this.props.put("gameBackgroundColor", new Color(255, 255, 255));
        this.props.put("trackLineColor", new Color(0, 255, 0));
        this.props.put("perspectiveColor", new Color(0, 170, 0));
        this.props.put("startFlagColor", new Color(0, 0, 0));
        this.props.put("finishFlagColor", new Color(0, 0, 0));
    }

    @Override
    public LinkedHashMap<String, String> getProps() {
        return ITheme.convertProperties(props);
    }

    @Override
    public void setProp(String key, Object prop) {
        props.put(key, prop);
    }

    public float getScaledDensity() {
        return (Float) props.get(scaledDensity);
    }

    public float getSpriteDensity() {
        return (Float) props.get(spriteDensity);
    }

    public Color getGameBackgroundColor() {
        return (Color) props.get(gameBackgroundColor);
    }

    public Color getTrackLineColor() {
        return (Color) props.get(trackLineColor);
    }

    public Color getPerspectiveColor() {
        return (Color) props.get(perspectiveColor);
    }

    public Color getStartFlagColor() {
        return (Color) props.get(startFlagColor);
    }

    public Color getFinishFlagColor() {
        return (Color) props.get(finishFlagColor);
    }
}
