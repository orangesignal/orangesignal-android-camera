/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import javax.microedition.khronos.egl.EGLContext;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * OpenGL ES 2.0 向け {@link EGLContext} の生成と破棄を行う {@link GLSurfaceView.EGLContextFactory} を提供します。<p>
 * Android 2.0 以降で OpenGL ES 2.0 を利用するために、{@link GLSurfaceView} で {@link GLES20ConfigChooser} と共に使用して下さい。
 * Android 2.2 以降では、{@code GLSurfaceView.setEGLContextClientVersion(2)} を設定することで同じ設定をすることが可能です。
 * Android 2.0 及び 2.1 では OpenGL ES 2.0 API は NDK のみで提供されており EGL 以外の API の使用は NDK を使用して JNI として実装する必要があります。
 * 
 * @author 杉澤 浩二
 * @see {@link GLSurfaceView#setEGLContextFactory(android.opengl.GLSurfaceView.EGLContextFactory)}
 * @see {@link GLES20ConfigChooser}
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class GLES20ContextFactory extends DefaultContextFactory {

	private static final int EGL_CONTEXT_CLIENT_VERSION = 2;

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20ContextFactory() {
		super(EGL_CONTEXT_CLIENT_VERSION);
	}

}