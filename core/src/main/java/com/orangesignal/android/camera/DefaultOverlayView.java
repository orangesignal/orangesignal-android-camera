/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * フォーカスエリアやホワイトバランスエリアなどの矩形描画をサポートする既定のオーバーレイビューを提供します。
 * 
 * @author 杉澤 浩二
 * @deprecated 設計中
 */
@Deprecated
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class DefaultOverlayView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private Thread mThread;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * Simple constructor to use when creating a view from code.
	 * 
	 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
	 */
	public DefaultOverlayView(final Context context) {
		super(context);
		initialize(context);
	}

	/**
	 * Constructor that is called when inflating a view from XML.
	 * This is called when a view is being constructed from an XML file, supplying attributes that were specified in the XML file.
	 * 
	 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
	 * @param attrs The attributes of the XML tag that is inflating the view.
	 */
	public DefaultOverlayView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	/**
	 * Perform inflation from XML and apply a class-specific base style.
	 * This constructor of View allows subclasses to use their own base style when they are inflating.
	 * 
	 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
	 * @param attrs The attributes of the XML tag that is inflating the view.
	 * @param defStyle The default style to apply to this view. If 0, no style will be applied (beyond what is included in the theme).
	 *        This may either be an attribute resource, whose value will be retrieved from the current theme, or an explicit style resource.
	 */
	public DefaultOverlayView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	/**
	 * このビューを初期化します。
	 * 
	 * @param context コンテキスト
	 */
	private void initialize(final Context context) {
//		mGestureDetector = new GestureDetector(context, this);

		final SurfaceHolder holder = getHolder();
		holder.setFormat(PixelFormat.TRANSPARENT);

		setZOrderMediaOverlay(true);
	}

	//////////////////////////////////////////////////////////////////////////
	// SurfaceHolder.Callback

	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		mThread = new Thread(this);
		mThread.start();
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		mThread = null;
	}

	//////////////////////////////////////////////////////////////////////////
	// Runnable

	@Override
	public void run() {
		while (mThread != null && mThread == Thread.currentThread()) {
			try {
				Thread.sleep(100L);
			} catch (final InterruptedException e) {
			}
		}
	}

}
