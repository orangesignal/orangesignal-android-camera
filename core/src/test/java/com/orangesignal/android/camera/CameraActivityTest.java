/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class CameraActivityTest {

	private CameraActivity mCameraActivity;

	@Before
	public void setUp() throws Exception {
		mCameraActivity = Robolectric.buildActivity(CameraActivity.class).create().get();
	}

	@Test
	public void testHoge() {
		
	}

}
