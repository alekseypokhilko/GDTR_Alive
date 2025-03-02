package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuHandler;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuImageView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.util.Fmt;

public class OptionsMenuElement<T>
        extends ClickableMenuElement<T>
        implements IOptionsMenuElement<T>, ActionHandler<IMenuActionElement<T>> {
    protected int selectedIndex;
    protected String[] options;
    protected int unlockedCount;
    protected String selectedOption;
    protected boolean m_oZ = false;
    protected PlatformMenuElementFactory<T> factory;
    protected MenuHandler<T> menuHandler;
    protected MenuScreen<T> optionsScreen = null;
    protected MenuScreen<T> screen = null;
    protected IMenuActionElement<T>[] optionsScreenItems = null;
    protected IMenuImageView<T> lockImage = null;
    protected IMenuTextView<T> optionTextView = null;
    protected ActionHandler<IOptionsMenuElement<T>> action;

    public OptionsMenuElement(String text, int selectedIndex, MenuHandler<T> menuHandler, String[] options,
                              MenuScreen<T> screen, ActionHandler<IOptionsMenuElement<T>> action,
                              IMenuHelmetView<T> helmetView, IMenuTextView<T> textView, TouchInterceptor<T> touchInterceptor,
                              T layout, IMenuTextView<T> optionsTextView, IMenuImageView<T> lockImage,
                              PlatformMenuElementFactory<T> factory) {
        super(layout, textView, helmetView, touchInterceptor);
        this.text = text;
        this.selectedIndex = selectedIndex;
        this.menuHandler = menuHandler;
        this.options = options;
        if (this.options == null) this.options = new String[]{""};
        unlockedCount = this.options.length - 1;
        this.action = action;
        this.textView.setText(getTextForView());
        this.optionTextView = optionsTextView;
        this.lockImage = lockImage;
        this.factory = factory;
        setSelectedOption(selectedIndex);
        this.screen = screen;
        updateSelectedOption();
        update();
    }

    private void updateSelectedOption() {
        selectedOption = options[selectedIndex];
        updateViewText();

        if (selectedIndex > unlockedCount) {
            if (lockImage != null) {
                lockImage.setVisibility(true);
            }
        } else {
            if (lockImage != null) {
                lockImage.setVisibility(false);
            }
        }
    }

    public int getUnlockedCount() {
        return unlockedCount;
    }

    public void setUnlockedCount(int k) {
        unlockedCount = k;
        if (unlockedCount > options.length - 1)
            unlockedCount = options.length - 1;
        if (optionsScreen != null) {
            for (int l = 0; l < optionsScreenItems.length; l++)
                if (l > k)
                    optionsScreenItems[l].setLock(true, true);
                else
                    optionsScreenItems[l].setLock(false, false);
        }
        updateSelectedOption();
    }

    public void setOptions(String[] as) {
        setOptions(as, true);
    }

    public void setOptions(String[] as, boolean update) {
        options = as;
        if (selectedIndex > options.length - 1)
            selectedIndex = options.length - 1;
        if (unlockedCount > options.length - 1)
            unlockedCount = options.length - 1;
        updateSelectedOption();
        if (update) update();
    }

    @Override
    public int getSelectedOption() {
        return selectedIndex;
    }

    @Override
    public void setSelectedOption(int k) {
        selectedIndex = k;
        if (selectedIndex > options.length - 1)
            selectedIndex = 0;
        if (selectedIndex < 0)
            selectedIndex = options.length - 1;
        updateSelectedOption();
    }

    @Override
    public void update() {
        optionsScreen = factory.screen(text, screen);
        optionsScreenItems = new IMenuActionElement[options.length];
        for (int k = 0; k < optionsScreenItems.length; k++) {
            optionsScreenItems[k] = factory.action(options[k], this);
            if (k > unlockedCount) {
                optionsScreenItems[k].setLock(true, true);
            }
            optionsScreen.add(optionsScreenItems[k]);
        }
        optionsScreen.setSelected(selectedIndex);

        System.gc();
    }

    @Override
    public boolean _charvZ() {
        if (m_oZ) {
            m_oZ = false;
            return true;
        } else {
            return m_oZ;
        }
    }

    @Override
    public void handleAction(MenuElement<T> item) {
        int k = 0;
        do {
            if (k >= optionsScreenItems.length)
                break;
            if (item == optionsScreenItems[k]) {
                selectedIndex = k;
                updateSelectedOption();
                break;
            }
            k++;
        } while (true);

        menuHandler.setCurrentMenu(screen);
        performAction();
        menuHandler.handleAction(this);
    }

    @Override
    public void handle(IMenuActionElement<T> item) {
        handleAction(item);
    }

    @Override
    public MenuScreen<T> getCurrentMenu() {
        return optionsScreen;
    }

    @Override
    protected void updateViewText() {
        if (textView != null)
            textView.setTextOnUiThread(getTextForView());
        if (optionTextView != null) optionTextView.setTextOnUiThread(selectedOption);
    }

    @Override
    public void performAction(int k) {
        // logDebug("OptionMenuElement performAction: k = " + k);
        switch (k) {
            case KeyboardHandler.KEY_FIRE:
                m_oZ = true;
                performAction();
                menuHandler.handleAction(this);
                return;

            case KeyboardHandler.KEY_RIGHT:
                selectedIndex++;
                if (selectedIndex > options.length - 1) {
                    selectedIndex = options.length - 1;
                } else {
                    performAction();
                    menuHandler.handleAction(this);
                }
                updateSelectedOption();
                return;

            case KeyboardHandler.KEY_LEFT: // '\003'
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = 0;
                } else {
                    updateSelectedOption();
                    performAction();
                    menuHandler.handleAction(this);
                }
                updateSelectedOption();
                break;
        }
    }

    @Override
    public void setScreen(MenuScreen<T> screen) {
        this.screen = screen;
    }

    @Override
    protected String getTextForView() {
        return Fmt.colon(text);
    }

    @Override
    protected void onHighlightChanged() {
        lockImage.setLock(isHighlighted ? 2 : factory.getModManager().getInterfaceTheme().getLockSkinIndex());
    }

    private void performAction() {
        if (action != null) {
            action.handle(this);
        }
    }
}
