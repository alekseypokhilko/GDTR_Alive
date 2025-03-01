package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;
import static org.happysanta.gdtralive.android.Helpers.s;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.Global;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.AMenuScreen;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.IOptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuHandler;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

public class OptionsMenuElement<T>
        extends ClickableMenuElement<T>
        implements IOptionsMenuElement<T> {

    protected int selectedIndex;
    protected String[] options;
    protected int unlockedCount;
    protected MenuHandler<T> menuHandler;
    protected MenuScreen<T> optionsScreen = null;
    protected MenuScreen<T> screen = null;
    protected boolean isOnOffToggle;
    protected boolean m_oZ = false;
    protected String selectedOption;
    protected MenuActionElement<T>[] optionsScreenItems = null;
    protected MenuImageView lockImage = null;
    protected MenuTextView optionTextView = null;
    protected ActionHandler<IOptionsMenuElement<T>> action;

    public OptionsMenuElement(String text, int selectedIndex, MenuHandler<T> menuHandler, String[] options, boolean isOnOffToggle, MenuScreen<T> screen, ActionHandler<IOptionsMenuElement<T>> action) {
        this.text = text;
        this.selectedIndex = selectedIndex;
        this.menuHandler = menuHandler;
        this.options = options;
        if (this.options == null) this.options = new String[]{""};
        unlockedCount = this.options.length - 1;
        this.isOnOffToggle = isOnOffToggle;
        this.action = action;

        createAllViews();
        setSelectedOption(selectedIndex);

        if (isOnOffToggle) {
            if (selectedIndex == 1) {
                selectedOption = s(R.string.off);
            } else {
                selectedOption = s(R.string.on);
            }
        } else {
            this.screen = screen;
            updateSelectedOption();
            update();
        }
    }

    @Override
    protected void createAllViews() {
        Context context = getGDActivity();

        super.createAllViews();

        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        optionTextView = new MenuTextView(context);
        optionTextView.setText(selectedOption);
        optionTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
        Helpers.getModManager().registerThemeReloadHandler(this::onOptionTextViewThemeReload);
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

        lockImage = new MenuImageView(context);
        lockImage.setImageResource(MenuActionElement.locks[Helpers.getModManager().getInterfaceTheme().getLockSkinIndex()]);
        lockImage.setScaleType(ImageView.ScaleType.CENTER);
        lockImage.setVisibility(View.GONE);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, getDp(MenuActionElement.LOCK_IMAGE_MARGIN_RIGHT), 0);
        lockImage.setLayoutParams(lp);
        lockImage.setVisibility(View.GONE);

        layout.addView(lockImage);
        layout.addView(optionTextView);
    }

    private void onOptionTextViewThemeReload() {
        getOptionTextView().setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
    }

    public MenuTextView getOptionTextView() {
        return optionTextView;
    }

    private void updateSelectedOption() {
        selectedOption = options[selectedIndex];
        updateViewText();

        if (selectedIndex > unlockedCount && !isOnOffToggle) {
            lockImage.setVisibility(View.VISIBLE);
        } else {
            lockImage.setVisibility(View.GONE);
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

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String as[]) {
        setOptions(as, true);
    }

    public void setOptions(String as[], boolean update) {
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
        optionsScreen = new AMenuScreen(text, screen);
        optionsScreenItems = new MenuActionElement[options.length];
        for (int k = 0; k < optionsScreenItems.length; k++) {
            if (k > unlockedCount) {
                optionsScreenItems[k] = new MenuActionElement(options[k], this, null);
                optionsScreenItems[k].setLock(true, true);
            } else {
                optionsScreenItems[k] = new MenuActionElement(options[k], this, null);
            }
            optionsScreen.addItem(optionsScreenItems[k]);
        }
        optionsScreen.setSelected(selectedIndex);

        // System.gc();
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
        handleAction();
        menuHandler.handleAction(this);
    }

    @Override
    public MenuScreen<T> getCurrentMenu() {
        return optionsScreen;
    }

    @Override
    protected void updateViewText() {
        if (textView != null && textView instanceof MenuTextView)
            ((MenuTextView) textView).setTextOnUiThread(getTextForView());
        if (optionTextView != null) optionTextView.setTextOnUiThread(selectedOption);
    }

    @Override
    public void performAction(int k) {
        // logDebug("OptionMenuElement performAction: k = " + k);
        switch (k) {
            case KeyboardHandler.KEY_FIRE:
                if (isOnOffToggle) {
                    selectedIndex++;
                    if (selectedIndex > 1)
                        selectedIndex = 0;
                    if (selectedIndex == 1)
                        selectedOption = s(R.string.off);
                    else
                        selectedOption = s(R.string.on);
                    updateViewText();
                    handleAction();
                    menuHandler.handleAction(this);
                    return;
                } else {
                    m_oZ = true;
                    handleAction();
                    menuHandler.handleAction(this);
                    return;
                }

            case KeyboardHandler.KEY_RIGHT:
                if (isOnOffToggle) {
                    if (selectedIndex == 1) {
                        selectedIndex = 0;
                        selectedOption = s(R.string.on);
                        handleAction();
                        menuHandler.handleAction(this);
                        updateViewText();
                    }
                    return;
                }
                selectedIndex++;
                if (selectedIndex > options.length - 1) {
                    selectedIndex = options.length - 1;
                } else {
                    handleAction();
                    menuHandler.handleAction(this);
                }
                updateSelectedOption();
                return;

            case KeyboardHandler.KEY_LEFT: // '\003'
                if (isOnOffToggle) {
                    if (selectedIndex == 0) {
                        selectedIndex = 1;
                        selectedOption = s(R.string.off);
                        handleAction();
                        menuHandler.handleAction(this);
                        updateViewText();
                    }
                    return;
                }
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = 0;
                } else {
                    updateSelectedOption();
                    handleAction();
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
        return String.format("%s: ", text);
    }

    @Override
    protected void onHighlightChanged() {
        lockImage.setImageResource(MenuActionElement.locks[isHighlighted ? 2 : Helpers.getModManager().getInterfaceTheme().getLockSkinIndex()]);
    }

    private void handleAction() {
        if (action != null) {
            action.handle(this);
        }
    }
}
