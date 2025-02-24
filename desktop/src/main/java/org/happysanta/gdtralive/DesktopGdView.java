package org.happysanta.gdtralive;

import org.happysanta.gdtralive.desktop.DesktopCanvas;
import org.happysanta.gdtralive.desktop.DesktopFileStorage;
import org.happysanta.gdtralive.desktop.DesktopGdApplication;
import org.happysanta.gdtralive.desktop.DesktopGdMenu;
import org.happysanta.gdtralive.desktop.DesktopGdSettings;
import org.happysanta.gdtralive.desktop.DesktopKeyboardController;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.external.GdApplication;
import org.happysanta.gdtralive.game.external.GdFileStorage;
import org.happysanta.gdtralive.game.external.GdMenu;
import org.happysanta.gdtralive.game.external.GdSettings;
import org.happysanta.gdtralive.game.mod.ModManager;
import org.happysanta.gdtralive.game.modes.MenuData;
import org.happysanta.gdtralive.game.trainer.Trainer;
import org.happysanta.gdtralive.game.visual.FrameRender;
import org.happysanta.gdtralive.game.visual.GdView;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;
import javax.swing.Timer;

public class DesktopGdView extends JPanel implements ActionListener, KeyListener {

    public static double ZOOM = 3.5;
    public static int height = 720;
    public static int width = 1280;


    public GdSettings settings;

    public Game game;
    public DesktopCanvas canvas = new DesktopCanvas();
    public final GdView gdView;
    public GdMenu menu;
    public ModManager modManager;
    Trainer trainer;
    private final GdFileStorage fileStorage = new DesktopFileStorage();
    DesktopKeyboardController desktopKeyboardController;

    public DesktopGdView() {
        setFocusable(true);
        addKeyListener(this);
        setSize(width, height);
        setVisible(true);


        this.settings = new DesktopGdSettings();
        modManager = new ModManager(fileStorage);
        GdApplication gdApplication = new DesktopGdApplication();
        final FrameRender frameRender = new FrameRender(modManager);
        final Engine engine = new Engine();
        gdView = new GdView(frameRender, engine, width, height);
        menu = new DesktopGdMenu();


        game = new Game(gdApplication, 1920, 1080);
        desktopKeyboardController = new DesktopKeyboardController(game);

        gdView.setLoadingState(null);
        gdView.setDrawTimer(true);

        game.init(menu);
        game.restart(false);
        menu.showMenu(MenuData.mainMenu());
        if (menu.canStartTrack())
            game.restart(true);
//        game.engine.unlockKeys();
//        game.startTrack(2, 2, 7);
        game.restart(false);


        Timer gameLoop = new Timer(20, this); //how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();
        Thread one = new Thread(() -> {
            try {
                while (true) {
                    game.tick();
                    Thread.sleep(30L);
                }
            } catch (Exception v) {
                System.out.println(v);
            }
        });

        one.start();

        new Timer(30000, actionEvent -> System.gc()).start(); //todo fix heap pollution


    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2d);
        //https://stackoverflow.com/questions/30792089/java-graphics2d-translate-and-scale
        int width = getWidth();
        int height = getHeight();
        double anchorx = (width - width * ZOOM) / ZOOM;
        double anchory = (height - height * ZOOM) / ZOOM;
        AffineTransform at = new AffineTransform();
        at.translate(anchorx, anchory);
        at.scale(ZOOM, ZOOM);
//        at.translate(-zoom, -zoom);
        g2d.setTransform(at);
        canvas.setCanvas(g2d);
        gdView.drawGame(canvas, width, height);
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
