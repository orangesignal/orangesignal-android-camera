/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.orangesignal.android.camera.CameraHelper;

/**
 * Android 2.0 以降向けの {@link PreviewSurfaceHelper} の実装を提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
class PreviewSurfaceHelperEclair implements PreviewSurfaceHelper {

	/**
	 * カメラ操作ヘルパーを保持します。
	 */
	private CameraHelper mCameraHelper;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタです。

	/**
	 * コンストラクタです。
	 * 
	 * @param camera カメラ操作ヘルパー
	 */
	public PreviewSurfaceHelperEclair(final CameraHelper camera) {
		mCameraHelper = camera;
	}

	//////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("deprecation")
	@Override
	public SurfaceView createPushBufferSurfaceViewIfNeed(final Context context) {
		final SurfaceView surface = new SurfaceView(context);
		surface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surface.setKeepScreenOn(true);
		surface.setWillNotDraw(true);
		return surface;
	}

	@Override
	public void setZOrderMediaOverlay(final SurfaceView surface) {
		surface.setZOrderMediaOverlay(true);
	}

	@Override
	public void setPreviewDisplay(final SurfaceHolder holder) throws IOException {
		mCameraHelper.setPreviewDisplay(holder);
	}

}