package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;
import static org.happysanta.gdtralive.android.Helpers.loadBitmapFromDrawable;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.game.api.external.GdCanvas;
import org.happysanta.gdtralive.game.api.model.Color;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.model.EngineStateRecord;
import org.happysanta.gdtralive.game.api.Sprite;
import org.happysanta.gdtralive.game.api.model.ViewState;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ACanvas implements GdCanvas {
    private final ModManager modManager;
    private final Paint paint;
    private final Paint infoFont;
    private final Paint timerFont;

    public static Map<Sprite, GDBitmapHolder> interfaceSprites = getDefaultInterfaceSprites();
    private final LinkedHashMap<String, Object> props = new LinkedHashMap<>();
    private Canvas canvas;

    public ACanvas(ModManager modManager) {
        this.modManager = modManager;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1); //todo move to settings

        infoFont = new Paint();
        infoFont.setTextSize(20);
        infoFont.setAntiAlias(true);
        infoFont.setTypeface(Global.robotoCondensedTypeface);

        timerFont = new Paint();
        timerFont.setTextSize(18);
        timerFont.setAntiAlias(true);
        timerFont.setTypeface(Global.robotoCondensedTypeface);

        this.props.put(Sprite.START_FLAG_0.name(), fromDrawable(R.drawable.s_flag_start0));
        this.props.put(Sprite.START_FLAG_1.name(), fromDrawable(R.drawable.s_flag_start1));
        this.props.put(Sprite.START_FLAG_2.name(), fromDrawable(R.drawable.s_flag_start2));
        this.props.put(Sprite.FINISH_FLAG_0.name(), fromDrawable(R.drawable.s_flag_finish0));
        this.props.put(Sprite.FINISH_FLAG_1.name(), fromDrawable(R.drawable.s_flag_finish1));
        this.props.put(Sprite.FINISH_FLAG_2.name(), fromDrawable(R.drawable.s_flag_finish2));

        props.put(Sprite.STEERING.name(), ACanvas.fromDrawable(R.drawable.s_steering));
        props.put(Sprite.ENGINE.name(), ACanvas.fromDrawable(R.drawable.s_engine));
        props.put(Sprite.FENDER.name(), ACanvas.fromDrawable(R.drawable.s_fender));
        props.put(Sprite.WHEEL_SMALL.name(), ACanvas.fromDrawable(R.drawable.s_wheel1));
        props.put(Sprite.WHEEL_BIG.name(), ACanvas.fromDrawable(R.drawable.s_wheel2));
        props.put(Sprite.HELMET.name(), ACanvas.fromDrawable(R.drawable.s_helmet));
        props.put(Sprite.ARM.name(), ACanvas.fromDrawable(R.drawable.s_bluearm));
        props.put(Sprite.LEG.name(), ACanvas.fromDrawable(R.drawable.s_blueleg));
        props.put(Sprite.BODY.name(), ACanvas.fromDrawable(R.drawable.s_bluebody));

    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setColor(Color color) {
        setColor(color.r, color.g, color.b);
    }

    public void setColor(int r, int g, int b) {
        paint.setColor(0xFF000000 | (r << 16) | (g << 8) | b);
    }

    public void drawRect(int left, int top, int right, int bottom, ViewState view) {
        canvas.drawRect(left, top, right, bottom, paint);
    }

    public void drawSpriteWithRotation(Float j, Float k, ViewState view, EngineStateRecord state, Float fAngleDeg, Sprite sprite) {
        GdBitmap bitmap = getSprite(sprite);
        if (bitmap != null) {
            float x = offsetX(j, view.offsetX) - bitmap.getWidthDp(modManager.getSpriteDensity()) / 2;
            float y = offsetY(k, view.offsetY) - bitmap.getHeightDp(modManager.getSpriteDensity()) / 2;
            drawBitmapWithRotation(bitmap, fAngleDeg, x, y);
        }
    }

    public void drawSprite2(Float j, Float k, ViewState view, EngineStateRecord state, Sprite sprite) {
        GdBitmap bitmap = getSprite(sprite);
        float x = offsetX(j - bitmap.getWidthDp(modManager.getSpriteDensity()) / 2, view.offsetX);
        float y = offsetY(k + bitmap.getHeightDp(modManager.getSpriteDensity()) / 2, view.offsetY);
        drawBitmap(bitmap, x, y);
    }

    public void drawBodySprite(Sprite sprite, Float l1, Float i2, Float fAngleDeg, int league) {
        GdBitmap bikerSprite = getSprite(sprite);
        if (bikerSprite != null) {
            float x = l1 - bikerSprite.getWidthDp(modManager.getSpriteDensity()) / 2;
            float y = i2 - bikerSprite.getHeightDp(modManager.getSpriteDensity()) / 2;
            drawBitmapWithRotation(bikerSprite, fAngleDeg, x, y);
        }
    }

    public void drawLogoSprite(Sprite sprite, ViewState view) {
        GdBitmap bitmap = getInterfaceSprite(sprite);
        if (bitmap != null) {
            drawBitmap(bitmap, (float) view.width / 2 - bitmap.getWidthDp(modManager.getSpriteDensity()) / 2,
                    (float) (view.height / 2 - bitmap.getHeightDp(modManager.getSpriteDensity()) / 1.6));
        }
    }

    public void drawLine(int j, int k, int l, int i1, ViewState view) {
        canvas.drawLine(
                offsetX((j << 2) / (float) 0xFFFF, view.offsetX),
                offsetY((k << 2) / (float) 0xFFFF, view.offsetY),
                offsetX((l << 2) / (float) 0xFFFF, view.offsetX),
                offsetY((i1 << 2) / (float) 0xFFFF, view.offsetY),
                paint);
    }

    public void drawLine2(int j, int k, int l, int i1, ViewState view) {
        canvas.drawLine(offsetX(j, view.offsetX), offsetY(k, view.offsetY), offsetX(l, view.offsetX), offsetY(i1, view.offsetY), paint);
    }

    public float offsetX(float j, int offsetX) {
        return j + offsetX;
    }

    public float offsetY(float j, int offsetY) {
        return -j + offsetY - getGDActivity().getButtonsLayoutHeight() / 2;
    }

    public void drawInfoMessage2(String message, Color color, ViewState view) {
        setColor(color);
        infoFont.setColor(paint.getColor());
        canvas.drawText(message, view.width / 2 - infoFont.measureText(message) / 2, view.height / 5, infoFont);
    }

    public void drawTimer2(int color, String time, ViewState view) {
        timerFont.setColor(color);
        canvas.drawText(time, 18, -infoFont.ascent() + 17, timerFont);
    }

    public void drawArc(int i1, Float j1, Float k1, int l1) {
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(new RectF(j1, k1, j1 + l1, k1 + l1), -((i1 >> 16) + 170), -90, false, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    public void drawRect2(int l, Float j1, Float k1) {
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(new RectF(j1, k1, j1 + l, k1 + l), 0, 360, true, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    public void drawFlag(int j, int k, ViewState view, Sprite sprite) {
        drawBitmap(getSprite(sprite),
                offsetX(j, view.offsetX), offsetY(k, view.offsetY) - 32);
    }

    private void drawBitmapWithRotation(GdBitmap bitmap, Float fAngleDeg, Float x, Float y) {
        canvas.save();
        canvas.rotate(fAngleDeg, x + bitmap.getWidthDp(modManager.getSpriteDensity()) / 2, y + bitmap.getHeightDp(modManager.getSpriteDensity()) / 2);
        drawBitmap(bitmap, x, y);
        canvas.restore();
    }

    private void drawBitmap(GdBitmap b, Float x, Float y) {
        Paint paint = new Paint();
        paint.setFlags(Paint.DITHER_FLAG);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(b.bitmap,
                new Rect(0, 0, b.getWidth(), b.getHeight()),
                new RectF(x, y, x + b.getWidthDp(modManager.getSpriteDensity()), y + b.getHeightDp(modManager.getSpriteDensity())),
                paint);
    }

    private static Map<Sprite, GDBitmapHolder> getDefaultInterfaceSprites() {
        Map<Sprite, GDBitmapHolder> sprites = new HashMap<>();
        sprites.put(Sprite.EMPTY, new GDBitmapHolder(new GdBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))));
        sprites.put(Sprite.CODEBREW_LOGO, new GDBitmapHolder(fromDrawable(R.drawable.codebrew)));
        sprites.put(Sprite.GD_LOGO, new GDBitmapHolder(fromDrawable(R.drawable.gd)));
        sprites.put(Sprite.SAVEPOINT, new GDBitmapHolder(fromDrawable(R.drawable.ic_savepoint)));
        sprites.put(Sprite.ARROWS, new GDBitmapHolder(new GdBitmap[]{
                fromDrawable(R.drawable.s_arrow_up),
                fromDrawable(R.drawable.s_arrow_down),
                fromDrawable(R.drawable.s_arrow_left),
                fromDrawable(R.drawable.s_arrow_right)
        }));
        sprites.put(Sprite.LOCKS, new GDBitmapHolder(new GdBitmap[]{
                fromDrawable(R.drawable.s_lock0),
                fromDrawable(R.drawable.s_lock1),
                fromDrawable(R.drawable.s_lock2)
        }));
        sprites.put(Sprite.MEDALS, new GDBitmapHolder(new GdBitmap[]{
                fromDrawable(R.drawable.s_medal_gold),
                fromDrawable(R.drawable.s_medal_silver),
                fromDrawable(R.drawable.s_medal_bronze)
        }));
        sprites.put(Sprite.LEVELS_WHEELS, new GDBitmapHolder(new GdBitmap[]{
                fromDrawable(R.drawable.levels_wheel0),
                fromDrawable(R.drawable.levels_wheel1),
                fromDrawable(R.drawable.levels_wheel2)
        }));
        sprites.put(Sprite.HELMET, new GDBitmapHolder(fromDrawable(R.drawable.s_helmet)));
        return sprites;
    }

    public static GdBitmap getInterfaceSprite(Sprite sprite) {
        GDBitmapHolder holder = interfaceSprites.get(sprite);
        if (holder != null && !holder.isArray && holder.gdBitmap != null) {
            return holder.gdBitmap;
        }
        return interfaceSprites.get(Sprite.EMPTY).gdBitmap;
    }

    public static GdBitmap fromDrawable(int id) {
        return new GdBitmap(loadBitmapFromDrawable(id));
    }

    public GdBitmap getSprite(Sprite sprite) {
        return (GdBitmap) props.get(sprite.name());
    }

    @TargetApi(Build.VERSION_CODES.O)  //todo refactor to lower version
    static String toBase64(Bitmap bitmap) {
        try {
            //https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            stream.close();
            return new String(Base64.getEncoder().encode(byteArray));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.O) //todo refactor to lower version
    static GdBitmap fromBase64(String b64) {
        //https://base64.guru/converter/encode/file
        byte[] b = Base64.getDecoder().decode(b64);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return new GdBitmap(bitmap);
    }
}
