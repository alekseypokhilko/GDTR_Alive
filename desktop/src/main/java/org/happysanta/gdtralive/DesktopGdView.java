package org.happysanta.gdtralive;

import org.happysanta.gdtralive.desktop.DGameView;
import org.happysanta.gdtralive.desktop.DPlatform;
import org.happysanta.gdtralive.desktop.DPlatformMenuElementFactory;
import org.happysanta.gdtralive.desktop.DSettingsStorage;
import org.happysanta.gdtralive.desktop.DesktopCanvas;
import org.happysanta.gdtralive.desktop.DesktopFileStorage;
import org.happysanta.gdtralive.desktop.DesktopGdDataSource;
import org.happysanta.gdtralive.desktop.DesktopGdSettings;
import org.happysanta.gdtralive.desktop.DesktopGdStr;
import org.happysanta.gdtralive.desktop.DesktopKeyboardController;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.GdView;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.menu.Menu;
import org.happysanta.gdtralive.game.api.menu.MenuFactory;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.TrackData;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.Timer;

public class DesktopGdView extends JPanel implements ActionListener, KeyListener {

    public static double ZOOM = 2;
    public static int height = 720;
    public static int width = 1280;


    public GdSettings settings;

    public Game game;
    public DesktopCanvas canvas = new DesktopCanvas();
    public final GdView gdView;
    DesktopKeyboardController desktopKeyboardController;

    public static Application application;
    public static DPlatform platform;
    public static Menu<Object> menu;

    private final AffineTransform at;
    public DesktopGdView() {
        super();
        int width = getWidth();
        int height = getHeight();
        double anchorx = (width - width * ZOOM) / ZOOM;
        double anchory = (height - height * ZOOM) / ZOOM;

        //https://stackoverflow.com/questions/30792089/java-graphics2d-translate-and-scale
        this.at = new AffineTransform();
        this.at.translate(anchorx, anchory);
        this.at.scale(ZOOM, ZOOM);
        this.at.translate(-ZOOM, -ZOOM);

        setFocusable(true);
        addKeyListener(this);
        setSize(width, height);
        setVisible(true);


        this.settings = new DesktopGdSettings();
        platform = new DPlatform();
        DSettingsStorage settingsStorage = new DSettingsStorage();
        DesktopGdStr str = new DesktopGdStr();
        DesktopGdDataSource dataSource = new DesktopGdDataSource();
        DesktopFileStorage fileStorage = new DesktopFileStorage(new File("mods"), new HashMap<>());
        DGameView gdGameView = new DGameView();
        application = new Application(platform, settingsStorage, str, fileStorage, dataSource, gdGameView);

        DPlatformMenuElementFactory dPlatformMenuElementFactory = new DPlatformMenuElementFactory();
        MenuFactory<Object> objectMenuFactory = new MenuFactory<>(application, platform, dPlatformMenuElementFactory);
         menu = new Menu<>(application, objectMenuFactory);
        application.doStart();
        dPlatformMenuElementFactory.setMenu(menu);
//        objectMenuFactory.init(menu, null, application.getGame());
        application.setMenu(platform.getMenu());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        game = application.getGame();
        startRandom();

        desktopKeyboardController = new DesktopKeyboardController(application.getGame());

        gdView = application.getGame().getView();
        gdView.setLoadingState(null);
        gdView.setDrawTimer(true);
        menu.menuToGame();
        game.resume();

        game.restart(false);
        if (menu.canStartTrack())
            game.restart(true);
        game.getEngine().unlockKeys();
        game.restart(false);

        new Timer(0, this).start();
        new Timer(5000, actionEvent -> System.gc()).start(); //todo fix heap pollution
    }

    private void startRandom() {
        TrackData randomTrack = application.getModManager().getRandomTrack();
        game.startTrack(GameParams.of(GameMode.CAMPAIGN, randomTrack, 2, 0, 0));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setTransform(at);
        canvas.setCanvas(g2d);
        if (application.isMenuShown()) {
            startRandom();
            menu.menuToGame();
        }
        gdView.drawGame(canvas, getWidth(), getHeight());
        g2d.dispose();
        g.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        desktopKeyboardController.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        desktopKeyboardController.keyReleased(e);
    }
}
