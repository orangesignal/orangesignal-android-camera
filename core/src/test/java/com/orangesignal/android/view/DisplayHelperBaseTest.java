/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowDisplay;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * {@link DisplayHelperBase} のテストクラスです。
 * 
 * @author 杉澤 浩二
 */
@RunWith(RobolectricTestRunner.class)
public final class DisplayHelperBaseTest {

	private ShadowDisplay mShadowDisplay;

	@Before
	public void setUp() {
		final WindowManager wm = (WindowManager) Robolectric.application.getSystemService(Context.WINDOW_SERVICE);
		mShadowDisplay = Robolectric.shadowOf(wm.getDefaultDisplay());
		mShadowDisplay.setWidth(320);
		mShadowDisplay.setHeight(480);
		mShadowDisplay.setRotation(90);
	}

	@Test
	public void testGetContext() {
		assertNotNull(new DisplayHelperBase(Robolectric.application).getContext());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetDefaultDisplay() {
		final Display display = new DisplayHelperBase(Robolectric.application).getDefaultDisplay();
		assertNotNull(display);
		assertEquals(display.getOrientation(), mShadowDisplay.getOrientation());
		assertEquals(display.getWidth(), mShadowDisplay.getWidth());
		assertEquals(display.getHeight(), mShadowDisplay.getHeight());
	}

	@Test
	public void testGetDisplayAngle() {
		assertEquals(new DisplayHelperBase(Robolectric.application).getDisplayAngle(), mShadowDisplay.getOrientation());
	}

	@Test
	public void testGetDisplaySize() {
		final Point displaySize = new DisplayHelperBase(Robolectric.application).getDisplaySize();
		assertNotNull(displaySize);
		assertEquals(displaySize.x, mShadowDisplay.getWidth());
		assertEquals(displaySize.y, mShadowDisplay.getHeight());
	}

	@Test
	public void testGetRawDisplaySize() {
		final Point displaySize = new DisplayHelperBase(Robolectric.application).getRawDisplaySize();
		assertNotNull(displaySize);
		assertEquals(displaySize.x, mShadowDisplay.getWidth());
		assertEquals(displaySize.y, mShadowDisplay.getHeight());
	}

}
