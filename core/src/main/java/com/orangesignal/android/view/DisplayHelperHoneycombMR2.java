/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;

/**
 * Android 3.2 (API レベル 13) 対応の実装を提供します。<p>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
class DisplayHelperHoneycombMR2 extends DisplayHelperHoneycomb {

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public DisplayHelperHoneycombMR2(final Context context) {
		super(context);
	}

	@Override
	public Point getDisplaySize() {
		final Point point = new Point();
		getDefaultDisplay().getSize(point);
		return point;
	}

}
