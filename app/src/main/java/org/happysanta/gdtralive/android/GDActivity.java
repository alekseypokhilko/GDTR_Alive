package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.game.api.GDFile.MRG;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.KeyboardController;
import org.happysanta.gdtralive.android.menu.Menu;
import org.happysanta.gdtralive.android.menu.MenuFactory;
import org.happysanta.gdtralive.android.menu.MenuScreen;
import org.happysanta.gdtralive.android.menu.views.MenuHelmetView;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.android.menu.views.MenuLinearLayout;
import org.happysanta.gdtralive.android.menu.views.MenuTitleLinearLayout;
import org.happysanta.gdtralive.android.menu.views.ObservableScrollView;
import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.api.external.GdApplication;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdUtils;
import org.happysanta.gdtralive.game.util.mrg.MrgUtils;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackReference;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.MenuType;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.HighScoreManager;
import org.happysanta.gdtralive.game.util.Utils;
import org.happysanta.gdtralive.game.SplashScreen;
import org.happysanta.gdtralive.game.api.Sprite;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GDActivity extends Activity implements GdApplication, Runnable {

    public static GDActivity shared = null;
    private GdSettings settings;

    private Game game;
    private GameView gameView;
    public Menu menu;
    private MenuFactory menuFactory;
    public ModManager modManager;
    public TrackEditorView trackEditor;
    private GdFileStorage fileStorage;
    private GdDataSource dataSource;
    private GdUtils utils;
    private HighScoreManager highScoreManager;

    public List<EditText> textInputs = new ArrayList<>();

    private boolean wasPaused = false;
    private boolean wasStarted = false;
    private boolean wasDestroyed = false;
    private boolean alive = false;
    private boolean pause = true;
    private boolean menuShown = false;
    public boolean fullResetting = false;
    public boolean exiting = false;

    private boolean inited = false;
    private Thread thread;
    private MenuImageView menuBtn;
    public MenuImageView actionButton;
    public MenuTitleLinearLayout titleLayout;
    public ObservableScrollView scrollView;
    public FrameLayout frame;
    public MenuLinearLayout menuLayout;
    private KeyboardController keyboardController;
    public TextView menuTitleTextView;
    private MenuLinearLayout keyboardLayout;
    private int buttonHeight = 60;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = this;

        this.utils = new AndroidGdUtils();
        this.settings = new AndroidGdSettings();
        this.fileStorage = new AndroidFileStorage(this);
        this.dataSource = new AndroidDataSource(this);
        this.highScoreManager = new HighScoreManager(this, dataSource);
        this.menuFactory = new MenuFactory(this);

        this.modManager = new ModManager(fileStorage);

        //todo
        modManager.getGameTheme().setProp("density", Helpers.getGDActivity().getResources().getDisplayMetrics().density);
        modManager.getGameTheme().setProp("spriteDensity", Helpers.getGDActivity().getResources().getDisplayMetrics().density);

        this.gameView = new GameView(this, modManager);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        scrollView = new ObservableScrollView(this);
        scrollView.setBackgroundColor(modManager.getInterfaceTheme().getMenuBackgroundColor());
        scrollView.setFillViewport(true);
        scrollView.setOnScrollListener((scrollView, x, y, oldx, oldy) -> {
            if (isMenuShown() && menu != null && menu.currentMenu != null) {
                int h = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                double p = 100.0 * y / h;
                if (p > 100f)
                    p = 100f;

                menu.currentMenu.onScroll(p);
            }
        });
        scrollView.setVisibility(View.GONE);

        frame = new FrameLayout(this);
        frame.setBackgroundColor(modManager.getInterfaceTheme().getFrameBackgroundColor());

        titleLayout = new MenuTitleLinearLayout(this);
        titleLayout.setBackgroundColor(modManager.getInterfaceTheme().getMenuTitleBackgroundColor());
        titleLayout.setGravity(Gravity.TOP);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        titleLayout.setPadding(Helpers.getDp(Theme.MENU_TITLE_LAYOUT_X_PADDING),
                Helpers.getDp(Theme.MENU_TITLE_LAYOUT_TOP_PADDING),
                Helpers.getDp(Theme.MENU_TITLE_LAYOUT_X_PADDING),
                Helpers.getDp(Theme.MENU_TITLE_LAYOUT_BOTTOM_PADDING));

        menuTitleTextView = new TextView(this);
        menuTitleTextView.setText(getString(R.string.main));
        menuTitleTextView.setTextColor(modManager.getInterfaceTheme().getMenuTitleTextColor());
        menuTitleTextView.setTypeface(Global.robotoCondensedTypeface);
        menuTitleTextView.setTextSize(Theme.MENU_TITLE_FONT_SIZE);
        menuTitleTextView.setLineSpacing(0f, 1.1f);
        menuTitleTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        menuTitleTextView.setVisibility(View.GONE);

        titleLayout.addView(menuTitleTextView);

        scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        buttonHeight = getButtonHeight();

        keyboardLayout = new MenuLinearLayout(this, true);
        keyboardLayout.setOrientation(LinearLayout.VERTICAL);

        keyboardController = new KeyboardController(this);

        for (int i = 0; i < 3; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setPadding(Helpers.getDp(KeyboardController.PADDING), i == 0 ? Helpers.getDp(KeyboardController.PADDING) : 0, Helpers.getDp(KeyboardController.PADDING), 0);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setBackgroundColor(modManager.getInterfaceTheme().getKeyboardBackgroundColor());
            ThemeObjectsReference.keyboardBackgroundRef[i] = row;
            for (int j = 0; j < 3; j++) {
                LinearLayout btn = new LinearLayout(this);
                TextView btnText = new TextView(this);
                btnText.setText(String.valueOf(i * 3 + j + 1));
                btnText.setTextColor(modManager.getInterfaceTheme().getKeyboardTextColor());
                btnText.setTextSize(17);
                ThemeObjectsReference.keyboardButtonsRef[(i * 3 + j + 1) - 1] = btnText;
                btn.setBackgroundResource(getResources().getIdentifier(Constants.BUTTON_RESOURCES[i * 3 + j], "drawable", getPackageName()));
                btn.addView(btnText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btn.setGravity(Gravity.CENTER);
                btn.setWeightSum(1);

                row.addView(btn, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Helpers.getDp(buttonHeight), 1));

                keyboardController.addButton(btn, j, i);
            }

            keyboardLayout.addView(row);
        }

        keyboardLayout.setGravity(Gravity.BOTTOM);
        keyboardLayout.setPadding(0, 0, 0, Helpers.getDp(KeyboardController.PADDING));
        keyboardLayout.setOnTouchListener(keyboardController);
        keyboardLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

        hideKeyboardLayout();

        menuBtn = new MenuImageView(this);
        menuBtn.setImageResource(R.drawable.ic_menu);
        menuBtn.setScaleType(ImageView.ScaleType.CENTER);
        menuBtn.setLayoutParams(new FrameLayout.LayoutParams(Helpers.getDp(Theme.GAME_MENU_BUTTON_LAYOUT_WIDTH), Helpers.getDp(Theme.GAME_MENU_BUTTON_LAYOUT_HEIGHT), Gravity.RIGHT | Gravity.TOP));
        menuBtn.setOnClickListener(v -> showMenu());
        menuBtn.setVisibility(View.GONE);

        actionButton = new MenuImageView(this);
        actionButton.setImageBitmap(AndroidCanvas.getInterfaceSprite(Sprite.SAVEPOINT).bitmap);
        actionButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Helpers.getDp((int) (buttonHeight / 1.5)), Gravity.CENTER | Gravity.BOTTOM);
        params.setMargins(Helpers.getDp(buttonHeight * 3), Helpers.getDp(buttonHeight * 9), 0, 0);
        actionButton.setLayoutParams(params);
        actionButton.setOnClickListener(v -> game.handleSetSavepointAction());
        actionButton.setVisibility(View.GONE);

        menuLayout = new MenuLinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        menuLayout.addView(titleLayout);
        menuLayout.addView(scrollView);

        trackEditor = new TrackEditorView(this);
        for (MenuLinearLayout view : trackEditor.getViews()) {
            frame.addView(view);
        }

        frame.addView(menuLayout);
        frame.addView(keyboardLayout);
        frame.addView(menuBtn);
        frame.addView(actionButton);
        frame.setOnTouchListener((v, event) -> {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                for (EditText textInput : textInputs) {
                    textInput.clearFocus();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });

        gameView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, 1));
        frame.addView(gameView, 0);

        setContentView(frame);

        modManager.registerThemeReloadHandler(() -> {
            scrollView.setBackgroundColor(modManager.getInterfaceTheme().getMainMenuBackgroundColor());
            frame.setBackgroundColor(modManager.getInterfaceTheme().getFrameBackgroundColor());
            titleLayout.setBackgroundColor(modManager.getInterfaceTheme().getMenuTitleBackgroundColor());
            menuTitleTextView.setTextColor(modManager.getInterfaceTheme().getMenuTitleTextColor());
            actionButton.setImageBitmap(AndroidCanvas.getInterfaceSprite(Sprite.SAVEPOINT).bitmap);
            if (ThemeObjectsReference.keyboardButtonsColor != modManager.getInterfaceTheme().getKeyboardTextColor()) {
                ThemeObjectsReference.keyboardButtonsColor = modManager.getInterfaceTheme().getKeyboardTextColor();
                for (int i = 0; i < ThemeObjectsReference.keyboardButtonsRef.length; i++) {
                    ThemeObjectsReference.keyboardButtonsRef[i].setTextColor(modManager.getInterfaceTheme().getKeyboardTextColor());
                }
            }
            if (ThemeObjectsReference.keyboardBackgroundColor != modManager.getInterfaceTheme().getKeyboardBackgroundColor()) {
                ThemeObjectsReference.keyboardBackgroundColor = modManager.getInterfaceTheme().getKeyboardBackgroundColor();
                for (int i = 0; i < ThemeObjectsReference.keyboardBackgroundRef.length; i++) {
                    ThemeObjectsReference.keyboardBackgroundRef[i].setBackgroundColor(modManager.getInterfaceTheme().getKeyboardBackgroundColor());
                }
            }
        });
        thread = null;
        inited = false;
        wasDestroyed = false;

//        try {
//
//            https://stackoverflow.com/questions/5747060/how-do-you-play-android-inputstream-on-mediaplayer
//             mp =  MediaPlayer.create(this, R.raw.menu);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////                mp.setDataSource(this, R.raw.menu);
////                mp.prepare();
//                mp.start();
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        frame.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                frame.getViewTreeObserver().removeOnPreDrawListener(this);
                doStart();
                return true;
            }
        });
    }
//    MediaPlayer mp;


    protected void doStart() {
        alive = true;
        pause = false;

        Thread.currentThread().setName("main_thread");

        if (thread == null) {
            thread = new Thread(this);
            thread.setName("game_thread");
            thread.start();
        }

        wasStarted = true;
    }

    @Override
    public void run() {
        Helpers.logDebug("!!! run()");

        if (!inited) {
            Helpers.logDebug("run(): initing");
            try {
                MenuHelmetView.clearStaticFields();

                int width = gameView.getGdWidth();
                int height = gameView.getGdHeight();
                game = new Game(this, width, height);


                gameView.setGdView(game.getView());
                keyboardController.setKeyboardHandler(game.getKeyboardHandler());

                menu = new Menu(this, menuFactory);
                game.init(menu);
                trackEditor.init(game, settings);
                menuFactory.init(menu);

                new SplashScreen().showSplashScreens(game.getView());
                inited = true;
            } catch (Exception _ex) {
                _ex.printStackTrace();
                throw new RuntimeException(_ex);
            }
        }

        Helpers.logDebug("start main loop");
        game.gameLoop();
        destroyApp(false);
    }

    @Override
    protected void onResume() {
        Helpers.logDebug("@@@ [GDActivity \"+hashCode()+\"] onResume()");
        super.onResume();
        Helpers.logDebug("[GDActivity \"+hashCode()+\"] onResume(), inited = " + inited);
        if (wasPaused && wasStarted) {
            pause = false;
            wasPaused = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Helpers.logDebug("@@@ [GDActivity " + hashCode() + "] onPause()");

        wasPaused = true;
        pause = true;
        Helpers.logDebug("inited : " + inited);
        if (!menuShown && inited)
            gameToMenu();

        try {
            game.levelsManager.updateLevelSettings();
        } catch (Exception e) {
            e.printStackTrace(); //todo
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //https://stackoverflow.com/questions/73568525/why-starting-the-activity-by-an-intent-intent-action-view-doesnt-always-start
        //https://stackoverflow.com/questions/11068648/launching-an-intent-for-file-and-mime-type
        //https://developer.android.com/training/sharing/receive
        //https://stackoverflow.com/questions/2975197/convert-file-uri-to-file-in-android
        String action = intent.getAction();
        String type = intent.getType();

        if (!Intent.ACTION_VIEW.equals(action) || type == null) {
            return;
        }
        //samsung content://media/external/file/1000094414
        //pixel content://com.android.externalstorage.documents/document/primary%3ADownload%2FGDAlive%2Fmods%2F4a27246d-a29d-4b31-be32-4a0a3e4c4db9_gravity%20%D1%84%D1%83%D1%82%D0%B1%D0%BE%D0%BB%D0%B8%D1%81%D1%82%D1%8B%20(%D0%BE%D0%B1%D0%BD%D0%BE%D0%B2%D0%BB%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5)%20%5B5.5.5%5D.gdmod
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri == null) {
            String dataString = intent.getDataString();
            if (dataString != null && !"".equals(dataString)) {
                uri = Uri.parse(dataString);
            } else {
                return;
            }
        }

        try {
            File file = new File(Objects.requireNonNull(uri.getPath()));
            String absolutePath = file.getAbsolutePath();
            String fileType = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
            GDFile typeFromExtension = GDFile.getTypeFromExtension(fileType);
            try (InputStream inputStream = Helpers.getGDActivity().getContentResolver().openInputStream(uri)) {
                switch (GDFile.UNDEFINED != typeFromExtension ? typeFromExtension : getType(uri)) { //todo fixme
                    case RECORD:
                        TrackRecord trackRecord = Utils.read(inputStream);
                        MenuScreen recordMenu = menuFactory.get(MenuType.RECORDING_OPTIONS).build(new MenuData(trackRecord));
                        menu.setCurrentMenu(recordMenu);
                        break;
                    case TRACK:
                        TrackReference track = Utils.read(inputStream);
                        //todo track screen
                        modManager.setTrackProperties(track);
                        game.startTrack(GameParams.of(GameMode.SINGLE_TRACK, track.getData()));
                        break;
                    case MRG:
                        final File file1 = new File(Objects.requireNonNull(uri.getPath()));
                        Mod mrg = MrgUtils.convertMrg(file1.getName(), Utils.readAllBytes(inputStream));
                        menu.setCurrentMenu(menuFactory.get(MenuType.MOD_OPTIONS).build(new MenuData(mrg)));
                        break;
                    case MOD:
                        Mod mod = Utils.read(inputStream);
                        modManager.setMod(mod);
                        MenuScreen packMenu = menuFactory.get(MenuType.MOD_OPTIONS).build(new MenuData(mod));
                        menu.setCurrentMenu(packMenu);
                        break;
                    case THEME:
                        Theme theme = Utils.read(inputStream);
//                        menu.mods.getThemes().themes.add(theme);//todo
                        MenuScreen themeMenu = menuFactory.get(MenuType.THEME_OPTIONS).build(new MenuData(theme));
                        menu.setCurrentMenu(themeMenu);
                        break;
                    case UNDEFINED:
                    default: //noop just launch the game \-_o/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //todo request permission
            notify("File loading error:" + e.getMessage());
        }
    }

    public GDFile getType(Uri uri) {
        //todo fixme
        String content = null;
        try (InputStream inputStream = Helpers.getGDActivity().getContentResolver().openInputStream(uri)) {
            content = Utils.readContent(inputStream);
        } catch (Exception e) {
            return MRG;
        }
        try {
            String header = content.substring(0, content.indexOf(":"));
            for (GDFile value : GDFile.values()) {
                if (value.extension.equals(header)) {
                    return value;
                }
            }
            return MRG;
        } catch (Exception e) {
            return MRG;
        }
    }

    @Override
    public void onBackPressed() {
        if (gameView != null && menu != null && inited) {
            if (menuShown)
                menu.back();
            else
                showMenu();
        }
    }

    public void showMenu() {
        if (menu != null) {
            menu.m_blZ = true;
            gameToMenu();
        }
    }

    @Override
    public boolean isMenuShown() {
        return menuShown;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public boolean isOnPause() {
        return pause;
    }

    public void notify(String message) {
        //todo move to application
        Toast.makeText(Helpers.getGDActivity(), message, Toast.LENGTH_LONG).show();
    }

    // @UiThread
    public void setMenu(final LinearLayout layout) {
        runOnUiThread(() -> {
            scrollView.removeAllViews();
            if (layout.getParent() != null) {
                ((ViewManager) layout.getParent()).removeView(layout);
            }
            scrollView.addView(layout);
        });
    }

    public void destroyApp(final boolean restart) {
        if (wasDestroyed) {
            return;
        }

        wasDestroyed = true;
        alive = false;

        final GDActivity self = this;

        runOnUiThread(() -> {
            Helpers.logDebug("[GDActivity " + self.hashCode() + "] destroyApp()");
            inited = false;

            synchronized (gameView) {
                destroyResources();

                if (exiting || restart) {
                    finish();
                }

                if (restart) {
                    doRestartApp();
                }
            }
        });
    }

    private void destroyResources() {
        Helpers.logDebug("[GDActivity " + hashCode() + "]  destroyResources()");

        if (gameView != null) gameView.gdView.destroy();

        menuShown = false;
        if (menu != null) {
            menu.destroy();
        }

        if (dataSource != null) {
            dataSource.close();
        }
    }

    public int getButtonsLayoutHeight() {
        return buttonHeight * 3 + KeyboardController.PADDING * 2;
    }

    @Override
    public GdFileStorage getFileStorage() {
        return fileStorage;
    }

    @Override
    public GdDataSource getDataSource() {
        return dataSource;
    }

    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    @Override
    public GdUtils getUtils() {
        return utils;
    }

    @Override
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public GdMenu getMenu() {
        return menu;
    }

    @Override
    public GdSettings getSettings() {
        return settings;
    }

    // @UiThread
    public void hideKeyboardLayout() {
        runOnUiThread(() -> {
            keyboardLayout.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            scrollView.setLayoutParams(params);
        });
    }

    // @UiThread
    public void showKeyboardLayout() {
        runOnUiThread(() -> {
            keyboardLayout.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
            params.setMargins(0, 0, 0, Helpers.getDp(getButtonsLayoutHeight()));
            scrollView.setLayoutParams(params);
        });
    }

    @Override
    public void gameToMenu() {
        Helpers.logDebug("gameToMenu()");

        if (gameView == null) {
            Helpers.logDebug("gameToMenu(): gameView == null");
            return;
        }

        game.pause();

        menuShown = true;

        if (!settings.isKeyboardInMenuEnabled())
            hideKeyboardLayout();
        else
            showKeyboardLayout();

        gameToMenuUpdateUi();
    }

    // @UiThread
    protected void gameToMenuUpdateUi() {
        runOnUiThread(() -> {
            menuBtn.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
            menuTitleTextView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void menuToGame() {
        Helpers.logDebug("menuToGame()");

        game.resume();

        menuShown = false;
        showKeyboardLayout();

        menuToGameUpdateUi();

        keyboardController.clearLogBuffer();
    }

    @Override
    public void exit() {
        exiting = true;
        alive = false;
        if (menu.currentMenu != null) {
            menu.menuBack();
        } else {
            menu.setCurrentMenu(null);
        }
    }

    public void exitEditMode() {
        trackEditor.hideLayout();
    }

    public void editMode() {
        menuToGame();
        hideKeyboardLayout();
    }

    // @UiThread
    protected void menuToGameUpdateUi() {
        runOnUiThread(() -> {
            menuBtn.setVisibility(View.VISIBLE);
            //actionButton.setVisibility(android.view.View.VISIBLE);
            menuTitleTextView.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);

            // Clear menu
            scrollView.removeAllViews();
            menuTitleTextView.setText("");
            menu.menuDisabled = true;
            // menu.currentMenu = null;
        });
    }

    public void scrollTextMenuUp() {
        runOnUiThread(() -> {
            int y = scrollView.getScrollY();
            scrollView.scrollTo(0, y - Helpers.getDp(20));
        });
    }

    public void scrollTextMenuDown() {
        runOnUiThread(() -> {
            int y = scrollView.getScrollY();
            scrollView.scrollTo(0, y + Helpers.getDp(20));
        });
    }

    public void scrollToView(final View view) {
        final GDActivity gd = Helpers.getGDActivity();
        final ObservableScrollView scrollView = gd.scrollView;

        runOnUiThread(() -> {
            Rect scrollBounds = new Rect();
            scrollView.getHitRect(scrollBounds);

            if (!view.getLocalVisibleRect(scrollBounds)
                    || scrollBounds.height() < view.getHeight()) {
                int top = view.getTop(),
                        height = view.getHeight(),
                        scrollY = scrollView.getScrollY(),
                        scrollHeight = scrollView.getHeight(),
                        y = top;

                if (top < scrollY) {
                    // scroll to y
                } else if (top + height > scrollY + scrollHeight) {
                    y = top + height - scrollHeight;
                    if (y < 0)
                        y = 0;
                }

                // logDebug("View is not visible, scroll to y = " + y);
                scrollView.scrollTo(0, y);
            }
        });
    }

    private void doRestartApp() {
        Intent mStartActivity = new Intent(this, GDActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
    }

    private int getButtonHeight() {
        if (getString(R.string.screen_type).equals("tablet")) {
            return 85;
        } else if (Helpers.getModManager().getGameTheme().getDensity() < 1.5) {
            return 55;
        }
        return 60;
    }
}