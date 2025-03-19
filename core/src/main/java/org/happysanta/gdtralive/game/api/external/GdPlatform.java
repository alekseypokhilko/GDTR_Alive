package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.Platform;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;

public interface GdPlatform {
    Platform getPlatform();
    PlatformMenuElementFactory getPlatformMenuElementFactory();
    void init();
    void setMenu(MenuScreen menu);
    GdMenu getMenu(); //todo remove from here
    float getDensity();
    void notify(String message);
    void showAlert(String title, String message, final Runnable listener);
    void showConfirm(String title, String message, final Runnable onOk, final Runnable onCancel);
    void runOnUiThread(Runnable action);

    void finish();

    void doRestartApp();

    void pickFile(int requestCode);

    void hideKeyboardLayout();

    void showKeyboardLayout();

    void gameToMenuUpdateUi();

    void menuToGameUpdateUi();

    void trainingMode();

    void exitEditMode();

    String getAppVersion();
    void share(GDFile gdFile, String name);

    void changeLocale(String code);
}
