/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;

/**
 * Android 1.5 (API レベル 3) 対応の実装を提供します。<p>
 * このバージョンの対応は以下の通りです。
 * <ul>
 * <li>{@link Camera#setOneShotPreviewCallback(android.hardware.Camera.PreviewCallback)}</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
class CameraHelperCupcake extends CameraHelperBase {

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperCupcake(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・停止

	/**
	 * この実装は {@link Camera#setOneShotPreviewCallback(android.hardware.Camera.PreviewCallback)} を使用してプレビューコールバックを設定します。
	 */
	@Override
	public void setPreviewCallback(final Camera.PreviewCallback cb) {
		getCamera().setOneShotPreviewCallback(cb);
	}

	/**
	 * この実装は {@link Camera#setOneShotPreviewCallback(android.hardware.Camera.PreviewCallback)} を使用してプレビューコールバックを設定します。
	 */
	@Override
	public void onPreviewFrame(final Camera.PreviewCallback cb) {
		getCamera().setOneShotPreviewCallback(cb);
	}

}