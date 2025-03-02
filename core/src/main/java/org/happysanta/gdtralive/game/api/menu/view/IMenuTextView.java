package org.happysanta.gdtralive.game.api.menu.view;

public interface IMenuTextView<T> {

    void setText(CharSequence text);
    void setTextOnUiThread(CharSequence sequence);
    void setTextColor(int color);
    T getView();
}
