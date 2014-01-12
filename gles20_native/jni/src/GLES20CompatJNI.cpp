/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "GLES20CompatJNI.h"

#include <GLES2/gl2.h>

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20Compat_glVertexAttribPointer(JNIEnv* env, jclass clazz, jint index, jint size, jint type, jboolean normalized, jint stride, jint offset) {
	glVertexAttribPointer((GLuint) index, (GLint) size, (GLenum) type, (GLboolean) (normalized == JNI_TRUE), (GLsizei) stride, (const GLvoid *) offset);
}
