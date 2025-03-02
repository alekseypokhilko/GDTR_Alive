package org.happysanta.gdtralive.android.menu;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;
import static org.happysanta.gdtralive.android.Helpers.s;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.GDActivity;
import org.happysanta.gdtralive.android.Global;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.element.AOptionsMenuElement;
import org.happysanta.gdtralive.android.menu.element.BadgeWithTextElement;
import org.happysanta.gdtralive.android.menu.element.HighScoreTextMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.InputTextElement;
import org.happysanta.gdtralive.android.menu.element.MenuActionElement;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.android.menu.views.MenuEditTextView;
import org.happysanta.gdtralive.android.menu.views.MenuHelmetView;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.api.S;
import org.happysanta.gdtralive.game.api.dto.InterfaceTheme;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuFactory;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.menu.ViewUtils;
import org.happysanta.gdtralive.game.api.menu.element.EmptyLineMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.IInputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.IToggleMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.MenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.ToggleMenuElement;
import org.happysanta.gdtralive.game.api.menu.view.IMenuEditTextView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.util.Fmt;

public class APlatformMenuElementFactory<T> implements PlatformMenuElementFactory<T> {
    public static int[] ACHIEVEMENT_ICONS = {
            R.drawable.s_lock1,
            R.drawable.levels_wheel0,
            R.drawable.levels_wheel1,
            R.drawable.levels_wheel2
    };

    public static final int[] locks = new int[]{
            R.drawable.s_lock0,
            R.drawable.s_lock1,
            R.drawable.s_lock2
    };
    private GdMenu<T> menu;
    private final Application application;
    private final Context context;

    public APlatformMenuElementFactory(Application application, GDActivity gdActivity) {
        this.application = application;
        this.context = gdActivity;
    }

    public void setMenu(GdMenu<T> menu) {
        this.menu = menu;
    }

    public MenuElement<T> emptyLine(boolean beforeAction) {
        return new EmptyLineMenuElement<>(createEmptyLineElement(beforeAction ? 10 : 20));
    }

    public MenuElement<T> restartAction(String name, ActionHandler handler) {
        return reatart(Fmt.colon(s(R.string.restart), name), handler);
    }

    public MenuElement<T> text(String title) {
        return new TextMenuElement<>(title);
    }

    public IMenuItemElement<T> menu(String title, MenuScreen<T> parent) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuItemElement<>(title, parent, menu, helmetView, textView, touchInterceptor, layout);
    }

    public MenuElement<T> actionContinue(ActionHandler handler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuActionElement<T>(s(R.string._continue), MenuFactory.CONTINUE, handler, helmetView, textView, touchInterceptor, layout);
    }

    public MenuScreen<T> screen(String title, MenuScreen<T> parent) {
        return new AMenuScreen<>(title, parent);
    }

    public MenuElement<T> createAction(int action, ActionHandler actionHandler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuActionElement<T>(application.getStr().s(getActionText(action)), action, actionHandler, helmetView, textView, touchInterceptor, layout);
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
        return new MenuActionElement<T>(title, MenuFactory.RESTART, handler, helmetView, textView, touchInterceptor, layout);
    }

    public MenuElement<T> action(String title, int action, ActionHandler<MenuElement<T>> handler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuActionElement<>(title, action, handler, helmetView, textView, touchInterceptor, layout);
    }

    public MenuElement<T> action(String title, ActionHandler<MenuElement<T>> handler) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new MenuActionElement<>(title, -1, handler, helmetView, textView, touchInterceptor, layout);
    }

    public MenuElement<T> textHtmlBold(String key, String value) {
        return new TextMenuElement<>(Html.fromHtml(String.format("<b>%s</b>: %s", key, value == null ? "" : value)));
    }

    public MenuElement<T> textHtml(String text) {
        return new TextMenuElement<>(Html.fromHtml(text));
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
        optionTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
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
        editTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
        editTextView.setText(value);
        editTextView.setLines(1);

        getGDActivity().textInputs.add(editTextView);

        Helpers.getModManager().registerThemeReloadHandler(() -> {
            InterfaceTheme interfaceTheme = Helpers.getModManager().getInterfaceTheme();
            optionTextView.setTextColor(interfaceTheme.getTextColor());
            editText.setTextColor(interfaceTheme.getTextColor());
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
        return new HighScoreTextMenuElement<>(title, place, padding);
    }

    public MenuElement<T> getItem(String text, boolean padding) {
        return new HighScoreTextMenuElement<>(Html.fromHtml(text), padding);
    }

    public OptionsMenuElement<T> selector(String title, int selected, String[] options, MenuScreen<T> parent, ActionHandler<OptionsMenuElement<T>> action) {
        IMenuHelmetView<T> helmetView = createHelmetView(context);
        MenuTextView<T> textView = createTextView(context);
        ((MenuTextView<T>) textView.getView()).setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        T layout = createLayout(context, helmetView, textView, createOnTouchListener(touchInterceptor));
        return new AOptionsMenuElement<>(title, selected, menu, options, parent, action, helmetView, textView, touchInterceptor, layout);
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
        mtv.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
        mtv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mtv.setPadding(0, getDp(PADDING_TOP), 0, getDp(PADDING_TOP));
        Helpers.getModManager().registerThemeReloadHandler(() -> mtv.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor()));
        return mtv;
    }

    private IMenuTextView<T> createOptionsTextView(Context context, MenuTextView<T> textView) {
        MenuTextView<T> optionTextView = new MenuTextView<>(context);
        optionTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
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
        Helpers.getModManager().registerThemeReloadHandler(() -> optionTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor()));
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
}
