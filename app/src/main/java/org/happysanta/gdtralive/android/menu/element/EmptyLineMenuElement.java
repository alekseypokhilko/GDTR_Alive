package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.view.View;
import android.view.ViewGroup;

import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;

public class EmptyLineMenuElement implements MenuElement<View> {

	protected String text;
	protected int offset;
	protected MenuTextView view;

	public EmptyLineMenuElement(int offset) {
		this.offset = offset;

		view = new MenuTextView(getGDActivity());
		view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDp(offset)));
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	@Override
	public void setText(String s) {
		text = s;
	}

	@Override
	public void performAction(int k) {
	}

}
