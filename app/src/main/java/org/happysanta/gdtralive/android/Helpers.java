package org.happysanta.gdtralive.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import org.happysanta.gdtralive.game.ModManager;

public class Helpers {

    public static GDActivity getGDActivity() {
        return GDActivity.shared;
    }

    public static ModManager getModManager() {
        return GDActivity.shared.getGdApplication().getModManager();
    }

    public static int getDp(int px) {
        return Math.round(px * Helpers.getModManager().getInterfaceDensity());
    }

    public static int getDp(float px) {
        return Math.round(px * Helpers.getModManager().getInterfaceDensity());
    }

    public static Bitmap loadBitmapFromDrawable(int id) {
        BitmapFactory.Options options = null;
        if (!isSDK11OrHigher()) {
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }
        return BitmapFactory.decodeResource(getGDActivity().getResources(), id);
    }

    public static void logDebug(String s) {
        Log.d("AGDTR<" + Thread.currentThread().getName() + ">", s);
    }

    public static void logDebug(Object s) {
        Log.d("AGDTR<" + Thread.currentThread().getName() + ">", s.toString());
    }

    public static String s(int r) { //todo to application?
        return GDActivity.shared.getString(r);
    }

    public static void runOnUiThread(Runnable runnable) {
        GDActivity.shared.runOnUiThread(runnable);
    }

    public static boolean isSDK11OrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

}
