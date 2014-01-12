/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import static com.orangesignal.android.opengl.GLES20Compat.GL_TEXTURE_EXTERNAL_OES;

import java.io.IOException;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.os.Build;

/**
 * Android 3.0 以降向けの {@link PreviewTexture} の実装を提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
final class GLES20SurfaceTexture implements PreviewTexture, SurfaceTexture.OnFrameAvailableListener {

	private SurfaceTexture mSurfaceTexture;
	private OnFrameAvailableListener mOnFrameAvailableListener;

	/**
	 * コンストラクタです。
	 * 
	 * @param texName テクスチャ識別子
	 */
	public GLES20SurfaceTexture(final int texName) {
		mSurfaceTexture = new SurfaceTexture(texName);
		mSurfaceTexture.setOnFrameAvailableListener(this);
	}

	@Override
	public void setOnFrameAvailableListener(final OnFrameAvailableListener l) {
		mOnFrameAvailableListener = l;
	}

	@Override
	public int getTextureTarget() {
		return GL_TEXTURE_EXTERNAL_OES;
	}

	@Override
	public void setup(final CameraHelper camera) throws IOException {
		camera.setPreviewTexture(mSurfaceTexture);
	}

	@Override
	public void updateTexImage() {
		mSurfaceTexture.updateTexImage();
	}

	@Override
	public void getTransformMatrix(final float[] mtx) {
		mSurfaceTexture.getTransformMatrix(mtx);
	}

	@Override
	public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
		if (mOnFrameAvailableListener != null) {
			mOnFrameAvailableListener.onFrameAvailable(this);
		}
	}

}