/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.orangesignal.android.camera.CameraView.CaptureCallback;

/**
 * カスタムプレビューの簡易な実装を提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(3)
public class SimplePreview extends SurfaceView implements CameraView.Preview, Camera.PreviewCallback, Camera.PictureCallback {

	/**
	 * ログ出力用のタグです。
	 */
	private static final String TAG = "SimplePreview";

	private SurfaceHolder mHolder;

	/**
	 * カメラ操作ヘルパーを保持します。
	 */
	private CameraHelper mCameraHelper;

	/**
	 * フェイスカメラ時に左右を反対にして手鏡状態とするかどうかを保持します。
	 */
	private boolean mFaceMirror = true;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * Simple constructor to use when creating a view from code.
	 * 
	 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
	 */
	public SimplePreview(final Context context) {
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
	public SimplePreview(final Context context, final AttributeSet attrs) {
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
	public SimplePreview(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	private void initialize(final Context context) {
		mHolder = getHolder();
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	/**
	 * フェイスカメラ時に左右を反対にして手鏡状態とするかどうかを返します。<p>
	 * デフォルトは {@code true} です。
	 * 
	 * @return フェイスカメラ時に左右を反対にして手鏡状態とするかどうか
	 */
	public boolean isFaceMirror() {
		return mFaceMirror;
	}

	/**
	 * フェイスカメラ時に左右を反対にして手鏡状態とするかどうかを設定します。
	 * 
	 * @param mirror フェイスカメラ時に左右を反対にして手鏡状態とするかどうか
	 */
	public void setFaceMirror(final boolean mirror) {
		mFaceMirror = mirror;
	}

	//////////////////////////////////////////////////////////////////////////
	// com.orangesignal.android.camera.Preview

	@Override
	public void setCameraHelper(final CameraHelper helper) {
		mCameraHelper = helper;
	}

	/**
	 * この実装は常に {@code true} を返します。
	 */
	@Override
	public boolean isSquareFrameSupported() {
		return true;
	}

	//////////////////////////////////////////////////////////////////////////
	// カメラの接続・切断

	/**
	 * 実装はカメラのプレビューテクスチャまたはプレビューディスプレイを構成します。
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

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・終了

	private boolean mPreviewing;

	private Camera.Size mPreviewSize;
	private byte[] mData;
	private int[] mPixels;
	private Matrix mMatrix;
	private static final Paint mBitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

	@Override
	public void startPreview(final int measurePreviewWidth, final int measurePreviewHeight, final CameraView.CameraStateListener listener) {
		synchronized (this) {
			if (!mPreviewing) {
				if (measurePreviewWidth > 0 && measurePreviewHeight > 0) {
					// プレビューサイズがサポートされている場合は、要求プレビューサイズから最適なプレビューサイズを設定します。
					mCameraHelper.setupOptimalPreviewSizeAndPictureSize(measurePreviewWidth, measurePreviewHeight, 0);
				}
				// サーフェスビューの縦横比をプレビューサイズに合わせるために再レイアウトを要求します。
				requestLayout();

				onStartPreview();

				mCameraHelper.setPreviewCallback(this);
				// FOCUS_MODE_CONTINUOUS_PICTURE や FOCUS_MODE_CONTINUOUS_VIDEO の場合に前回の撮影でオートフォーカスがロックされている可能性があるのでオートフォーカスをリセットします。
//				mCameraHelper.cancelAutoFocus();
				mCameraHelper.startPreview();
				mPreviewing = true;

				if (listener != null) {
					listener.onStartPreview();
				}
			}
		}
	}

	private void onStartPreview() {

		mPreviewSize = mCameraHelper.getPreviewSize();
		mPixels = new int[mPreviewSize.width * mPreviewSize.height];
		mMatrix = new Matrix();

		final int orientation = mCameraHelper.getOrientation();

		// カメラの傾き補正
		switch (orientation % 360) {
			case 0:
				break;
			case 90:
				mMatrix.postRotate(90);
				mMatrix.postTranslate(mPreviewSize.height, 0);
				break;
			case 180:
				mMatrix.postRotate(180);
				mMatrix.postTranslate(mPreviewSize.width, mPreviewSize.height);
				break;
			case 270:
				mMatrix.postRotate(270);
				mMatrix.postTranslate(0, mPreviewSize.width);
				break;
			default:
				throw new IllegalStateException();
		}

		// フェイスカメラプレビュー時のミラー (手鏡) 補正 (プレビューだけ X 軸を反転させる)
		if (mCameraHelper.isFaceCamera() && mFaceMirror) {
			switch (orientation % 180) {
				case 0:		// 横置き
					mMatrix.postScale(-1, 1, mPreviewSize.width / 2, mPreviewSize.height / 2);
					break;
				case 90:	// 縦置き
					mMatrix.postScale(-1, 1, mPreviewSize.height / 2, mPreviewSize.width / 2);
					break;
				default:
					throw new IllegalStateException();
			}
		}

		// ビューとの座標補正
		final int width = getWidth();
		final int height = getHeight();
		float scale = (float) Math.min(width, height) / (float) Math.min(mPreviewSize.width, mPreviewSize.height);
//		if (width == height) {
//			scale = Math.min((float) width / (float) mPreviewSize.height, (float) height / (float) mPreviewSize.width);
//		}
		mMatrix.postScale(scale, scale);
	}

	@Override
	public void onStopPreview() {
		synchronized (this) {
			mPreviewSize = null;
			mPixels = null;
			mMatrix = null;
			mData = null;
			mPreviewing = false;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// android.hardware.Camera.PreviewCallback

	@TargetApi(3)
	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		synchronized (this) {
			if (mData == null) {
				mData = new byte[data.length];
			}

			System.arraycopy(data, 0, mData, 0, data.length);
			nativeYUV420sp2ARGB(mData, mPreviewSize.width, mPreviewSize.height, mPixels);

			final Canvas canvas = mHolder.lockCanvas();
			if (canvas != null) {
				canvas.concat(mMatrix);
				canvas.drawBitmap(mPixels, 0, mPreviewSize.width, 0, 0, mPreviewSize.width, mPreviewSize.height, false, mBitmapPaint);
				mHolder.unlockCanvasAndPost(canvas);
			}
		}

		mCameraHelper.onPreviewFrame(this);
	}

	//////////////////////////////////////////////////////////////////////////
	// 画像キャプチャ

	/**
	 * 画像キャプチャ時のコールバックを一時的に保持します。
	 */
	private CaptureCallback mCaptureCallback;

	@Override
	public void takePicture(final CaptureCallback callback) {
		takePicture(callback, true);
	}

	@Override
	public void takePicture(final CaptureCallback callback, final boolean autoFocus) {
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
	// ネイティブ メソッド

	@TargetApi(3)
	public static native void nativeYUV420sp2ARGB(byte[] data, int width, int height, int[] pixels);

	static {
		System.loadLibrary("orangesignal-camera-simple");
	}

}