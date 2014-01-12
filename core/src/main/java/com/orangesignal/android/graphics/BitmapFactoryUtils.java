/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.graphics;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.orangesignal.android.media.ExifUtils;

/**
 * {@link BitmapFactory} に関するユーティリティを提供します。
 * 
 * @author 杉澤 浩二
 */
public final class BitmapFactoryUtils {

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * インスタンス化できない事を強制します。
	 */
	private BitmapFactoryUtils() {}

	//////////////////////////////////////////////////////////////////////////

	public static Bitmap decodeFile(final String filename, final int maxSize, final boolean square) throws IOException {
		final int angle = ExifUtils.getAngle(filename);

		// 画像が大きい場合があるので、画像データではなく画像情報を取得してサイズを検証します。
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, opts);

		final int size = Math.max(opts.outWidth, opts.outHeight);
		if (size > maxSize) {
			// 液晶サイズよりも画像サイズが大きい場合は画像を縮小して読み込む指定をします。
			opts.inSampleSize = size / maxSize;
		} else {
			opts.inSampleSize = 1;
		}

		// 画像を読み込みます。
		Bitmap bitmap = decodeFile(filename, opts.inSampleSize, 0, 2);

		// 画像の向きを補正する必要がある場合は補正します。
		if (angle != 0) {
			final Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			final Bitmap _bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
			bitmap.recycle();
			bitmap = _bitmap;
		}
		if (square && bitmap.getWidth() != bitmap.getHeight()) {
			if (bitmap.getWidth() > bitmap.getHeight()) {
				final Bitmap _bitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - bitmap.getHeight()) / 2, 0, bitmap.getHeight(), bitmap.getHeight());
				bitmap.recycle();
				bitmap = _bitmap;
			} else if (bitmap.getWidth() < bitmap.getHeight()) {
				final Bitmap _bitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - bitmap.getWidth()) / 2, bitmap.getWidth(), bitmap.getWidth());
				bitmap.recycle();
				bitmap = _bitmap;
			}
		}
		return bitmap;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * {@link OutOfMemoryError} を発生させずに指定された画像リソースを読み込んで {@link Bitmap} として返します。<p>
	 * 実装は単に、初期サンプリングサイズを {@code 1}、加算値を {@code 0}、乗算値を {@code 2} として {@link #decodeStream(InputStream, int, int, int)} を呼び出します。
	 * 
	 * @param is 画像の入力ストリーム
	 * @return {@link Bitmap} オブジェクト
	 */
	public static Bitmap decodeStream(final InputStream is) {
		return decodeStream(is, 1, 0, 2);
	}

	/**
	 * {@link OutOfMemoryError} を発生させずに指定された画像リソースを読み込んで {@link Bitmap} として返します。<p>
	 * 実装は、指定された画像を読み込んで {@link OutOfMemoryError} が発生する都度に、次の計算式でサンプリングサイズを調整して読み込みをします。
	 * <ul>
	 * <li>新サンプリングサイズ = (現サンプリングサイズ + {@code add}) * {@code multi}</li>
	 * </ul>
	 * 
	 * @param is 画像の入力ストリーム
	 * @param startInSampleSize サンプリングサイズの初期値
	 * @param add 読み込みに失敗した場合にサンプリングサイズへ加算する値
	 * @param multi 読み込みに失敗した場合にサンプリングサイズへ乗算する値
	 * @return {@link Bitmap} オブジェクト
	 */
	public static Bitmap decodeStream(final InputStream is, final int startInSampleSize, final int add, final int multi) {
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		int inSampleSize = startInSampleSize;
		while (true) {
			opts.inSampleSize = inSampleSize;
			opts.inDither = true;
			try {
				return BitmapFactory.decodeStream(is, null, opts);
			} catch (final OutOfMemoryError e) {
				inSampleSize = (inSampleSize + add) * multi;
			}
		}
	}

	/**
	 * {@link OutOfMemoryError} を発生させずに指定された画像リソースを読み込んで {@link Bitmap} として返します。<p>
	 * 実装は単に、初期サンプリングサイズを {@code 1}、加算値を {@code 0}、乗算値を {@code 2} として {@link #decodeResource(Resources, int, int, int, int)} を呼び出します。
	 * 
	 * @param res {@link Resources}
	 * @param id 画像リソースのID
	 * @return {@link Bitmap} オブジェクト
	 */
	public static Bitmap decodeResource(final Resources res, final int id) {
		return decodeResource(res, id, 1, 0, 2);
	}

	/**
	 * {@link OutOfMemoryError} を発生させずに指定された画像リソースを読み込んで {@link Bitmap} として返します。<p>
	 * 実装は、指定された画像を読み込んで {@link OutOfMemoryError} が発生する都度に、次の計算式でサンプリングサイズを調整して読み込みをします。
	 * <ul>
	 * <li>新サンプリングサイズ = (現サンプリングサイズ + {@code add}) * {@code multi}</li>
	 * </ul>
	 * 
	 * @param res {@link Resources}
	 * @param id 画像リソースのID
	 * @param startInSampleSize サンプリングサイズの初期値
	 * @param add 読み込みに失敗した場合にサンプリングサイズへ加算する値
	 * @param multi 読み込みに失敗した場合にサンプリングサイズへ乗算する値
	 * @return {@link Bitmap} オブジェクト
	 */
	public static Bitmap decodeResource(final Resources res, final int id, final int startInSampleSize, final int add, final int multi) {
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		int inSampleSize = startInSampleSize;
		while (true) {
			opts.inSampleSize = inSampleSize;
			opts.inDither = true;
			try {
				return BitmapFactory.decodeResource(res, id, opts);
			} catch (final OutOfMemoryError e) {
				inSampleSize = (inSampleSize + add) * multi;
			}
		}
	}

	/**
	 * {@link OutOfMemoryError} を発生させずに指定された画像リソースを読み込んで {@link Bitmap} として返します。<p>
	 * 実装は単に、初期サンプリングサイズを {@code 1}、加算値を {@code 0}、乗算値を {@code 2} として {@link #decodeFile(String, int, int, int)} を呼び出します。
	 * 
	 * @param pathName 画像ファイルへのパス
	 * @return {@link Bitmap} オブジェクト
	 */
	public static Bitmap decodeFile(final String pathName) {
		return decodeFile(pathName, 1, 0, 2);
	}

	/**
	 * {@link OutOfMemoryError} を発生させずに指定された画像リソースを読み込んで {@link Bitmap} として返します。<p>
	 * 実装は、指定された画像を読み込んで {@link OutOfMemoryError} が発生する都度に、次の計算式でサンプリングサイズを調整して読み込みをします。
	 * <ul>
	 * <li>新サンプリングサイズ = (現サンプリングサイズ + {@code add}) * {@code multi}</li>
	 * </ul>
	 * 
	 * @param pathName 画像ファイルへのパス
	 * @param startInSampleSize サンプリングサイズの初期値
	 * @param add 読み込みに失敗した場合にサンプリングサイズへ加算する値
	 * @param multi 読み込みに失敗した場合にサンプリングサイズへ乗算する値
	 * @return {@link Bitmap} オブジェクト
	 */
	public static Bitmap decodeFile(final String pathName, final int startInSampleSize, final int add, final int multi) {
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		int inSampleSize = startInSampleSize;
		while (true) {
			opts.inSampleSize = inSampleSize;
			opts.inDither = true;
			try {
				return BitmapFactory.decodeFile(pathName, opts);
			} catch (final OutOfMemoryError e) {
				inSampleSize = (inSampleSize + add) * multi;
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////

	public static Bitmap decodeStream(final Context context, final String name, final BitmapFactory.Options opts) throws FileNotFoundException {
		final InputStream in = new BufferedInputStream(context.openFileInput(name));
		try {
			return BitmapFactory.decodeStream(in, null, opts);
		} finally {
			try {
				in.close();
			} catch (final IOException e) {}	// 無視する
		}
	}

	//////////////////////////////////////////////////////////////////////////

	public static Bitmap decodeByteArray(final byte[] data, final Bitmap.Config config) {
		return decodeByteArray(data, 0, data.length, config);
	}

	public static Bitmap decodeByteArray(final byte[] data, final int offset, final int length, final Bitmap.Config config) {
		Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(data, offset, length);
		if (bitmap.getConfig().compareTo(config) == 0) {
			return bitmap;
		}
		final int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		bitmap.recycle();

		return Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), config);
	}

}
