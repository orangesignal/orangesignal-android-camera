/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * プレビューを表示するだけのシンプルな実装を提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class DefaultPreview extends SurfaceView implements CameraView.Preview, Camera.PictureCallback, GestureDetector.OnGestureListener {

	/**
	 * ログ出力用のタグです。
	 */
	private static final String TAG = "DefaultPreview";

	/**
	 * カメラ操作ヘルパーを保持します。
	 */
	private CameraHelper mCameraHelper;

	private GestureDetector mGestureDetector;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * Simple constructor to use when creating a view from code.
	 * 
	 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
	 */
	public DefaultPreview(final Context context) {
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
	public DefaultPreview(final Context context, final AttributeSet attrs) {
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
	public DefaultPreview(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	/**
	 * このビューを初期化します。<p>
	 * 実装は単に {@link android.view.SurfaceHolder} へ {@link android.view.SurfaceHolder#SURFACE_TYPE_PUSH_BUFFERS} を設定します。
	 * 
	 * @param context コンテキスト
	 */
	@SuppressWarnings("deprecation")
	private void initialize(final Context context) {
		mGestureDetector = new GestureDetector(context, this);

		// deprecated setting, but required on Android versions prior to 3.0
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	//////////////////////////////////////////////////////////////////////////
	// com.orangesignal.android.camera.Preview

	@Override
	public void setCameraHelper(final CameraHelper helper) {
		mCameraHelper = helper;
	}

	/**
	 * この実装は常に {@code false} を返します。
	 */
	@Override
	public boolean isSquareFrameSupported() {
		return false;
	}

	/**
	 * この実装は何も行いません。
	 */
	@Override
	public void onOpenCamera() {
	}

	/**
	 * この実装は何も行いません。
	 */
	@Override
	public void onReleaseCamera() {
	}

	@Override
	public void startPreview(final int measurePreviewWidth, final int measurePreviewHeight, final CameraView.CameraStateListener listener) {
		if (measurePreviewWidth > 0 && measurePreviewHeight > 0) {
			// プレビューサイズがサポートされている場合は、要求プレビューサイズから最適なプレビューサイズを設定します。
			mCameraHelper.setupOptimalPreviewSizeAndPictureSize(measurePreviewWidth, measurePreviewHeight, 0);
		}
		// サーフェスビューの縦横比をプレビューサイズに合わせるために再レイアウトを要求します。
		requestLayout();

		// カメラと画面の向きを計算します。
		mCameraHelper.setDisplayOrientation(mCameraHelper.getOptimalOrientation());
		// FOCUS_MODE_CONTINUOUS_PICTURE や FOCUS_MODE_CONTINUOUS_VIDEO の場合に前回の撮影でオートフォーカスがロックされている可能性があるのでオートフォーカスをリセットします。
//		mCameraHelper.cancelAutoFocus();
		try {
			mCameraHelper.setPreviewDisplay(getHolder());
		} catch (IOException e) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", e);
			throw new IllegalStateException(e.getMessage(), e);
		}
		mCameraHelper.startPreview();

		if (listener != null) {
			listener.onStartPreview();
		}
	}

	/**
	 * この実装は何も行いません。
	 */
	@Override
	public void onStopPreview() {
	}

	//////////////////////////////////////////////////////////////////////////
	// 画像キャプチャ

	/**
	 * 画像キャプチャ時のコールバックを一時的に保持します。
	 */
	private CameraView.CaptureCallback mCaptureCallback;

	@Override
	public void takePicture(final CameraView.CaptureCallback callback) {
		takePicture(callback, true);
	}

	@Override
	public void takePicture(final CameraView.CaptureCallback callback, final boolean autoFocus) {
		mCaptureCallback = callback;
		mCameraHelper.takePicture(this, autoFocus);
	}

	@Override
	public void onPictureTaken(final byte[] data, final Camera camera) {
		// takePicture してもプレビューが停止しない仕様違反な端末 (Xperia P22i) があるためプレビューを停止させます。
		mCameraHelper.stopPreview();

		final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		if (!mCaptureCallback.onImageCapture(bitmap) && bitmap != null) {
			bitmap.recycle();
		}
		mCaptureCallback = null;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	//////////////////////////////////////////////////////////////////////////
	// GestureDetector.OnGestureListener

	@Override
	public boolean onDown(final MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(final MotionEvent e) {
		onTap(e);
	}

	@Override
	public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(final MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(final MotionEvent e) {
		return onTap(e);
	}

	//////////////////////////////////////////////////////////////////////////

	private static final int AREA_SIZE = 2000;
	private static final int AREA_HALF_SIZE = AREA_SIZE / 2;
	private static final int DEFAULT_AREA_WEIGHT = 1000;

	private boolean onTap(final MotionEvent event) {
		boolean result = false;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// View 座標からプレビュー座標へ変換します。
			final double x = event.getX() / getWidth()  * AREA_SIZE - AREA_HALF_SIZE;
			final double y = event.getY() / getHeight() * AREA_SIZE - AREA_HALF_SIZE;

			// カメラの向きから座標を回転させます。
			final int angle = mCameraHelper.getOrientation() * -1;
			final int x2 = Math.min(Math.max((int) Math.round(x * Math.cos(angle) - y * Math.sin(angle)), -AREA_HALF_SIZE), AREA_HALF_SIZE);
			final int y2 = Math.min(Math.max((int) Math.round(x * Math.sin(angle) + y * Math.cos(angle)), -AREA_HALF_SIZE), AREA_HALF_SIZE);
			final int size = (int) Math.max(event.getSize() / 2, 10);

			final CameraHelper.AreaCompat area = new CameraHelper.AreaCompat(new Rect(
					Math.max(x2 - size, -AREA_HALF_SIZE),
					Math.max(y2 - size, -AREA_HALF_SIZE),
					Math.min(x2 + size,  AREA_HALF_SIZE),
					Math.min(y2 + size,  AREA_HALF_SIZE)
				), DEFAULT_AREA_WEIGHT);

			if (mCameraHelper.getMaxNumFocusAreas() > 0) {
				// View 座標をカメラ座標へ変換します。
				mCameraHelper.setFocusAreas(area);
				result = true;
			}
			if (mCameraHelper.getMaxNumMeteringAreas() > 0) {
				// View 座標をカメラ座標へ変換します。
				mCameraHelper.setMeteringAreas(area);
				result = true;
			}
		}

		return result;
	}

}