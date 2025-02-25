package org.happysanta.gdtralive.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.game.ModManager;

public class Helpers {

    public static GDActivity getGDActivity() {
        return GDActivity.shared;
    }

    public static ModManager getModManager() {
        return GDActivity.shared.modManager;
    }

    public static int getDp(int px) {
        return Math.round(px * Helpers.getModManager().getGameTheme().getDensity());
    }

    public static int getDp(float px) {
        return Math.round(px * Helpers.getModManager().getGameTheme().getDensity());
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

    public static Spanned boldAttr(int attrId, String data) {
        String str = data == null ? "" : data;
        return Html.fromHtml(bold(attrId) + str);
    }

    public static String bold(int strId) {
        return String.format("<b>%s</b>: ", s(strId));
    }

    public static String[] getStringArray(int r) {
        return GDActivity.shared.getResources().getStringArray(r);
    }

    public static void runOnUiThread(Runnable runnable) {
        GDActivity.shared.runOnUiThread(runnable);
    }

    public static void showAlert(String title, String message, final Runnable listener) {
        Context context = getGDActivity();
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(s(R.string.ok), (dialog, which) -> {
                    if (listener != null) listener.run();
                })
                .setOnCancelListener(dialog -> {
                    if (listener != null) listener.run();
                })
                .create();
        alertDialog.show();
    }

    public static void showConfirm(String title, String message, final Runnable onOk, final Runnable onCancel) {
        Context context = getGDActivity();
        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(s(R.string.ok), (dialog, which) -> {
                    if (onOk != null) onOk.run();
                })
                .setNegativeButton(s(R.string.cancel), (dialog, which) -> {
                    if (onCancel != null) onCancel.run();
                })
                .setOnCancelListener(dialog -> {
                    if (onCancel != null) onCancel.run();
                });
        alert.show();
    }

    public static boolean isSDK11OrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static String getAppVersion() {
        String v = "0.0";
        try {
            PackageInfo pInfo = GDActivity.shared.getPackageManager().getPackageInfo(GDActivity.shared.getPackageName(), 0);
            v = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return v;
    }

}
