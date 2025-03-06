package org.happysanta.gdtralive.desktop.menu;

import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;

import javax.swing.JLabel;

public class MenuHelmetView<T> extends JLabel implements IMenuHelmetView<T> {
    @Override
    public void setShow(boolean show) {

    }

    @Override
    public T getView() {
        return (T) this;
    }
}
