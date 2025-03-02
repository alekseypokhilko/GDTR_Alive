package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.content.Context;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.android.Global;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.ToggleMenuElement;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.util.Fmt;

public class AToggleMenuElement<T> extends ClickableMenuElement<T> implements ToggleMenuElement<T> {

    protected int selectedIndex;
    protected String[] options;
    protected MenuTextView optionTextView = null;
    protected ActionHandler<ToggleMenuElement<T>> action;

    public AToggleMenuElement(String text, int selectedIndex, String[] options, ActionHandler<ToggleMenuElement<T>> action) {
        this.text = text;
        this.selectedIndex = selectedIndex;
        this.options = options;
        this.action = action;

        createAllViews(getGDActivity());
        if (this.selectedIndex > options.length - 1)
            this.selectedIndex = 0;
        if (this.selectedIndex < 0)
            this.selectedIndex = options.length - 1;
        updateViewText();
    }

    @Override
    protected void createAllViews(Context context) {
        super.createAllViews(context);

        ((MenuTextView) textView.getView()).setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        optionTextView = new MenuTextView(getGDActivity());
        optionTextView.setText(this.options[this.selectedIndex]);
        optionTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
        optionTextView.setTextSize(TEXT_SIZE);
        optionTextView.setTypeface(Global.robotoCondensedTypeface);
        optionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        optionTextView.setPadding(
                ((MenuTextView) textView.getView()).getPaddingLeft(),
                ((MenuTextView) textView.getView()).getPaddingTop(),
                ((MenuTextView) textView.getView()).getPaddingRight(),
                ((MenuTextView) textView.getView()).getPaddingBottom()
        );

        ((LinearLayout) layout).addView(optionTextView);

        Helpers.getModManager().registerThemeReloadHandler(this::onOptionTextViewThemeReload);
    }

    @Override
    public int getSelectedOption() {
        return selectedIndex;
    }

    @Override
    protected void updateViewText() {
        if (textView != null && textView instanceof MenuTextView)
            ((MenuTextView) textView).setTextOnUiThread(getTextForView());
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

    private void onOptionTextViewThemeReload() {
        optionTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
    }
}
