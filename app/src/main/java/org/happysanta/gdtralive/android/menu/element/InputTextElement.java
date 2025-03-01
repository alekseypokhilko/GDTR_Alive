package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.android.Global;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.dto.InterfaceTheme;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;

public class InputTextElement implements MenuElement<View> {

    protected static final int TEXT_SIZE = 20;

    protected EditText editText;
    protected LinearLayout textView;
    protected MenuTextView optionTextView;

    protected ActionHandler<EditText> actionHandler;

    public InputTextElement(String title, String text, final ActionHandler<EditText> action) {
        this.actionHandler = action;
        textView = new LinearLayout(getGDActivity());
        String textValue = text == null ? "" : text;
        if (textValue.length() > 25) {
            textView.setOrientation(LinearLayout.VERTICAL);
        } else {
            textView.setOrientation(LinearLayout.HORIZONTAL);
        }
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        optionTextView = new MenuTextView(getGDActivity());
        optionTextView.setText(title);
        optionTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
        optionTextView.setTextSize(TEXT_SIZE);
        optionTextView.setTypeface(Global.robotoCondensedTypeface);
        optionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        optionTextView.setPadding(
                textView.getPaddingLeft(),
                textView.getPaddingTop(),
                textView.getPaddingRight(),
                textView.getPaddingBottom()
        );

        editText = new EditText(getGDActivity());
        editText.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
        editText.setText(textValue);
        editText.setLines(1);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && actionHandler != null) {
                actionHandler.handle(editText);
            }
        });

        getGDActivity().textInputs.add(editText); //todo refactor / find better solution

        Helpers.getModManager().registerThemeReloadHandler(this::onThemeReload);
        textView.addView(optionTextView);
        textView.addView(editText);
    }

    @Override
    public View getView() {
        return textView;
    }

    public String getText() {
        return editText.getText().toString(); //todo handle npe
    }

    @Override
    public void setText(String text) {
        editText.setText(text);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void performAction(int k) {
    }

    public void onThemeReload() {
        InterfaceTheme interfaceTheme = Helpers.getModManager().getInterfaceTheme();
        optionTextView.setTextColor(interfaceTheme.getTextColor());
        editText.setTextColor(interfaceTheme.getTextColor());
        editText.setBackgroundColor(interfaceTheme.getMenuBackgroundColor());
    }
}
