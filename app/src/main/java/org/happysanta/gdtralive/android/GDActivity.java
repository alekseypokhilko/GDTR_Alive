package org.happysanta.gdtralive.android;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
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
import org.happysanta.gdtralive.game.api.menu.Menu;
import org.happysanta.gdtralive.android.menu.KeyboardController;
import org.happysanta.gdtralive.game.api.menu.MenuFactory;
import org.happysanta.gdtralive.android.menu.APlatformMenuElementFactory;
import org.happysanta.gdtralive.android.menu.views.MenuHelmetView;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.android.menu.views.MenuLinearLayout;
import org.happysanta.gdtralive.android.menu.views.MenuTitleLinearLayout;
import org.happysanta.gdtralive.android.menu.views.ObservableScrollView;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.MenuType;
import org.happysanta.gdtralive.game.api.Sprite;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.util.Utils;
import org.happysanta.gdtralive.game.util.mrg.MrgUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GDActivity extends Activity implements GdPlatform {

    public static GDActivity shared = null;
    private Application application;
    private APlatformMenuElementFactory<View> viewPlatformMenuElementFactory;

    private GdMenu<View> menu;
    private MenuFactory<View> menuFactory;
    private TrackEditorView trackEditor;

    public List<EditText> textInputs = new ArrayList<>();
    private MenuImageView menuBtn;
    public MenuImageView actionButton;
    public MenuTitleLinearLayout titleLayout;
    public ObservableScrollView scrollView;
    public FrameLayout frame;
    public MenuLinearLayout menuLayout;
    public KeyboardController keyboardController;
    public TextView menuTitleTextView;
    private MenuLinearLayout keyboardLayout;
    private int buttonHeight = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = this;

        GdStr str = new AStr();
        File appFolder = getExternalFilesDir(Constants.APP_DIRECTORY);
        Map<GDFile, File> folders = new HashMap<>();
        folders.put(GDFile.MOD, getExternalFilesDir(GDFile.MOD.appFolder));
        folders.put(GDFile.THEME, getExternalFilesDir(GDFile.THEME.appFolder));
        folders.put(GDFile.TRACK, getExternalFilesDir(GDFile.TRACK.appFolder));
        folders.put(GDFile.RECORD, getExternalFilesDir(GDFile.RECORD.appFolder));
        GdFileStorage fileStorage = new AFileStorage(appFolder, folders);
        GdDataSource dataSource = new ADataSource(this);
        AGameView gameView = new AGameView(this);

        this.application = new Application(this, new ASettingsStorage(), str, fileStorage, dataSource, gameView);
        viewPlatformMenuElementFactory = new APlatformMenuElementFactory<>(application, this);
        this.menuFactory = new MenuFactory<>(application, this, viewPlatformMenuElementFactory);

        ModManager modManager = application.getModManager();
        gameView.setModManager(modManager);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        scrollView = new ObservableScrollView(this);
        scrollView.setBackgroundColor(modManager.getInterfaceTheme().getMenuBackgroundColor());
        scrollView.setFillViewport(true);
        scrollView.setOnScrollListener((scrollView, x, y, oldx, oldy) -> {
            if (application.isMenuShown() && menu != null && !menu.isCurrentMenuEmpty()) {
                int h = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                double p = 100.0 * y / h;
                if (p > 100f)
                    p = 100f;

                menu.onCurrentMenuScroll(p);
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

        keyboardController = new KeyboardController(application);

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
        menuBtn.setOnClickListener(v -> application.showMenu());
        menuBtn.setVisibility(View.GONE);

        actionButton = new MenuImageView(this);
        actionButton.setImageBitmap(ACanvas.getInterfaceSprite(Sprite.SAVEPOINT).bitmap);
        actionButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Helpers.getDp((int) (buttonHeight / 1.5)), Gravity.CENTER | Gravity.BOTTOM);
        params.setMargins(Helpers.getDp(buttonHeight * 3), Helpers.getDp(buttonHeight * 9), 0, 0);
        actionButton.setLayoutParams(params);
        actionButton.setOnClickListener(v -> application.getGame().handleSetSavepointAction());
        actionButton.setVisibility(View.GONE);

        menuLayout = new MenuLinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        menuLayout.addView(titleLayout);
        menuLayout.addView(scrollView);

        trackEditor = new TrackEditorView(this, application, application.getModManager());
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
            actionButton.setImageBitmap(ACanvas.getInterfaceSprite(Sprite.SAVEPOINT).bitmap);
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

        frame.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                frame.getViewTreeObserver().removeOnPreDrawListener(this);
                doStart();
                return true;
            }
        });
    }

    @Override
    public void init() {
        keyboardController.setKeyboardHandler(application.getGame().getKeyboardHandler());

        //todo move to init method in application
        Menu<View> menu = new Menu<>(application, menuFactory);
        viewPlatformMenuElementFactory.setMenu(menu);
        trackEditor.init(application.getGame());
        menuFactory.init(menu, trackEditor, application.getGame());
        application.setMenu(menu);
        this.menu = menu;
    }

    public Application getGdApplication() {
        return application;
    }

    @Override
    public GdMenu getMenu() {
        return menu;
    }

    @Override
    public float getDensity() {
        return getResources().getDisplayMetrics().density;
    }

    public void doStart() {
        MenuHelmetView.clearStaticFields();
        application.doStart();
    }

    @Override
    protected void onResume() {
        Helpers.logDebug("@@@ [GDActivity \"+hashCode()+\"] onResume()");
        super.onResume();
        Helpers.logDebug("[GDActivity \"+hashCode()+\"] onResume(), inited = ");
        application.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Helpers.logDebug("@@@ [GDActivity " + hashCode() + "] onPause()");
        application.onPause();
    }

    //todo refactor
    private static final int[] ignore = new int[] {4, 24, 25};

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        application.onKeyDown(keyCode);
        for (int i : ignore) {
            if (i == keyCode) {
                return super.onKeyDown(keyCode, event);
            }
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        application.onKeyUp(keyCode);
        for (int i : ignore) {
            if (i == keyCode) {
                return super.onKeyDown(keyCode, event);
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        application.onBackPressed();
    }

    @Override
    public void notify(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void setMenu(final MenuScreen menu) {
        LinearLayout layout = (LinearLayout) menu.getLayout();
        setMenu(layout);
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

    public int getButtonsLayoutHeight() {
        return buttonHeight * 3 + KeyboardController.PADDING * 2;
    }

    @Override
    public void showAlert(String title, String message, final Runnable listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(application.getStr().s(R.string.ok), (dialog, which) -> {
                    if (listener != null) listener.run();
                })
                .setOnCancelListener(dialog -> {
                    if (listener != null) listener.run();
                })
                .create();
        alertDialog.show();
    }

    public void showConfirm(String title, String message, final Runnable onOk, final Runnable onCancel) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(application.getStr().s(R.string.ok), (dialog, which) -> {
                    if (onOk != null) onOk.run();
                })
                .setNegativeButton(application.getStr().s(R.string.cancel), (dialog, which) -> {
                    if (onCancel != null) onCancel.run();
                })
                .setOnCancelListener(dialog -> {
                    if (onCancel != null) onCancel.run();
                });
        alert.show();
    }

    // @UiThread
    @Override
    public void hideKeyboardLayout() {
        runOnUiThread(() -> {
            keyboardLayout.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            scrollView.setLayoutParams(params);
        });
    }

    // @UiThread
    @Override
    public void showKeyboardLayout() {
        runOnUiThread(() -> {
            keyboardLayout.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
            params.setMargins(0, 0, 0, Helpers.getDp(getButtonsLayoutHeight()));
            scrollView.setLayoutParams(params);
        });
    }

    // @UiThread
    @Override
    public void gameToMenuUpdateUi() {
        runOnUiThread(() -> {
            menuBtn.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
            menuTitleTextView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
        });
    }

    public void exitEditMode() {
        trackEditor.hideLayout();
    }

    @Override
    public void trainingMode() {
        actionButton.setVisibility(View.VISIBLE);
    }

    // @UiThread
    @Override
    public void menuToGameUpdateUi() {
        runOnUiThread(() -> {
            menuBtn.setVisibility(View.VISIBLE);
            //actionButton.setVisibility(android.view.View.VISIBLE);
            menuTitleTextView.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);

            // Clear menu
            scrollView.removeAllViews();
            menuTitleTextView.setText("");
            menu.setMenuDisabled(true);
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

    @Override
    public void doRestartApp() {
        Intent mStartActivity = new Intent(this, GDActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
    }

    public void pickFile(int requestCode) {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Select a file");
        startActivityForResult(chooseFile, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICKFILE_MRG_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try (InputStream inputStream = Helpers.getGDActivity().getContentResolver().openInputStream(uri)) {
                String name = "MOD" + System.currentTimeMillis();
                Mod mrg = MrgUtils.convertMrg(name, Utils.readAllBytes(inputStream), false);
                menu.setCurrentMenu(menuFactory.get(MenuType.MOD_OPTIONS).build(new MenuData(mrg, name)));
            } catch (Exception e) {
                e.printStackTrace();
                notify("Error: " + e.getMessage());
            }
        }
        if (requestCode == Constants.PICKFILE_MOD_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try (InputStream inputStream = Helpers.getGDActivity().getContentResolver().openInputStream(uri)) {
                Mod mod = Utils.read(inputStream, GDFile.MOD);
                MenuScreen modMenu = menuFactory.get(MenuType.MOD_OPTIONS).build(new MenuData(mod, mod.getName()));
                menu.setCurrentMenu(modMenu);
            } catch (Exception e) {
                e.printStackTrace();
                notify("Error: " + e.getMessage());
            }
        }
        if (requestCode == Constants.PICKFILE_THEME_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try (InputStream inputStream = Helpers.getGDActivity().getContentResolver().openInputStream(uri)) {
                Theme theme = Utils.read(inputStream, GDFile.THEME);
                MenuScreen themeMenu = menuFactory.get(MenuType.THEME_OPTIONS).build(new MenuData(theme, theme.getHeader().getName()));
                menu.setCurrentMenu(themeMenu);
            } catch (Exception e) {
                e.printStackTrace();
                notify("Error: " + e.getMessage());
            }
        }
        if (requestCode == Constants.PICKFILE_RECORD_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try (InputStream inputStream = Helpers.getGDActivity().getContentResolver().openInputStream(uri)) {
                TrackRecord trackRecord = Utils.read(inputStream, GDFile.RECORD);
                MenuScreen recordMenu = menuFactory.get(MenuType.RECORDING_OPTIONS).build(new MenuData(trackRecord));
                menu.setCurrentMenu(recordMenu);
            } catch (Exception e) {
                e.printStackTrace();
                notify("Error: " + e.getMessage());
            }
        }
        if (requestCode == Constants.PICKFILE_TRACK_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try (InputStream inputStream = Helpers.getGDActivity().getContentResolver().openInputStream(uri)) {
                TrackParams track = Utils.read(inputStream, GDFile.TRACK);
                //todo track screen
                application.getModManager().setTrackTheme(track);
                application.getGame().startTrack(GameParams.of(GameMode.SINGLE_TRACK, track.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                notify("Error: " + e.getMessage());
            }
        }
    }

    private int getButtonHeight() {
        if (getString(R.string.screen_type).equals("tablet")) {
            return 85;
        } else if (Helpers.getModManager().getGameDensity() < 1.5) {
            return 55;
        }
        return 60;
    }

    @Override
    public String getAppVersion() {
        String v = "0.0";
        try {
            PackageInfo pInfo = GDActivity.shared.getPackageManager().getPackageInfo(GDActivity.shared.getPackageName(), 0);
            v = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return v;
    }
}