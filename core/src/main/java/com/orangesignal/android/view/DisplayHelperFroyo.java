/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.Surface;

/**
 * Android 2.2 (API レベル 8) 対応の実装を提供します。<p>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
class DisplayHelperFroyo extends DisplayHelperBase {

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public DisplayHelperFroyo(final Context context) {
		super(context);
	}

	@Override
	public int getDisplayAngle() {
		switch (getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_0:
				return 0;
			case Surface.ROTATION_90:
				return 90;
			case Surface.ROTATION_180:
				return 180;
			case Surface.ROTATION_270:
				return 270;
			default:
				throw new IllegalStateException();
		}
	}

}