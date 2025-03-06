package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.KeyboardHandler;

import java.awt.event.KeyEvent;

public class DesktopKeyboardController {

    private Application application;
    private Game game;

    public void setApplication(Application application) {
        this.game = application.getGame();
        this.application = application;
    }

    public void keyPressed(KeyEvent e) {
        KeyboardHandler keyboardHandler = game.getKeyboardHandler();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                keyboardHandler.keyPressed(52);
                break;
            case KeyEvent.VK_UP:
                keyboardHandler.keyPressed(50);
                break;
            case KeyEvent.VK_DOWN:
                keyboardHandler.keyPressed(56);
                break;
            case KeyEvent.VK_RIGHT:
                keyboardHandler.keyPressed(54);
                break;
            case KeyEvent.VK_0:
                startTrack(0);
                break;
            case KeyEvent.VK_ESCAPE:
                if (application.isMenuShown()) {
                    application.menuToGame();
                } else {
                    application.gameToMenu();
                }
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
//        game.startTrack(2, 2, track);
        game.restart(true);
    }

    public void keyReleased(KeyEvent e) {
        KeyboardHandler keyboardHandler = game.getKeyboardHandler();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                keyboardHandler.keyReleased(52);
                break;
            case KeyEvent.VK_UP:
                keyboardHandler.keyReleased(50);
                break;
            case KeyEvent.VK_DOWN:
                keyboardHandler.keyReleased(56);
                break;
            case KeyEvent.VK_RIGHT:
                keyboardHandler.keyReleased(54);
                break;
        }
    }
}