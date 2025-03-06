package org.happysanta.gdtralive.desktop;

import static org.happysanta.gdtralive.DesktopGdView.application;
import static org.happysanta.gdtralive.DesktopGdView.platform;

import org.happysanta.gdtralive.GdDesktopApp;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.menu.Menu;
import org.happysanta.gdtralive.game.api.menu.MenuFactory;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class DPlatform implements GdPlatform {
    private Menu<JComponent> menu;

    @Override
    public void init() {
        DPlatformMenuElementFactory<JComponent> dPlatformMenuElementFactory = new DPlatformMenuElementFactory<>(application);
        MenuFactory<JComponent> menuFactory = new MenuFactory<>(application, platform, dPlatformMenuElementFactory);
        menu = new Menu<>(application, menuFactory);
        dPlatformMenuElementFactory.setMenu(menu);
        menuFactory.init(menu, null, application.getGame());
        application.setMenu(menu);
    }

    @Override
    public void setMenu(MenuScreen menu) {
        for (Component c : GdDesktopApp.menu.getComponents()) {
            GdDesktopApp.menu.remove(c);
        }
        if (menu != null) {
            GdDesktopApp.menu.add(((MenuScreen<JComponent>) menu).getLayout());
        }

        GdDesktopApp.instance.revalidate();
        GdDesktopApp.instance.repaint();
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
        action.run();
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
        setMenu(this.menu.getCurrentMenu());
    }

    @Override
    public void menuToGameUpdateUi() {
        JPanel menu = GdDesktopApp.menu;
        if (menu != null) {
            for (Component c : menu.getComponents()) {
                menu.remove(c);
            }
            menu.revalidate();
            menu.repaint();
        }

        this.menu.setMenuDisabled(true);
        GdDesktopApp.instance.revalidate();
        GdDesktopApp.instance.repaint();
    }

    @Override
    public void trainingMode() {

    }

    @Override
    public String getAppVersion() {
        return "";
    }
}
