package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.Color;

import java.util.HashMap;
import java.util.Map;

public class InterfaceTheme implements ITheme {
    public static String density = "density";
    public static String matteMenu = "matteMenu"; // true;
    public static String infoMessageColor = "infoMessageColor"; // new Color(0, 0, 0);
    public static String progressBackgroundColor = "progressBackgroundColor"; // new Color(196, 196, 196);
    public static String progressColor = "progressColor"; // new Color(41, 170, 39);
    public static String splashColor = "splashColor"; // new Color(255, 255, 255);
    public static String lockSkinIndex = "lockSkinIndex"; // 0;
    public static String menuBackgroundColor = "menuBackgroundColor"; // 0x80FFFFFF;
    public static String keyboardTextColor = "keyboardTextColor"; // 0xff000000;
    public static String keyboardBackgroundColor = "keyboardBackgroundColor"; // 0xc6ffffff;
    public static String menuTitleTextColor = "menuTitleTextColor"; // 0xff000000;
    public static String menuTitleBackgroundColor = "menuTitleBackgroundColor"; // 0x00ffffff;
    public static String frameBackgroundColor = "frameBackgroundColor"; // 0xffffffff;
    public static String mainMenuBackgroundColor = "mainMenuBackgroundColor"; // 0x00ffffff;
    public static String textColor = "textColor"; // 0xff000000;

    private final Map<String, Object> props = new HashMap<>();

    public InterfaceTheme() {
        initDefaults();
    }

    public InterfaceTheme(Map<String, String> properties) {
        initDefaults();
        ITheme.mergeProperties(properties, props);
    }

    private void initDefaults() {
        props.put(infoMessageColor, new Color(0, 0, 0));
        props.put(progressBackgroundColor, new Color(196, 196, 196));
        props.put(progressColor, new Color(41, 170, 39));
        props.put(splashColor, new Color(255, 255, 255));
        props.put(lockSkinIndex, 0);
        props.put(keyboardTextColor, 0xff000000);
        props.put(menuTitleTextColor, 0xff000000);
        props.put(textColor, 0xff000000);

        props.put(matteMenu, false);
        props.put(menuBackgroundColor, 0x00ffffff);
        props.put(keyboardBackgroundColor, 0x00ffffff);
        props.put(menuTitleBackgroundColor, 0x00ffffff);
        props.put(frameBackgroundColor, 0x00ffffff);
        props.put(mainMenuBackgroundColor, 0x00ffffff);

        //original
        //props.put(keyboardBackgroundColor, 0xc6ffffff);
        //props.put(matteMenu, true);
        //props.put(menuBackgroundColor, 0x80FFFFFF);
        //props.put(menuTitleBackgroundColor, 0x00ffffff);
        //props.put(frameBackgroundColor, 0xffffffff);
        //props.put(mainMenuBackgroundColor, 0x00ffffff);
    }

    @Override
    public Map<String, String> getProps() {
        return ITheme.convertProperties(props);
    }

    @Override
    public void setProp(String key, Object prop) {
        props.put(key, prop);
    }

    public float getDensity() {
        return (Float) props.get(density);
    }

    public boolean isMatteMenu() {
        return (boolean) props.get(matteMenu);
    }

    public Color getInfoMessageColor() {
        return (Color) props.get(infoMessageColor);
    }

    public Color getProgressBackgroundColor() {
        return (Color) props.get(progressBackgroundColor);
    }

    public Color getProgressColor() {
        return (Color) props.get(progressColor);
    }

    public Color getSplashColor() {
        return (Color) props.get(splashColor);
    }

    public int getLockSkinIndex() {
        return (int) props.get(lockSkinIndex);
    }

    public int getMenuBackgroundColor() {
        return (int) props.get(menuBackgroundColor);
    }

    public int getKeyboardTextColor() {
        return (int) props.get(keyboardTextColor);
    }

    public int getKeyboardBackgroundColor() {
        return (int) props.get(keyboardBackgroundColor);
    }

    public int getMenuTitleTextColor() {
        return (int) props.get(menuTitleTextColor);
    }

    public int getMenuTitleBackgroundColor() {
        return (int) props.get(menuTitleBackgroundColor);
    }

    public int getFrameBackgroundColor() {
        return (int) props.get(frameBackgroundColor);
    }

    public int getMainMenuBackgroundColor() {
        return (int) props.get(mainMenuBackgroundColor);
    }

    public int getTextColor() {
        return (int) props.get(textColor);
    }
}
