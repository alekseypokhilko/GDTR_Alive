package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.android.Global;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.api.MenuElement;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.android.menu.MenuHandler;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.KeyboardHandler;

public class BadgeWithTextElement implements MenuElement<View> {

	protected static final int TEXT_SIZE = 20;
	private final MenuHandler handler;
	private final ActionHandler action;

	protected LinearLayout textView;
	protected MenuTextView optionTextView;

	public BadgeWithTextElement(int badgeId, String title, MenuHandler handler, ActionHandler action ) {
		this.handler = handler;
		this.action = action;
		textView = new LinearLayout(getGDActivity());
		textView.setOrientation(LinearLayout.HORIZONTAL);
		textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		textView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		));

		optionTextView = new MenuTextView(getGDActivity());
		optionTextView.setText(title);
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

		MenuImageView badge = new MenuImageView(Helpers.getGDActivity());
		badge.setImageResource(badgeId);
		badge.setScaleType(ImageView.ScaleType.CENTER);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(0, 0, getDp(MenuAction.LOCK_IMAGE_MARGIN_RIGHT), 0);
		badge.setLayoutParams(lp);

		Helpers.getModManager().registerThemeReloadHandler(this::onThemeReload);
		textView.addView(badge);
		textView.addView(optionTextView);
	}

	@Override
	public View getView() {
		return textView;
	}

	@Override
	public void setText(String text) {
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	@Override
	public void performAction(int k) {
		if (k == KeyboardHandler.KEY_FIRE) {
			if (action != null) {
				action.handle(this);
			}
			if (handler != null) { //todo remove
				handler.handleAction(this);
			}
		}
	}

	public void onThemeReload() {
        optionTextView.setTextColor(Helpers.getModManager().getInterfaceTheme().getTextColor());
	}
}
