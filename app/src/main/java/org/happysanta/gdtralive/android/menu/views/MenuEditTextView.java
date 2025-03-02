package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.runOnUiThread;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;

import org.happysanta.gdtralive.game.api.menu.view.IMenuEditTextView;

public class MenuEditTextView<T> extends EditText implements IMenuEditTextView<T> {

	protected boolean isAttached = false;

	public MenuEditTextView(Context context) {
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
	public void setTextSize(final float size) {
		runOnUiThread(() -> MenuEditTextView.super.setTextSize(size));
	}

	@Override
	public void setTypeface(final Typeface typeface) {
		runOnUiThread(() -> MenuEditTextView.super.setTypeface(typeface));
	}

	@Override
	public void setVisibility(final int visibility) {
		runOnUiThread(() -> MenuEditTextView.super.setVisibility(visibility));
	}

	@Override
	public void setTextColor(int color) {
		super.setTextColor(color);
	}

	@Override
	public T getView() {
		return (T) this;
	}

	@Override
	public String getTextString() {
		return this.getText().toString();
	}
}
