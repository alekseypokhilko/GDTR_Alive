package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuImageView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.model.Color;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

public class MenuActionElement<T> extends ClickableMenuElement<T> implements IMenuActionElement<T> {

    protected boolean isLocked = false;
    protected boolean isBlackLock = true;
    protected IMenuImageView<T> lockImage;
    protected ActionHandler<IMenuActionElement<T>> action;
    protected PlatformMenuElementFactory<T> factory;

    protected int actionValue = -1;

    public MenuActionElement(String s, int value, ActionHandler<IMenuActionElement<T>> action,
                             IMenuHelmetView<T> helmetView, IMenuTextView<T> textView,
                             TouchInterceptor<T> touchInterceptor, T layout, IMenuImageView<T> lockImage,
                             PlatformMenuElementFactory<T> factory) {
        super(layout, textView, helmetView, touchInterceptor);
        this.actionValue = value;
        this.action = action;
        this.text = s;
        this.textView.setText(getTextForView());
        this.lockImage = lockImage;
        this.factory = factory;
    }

    public int getActionValue() {
        return actionValue;
    }

    @Override
    public void setLock(boolean isLocked, boolean isBlackLock) {
        this.isLocked = isLocked;
        this.isBlackLock = isBlackLock;

        if (lockImage != null) {
            lockImage.setVisibility(isLocked);
            lockImage.setLock(isBlackLock ? factory.getModManager().getInterfaceTheme().getLockSkinIndex() : 1);
        }
    }

    @Override
    public void setColor(Color color) {
        try {
            super.textView.setTextColor(color.intValue());
        } catch (Exception ignore) {
        }
    }

    @Override
    public void performAction(int k) {
        if (disabled) return;

        if (k == KeyboardHandler.KEY_FIRE) {
            if (action != null) {
                action.handle(this);
            }
        }
    }

    @Override
    protected void onHighlightChanged() {
        lockImage.setLock(isHighlighted ? 2 : factory.getModManager().getInterfaceTheme().getLockSkinIndex());
    }
}
