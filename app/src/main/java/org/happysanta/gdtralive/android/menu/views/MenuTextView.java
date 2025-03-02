package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.runOnUiThread;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;

public class MenuTextView<T> extends TextView implements IMenuTextView<T> {

	protected boolean isAttached = false;

	public MenuTextView(Context context) {
		super(context);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		isAttached = true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isAttached = false;
	}

	@Override
	public boolean isAttachedToWindow() {
		return isAttached;
	}

	@Override
	public void setTextOnUiThread(final CharSequence sequence) {
		runOnUiThread(() -> MenuTextView.super.setText(sequence));
	}

	@Override
	public void setTextSize(final float size) {
		runOnUiThread(() -> MenuTextView.super.setTextSize(size));
	}

	@Override
	public void setTypeface(final Typeface typeface) {
		runOnUiThread(() -> MenuTextView.super.setTypeface(typeface));
	}

	@Override
	public void setVisibility(final int visibility) {
		runOnUiThread(() -> MenuTextView.super.setVisibility(visibility));
	}

	@Override
	public void setTextColor(int color) {
		super.setTextColor(color);
	}

	@Override
	public T getView() {
		return (T) this;
	}
}
