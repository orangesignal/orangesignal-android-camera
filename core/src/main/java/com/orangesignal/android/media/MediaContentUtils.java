/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;

/**
 * 画像コンテンツに関するユーティリティを提供します。
 * 
 * @author 杉澤 浩二
 */
public final class MediaContentUtils {

	/**
	 * このクラスの新しいインスタンスを生成して返します。
	 * 
	 * @param context コンテキスト
	 * @return このクラスの新しいインスタンス
	 */
	public static MediaContentUtils newInstance(final Context context) {
		return new MediaContentUtils(context);
	}

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンテキストを保持します。
	 */
	private Context mContext;

	/**
	 * インスタンス化できない事を強制します。
	 */
	private MediaContentUtils(final Context context) {
		mContext = context;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * 指定された画像コンテンツの URI から画像ファイルの場所を返します。
	 *  
	 * @param uri 画像コンテンツの URI
	 * @return 画像ファイルの場所
	 */
	public String getFilename(final Uri uri) {
		final Cursor c = mContext.getContentResolver().query(uri, new String[]{ MediaColumns.DATA }, null, null, null);
		try {
			while (c.moveToNext()) {
				return c.getString(c.getColumnIndex(MediaColumns.DATA));
			}
		} finally {
			c.close();
		}
		return null;
	}

}