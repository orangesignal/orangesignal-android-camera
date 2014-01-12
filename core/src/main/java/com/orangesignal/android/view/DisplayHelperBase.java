/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * {@link DisplayHelper} の既定の実装を提供します。
 * 
 * @author 杉澤 浩二
 */
class DisplayHelperBase implements DisplayHelper {

	/**
	 * コンテキストを保持します。
	 */
	private Context mContext;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public DisplayHelperBase(final Context context) {
		mContext = context;
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	/**
	 * コンテキストを返します。
	 * 
	 * @return コンテキスト
	 */
	protected final Context getContext() {
		return mContext;
	}

	/**
	 * {@link WindowManager} のデフォルトディスプレイを返します。
	 * @return
	 */
	protected final Display getDefaultDisplay() {
		return ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}

	//////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("deprecation")
	@Override
	public int getDisplayAngle() {
//		return getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 90 : 0;
		return getDefaultDisplay().getOrientation();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Point getDisplaySize() {
		final Display display = getDefaultDisplay();
		return new Point(display.getWidth(), display.getHeight());
	}

	@Override
	public Point getRawDisplaySize() {
		return getDisplaySize();
	}

}