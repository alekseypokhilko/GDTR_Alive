package org.happysanta.gdtralive.game.api.menu.view;

public interface IMenuEditTextView<T> {

    void setText(CharSequence text);
    void setTextColor(int color);
    T getView();
    String getTextString();
}
