/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20COMPATJNI_H_
#define ORANGESIGNAL_GLES20COMPATJNI_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20Compat_glVertexAttribPointer(JNIEnv* env, jclass clazz, jint indx, jint size, jint type, jboolean normalized, jint stride, jint offset);

#ifdef __cplusplus
}
#endif
#endif // ORANGESIGNAL_GLES20COMPATJNI_H_
