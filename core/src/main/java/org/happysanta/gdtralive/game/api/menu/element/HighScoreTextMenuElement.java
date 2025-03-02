package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.view.IMenuImageView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;

//todo refactor
public class HighScoreTextMenuElement<T> extends TextMenuElement<T> implements MenuElement<T> {
    protected T layout;
    protected IMenuImageView<T> image;

    public HighScoreTextMenuElement(int place, boolean layoutPadding, IMenuTextView<T> menuTextView, T layout) {
        super(menuTextView);
        this.layout = layout;
    }

    public HighScoreTextMenuElement(boolean subtitle, IMenuTextView<T> menuTextView, T layout) {
        super(menuTextView);
        this.layout = layout;
    }

    @Override
    public T getView() {
        return (T) layout;
    }

}
