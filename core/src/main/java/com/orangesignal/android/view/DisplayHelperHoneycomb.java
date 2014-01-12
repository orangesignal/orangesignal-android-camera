/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;

/**
 * Android 3.0 (API レベル 11) 対応の実装を提供します。<p>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class DisplayHelperHoneycomb extends DisplayHelperFroyo {

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public DisplayHelperHoneycomb(final Context context) {
		super(context);
	}

	@Override
	public Point getRawDisplaySize() {
		final Display display = getDefaultDisplay();
		try {
			return new Point(
					(Integer) Display.class.getMethod("getRawWidth").invoke(display),
					(Integer) Display.class.getMethod("getRawHeight").invoke(display)
				);
		} catch (final Exception e) {
			return super.getRawDisplaySize();
		}
	}

}