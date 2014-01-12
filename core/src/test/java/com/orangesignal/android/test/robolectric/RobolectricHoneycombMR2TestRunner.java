/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.test.robolectric;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.runners.model.InitializationError;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.bytecode.ShadowMap;

import com.orangesignal.android.test.robolectric.shadows.ShadowDisplayHoneycombMR2;

import android.os.Build;

/**
 * Android OS 3.2 (API レベル 13) 環境の {@link RobolectricTestRunner} を提供します。
 * 
 * @author 杉澤 浩二
 */
public final class RobolectricHoneycombMR2TestRunner extends RobolectricTestRunner {

	public RobolectricHoneycombMR2TestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

	@Override
	protected ShadowMap createShadowMap() {
		return new ShadowMap.Builder()
				.addShadowClass(ShadowSystemProperties.class)
				.addShadowClass(ShadowDisplayHoneycombMR2.class)
				.build();
	}

	// このクラスのスコープは public でなければなりません
	@Implements(value = Robolectric.Anything.class, className = "android.os.SystemProperties")
	public static class ShadowSystemProperties {

		private static final Map<String, Object> VALUES = new HashMap<String, Object>();
		private static final Set<String> alreadyWarned = new HashSet<String>();

		static {
			VALUES.put("ro.build.version.sdk", Build.VERSION_CODES.HONEYCOMB_MR2);
			VALUES.put("ro.debuggable", 0);
			VALUES.put("ro.secure", 1);
			VALUES.put("log.closeguard.Animation", false);
			VALUES.put("debug.choreographer.vsync", false); // disable vsync for
															// Choreographer
		}

		@Implementation
		public static String get(final String key) {
			final Object o = VALUES.get(key);
			if (o == null) {
				warnUnknown(key);
				return null;
			}
			return o.toString();
		}

		@Implementation
		public static String get(final String key, final String def) {
			final Object value = VALUES.get(key);
			return value == null ? def : value.toString();
		}

		@Implementation
		public static int getInt(final String key, final int def) {
			final Object value = VALUES.get(key);
			return value == null ? def : (Integer) value;
		}

		@Implementation
		public static long getLong(final String key, final long def) {
			final Object value = VALUES.get(key);
			return value == null ? def : (Long) value;
		}

		@Implementation
		public static boolean getBoolean(final String key, final boolean def) {
			final Object value = VALUES.get(key);
			return value == null ? def : (Boolean) value;
		}

		synchronized private static void warnUnknown(final String key) {
			if (alreadyWarned.add(key)) {
				System.err.println("WARNING: no system properties value for " + key);
			}
		}
	}

}
