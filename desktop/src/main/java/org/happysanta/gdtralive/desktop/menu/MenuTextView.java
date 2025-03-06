package org.happysanta.gdtralive.desktop.menu;

import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;

import javax.swing.JLabel;

public class MenuTextView<T> extends JLabel implements IMenuTextView<T> {
    @Override
    public void setText(CharSequence text) {
        super.setText(text.toString());
    }

    @Override
    public void setTextOnUiThread(CharSequence sequence) {
        this.setText(sequence);
    }

    @Override
    public void setTextColor(int color) {
        //todo
    }

    @Override
    public T getView() {
        return (T) this;
    }
}
