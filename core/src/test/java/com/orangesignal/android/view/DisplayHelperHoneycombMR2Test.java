/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowDisplay;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import com.orangesignal.android.test.robolectric.RobolectricHoneycombMR2TestRunner;

@RunWith(RobolectricHoneycombMR2TestRunner.class)
public final class DisplayHelperHoneycombMR2Test {

	private ShadowDisplay mShadowDisplay;

	@Before
	public void setUp() {
		final WindowManager wm = (WindowManager) Robolectric.application.getSystemService(Context.WINDOW_SERVICE);
		mShadowDisplay = Robolectric.shadowOf(wm.getDefaultDisplay());
		mShadowDisplay.setWidth(320);
		mShadowDisplay.setHeight(480);
	}

	@Test
	public void testGetContext() {
		assertNotNull(new DisplayHelperHoneycombMR2(Robolectric.application).getContext());
	}

	@Test
	public void testGetDisplaySize() {
		final Point displaySize = new DisplayHelperHoneycombMR2(Robolectric.application).getDisplaySize();
		assertNotNull(displaySize);
		assertEquals(displaySize.x, mShadowDisplay.getWidth());
		assertEquals(displaySize.y, mShadowDisplay.getHeight());
	}

}
