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

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import com.orangesignal.android.test.robolectric.RobolectricHoneycombTestRunner;
import com.orangesignal.android.test.robolectric.shadows.ShadowDisplayHoneycomb;

@RunWith(RobolectricHoneycombTestRunner.class)
public final class DisplayHelperHoneycombTest {

	private ShadowDisplayHoneycomb mShadowDisplay;

	@Before
	public void setUp() {
		final WindowManager wm = (WindowManager) Robolectric.application.getSystemService(Context.WINDOW_SERVICE);
		mShadowDisplay = (ShadowDisplayHoneycomb) Robolectric.shadowOf(wm.getDefaultDisplay());
		mShadowDisplay.setWidth(320);
		mShadowDisplay.setHeight(480);
		mShadowDisplay.setRawWidth(320 + 100);
		mShadowDisplay.setRawHeight(480 + 100);
	}

	@Test
	public void testGetContext() {
		assertNotNull(new DisplayHelperHoneycomb(Robolectric.application).getContext());
	}

	@Test
	public void testGetRawDisplaySize() {
		final Point displaySize = new DisplayHelperHoneycomb(Robolectric.application).getRawDisplaySize();
		assertNotNull(displaySize);
		assertEquals(displaySize.x, mShadowDisplay.getRawWidth());
		assertEquals(displaySize.y, mShadowDisplay.getRawHeight());
	}

}
