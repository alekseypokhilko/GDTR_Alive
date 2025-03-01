package org.happysanta.gdtralive.game.api.menu;

/**
 * Author: ch1p
 */
public interface MenuElement<T> {

	boolean isSelectable();

	T getView();

	void setText(String text);

	void performAction(int k);

	default void showHelmet() {
	}
}
