/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT_5_6_5;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexSubImage2D;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.opengl.Matrix;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Utils;

/**
 * Android 3.0 未満向けの {@link PreviewTexture} の実装を提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
class GLES20PreviewTexture implements PreviewTexture, Camera.PreviewCallback {

	private OnFrameAvailableListener mOnFrameAvailableListener;

	private int mWidth;
	private int mHeight;
	private int mFormat;
	private final int mTexName;
	private final int mTexTarget;
	private final float[] mTransformMatrix = new float[16];
	private long mTimestamp;

	private CameraHelper mCameraHelper;
	private ByteBuffer mPreviewDataBuffer;

	/**
	 * ロックオブジェクトを保持します。
	 */
	private final ReentrantLock mLock = new ReentrantLock();

	//////////////////////////////////////////////////////////////////////////
	// Transform matrices

/*
	private static final float[] mtxIdentity = new float[]{
		 1,  0,  0,  0,
		 0,  1,  0,  0,
		 0,  0,  1,  0,
		 0,  0,  0,  1,
	};
*/

	private static final float[] mtxFlipH = new float[]{
		-1,  0,  0,  0,
		 0,  1,  0,  0,
		 0,  0,  1,  0,
		 1,  0,  0,  1,
	};

	private static final float[] mtxFlipV = new float[]{
		 1,  0,  0,  0,
		 0, -1,  0,  0,
		 0,  0,  1,  0,
		 0,  1,  0,  1,
	};

/*
	private static final float[] mtxRot90 = new float[]{
		 0,  1,  0,  0,
		-1,  0,  0,  0,
		 0,  0,  1,  0,
		 1,  0,  0,  1,
	};

	private static final float[] mtxRot180 = new float[]{
		-1,  0,  0,  0,
		 0, -1,  0,  0,
		 0,  0,  1,  0,
		 1,  1,  0,  1,
	};

	private static final float[] mtxRot270 = new float[]{
		 0, -1,  0,  0,
		 1,  0,  0,  0,
		 0,  0,  1,  0,
		 0,  1,  0,  1,
	};
*/

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * Construct a new GLES20PreviewTexture to stream images to a given OpenGL texture.
	 * 
	 * @param texName the OpenGL texture object name (e.g. generated via glGenTextures)
	 */
	public GLES20PreviewTexture(final int texName) {
		mWidth = 0;
		mHeight = 0;
		mTexName = texName;
		mTexTarget = GL_TEXTURE_2D;
	}

	//////////////////////////////////////////////////////////////////////////
	// PreviewTexture

	@Override
	public void setOnFrameAvailableListener(final OnFrameAvailableListener l) {
		mOnFrameAvailableListener = l;
	}

	@Override
	public int getTextureTarget() {
		return mTexTarget;
	}

	private static final String SAMSUNG_GALAXY_S = "SC-02B";

	@Override
	public void setup(final CameraHelper camera) throws IOException {
		mCameraHelper = camera;
		mCameraHelper.setPreviewCallback(this);

		final Camera.Size previewSize = camera.getPreviewSize();
		mWidth = previewSize.width;
		mHeight = previewSize.height;

		final int type;

		// Galaxy S の場合は、テクスチャ転送パフォーマンスを向上させるため、RGBA ではなく RGB で処理されるようにします。
		if (SAMSUNG_GALAXY_S.equalsIgnoreCase(Build.MODEL)) {
			mPreviewDataBuffer = GLES20Utils.toByteBuffer(mWidth * mHeight * 3);
			mFormat = GL_RGB;
			type = GL_UNSIGNED_SHORT_5_6_5;
		} else {
			try {
				// GPU 内で変換をしなくてよい RGBA 形式でプレビューデータを処理します。
				mPreviewDataBuffer = GLES20Utils.toByteBuffer(mWidth * mHeight * 4);
				mFormat = GL_RGBA;
			} catch (final OutOfMemoryError e) {
				// アルファ値を持たない RGB 形式でプレビューデータを処理します。 
				mPreviewDataBuffer = GLES20Utils.toByteBuffer(mWidth * mHeight * 3);
				mFormat = GL_RGB;
			}
			type = GL_UNSIGNED_BYTE;
		}

		// プレビュー用のテクスチャを作成します。
		glBindTexture(mTexTarget, mTexName);
		glTexImage2D(mTexTarget, 0, mFormat, mWidth, mHeight, 0, mFormat, type, null);

		// TransformMatrix を計算します。
		Matrix.setIdentityM(mTransformMatrix, 0);
		if (camera.isFaceCamera()) {
			Matrix.multiplyMM(mTransformMatrix, 0, mtxFlipH, 0, mTransformMatrix, 0);
		}
		Matrix.multiplyMM(mTransformMatrix, 0, mtxFlipV, 0, mTransformMatrix, 0);
	}

	@Override
	public void updateTexImage() {
		mLock.lock();
		try {
			glBindTexture(mTexTarget, mTexName);
			glTexSubImage2D(mTexTarget, 0, 0, 0, mWidth, mHeight, mFormat, GL_UNSIGNED_BYTE, mPreviewDataBuffer);
		} finally {
			mLock.unlock();
		}
		mTimestamp = System.nanoTime();
	}

	@Override
	public void getTransformMatrix(final float[] mtx) {
		// NullPointerException が発生するようにするため、mtx の null チェックを意図的に行わない点に注意して下さい。
		if (mtx.length != 16) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(mTransformMatrix, 0, mtx, 0, 16);
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieve the timestamp associated with the texture image set by the most recent call to updateTexImage.
	 *
	 * This timestamp is in nanoseconds, and is normally monotonically increasing. The timestamp
	 * should be unaffected by time-of-day adjustments, and for a camera should be strictly
	 * monotonic but for a MediaPlayer may be reset when the position is set.  The
	 * specific meaning and zero point of the timestamp depends on the source providing images to
	 * the SurfaceTexture. Unless otherwise specified by the image source, timestamps cannot
	 * generally be compared across SurfaceTexture instances, or across multiple program
	 * invocations. It is mostly useful for determining time offsets between subsequent frames.
	 */
	public long getTimestamp() {
		return mTimestamp;
	}

	public void release() {
		mWidth = 0;
		mHeight = 0;
		mCameraHelper = null;
		mPreviewDataBuffer = null;
	}

	//////////////////////////////////////////////////////////////////////////
	// android.hardware.Camera.PreviewCallback

	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		if (data != null) {
			mLock.lock();
			try {
				if (mFormat == GL_RGBA) {
					nativeYUV420sp2RGBA(data, mWidth, mHeight, mPreviewDataBuffer);
				} else {
					nativeYUV420sp2RGB(data, mWidth, mHeight, mPreviewDataBuffer);
				}
			} finally {
				mLock.unlock();
			}

			if (mOnFrameAvailableListener != null) {
				mOnFrameAvailableListener.onFrameAvailable(this);
			}
		}

		// 次のフレームデータが通知されるようにします。
		mCameraHelper.onPreviewFrame(this);
	}

	//////////////////////////////////////////////////////////////////////////
	// ネイティブ メソッド

	static {
		System.loadLibrary("orangesignal-gles20");
	}

	private static native void nativeYUV420sp2RGB(byte[] yuv, int width, int height, ByteBuffer buffer);
	private static native void nativeYUV420sp2RGBA(byte[] yuv, int width, int height, ByteBuffer buffer);

}