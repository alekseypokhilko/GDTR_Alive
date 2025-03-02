package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.runOnUiThread;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MenuElementLinearLayout extends LinearLayout {

	boolean interceptTouchEvents = false;

	public MenuElementLinearLayout(Context context) {
		super(context);
	}

	public MenuElementLinearLayout(Context context, boolean interceptTouchEvents) {
		super(context);
		this.interceptTouchEvents = interceptTouchEvents;
	}

	@Override
	public void removeAllViews() {
		runOnUiThread(() -> MenuElementLinearLayout.super.removeAllViews());
	}

	@Override
	public void setVisibility(final int visibility) {
		runOnUiThread(() -> MenuElementLinearLayout.super.setVisibility(visibility));
	}

	@Override
	public void addView(final View view) {
		runOnUiThread(() -> MenuElementLinearLayout.super.addView(view));
	}

	@Override
	public void setPadding(final int left, final int top, final int right, final int bottom) {
		runOnUiThread(() -> MenuElementLinearLayout.super.setPadding(left, top, right, bottom));
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent evt) {
		return interceptTouchEvents;
	}

}
