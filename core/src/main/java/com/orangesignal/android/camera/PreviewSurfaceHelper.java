/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * プレビュー用サーフェスのヘルパーインタフェースを提供します。
 * 
 * @author 杉澤 浩二
 */
public interface PreviewSurfaceHelper {

	/**
	 * 実装がサーフェスビューのオーバレイをサポートしている場合は、プッシュバッファ用のサーフェスビューを生成して返します。
	 * 
	 * @param context コンテキスト
	 * @return プッシュバッファ用のサーフェスビュー。または {@code null}
	 */
	SurfaceView createPushBufferSurfaceViewIfNeed(Context context);

	/**
	 * 指定されたプレビュー用のサーフェスビューに可能であれば、オーバレイの設定を行います。
	 * 
	 * @param surface プレビュー用のサーフェスビュー
	 */
	void setZOrderMediaOverlay(SurfaceView surface);

	/**
	 * 実装がサーフェスビューのオーバレイをサポートしている場合は、
	 * カメラのプレビューディスプレーとして指定されたサーフェスホルダーを設定します。
	 * それ以外の場合は、{@code null} を設定します。
	 * 
	 * @param holder サーフェスホルダー
	 * @throws IOException 入出力例外が発生した場合
	 */
	void setPreviewDisplay(SurfaceHolder holder) throws IOException;

}
