package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.runOnUiThread;

import android.content.Context;
import android.widget.ImageView;

public class MenuImageView extends ImageView {

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
