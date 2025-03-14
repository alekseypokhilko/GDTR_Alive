package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.runOnUiThread;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MenuLinearLayout extends LinearLayout {

	boolean interceptTouchEvents = false;

	public MenuLinearLayout(Context context) {
		super(context);
	}

	public MenuLinearLayout(Context context, boolean interceptTouchEvents) {
		super(context);
		this.interceptTouchEvents = interceptTouchEvents;
	}

	@Override
	public void removeAllViews() {
		runOnUiThread(() -> MenuLinearLayout.super.removeAllViews());
	}

	@Override
	public void setVisibility(final int visibility) {
		runOnUiThread(() -> MenuLinearLayout.super.setVisibility(visibility));
	}

	@Override
	public void addView(final View view) {
		runOnUiThread(() -> MenuLinearLayout.super.addView(view));
	}

	@Override
	public void setPadding(final int left, final int top, final int right, final int bottom) {
		runOnUiThread(() -> MenuLinearLayout.super.setPadding(left, top, right, bottom));
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent evt) {
		return interceptTouchEvents;
	}

}
