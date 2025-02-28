package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.Color;

public class InterfaceTheme {
    private Color infoMessageColor = new Color(0, 0, 0);
    private Color progressBackgroundColor = new Color(196, 196, 196);
    private Color progressColor = new Color(41, 170, 39);
    private Color splashColor = new Color(255, 255, 255);
    private int lockSkinIndex =  0;
    private int menuBackgroundColor = 0x00ffffff;
    private int keyboardTextColor = 0xff000000;
    private int keyboardBackgroundColor = 0x00ffffff;
    private int menuTitleTextColor = 0xff000000;
    private int menuTitleBackgroundColor = 0x00ffffff;
    private int frameBackgroundColor = 0x00ffffff;
    private int mainMenuBackgroundColor = 0x00ffffff;
    private int textColor = 0xff000000;

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

    public int getMenuBackgroundColor() {
        return menuBackgroundColor;
    }

    public int getKeyboardTextColor() {
        return keyboardTextColor;
    }

    public int getKeyboardBackgroundColor() {
        return keyboardBackgroundColor;
    }

    public int getMenuTitleTextColor() {
        return menuTitleTextColor;
    }

    public int getMenuTitleBackgroundColor() {
        return menuTitleBackgroundColor;
    }

    public int getFrameBackgroundColor() {
        return frameBackgroundColor;
    }

    public int getMainMenuBackgroundColor() {
        return mainMenuBackgroundColor;
    }

    public int getTextColor() {
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

    public void setMenuBackgroundColor(int menuBackgroundColor) {
        this.menuBackgroundColor = menuBackgroundColor;
    }

    public void setKeyboardTextColor(int keyboardTextColor) {
        this.keyboardTextColor = keyboardTextColor;
    }

    public void setKeyboardBackgroundColor(int keyboardBackgroundColor) {
        this.keyboardBackgroundColor = keyboardBackgroundColor;
    }

    public void setMenuTitleTextColor(int menuTitleTextColor) {
        this.menuTitleTextColor = menuTitleTextColor;
    }

    public void setMenuTitleBackgroundColor(int menuTitleBackgroundColor) {
        this.menuTitleBackgroundColor = menuTitleBackgroundColor;
    }

    public void setFrameBackgroundColor(int frameBackgroundColor) {
        this.frameBackgroundColor = frameBackgroundColor;
    }

    public void setMainMenuBackgroundColor(int mainMenuBackgroundColor) {
        this.mainMenuBackgroundColor = mainMenuBackgroundColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
