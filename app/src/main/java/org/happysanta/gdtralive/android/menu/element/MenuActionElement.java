package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.element.ClickableMenuElement;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

public class MenuActionElement<T> extends ClickableMenuElement<T> {

    protected static final int DISABLED_COLOR = 0xff999999;
    public static final int LOCK_IMAGE_MARGIN_RIGHT = 5;
    public static final int[] locks = new int[]{
            R.drawable.s_lock0,
            R.drawable.s_lock1,
            R.drawable.s_lock2
    };

    protected boolean isLocked = false;
    protected boolean isBlackLock = true;
    protected MenuImageView lockImage = null;
    protected ActionHandler<MenuElement<T>> action;

    protected int actionValue = -1;

    public MenuActionElement(String s, int value, ActionHandler<MenuElement<T>> action,
                             IMenuHelmetView<T> helmetView, MenuTextView<T> textView, TouchInterceptor<T> touchInterceptor, T layout) {
        super(layout, textView, helmetView, touchInterceptor);
        this.actionValue = value;
        this.action = action;
        this.text = s;
        this.textView.setText(getTextForView());

        Context context = getGDActivity();
        createAllViews(context);
    }

    protected void createAllViews(Context context) {
        lockImage = new MenuImageView(context);
        lockImage.setScaleType(ImageView.ScaleType.CENTER);
        lockImage.setVisibility(View.GONE);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, getDp(MenuActionElement.LOCK_IMAGE_MARGIN_RIGHT), 0);
        lockImage.setLayoutParams(lp);

        ((LinearLayout)layout).addView(lockImage, 1);
    }

    public int getActionValue() {
        return actionValue;
    }

    public void setLock(boolean isLocked, boolean isBlackLock) {
        this.isLocked = isLocked;
        this.isBlackLock = isBlackLock;

        if (lockImage != null) {
            lockImage.setVisibility(isLocked ? View.VISIBLE : View.GONE);
            lockImage.setImageResource(locks[isBlackLock ? Helpers.getModManager().getInterfaceTheme().getLockSkinIndex() : 1]);
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
        lockImage.setImageResource(locks[isHighlighted ? 2 : Helpers.getModManager().getInterfaceTheme().getLockSkinIndex()]);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;

        if (disabled) {
            ((MenuTextView) textView).setTextColor(DISABLED_COLOR);
        } else {
            ((MenuTextView) textView).setTextColor(getGDActivity().getResources().getColorStateList(R.drawable.menu_item_color));
        }
    }

}
