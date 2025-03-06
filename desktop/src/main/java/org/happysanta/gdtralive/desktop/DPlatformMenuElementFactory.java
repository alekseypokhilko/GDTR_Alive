package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.desktop.menu.DMenuScreen;
import org.happysanta.gdtralive.desktop.menu.DViewUtils;
import org.happysanta.gdtralive.desktop.menu.MenuHelmetView;
import org.happysanta.gdtralive.desktop.menu.MenuImageView;
import org.happysanta.gdtralive.desktop.menu.MenuTextView;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.S;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuFactory;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.menu.ViewUtils;
import org.happysanta.gdtralive.game.api.menu.element.EmptyLineMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.IInputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuActionElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.IOptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.IToggleMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.MenuActionElement;
import org.happysanta.gdtralive.game.api.menu.element.MenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.TextMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.ToggleMenuElement;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuImageView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DPlatformMenuElementFactory<T> implements PlatformMenuElementFactory<T> {

    private GdMenu<T> menu;
    private final Application application;
    private final ModManager modManager;
    private Font font;

    public DPlatformMenuElementFactory(Application application) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream("RobotoCondensed-Regular.ttf")) {
            font = Font.createFont(0, inputStream);
        } catch (Exception e) {
            font = new Font(null, Font.PLAIN, 20);
        }
        this.application = application;
        this.modManager = application.getModManager();
    }

    @Override
    public void setMenu(GdMenu<T> menu) {
        this.menu = menu;
    }

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public MenuElement<T> emptyLine(boolean beforeAction) {
        return new EmptyLineMenuElement<>(createEmptyLineElement(beforeAction ? 10 : 20));
    }

    @Override
    public MenuElement<T> restartAction(String name, ActionHandler handler) {
        return null;
    }

    @Override
    public MenuElement<T> text(String title) {
        MenuTextView<T> textView = getTextView();
        textView.setText(title);
        return new TextMenuElement<>(textView);
    }

    @Override
    public IMenuItemElement<T> menu(String title, MenuScreen<T> parent) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        JLabel view = (JLabel) textView.getView();
        textView.setText(title);
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        T layout = createLayout(helmetView, textView);
        return new MenuItemElement<>(title, parent, menu, helmetView, textView, touchInterceptor, layout);
    }

    @Override
    public IMenuActionElement<T> actionContinue(ActionHandler<IMenuActionElement<T>> handler) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        JLabel view = (JLabel) textView.getView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        MenuImageView<T> lock = getLock();
        T layout = createLayout(helmetView, textView);
        return new MenuActionElement<T>("Continue", MenuFactory.CONTINUE, handler, helmetView, textView, touchInterceptor, layout, lock, this);
    }

    @Override
    public MenuScreen<T> screen(String title, MenuScreen<T> parent) {
        return new DMenuScreen<>(title, parent);
    }

    @Override
    public MenuElement<T> createAction(int action, ActionHandler actionHandler) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        JLabel view = (JLabel) textView.getView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        MenuImageView<T> lock = getLock();
        T layout = createLayout(helmetView, textView);
        return new MenuActionElement<T>(application.getStr().s(action), action, actionHandler, helmetView, textView, touchInterceptor, layout, lock, this);
    }

    @Override
    public MenuElement<T> backAction(Runnable beforeBack) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        textView.setText("Back");
        JLabel view = (JLabel) textView.getView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        MenuImageView<T> lock = getLock();
        T layout = createLayout(helmetView, textView);
        return new MenuActionElement<T>("Back", MenuFactory.BACK, item -> {
            beforeBack.run();
            menu.menuBack();
        }, helmetView, textView, touchInterceptor, layout, lock, this);
    }

    @Override
    public MenuElement<T> backAction() {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        textView.setText("Back");
        JLabel view = (JLabel) textView.getView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        MenuImageView<T> lock = getLock();
        T layout = createLayout(helmetView, textView);
        return new MenuActionElement<T>("Back", MenuFactory.BACK, item -> menu.menuBack(), helmetView, textView, touchInterceptor, layout, lock, this);

    }

    @Override
    public MenuElement<T> reatart(String title, ActionHandler handler) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        textView.setText(title);
        JLabel view = (JLabel) textView.getView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        MenuImageView<T> lock = getLock();
        T layout = createLayout(helmetView, textView);
        return new MenuActionElement<T>("Restart", MenuFactory.RESTART, handler, helmetView, textView, touchInterceptor, layout, lock, this);
    }

    @Override
    public IMenuActionElement<T> action(String title, int action, ActionHandler<IMenuActionElement<T>> handler) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        JLabel view = (JLabel) textView.getView();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        MenuImageView<T> lock = getLock();
        T layout = createLayout(helmetView, textView);
        return new MenuActionElement<>(title, action, handler, helmetView, textView, touchInterceptor, layout, lock, this);
    }

    @Override
    public IMenuActionElement<T> action(String title, ActionHandler<IMenuActionElement<T>> handler) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        JLabel view = (JLabel) textView.getView();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        MenuImageView<T> lock = getLock();
        T layout = createLayout(helmetView, textView);
        return new MenuActionElement<>(title, -1, handler, helmetView, textView, touchInterceptor, layout, lock, this);
    }

    @Override
    public MenuElement<T> textHtmlBold(String key, String value) {
        return null;
    }

    @Override
    public MenuElement<T> textHtml(String text) {
        return null;
    }

    @Override
    public IMenuItemElement<T> menu(String title, MenuScreen<T> parent, ActionHandler<IMenuItemElement<T>> handler) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        JLabel view = (JLabel) textView.getView();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        T layout = createLayout(helmetView, textView);
        return new MenuItemElement<>(title, parent, menu, handler, helmetView, textView, touchInterceptor, layout);
    }

    @Override
    public MenuElement<T> badge(int icon, String title) {
        return null;
    }

    @Override
    public IInputTextElement<T> editText(String title, String value, ActionHandler<IInputTextElement<T>> handler) {
        return null;
    }

    @Override
    public MenuElement<T> highScore(String title, int place, boolean padding) {
        return null;
    }

    @Override
    public MenuElement<T> getItem(String text, boolean padding) {
        return null;
    }

    @Override
    public IOptionsMenuElement<T> selector(String title, int selected, String[] options, MenuScreen<T> parent, ActionHandler<IOptionsMenuElement<T>> action) {
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        IMenuTextView<T> optionsTextView = getTextView();
        IMenuImageView<T> lockImage = getLock();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        JLabel view = (JLabel) textView.getView();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        T layout = createLayout(helmetView, textView);
        ((JPanel) layout).add((JComponent) lockImage);
        ((JPanel) layout).add((JComponent) optionsTextView.getView());
        return new OptionsMenuElement<>(
                title, selected, menu, options, parent, action, helmetView,
                textView, touchInterceptor, layout, optionsTextView, lockImage, this
        );
    }

    @Override
    public IToggleMenuElement<T> toggle(String title, int selected, ActionHandler<IToggleMenuElement<T>> action) {
        String[] onOffStrings = application.getStr().getStringArray(S.on_off);
        IMenuHelmetView<T> helmetView = getHelmetView();
        MenuTextView<T> textView = getTextView();
        TouchInterceptor<T> touchInterceptor = new TouchInterceptor<>();
        IMenuTextView<T> optionsTextView = getTextView();
        IMenuImageView<T> lockImage = getLock();
        ViewUtils<T> viewUtils = new DViewUtils<>();
        JLabel view = (JLabel) textView.getView();
        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                touchInterceptor.onTouch((T) view, 1, 0, 0, viewUtils);
            }
        });
        T layout = createLayout(helmetView, textView);
        ((JPanel) layout).add((JComponent) lockImage);
        ((JPanel) layout).add((JComponent) optionsTextView.getView());
        return new ToggleMenuElement<>(title, selected, onOffStrings, action, helmetView, textView, touchInterceptor, layout, optionsTextView);

    }

    protected T createLayout(IMenuHelmetView<T> helmet, IMenuTextView<T> textView) {
        JPanel layout = new JPanel(new FlowLayout(FlowLayout.LEFT));
        layout.setBackground(new Color(0,0,0,0));
        layout.setOpaque(false);
        layout.add((Component) helmet.getView());
        layout.add((Component) textView.getView());
        return (T) layout;
    }
    private static <T> MenuHelmetView<T> getHelmetView() {
        return new MenuHelmetView<>();
    }

    private MenuTextView<T> getTextView() {
        MenuTextView<T> view = new MenuTextView<>();
        view.setBackground(new Color(0,0,0,0));
        view.setOpaque(false);
        try {
            view.setFont(font.deriveFont(25f));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private IMenuTextView<T> createEmptyLineElement(int offset) { //todo
        return new MenuTextView<>();
    }

    private static <T> MenuImageView<T> getLock() {
        return new MenuImageView<>();
    }
}
