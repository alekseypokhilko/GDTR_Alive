package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.logDebug;

import android.view.View;

import org.happysanta.gdtralive.android.menu.api.MenuElement;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.android.menu.MenuHandler;
import org.happysanta.gdtralive.android.menu.AMenuScreen;
import org.happysanta.gdtralive.game.KeyboardHandler;

public class MenuItem extends ClickableMenuElement
		implements MenuElement<View> {

	public int x;
	public int y;
	private int value;
	protected AMenuScreen screen;
	protected MenuHandler handler;
	protected ActionHandler action;

	public MenuItem(String text, AMenuScreen screen, MenuHandler handler, ActionHandler action) {
		this.text = text + ">";
		this.screen = screen;
		this.handler = handler;
		this.action = action;

		createAllViews();
	}

	public MenuItem(String text, AMenuScreen screen, MenuHandler handler) {
		this.text = text + ">";
		this.screen = screen;
		this.handler = handler;
		this.action = null;

		createAllViews();
	}

	@Override
	public void setText(String s) {
		super.setText(s + ">");
	}

	// @Override
	public void performAction(int k) {
		logDebug("SimpleMenuElementNew performAction k = " + k);

		switch (k) {
			case KeyboardHandler.KEY_FIRE:
			case KeyboardHandler.KEY_RIGHT:
				if (action != null) {
					action.handle(this);
				}
				handler.handleAction(this);
				screen.setParent(handler.getCurrentMenu());
				handler.setCurrentMenu(screen);
				break;
		}
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
