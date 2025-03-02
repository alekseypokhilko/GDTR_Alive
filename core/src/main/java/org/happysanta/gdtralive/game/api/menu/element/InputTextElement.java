package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.api.menu.view.IMenuEditTextView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

public class InputTextElement<T> implements IInputTextElement<T> {
    protected IMenuEditTextView<T> editText;
    protected T textView;
    protected IMenuTextView<T> optionTextView;

    protected ActionHandler<IInputTextElement<T>> actionHandler;

    public InputTextElement(final ActionHandler<IInputTextElement<T>> action, IMenuTextView<T> optionTextView, T textView, IMenuEditTextView<T> editText) {
        this.actionHandler = action;
        this.optionTextView = optionTextView;
        this.textView = textView;
        this.editText = editText;
    }

    @Override
    public T getView() {
        return textView;
    }

    public String getText() {
        return editText.getTextString();
    }

    @Override
    public void setText(String text) {
        editText.setText(text);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

}
