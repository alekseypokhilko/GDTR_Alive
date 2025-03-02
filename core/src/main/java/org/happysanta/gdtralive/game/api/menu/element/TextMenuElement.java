package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;

//todo refactor
public class TextMenuElement<T> implements MenuElement<T> {

    protected static final int TEXT_SIZE = 15;
    protected IMenuTextView<T> textView;

    public TextMenuElement(IMenuTextView<T> menuTextView) {
        textView = menuTextView;
    }

    @Override
    public T getView() {
        return (T) textView;
    }

    @Override
    public void setText(String text) {
        throw new RuntimeException("org.happysanta.gdtralive.game.api.menu.element.TextMenuElement.setText");
//        this.spanned = Html.fromHtml(text);
//        textView.setTextOnUiThread(spanned);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    public IMenuTextView<T> getTextView() {
        return textView;
    }
}
