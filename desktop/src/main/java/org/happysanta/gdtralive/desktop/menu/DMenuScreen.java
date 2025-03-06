package org.happysanta.gdtralive.desktop.menu;

import org.happysanta.gdtralive.DesktopGdView;
import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.element.ClickableMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.MenuActionElement;
import org.happysanta.gdtralive.game.api.menu.element.OnMenuElementHighlightListener;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.util.BiFunction;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;

public class DMenuScreen<T> implements OnMenuElementHighlightListener<T>, MenuScreen<T> {
	protected MenuScreen<T> parent;

	protected String title;
	protected int selectedIndex;
	protected Vector menuItems;
	protected JPanel layout;
	protected MenuElement<T> lastHighlighted;
	protected boolean isTextScreen = false;
	protected Runnable beforeShowAction;
	protected BiFunction<MenuScreen<T>, MenuData, MenuScreen<T>> builder;
	protected Map<Integer, MenuElement<T>> actions = new HashMap<>();

	public DMenuScreen(String title, MenuScreen<T> parent) {
		this.title = title;
		selectedIndex = -1;
		menuItems = new Vector();
		this.parent = parent;

		layout = new JPanel();
		layout.setLayout(new GridLayout(20,1,1,1));
		layout.setOpaque(false);
		layout.setBackground(new Color(0,0,0,0));
		layout.setBounds(0, 0, DesktopGdView.width, DesktopGdView.height);
	}

	public void add(MenuElement<T> item) {
		if (item instanceof MenuActionElement) {
			MenuActionElement action = (MenuActionElement) item;
			actions.put(action.getActionValue(), action);
		}

		if (item != null) {
			layout.add((Component) item.getView());
			menuItems.add(item);
		}

		if (item instanceof ClickableMenuElement)
			((ClickableMenuElement) item).setOnHighlightListener(this);
	}

	protected void scrollToItem(MenuElement<T> item) {

	}

	@Override
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
//					getGDActivity().scrollTextMenuUp();
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
//					getGDActivity().scrollTextMenuDown();
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

	public MenuScreen<T> getParent() {
		return parent;
	}

	@Override
	public MenuScreen<T> setParent(MenuScreen<T> target) {
		parent = target;
		return this;
	}

	@Override
	public void clear() {
		menuItems.removeAllElements();
		for (Component component : layout.getComponents()) {
			layout.remove(component);
		}

		selectedIndex = -1;
		lastHighlighted = null;
	}

	public T getLayout() {
		return (T)layout;
	}

	@Override
	public void setTitle(String s) {
		title = s;
	}

	protected void updateTitle() {
//		gd.runOnUiThread(() -> {
//			gd.menuTitleTextView.setText(title);
//			// activity.menuTitleTextView.invalidate();
//			gd.titleLayout.invalidate();
//		});
	}

	@Override
	public MenuElement<T> getActions(int action) {
		return actions.get(action);
	}

	@Override
	public void onShow() {
		updateTitle();
		highlightElement();
	}

	@Override
	public void resetHighlighted() {
		lastHighlighted = null;
	}

	@Override
	public void highlightElement() {
		if (lastHighlighted != null) {
			lastHighlighted.showHelmet();
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

	@Override
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

	public void highlightElement(MenuElement<T> el) {
		el.showHelmet();
		lastHighlighted = el;
	}

	@Override
	public void onScroll(double percent) {
	}

	@Override
	public void onElementHighlight(MenuElement el) {
		lastHighlighted = el;

		int index = menuItems.indexOf(el);
		if (index != -1)
			selectedIndex = index;
	}

	@Override
	public void setIsTextScreen(boolean isTextScreen) {
		this.isTextScreen = isTextScreen;
	}

	@Override
	public MenuScreen<T> setBeforeShowAction(Runnable beforeShowAction) {
		this.beforeShowAction = beforeShowAction;
		return this;
	}

	@Override
	public void performBeforeShowAction() {
		if (beforeShowAction != null) {
			beforeShowAction.run();
		}
	}

	@Override
	public MenuScreen<T> builder(BiFunction<MenuScreen<T>, MenuData, MenuScreen<T>> builder) {
		this.builder = builder;
		return this;
	}

	@Override
	public MenuScreen<T> build() {
		return build(null);
	}

	@Override
	public MenuScreen<T> build(MenuData data) {
		if (builder != null) {
			return builder.apply(this, data);
		}
		return this;
	}
}
