/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;

/**
 * Android 1.6 (API レベル 4) 対応の実装を提供します。<p>
 * このバージョンの対応は以下の通りです。
 * <ul>
 * <li>SHARP カメラ拡張</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 * @see https://sh-dev.sharp.co.jp/android/modules/sdk/index.php?/sdk
 */
@TargetApi(Build.VERSION_CODES.DONUT)
class CameraHelperDonut extends CameraHelperCupcake {

	/**
	 * Sharp 製 Android アドオン提供の {@link Camera} クラス拡張 {@link jp.co.sharp.android.hardware.CameraEx} クラスです。
	 */
	protected static final Class<? extends Camera> sSharpCameraClass = getSharpCameraClass();

	@SuppressWarnings("unchecked")
	private static Class<? extends Camera> getSharpCameraClass() {
		try {
			return (Class<? extends Camera>) Class.forName("jp.co.sharp.android.hardware.CameraEx");
		} catch (final ClassNotFoundException e) {
			return null;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperDonut(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * この実装は常に {@code 1} を返します。
	 */
	@Override
	public int getNumberOfCameras() {
		if (sSharpCameraClass != null) {
			return 2;
		}
		return super.getNumberOfCameras();
	}

	@Override
	public CameraInfoCompat getCameraInfo() {
		if (sSharpCameraClass != null) {
			final CameraInfoCompat info = new CameraInfoCompat();
			if (getCameraId() == DEFAULT_CAMERA_ID) {
				info.facing = CameraInfoCompat.CAMERA_FACING_BACK;
				info.orientation = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 90 : 0;
			} else {
				info.facing = CameraInfoCompat.CAMERA_FACING_FRONT;
				info.orientation = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 270 : 180;
			}
			return info;
		}
		return super.getCameraInfo();
	}

	//////////////////////////////////////////////////////////////////////////
	// カメラの接続・切断

	@Override
	public void openCamera(final int cameraId) {
		releaseCamera();

		if (sSharpCameraClass != null) {
			final Method openMethod;
			try {
				openMethod = sSharpCameraClass.getMethod("open", int.class);
			} catch (final NoSuchMethodException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			try {
				setCamera((Camera) openMethod.invoke(null, cameraId));
			} catch (final IllegalArgumentException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (final InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else if (cameraId != DEFAULT_CAMERA_ID) {
			throw new RuntimeException();
		} else {
			setCamera(Camera.open());
		}

		setCameraId(cameraId);
		initializeFocusMode();
	}

}