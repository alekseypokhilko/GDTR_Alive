package org.happysanta.gdtralive.android;

public class GDBitmapHolder {

    public GdBitmap gdBitmap = null;
    public GdBitmap[] gdBitmaps = null;
    public boolean isArray = false;

    public GDBitmapHolder(GdBitmap gdBitmap) {
        this.gdBitmap = gdBitmap;
    }

    public GDBitmapHolder(GdBitmap[] gdBitmaps) {
        this.gdBitmaps = new GdBitmap[gdBitmaps.length];
        System.arraycopy(gdBitmaps, 0, this.gdBitmaps, 0, gdBitmaps.length);
        isArray = true;
    }

}