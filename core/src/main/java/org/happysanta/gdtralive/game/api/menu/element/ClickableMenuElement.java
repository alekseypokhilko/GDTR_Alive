package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.menu.ViewUtils;

public abstract class ClickableMenuElement<T> implements MenuElement<T> {
    public static final int TEXT_SIZE = 20;
    protected T layout;
    protected TouchInterceptor<T> touchInterceptor;
    protected IMenuHelmetView<T> helmet;
    protected IMenuTextView<T> textView;
    protected OnMenuElementHighlightListener<T> onMenuElementHighlightListener = null;
    protected String text;
    protected boolean isHighlighted = false;
    protected boolean disabled = false;

    public ClickableMenuElement(T layout, IMenuTextView<T> textView, IMenuHelmetView<T> helmet, TouchInterceptor<T> touchInterceptor) {
        this.touchInterceptor = touchInterceptor;
        this.touchInterceptor.setTarget(this);
        this.layout = layout;
        this.textView = textView;
        this.helmet = helmet;
    }

    public boolean onTouch(T view, int action, int rawX, int rawY, ViewUtils<T> viewUtils) {
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

            case 2://MotionEvent.ACTION_MOVE:
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
        if (textView != null)
            textView.setTextOnUiThread(getTextForView());
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
}
