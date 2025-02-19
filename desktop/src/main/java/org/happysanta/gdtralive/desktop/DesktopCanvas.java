package org.happysanta.gdtralive.desktop;

import static org.happysanta.gdtralive.DesktopGdView.ZOOM;

import org.happysanta.gdtralive.game.external.GdCanvas;
import org.happysanta.gdtralive.game.mod.Color;
import org.happysanta.gdtralive.game.recorder.EngineStateRecord;
import org.happysanta.gdtralive.game.visual.Sprite;
import org.happysanta.gdtralive.game.visual.ViewState;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class DesktopCanvas implements GdCanvas {

    private Graphics2D canvas;
    private Map<Sprite, Image> sprites = new HashMap<>();
    private Map<Sprite, BufferedImage> spritesBuffered = new HashMap<>();
    private Map<Sprite, Integer> spritesSizes = new HashMap<>();
    private final Font font;

    public DesktopCanvas() {
        font = new Font(null, Font.PLAIN, 20);
        String base = "sprites/";
        sprites.put(Sprite.ENGINE, new ImageIcon(readBytes(base + "s_engine.png")).getImage());
        sprites.put(Sprite.FENDER, new ImageIcon(readBytes(base + "s_fender.png")).getImage());
        sprites.put(Sprite.HELMET, new ImageIcon(readBytes(base + "s_helmet.png")).getImage());
        sprites.put(Sprite.LEG, new ImageIcon(readBytes(base + "s_blueleg.png")).getImage());
        sprites.put(Sprite.ARM, new ImageIcon(readBytes(base + "s_bluearm.png")).getImage());
        sprites.put(Sprite.BODY, new ImageIcon(readBytes(base + "s_bluebody.png")).getImage());
        sprites.put(Sprite.WHEEL_SMALL, new ImageIcon(readBytes(base + "s_wheel1.png")).getImage());
        sprites.put(Sprite.WHEEL_BIG, new ImageIcon(readBytes(base + "s_wheel2.png")).getImage());
        sprites.put(Sprite.STEERING, new ImageIcon(readBytes(base + "s_steering.png")).getImage());
        sprites.put(Sprite.START_FLAG_0, new ImageIcon(readBytes(base + "s_flag_start0.png")).getImage());
        sprites.put(Sprite.START_FLAG_1, new ImageIcon(readBytes(base + "s_flag_start1.png")).getImage());
        sprites.put(Sprite.START_FLAG_2, new ImageIcon(readBytes(base + "s_flag_start2.png")).getImage());
        sprites.put(Sprite.FINISH_FLAG_0, new ImageIcon(readBytes(base + "s_flag_finish0.png")).getImage());
        sprites.put(Sprite.FINISH_FLAG_1, new ImageIcon(readBytes(base + "s_flag_finish1.png")).getImage());
        sprites.put(Sprite.FINISH_FLAG_2, new ImageIcon(readBytes(base + "s_flag_finish2.png")).getImage());

        spritesBuffered.put(Sprite.ENGINE, convertToBufferedImage(sprites.get(Sprite.ENGINE)));
        spritesBuffered.put(Sprite.FENDER, convertToBufferedImage(sprites.get(Sprite.FENDER)));
        spritesBuffered.put(Sprite.HELMET, convertToBufferedImage(sprites.get(Sprite.HELMET)));
        spritesBuffered.put(Sprite.LEG, convertToBufferedImage(sprites.get(Sprite.LEG)));
        spritesBuffered.put(Sprite.ARM, convertToBufferedImage(sprites.get(Sprite.ARM)));
        spritesBuffered.put(Sprite.BODY, convertToBufferedImage(sprites.get(Sprite.BODY)));
        spritesBuffered.put(Sprite.WHEEL_SMALL, convertToBufferedImage(sprites.get(Sprite.WHEEL_SMALL)));
        spritesBuffered.put(Sprite.WHEEL_BIG, convertToBufferedImage(sprites.get(Sprite.WHEEL_BIG)));
        spritesBuffered.put(Sprite.STEERING, convertToBufferedImage(sprites.get(Sprite.STEERING)));
        spritesBuffered.put(Sprite.START_FLAG_0, convertToBufferedImage(sprites.get(Sprite.START_FLAG_0)));
        spritesBuffered.put(Sprite.START_FLAG_1, convertToBufferedImage(sprites.get(Sprite.START_FLAG_1)));
        spritesBuffered.put(Sprite.START_FLAG_2, convertToBufferedImage(sprites.get(Sprite.START_FLAG_2)));
        spritesBuffered.put(Sprite.FINISH_FLAG_0, convertToBufferedImage(sprites.get(Sprite.FINISH_FLAG_0)));
        spritesBuffered.put(Sprite.FINISH_FLAG_1, convertToBufferedImage(sprites.get(Sprite.FINISH_FLAG_1)));
        spritesBuffered.put(Sprite.FINISH_FLAG_2, convertToBufferedImage(sprites.get(Sprite.FINISH_FLAG_2)));

        spritesSizes.put(Sprite.ENGINE, 20);
        spritesSizes.put(Sprite.FENDER, 22);
        spritesSizes.put(Sprite.WHEEL_SMALL, 16);
        spritesSizes.put(Sprite.WHEEL_BIG, 16);
        spritesSizes.put(Sprite.STEERING, 4);
        spritesSizes.put(Sprite.HELMET, 6);
        spritesSizes.put(Sprite.LEG, 10);
        spritesSizes.put(Sprite.ARM, 10);
        spritesSizes.put(Sprite.BODY, 10);
        spritesSizes.put(Sprite.START_FLAG_0, 10);
        spritesSizes.put(Sprite.START_FLAG_1, 10);
        spritesSizes.put(Sprite.START_FLAG_2, 10);
        spritesSizes.put(Sprite.FINISH_FLAG_0, 10);
        spritesSizes.put(Sprite.FINISH_FLAG_1, 10);
        spritesSizes.put(Sprite.FINISH_FLAG_2, 10);
    }

    private static byte[] readBytes(String path) {
        DesktopFileStorage storage = new DesktopFileStorage();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream(path)) {
            return storage.readAllBytes(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCanvas(Graphics2D canvas) {
        this.canvas = canvas;
    }

    public void setColor(Color color) {
        setColor(color.r, color.g, color.b);
    }

    public void setColor(int r, int g, int b) {
        canvas.setColor(new java.awt.Color(r, g, b));
    }

    public void drawRect(int left, int top, int right, int bottom, ViewState view) {
        int x = (int) (view.width / 2 - view.width / ZOOM) ;
        int y = (int) (view.height / 2 - view.height / ZOOM) ;
        int xx = (int) (view.width / 2 - right / ZOOM) ;
        int yy = (int) (view.height / 2 - view.height / ZOOM) + bottom;
        canvas.fillRect(left, top, right, bottom);
    }

    public void drawSpriteWithRotation(Float j, Float k, ViewState view, EngineStateRecord state, Float fAngleDeg, Sprite sprite) {
        int size = spritesSizes.get(sprite);
        float x = offsetX(j, view.offsetX) - size / 2;
        float y = offsetY(k, view.offsetY) - size / 2;
        drawBitmapWithRotation(fAngleDeg, x, y, sprite);
    }

    public void drawSprite2(Float j, Float k, ViewState view, EngineStateRecord state, Sprite sprite) {
        int size = spritesSizes.get(sprite);

        float x = offsetX(j - size / 2, view.offsetX);
        float y = offsetY(k + size / 2, view.offsetY);
        drawBitmap((int) x, (int) y, sprite);
    }

    public void drawBodySprite(Sprite sprite, Float l1, Float i2, Float fAngleDeg, int league) {
        int size = spritesSizes.get(sprite);
        float x = l1 - size / 2;
        float y = i2 - size / 2;
        drawBitmapWithRotation(fAngleDeg, x, y, sprite);
    }

    public void drawLogoSprite(Sprite sprite, ViewState view) {
//        GdBitmap bitmap = mm().getInterfaceSprite(sprite);
//        if (bitmap != null) {
//            drawBitmap(bitmap, view.width / 2 - bitmap.getWidthDp() / 2,
//                    (float) (view.height / 2 - bitmap.getHeightDp() / 1.6));
//        }
    }

    public void drawLine(int j, int k, int l, int i1, ViewState view) {
        canvas.drawLine(
                (int) offsetX((j << 2) / (float) 0xFFFF, view.offsetX),
                (int) offsetY((k << 2) / (float) 0xFFFF, view.offsetY),
                (int) offsetX((l << 2) / (float) 0xFFFF, view.offsetX),
                (int) offsetY((i1 << 2) / (float) 0xFFFF, view.offsetY));
    }

    public void drawLine2(int j, int k, int l, int i1, ViewState view) {
        canvas.drawLine(
                (int) offsetX(j, view.offsetX),
                (int) offsetY(k, view.offsetY),
                (int) offsetX(l, view.offsetX),
                (int) offsetY(i1, view.offsetY));
    }

    public float offsetX(float j, int offsetX) {
        return j + offsetX;
    }

    public float offsetY(float j, int offsetY) {
        return -j + offsetY;
    }

    public void drawInfoMessage2(String message, Color color, ViewState view) {
        FontMetrics metrics = canvas.getFontMetrics(font);
        canvas.setFont(font);
        canvas.setColor(java.awt.Color.BLACK);
        canvas.drawString(message, (view.width / 2) - (metrics.stringWidth(message) / 2), view.height / 2 - 100);
    }

    public void drawTimer2(int color, String time, ViewState view) {
        Font font = new Font(null, Font.PLAIN, 20);
        canvas.setFont(font);
        canvas.setColor(java.awt.Color.BLACK);
        int x = (int) (view.width / 2 - view.width / ZOOM);
        int y = (int) (view.height / 2 - view.height / ZOOM + 25);
        canvas.drawString(time, x, y); //todo calculate
    }

    public void drawArc(int i1, Float j1, Float k1, int l1) {
        canvas.drawOval(j1.intValue(), k1.intValue(), l1, l1);
    }

    public void drawRect2(int l, Float j1, Float k1) {
        canvas.drawOval(j1.intValue(), k1.intValue(), l, l);
    }

    public void drawFlag(int j, int k, ViewState view, Sprite sprite) {
        drawBitmap((int) offsetX(j, view.offsetX), (int) offsetY(k, view.offsetY) - 32, sprite);
    }

    private void drawBitmapWithRotation(Float fAngleDeg, Float x, Float y, Sprite sprite) {
        int size = spritesSizes.get(sprite);
        AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(fAngleDeg), size / 2, size / 2);
        tx.scale(0.35, 0.35);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        canvas.drawImage(op.filter(spritesBuffered.get(sprite), null), x.intValue(), y.intValue(), null);
    }

    private void drawBitmap(int x, int y, Sprite sprite) {
        int size = spritesSizes.get(sprite);
        canvas.drawImage(spritesBuffered.get(sprite), x, y, size, size, null);
    }

    public static BufferedImage convertToBufferedImage(Image image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
