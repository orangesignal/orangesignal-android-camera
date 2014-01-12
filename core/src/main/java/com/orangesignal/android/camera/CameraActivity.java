/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * {@link CameraView} と {@link CameraHelper} をサポートするカメラ用のアクティビティを提供します。<p>
 * 
 * 例
 * <pre>
 * public class MainActivity extends CameraActivity {
 * 
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         
 *         CameraView camera = new CameraView(this);
 *         camera.setId(com.orangesignal.android.camera.R.id.camera);
 *         setContentView(camera);
 *     }
 * 
 * }
 * </pre>
 * 
 * @author 杉澤 浩二
 */
public class CameraActivity extends Activity {

	/**
	 * Android 2.2 の API レベルを表します。
	 */
	private static final int FROYO = 8;

	private CameraView mCameraView;

	//////////////////////////////////////////////////////////////////////////
	// オーバーライド メソッド

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// カメラ画面ではスクリーンをオフにしないようにします。
		// CPU をフル稼働させたい訳ではないので android.permission.WAKE_LOCK ではなく FLAG_KEEP_SCREEN_ON を使用します。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// タイトルバーなしのフルスクリーンで画面が構成されるようにします。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (Integer.parseInt(Build.VERSION.SDK) < FROYO) {
			// Android 2.2 未満の環境ではカメラの縦置きは機種依存なので横置き専用とします。
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mCameraView != null && mCameraView.isAutoStart()) {
			mCameraView.startPreview();
		}
	}

	@Override
	protected void onPause() {
		if (mCameraView != null) {
			mCameraView.stopPreview();
		}
		super.onPause();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		mCameraView = (CameraView) findViewById(R.id.camera);
		if (mCameraView == null) {
			throw new RuntimeException("Your content must have a CameraView whose id attribute is 'com.orangesignal.android.camera.R.id.camera'");
		}
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * カメラビューを返します。
	 * 
	 * @return カメラビュー
	 */
	public CameraView getCameraView() {
		return mCameraView;
	}

	/**
	 * カメラ操作ヘルパーを返します。
	 * 
	 * @return カメラ操作ヘルパー
	 */
	public CameraHelper getCameraHelper() {
		return mCameraView.getCameraHelper();
	}

}