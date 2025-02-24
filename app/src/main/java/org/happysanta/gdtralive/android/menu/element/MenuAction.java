package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.game.util.ActionHandler;
import org.happysanta.gdtralive.android.menu.MenuHandler;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.engine.KeyboardHandler;

public class MenuAction
        extends ClickableMenuElement
        implements MenuHandler, MenuElement {

    protected static final int DISABLED_COLOR = 0xff999999;
    public static final int LOCK_IMAGE_MARGIN_RIGHT = 5;
    public static final int locks[] = new int[]{
            R.drawable.s_lock0,
            R.drawable.s_lock1,
            R.drawable.s_lock2
    };

    public static final int OK = 0;
    public static final int BACK = 1;
    public static final int EXIT = 2;
    public static final int YES = 3;
    public static final int NO = 4;
    public static final int PLAY_MENU = 5;
    public static final int GO_TO_MAIN = 6;
    public static final int RESTART = 7;
    public static final int NEXT = 8;
    public static final int CONTINUE = 9;
    public static final int INSTALL = 10;
    public static final int LOAD = 11;
    public static final int SELECT_FILE = 12;
    public static final int DELETE = 13;
    public static final int RESTART_WITH_NEW_LEVEL = 14;
    public static final int SEND_LOGS = 15;
    public static final int LIKE = 16;

    protected MenuHandler handler;
    protected boolean isLocked = false;
    protected boolean isBlackLock = true;
    protected MenuImageView lockImage = null;
    protected ActionHandler action;

    protected int actionValue = -1;

    public MenuAction(String s, int value, MenuHandler handler, ActionHandler action) {
        actionValue = value;
        this.handler = handler;
        this.action = action;

        text = s;

        createAllViews();
    }

    public MenuAction(String s, MenuHandler handler, ActionHandler action) {
        this(s, -1, handler, action);
    }

    @Override
    protected void createAllViews() {
        super.createAllViews();

        Context context = getGDActivity();
        lockImage = new MenuImageView(context);
        lockImage.setScaleType(ImageView.ScaleType.CENTER);
        lockImage.setVisibility(View.GONE);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, getDp(MenuAction.LOCK_IMAGE_MARGIN_RIGHT), 0);
        lockImage.setLayoutParams(lp);

        layout.addView(lockImage, 1);
    }

    public int getActionValue() {
        return actionValue;
    }

    public void setHandler(MenuHandler hander) {
        this.handler = hander;
    }

    public void setLock(boolean isLocked, boolean isBlackLock) {
        this.isLocked = isLocked;
        this.isBlackLock = isBlackLock;

        lockImage.setVisibility(isLocked ? View.VISIBLE : View.GONE);
        lockImage.setImageResource(locks[isBlackLock ? Helpers.getModManager().getInterfaceTheme().getLockSkinIndex() : 1]);
    }

    @Override
    public void setText(String s) {
        text = s;
        updateViewText();
    }

    @Override
    public void performAction(int k) {
        if (disabled || handler == null) return;

        if (k == KeyboardHandler.KEY_FIRE) {
            if (action != null) {
                action.handle(this);
            }
            handler.handleAction(this);
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
            ((MenuTextView) textView).setTextColor(defaultColorStateList());
        }
    }

}
