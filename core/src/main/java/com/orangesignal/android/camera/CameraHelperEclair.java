/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;

/**
 * Android 2.0 (API レベル 5) 対応の実装を提供します。<p>
 * このバージョンの対応は以下の通りです。
 * <ul>
 * <li>{@link Camera.Parameters#FOCUS_MODE_AUTO}</li>
 * <li>{@link Camera.Parameters#FOCUS_MODE_INFINITY}</li>
 * <li>{@link Camera.Parameters#FOCUS_MODE_MACRO}</li>
 * <li>{@link Camera.Parameters#FOCUS_MODE_FIXED}</li>
 * <li>{@link Camera#getParameters()#getSupportedPreviewSizes()}</li>
 * <li>{@link Camera#getParameters()#getSupportedPictureSizes()}</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
class CameraHelperEclair extends CameraHelperDonut implements Camera.AutoFocusCallback {

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperEclair(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・停止

	/**
	 * この実装は可能であれば {@link Camera.Parameters#setRotation(int)} を呼び出して設定します。
	 */
	@Override
	public void setDisplayOrientation(final int degrees) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setRotation(degrees);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException e) {
			// 無視する
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// カメラの接続・切断

	/**
	 * この実装は現在のカメラが {@link Camera.Parameters#FOCUS_MODE_INFINITY} をサポートしている場合は、{@link Camera.Parameters#FOCUS_MODE_INFINITY} を設定します。
	 */
	@Override
	public void initializeFocusMode() {
		// 写真撮影に最適なフォーカスモードを設定します。
		final List<String> supportedFocusModes = getSupportedFocusModes();
		if (supportedFocusModes != null) {
			final Camera.Parameters parameters = getCamera().getParameters();
			// 無限フォーカスをサポートしている場合は設定します。
			if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				try {
					getCamera().setParameters(parameters);
				} catch (final RuntimeException e) {}	// 無視する
			} else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
				try {
					getCamera().setParameters(parameters);
				} catch (final RuntimeException e) {}	// 無視する
			} else {
				super.initializeFocusMode();
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// キャプチャ

	/**
	 * 実装は {@link Camera#autoFocus(android.hardware.Camera.AutoFocusCallback)} を呼び出します。
	 */
	@Override
	public void takePicture(final Camera.PictureCallback callback, final boolean autoFocus) {
		setPictureCallback(callback);

		if (autoFocus) {
			getCamera().autoFocus(this);
		} else {
			takePicture(getCamera());
		}
	}

	/**
	 * 実装は単に {@link Camera#cancelAutoFocus()} を呼び出すだけです。
	 */
	@Override
	public final void cancelAutoFocus() {
		getCamera().cancelAutoFocus();
	}

	protected void takePicture(final Camera camera) {
		// Camera.takePicture 実行前に Camera.setPreviewCallback(null) をする
		// see - https://groups.google.com/forum/?fromgroups=#!topic/android-sdk-japan/3AWz8hpAOfo
		camera.setPreviewCallback(null);

		// メモリ不足の場合、Camera.PictureCallback へコールバックされないので念のため GC を実行します。
		// see - http://stackoverflow.com/questions/7627921/android-camera-takepicture-does-not-return-some-times
		System.gc();

		camera.takePicture(mEnableShutterSound ? mNoopShutterCallback : null, null, null, this);
	}

	//////////////////////////////////////////////////////////////////////////
	// Camera.AutoFocusCallback

	@Override
	public final void onAutoFocus(final boolean success, final Camera camera) {
//		camera.cancelAutoFocus();
		takePicture(camera);
	}

	//////////////////////////////////////////////////////////////////////////
	// パラメータ操作

	/**
	 * この実装は {@link Camera.Parameters#getSupportedPreviewSizes()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<Camera.Size> getSupportedPreviewSizes() {
		final List<Camera.Size> results = getCamera().getParameters().getSupportedPreviewSizes();
		// サイズを大きい順に並べて返却しない端末があるので、マニュアルで大きい順に並べて返却します。
		Collections.sort(results, new CameraSizeComparator());
		return results;
	}

	/**
	 * この実装は {@link Camera.Parameters#getSupportedPictureSizes()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<Camera.Size> getSupportedPictureSizes() {
		final List<Camera.Size> results = getCamera().getParameters().getSupportedPictureSizes();
		// サイズを大きい順に並べて返却しない端末があるので、マニュアルで大きい順に並べて返却します。
		Collections.sort(results, new CameraSizeComparator());
		return results;
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getAntibanding()} を呼び出して戻り値を返します。
	 */
	@Override
	public String getAntibanding() {
		return getCamera().getParameters().getAntibanding();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getColorEffect()} を呼び出して戻り値を返します。
	 */
	@Override
	public String getColorEffect() {
		return getCamera().getParameters().getColorEffect();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getFlashMode()} を呼び出して戻り値を返します。
	 */
	@Override
	public String getFlashMode() {
		return getCamera().getParameters().getFlashMode();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getFocusMode()} を呼び出して戻り値を返します。
	 */
	@Override
	public String getFocusMode() {
		return getCamera().getParameters().getFocusMode();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getSceneMode()} を呼び出して戻り値を返します。
	 */
	@Override
	public String getSceneMode() {
		return getCamera().getParameters().getSceneMode();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getWhiteBalance()} を呼び出して戻り値を返します。
	 */
	@Override
	public String getWhiteBalance() {
		return getCamera().getParameters().getWhiteBalance();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getSupportedAntibanding()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<String> getSupportedAntibanding() {
		return getCamera().getParameters().getSupportedAntibanding();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getSupportedColorEffects()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<String> getSupportedColorEffects() {
		return getCamera().getParameters().getSupportedColorEffects();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getSupportedFlashModes()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<String> getSupportedFlashModes() {
		return getCamera().getParameters().getSupportedFlashModes();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getSupportedFocusModes()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<String> getSupportedFocusModes() {
		return getCamera().getParameters().getSupportedFocusModes();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getSupportedSceneModes()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<String> getSupportedSceneModes() {
		return getCamera().getParameters().getSupportedSceneModes();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getSupportedWhiteBalance()} を呼び出して戻り値を返します。
	 */
	@Override
	public List<String> getSupportedWhiteBalance() {
		return getCamera().getParameters().getSupportedWhiteBalance();
	}

	@Override
	public List<String> getSupportedAntibanding(final String... values) {
		return getContainsList(getCamera().getParameters().getSupportedAntibanding(), values);
	}

	@Override
	public List<String> getSupportedColorEffects(final String... values) {
		return getContainsList(getCamera().getParameters().getSupportedColorEffects(), values);
	}

	@Override
	public List<String> getSupportedFlashModes(final String... values) {
		return getContainsList(getCamera().getParameters().getSupportedFlashModes(), values);
	}

	@Override
	public List<String> getSupportedFocusModes(final String... values) {
		return getContainsList(getCamera().getParameters().getSupportedFocusModes(), values);
	}

	@Override
	public List<String> getSupportedSceneModes(final String... values) {
		return getContainsList(getCamera().getParameters().getSupportedSceneModes(), values);
	}

	@Override
	public List<String> getSupportedWhiteBalance(final String... values) {
		return getContainsList(getCamera().getParameters().getSupportedWhiteBalance(), values);
	}

	private static List<String> getContainsList(final List<String> list, final String... values) {
		if (list == null) {
			return null;
		}

		final ArrayList<String> results = new ArrayList<String>();
		for (final String value : values) {
			if (list.contains(value)) {
				results.add(value);
			}
		}
		if (results.isEmpty()) {
			return null;
		}
		return results;
	}

	@Override
	public void setAntibanding(final String antibanding) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setAntibanding(antibanding);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException  e) {}	// 無視する
	}

	@Override
	public void setColorEffect(final String value) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setColorEffect(value);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException  e) {}	// 無視する
	}

	@Override
	public void setFlashMode(final String value) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setFlashMode(value);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException  e) {}	// 無視する
	}

	@Override
	public void setFocusMode(final String value) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setFocusMode(value);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException  e) {}	// 無視する
	}

	@Override
	public void setSceneMode(final String value) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setSceneMode(value);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException  e) {}	// 無視する
	}

	@Override
	public void setWhiteBalance(final String value) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setWhiteBalance(value);
		try {
			getCamera().setParameters(params);
		} catch (final RuntimeException  e) {}	// 無視する
	}

	@Override
	public String switchAntibanding() {
		return switchAntibanding(getSupportedAntibanding());
	}
	@Override
	public String switchAntibanding(final String... values) {
		return switchAntibanding(getSupportedAntibanding(values));
	}
	private String switchAntibanding(final List<String> list) {
		final String value = getNextValue(list, getAntibanding());
		if (value != null) {
			setAntibanding(value);
		}
		return value;
	}

	@Override
	public String switchColorEffect() {
		return switchColorEffect(getSupportedColorEffects());
	}
	@Override
	public String switchColorEffect(final String... values) {
		return switchColorEffect(getSupportedColorEffects(values));
	}
	private String switchColorEffect(final List<String> list) {
		final String value = getNextValue(list, getColorEffect());
		if (value != null) {
			setColorEffect(value);
		}
		return value;
	}

	@Override
	public String switchFlashMode() {
		return switchFlashMode(getSupportedFlashModes());
	}
	@Override
	public String switchFlashMode(final String... values) {
		return switchFlashMode(getSupportedFlashModes(values));
	}
	private String switchFlashMode(final List<String> list) {
		final String value = getNextValue(list, getFlashMode());
		if (value != null) {
			setFlashMode(value);
		}
		return value;
	}

	@Override
	public String switchFocusMode() {
		return switchFocusMode(getSupportedFocusModes());
	}
	@Override
	public String switchFocusMode(final String... values) {
		return switchFocusMode(getSupportedFocusModes(values));
	}
	private String switchFocusMode(final List<String> list) {
		final String value = getNextValue(list, getFocusMode());
		if (value != null) {
			setFocusMode(value);
		}
		return value;
	}

	@Override
	public String switchSceneMode() {
		return switchSceneMode(getSupportedSceneModes());
	}
	@Override
	public String switchSceneMode(final String... values) {
		return switchSceneMode(getSupportedSceneModes(values));
	}
	private String switchSceneMode(final List<String> list) {
		final String value = getNextValue(list, getSceneMode());
		if (value != null) {
			setSceneMode(value);
		}
		return value;
	}

	@Override
	public String switchWhiteBalance() {
		return switchWhiteBalance(getSupportedWhiteBalance());
	}
	@Override
	public String switchWhiteBalance(final String... values) {
		return switchWhiteBalance(getSupportedWhiteBalance(values));
	}
	private String switchWhiteBalance(final List<String> list) {
		final String value = getNextValue(list, getWhiteBalance());
		if (value != null) {
			setWhiteBalance(value);
		}
		return value;
	}

	private static String getNextValue(final List<String> list, final String value) {
		if (list != null && list.size() > 1) {
			final int index = list.indexOf(value);
			final String result;
			if (index != -1) {
				result = list.get((index + 1) % list.size());
			} else {
				result = list.get(0);
			}
			return result;
		}
		return null;
	}

}