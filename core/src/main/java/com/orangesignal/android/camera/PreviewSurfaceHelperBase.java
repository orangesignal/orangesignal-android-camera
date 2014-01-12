/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.orangesignal.android.camera.CameraHelper;

/**
 * {@link PreviewSurfaceHelper} の既定の実装を提供します。
 * 
 * @author 杉澤 浩二
 */
class PreviewSurfaceHelperBase implements PreviewSurfaceHelper {

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
	public PreviewSurfaceHelperBase(final CameraHelper camera) {
		mCameraHelper = camera;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * この実装は常に {@code null} を返します。
	 */
	@Override
	public SurfaceView createPushBufferSurfaceViewIfNeed(final Context context) {
		return null;
	}

	/**
	 * この実装は何も行いません。
	 */
	@Override
	public void setZOrderMediaOverlay(final SurfaceView surface) {
	}

	/**
	 * この実装は単に {@link CameraHelper#setPreviewDisplay(SurfaceHolder)} に {@code null} を設定します。
	 */
	@Override
	public void setPreviewDisplay(final SurfaceHolder holder) throws IOException {
		mCameraHelper.setPreviewDisplay(null);
	}

}