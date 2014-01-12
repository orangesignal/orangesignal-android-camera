package com.orangesignal.android.test.robolectric.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowDisplay;

import android.view.Display;

@Implements(Display.class)
public class ShadowDisplayHoneycomb extends ShadowDisplay {

	private int rawWidth;
	private int rawHeight;

	public ShadowDisplayHoneycomb() {
		rawWidth = getWidth();
		rawHeight = getHeight();
	}

	@Implementation
	public int getRawWidth() {
		return rawWidth;
	}

	@Implementation
	public int getRawHeight() {
		return rawHeight;
	}

	@Override
	public void setWidth(final int width) {
		super.setWidth(width);
		if (width > getRawWidth()) {
			setRawWidth(width);
		}
	}

	@Override
	public void setHeight(final int height) {
		super.setHeight(height);
		if (height > getRawHeight()) {
			setRawHeight(height);
		}
	}

	public void setRawWidth(final int rawWidth) {
		this.rawWidth = rawWidth;
		if (getWidth() > rawWidth) {
			setWidth(rawWidth);
		}
	}

	public void setRawHeight(final int rawHeight) {
		this.rawHeight = rawHeight;
		if (getHeight() > rawHeight) {
			setHeight(rawHeight);
		}
	}

}
