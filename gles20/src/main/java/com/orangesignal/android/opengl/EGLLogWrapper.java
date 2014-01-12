/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static javax.microedition.khronos.egl.EGL10.EGL_BAD_ACCESS;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_ALLOC;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_ATTRIBUTE;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_CONFIG;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_CONTEXT;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_CURRENT_SURFACE;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_DISPLAY;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_MATCH;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_NATIVE_PIXMAP;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_NATIVE_WINDOW;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_PARAMETER;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_SURFACE;
import static javax.microedition.khronos.egl.EGL10.EGL_NOT_INITIALIZED;
import static javax.microedition.khronos.egl.EGL10.EGL_SUCCESS;
import static javax.microedition.khronos.egl.EGL11.EGL_CONTEXT_LOST;

final class EGLLogWrapper {

	public static String getErrorString(final int error) {
		switch (error) {
			case EGL_SUCCESS:
				return "EGL_SUCCESS";
			case EGL_NOT_INITIALIZED:
				return "EGL_NOT_INITIALIZED";
			case EGL_BAD_ACCESS:
				return "EGL_BAD_ACCESS";
			case EGL_BAD_ALLOC:
				return "EGL_BAD_ALLOC";
			case EGL_BAD_ATTRIBUTE:
				return "EGL_BAD_ATTRIBUTE";
			case EGL_BAD_CONFIG:
				return "EGL_BAD_CONFIG";
			case EGL_BAD_CONTEXT:
				return "EGL_BAD_CONTEXT";
			case EGL_BAD_CURRENT_SURFACE:
				return "EGL_BAD_CURRENT_SURFACE";
			case EGL_BAD_DISPLAY:
				return "EGL_BAD_DISPLAY";
			case EGL_BAD_MATCH:
				return "EGL_BAD_MATCH";
			case EGL_BAD_NATIVE_PIXMAP:
				return "EGL_BAD_NATIVE_PIXMAP";
			case EGL_BAD_NATIVE_WINDOW:
				return "EGL_BAD_NATIVE_WINDOW";
			case EGL_BAD_PARAMETER:
				return "EGL_BAD_PARAMETER";
			case EGL_BAD_SURFACE:
				return "EGL_BAD_SURFACE";
			case EGL_CONTEXT_LOST:
				return "EGL_CONTEXT_LOST";
			default:
				return getHex(error);
		}
	}

	private static String getHex(final int value) {
		return "0x" + Integer.toHexString(value);
	}

}
