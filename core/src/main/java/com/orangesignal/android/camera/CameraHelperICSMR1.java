/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;

/**
 * Android 4.0.3 (API レベル 15) 対応の実装を提供します。<p>
 * <ul>
 * <li>{@link Camera.Parameters#isVideoStabilizationSupported()}</li>
 * <li>{@link Camera.Parameters#setVideoStabilization(boolean)}</li>
 * <li>{@link Camera.Parameters#getVideoStabilization()}</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
class CameraHelperICSMR1 extends CameraHelperICS {

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperICSMR1(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////
	// Video Stabilization

	/**
	 * この実装は単に {@link Camera.Parameters#isVideoStabilizationSupported()} を呼び出して戻り値を返します。
	 */
	@Override
	public boolean isVideoStabilizationSupported() {
		return getCamera().getParameters().isVideoStabilizationSupported();
	}

	/**
	 * この実装は指定されたパラメータで {@link Camera.Parameters#setVideoStabilization(boolean)} を呼び出します。
	 */
	@Override
	public void setVideoStabilization(final boolean toggle) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setVideoStabilization(toggle);
		getCamera().setParameters(params);
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getVideoStabilization()} を呼び出して戻り値を返します。
	 */
	@Override
	public boolean getVideoStabilization() {
		return getCamera().getParameters().getVideoStabilization();
	}

}