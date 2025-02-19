package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.Game;

import java.awt.event.KeyEvent;

public class DesktopKeyboardController {

    private final Game game;

    public DesktopKeyboardController(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                game.getKeyboardHandler().keyPressed(52);
                break;
            case KeyEvent.VK_UP:
                game.getKeyboardHandler().keyPressed(50);
                break;
            case KeyEvent.VK_DOWN:
                game.getKeyboardHandler().keyPressed(56);
                break;
            case KeyEvent.VK_RIGHT:
                game.getKeyboardHandler().keyPressed(54);
                break;
            case KeyEvent.VK_0:
                startTrack(0);
                break;
            case KeyEvent.VK_1:
                startTrack(1);
                break;
            case KeyEvent.VK_2:
                startTrack(2);
                break;
            case KeyEvent.VK_3:
                startTrack(3);
                break;
            case KeyEvent.VK_4:
                startTrack(4);
                break;
            case KeyEvent.VK_5:
                startTrack(5);
                break;
            case KeyEvent.VK_6:
                startTrack(6);
                break;
            case KeyEvent.VK_7:
                startTrack(7);
                break;
            case KeyEvent.VK_8:
                startTrack(8);
                break;
            case KeyEvent.VK_9:
                startTrack(9);
                break;
        }
    }

    private void startTrack(int track) {
        game.startTrack(2, 2, track, true);
        game.restart(true);
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                game.getKeyboardHandler().keyReleased(52);
                break;
            case KeyEvent.VK_UP:
                game.getKeyboardHandler().keyReleased(50);
                break;
            case KeyEvent.VK_DOWN:
                game.getKeyboardHandler().keyReleased(56);
                break;
            case KeyEvent.VK_RIGHT:
                game.getKeyboardHandler().keyReleased(54);
                break;
        }
    }
}