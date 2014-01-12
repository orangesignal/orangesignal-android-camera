/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import com.orangesignal.android.camera.CameraHelper;

import android.content.Context;
import android.os.Build;

/**
 * {@link DisplayHelper} を生成するファクトリークラスを提供します。
 * 
 * @author 杉澤 浩二
 */
public final class DisplayHelperFactory {

	/**
	 * Android 3.2 の API レベルを表します。
	 */
	private static final int HONEYCOMB_MR2 = 13;

	/**
	 * Android 3.0 の API レベルを表します。
	 */
	private static final int HONEYCOMB = 11;

	/**
	 * Android 2.2 の API レベルを表します。
	 */
	private static final int FROYO = 8;

	/**
	 * {@link DisplayHelper} を生成して返します。
	 * 
	 * @param context コンテキスト
	 * @return {@link CameraHelper} の新しいインスタンス
	 */
	public static DisplayHelper newDisplayHelper(final Context context) {
		// Build.VERSION.SDK_INT は Android 1.6 (API レベル 4) から使用可能なので使用しません。
		@SuppressWarnings("deprecation")
		final int version = Integer.parseInt(Build.VERSION.SDK);
		if (version >= HONEYCOMB_MR2) {
			return new DisplayHelperHoneycombMR2(context);
		} else if (version >= HONEYCOMB) {
			return new DisplayHelperHoneycomb(context);
		} else if (version >= FROYO) {
			return new DisplayHelperFroyo(context);
		}
		return new DisplayHelperBase(context);
	}

	/**
	 * インスタンス化できないことを強制します。
	 */
	private DisplayHelperFactory() {}

}