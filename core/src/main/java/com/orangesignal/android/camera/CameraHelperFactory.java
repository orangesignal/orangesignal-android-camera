/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.content.Context;
import android.os.Build;

/**
 * {@link CameraHelper} を生成するファクトリークラスを提供します。
 * 
 * @author 杉澤 浩二
 */
public final class CameraHelperFactory {

	/**
	 * Android 4.2 の API レベルを表します。
	 */
	private static final int JELLY_BEAN_MR1 = 17;

	/**
	 * Android 4.0.3 の API レベルを表します。
	 */
	private static final int ICE_CREAM_SANDWICH_MR1 = 15;

	/**
	 * Android 4.0 の API レベルを表します。
	 */
	private static final int ICE_CREAM_SANDWICH = 14;

	/**
	 * Android 3.0 の API レベルを表します。
	 */
	private static final int HONYCOMB = 11;

	/**
	 * Android 2.3 の API レベルを表します。
	 */
	private static final int GINGERBREAD = 9;

	/**
	 * Android 2.2 の API レベルを表します。
	 */
	private static final int FROYO = 8;

	/**
	 * Android 2.0 の API レベルを表します。
	 */
	private static final int ECLAIR = 5;

	/**
	 * Android 1.6 の API レベルを表します。
	 */
	private static final int DONUT = 4;

	/**
	 * Android 1.5 の API レベルを表します。
	 */
	private static final int CUPCAKE = 3;

	/**
	 * {@link CameraHelper} を生成して返します。
	 * 
	 * @param context コンテキスト
	 * @return {@link CameraHelper} の新しいインスタンス
	 */
	public static CameraHelper newCameraHelper(final Context context) {
		// Build.VERSION.SDK_INT は Android 1.6 (API レベル 4) から使用可能なので使用しません。
		@SuppressWarnings("deprecation")
		final int version = Integer.parseInt(Build.VERSION.SDK);
		if (version >= JELLY_BEAN_MR1) {
			return new CameraHelperJellyBeanMR1(context);
		} else if (version >= ICE_CREAM_SANDWICH_MR1) {
			return new CameraHelperICSMR1(context);
		} else if (version >= ICE_CREAM_SANDWICH) {
			return new CameraHelperICS(context);
		} else if (version >= HONYCOMB) {
			return new CameraHelperHonycomb(context);
		} else if (version >= GINGERBREAD) {
			return new CameraHelperGingerbread(context);
		} else if (version >= FROYO) {
			return new CameraHelperFroyo(context);
		} else if (version >= ECLAIR) {
			return new CameraHelperEclair(context);
		} else if (version >= DONUT) {
			return new CameraHelperDonut(context);
		} else if (version >= CUPCAKE) {
			return new CameraHelperCupcake(context);
		}
		return new CameraHelperBase(context);
	}

	/**
	 * インスタンス化できないことを強制します。
	 */
	private CameraHelperFactory() {}

}