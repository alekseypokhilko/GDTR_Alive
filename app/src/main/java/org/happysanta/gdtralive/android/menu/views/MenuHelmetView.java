package org.happysanta.gdtralive.android.menu.views;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.isSDK11OrHigher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.android.ACanvas;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.GdBitmap;
import org.happysanta.gdtralive.game.api.Sprite;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;

public class MenuHelmetView<T> extends View implements IMenuHelmetView<T> {

	protected static final int WIDTH = 8;
	protected static final int HEIGHT = 8;

	protected static int angle = 0;
	protected static long angleLastMs = 0;
	protected static final int angleInterval = 50;
	protected static final int angleDelta = 10;

	protected boolean show = false;
	protected boolean _setMeasuredHeight = false;
	protected GdBitmap helmet = ACanvas.getInterfaceSprite(Sprite.HELMET);
	protected static MenuHelmetView lastActive = null;

	public static void clearStaticFields() {
		lastActive = null;
		angle = 0;
		angleLastMs = 0;
	}

	public MenuHelmetView(Context context) {
		super(context);
		this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
	}

	public MenuHelmetView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	@Override
	public void setShow(boolean show) {
		setShow(show, true);
	}

	@Override
	public T getView() {
		return (T) this;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.save();
		float density = Helpers.getModManager().getInterfaceDensity();
		canvas.scale(density, density);

		drawHelmet(canvas);

		canvas.restore();
		invalidate();
	}

	protected void drawHelmet(Canvas canvas) {
		if (show) {
			long ms = System.currentTimeMillis();
			if (angleLastMs == 0 || ms - angleLastMs >= angleInterval) {
				angle += angleDelta;
				if (angle >= 360) angle -= 360;
				angleLastMs = ms;
			}

			float spriteDensity = Helpers.getModManager().getInterfaceDensity();
			int y = getScaledHeight() / 2 - helmet.getHeightDp(spriteDensity) / 2;

			canvas.save();
			canvas.rotate(angle, helmet.getWidthDp(spriteDensity) / 2, y + helmet.getHeightDp(spriteDensity) / 2);
			canvas.drawBitmap(
					helmet.bitmap,
					new Rect(0, 0, helmet.getWidth(), helmet.getHeight()),
					new RectF(0, y, helmet.getWidthDp(spriteDensity), y + helmet.getHeightDp(spriteDensity)),
					null
			);
			canvas.restore();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(getDp(WIDTH * 2.2f));
		int height = heightMeasureSpec;
		if (_setMeasuredHeight)
			height = MeasureSpec.getSize(getDp(HEIGHT * 2.2f));
		else if (!isSDK11OrHigher()) {
			height = MeasureSpec.getSize(getDp(HEIGHT * 4.5f));
		}
		setMeasuredDimension(width, height);
	}

	public void setShow(boolean show, boolean checkLast) {
		if (checkLast && lastActive != null) {
			lastActive.setShow(false, false);
		}
		this.show = show;
		lastActive = this;
	}

	protected int getScaledHeight() {
		return Math.round(getHeight() / Helpers.getModManager().getInterfaceDensity());
	}
}
