/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.os.Build;

/**
 * {@link PreviewSurfaceHelper} を生成するファクトリークラスを提供します。
 * 
 * @author 杉澤 浩二
 */
public final class PreviewSurfaceHelperFactory {

	/**
	 * Android 2.0 の API レベルを表します。
	 */
	private static final int ECLAIR = 5;

	/**
	 * {@link PreviewSurfaceHelper} を生成して返します。
	 * 
	 * @return {@link PreviewSurfaceHelper} の新しいインスタンス
	 */
	public static PreviewSurfaceHelper newPreviewSurfaceHelper(final CameraHelper camera) {
		// Build.VERSION.SDK_INT は Android 1.6 (API レベル 4) から使用可能なので使用しません。
		@SuppressWarnings("deprecation")
		final int version = Integer.parseInt(Build.VERSION.SDK);
		if (version >= ECLAIR) {
			return new PreviewSurfaceHelperEclair(camera);
		}
		return new PreviewSurfaceHelperBase(camera);
	}

	/**
	 * インスタンス化できないことを強制します。
	 */
	private PreviewSurfaceHelperFactory() {}

}