/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;

/**
 * {@link Bitmap} を入力ソースとする OpenGL ES 2.0 向けのテクスチャオブジェクト管理クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20ImageBitmapTexture extends GLES20ImageTexture {

	/**
	 * 入力ソースの {@link Bitmap} オブジェクトを保持します。
	 */
	private Bitmap mBitmap;

	/**
	 * {@link Bitmap} オブジェクトを自動的にリサイクルするかどうかを保持します。
	 */
	private final boolean mAutoRecycle;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * 指定された {@link Bitmap} オブジェクトを入力ソースとして、このクラスを構築するコンストラクタです。<p>
	 * このコンストラクタは、{@link Bitmap} オブジェクトを自動的にリサイクルするとして {@link #GLES20ImageBitmapTexture(Bitmap, boolean)} を呼び出します。
	 * 
	 * @param bitmap {@link Bitmap} オブジェクト
	 * @see {@link #GLES20ImageBitmapTexture(Bitmap, boolean)}
	 */
	public GLES20ImageBitmapTexture(final Bitmap bitmap) {
		this(bitmap, true);
	}

	/**
	 * 指定された {@link Bitmap} オブジェクトを入力ソースとして、このクラスを構築するコンストラクタです。<p>
	 * {@link Bitmap} オブジェクトを自動的にリサイクルするとした場合、このオブジェクトへの参照がなくなってから {@link Bitmap} オブジェクトがいつリサイクルされるかはファイナライザに依存します。
	 * {@link Bitmap} オブジェクトを自動的にリサイクルするかどうかに関わらず、アプリではテクスチャオブジェクトを保持して、明示的に {@link #dispose()} を呼び出すことを推奨します。
	 * 
	 * @param bitmap {@link Bitmap} オブジェクト
	 * @param autoRecycle {@link Bitmap} オブジェクトを自動的にリサイクルするかどうか
	 */
	public GLES20ImageBitmapTexture(final Bitmap bitmap, final boolean autoRecycle) {
		mBitmap = bitmap;
		mAutoRecycle = autoRecycle;
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	/**
	 * {@link Bitmap} オブジェクトを自動的にリサイクルするかどうかを返します。
	 * 
	 * @return {@link Bitmap} オブジェクトを自動的にリサイクルするかどうか
	 */
	public boolean isAutoRecycle() {
		return mAutoRecycle;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void setup() {
		attachToTexture(mBitmap);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (mAutoRecycle) {
				dispose();
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * このテクスチャオブジェクトに紐付く {@link Bitmap} オブジェクトを解放します。
	 */
	public void dispose() {
		if (mBitmap != null) {
			if (!mBitmap.isRecycled()) {
				mBitmap.recycle();
			}
			mBitmap = null;
		}
	}

}