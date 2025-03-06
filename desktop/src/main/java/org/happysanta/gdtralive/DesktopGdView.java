package org.happysanta.gdtralive;

import org.happysanta.gdtralive.desktop.DesktopCanvas;
import org.happysanta.gdtralive.desktop.DesktopKeyboardController;
import org.happysanta.gdtralive.game.GdView;
import org.happysanta.gdtralive.game.api.external.GdGameView;

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

public class DesktopGdView extends JPanel implements ActionListener, KeyListener, GdGameView {
    public static int height = 720;
    public static int width = 1280;

    private final DesktopCanvas canvas = new DesktopCanvas();
    private GdView gdView;
    private final DesktopKeyboardController desktopKeyboardController;

    public DesktopGdView(DesktopKeyboardController desktopKeyboardController) {
        super();
        this.desktopKeyboardController = desktopKeyboardController;
        setFocusable(true);
        addKeyListener(this);
        setSize(width, height);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (GdDesktopApp.instance.getWidth() != getWidth() || GdDesktopApp.instance.getHeight() != getHeight()) {
            setSize(GdDesktopApp.instance.getWidth(), GdDesktopApp.instance.getHeight());
            GdDesktopApp.lpane.setSize(GdDesktopApp.instance.getWidth(), GdDesktopApp.instance.getHeight());
        }

        float density = GdDesktopApp.application.getModManager().getGameDensity();
        //https://stackoverflow.com/questions/30792089/java-graphics2d-translate-and-scale
        AffineTransform at = new AffineTransform();
        at.scale(density, density);
        g2d.transform(at);

        canvas.setCanvas(g2d);
        gdView.drawGame(canvas, (int) (getWidth() / density), (int) (getHeight() / density));
        g2d.dispose();
        g.dispose();
    }

    @Override
    public int getGdWidth() {
        return getWidth();
    }

    @Override
    public int getGdHeight() {
        return getHeight();
    }

    @Override
    public void setGdView(GdView gdView) {
        this.gdView = gdView;
    }

    @Override
    public GdView getGdView() {
        return gdView;
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
