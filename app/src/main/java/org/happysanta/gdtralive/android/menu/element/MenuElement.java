package org.happysanta.gdtralive.android.menu.element;

import android.view.View;

/**
 * Author: ch1p
 */
public interface MenuElement {

	boolean isSelectable();

	View getView();

	void setText(String text);

	void performAction(int k);

}
