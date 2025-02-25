package org.happysanta.gdtralive.android.menu.element;

import org.happysanta.gdtralive.game.api.util.ActionHandler;

public class PropInput extends InputTextElement {
    private final String key;
    ActionHandler<PropInput> action;
    //todo create custom edit element by prop type
    public PropInput(String title, String text, String key, ActionHandler<PropInput> action) {
        super(title, text, null);
        this.action = action;
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && action != null) {
                extracted();
            }
        });
        this.key = key;
    }

    private void extracted() {
        action.handle(this);
    }

    public String getKey() {
        return key;
    }
}
