/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20UTILSJNI_H_
#define ORANGESIGNAL_GLES20UTILSJNI_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20Utils_texImage2D(JNIEnv* env, jclass clazz, jint target, jint level, jint width, jint height, jint border, jintArray pixels);

#ifdef __cplusplus
}
#endif
#endif // ORANGESIGNAL_GLES20UTILSJNI_H_
