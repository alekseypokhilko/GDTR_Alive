package org.happysanta.gdtralive.android;

import android.graphics.Typeface;

public class Global {

	public static Typeface robotoCondensedTypeface;

	static {
		//todo from mod
		robotoCondensedTypeface = Typeface.createFromAsset(Helpers.getGDActivity().getAssets(), "RobotoCondensed-Regular.ttf");
	}

}
