package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.runOnUiThread;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.game.api.menu.view.IMenuImageView;

public class MenuImageView<T> extends ImageView implements IMenuImageView<T> {
    public static final int[] locks = new int[]{
            R.drawable.s_lock0,
            R.drawable.s_lock1,
            R.drawable.s_lock2
    };

    public MenuImageView(Context context) {
        super(context);
    }

    @Override
    public void setImageResource(final int resid) {
        runOnUiThread(() -> MenuImageView.super.setImageResource(resid));
    }

    @Override
    public void setVisibility(final int visibility) {
        runOnUiThread(() -> MenuImageView.super.setVisibility(visibility));
    }

    @Override
    public void setLock(int index) {
        this.setImageResource(locks[index]);
    }

    public void setVisibility(boolean isLocked) {
        this.setVisibility(isLocked ? View.VISIBLE : View.GONE);
    }
}
