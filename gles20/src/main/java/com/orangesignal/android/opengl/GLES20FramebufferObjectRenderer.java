/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;

import com.orangesignal.android.graphics.Fps;

/**
 * OpenGL ES 2.0 のフレームバッファオブジェクトによる
 * オフスクリーンレンダリングをサポートした {@link GLSurfaceView.Renderer} の基底クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public abstract class GLES20FramebufferObjectRenderer implements GLSurfaceView.Renderer {

	/**
	 * オフスクリーン描画用のフレームバッファオブジェクトを保持します。
	 */
	private GLES20FramebufferObject mFramebufferObject;

	/**
	 * オンスクリーン描画用の GLSL シェーダーオブジェクトを保持します。
	 */
	private GLES20Shader mShader;

	private Fps mFps;

	//////////////////////////////////////////////////////////////////////////
	// パブリックメソッド

	public void setFps(final Fps fps) {
		if (mFps != null) {
			mFps.stop();
			mFps = null;
		}
		mFps = fps;
	}

	/**
	 * フレームバッファオブジェクトの描画内容を {@link Bitmap} として返します。
	 * 
	 * @return {@link Bitmap} オブジェクト
	 */
	public Bitmap getBitmap() {
		return mFramebufferObject.getBitmap();
	}

	/**
	 * フレームバッファオブジェクトの描画内容を {@link Bitmap} として返します。
	 * 
	 * @param orientation 傾き
	 * @return {@link Bitmap} オブジェクト
	 */
	public Bitmap getBitmap(final int orientation) {
		return mFramebufferObject.getBitmap(orientation);
	}

	/**
	 * フレームバッファオブジェクトの描画内容を {@link Bitmap} として返します。
	 * 
	 * @param orientation 傾き
	 * @param mirror ミラーモードかどうか
	 * @return {@link Bitmap} オブジェクト
	 */
	public Bitmap getBitmap(final int orientation, final boolean mirror) {
		return mFramebufferObject.getBitmap(orientation, mirror);
	}

	//////////////////////////////////////////////////////////////////////////
	// オーバーライドメソッド

	/**
	 * 実装は、オフスクリーン描画用のフレームバッファオブジェクトとオンスクリーン描画用のシェーダーオブジェクトを初期化した後に、{@link #onSurfaceCreated(EGLConfig)} を呼び出します。
	 */
	@Override
	public final void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
		mFramebufferObject = new GLES20FramebufferObject();
		mShader = new GLES20Shader();
		mShader.setup();
		onSurfaceCreated(config);
		if (mFps != null) {
			mFps.start();
		}
	}

	/**
	 * 実装は、オフスクリーン描画用のフレームバッファオブジェクトを構成または再構成した後に、{@link #onSurfaceChanged(int, int)} を呼び出します。
	 */
	@Override
	public final void onSurfaceChanged(final GL10 gl, final int width, final int height) {
		mFramebufferObject.setup(width, height);
		mShader.setFrameSize(width, height);
		onSurfaceChanged(width, height);
	}

	/**
	 * 実装はオフスクリーン描画用のフレームバッファオブジェクトを有効にした後に、{@link #onDrawFrame(GLES20FramebufferObject)} を呼び出します。
	 * その後、フレームバッファオブジェクトの内容をウィンドウシステムが提供するデフォルトのフレームバッファへ描画します。
	 */
	@Override
	public final void onDrawFrame(final GL10 gl) {

		////////////////////////////////////////////////////////////
		// オフスクリーンレンダリング

		// FBO へ切り替えます。
		mFramebufferObject.enable();
		glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());

		// オフスクリーン描画を行います。
		onDrawFrame(mFramebufferObject);

		////////////////////////////////////////////////////////////
		// オンスクリーンレンダリング

		// ウィンドウシステムが提供するフレームバッファへ切り替えます。
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		mShader.draw(mFramebufferObject.getTexName(), null);

		if (mFps != null) {
			mFps.countup();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (mFps != null) {
				mFps.stop();
				mFps = null;
			}
		} finally {
			super.finalize();
		}
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * Called when the surface is created or recreated.
	 * <p>
	 * Called when the rendering thread
	 * starts and whenever the EGL context is lost. The context will typically
	 * be lost when the Android device awakes after going to sleep.
	 * <p>
	 * Since this method is called at the beginning of rendering, as well as
	 * every time the EGL context is lost, this method is a convenient place to put
	 * code to create resources that need to be created when the rendering
	 * starts, and that need to be recreated when the EGL context is lost.
	 * Textures are an example of a resource that you might want to create
	 * here.
	 * <p>
	 * Note that when the EGL context is lost, all OpenGL resources associated
	 * with that context will be automatically deleted. You do not need to call
	 * the corresponding "glDelete" methods such as glDeleteTextures to
	 * manually delete these lost resources.
	 * <p>
	 * 
	 * @param config the EGLConfig of the created surface. Can be used
	 * to create matching pbuffers.
	 */
	public abstract void onSurfaceCreated(EGLConfig config);

	/**
	 * Called when the surface changed size.
	 * <p>
	 * Called after the surface is created and whenever
	 * the OpenGL ES surface size changes.
	 * <p>
	 * Typically you will set your viewport here. If your camera
	 * is fixed then you could also set your projection matrix here:
	 * <pre class="prettyprint">
	 * void onSurfaceChanged(int width, int height) {
	 *     // for a fixed camera, set the projection too
	 *     float ratio = (float) width / height;
	 *     Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	 * }
	 * </pre>
	 * @param width
	 * @param height
	 */
	public abstract void onSurfaceChanged(int width, int height);

	/**
	 * Called to draw the current frame.<p>
	 * This method is responsible for drawing the current frame.<p>
	 * The implementation of this method typically looks like this:
	 * <pre>
	 * void onDrawFrame(GLES20FramebufferObject fbo) {
	 *     GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	 *     //... other gl calls to render the scene ...
	 * }
	 * </pre>
	 * @param fbo オフスクリーン描画用のフレームバッファオブジェクト
	 */
	public abstract void onDrawFrame(GLES20FramebufferObject fbo);

}