/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.os.Build;

/**
 * {@link PreviewTexture} を生成するファクトリークラスを提供します。
 * 
 * @author 杉澤 浩二
 */
public final class GLES20PreviewTextureFactory {

	/**
	 * Android 3.0 の API レベルを表します。
	 */
	private static final int HONYCOMB = 11;

	/**
	 * {@link PreviewTexture} を生成して返します。
	 * 
	 * @param texName テクスチャ識別子
	 * @return {@link PreviewTexture} の新しいインスタンス
	 */
	public static PreviewTexture newPreviewTexture(final int texName) {
		// Build.VERSION.SDK_INT は Android 1.6 (API レベル 4) から使用可能なので使用しません。
		final int version = Integer.parseInt(Build.VERSION.SDK);
		if (version >= HONYCOMB) {
			return new GLES20SurfaceTexture(texName);
		}
		return new GLES20PreviewTexture(texName);
	}

	/**
	 * インスタンス化できない事を強制します。
	 */
	private GLES20PreviewTextureFactory() {}

}