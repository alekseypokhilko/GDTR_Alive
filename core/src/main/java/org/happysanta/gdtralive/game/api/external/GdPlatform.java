package org.happysanta.gdtralive.game.api.external;

public interface GdPlatform {

    void init();
    GdMenu getMenu(); //todo remove from here
    float getDensity();
    void notify(String message);
    void runOnUiThread(Runnable action);

    void finish();

    void doRestartApp();

    void hideKeyboardLayout();

    void showKeyboardLayout();

    void gameToMenuUpdateUi();

    void menuToGameUpdateUi();

    void trainingMode();

}
