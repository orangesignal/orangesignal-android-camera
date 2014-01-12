/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;

/**
 * Android 4.0 (API レベル 14) 対応の実装を提供します。<p>
 * <ul>
 * <li>{@link Camera.Parameters#getMaxNumFocusAreas()}</li>
 * <li>{@link Camera.Parameters#getFocusAreas()}</li>
 * <li>{@link Camera.Parameters#setFocusAreas(List)}</li>
 * <li>{@link Camera.Parameters#getMaxNumMeteringAreas()}</li>
 * <li>{@link Camera.Parameters#getMeteringAreas()}</li>
 * <li>{@link Camera.Parameters#setMeteringAreas(List)}</li>
 * <li>{@link Camera.Parameters#isAutoExposureLockSupported()}</li>
 * <li>{@link Camera.Parameters#setAutoExposureLock(boolean)}</li>
 * <li>{@link Camera.Parameters#getAutoExposureLock()}</li>
 * <li>{@link Camera.Parameters#isAutoWhiteBalanceLockSupported()}</li>
 * <li>{@link Camera.Parameters#setAutoWhiteBalanceLock(boolean)}</li>
 * <li>{@link Camera.Parameters#getAutoWhiteBalanceLock()}</li>
 * <li>{@link Camera.Parameters#isVideoSnapshotSupported()}</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class CameraHelperICS extends CameraHelperHonycomb {

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperICS(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////
	// カメラの接続・切断

	@Override
	public void initializeFocusMode() {
		// 写真撮影に最適なフォーカスモードを設定します。
		final List<String> supportedFocusModes = getSupportedFocusModes();
		if (supportedFocusModes != null) {
			// 動画向けの連続フォーカスをサポートしている場合は設定します。
			if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				final Camera.Parameters parameters = getCamera().getParameters();
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				try {
					getCamera().setParameters(parameters);
				} catch (final RuntimeException e) {}	// 無視する
			} else {
				super.initializeFocusMode();
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// エリア

	/**
	 * この実装は単に {@link Camera.Parameters#getMaxNumFocusAreas()} を呼び出して戻り値を返します。
	 */
	@Override
	public int getMaxNumFocusAreas() {
		return getCamera().getParameters().getMaxNumFocusAreas();
	}

	/**
	 * この実装は {@link Camera.Parameters#getFocusAreas()} の結果を {@link AreaCompat} へ詰替えて返します。
	 */
	@Override
	public List<AreaCompat> getFocusAreas() {
		return repackCompatAreas(getCamera().getParameters().getFocusAreas());
	}

	/**
	 * この実装は指定されたパラメータを {@link Arrays#asList(Object...)} でリストへ変換して単に {@link #setFocusAreas(List)} を呼び出します。
	 */
	@Override
	public void setFocusAreas(final AreaCompat... focusAreas) {
		setFocusAreas(Arrays.asList(focusAreas));
	}

	/**
	 * この実装は指定されたパラメータを {@link Camera.Area} へ詰替えてから {@link Camera.Parameters#setFocusAreas(List)} を呼び出します。
	 */
	@Override
	public void setFocusAreas(final List<AreaCompat> focusAreas) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setFocusAreas(repackInternalAreas(focusAreas));
		getCamera().setParameters(params);
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getMaxNumMeteringAreas()} を呼び出して戻り値を返します。
	 */
	@Override
	public int getMaxNumMeteringAreas() {
		return getCamera().getParameters().getMaxNumMeteringAreas();
	}

	/**
	 * この実装は {@link Camera.Parameters#getMeteringAreas()} の結果を {@link AreaCompat} へ詰替えて返します。
	 */
	@Override
	public List<AreaCompat> getMeteringAreas() {
		return repackCompatAreas(getCamera().getParameters().getMeteringAreas());
	}

	/**
	 * この実装は指定されたパラメータを {@link Arrays#asList(Object...)} でリストへ変換して単に {@link #setMeteringAreas(List)} を呼び出します。
	 */
	@Override
	public void setMeteringAreas(final AreaCompat... meteringAreas) {
		setMeteringAreas(Arrays.asList(meteringAreas));
	}

	/**
	 * この実装は指定されたパラメータを {@link Camera.Area} へ詰替えてから {@link Camera.Parameters#setMeteringAreas(List)} を呼び出します。
	 */
	@Override
	public void setMeteringAreas(final List<AreaCompat> meteringAreas) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setMeteringAreas(repackInternalAreas(meteringAreas));
		getCamera().setParameters(params);
	}

	protected static final List<AreaCompat> repackCompatAreas(final List<Camera.Area> areas) {
		if (areas == null) {
			return null;
		}

		final List<AreaCompat> results = new ArrayList<AreaCompat>(areas.size());
		for (final Camera.Area area : areas) {
			results.add(new AreaCompat(area.rect, area.weight));
		}
		return results;
	}

	protected static final List<Camera.Area> repackInternalAreas(final List<AreaCompat> areas) {
		List<Camera.Area> results = null;

		if (areas != null) {
			results = new ArrayList<Camera.Area>(areas.size());
			for (final AreaCompat area : areas) {
				results.add(new Camera.Area(area.rect, area.weight));
			}
		}

		return results;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * この実装は単に {@link Camera.Parameters#isAutoExposureLockSupported()} を呼び出して戻り値を返します。
	 */
	@Override
	public boolean isAutoExposureLockSupported() {
		return getCamera().getParameters().isAutoExposureLockSupported();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#setAutoExposureLock(boolean)} を呼び出します。
	 */
	@Override
	public void setAutoExposureLock(final boolean toggle) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setAutoExposureLock(toggle);
		getCamera().setParameters(params);
	};

	/**
	 * この実装は単に {@link Camera.Parameters#getAutoExposureLock()} を呼び出して戻り値を返します。
	 */
	@Override
	public boolean getAutoExposureLock() {
		return getCamera().getParameters().getAutoExposureLock();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#isAutoWhiteBalanceLockSupported()} を呼び出して戻り値を返します。
	 */
	@Override
	public boolean isAutoWhiteBalanceLockSupported() {
		return getCamera().getParameters().isAutoWhiteBalanceLockSupported();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#setAutoWhiteBalanceLock(boolean)} を呼び出します。
	 */
	@Override
	public void setAutoWhiteBalanceLock(final boolean toggle) {
		final Camera.Parameters params = getCamera().getParameters();
		params.setAutoWhiteBalanceLock(toggle);
		getCamera().setParameters(params);
	}

	/**
	 * この実装は単に {@link Camera.Parameters#getAutoWhiteBalanceLock()} を呼び出して戻り値を返します。
	 */
	@Override
	public boolean getAutoWhiteBalanceLock() {
		return getCamera().getParameters().getAutoWhiteBalanceLock();
	}

	/**
	 * この実装は単に {@link Camera.Parameters#isVideoSnapshotSupported()} を呼び出して戻り値を返します。
	 */
	@Override
	public boolean isVideoSnapshotSupported() {
		return getCamera().getParameters().isVideoSnapshotSupported();
	}

}