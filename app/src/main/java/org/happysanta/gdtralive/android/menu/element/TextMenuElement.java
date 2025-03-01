package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;

import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.api.MenuElement;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;

public class TextMenuElement implements MenuElement<View> {

    protected static final int TEXT_SIZE = 15;
    protected static final int TEXT_COLOR = 0xff000000;

    protected Spanned spanned;
    protected MenuTextView textView;

    public TextMenuElement(String text) {
        this.spanned = SpannedString.valueOf(text);
        textView = createTextView();
    }

    public TextMenuElement(Spanned text) {
        this.spanned = text;
        textView = createTextView();
    }

    protected MenuTextView createTextView() {
        Context activity = getGDActivity();

        MenuTextView textView = new MenuTextView(activity);
        textView.setText(spanned);
        textView.setTextColor(TEXT_COLOR);
        textView.setTextSize(TEXT_SIZE);
        textView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
        Helpers.getModManager().registerThemeReloadHandler(this::onThemeReload);
        textView.setLineSpacing(0f, 1.5f);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Linkify.addLinks(textView, Linkify.WEB_URLS);
        textView.setLinksClickable(true);

        return textView;
    }

    @Override
    public View getView() {
        return textView;
    }

    public String getText() {
        return spanned.toString();
    }

    @Override
    public void setText(String text) {
        this.spanned = Html.fromHtml(text);
        textView.setTextOnUiThread(spanned);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void performAction(int k) {
    }

    public void onThemeReload() {
        getTextView().setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
    }

    public MenuTextView getTextView() {
        return textView;
    }
}
