package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.Global;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.AViewUtils;
import org.happysanta.gdtralive.android.menu.views.MenuHelmetView;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.IMenuTextView;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.OnMenuElementHighlightListener;
import org.happysanta.gdtralive.game.api.menu.ViewUtils;

public abstract class ClickableMenuElement<T> implements MenuElement<T> {
    public static final int TEXT_SIZE = 20;
    public static final int PADDING_TOP = 5;
    protected IMenuTextView<T> textView;
    protected String text;
    protected T layout;
    protected IMenuHelmetView<T> helmet;
    protected OnMenuElementHighlightListener<T> onMenuElementHighlightListener = null;
    protected boolean isHighlighted = false;
    protected boolean disabled = false;

//    public ClickableMenuElement(T layout, MenuTextView<T> textView, IMenuHelmetView<T> helmet) {
//        this.layout = layout;
//        this.textView = textView;
//        this.helmet = helmet;
//    }

    protected void createAllViews(Context context) {
        helmet = createHelmetView(context);
        textView = createTextView(context);
        layout = createLayout(context, helmet, textView, createOnTouchListener());
    }

    private View.OnTouchListener createOnTouchListener() {
        ViewUtils<T> viewUtils = new AViewUtils();
        return (view, motionEvent) -> onTouch((T) view, motionEvent.getAction(), (int) motionEvent.getRawX(), (int) motionEvent.getRawY(), viewUtils);
    }

    private IMenuHelmetView<T> createHelmetView(Context context) {
        return new MenuHelmetView<>(context);
    }

    private T createLayout(Context context, IMenuHelmetView<T> helmet, IMenuTextView<T> textView, View.OnTouchListener onTouchListener) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView((View) helmet.getView());
        layout.addView((View) textView.getView());
        layout.setOnTouchListener(onTouchListener);
        return (T) layout;
    }

    private boolean onTouch(T view, int action, int rawX, int rawY, ViewUtils<T> viewUtils) {
        if (isDisabled()) {
            return false;
        }
        switch (action) {
            case 0://MotionEvent.ACTION_DOWN:
                viewUtils.setSelected(view, true);
                helmet.setShow(true);

                if (onMenuElementHighlightListener != null)
                    onMenuElementHighlightListener.onElementHighlight(this);

                setHighlighted(true);
                break;

            case 3://MotionEvent.ACTION_CANCEL:
            case 1://MotionEvent.ACTION_UP:
                viewUtils.setSelected(view, false);

                if (action == 1 /*MotionEvent.ACTION_UP*/ && viewUtils.inViewBounds(view, rawX, rawY)) {
                    performAction(KeyboardHandler.KEY_FIRE);
                }

                setHighlighted(false);
                break;

            case MotionEvent.ACTION_MOVE:
                if (!viewUtils.inViewBounds(view, rawX, rawY)) {
                    viewUtils.setSelected(view, false);
                    setHighlighted(false);
                } else {
                    viewUtils.setSelected(view, true);
                    setHighlighted(true);
                }
                break;
        }
        return true;
    }

    protected MenuTextView<T> createTextView(Context context) {
        MenuTextView<T> mtv = new MenuTextView<>(context);
        mtv.setText(getTextForView());
        mtv.setTextColor(getGDActivity().getResources().getColorStateList(R.drawable.menu_item_color));
        mtv.setTypeface(Global.robotoCondensedTypeface);
        mtv.setTextSize(TEXT_SIZE);
        mtv.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
        mtv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mtv.setPadding(0, getDp(PADDING_TOP), 0, getDp(PADDING_TOP));
        Helpers.getModManager().registerThemeReloadHandler(() -> getTextView().setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor()));
        return mtv;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public T getView() {
        return (T) layout;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        updateViewText();
    }

    public String getText() {
        return text;
    }

    protected void updateViewText() {
        if (textView != null && textView instanceof MenuTextView)
            ((MenuTextView) textView).setTextOnUiThread(getTextForView());
    }

    protected String getTextForView() {
        return text;
    }

    public void setOnHighlightListener(OnMenuElementHighlightListener listener) {
        onMenuElementHighlightListener = listener;
    }

    @Override
    public void showHelmet() {
        helmet.setShow(true);
    }

    private void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
        onHighlightChanged();
    }

    protected void onHighlightChanged() {
    }

    public boolean isDisabled() {
        return disabled;
    }

    public IMenuTextView<T> getTextView() {
        return textView;
    }
}
