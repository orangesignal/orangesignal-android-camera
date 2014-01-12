/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Build;

/**
 * {@link DisplayHelperFactory} のテストクラスです。
 * 
 * @author 杉澤 浩二
 * @deprecated Robolectric の coming soon 機能実装版なのでこのクラスのテストは全てスキップされます。
 */
@Deprecated
@RunWith(RobolectricTestRunner.class)
public final class DisplayHelperFactoryTest {

	@Ignore("@Config の emulateSdk は coming soon 機能とのことなのでいまはまだ実行しない")
	@Config(emulateSdk = Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Test
	public void testNewDisplayHelperJELLY_BEAN_MR1() {
		final DisplayHelper result = DisplayHelperFactory.newDisplayHelper(Robolectric.application);
		assertNotNull(result);
		assertThat(result, instanceOf(DisplayHelperHoneycombMR2.class));
	}

	@Ignore("@Config の emulateSdk は coming soon 機能とのことなのでいまはまだ実行しない")
	@Config(emulateSdk = Build.VERSION_CODES.HONEYCOMB)
	@Test
	public void testNewDisplayHelperHONEYCOMB() {
		final DisplayHelper result = DisplayHelperFactory.newDisplayHelper(Robolectric.application);
		assertNotNull(result);
		assertThat(result, instanceOf(DisplayHelperHoneycomb.class));
	}

	@Ignore("@Config の emulateSdk は coming soon 機能とのことなのでいまはまだ実行しない")
	@Config(emulateSdk = Build.VERSION_CODES.FROYO)
	@Test
	public void testNewDisplayHelperFROYO() {
		final DisplayHelper result = DisplayHelperFactory.newDisplayHelper(Robolectric.application);
		assertNotNull(result);
		assertThat(result, instanceOf(DisplayHelperFroyo.class));
	}

}
