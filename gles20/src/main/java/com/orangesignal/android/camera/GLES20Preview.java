/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_MAX_TEXTURE_SIZE;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glViewport;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;

import com.orangesignal.android.graphics.Fps;
import com.orangesignal.android.opengl.GLES20ConfigChooser;
import com.orangesignal.android.opengl.GLES20ContextFactory;
import com.orangesignal.android.opengl.GLES20FramebufferObject;
import com.orangesignal.android.opengl.GLES20FramebufferObjectRenderer;
import com.orangesignal.android.opengl.GLES20Shader;
import com.orangesignal.android.opengl.GLES20Utils;
import com.orangesignal.android.opengl.GLSurfaceView;
import com.orangesignal.android.opengl.Texture;

/**
 * OpenGL ES 2.0 を使用するプレビューの実装を提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20Preview extends GLSurfaceView implements CameraView.Preview, Camera.PictureCallback {

	/**
	 * カメラ操作ヘルパーを保持します。
	 */
	CameraHelper mCameraHelper;

	/**
	 * レンダラーを保持します。
	 */
	Renderer mRenderer;

	/**
	 * フェイスカメラ時に左右を反対にして手鏡状態とするかどうかを保持します。
	 */
	boolean mFaceMirror = true;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コードからインスタンス化する場合に使用するシンプルなコンストラクタです。
	 * 
	 * @param context コンテキスト
	 */
	public GLES20Preview(final Context context) {
		super(context);
		initialize(context);
	}

	/**
	 * XML からインフレートする場合に使用するコンストラクタです。
	 * XML ファイルから生成される時に呼び出され、XML ファイルで指定された属性情報を提供します。
	 * 
	 * @param context コンテキスト
	 * @param attrs XML タグの属性情報
	 */
	public GLES20Preview(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	private void initialize(final Context context) {
		// OpenGL ES 2.0 を使用するように構成します。
		// Android 2.0 以上で OpenGL ES 2.0 を使用可能にするため setEGLContextClientVersion(2) を
		// 直接使用せず setEGLContextClientVersion(2) の内部処理と同様に、
		// OpenGL ES 2.0 向けの EGLContextFactory と EGLConfigChooser を使用します。
		setEGLConfigChooser(new GLES20ConfigChooser(false));
		setEGLContextFactory(new GLES20ContextFactory());

		mRenderer = new Renderer();
		setRenderer(mRenderer);

		// requestRender によって手動で描画を行います。
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * フェイスカメラ時に左右を反対にして手鏡状態とするかどうかを返します。<p>
	 * デフォルトは {@code true} です。
	 * 
	 * @return フェイスカメラ時に左右を反対にして手鏡状態とするかどうか
	 */
	public final boolean isFaceMirror() {
		return mFaceMirror;
	}

	/**
	 * フェイスカメラ時に左右を反対にして手鏡状態とするかどうかを設定します。
	 * 
	 * @param mirror フェイスカメラ時に左右を反対にして手鏡状態とするかどうか
	 */
	public final void setFaceMirror(final boolean mirror) {
		mFaceMirror = mirror;
	}

	/**
	 * 指定された GLSL プログラマブルシェーダーオブジェクトのコレクションをレンダラーへ設定します。
	 * 
	 * @param shaders プレビュー用の GLSL プログラマブルシェーダーオブジェクトのコレクション
	 */
	public void setShader(final GLES20Shader shader) {
		queueEvent(new Runnable() {
			@Override
			public void run() {
				mRenderer.setShader(shader);
			}
		});
	}

	/**
	 * 指定されたテクスチャオブジェクトを設定します。
	 * 
	 * @param texture テクスチャオブジェクト
	 */
	public void setInputTexture(final Texture texture) {
		queueEvent(new Runnable() {
			@Override
			public void run() {
				mRenderer.setTexture(texture);
			}
		});
	}

	public void setFps(final Fps fps) {
		queueEvent(new Runnable() {
			@Override
			public void run() {
				mRenderer.setFps(fps);
			}
		});
	}

	//////////////////////////////////////////////////////////////////////////

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
	 * この実装は何も行いません。
	 */
	@Override public void onOpenCamera() {}

	/**
	 * この実装は何も行いません。
	 */
	@Override public void onReleaseCamera() {}

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・終了

	private boolean mPreviewing;

	private int mMeasurePreviewWidth;
	private int mMeasurePreviewHeight;
	private CameraView.CameraStateListener mCameraStateListener;
	private boolean mWaitingStartPreview;

	@Override
	public void startPreview(final int measurePreviewWidth, final int measurePreviewHeight, final CameraView.CameraStateListener listener) {
		synchronized (this) {
			mMeasurePreviewWidth = measurePreviewWidth;
			mMeasurePreviewHeight = measurePreviewHeight;
			mCameraStateListener = listener;

			if (mRenderer.mMaxTextureSize != 0) {
				startPreview();
			} else {
				mWaitingStartPreview = true;
			}
		}
	}

	void onRendererInitialized() {
		if (mWaitingStartPreview) {
			mWaitingStartPreview = false;
			startPreview();
		}
	}

	private void startPreview() {
		synchronized (this) {
			mPreviewing = false;

			if (mMeasurePreviewWidth > 0 && mMeasurePreviewHeight > 0) {
				// プレビューサイズがサポートされている場合は、要求プレビューサイズから最適なプレビューサイズを設定します。
				mCameraHelper.setupOptimalPreviewSizeAndPictureSize(mMeasurePreviewWidth, mMeasurePreviewHeight, mRenderer.mMaxTextureSize);
			}
			// サーフェスビューの縦横比をプレビューサイズに合わせるために再レイアウトを要求します。
			requestLayout();

			queueEvent(new Runnable() {
				@Override
				public void run() {
					mRenderer.onStartPreview();
				}
			});
		}
	}

	void onStartPreviewFinished() {
		synchronized (this) {
			if (!mPreviewing && mCameraHelper.isOpened()) {
				// FOCUS_MODE_CONTINUOUS_PICTURE や FOCUS_MODE_CONTINUOUS_VIDEO の場合に前回の撮影でオートフォーカスがロックされている可能性があるのでオートフォーカスをリセットします。
//				mCameraHelper.cancelAutoFocus();

				// Camera.startPreview を GL スレッド上で行うと Android 4.0.3 環境の Motorola XOOM などで、
				// 正常に処理されないので、GLSurfaceView.Renderer ではなくコールバックを受けて GLSurfaceView 上で行います。
				mCameraHelper.startPreview();
				mPreviewing = true;

				if (mCameraStateListener != null) {
					mCameraStateListener.onStartPreview();
					mCameraStateListener = null;
				}
			}
		}
	}

	@Override
	public void onStopPreview() {
		synchronized (this) {
			mWaitingStartPreview = false;
			mPreviewing = false;
		}
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

		queueEvent(new Runnable() {
			@Override
			public void run() {
				mRenderer.capture();
			}
		});
	}

	public void capture(final CameraView.CaptureCallback callback) {
		mCaptureCallback = callback;

		queueEvent(new Runnable() {
			@Override
			public void run() {
				mRenderer.capture();
			}
		});
	}

	void onImageCapture(final Bitmap bitmap) {
		if (!mCaptureCallback.onImageCapture(bitmap) && bitmap != null) {
			bitmap.recycle();
		}
		mCaptureCallback = null;
	}

	//////////////////////////////////////////////////////////////////////////
	// レンダラー

	@TargetApi(Build.VERSION_CODES.FROYO)
	private final class Renderer extends GLES20FramebufferObjectRenderer implements PreviewTexture.OnFrameAvailableListener {

		/**
		 * ログ出力用のタグです。
		 */
		private static final String TAG = "GLES20Preview.Renderer";

		/**
		 * コールバック通知用の {@link Handler} を保持します。
		 */
		private final Handler mHandler = new Handler();

		private PreviewTexture mPreviewTexture;
		private boolean mUpdateSurface = false;

		private Texture mImageTexture;
		private boolean mUploadTexture;

		/**
		 * カメラのテクスチャ識別子を保持します。
		 */
		private int mTexName;

		private float[] mMVPMatrix  = new float[16];		// 下の三つを乗算した行列
		private float[] mProjMatrix = new float[16];	// 投影変換用行列
		private float[] mMMatrix    = new float[16];		// モデルビュー変換用行列
		private float[] mVMatrix    = new float[16];		// 視野変換用行列
		private float[] mSTMatrix   = new float[16];
		private float mCameraRatio  = 1.0f;

		private GLES20FramebufferObject mFramebufferObject;
		private GLES20PreviewShader mPreviewShader;
		private GLES20PreviewShader mImageShader;

		private GLES20Shader mShader;
		private boolean mIsNewShader;

		/**
		 * 最大テクスチャサイズを保持します。
		 */
		int mMaxTextureSize;

		//////////////////////////////////////////////////////////////////////////
		// コンストラクタ

		/**
		 * デフォルトコンストラクタです。
		 */
		public Renderer() {
			// ST マトリックスを初期化します。
			Matrix.setIdentityM(mSTMatrix, 0);
		}

		//////////////////////////////////////////////////////////////////////////
		// パブリックメソッド

		public void setShader(final GLES20Shader shader) {
			if (mShader != null) {
				mShader.release();
			}
			if (shader != null) {
				mIsNewShader = true;
			}
			mShader = shader;
			mIsNewShader = true;
			requestRender();
		}

		/**
		 * プレビューを開始するために {@link com.orangesignal.android.camera.CameraView.Preview} を実装した {@link GLSurfaceView} から呼び出されます。
		 */
		public void onStartPreview() {
			// モデルマトリックスを初期化して角度を調整します。
			Matrix.setIdentityM(mMMatrix, 0);
			Matrix.rotateM(mMMatrix, 0, -mCameraHelper.getOptimalOrientation(), 0.0f, 0.0f, 1.0f);
			if (mCameraHelper.isFaceCamera() && !mFaceMirror) {
				Matrix.scaleM(mMMatrix, 0, 1.0f, -1.0f, 1.0f);
			}

			// Android カメラプレビューサイズのアスペクト非を求めます。
			final Camera.Size previewSize = mCameraHelper.getPreviewSize();
			mCameraRatio = (float) previewSize.width / previewSize.height;

			try {
				mPreviewTexture.setup(mCameraHelper);
			} catch (final IOException e) {
				Log.e(TAG, "Cannot set preview texture target!");
			}

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onStartPreviewFinished();
				}
			});
		}

		public void setTexture(final Texture texture) {
			synchronized (this) {
				if (mImageTexture != null) {
					mImageTexture.release();
				}
				Matrix.setIdentityM(mMMatrix, 0);
				mImageTexture = texture;
				mUploadTexture = true;
			}
			requestRender();
		}

		/**
		 * Android 2.3 の API レベルを表します。
		 */
		private static final int GINGERBREAD = 9;

		/**
		 * 画像をキャプチャして、{@link CameraView.CaptureCallback#onImageCapture(Bitmap)} を呼び出してコールバックします。
		 */
		public void capture() {
			final Bitmap bitmap;
			if (mCameraHelper != null) {
				bitmap= getBitmap(mCameraHelper.getOrientation(), Integer.parseInt(Build.VERSION.SDK) < GINGERBREAD && mCameraHelper.isFaceCamera());
			} else {
				bitmap= getBitmap();
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onImageCapture(bitmap);
				}
			});
		}

		//////////////////////////////////////////////////////////////////////////
		// com.orangesignal.android.opengl.GLES20FramebufferObjectRenderer

		@Override
		public void onSurfaceCreated(final EGLConfig config) {
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

			final int[] args = new int[1];

			// カメラのテクスチャ識別子を生成します。
			glGenTextures(args.length, args, 0);
			mTexName = args[0];

			/*
			 * Create the PreviewTexture that will feed this textureID, and pass it to the camera
			 */
			mPreviewTexture = GLES20PreviewTextureFactory.newPreviewTexture(mTexName);
			mPreviewTexture.setOnFrameAvailableListener(this);

			// カメラのテクスチャ識別子に対応するテクスチャオブジェクトを作成します。
			glBindTexture(mPreviewTexture.getTextureTarget(), mTexName);
			GLES20Utils.setupSampler(mPreviewTexture.getTextureTarget(), GL_LINEAR, GL_NEAREST);
			// 現在のテクスチャへの紐付けを解除します。
			glBindTexture(GL_TEXTURE_2D, 0);

			mFramebufferObject = new GLES20FramebufferObject();
			mPreviewShader = new GLES20PreviewShader(mPreviewTexture.getTextureTarget());
			mPreviewShader.setup();
			mImageShader = new GLES20PreviewShader(GL_TEXTURE_2D);
			mImageShader.setup();

			// カメラ (ビューマトリックス) を初期化します。
			Matrix.setLookAtM(mVMatrix, 0,
					0.0f, 0.0f, 5.0f,	// カメラの視点 (x, y, z)
					0.0f, 0.0f, 0.0f,	// カメラの焦点 (center-x, center-y, center-z)
					0.0f, 1.0f, 0.0f	// カメラの上方向 (up-x, ip-y, up-z)
				);

			synchronized (this) {
				mUpdateSurface = false;
			}
			if (mImageTexture != null) {
				mUploadTexture = true;
			}
			if (mShader != null) {
				mIsNewShader = true;
			}

			// 最大テクスチャサイズを取得して GLSurfaceView へ通知します。
			glGetIntegerv(GL_MAX_TEXTURE_SIZE, args, 0);
			mMaxTextureSize = args[0];

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onRendererInitialized();
				}
			});
		}

		@Override
		public void onSurfaceChanged(final int width, final int height) {
			mFramebufferObject.setup(width, height);
			mPreviewShader.setFrameSize(width, height);
			mImageShader.setFrameSize(width, height);
			if (mShader != null) {
				mShader.setFrameSize(width, height);
			}

			final float aspectRatio = (float) width / height;
			Matrix.frustumM(mProjMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 5, 7);
		}

		@Override
		public void onDrawFrame(final GLES20FramebufferObject fbo) {

			////////////////////////////////////////////////////////////
			// 描画に必要なリソースを準備します。

			// カメラからの新しいフレーム画像が利用可能な場合はテクスチャを更新します。
			synchronized (this) {
				if (mUpdateSurface) {
					mPreviewTexture.updateTexImage();
					mPreviewTexture.getTransformMatrix(mSTMatrix);
					mUpdateSurface = false;
				}
			}

			// 静的な画像データの使用を要求されている場合は、静的な画像データでテクスチャを更新します。
			if (mUploadTexture) {
				mImageTexture.setup();
				mCameraRatio = (float) mImageTexture.getWidth() / mImageTexture.getHeight();
				Matrix.setIdentityM(mSTMatrix, 0);
				mUploadTexture = false;
			}

			// 新しいシェーダーオブジェクトが指定された場合は、シェーダーオブジェクトを使用可能な状態に構成します。
			if (mIsNewShader) {
				if (mShader != null) {
					mShader.setup();
					mShader.setFrameSize(fbo.getWidth(), fbo.getHeight());
				}
				mIsNewShader = false;
			}

			////////////////////////////////////////////////////////////
			// 描画

			// ユーザー指定のカスタムシェーダーが指定されている場合は、プレビューテクスチャ描画用の FBO へ切り替えます。
			if (mShader != null) {
				mFramebufferObject.enable();
				glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());
			}

			glClear(GL_COLOR_BUFFER_BIT);

			// MVP マトリックスを計算します。
			Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);			// 視野行列とモデルビュー行列を乗算します。
			Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);	// 投影行列と乗算します。

			// プレビューを描画します。
			if (mImageTexture != null) {
				mImageShader.draw(mImageTexture.getTexName(), mMVPMatrix, mSTMatrix, mCameraRatio);
			} else {
				mPreviewShader.draw(mTexName, mMVPMatrix, mSTMatrix, mCameraRatio);
			}

			// ユーザー指定のカスタムシェーダーが指定されている場合は、オフスクリーン用の FBO へ切り替えてカスタムシェーダーによる描画を行います。
			if (mShader != null) {
				fbo.enable();
				glViewport(0, 0, fbo.getWidth(), fbo.getHeight());
				glClear(GL_COLOR_BUFFER_BIT);
				mShader.draw(mFramebufferObject.getTexName(), fbo);
			}
		}

		//////////////////////////////////////////////////////////////////////
		// com.orangesignal.android.camera.preview.PreviewTexture.OnFrameAvailableListener

		@Override
		public synchronized void onFrameAvailable(final PreviewTexture previewTexture) {
			/*
			 * For simplicity, PreviewTexture calls here when it has new data
			 * available. Call may come in from some random thread, so let's be
			 * safe and use synchronize. No OpenGL calls can be done here.
			 */
			mUpdateSurface = true;
			requestRender();
		}

	}

}