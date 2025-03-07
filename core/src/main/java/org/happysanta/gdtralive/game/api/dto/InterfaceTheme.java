package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.Color;

public class InterfaceTheme {
    private Color infoMessageColor = new Color(0, 0, 0);
    private Color progressBackgroundColor = new Color(196, 196, 196);
    private Color progressColor = new Color(41, 170, 39);
    private Color splashColor = new Color(255, 255, 255);
    private int lockSkinIndex = 0;
    private Color menuBackgroundColor = Color.of("0x00ffffff");
    private Color keyboardTextColor = Color.of("0xff000000");
    private Color keyboardBackgroundColor = Color.of("0x00ffffff");
    private Color menuTitleTextColor = Color.of("0xff000000");
    private Color menuTitleBackgroundColor = Color.of("0x00ffffff");
    private Color frameBackgroundColor = Color.of("0x00ffffff");
    private Color mainMenuBackgroundColor = Color.of("0x00ffffff");
    private Color textColor = Color.of("0xff000000");

    public Color getInfoMessageColor() {
        return infoMessageColor;
    }

    public Color getProgressBackgroundColor() {
        return progressBackgroundColor;
    }

    public Color getProgressColor() {
        return progressColor;
    }

    public Color getSplashColor() {
        return splashColor;
    }

    public int getLockSkinIndex() {
        return lockSkinIndex;
    }

    public int getMenuBackgroundColorInt() {
        return menuBackgroundColor.intValue();
    }

    public int getKeyboardTextColorInt() {
        return keyboardTextColor.intValue();
    }

    public Color getKeyboardTextColor() {
        return keyboardTextColor;
    }

    public int getKeyboardBackgroundColor() {
        return keyboardBackgroundColor.intValue();
    }

    public int getMenuTitleTextColorInt() {
        return menuTitleTextColor.intValue();
    }

    public Color getMenuTitleTextColor() {
        return menuTitleTextColor;
    }

    public int getMenuTitleBackgroundColor() {
        return menuTitleBackgroundColor.intValue();
    }

    public int getFrameBackgroundColor() {
        return frameBackgroundColor.intValue();
    }

    public int getMainMenuBackgroundColor() {
        return mainMenuBackgroundColor.intValue();
    }

    public int getTextColorInt() {
        return textColor.intValue();
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setInfoMessageColor(Color infoMessageColor) {
        this.infoMessageColor = infoMessageColor;
    }

    public void setProgressBackgroundColor(Color progressBackgroundColor) {
        this.progressBackgroundColor = progressBackgroundColor;
    }

    public void setProgressColor(Color progressColor) {
        this.progressColor = progressColor;
    }

    public void setSplashColor(Color splashColor) {
        this.splashColor = splashColor;
    }

    public void setLockSkinIndex(int lockSkinIndex) {
        this.lockSkinIndex = lockSkinIndex;
    }

    public void setMenuBackgroundColor(Color menuBackgroundColor) {
        this.menuBackgroundColor = menuBackgroundColor;
    }

    public void setKeyboardTextColor(Color keyboardTextColor) {
        this.keyboardTextColor = keyboardTextColor;
    }

    public void setKeyboardBackgroundColor(Color keyboardBackgroundColor) {
        this.keyboardBackgroundColor = keyboardBackgroundColor;
    }

    public void setMenuTitleTextColor(Color menuTitleTextColor) {
        this.menuTitleTextColor = menuTitleTextColor;
    }

    public void setMenuTitleBackgroundColor(Color menuTitleBackgroundColor) {
        this.menuTitleBackgroundColor = menuTitleBackgroundColor;
    }

    public void setFrameBackgroundColor(Color frameBackgroundColor) {
        this.frameBackgroundColor = frameBackgroundColor;
    }

    public void setMainMenuBackgroundColor(Color mainMenuBackgroundColor) {
        this.mainMenuBackgroundColor = mainMenuBackgroundColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
}
