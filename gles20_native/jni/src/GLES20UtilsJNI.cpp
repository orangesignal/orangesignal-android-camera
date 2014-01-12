/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "GLES20UtilsJNI.h"

#include <GLES2/gl2.h>

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20Utils_texImage2D(JNIEnv* env, jclass clazz, jint target, jint level, jint width, jint height, jint border, jintArray pixels) {
	jint* _pixels = env->GetIntArrayElements(pixels, 0);

	const int size = width * height;
	for (int i = 0; i < size; i++) {
		unsigned int p = _pixels[i];
		// ABGR -> ARGB なんでABGRになってるのかはよくわからん･･･
		_pixels[i] =
				(((p      ) & 0xFF000000) | // A
				((p << 16) & 0x00FF0000) | // R
				((p      ) & 0x0000FF00) | // G
				((p >> 16) & 0x000000FF)); // B
	}

	glTexImage2D((GLenum) target, (GLint) level, GL_RGBA, (GLsizei) width, (GLsizei) height, (GLint) border, GL_RGBA, GL_UNSIGNED_BYTE, (GLvoid*) _pixels);

	env->ReleaseIntArrayElements(pixels, _pixels, JNI_ABORT);
}
