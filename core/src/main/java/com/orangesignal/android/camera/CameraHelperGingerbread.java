/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;

/**
 * Android 2.3 (API レベル 9) 対応の実装を提供します。<p>
 * <ul>
 * <li>{@link Camera#getNumberOfCameras()}</li>
 * <li>{@link Camera#getCameraInfo(int, android.hardware.Camera.CameraInfo)}</li>
 * <li>{@link Camera#open(int)}</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
class CameraHelperGingerbread extends CameraHelperFroyo {

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperGingerbread(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * 実装は単に {@link Camera#getNumberOfCameras()} を呼び出すだけです。
	 */
	@Override
	public int getNumberOfCameras() {
		return Camera.getNumberOfCameras();
	}

	@Override
	public CameraInfoCompat getCameraInfo() {
		final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(getCameraId(), cameraInfo);

		final CameraInfoCompat result = new CameraInfoCompat();
		result.facing = cameraInfo.facing;
		result.orientation = cameraInfo.orientation;
		return result;
	}

	//////////////////////////////////////////////////////////////////////////
	// カメラの接続・切断

	@Override
	public void openCamera(final int cameraId) {
		releaseCamera();

		if (getNumberOfCameras() > 1) {
			setCamera(Camera.open(cameraId));
		} else if (cameraId != DEFAULT_CAMERA_ID) {
			throw new RuntimeException();
		} else {
			setCamera(Camera.open());
		}

		setCameraId(cameraId);
		initializeFocusMode();
	}

	@Override
	public void initializeFocusMode() {
		// 写真撮影に最適なフォーカスモードを設定します。
		final List<String> supportedFocusModes = getSupportedFocusModes();
		if (supportedFocusModes != null) {
			// 動画向けの連続フォーカスをサポートしている場合は設定します。
			if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				final Camera.Parameters parameters = getCamera().getParameters();
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				try {
					getCamera().setParameters(parameters);
				} catch (final RuntimeException e) {}	// 無視する
			} else {
				super.initializeFocusMode();
			}
		}
	}

}
