package org.happysanta.gdtralive.android.menu;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;
import static org.happysanta.gdtralive.android.Helpers.isSDK11OrHigher;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.android.GDActivity;
import org.happysanta.gdtralive.android.menu.api.MenuScreen;
import org.happysanta.gdtralive.android.menu.api.OnMenuElementHighlightListener;
import org.happysanta.gdtralive.android.menu.element.ClickableMenuElement;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.android.menu.api.MenuElement;
import org.happysanta.gdtralive.android.menu.views.MenuLinearLayout;
import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.util.BiFunction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class AMenuScreen implements OnMenuElementHighlightListener, MenuScreen<View>, Serializable {

	protected static final int LAYOUT_LEFT_PADDING = 30;
	protected static final int LAYOUT_TOP_PADDING = 0;
	protected static final int LAYOUT_BOTTOM_PADDING = 15;

	protected AMenuScreen parent;

	protected String title;
	protected int selectedIndex;
	protected Vector menuItems;
	protected MenuLinearLayout layout;
	protected MenuElement<View> lastHighlighted;
	protected boolean isTextScreen = false;
	protected Runnable beforeShowAction;
	protected BiFunction<AMenuScreen, MenuData, AMenuScreen> builder;
	protected Map<Integer, MenuAction> actions = new HashMap<>();

	public AMenuScreen(String title, AMenuScreen parent) {
		this.title = title;
		selectedIndex = -1;
		menuItems = new Vector();
		this.parent = parent;

		Context context = getGDActivity();

		layout = new MenuLinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(getDp(LAYOUT_LEFT_PADDING), getDp(LAYOUT_TOP_PADDING), getDp(LAYOUT_LEFT_PADDING), getDp(LAYOUT_BOTTOM_PADDING));

		// Disable multi-touch in menu
		if (isSDK11OrHigher())
			layout.setMotionEventSplittingEnabled(false);
	}

	public void addItem(MenuElement<View> item) {
		if (item instanceof MenuAction) {
			MenuAction action = (MenuAction) item;
			actions.put(action.getActionValue(), action);
		}

		layout.addView(item.getView());
		menuItems.add(item);

		if (item instanceof ClickableMenuElement)
			((ClickableMenuElement) item).setOnHighlightListener(this);
	}

	protected void scrollToItem(MenuElement<View> item) {
		getGDActivity().scrollToView(item.getView());
	}

	public void performAction(int k) {
		// logDebug("MenuScreen.performAction: k = " + k);
		int from = 0;
		switch (k) {
			default:
				// logDebug("selectedIndex = " + selectedIndex);
				if (selectedIndex != -1) {
					for (int i = selectedIndex; i < menuItems.size(); i++) {
						MenuElement item;
						if ((item = (MenuElement) menuItems.elementAt(i)) != null && item.isSelectable()) {
							item.performAction(k);
							return;
						}
					}
				}
				break;

			case KeyboardHandler.KEY_UP:
				if (isTextScreen) {
					getGDActivity().scrollTextMenuUp();
					return;
				}

				if (selectedIndex > 0 && !elementIsFirstClickable(selectedIndex)) {
					from = selectedIndex - 1;
				} else {
					from = menuItems.size() - 1;
				}

				for (int i = from; i >= 0; i--) {
					MenuElement el = (MenuElement) menuItems.elementAt(i);
					if (!(el instanceof ClickableMenuElement) || ((ClickableMenuElement) el).isDisabled()) {
						continue;
					}

					highlightElement((ClickableMenuElement) el);
					selectedIndex = i;
					scrollToItem(el);
					break;
				}
				break;

			case KeyboardHandler.KEY_DOWN:
				if (isTextScreen) {
					getGDActivity().scrollTextMenuDown();
					return;
				}

				if (selectedIndex < menuItems.size() - 1) {
					from = selectedIndex + 1;
				} else {
					from = 0;
				}
				for (int i = from; i < menuItems.size(); i++) {
					MenuElement el = (MenuElement) menuItems.elementAt(i);
					if (!(el instanceof ClickableMenuElement) || ((ClickableMenuElement) el).isDisabled()) {
						continue;
					}

					highlightElement((ClickableMenuElement) el);
					selectedIndex = i;
					scrollToItem(el);
					break;
				}
				break;
		}
	}

	protected boolean elementIsFirstClickable(int index) {
		for (int i = 0; i < menuItems.size(); i++) {
			MenuElement el = (MenuElement) menuItems.elementAt(i);
			if (!(el instanceof ClickableMenuElement) || ((ClickableMenuElement) el).isDisabled()) {
				if (i == index) {
					return false;
				}
			} else {
				if (i < index) return false;
				if (i == index) return true;
			}
		}

		return false;
	}

	public AMenuScreen getParent() {
		return parent;
	}

	public void setParent(AMenuScreen target) {
		parent = target;
	}

	public void clear() {
		menuItems.removeAllElements();
		layout.removeAllViews();

		selectedIndex = -1;
		lastHighlighted = null;
	}

	public LinearLayout getLayout() {
		return layout;
	}

	public void setTitle(String s) {
		title = s;
	}

	protected void updateTitle() {
		final GDActivity gd = getGDActivity();
		gd.runOnUiThread(() -> {
			gd.menuTitleTextView.setText(title);
			// activity.menuTitleTextView.invalidate();
			gd.titleLayout.invalidate();
		});
	}

	public MenuAction getActions(int action) {
		return actions.get(action);
	}

	public void onShow() {
		updateTitle();
		highlightElement();
	}

	public void resetHighlighted() {
		lastHighlighted = null;
	}

	public void highlightElement() {
		if (lastHighlighted != null) {
			lastHighlighted.showHelmet();
			final ViewTreeObserver obs = layout.getViewTreeObserver();
			obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					try {
						obs.removeOnPreDrawListener(this);
						scrollToItem(lastHighlighted);
					} catch (Exception e) {
					}

					return true;
				}
			});
		} else {
			for (int i = 0; i < menuItems.size(); i++) {
				if (menuItems.elementAt(i) instanceof ClickableMenuElement) {
					ClickableMenuElement item = (ClickableMenuElement) menuItems.elementAt(i);
					if (item.isDisabled()) continue;

					highlightElement(item);
					scrollToItem(lastHighlighted);
					selectedIndex = i;

					break;
				}
			}
		}
	}

	public void setSelected(int index) {
		try {
			if (menuItems.elementAt(index) instanceof ClickableMenuElement) {
				ClickableMenuElement item = (ClickableMenuElement) menuItems.elementAt(index);
				if (item.isDisabled()) return;

				highlightElement(item);
				selectedIndex = index;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void highlightElement(ClickableMenuElement el) {
		el.showHelmet();
		lastHighlighted = el;
	}

	public void onScroll(double percent) {
	}

	@Override
	public void onElementHighlight(MenuElement el) {
		lastHighlighted = el;

		int index = menuItems.indexOf(el);
		if (index != -1)
			selectedIndex = index;
	}

	public void setIsTextScreen(boolean isTextScreen) {
		this.isTextScreen = isTextScreen;
	}

	public void setBeforeShowAction(Runnable beforeShowAction) {
		this.beforeShowAction = beforeShowAction;
	}

	public void performBeforeShowAction() {
		if (beforeShowAction != null) {
			beforeShowAction.run();
		}
	}

	public void setBuilder(BiFunction<AMenuScreen, MenuData, AMenuScreen> builder) {
		this.builder = builder;
	}

	public AMenuScreen build() {
		return build(null);
	}

	public AMenuScreen build(MenuData data) {
		if (builder != null) {
			return builder.apply(this, data);
		}
		return this;
	}
}
