/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * OpenGL ES 2.0 の後方互換性のためのクラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class GLES20Compat {

	static {
		System.loadLibrary("orangesignal-gles20");
	}

	/**
	 * GL_OES_texture_half_float
	 */
	public static final int GL_HALF_FLOAT_OES = 0x8D61;

	/**
	 * GL_OES_EGL_image_external
	 */
	public static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

	/**
	 * @see http://www.khronos.org/opengles/sdk/docs/man/xhtml/glVertexAttribPointer.xml
	 */
	public static native void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset);

}