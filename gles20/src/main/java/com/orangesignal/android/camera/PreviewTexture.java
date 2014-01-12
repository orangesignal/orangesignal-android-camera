/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;

/**
 * OpenGL ES テクスチャとしてイメージストリームからフレームをキャプチャするための共通インタフェースを提供します。
 * 
 * @author 杉澤 浩二
 * @see {@link android.graphics.SurfaceTexture}
 */
public interface PreviewTexture {

	/**
	 * 新しいストリームフレームが利用可能な場合に、通知するためのコールバックインターフェースを提供します。
	 */
	public interface OnFrameAvailableListener {
		void onFrameAvailable(PreviewTexture previewTexture);
	}

	/**
	 * Register a callback to be invoked when a new image frame becomes available to the
	 * PreviewTexture. Note that this callback may be called on an arbitrary thread, so it is not
	 * safe to call {@link #updateTexImage} without first binding the OpenGL ES context to the
	 * thread invoking the callback.
	 */
	void setOnFrameAvailableListener(final OnFrameAvailableListener l);

	/**
	 * getTextureTarget returns the texture target of the current
	 * texture as returned by updateTexImage().
	 * 
	 * @return the OpenGL texture target
	 */
	int getTextureTarget();

	/**
	 * Sets the surface to be used for live preview.
	 * Either a surface or surface texture is necessary for preview, and preview is necessary to take pictures.
	 * The same surface texture can be re-set without harm.
	 * 
	 * @param camera {@link CameraHelper}
	 */
	void setup(CameraHelper camera) throws IOException;

	/**
	 * Update the texture image to the most recent frame from the image stream.  This may only be
	 * called while the OpenGL ES context that owns the texture is bound to the thread.  It will
	 * implicitly bind its texture to the GL_TEXTURE_2D texture target.
	 */
	void updateTexImage();

	/**
	 * Retrieve the 4x4 texture coordinate transform matrix associated with the texture image set by
	 * the most recent call to updateTexImage.
	 *
	 * This transform matrix maps 2D homogeneous texture coordinates of the form (s, t, 0, 1) with s
	 * and t in the inclusive range [0, 1] to the texture coordinate that should be used to sample
	 * that location from the texture.  Sampling the texture outside of the range of this transform
	 * is undefined.
	 *
	 * The matrix is stored in column-major order so that it may be passed directly to OpenGL ES via
	 * the glLoadMatrixf or glUniformMatrix4fv functions.
	 *
	 * @param mtx the array into which the 4x4 matrix will be stored.  The array must have exactly 16 elements.
	 */
	void getTransformMatrix(float[] mtx);

}