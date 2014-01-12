/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import android.annotation.TargetApi;
import android.opengl.GLSurfaceView;
import android.os.Build;

/**
 * OpenGL ES 2.0 向けの {@link GLSurfaceView.EGLConfigChooser} を提供します。<p>
 * Android 2.0 以降で OpenGL ES 2.0 を利用するために、{@link GLSurfaceView} で {@link GLES20ContextFactory} と共に使用して下さい。
 * Android 2.2 以降では、{@code GLSurfaceView.setEGLContextClientVersion(2)} を設定することで同じ設定をすることが可能です。
 * 
 * @author 杉澤 浩二
 * @see {@link GLSurfaceView#setEGLConfigChooser(android.opengl.GLSurfaceView.EGLConfigChooser)}
 * @see {@link GLES20ContextFactory}
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class GLES20ConfigChooser extends DefaultConfigChooser {

	private static final int EGL_CONTEXT_CLIENT_VERSION = 2;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 * 実装はデプスバッファを指定して単に {@link #GLES20ConfigChooser(boolean)} を呼び出すだけです。
	 */
	public GLES20ConfigChooser() {
		super(EGL_CONTEXT_CLIENT_VERSION);
	}

	/**
	 * デプスバッファの有無に関わらず RGB_565 (API レベル 17 以上の場合は RGB_888) サーフェス選択するコンストラクタです。
	 * 
	 * @param withDepthBuffer デプスバッファの有無
	 */
	public GLES20ConfigChooser(final boolean withDepthBuffer) {
		super(withDepthBuffer, EGL_CONTEXT_CLIENT_VERSION);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param redSize
	 * @param greenSize
	 * @param blueSize
	 * @param alphaSize
	 * @param depthSize
	 * @param stencilSize
	 */
	public GLES20ConfigChooser(final int redSize, final int greenSize, final int blueSize, final int alphaSize, final int depthSize, final int stencilSize) {
		super(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize, EGL_CONTEXT_CLIENT_VERSION);
	}

}