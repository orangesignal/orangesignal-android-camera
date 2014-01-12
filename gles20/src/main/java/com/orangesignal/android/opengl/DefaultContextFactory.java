/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

/**
 * {@link EGLContext} の生成と破棄を行う {@link GLSurfaceView.EGLContextFactory} の既定の実装を提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class DefaultContextFactory implements GLSurfaceView.EGLContextFactory {

	/**
	 * ログ出力用のタグです。
	 */
	private static final String TAG = "DefaultContextFactory";

	private int mEGLContextClientVersion;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public DefaultContextFactory() {}

	/**
	 * コンストラクタです。
	 * 
	 * @param version OpenGL ES バージョン
	 */
	public DefaultContextFactory(final int version) {
		mEGLContextClientVersion = version;
	}

	//////////////////////////////////////////////////////////////////////////

	private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

	@Override
	public EGLContext createContext(final EGL10 egl, final EGLDisplay display, final EGLConfig config) {
		final int[] attrib_list;
		if (mEGLContextClientVersion != 0) {
			attrib_list = new int[]{ EGL_CONTEXT_CLIENT_VERSION, mEGLContextClientVersion, EGL_NONE };
		} else {
			attrib_list = null;
		}
		return egl.eglCreateContext(display, config, EGL_NO_CONTEXT, attrib_list);
	}

	@Override
	public void destroyContext(final EGL10 egl, final EGLDisplay display, final EGLContext context) {
		if (!egl.eglDestroyContext(display, context)) {
			Log.e(TAG, "display:" + display + " context: " + context);
			throw new RuntimeException("eglDestroyContex" + egl.eglGetError());
		}
	}

}
