package org.happysanta.gdtralive.android;

import android.graphics.Bitmap;

public class GdBitmap {
    public Bitmap bitmap;

    public GdBitmap(Bitmap b) {
        bitmap = b;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public int getWidthDp(float spriteDensity) {
        return Math.round(getWidth() / spriteDensity);
    }

    public int getHeightDp(float spriteDensity) {
        return Math.round(getHeight() / spriteDensity);
    }

}
