/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static android.opengl.GLES20.GL_MAX_TEXTURE_SIZE;
import static android.opengl.GLES20.glGetIntegerv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.orangesignal.android.graphics.BitmapFactoryUtils;

/**
 * 画像リソースを入力ソースとする OpenGL ES 2.0 向けのテクスチャオブジェクト管理クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20ImageResourceTexture extends GLES20ImageTexture {

	/**
	 * 画像ストリームを保持します。
	 */
	private InputStream mImageStream;

	/**
	 * 画像ストリームを自動的に閉じるかどうかを保持します。
	 */
	private final boolean mAutoClose;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * 指定された画像リソースを入力ソースとして、このクラスを構築するコンストラクタです。<p>
	 * このコンストラクタは、画像ストリームを自動的に閉じるとして {@link #GLES20ImageResourceTexture(InputStream, boolean)} を呼び出します。
	 * 
	 * @param res {@link Resources} オブジェクト
	 * @param resId 画像リソース ID
	 */
	public GLES20ImageResourceTexture(final Resources res, final int resId) {
		this(res.openRawResource(resId), true);
	}

	/**
	 * 指定された画像ファイルを入力ソースとして、このクラスを構築するコンストラクタです。<p>
	 * このコンストラクタは、画像ストリームを自動的に閉じるとして {@link #GLES20ImageResourceTexture(InputStream, boolean)} を呼び出します。
	 * 
	 * @param filename 画像ファイル
	 * @throws FileNotFoundException 画像ファイルが存在しない場合
	 * @see {@link #GLES20ImageResourceTexture(InputStream, boolean)}
	 */
	public GLES20ImageResourceTexture(final String filename) throws FileNotFoundException {
		this(new FileInputStream(new File(filename)), true);
	}

	/**
	 * 指定された画像ファイルを入力ソースとして、このクラスを構築するコンストラクタです。<p>
	 * このコンストラクタは、画像ストリームを自動的に閉じるとして {@link #GLES20ImageResourceTexture(InputStream, boolean)} を呼び出します。
	 * 
	 * @param file 画像ファイル
	 * @throws FileNotFoundException 画像ファイルが存在しない場合
	 * @see {@link #GLES20ImageResourceTexture(InputStream, boolean)}
	 */
	public GLES20ImageResourceTexture(final File file) throws FileNotFoundException {
		this(new FileInputStream(file), true);
	}

	/**
	 * 指定された画像ストリームを入力ソースとして、このクラスを構築するコンストラクタです。<p>
	 * このコンストラクタは、画像ストリームを自動的に閉じるとして {@link #GLES20ImageResourceTexture(InputStream, boolean)} を呼び出します。
	 * 
	 * @param is 画像ストリーム
	 * @see {@link #GLES20ImageResourceTexture(InputStream, boolean)}
	 */
	public GLES20ImageResourceTexture(final InputStream is) {
		this(is, true);
	}

	/**
	 * 指定された画像ストリームを入力ソースとして、このクラスを構築するコンストラクタです。<p>
	 * 画像ストリームを自動的に閉じるとした場合、このオブジェクトへの参照がなくなってから画像ストリームがいつ閉じられるかはファイナライザに依存します。
	 * 画像ストリームを自動的に閉じるかどうかに関わらず、アプリではテクスチャオブジェクトを保持して、明示的に {@link #dispose()} を呼び出すことを推奨します。
	 * 
	 * @param is 画像ストリーム
	 * @param autoClose 画像ストリームを自動的に閉じるかどうか
	 */
	public GLES20ImageResourceTexture(final InputStream is, final boolean autoClose) {
		if (is == null) {
			throw new IllegalArgumentException("InputStream must not be null");
		}
		mImageStream = is;
		mAutoClose = autoClose;
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	/**
	 * 画像ストリームを自動的に閉じるかどうかを返します。
	 * 
	 * @return 画像ストリームを自動的に閉じるかどうか
	 */
	public boolean isAutoClose() {
		return mAutoClose;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void setup() {
		// 最大テクスチャサイズを取得します。
		final int[] args = new int[1];
		glGetIntegerv(GL_MAX_TEXTURE_SIZE, args, 0);
		final int maxTextureSize = args[0];

		// ビットマップ情報のみを読み込みます。(データは読み込みません)
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(mImageStream, null, opts);

		// 縮小する必要があるかどうかを確認して処理します。
		final int size = Math.max(opts.outWidth, opts.outHeight);
		if (size > maxTextureSize) {
			opts.inSampleSize = size / maxTextureSize;
		}

		// ビットマップデータを読み込みます。
		opts.inJustDecodeBounds = false;
		opts.inDither = true;
		final Bitmap bitmap = BitmapFactoryUtils.decodeStream(mImageStream, opts.inSampleSize, 0, 2);
		try {
			attachToTexture(bitmap);
		} finally {
			if (bitmap != null) {
				bitmap.recycle();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (mAutoClose) {
				dispose();
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * このテクスチャオブジェクトに紐付く画像ストリームを解放します。
	 */
	public void dispose() {
		if (mImageStream != null) {
			try {
				mImageStream.close();
			} catch (final IOException e) {}	// 無視する
		}
		mImageStream = null;
	}

}