/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.media;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * ビットマップを画像コンテンツとして保存するためのユーティリティを提供します。
 * 
 * @author 杉澤 浩二
 */
public final class ImageContentBuilder {

	/**
	 * {@link ContentResolver} を保持します。
	 */
	private final ContentResolver mContentResolver;

	/**
	 * 画像形式を保持します。
	 */
	private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;

	private int mOrientation;

	/**
	 * JPEG 形式の既定の画像品質です。
	 */
	public static final int DEFAULT_JPEG_QUALITY = 100;

	/**
	 * JPEG 形式の画像品質を保持します。<p>
	 * PNG 形式の場合は効果を失います。
	 */
	private int mJpegQuality = DEFAULT_JPEG_QUALITY;

	/**
	 * 既定の名前形式です。
	 */
	public static final String DEFAULT_NAME_FORMAT = "'IMG_'yyyyMMdd_HHmmss";

	/**
	 * 名前形式を保持します。
	 */
	private String mNameFormat = DEFAULT_NAME_FORMAT;

	/**
	 * タイトルを保持します。
	 */
	private String mTitle;

	/**
	 * 表示名を保持します。
	 */
	private String mDisplayName;

	/**
	 * 説明を保持します。
	 */
	private String mDescription;

	private Boolean mFlash;

	/**
	 * 位置情報を保持します。
	 */
	private Location mLocation;

	/**
	 * カスタムグループ名を保持します。
	 */
	private String mGroupName;

	/**
	 * EXIF (エグジフ) 情報を保持します。<p>
	 * この設定は Android 2.0 以降でのみ効果があります。
	 */
	private boolean mExif;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public ImageContentBuilder(final Context context) {
		mContentResolver = context.getContentResolver();
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	/**
	 * 画像形式を設定します。
	 * 
	 * @param compressFormat 画像形式
	 * @return このクラスのインスタンス
	 */
	public ImageContentBuilder setCompressFormat(final Bitmap.CompressFormat compressFormat) {
		mCompressFormat = compressFormat;
		return this;
	}

	/**
	 * 画像形式を返します。
	 * 
	 * @return 画像形式
	 */
	public Bitmap.CompressFormat getCompressFormat() {
		return mCompressFormat;
	}

	public int getOrientation() {
		return mOrientation;
	}

	public ImageContentBuilder setOrientation(final int orientation) {
		mOrientation = orientation;
		return this;
	}

	/**
	 * JPEG 形式の画像品質を設定します。
	 * 
	 * @param quality JPEG 形式の画像品質
	 * @return このクラスのインスタンス
	 */
	public ImageContentBuilder setJpegQuality(final int quality) {
		mJpegQuality = quality;
		return this;
	}

	/**
	 * JPEG 形式の画像品質を返します。
	 * 
	 * @return JPEG 形式の画像品質
	 */
	public int getJpegQuality() {
		return mJpegQuality;
	}

	/**
	 * 名前形式を設定します。
	 * 
	 * @param format 名前形式
	 */
	public ImageContentBuilder setNameFormat(final String format) {
		mNameFormat = format;
		return this;
	}

	/**
	 * 名前形式を返します。
	 * 
	 * @return 名前形式
	 */
	public String getNameFormat() {
		return mNameFormat;
	}

	/**
	 * タイトルを設定します。
	 * 
	 * @param title タイトル
	 * @return このクラスのインスタンス
	 */
	public ImageContentBuilder setTitle(final String title) {
		mTitle = title;
		return this;
	}

	/**
	 * タイトルを返します。
	 * 
	 * @return タイトル
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * 表示名を設定します。
	 * 
	 * @param displayName 表示名
	 * @return このクラスのインスタンス
	 */
	public ImageContentBuilder setDisplayName(final String displayName) {
		mDisplayName = displayName;
		return this;
	}

	/**
	 * 表示名を返します。
	 * 
	 * @return 表示名
	 */
	public String geDisplayName() {
		return mDisplayName;
	}

	/**
	 * 説明を設定します。
	 * 
	 * @param description 説明
	 * @return このクラスのインスタンス
	 */
	public ImageContentBuilder setDescription(final String description) {
		mDescription = description;
		return this;
	}

	/**
	 * 説明を返します。
	 * 
	 * @return 説明
	 */
	public String getDescription() {
		return mDescription;
	}

	public ImageContentBuilder setFlash(final Boolean flash) {
		mFlash = flash;
		return this;
	}

	public Boolean getFlash() {
		return mFlash;
	}

	/**
	 * 位置情報を設定します。
	 * 
	 * @param location 位置情報
	 * @return このクラスのインスタンス
	 */
	public ImageContentBuilder setLocation(final Location location) {
		mLocation = location;
		return this;
	}

	/**
	 * 位置情報を返します。
	 * 
	 * @return 位置情報
	 */
	public Location getLocation() {
		return mLocation;
	}

	/**
	 * カスタムグループ名を設定します。
	 * 
	 * @param groupName カスタムグループ名
	 * @return このクラスのインスタンス
	 */
	public ImageContentBuilder setGroupName(final String groupName) {
		mGroupName = groupName;
		return this;
	}

	/**
	 * カスタムグループ名を返します。
	 * 
	 * @return カスタムグループ名
	 */
	public String getGroupName() {
		return mGroupName;
	}

	/**
	 * EXIF 情報を構成するかどうかを設定します。<p>
	 * この設定は Android 2.0 以降でのみ効果があります。
	 * 
	 * @param exif EXIF 情報を構成するかどうか
	 * @return このクラスのインスタンス
	 */
	public ImageContentBuilder setExif(final boolean exif) {
		mExif = exif;
		return this;
	}

	/**
	 * EXIF 情報を構成するかどうかを返します。
	 * 
	 * @return EXIF 情報を構成するかどうか
	 */
	public boolean isExif() {
		return mExif;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * Android 2.0 の API レベルです。
	 */
	private static final int ECLAIR = 5;

	/**
	 * 指定されたビットマップオブジェクトをデータストアへ保存します。
	 * 
	 * @param source ビットマップオブジェクト
	 * @return 保存した画像コンテンツの URI
	 * @throws IOException 入出力例外が発生した場合
	 */
	public Uri build(final Bitmap source) throws IOException {
		return build(null, source);
	}

	/**
	 * 指定された画像コンテンツの {@link Uri} へ指定されたビットマップオブジェクトを保存します。
	 * 
	 * @param uri 画像コンテンツの {@link Uri}
	 * @param source ビットマップオブジェクト
	 * @return 保存した画像コンテンツの URI
	 * @throws IOException 入出力例外が発生した場合
	 */
	public Uri build(final Uri uri, final Bitmap source) throws IOException {
		final long time = System.currentTimeMillis();
		if (uri == null) {
			final ContentValues values = createContentValues(time);
			final Uri u = mContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			save(u, source, time);
			return u;
		}
		save(uri, source, time);
		return uri;
	}

	private ContentValues createContentValues(final long time) {
		final ContentValues values = new ContentValues();

		values.put(MediaStore.Images.Media.DATE_MODIFIED, time / 1000L);
		values.put(MediaStore.Images.Media.DATE_TAKEN, time);
		values.put(MediaStore.Images.Media.ORIENTATION, mOrientation);

		final String name = new SimpleDateFormat(mNameFormat, Locale.getDefault()).format(new Date(time));

		if (!TextUtils.isEmpty(mTitle)) {
			values.put(MediaStore.Images.Media.TITLE, mTitle);
		} else {
			values.put(MediaStore.Images.Media.TITLE, name);
		}
	
		if (!TextUtils.isEmpty(mDisplayName)) {
			values.put(MediaStore.Images.Media.DISPLAY_NAME, mDisplayName);
		}
		if (!TextUtils.isEmpty(mDescription)) {
			values.put(MediaStore.Images.Media.DESCRIPTION, mDescription);
		}
		if (mLocation != null) {
			values.put(MediaStore.Images.Media.LATITUDE, mLocation.getLatitude());
			values.put(MediaStore.Images.Media.LONGITUDE, mLocation.getLongitude());
		}

		String ext;

		if (Bitmap.CompressFormat.PNG.equals(mCompressFormat)) {
			values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
			ext = "png";
		} else if (Bitmap.CompressFormat.JPEG.equals(mCompressFormat)) {
			values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
			ext = "jpg";
		} else {
			throw new IllegalArgumentException();
		}

		if (!TextUtils.isEmpty(mGroupName)) {
			final File file = new File(
					new StringBuilder().append(Environment.getExternalStorageDirectory()).append(File.separator).append(mGroupName).append(File.separator).toString(),
					new StringBuilder().append(name).append('.').append(ext).toString() 
				);
			values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
		}

		return values;
	}

	@SuppressWarnings("deprecation")
	private void save(final Uri uri, final Bitmap source, final long time) throws IOException {
		try {
			final OutputStream out = mContentResolver.openOutputStream(uri);
			try {
				source.compress(mCompressFormat, mJpegQuality, out);
			} finally {
				try {
					out.close();
				} catch (final IOException e) {}
			}
		} catch (final FileNotFoundException e) {}

		if (mExif && Integer.parseInt(Build.VERSION.SDK) >= ECLAIR) {
			final Cursor c = mContentResolver.query(uri, new String[]{ MediaStore.Images.Media.DATA }, null, null, null);
			// 可能であれば EXIF 情報を書き込みます。
			if (c != null) {
				try {
					while (c.moveToNext()) {
						ExifUtils.save(c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)), new Date(time), mOrientation, mFlash, mLocation);
					}
				} finally {
					c.close();
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// static method

	public static boolean isWritable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

}