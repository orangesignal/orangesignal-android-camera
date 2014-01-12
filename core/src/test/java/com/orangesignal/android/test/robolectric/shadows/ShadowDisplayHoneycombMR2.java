package com.orangesignal.android.test.robolectric.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import android.graphics.Point;
import android.view.Display;

@Implements(Display.class)
public class ShadowDisplayHoneycombMR2 extends ShadowDisplayHoneycomb {

	@Implementation
	public void getSize(final Point point) {
		point.x = getWidth();
		point.y = getHeight();
	}

}
