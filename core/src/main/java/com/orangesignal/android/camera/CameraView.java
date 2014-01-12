/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.orangesignal.android.view.DisplayHelperFactory;

/**
 * カメラ操作をサポートするビューを提供します。<p>
 * 
 * レイアウトファイルの例
 * <pre>
 * &lt;com.orangesignal.android.camera.CameraView
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     xmlns:app="http://schemas.android.com/apk/res-auto"
 *     android:id="@id/camera"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:background="@android:color/transparent" /&gt;
 * </pre>
 * 
 * @author 杉澤 浩二
 */
public class CameraView extends ViewGroup implements SurfaceHolder.Callback {

	/**
	 * {@link CameraView} のプレビュー機能をサポートするためのインタフェースを提供します。
	 */
	public interface Preview {

		/**
		 * カメラヘルパーを設定します。
		 * 
		 * @param helper カメラヘルパー
		 */
		void setCameraHelper(CameraHelper helper);

		/**
		 * 正方形フレームでのプレビューをサポートしているかどうかを返します。
		 * 
		 * @return 正方形フレームでのプレビューをサポートしているかどうか
		 */
		boolean isSquareFrameSupported();

		/**
		 * カメラへ接続した場合に呼び出されます。
		 */
		void onOpenCamera();

		/**
		 * カメラから切断した場合に呼び出されます。
		 */
		void onReleaseCamera();

		/**
		 * プレビューを開始するために呼び出されます。
		 * 
		 * @param measurePreviewWidth 要求プレビュー幅
		 * @param measurePreviewHeight 要求プレビュー高さ
		 * @param l {@link CameraStateListener}
		 */
		void startPreview(int measurePreviewWidth, int measurePreviewHeight, CameraStateListener l);

		/**
		 * プレビューが停止した時に呼び出されます。
		 * 実装はテクスチャやバッファの確保を行っている場合、このタイミングで解放処理などを行うことができます。
		 */
		void onStopPreview();

		/**
		 * 画像キャプチャを要求します。
		 * 
		 * @param callback コールバック
		 */
		void takePicture(CaptureCallback callback);

		/**
		 * 画像キャプチャを要求します。
		 * 
		 * @param callback コールバック
		 * @param autoFocus
		 */
		void takePicture(CaptureCallback callback, boolean autoFocus);

	}

	/**
	 * プレビューサイズとピクチャーサイズ設定戦略の列挙型を提供します。
	 */
	public static enum PreviewSizePolicy {
		/**
		 * 画面サイズを基準とする戦略です。
		 */
		DISPLAY,

		/**
		 * ビューを基準とする戦略です。
		 */
		VIEW,

		/**
		 * プレビューを基準とする戦略です。
		 */
		PREVIEW,

		/**
		 * プレビューサイズを指定する戦略です。
		 */
		MANUAL
	}

	/**
	 * カメラへの接続や切断などカメラの状態を監視するリスナーのインタフェースを提供します。
	 * このインタフェースを実装したクラスでは、カメラ接続時や切断時にアニメーションを行ったり、ボタンやアイコンの表示状態の変更を容易に行うことが可能となります。
	 */
	public interface CameraStateListener {

		void onOpenCamera();

		void onStartPreview();

		void onReleaseCamera();

	}

	/**
	 * エラーを通知するリスナーのインタフェースを提供します。
	 */
	public interface OnErrorListener {

		static final int ERROR_UNKNOWN = -1;
		static final int ERROR_CAMERA_INITIAL_OPEN =  0;

		void onError(int error, Exception e, CameraView view);

	}

	/**
	 * Callback interface used to supply image data from a photo capture.
	 */
	public interface CaptureCallback {

		/**
		 * Called when image data is available after a picture is taken.
		 * 
		 * @param 写真データのビットマップ
		 * @return 呼出し先がビットマップを消費したかどうか
		 */
		boolean onImageCapture(Bitmap bitmap);

	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * ログ出力用のタグです。
	 */
	private static final String TAG = "CameraView";

	/**
	 * カメラ操作ヘルパーを保持します。
	 */
	private CameraHelper mCameraHelper;

	private PreviewSurfaceHelper mPreviewSurfaceHelper;

	/**
	 * プレビュー表示用の {@link SurfaceView} を保持します。
	 */
	private Preview mPreview;

	/**
	 * プレビューが {@link Camera.PreviewCallback} を使用するかどうかを保持します。
	 */
	private boolean mUsePreviewCallback;

	/**
	 * プレビューが {@link Camera.PreviewCallback} を使用する場合にプッシュバッファとして使用するサーフェスを保持します。
	 */
	private SurfaceView mPushBufferSurface;

	/**
	 * プレビューを自動的に開始するかどうかを保持します。
	 */
	private boolean mAutoStart = true;

	/**
	 * プレビュー表示用の {@link SurfaceView} をこのビューの中央へ配置するかどうかを保持します。
	 */
	private boolean mPreviewAlignCenter;

	/**
	 * 正方形プレビューであるかどうかを保持します。
	 */
	private boolean mSquareFrame;

	/**
	 * プレビューへ重ねて表示させるオーバーレイ用のビューを保持します。
	 */
	private View mOverlayView;

	/**
	 * プレビューサイズとピクチャーサイズ設定戦略を保持します。
	 */
	private PreviewSizePolicy mPreviewSizePolicy = PreviewSizePolicy.DISPLAY;

	/**
	 * カメラの状態を監視するリスナーを保持します。
	 */
	private CameraStateListener mCameraStateListener;

	/**
	 * エラーを通知するリスナーを保持します。
	 */
	private OnErrorListener mOnErrorListener;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * Simple constructor to use when creating a view from code.
	 * 
	 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
	 */
	public CameraView(final Context context) {
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
	public CameraView(final Context context, final AttributeSet attrs) {
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
	public CameraView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	private void initialize(final Context context) {
		mCameraHelper = CameraHelperFactory.newCameraHelper(context);
		mPreviewSurfaceHelper = PreviewSurfaceHelperFactory.newPreviewSurfaceHelper(mCameraHelper);

		// デフォルトのプレビューを設定します。
		setPreview(new DefaultPreview(context), false);
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
		// requestLayout で子ビューの再レイアウトを期待した要求を行っているので、changed フラグは見ません。

		final int width = r - l;
		final int height = b - t;
		final int count = getChildCount();

		View preview = null;

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child != null) {
				if (child.equals(mPreview)) {
					int childWidth = width - getPaddingLeft() - getPaddingRight();
					int childHeight = height - getPaddingTop() - getPaddingBottom();;

					if (mPreview.isSquareFrameSupported() && mSquareFrame) {
						// 正方形プレビューの場合
						final int size = Math.min(childWidth, childHeight);
						childWidth = size;
						childHeight = size;
					} else if (mCameraHelper.isOpened()) {
						// プレビュー描画用の子ビューの大きさをプレビューサイズの縦横比と合うように調整します。
						final Camera.Size previewSize = mCameraHelper.getPreviewSize();
						if (previewSize != null) {
							// カメラのプレビューサイズは常に横置きを前提とした幅と高さを返すので、端末の向きによって幅と高さを入れ換えて計算させます。
							final int previewWidth;
							final int previewHeight;
							switch (getResources().getConfiguration().orientation) {
								// 縦置きの場合
								case Configuration.ORIENTATION_PORTRAIT:
									previewWidth = previewSize.height;
									previewHeight = previewSize.width;
									break;
								// 縦置きでない場合
								default:
									previewWidth = previewSize.width;
									previewHeight = previewSize.height;
									break;
							}
							final double scale = Math.min((double) childWidth / (double) previewWidth, (double) childHeight / (double) previewHeight);
							childWidth = (int) Math.floor(previewWidth * scale);
							childHeight = (int) Math.floor(previewHeight * scale);
						}
					}

					final int childLeft;
					final int childTop;
					if (mPreviewAlignCenter) {
						childLeft = (width - childWidth) / 2;
						childTop = (height - childHeight) / 2;
					} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						childLeft = (width - childWidth) / 2;
						childTop = 0;
					} else {
						childLeft = 0;
						childTop = (height - childHeight) / 2;
					}
					child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

					preview = child;
					break;
				}
			}
		}
		if (preview != null) {
			if (mPushBufferSurface != null) {
				mPushBufferSurface.layout(preview.getLeft(), preview.getTop(), preview.getRight(), preview.getBottom());
			}
			if (mOverlayView != null) {
				mOverlayView.layout(preview.getLeft(), preview.getTop(), preview.getRight(), preview.getBottom());
			}
		}
	}

	@Override
	public void removeAllViews() {
		mOverlayView = null;
		super.removeAllViews();
	}

	@Override
	public void removeView(final View view) {
		if (mOverlayView != null && mOverlayView.equals(view)) {
			mOverlayView = null;
		}
		super.removeView(view);
	}

	@Override
	public void removeViewAt(final int index) {
		final View view = getChildAt(index);
		if (mOverlayView != null && mOverlayView.equals(view)) {
			mOverlayView = null;
		}
		super.removeViewAt(index);
	}

	//////////////////////////////////////////////////////////////////////////
	// android.view.SurfaceHolder.Callback

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		try {
			openCamera(mCameraHelper.getCameraId());
		} catch (final RuntimeException e) {
			if (mOnErrorListener != null) {
				mOnErrorListener.onError(OnErrorListener.ERROR_CAMERA_INITIAL_OPEN, e, this);
			} else {
				throw e;
			}
		}
	}

	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		if (!mCameraHelper.isOpened()) {
			return;
		}

		if (mUsePreviewCallback) {
			try {
				mPreviewSurfaceHelper.setPreviewDisplay(holder);
			} catch (IOException e) {
				Log.e(TAG, "IOException caused by setPreviewDisplay()", e);
				throw new IllegalStateException(e.getMessage(), e);
			}
		}

		if (mAutoStart) {
			startPreview();
		}
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		releaseCamera();
	}

	//////////////////////////////////////////////////////////////////////////
	// パブリック メソッド

	/**
	 * カメラ操作ヘルパーを返します。
	 * 
	 * @return カメラ操作ヘルパー
	 */
	public CameraHelper getCameraHelper() {
		return mCameraHelper;
	}

	/**
	 * プレビューを返します。
	 * 
	 * @return プレビュー
	 */
	public Preview getPreview() {
		return mPreview;
	}

	/**
	 * プレビューを設定します。
	 * 
	 * @param preview プレビュー表示用の {@link SurfaceView}
	 */
	public void setPreview(final Preview preview) {
		setPreview(preview, preview != null && !(preview instanceof DefaultPreview));
	}

	/**
	 * プレビューを設定します。
	 * 
	 * @param preview プレビュー表示用の {@link SurfaceView}
	 * @param usePreviewCallback プレビューが {@link Camera.PreviewCallback} を使用するかどうか
	 */
	public void setPreview(final Preview preview, final boolean usePreviewCallback) {
		removePreview();

		mUsePreviewCallback = usePreviewCallback;

		if (preview != null) {
			if (preview instanceof SurfaceView) {
				final SurfaceView surface = (SurfaceView) preview;

				if (usePreviewCallback) {
					mPushBufferSurface = mPreviewSurfaceHelper.createPushBufferSurfaceViewIfNeed(getContext());
				}

				// Install a SurfaceHolder.Callback so we get notified when the
				// underlying surface is created and destroyed.
				if (mPushBufferSurface != null) {
					mPushBufferSurface.getHolder().addCallback(this);
					// プッシュバッファサーフェスよりもプレビューが優先して前面に描画されるように指示します。
					mPreviewSurfaceHelper.setZOrderMediaOverlay(surface);
				} else {
					surface.getHolder().addCallback(this);
				}

				addView(surface, 0);

				if (mPushBufferSurface != null) {
					addView(mPushBufferSurface, 0);
				}
			} else {
//				Class<?> cls = preview.getClass();
//				while (cls != null && !"android.view.TextureView".equals(cls.getName())) {
//					cls = cls.getSuperclass();
//				}
//				if (cls != null) {
//					
//				}
				throw new IllegalArgumentException();
			}
			mPreview = preview;
			mPreview.setCameraHelper(mCameraHelper);
		}
	}

	public void removePreview() {
		if (mPreview != null) {
			if (mPreview instanceof SurfaceView) {
				final SurfaceView surface = (SurfaceView) mPreview;
				surface.getHolder().removeCallback(this);
			}
			if (mPreview instanceof View) {
				final View view = (View) mPreview;
				removeView(view);
			}
			mPreview.setCameraHelper(null);
			mPreview = null;
		}
		if (mPushBufferSurface != null) {
			mPushBufferSurface.getHolder().removeCallback(this);
			removeView(mPushBufferSurface);
			mPushBufferSurface = null;
		}
		mUsePreviewCallback = false;
	}

	/**
	 * プレビューを自動的に開始するかどうかを返します。
	 * 
	 * @return プレビューを自動的に開始するかどうか
	 */
	public boolean isAutoStart() {
		return mAutoStart;
	}

	/**
	 * プレビューを自動的に開始するかどうかを設定します。
	 * 
	 * @param autoStart プレビューを自動的に開始するかどうか
	 */
	public void setAutoStart(final boolean autoStart) {
		mAutoStart = autoStart;
	}

	/**
	 * プレビュー表示用の {@link SurfaceView} をこのビューの中央へ配置するかどうかを返します。
	 * 
	 * @return プレビュー表示用の {@link SurfaceView} をこのビューの中央へ配置するかどうか
	 */
	public boolean isPreviewAlignCenter() {
		return mPreviewAlignCenter;
	}

	/**
	 * プレビュー表示用の {@link SurfaceView} をこのビューの中央へ配置するかどうかを設定します。
	 * 
	 * @param previewAlignCenter プレビュー表示用の {@link SurfaceView} をこのビューの中央へ配置するかどうか
	 */
	public void setPreviewAlignCenter(final boolean previewAlignCenter) {
		mPreviewAlignCenter = previewAlignCenter;
		requestLayout();
	}

	/**
	 * 正方形プレビューであるかどうかを返します。
	 * 
	 * @return 正方形プレビューであるかどうか
	 */
	public boolean isSquareFrame() {
		return mSquareFrame;
	}

	/**
	 * 正方形プレビューであるかどうかを設定します。
	 * {@link Preview} が正方形プレビューをサポートしていない場合は、このビューのレイアウト時に無視されます。
	 * 
	 * @param square 正方形プレビューであるかどうか
	 * @see {@link Preview#isSquareFrameSupported()}
	 */
	public void setSquareFrame(final boolean square) {
		if (mSquareFrame != square) {
			mSquareFrame = square;
			requestLayout();
		}
	}

	/**
	 * プレビューへ重ねて表示させるオーバーレイ用のビューを追加します。
	 * 
	 * @param child
	 */
	public void setOverlayView(final View child) {
		if (mOverlayView != null) {
			removeView(mOverlayView);
		}
		mOverlayView = child;
		if (mOverlayView != null) {
			addView(child);
		}
	}

	/**
	 * プレビューへ重ねて表示させるオーバーレイ用のビューを返します。
	 * 
	 * @return プレビューへ重ねて表示させるオーバーレイ用のビュー。または {@code null}
	 */
	public View getOverlayView() {
		return mOverlayView;
	}

	/**
	 * プレビューサイズとピクチャーサイズ設定戦略を返します。
	 * 
	 * @return プレビューサイズとピクチャーサイズ設定戦略
	 */
	public PreviewSizePolicy getPreviewSizePolicy() {
		return mPreviewSizePolicy;
	}

	/**
	 * プレビューサイズとピクチャーサイズ設定戦略を設定します。
	 * 
	 * @param previewSizePolicy プレビューサイズとピクチャーサイズ設定戦略
	 * @throws IllegalArgumentException {@code previewSizePolicy} に {@code null} が指定された場合
	 */
	public void setPreviewSizePolicy(final PreviewSizePolicy previewSizePolicy) {
		if (previewSizePolicy == null) {
			throw new IllegalArgumentException("PreviewSizePolicy must not be null");
		}
		mPreviewSizePolicy = previewSizePolicy;
	}

	/**
	 * カメラの状態を監視するリスナーを設定します。
	 * 
	 * @param callback カメラの状態を監視するリスナー
	 */
	public void setCameraStateListener(final CameraStateListener callback) {
		mCameraStateListener = callback;
	}

	/**
	 * エラーを通知するリスナーを設定します。
	 * 
	 * @param l エラーを通知するリスナー
	 */
	public void setOnErrorListener(final OnErrorListener l) {
		mOnErrorListener = l;
	}

	//////////////////////////////////////////////////////////////////////////
	// カメラの接続・切断

	/**
	 * 指定されたカメラへ切り替えてプレビューを開始します。
	 * 
	 * @param cameraId 切り替えるカメラ ID
	 */
	public void switchCamera(final int cameraId) {
		openCamera(cameraId);
		startPreview();
	}

	/**
	 * 指定されたカメラを現在のカメラとして接続します。
	 * カメラの状態を監視するリスナーが設定されている場合は、カメラへ接続後に {@link CameraStateListener#onOpenedCamera(CameraView)} を呼び出します。
	 * 
	 * @param cameraId カメラ ID
	 */
	private void openCamera(final int cameraId) {
		synchronized (this) {
			mCameraHelper.openCamera(cameraId);
//			requestLayout();
			mPreview.onOpenCamera();

			if (mCameraStateListener != null) {
				mCameraStateListener.onOpenCamera();
			}
		}
	}

	private void releaseCamera() {
		stopPreview();

		synchronized (this) {
			if (mCameraStateListener != null) {
				mCameraStateListener.onReleaseCamera();
			}
			mCameraHelper.releaseCamera();
			mPreview.onReleaseCamera();
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・停止

	/**
	 * プレビューを開始します。
	 */
	public void startPreview() {
		stopPreview();

		synchronized (this) {
			if (mCameraHelper.isOpened()) {
				// プレビューサイズとピクチャーサイズ設定戦略から要求プレビューサイズを求めます。
				int width;
				int height;
				switch (mPreviewSizePolicy) {
					case DISPLAY: {
						final Point point = DisplayHelperFactory.newDisplayHelper(getContext()).getRawDisplaySize();
						width = point.x;
						height = point.y;
					} break;

					case VIEW: {
						width = getWidth();
						height = getHeight();
					} break;

					case PREVIEW: {
						final View view = (View) mPreview;
						width = view.getWidth();
						height = view.getHeight();
					} break;

					case MANUAL: {
						width = 0;
						height = 0;
					} break;

					default:
						throw new IllegalStateException("Unsupported PreviewSizePolicy type " + mPreviewSizePolicy.toString());
				}

				// プレビューオブジェクトへプレビューの開始を通知して準備をさせてからプレビューを開始します。
				mPreview.startPreview(width, height, mCameraStateListener);
			}
		}
	}

	/**
	 * プレビューを停止します。
	 */
	public void stopPreview() {
		synchronized (this) {
			mCameraHelper.stopPreview();
			mPreview.onStopPreview();
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// キャプチャ

	/**
	 * 画像のキャプチャを要求します。
	 * 
	 * @param callback コールバック
	 */
	public void capture(final CameraView.CaptureCallback callback) {
		capture(callback, true);
	}

	/**
	 * 画像のキャプチャを要求します。
	 * 
	 * @param callback コールバック
	 * @param autoFocus
	 */
	public void capture(final CameraView.CaptureCallback callback, final boolean autoFocus) {
		mPreview.takePicture(callback, autoFocus);
	}

}