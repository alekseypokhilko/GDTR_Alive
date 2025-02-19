package org.happysanta.gdtralive.game.visual;


import java.io.Serializable;
import java.util.TimerTask;

public class MessageElement extends TimerTask implements Serializable {

    Runnable action;

    public MessageElement(Runnable action) {
        this.action = action;
    }

    @Override
    public void run() {
        action.run();
    }

}
