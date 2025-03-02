package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.runOnUiThread;

import android.content.Context;
import android.widget.ImageView;

import org.happysanta.gdtralive.game.api.menu.view.IMenuImageView;

public class MenuImageView extends ImageView implements IMenuImageView {

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

}
