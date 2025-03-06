package org.happysanta.gdtralive;

import org.happysanta.gdtralive.desktop.DPlatformMenuElementFactory;
import org.happysanta.gdtralive.desktop.DSettingsStorage;
import org.happysanta.gdtralive.desktop.DesktopFileStorage;
import org.happysanta.gdtralive.desktop.DesktopGdDataSource;
import org.happysanta.gdtralive.desktop.DesktopGdStr;
import org.happysanta.gdtralive.desktop.DesktopKeyboardController;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.api.Platform;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.menu.Menu;
import org.happysanta.gdtralive.game.api.menu.MenuFactory;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GdDesktopApp extends JFrame implements GdPlatform {
    public static GdDesktopApp instance;
    public static JLayeredPane lpane = new JLayeredPane();
    public static DesktopGdView gameView;
    public static JPanel menuPanel;
    private Menu<JComponent> menu;
    private final Timer gameTimer;
    private final Timer gcTimer;

    public static Application application;

    public GdDesktopApp() {
        instance = this;
        setTitle("GDTR Alive!");
        setSize(DesktopGdView.width, DesktopGdView.height);
        setPreferredSize(new Dimension(DesktopGdView.width, DesktopGdView.height));
        setLayout(new BorderLayout());

        menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(new Color(0,0,0,0));
        menuPanel.setOpaque(false);
        menuPanel.setVisible(true);
        menuPanel.setBounds(20, 50, 500, 700);

        DSettingsStorage settingsStorage = new DSettingsStorage();
        DesktopGdStr str = new DesktopGdStr();
        DesktopGdDataSource dataSource = new DesktopGdDataSource();
        DesktopFileStorage fileStorage = new DesktopFileStorage(new File("mods"), new HashMap<>());
        DesktopKeyboardController desktopKeyboardController = new DesktopKeyboardController();
        gameView = new DesktopGdView(desktopKeyboardController);
        gameView.setBounds(0, 0, DesktopGdView.width, DesktopGdView.height);
        gameView.setPreferredSize(new Dimension(DesktopGdView.width, DesktopGdView.height));

        application = new Application(this, settingsStorage, str, fileStorage, dataSource, gameView);
        application.doStart();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        menu = (Menu<JComponent>) this.getMenu();
        application.setMenu(this.getMenu());
        application.gameToMenu();
        desktopKeyboardController.setApplication(application);

        add(lpane, BorderLayout.CENTER);
        lpane.setPreferredSize(new Dimension(DesktopGdView.width, DesktopGdView.height));
        lpane.add(gameView, 0, 0);
        lpane.add(menuPanel, 1, 0);

        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        gameTimer = new Timer(0, gameView);
        gameTimer.start();
        gcTimer = new Timer(5000, actionEvent -> System.gc());
        gcTimer.start(); //todo fix heap pollution
    }

    public static void main(String[] args) {
        GdDesktopApp gd = new GdDesktopApp();
    }

    @Override
    public Platform getPlatform() {
        return Platform.PC;
    }

    @Override
    public void init() {
        DPlatformMenuElementFactory<JComponent> dPlatformMenuElementFactory = new DPlatformMenuElementFactory<>(application);
        MenuFactory<JComponent> menuFactory = new MenuFactory<>(application, this, dPlatformMenuElementFactory);
        menu = new Menu<>(application, menuFactory);
        dPlatformMenuElementFactory.setMenu(menu);
        menuFactory.init(menu, null, application.getGame());
        application.setMenu(menu);
    }

    @Override
    public void setMenu(MenuScreen menu) {
        for (Component c : menuPanel.getComponents()) {
            menuPanel.remove(c);
        }
        if (menu != null) {
            menuPanel.add(((MenuScreen<JComponent>) menu).getLayout());
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
        return 1.5f;
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
        gameTimer.stop();
        gcTimer.stop();
        setVisible(false);
        dispose();
        SwingUtilities.invokeLater(() -> System.exit(0));
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
        if (menuPanel != null) {
            for (Component c : menuPanel.getComponents()) {
                menuPanel.remove(c);
            }
            menuPanel.revalidate();
            menuPanel.repaint();
        }

        this.menu.setMenuDisabled(true);
        revalidate();
        repaint();
    }

    @Override
    public void trainingMode() {

    }

    @Override
    public String getAppVersion() {
        return "";
    }
}
