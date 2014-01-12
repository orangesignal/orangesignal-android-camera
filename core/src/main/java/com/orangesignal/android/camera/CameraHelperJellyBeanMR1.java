/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;

/**
 * Android 4.2 (API レベル 17) 対応の実装を提供します。<p>
 * <ul>
 * <li>{@link Camera#enableShutterSound(boolean)}</li>
 * </ul>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class CameraHelperJellyBeanMR1 extends CameraHelperICSMR1 {

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public CameraHelperJellyBeanMR1(final Context context) {
		super(context);
	}

	//////////////////////////////////////////////////////////////////////////
	// キャプチャ

	/**
	 * この実装は単に {@link Camera#enableShutterSound(boolean)} を呼出して戻り値を返します。
	 */
	@Override
	public boolean enableShutterSound(final boolean enabled) {
		return getCamera().enableShutterSound(enabled);
	}

}