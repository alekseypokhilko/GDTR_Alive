package org.happysanta.gdtralive.desktop;

import static org.happysanta.gdtralive.DesktopGdView.application;
import static org.happysanta.gdtralive.DesktopGdView.platform;

import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.menu.Menu;
import org.happysanta.gdtralive.game.api.menu.MenuFactory;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;

public class DPlatform implements GdPlatform {
    Menu<Object> menu;
    @Override
    public void init() {
        DPlatformMenuElementFactory dPlatformMenuElementFactory = new DPlatformMenuElementFactory();
        MenuFactory<Object> objectMenuFactory = new MenuFactory<>(application, platform, dPlatformMenuElementFactory);
        menu = new Menu<>(application, objectMenuFactory);
        application.setMenu(menu);
    }

    @Override
    public void setMenu(MenuScreen menu) {
    }

    @Override
    public GdMenu getMenu() {
        return menu;
    }

    @Override
    public float getDensity() {
        return 2.5f;
    }

    @Override
    public void notify(String message) {

    }

    @Override
    public void showAlert(String title, String message, Runnable listener) {

    }

    @Override
    public void showConfirm(String title, String message, Runnable onOk, Runnable onCancel) {

    }

    @Override
    public void runOnUiThread(Runnable action) {

    }

    @Override
    public void finish() {

    }

    @Override
    public void doRestartApp() {

    }

    @Override
    public void pickFile(int requestCode) {

    }

    @Override
    public void hideKeyboardLayout() {

    }

    @Override
    public void showKeyboardLayout() {

    }

    @Override
    public void gameToMenuUpdateUi() {

    }

    @Override
    public void menuToGameUpdateUi() {

    }

    @Override
    public void trainingMode() {

    }

    @Override
    public String getAppVersion() {
        return "";
    }
}
