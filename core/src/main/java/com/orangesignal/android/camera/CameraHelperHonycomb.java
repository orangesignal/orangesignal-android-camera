/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;

/**
 * Android 3.0 (API レベル 11) 対応の実装を提供します。<p>
 * <ul>
 * <li>{@link Camera#setPreviewTexture(SurfaceTexture)}</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class CameraHelperHonycomb extends CameraHelperGingerbread {

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperHonycomb(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・停止

	@Override
	public void setPreviewTexture(final Object surfaceTexture) throws IOException {
		getCamera().setPreviewTexture((SurfaceTexture) surfaceTexture);
	}

}
