/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

/**
 * {@link CameraHelper} の既定の実装を提供します。
 * 
 * @author 杉澤 浩二
 */
class CameraHelperBase implements CameraHelper, Camera.PictureCallback {

	/**
	 * コンテキストを保持します。
	 */
	private final Context mContext;

	/**
	 * 現在のカメラ ID を保持します。
	 */
	private int mCameraId;

	/**
	 * カメラサービスオブジェクトを保持します。
	 */
	private Camera mCamera;

	/**
	 * 画像キャプチャ用コールバックを一時的に (キャプチャ要求から完了までの間) 保持します。
	 */
	private Camera.PictureCallback mPictureCallback;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperBase(final Context context) {
		mContext = context;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * コンテキストを返します。
	 * 
	 * @return コンテキスト
	 */
	protected final Context getContext() {
		return mContext;
	}

	/**
	 * カメラサービスのインスタンスを返します。
	 * 
	 * @return カメラサービスのインスタンス。または {@code null}
	 */
	protected final Camera getCamera() {
		return mCamera;
	}

	/**
	 * 指定されたカメラサービスのインスタンスを設定します。
	 * 
	 * @param camera カメラサービスのインスタンス
	 */
	protected final void setCamera(final Camera camera) {
		mCamera = camera;
	}

	/**
	 * この実装は常に {@code 1} を返します。
	 */
	@Override
	public int getNumberOfCameras() {
		return 1;
	}

	/**
	 * 現在のカメラ ID を設定します。
	 * 
	 * @param cameraId 現在のカメラ ID
	 */
	protected final void setCameraId(final int cameraId) {
		mCameraId = cameraId;
	}

	@Override
	public final int getCameraId() {
		return mCameraId;
	}

	@Override
	public CameraInfoCompat getCameraInfo() {
		final CameraInfoCompat result = new CameraInfoCompat();
		result.facing = CameraInfoCompat.CAMERA_FACING_BACK;
		result.orientation = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 90 : 0;
		return result;
	}

	/**
	 * この実装は常に {@code false} を返します。
	 */
	@Override
	public final boolean isFaceCamera() {
		return getCameraInfo().facing == CameraInfoCompat.CAMERA_FACING_FRONT;
	}

	//////////////////////////////////////////////////////////////////////////
	// カメラの接続・切断

	@Override
	public final boolean isOpened() {
		return mCamera != null;
	}

	@Override
	public void openCamera(final int cameraId) {
		releaseCamera();

		if (cameraId != DEFAULT_CAMERA_ID) {
			throw new RuntimeException();
		}
		mCamera = Camera.open();
		setCameraId(cameraId);
		initializeFocusMode();
	}

	@Override
	public void nextCamera() {
		openCamera((mCameraId + 1) % getNumberOfCameras());
	}

	/**
	 * この実装は何も行いません。
	 */
	@Override
	public void initializeFocusMode() {
	}

	@Override
	public final void releaseCamera() {
//		synchronized (this) {
			if (mCamera != null) {
				stopPreview();
				mCamera.release();
				mCamera = null;
			}
//		}
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * この実装は単に {@link Camera#setErrorCallback(android.hardware.Camera.ErrorCallback)} を呼び出すだけです。
	 */
	@Override
	public void setErrorCallback(final Camera.ErrorCallback cb) {
		mCamera.setErrorCallback(cb);
	}

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・停止

	@Override
	public final void setupOptimalPreviewSizeAndPictureSize(final int measureWidth, final int measureHeight, final int maxSize) {
		final List<Camera.Size> supportedPreviewSizes = getSupportedPreviewSizes();
		final List<Camera.Size> supportedPictureSizes = getSupportedPictureSizes();

		// プレビューサイズの一覧とピクチャーサイズの一覧をサポートしている場合のみ以下の処理ブロックを実行します。
		if (supportedPreviewSizes != null && supportedPictureSizes != null) {
			// プレビューサイズやピクチャーサイズは横置きを前提としたサイズなので端末が縦置きか横置きかで指定されたサイズを読みかえます。
			int width;
			int height;
			if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				width = measureHeight;
				height = measureWidth;
			} else {
				width = measureWidth;
				height = measureHeight;
			}

			// まずはピクチャーサイズを選定します。
			final Camera.Size pictureSize = getOptimalSize(supportedPictureSizes, width, height, maxSize);
			if (pictureSize != null) {
				width = pictureSize.width;
				height = pictureSize.height;
			}
			// 指定されたサイズに最適なプレビューサイズとピクチャーサイズを求めます。
			final Camera.Size previewSize = getOptimalSize(supportedPreviewSizes, width, height, maxSize);

			// 最適なプレビューサイズとピクチャーサイズが共にある場合は設定します。
			if (previewSize != null && pictureSize != null) {
				final Camera.Parameters parameters = getCamera().getParameters();
				parameters.setPreviewSize(previewSize.width, previewSize.height);
				parameters.setPictureSize(pictureSize.width, pictureSize.height);
				try {
					getCamera().setParameters(parameters);
				} catch (final RuntimeException e) {}	// 無視する
			}
		}
	}

	private static final double ASPECT_TOLERANCE = 0.1D;

	/**
	 * 指定された幅と高さから最適なサイズを返します。
	 * サイズのリストに {@code null} が指定された場合や、最適なサイズが見つからなかった場合は {@code null} を返します。
	 * 
	 * @param sizes サイズのリスト
	 * @param width 基準とする幅
	 * @param height 基準とする高さ
	 * @param maxSize 制限サイズ (オプショナル)
	 * @return 最適なサイズ。または {@code null}
	 * @see ApiDemos の CameraPreview
	 */
	private static Camera.Size getOptimalSize(final List<Camera.Size> sizes, final int width, final int height, final int maxSize) {
		if (sizes == null) {
			return null;
		}

		Camera.Size result = null;
		double minDiff = Double.MAX_VALUE;

		// アスペクト比が一致するサイズを検索します。
		final double targetRatio = (double) width / (double) height;
		for (final Camera.Size size : sizes) {
			// サイズ制限が有効な場合に、制限を超えていれば無視します。
			if (maxSize > 0 && (size.width > maxSize || size.height > maxSize)) {
				continue;
			}
			// アスペクト比が近似値の範囲外であれば無視します。
			final double ratio = (double) size.width / (double) size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
				continue;
			}
			// 指定されたサイズにより近ければ採用します。
			if (Math.abs(size.height - height) < minDiff) {
				result = size;
				minDiff = Math.abs(size.height - height);
			}
		}

		// アスペクト非で一致するサイズが見つからなかった場合
		if (result == null) {
			minDiff = Double.MAX_VALUE;
			for (final Camera.Size size : sizes) {
				// サイズ制限が有効な場合に、制限を超えていれば無視します。
				if (maxSize > 0 && (size.width > maxSize || size.height > maxSize)) {
					continue;
				}
				if (Math.abs(size.height - height) < minDiff) {
					result = size;
					minDiff = Math.abs(size.height - height);
				}
			}
		}

		return result;
	}

	@Override
	public int getOrientation() {
		if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return 90;
		}
		return 0;
	}

	@Override
	public int getOptimalOrientation() {
		return getOrientation();
	}

	/**
	 * この実装は可能であれば {@link Camera.Parameters#set(String, String)} および {@link Camera.Parameters#set(String, int)} を使用して設定します。
	 * 実際に設定が有効となるかどうかは実機依存です。
	 */
	@Override
	public void setDisplayOrientation(final int degrees) {
		final Camera.Parameters params = getCamera().getParameters();
		if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			params.set("orientation", "portrait");
		} else {
			params.set("orientation", "landscape");
		}
		params.set("rotation", degrees);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException e) {
			// 無視する
		}
	}

	/**
	 * この実装は単に {@link Camera#setPreviewCallback(PreviewCallback)} を呼び出すだけです。
	 */
	@Override
	public void setPreviewCallback(final Camera.PreviewCallback cb) {
		mCamera.setPreviewCallback(cb);
	}

	/**
	 * この実装は単に {@link Camera#setPreviewDisplay(SurfaceHolder)} を呼び出すだけです。
	 */
	@Override
	public void setPreviewDisplay(final SurfaceHolder holder) throws IOException {
		mCamera.setPreviewDisplay(holder);
	}

	/**
	 * この実装は常に {@link IOException} をスローします。
	 */
	@Override
	public void setPreviewTexture(final Object surfaceTexture) throws IOException {
		throw new IOException("setPreviewTexture not supported");
	}

	/**
	 * この実装は単に {@link Camera#startPreview()} を呼び出すだけです。
	 */
	@Override
	public void startPreview() {
		mCamera.startPreview();
	}

	/**
	 * この実装は何も行いません。
	 */
	@Override
	public void onPreviewFrame(final Camera.PreviewCallback cb) {
	}

	@Override
	public final void stopPreview() {
		synchronized (this) {
			if (mCamera != null) {
				mCamera.setPreviewCallback(null);
				// stop preview before making changes
				try {
					mCamera.stopPreview();
				} catch (final Exception e) {}	// ignore: tried to stop a non-existent preview
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// キャプチャ

	protected final void setPictureCallback(final Camera.PictureCallback callback) {
		mPictureCallback = callback;
	}

	@Override
	public final void takePicture(final Camera.PictureCallback callback) {
		takePicture(callback, true);
	}

	/**
	 * シャッター音を鳴らすための空実装の {@link Camera.ShutterCallback} です。
	 */
	protected final Camera.ShutterCallback mNoopShutterCallback = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
			// NOP
		}
	};

	@Override
	public void takePicture(final Camera.PictureCallback callback, final boolean autoFocus) {
		setPictureCallback(callback);

		// Camera.takePicture 実行前に Camera.setPreviewCallback(null) をする
		// see - https://groups.google.com/forum/?fromgroups=#!topic/android-sdk-japan/3AWz8hpAOfo
		mCamera.setPreviewCallback(null);

		// メモリ不足の場合、Camera.PictureCallback へコールバックされないので念のため GC を実行します。
		// see - http://stackoverflow.com/questions/7627921/android-camera-takepicture-does-not-return-some-times
		System.gc();

		mCamera.takePicture(mEnableShutterSound ? mNoopShutterCallback : null, null, this);
	}

	/**
	 * この実装は何も行いません。
	 */
	@Override public void cancelAutoFocus() {}

	protected boolean mEnableShutterSound = true;

	/**
	 * この実装は常に {@code true} を返しますが、実際にシャッター音が無効となるかどうかは機種依存です。
	 */
	@Override
	public boolean enableShutterSound(final boolean enabled) {
		mEnableShutterSound = enabled;
		return true;
	}

	//////////////////////////////////////////////////////////////////////////
	// android.hardware.Camera.PictureCallback

	@Override
	public final void onPictureTaken(final byte[] data, final Camera camera) {
		mPictureCallback.onPictureTaken(data, camera);
		mPictureCallback = null;
	}

	//////////////////////////////////////////////////////////////////////////

	protected final Camera.Parameters getParameters() {
		return mCamera.getParameters();
	}

	/**
	 * {@link Camera.Size} 用のコンパレータを提供します。
	 */
	public static final class CameraSizeComparator implements Comparator<Camera.Size> {

		private static final int LOW = 1;
		private static final int HIGH = -1;
		private static final int EQUAL = 0;

		@Override
		public int compare(final Camera.Size lhs, final Camera.Size rhs) {
			if (lhs == null && rhs == null) {
				return EQUAL;
			}
			if (lhs == null) {
				return LOW;
			}
			if (rhs == null) {
				return HIGH;
			}

			final int lhsSize = lhs.width * lhs.height;
			final int rhsSize = rhs.width * rhs.height;
			if (lhsSize < rhsSize) {
				return LOW;
			} else if (lhsSize > rhsSize) {
				return HIGH;
			}
			return EQUAL;
		}

	}

	@Override
	public LinkedHashMap<Camera.Size, Camera.Size> getSupportedPreviewSizeAndSupportedPictureSizeMap() {
		final List<Camera.Size> previewSizes = getSupportedPreviewSizes();
		final List<Camera.Size> pictureSizes = getSupportedPictureSizes();
		if (previewSizes == null || pictureSizes == null) {
			return null;
		}

		final LinkedHashMap<Camera.Size, Camera.Size> results = new LinkedHashMap<Camera.Size, Camera.Size>();

		for (final Camera.Size previewSize : previewSizes) {
			final double previewRatio = (double) previewSize.width / (double) previewSize.height;
			for (final Camera.Size pictureSize : pictureSizes) {
				final double pictureRatio = (double) pictureSize.width / (double) pictureSize.height;
				if (Math.abs(previewRatio - pictureRatio) == 0D) {
					results.put(previewSize, pictureSize);
					break;
				}
				
			}
		}

		if (results.isEmpty()) {
			return null;
		}
		return results;
	}

	/**
	 * この実装はリフレクションを使用して {@link Camera.Parameters#getSupportedPreviewSizes()} を呼び出して戻り値を返します。
	 * {@link Camera.Parameters#getSupportedPreviewSizes()} が見つからない場合は {@code null} を返します。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Camera.Size> getSupportedPreviewSizes() {
		final Method method;
		try {
			method = Camera.Parameters.class.getMethod("getSupportedPreviewSizes", new Class[]{});
		} catch (final NoSuchMethodException e) {
			return null;
		}
		try {
			final List<Camera.Size> results = (List<Camera.Size>) method.invoke(mCamera.getParameters());
			// サイズを大きい順に並べて返却しない端末があるので、マニュアルで大きい順に並べて返却します。
			Collections.sort(results, new CameraSizeComparator());
			return results;
		} catch (final InvocationTargetException e) {
		} catch (final IllegalAccessException e) {
		}
		return null;
	}

	/**
	 * この実装はリフレクションを使用して {@link Camera.Parameters#getSupportedPictureSizes()} を呼び出して戻り値を返します。
	 * {@link Camera.Parameters#getSupportedPictureSizes()} が見つからない場合は {@code null} を返します。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Camera.Size> getSupportedPictureSizes() {
		final Method method;
		try {
			method = Camera.Parameters.class.getMethod("getSupportedPictureSizes", new Class[]{});
		} catch (final NoSuchMethodException e) {
			return null;
		}
		try {
			final List<Camera.Size> results = (List<Camera.Size>) method.invoke(mCamera.getParameters());
			// サイズを大きい順に並べて返却しない端末があるので、マニュアルで大きい順に並べて返却します。
			Collections.sort(results, new CameraSizeComparator());
			return results;
		} catch (final InvocationTargetException e) {
		} catch (final IllegalAccessException e) {
		}
		return null;
	}

	@Override
	public final Size getPreviewSize() {
		return mCamera.getParameters().getPreviewSize();
	}

	@Override
	public final Size getPictureSize() {
		return mCamera.getParameters().getPictureSize();
	}

	@Override
	public final void setPictureFormat(final int format) {
		final Camera.Parameters params = mCamera.getParameters();
		params.setPictureFormat(format);
		try {
			mCamera.setParameters(params);
		} catch (final RuntimeException e) {
			// 無視する
		}
	}

	/** この実装は常に {@code null} を返します。 */
	@Override public String getAntibanding() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public String getColorEffect() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public String getFlashMode() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public String getFocusMode() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public String getSceneMode() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public String getWhiteBalance() { return null; }

	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedAntibanding() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedColorEffects() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedFlashModes() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedFocusModes() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedSceneModes() { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedWhiteBalance() { return null; }

	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedAntibanding(final String... values) { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedColorEffects(final String... values) { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedFlashModes(final String... values) { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedFocusModes(final String... values) { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedSceneModes(final String... values) { return null; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<String> getSupportedWhiteBalance(final String... values) { return null; }

	/** この実装は何も行いません。 */
	@Override public void setAntibanding(final String antibanding) {}
	/** この実装は何も行いません。 */
	@Override public void setColorEffect(final String value) {}
	/** この実装は何も行いません。 */
	@Override public void setFlashMode(final String value) {}
	/** この実装は何も行いません。 */
	@Override public void setFocusMode(final String value) {}
	/** この実装は何も行いません。 */
	@Override public void setSceneMode(final String value) {}
	/** この実装は何も行いません。 */
	@Override public void setWhiteBalance(final String value) {}

	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchAntibanding() { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchColorEffect() { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchFlashMode() { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchFocusMode() { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchSceneMode() { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchWhiteBalance() { return null; }

	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchAntibanding(final String... values) { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchColorEffect(final String... values) { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchFlashMode(final String... values) { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchFocusMode(final String... values) { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchSceneMode(final String... values) { return null; }
	/** この実装は何も行わず常に {@code null} を返します。 */
	@Override public String switchWhiteBalance(final String... values) { return null; }

	@Override
	public final boolean isExposureCompensationSupported() {
		return getMinExposureCompensation() != 0 && getMaxExposureCompensation() != 0;
	}

	/** この実装は常に {@code 0} を返します。 */
	@Override public int getMaxExposureCompensation() { return 0; }
	/** この実装は常に {@code 0} を返します。 */
	@Override public int getMinExposureCompensation() { return 0; }
	/** この実装は常に {@code 0} を返します。 */
	@Override public float getExposureCompensationStep() { return 0; }
	/** この実装は常に {@code 0} を返します。 */
	@Override public int getExposureCompensation() { return 0; }
	/** この実装は何も行いません。 */
	@Override public void setExposureCompensation(final int value) {}

	//////////////////////////////////////////////////////////////////////////
	// ズーム

	/** この実装は常に {@code false} を返します。 */
	@Override public boolean isZoomSupported() { return false; }
	/** この実装は常に {@code 0} を返します。 */
	@Override public int getMaxZoom() { return 0; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<Integer> getZoomRatios() { return null; }
	/** この実装は常に {@code 0} を返します。 */
	@Override public int getZoom() { return 0; }
	/** この実装は何も行ないません。 */
	@Override public void setZoom(final int value) {}
	/** この実装は何も行ないません。 */
	@Override public void setZoomChangeListener(final OnZoomChangeListener listener) {}
	/** この実装は何も行ないません。 */
	@Override public void startSmoothZoom(final int value) {}
	/** この実装は何も行ないません。 */
	@Override public void stopSmoothZoom() {}

	//////////////////////////////////////////////////////////////////////////
	// エリア

	/** この実装は常に {@code 0} を返します。 */
	@Override public int getMaxNumFocusAreas() { return 0; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<AreaCompat> getFocusAreas() { return null; }
	/** この実装は何も行いません。 */
	@Override public void setFocusAreas(final AreaCompat... focusAreas) {}
	/** この実装は何も行いません。 */
	@Override public void setFocusAreas(final List<AreaCompat> focusAreas) {}
	/** この実装は常に {@code 0} を返します。 */
	@Override public int getMaxNumMeteringAreas() { return 0; }
	/** この実装は常に {@code null} を返します。 */
	@Override public List<AreaCompat> getMeteringAreas() { return null; }
	/** この実装は何も行いません。 */
	@Override public void setMeteringAreas(final AreaCompat... meteringAreas) {}
	/** この実装は何も行いません。 */
	@Override public void setMeteringAreas(final List<AreaCompat> meteringAreas) {}

	//////////////////////////////////////////////////////////////////////////

	/** この実装は常に {@code false} を返します。 */
	@Override public boolean isAutoExposureLockSupported() { return false; }
	/** この実装は何も行いません。 */
	@Override public void setAutoExposureLock(final boolean toggle) {};
	/** この実装は常に {@code false} を返します。 */
	@Override public boolean getAutoExposureLock() { return false; }

	/** この実装は常に {@code false} を返します。 */
	@Override public boolean isAutoWhiteBalanceLockSupported() { return false; }
	/** この実装は何も行いません。 */
	@Override public void setAutoWhiteBalanceLock(final boolean toggle) {}
	/** この実装は常に {@code false} を返します。 */
	@Override public boolean getAutoWhiteBalanceLock() { return false; }

	/** この実装は常に {@code false} を返します。 */
	@Override public boolean isVideoSnapshotSupported() { return false; }

	//////////////////////////////////////////////////////////////////////////
	// Video Stabilization

	/** この実装は常に {@code false} を返します。 */
	@Override public boolean isVideoStabilizationSupported() { return false; }
	/** この実装は何も行いません。 */
	@Override public void setVideoStabilization(final boolean toggle) {}
	/** この実装は常に {@code false} を返します。 */
	@Override public boolean getVideoStabilization() { return false; }

}