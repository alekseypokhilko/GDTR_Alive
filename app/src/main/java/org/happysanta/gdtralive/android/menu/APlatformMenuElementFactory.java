package org.happysanta.gdtralive.android.menu;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;
import static org.happysanta.gdtralive.android.Helpers.s;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.GDActivity;
import org.happysanta.gdtralive.android.Global;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.views.MenuLinearLayout;
import org.happysanta.gdtralive.game.api.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.android.menu.element.BadgeWithTextElement;
import org.happysanta.gdtralive.game.api.menu.element.HighScoreTextMenuElement;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.menu.element.IMenuActionElement;
import org.happysanta.gdtralive.game.api.menu.element.InputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.MenuActionElement;
import org.happysanta.gdtralive.game.api.menu.element.TextMenuElement;
import org.happysanta.gdtralive.android.menu.views.MenuEditTextView;
import org.happysanta.gdtralive.android.menu.views.MenuHelmetView;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.api.S;
import org.happysanta.gdtralive.game.api.dto.InterfaceTheme;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.MenuFactory;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.menu.ViewUtils;
import org.happysanta.gdtralive.game.api.menu.element.EmptyLineMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.IInputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.IToggleMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.MenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.IOptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.ToggleMenuElement;
import org.happysanta.gdtralive.game.api.menu.view.IMenuEditTextView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuImageView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.util.Fmt;

public class APlatformMenuElementFactory<T> implements PlatformMenuElementFactory<T> {
    public static final int LOCK_IMAGE_MARGIN_RIGHT = 5;
    public static int[] ACHIEVEMENT_ICONS = {
            R.drawable.s_lock1,
            R.drawable.levels_wheel0,
            R.drawable.levels_wheel1,
            R.drawable.levels_wheel2
    };

    private GdMenu<T> menu;
    private final Application application;
    private final ModManager modManager;
    private final Context context;

    public APlatformMenuElementFactory(Application application, GDActivity gdActivity) {
        this.application = application;
        this.modManager = application.getModManager();
        this.context = gdActivity;
    }

    public void setMenu(GdMenu<T> menu) {
        this.menu = menu;
    }

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    public MenuElement<T> emptyLine(boolean beforeAction) {
        return new EmptyLineMenuElement<>(createEmptyLineElement(beforeAction ? 10 : 20));
    }

    public MenuElement<T> restartAction(String name, ActionHandler handler) {
        return reatart(Fmt.colon(s(R.string.restart), name), handler);
    }

    protected static final int TEXT_COLOR = 0xff000000;
    public MenuElement<T> text(String title) {
        IMenuTextView<T> textView = getMenuTextView(SpannedString.valueOf(title), context);
        ((MenuTextView) textView).setTextSize(15);
        return new TextMenuElement<>(textView);
    }

    public IMenuItemElement<T> menu(String title, MenuScreen<T> parent) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuItemElement<>(title, parent, menu, helmetView, textView, touchInterceptor, layout);
    }

    public IMenuActionElement<T> actionContinue(ActionHandler<IMenuActionElement<T>> handler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));

        IMenuImageView<T> lockImage = getMenuImageView(context);
        ((LinearLayout) layout).addView((View) lockImage, 1);
        return new MenuActionElement<T>(s(R.string._continue), MenuFactory.CONTINUE, handler, helmetView, textView, touchInterceptor, layout, lockImage, this);
    }

    public MenuScreen<T> screen(String title, MenuScreen<T> parent) {
        return new AMenuScreen<>(title, parent);
    }

    public MenuElement<T> createAction(int action, ActionHandler actionHandler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuActionElement<T>(application.getStr().s(getActionText(action)), action, actionHandler, helmetView, textView, touchInterceptor, layout, getMenuImageView(context), this);
    }

    public MenuElement<T> backAction(Runnable beforeBack) {
        return createAction(MenuFactory.BACK, item -> {
            beforeBack.run();
            menu.menuBack();
        });
    }

    public MenuElement<T> backAction() {
        return createAction(MenuFactory.BACK, item -> menu.menuBack());
    }

    public MenuElement<T> reatart(String title, ActionHandler handler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuActionElement<T>(title, MenuFactory.RESTART, handler, helmetView, textView, touchInterceptor, layout, getMenuImageView(context), this);
    }

    public IMenuActionElement<T> action(String title, int action, ActionHandler<IMenuActionElement<T>> handler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        IMenuImageView<T> lockImage = getMenuImageView(context);
        ((LinearLayout) layout).addView((View) lockImage, 1);
        return new MenuActionElement<>(title, action, handler, helmetView, textView, touchInterceptor, layout, lockImage, this);
    }

    public IMenuActionElement<T> action(String title, ActionHandler<IMenuActionElement<T>> handler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        IMenuImageView<T> lockImage = getMenuImageView(context);
        ((LinearLayout) layout).addView((View) lockImage, 1);
        return new MenuActionElement<>(title, -1, handler, helmetView, textView, touchInterceptor, layout, lockImage, this);
    }

    public MenuElement<T> textHtmlBold(String key, String value) {
        IMenuTextView<T> textView = getMenuTextView(Html.fromHtml(String.format("<b>%s</b>: %s", key, value == null ? "" : value)), context);
        ((MenuTextView) textView).setTextSize(15);
        return new TextMenuElement<>(textView);
    }

    public MenuElement<T> textHtml(String text) {
        IMenuTextView<T> textView = getMenuTextView(Html.fromHtml(text), context);
        ((MenuTextView) textView).setTextSize(15);
        return new TextMenuElement<>(textView);
    }

    public IMenuItemElement<T> menu(String title, MenuScreen<T> parent, ActionHandler<IMenuItemElement<T>> handler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuItemElement<>(title, parent, menu, handler, helmetView, textView, touchInterceptor, layout);
    }

    public MenuElement<T> badge(int icon, String title) {
        return new BadgeWithTextElement<>(ACHIEVEMENT_ICONS[icon], title, menu, null);
    }

    public IInputTextElement<T> editText(String title, String value, ActionHandler<IInputTextElement<T>> handler) {
        LinearLayout textView = new LinearLayout(getGDActivity());
        String textValue = title == null ? "" : title;
        if (textValue.length() > 25) {
            textView.setOrientation(LinearLayout.VERTICAL);
        } else {
            textView.setOrientation(LinearLayout.HORIZONTAL);
        }
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        MenuTextView<T> optionTextView = new MenuTextView<>(getGDActivity());
        optionTextView.setText(textValue);
        optionTextView.setTextColor(modManager.getInterfaceTheme().getTextColor());
        optionTextView.setTextSize(TEXT_SIZE);
        optionTextView.setTypeface(Global.robotoCondensedTypeface);
        optionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        optionTextView.setPadding(
                textView.getPaddingLeft(),
                textView.getPaddingTop(),
                textView.getPaddingRight(),
                textView.getPaddingBottom()
        );

        IMenuEditTextView<T> editText = new MenuEditTextView<>(context);
        EditText editTextView = (EditText) editText.getView();
        editTextView.setTextColor(modManager.getInterfaceTheme().getTextColor());
        editTextView.setBackgroundColor(modManager.getInterfaceTheme().getMenuBackgroundColor());
        editTextView.setText(value);
        editTextView.setLines(1);

        getGDActivity().textInputs.add(editTextView);

        modManager.registerThemeReloadHandler(() -> {
            InterfaceTheme interfaceTheme = modManager.getInterfaceTheme();
            optionTextView.setTextColor(interfaceTheme.getTextColor());
            editText.setTextColor(interfaceTheme.getTextColor());
            editTextView.setTextColor(interfaceTheme.getTextColor());
            editTextView.setBackgroundColor(interfaceTheme.getMenuBackgroundColor());
        });

        textView.addView(optionTextView);
        textView.addView(editTextView);
        InputTextElement<T> tInputTextElement = new InputTextElement<>(handler, optionTextView, (T) textView, editText);
        editTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && handler != null) {
                handler.handle(tInputTextElement);
            }
        });
        return tInputTextElement;
    }

    public MenuElement<T> highScore(String title, int place, boolean padding) {
        int TEXT_LEFT_MARGIN = 5;
        int SUBTITLE_MARGIN_BOTTOM = 8;
        int SUBTITLE_TEXT_SIZE = 20;
        int LAYOUT_PADDING = 3;
        int TEXT_SIZE = 15;
        int[] medals = new int[]{
                R.drawable.s_medal_gold,
                R.drawable.s_medal_silver,
                R.drawable.s_medal_bronze
        };
        MenuTextView textView = (MenuTextView) getMenuTextView(SpannedString.valueOf(title), context);

        MenuLinearLayout layout = new MenuLinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        MenuImageView image = new MenuImageView(context);
        image.setScaleType(ImageView.ScaleType.CENTER);
        image.setVisibility(View.GONE);
        // textView was already created in super constructor
        textView.setLineSpacing(0, 1);
        textView.setTextSize(TEXT_SIZE);

        LinearLayout.LayoutParams textViewLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // textViewLayoutParams.setMargins(getDp(TEXT_LEFT_MARGIN), 0, 0, 0);
        textView.setLayoutParams(textViewLayoutParams1);

        layout.addView(image, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout.addView(textView);
        if (place >=0 && place <= 2) {
            image.setVisibility(true);
            image.setImageResource(medals[place]);

            LinearLayout.LayoutParams textViewLayoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            textViewLayoutParams.setMargins(getDp(TEXT_LEFT_MARGIN), 0, 0, 0);
            textView.setLayoutParams(textViewLayoutParams);
        }
        layout.setPadding(0, padding ? getDp(LAYOUT_PADDING) : 0, 0, padding ? getDp(LAYOUT_PADDING) : 0);
        return new HighScoreTextMenuElement<>(place, padding, textView, layout);
    }

    public MenuElement<T> getItem(String text, boolean padding) {
        int TEXT_LEFT_MARGIN = 5;
        int SUBTITLE_MARGIN_BOTTOM = 8;
        int SUBTITLE_TEXT_SIZE = 20;
        int LAYOUT_PADDING = 3;
        int TEXT_SIZE = 15;
        MenuTextView textView = (MenuTextView)getMenuTextView(Html.fromHtml(text), context);
        MenuLinearLayout layout = new MenuLinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        MenuImageView image = new MenuImageView(context);
        image.setScaleType(ImageView.ScaleType.CENTER);
        image.setVisibility(View.GONE);
        // textView was already created in super constructor
        textView.setLineSpacing(0, 1);

        LinearLayout.LayoutParams textViewLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // textViewLayoutParams.setMargins(getDp(TEXT_LEFT_MARGIN), 0, 0, 0);
        textView.setLayoutParams(textViewLayoutParams1);

        textView.setTextSize(TEXT_SIZE);
        textView.setTypeface(Global.robotoCondensedTypeface);
        layout.addView(image, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout.addView(textView);

        LinearLayout.LayoutParams textViewLayoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        textViewLayoutParams.setMargins(!padding && false/*showMedal*/ ? getDp(TEXT_LEFT_MARGIN) : 0, 0, 0, padding ? getDp(SUBTITLE_MARGIN_BOTTOM) : 0);
        textView.setLayoutParams(textViewLayoutParams);
        return new HighScoreTextMenuElement<>(padding, textView, layout);
    }

    public IOptionsMenuElement<T> selector(String title, int selected, String[] options, MenuScreen<T> parent, ActionHandler<IOptionsMenuElement<T>> action) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        ((MenuTextView<T>) textView.getView()).setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        IMenuTextView<T> optionsTextView = createOptionsTextView(context, textView);
        IMenuImageView<T> lockImage = getMenuImageView(context);
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        ((LinearLayout) layout).addView((View) lockImage);
        ((LinearLayout) layout).addView((View) optionsTextView.getView());
        return new OptionsMenuElement<>(
                title, selected, menu, options, parent, action, helmetView,
                textView, touchInterceptor, layout, optionsTextView, lockImage, this
        );
    }

    public IToggleMenuElement<T> toggle(String title, int selected, ActionHandler<IToggleMenuElement<T>> action) {
        String[] onOffStrings = application.getStr().getStringArray(S.on_off);
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        ((MenuTextView<T>) textView.getView()).setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        IMenuTextView<T> optionsTextView = createOptionsTextView(context, textView);
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        ((LinearLayout) layout).addView((View) optionsTextView.getView());
        return new ToggleMenuElement<>(title, selected, onOffStrings, action, helmetView, textView, touchInterceptor, layout, optionsTextView);
    }

    public static int getActionText(int action) {
        int r = 0;
        switch (action) {
            case MenuFactory.BACK:
                r = R.string.back;
                break;

            case MenuFactory.NO:
                r = R.string.no;
                break;

            case MenuFactory.YES:
                r = R.string.yes;
                break;

            case MenuFactory.EXIT:
                r = R.string.exit;
                break;

            case MenuFactory.OK:
                r = R.string.ok;
                break;

            case MenuFactory.PLAY_MENU:
                r = R.string.campaign;
                break;

            case MenuFactory.GO_TO_MAIN:
                r = R.string.go_to_main;
                break;

            case MenuFactory.RESTART:
                r = R.string.restart;
                break;

            case MenuFactory.NEXT:
                r = R.string.next;
                break;

            case MenuFactory.CONTINUE:
                r = R.string._continue;
                break;

            case MenuFactory.LOAD:
                r = R.string.load_this_game;
                break;

            case MenuFactory.INSTALL:
                r = R.string.install_kb;
                break;

            case MenuFactory.DELETE:
                r = R.string.delete;
                break;

            case MenuFactory.RESTART_WITH_NEW_LEVEL:
                r = R.string.restart_with_new_level;
                break;

            case MenuFactory.SEND_LOGS:
                r = R.string.send_logs;
                break;

            case MenuFactory.LIKE:
                r = R.string.like;
                break;

        }
        return r;
    }

    protected IMenuHelmetView<T> createHelmetView(Context context) {
        return new MenuHelmetView<>(context);
    }

    public static final int TEXT_SIZE = 20;
    public static final int PADDING_TOP = 5;

    protected MenuTextView<T> createTextView(Context context) {
        MenuTextView<T> mtv = new MenuTextView<>(context);
//        mtv.setText(getTextForView());
        mtv.setTextColor(getGDActivity().getResources().getColorStateList(R.drawable.menu_item_color));
        mtv.setTypeface(Global.robotoCondensedTypeface);
        mtv.setTextSize(TEXT_SIZE);
        mtv.setTextColor(modManager.getInterfaceTheme().getTextColor());
        mtv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mtv.setPadding(0, getDp(PADDING_TOP), 0, getDp(PADDING_TOP));
        modManager.registerThemeReloadHandler(() -> mtv.setTextColor(modManager.getInterfaceTheme().getTextColor()));
        return mtv;
    }

    private IMenuTextView<T> createOptionsTextView(Context context, MenuTextView<T> textView) {
        MenuTextView<T> optionTextView = new MenuTextView<>(context);
        optionTextView.setTextColor(modManager.getInterfaceTheme().getTextColor());
        optionTextView.setTextSize(TEXT_SIZE);
        optionTextView.setTypeface(Global.robotoCondensedTypeface);
        optionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        optionTextView.setPadding(
                ((MenuTextView) textView.getView()).getPaddingLeft(),
                ((MenuTextView) textView.getView()).getPaddingTop(),
                ((MenuTextView) textView.getView()).getPaddingRight(),
                ((MenuTextView) textView.getView()).getPaddingBottom()
        );
        modManager.registerThemeReloadHandler(() -> optionTextView.setTextColor(modManager.getInterfaceTheme().getTextColor()));
        return optionTextView;
    }

    protected T createLayout(Context context, IMenuHelmetView<T> helmet, IMenuTextView<T> textView, View.OnTouchListener onTouchListener) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView((View) helmet.getView());
        layout.addView((View) textView.getView());
        layout.setOnTouchListener(onTouchListener);
        return (T) layout;
    }

    protected View.OnTouchListener createOnTouchListener(TouchInterceptor<T> touchInterceptor) {
        ViewUtils<T> viewUtils = new AViewUtils<>();
        return (view, motionEvent) -> touchInterceptor.onTouch((T) view, motionEvent.getAction(), (int) motionEvent.getRawX(), (int) motionEvent.getRawY(), viewUtils);
    }

    private IMenuTextView<T> createEmptyLineElement(int offset) {
        MenuTextView<T> view = new MenuTextView<>(context);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDp(offset)));
        return view;
    }

    private MenuImageView<T> getMenuImageView(Context context) {
        MenuImageView<T> lockImage = new MenuImageView<>(context);
        lockImage.setScaleType(ImageView.ScaleType.CENTER);
        lockImage.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, getDp(LOCK_IMAGE_MARGIN_RIGHT), 0);
        lockImage.setLayoutParams(lp);
        return lockImage;
    }

    private IMenuTextView<T> getMenuTextView(Spanned text, Context activity) {
        MenuTextView textView1 = new MenuTextView(activity);
        textView1.setText(text);
        textView1.setTextColor(TEXT_COLOR);
        textView1.setTextSize(TEXT_SIZE);
        textView1.setTextColor(modManager.getInterfaceTheme().getTextColor());
        modManager.registerThemeReloadHandler(() -> textView1.setTextColor(modManager.getInterfaceTheme().getTextColor()));
        textView1.setLineSpacing(0f, 1.5f);
        textView1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Linkify.addLinks(textView1, Linkify.WEB_URLS);
        textView1.setLinksClickable(true);
        return textView1;
    }
}
