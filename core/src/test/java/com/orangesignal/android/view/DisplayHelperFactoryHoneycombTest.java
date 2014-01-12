/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import com.orangesignal.android.test.robolectric.RobolectricHoneycombTestRunner;

/**
 * Android 3.0 (API レベル 11) 環境における {@link DisplayHelperFactory} のテストクラスです。
 * 
 * @author 杉澤 浩二
 */
@RunWith(RobolectricHoneycombTestRunner.class)
public final class DisplayHelperFactoryHoneycombTest {

	@Test
	public void testNewDisplayHelper() {
		final DisplayHelper result = DisplayHelperFactory.newDisplayHelper(Robolectric.application);
		assertNotNull(result);
		assertThat(result, instanceOf(DisplayHelperHoneycomb.class));
	}

}
