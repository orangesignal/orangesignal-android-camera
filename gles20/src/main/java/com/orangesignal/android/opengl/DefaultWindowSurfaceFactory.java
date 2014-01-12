/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

/**
 * {@link GLSurfaceView.EGLWindowSurfaceFactory} の既定の実装を提供します。<p>
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class DefaultWindowSurfaceFactory implements GLSurfaceView.EGLWindowSurfaceFactory {

	/**
	 * ログ出力用のタグです。
	 */
	private static final String TAG = "DefaultWindowSurfaceFactory";

	@Override
	public EGLSurface createWindowSurface(final EGL10 egl, final EGLDisplay display, final EGLConfig config, final Object nativeWindow) {
		try {
			return egl.eglCreateWindowSurface(display, config, nativeWindow, null);
		} catch (final IllegalArgumentException e) {
			// This exception indicates that the surface flinger surface
			// is not valid. This can happen if the surface flinger surface has
			// been torn down, but the application has not yet been
			// notified via SurfaceHolder.Callback.surfaceDestroyed.
			// In theory the application should be notified first,
			// but in practice sometimes it is not. See b/4588890
			Log.e(TAG, "eglCreateWindowSurface", e);
			return null;
		}
	}

	@Override
	public void destroySurface(final EGL10 egl, final EGLDisplay display, final EGLSurface surface) {
		egl.eglDestroySurface(display, surface);
	}

}