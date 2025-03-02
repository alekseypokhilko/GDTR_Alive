package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.util.Fmt;

public class ToggleMenuElement<T> extends ClickableMenuElement<T> implements IToggleMenuElement<T> {
    private int selectedIndex;
    private final String[] options;
    protected final IMenuTextView<T> optionTextView;
    private final ActionHandler<IToggleMenuElement<T>> action;

    public ToggleMenuElement(String text, int selectedIndex, String[] options, ActionHandler<IToggleMenuElement<T>> action,
                             IMenuHelmetView<T> helmetView, IMenuTextView<T> textView,
                             TouchInterceptor<T> touchInterceptor, T layout, IMenuTextView<T> optionsTextView) {
        super(layout, textView, helmetView, touchInterceptor);
        this.text = text;
        this.selectedIndex = selectedIndex;
        this.options = options;
        this.action = action;
        this.optionTextView = optionsTextView;
        this.textView.setText(getTextForView());
        this.optionTextView.setText(this.options[this.selectedIndex]);
        if (this.selectedIndex > options.length - 1)
            this.selectedIndex = 0;
        if (this.selectedIndex < 0)
            this.selectedIndex = options.length - 1;
        updateViewText();
    }

    @Override
    public int getSelectedOption() {
        return selectedIndex;
    }

    @Override
    protected void updateViewText() {
        if (textView != null)
            textView.setTextOnUiThread(getTextForView());
        if (optionTextView != null)
            optionTextView.setTextOnUiThread(this.options[this.selectedIndex]);
    }

    @Override
    public void performAction(int k) {
        switch (k) {
            case KeyboardHandler.KEY_FIRE:
                selectedIndex++;
                if (selectedIndex > 1)
                    selectedIndex = 0;
                this.options[this.selectedIndex] = options[selectedIndex == 1 ? 1 : 0];
                updateViewText();
                performAction();
                break;


            case KeyboardHandler.KEY_RIGHT:
                if (selectedIndex == 1) {
                    selectedIndex = 0;
                    this.options[this.selectedIndex] = options[0];
                    performAction();
                    updateViewText();
                }
                break;


            case KeyboardHandler.KEY_LEFT:
                if (selectedIndex == 0) {
                    selectedIndex = 1;
                    this.options[this.selectedIndex] = options[1];
                    performAction();
                    updateViewText();
                }
        }
    }

    @Override
    protected String getTextForView() {
        return Fmt.colon(text);
    }

    private void performAction() {
        if (action != null) {
            action.handle(this);
        }
    }
}
