/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Android 2.2 (API レベル 8) 対応の実装を提供します。<p>
 * このバージョンの対応は以下の通りです。
 * <ul>
 * <li>{@link Camera#setPreviewCallbackWithBuffer(android.hardware.Camera.PreviewCallback)}</li>
 * <li>{@link Camera#addCallbackBuffer(byte[])}</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
class CameraHelperFroyo extends CameraHelperEclair implements Camera.OnZoomChangeListener {

	/**
	 * プレビューコールバック用のバッファを保持します。
	 */
	private byte[] mBuffer;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperFroyo(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・停止

	/**
	 * この実装は単に {@link Camera#setDisplayOrientation(int)} を呼び出すだけです。
	 */
	@Override
	public void setDisplayOrientation(final int degrees) {
		getCamera().setDisplayOrientation(degrees);
	}

	@Override
	public int getOrientation() {
		final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();

		final int degrees;
		switch (display.getRotation()) {
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_0:
			default:
				degrees = 0;
				break;
		}

		int result;
		final CameraInfoCompat info = getCameraInfo();
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
//			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	@Override
	public int getOptimalOrientation() {
		final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();

		final int degrees;
		switch (display.getRotation()) {
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_0:
			default:
				degrees = 0;
				break;
		}

		int result;
		final CameraInfoCompat info = getCameraInfo();
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	/**
	 * この実装は {@link Camera#setPreviewCallbackWithBuffer(android.hardware.Camera.PreviewCallback)} を使用してプレビューコールバックを設定します。
	 */
	@Override
	public void setPreviewCallback(final Camera.PreviewCallback cb) {
		final Camera camera = getCamera();
		if (cb != null) {
			try {
				// コールバックバッファを設定します。
				final Camera.Size previewSize = getPreviewSize();
				final Camera.Size pictureSize = getPictureSize();
				final Camera.Parameters parameters = camera.getParameters();
				mBuffer = new byte[Math.max(
						previewSize.width * previewSize.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8,
						pictureSize.width * pictureSize.height * ImageFormat.getBitsPerPixel(ImageFormat.RGB_565) / 8
					)];
				camera.setPreviewCallbackWithBuffer(cb);
				camera.addCallbackBuffer(mBuffer);
			} catch (final OutOfMemoryError e) {
				// メモリ不足の場合は、以前の方式でプレビューコールバックを設定します。
				mBuffer = null;
				camera.setPreviewCallbackWithBuffer(null);
				super.setPreviewCallback(cb);
			}
		} else {
			mBuffer = null;
			camera.setPreviewCallbackWithBuffer(null);
			camera.setPreviewCallback(null);
		}
	}

	/**
	 * この実装は {@link Camera#addCallbackBuffer(byte[])} を使用してプレビューのコールバックが再度通知されるようにします。
	 */
	@Override
	public void onPreviewFrame(final Camera.PreviewCallback cb) {
		if (mBuffer != null) {
			getCamera().addCallbackBuffer(mBuffer);
		} else {
			super.onPreviewFrame(cb);
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// 露出補正

	/**
	 * この実装は単に {@link Camera.Parameters#getMaxExposureCompensation()} を呼び出して戻り値を返します。
	 */
	@Override
	public int getMaxExposureCompensation() {
		return getCamera().getParameters().getMaxExposureCompensation();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getMinExposureCompensation()} を呼び出して戻り値を返します。
	 */
	@Override
	public int getMinExposureCompensation() {
		return getCamera().getParameters().getMinExposureCompensation();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getExposureCompensationStep()} を呼び出して戻り値を返します。
	 */
	@Override
	public float getExposureCompensationStep() {
		return getCamera().getParameters().getExposureCompensationStep();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getExposureCompensation()} を呼び出して戻り値を返します。
	 */
	@Override
	public int getExposureCompensation() {
		return getCamera().getParameters().getExposureCompensation();
	}

	@Override
	public void setExposureCompensation(final int value) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setExposureCompensation(value);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException  e) {}	// 無視する
	}

	//////////////////////////////////////////////////////////////////////////
	// ズーム

	/**
	 * この実装は単に {@link Camera.Parameters#isZoomSupported()} を呼び出して戻り値を返します。
	 */
	@Override
	public boolean isZoomSupported() {
		return getParameters().isZoomSupported();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getMaxZoom()} を呼び出して戻り値を返します。
	 */
	@Override
	public int getMaxZoom() {
		return getParameters().getMaxZoom();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getZoomRatios()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<Integer> getZoomRatios() {
		return getParameters().getZoomRatios();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getZoom()} を呼び出して戻り値を返します。
	 */
	@Override
	public int getZoom() {
		return getParameters().getZoom();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#setZoom(int)} を呼び出します。
	 */
	@Override
	public void setZoom(final int value) {
		final Camera.Parameters params = getParameters();
		params.setZoom(value);
		getCamera().setParameters(params);
	}

	private OnZoomChangeListener mOnZoomChangeListener;

	/**
	 * この実装は単に {@link Camera#setZoomChangeListener(android.hardware.Camera.OnZoomChangeListener)} を呼び出します。
	 */
	@Override
	public void setZoomChangeListener(final OnZoomChangeListener listener) {
		mOnZoomChangeListener = listener;
		getCamera().setZoomChangeListener(this);
	}

	@Override
	public void onZoomChange(final int zoomValue, final boolean stopped, final Camera camera) {
		if (mOnZoomChangeListener != null) {
			mOnZoomChangeListener.onZoomChange(zoomValue, stopped, this);
		}
	}

	/**
	 * この実装は単に {@link Camera#startSmoothZoom(int)} を呼び出します。
	 */
	@Override
	public void startSmoothZoom(final int value) {
		getCamera().startSmoothZoom(value);
	}

	/**
	 * この実装は単に {@link Camera#stopSmoothZoom()} を呼び出します。
	 */
	@Override
	public void stopSmoothZoom() {
		getCamera().stopSmoothZoom();
	}

}