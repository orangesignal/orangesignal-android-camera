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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowDisplay;

import android.content.Context;
import android.view.Surface;
import android.view.WindowManager;

/**
 * {@link DisplayHelperFroyo} のテストクラスです。
 * 
 * @author 杉澤 浩二
 */
@RunWith(RobolectricTestRunner.class)
public final class DisplayHelperFroyoTest {

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
		assertNotNull(new DisplayHelperFroyo(Robolectric.application).getContext());
	}

	@Test
	public void testGetDisplayAngle() {
		final DisplayHelperFroyo helper = new DisplayHelperFroyo(Robolectric.application);

		mShadowDisplay.setRotation(Surface.ROTATION_0);
		assertEquals(helper.getDisplayAngle(), 0);
		mShadowDisplay.setRotation(Surface.ROTATION_90);
		assertEquals(helper.getDisplayAngle(), 90);
		mShadowDisplay.setRotation(Surface.ROTATION_180);
		assertEquals(helper.getDisplayAngle(), 180);
		mShadowDisplay.setRotation(Surface.ROTATION_270);
		assertEquals(helper.getDisplayAngle(), 270);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetDisplayAngleIllegalStateException() {
		mShadowDisplay.setRotation(-1);
		new DisplayHelperFroyo(Robolectric.application).getDisplayAngle();
	}

}
