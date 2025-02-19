package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.runOnUiThread;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

public class MenuRelativeLayout extends RelativeLayout {

	public MenuRelativeLayout(Context context) {
		super(context);
	}

	@Override
	public void removeAllViews() {
		runOnUiThread(() -> MenuRelativeLayout.super.removeAllViews());
	}

	@Override
	public void setVisibility(final int visibility) {
		runOnUiThread(() -> MenuRelativeLayout.super.setVisibility(visibility));
	}

	@Override
	public void addView(final View view) {
		runOnUiThread(() -> MenuRelativeLayout.super.addView(view));
	}

	@Override
	public void setPadding(final int left, final int top, final int right, final int bottom) {
		runOnUiThread(() -> MenuRelativeLayout.super.setPadding(left, top, right, bottom));
	}

}
