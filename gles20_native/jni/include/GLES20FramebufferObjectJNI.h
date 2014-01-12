/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20FRAMEBUFFEROBJECTJNI_H_
#define ORANGESIGNAL_GLES20FRAMEBUFFEROBJECTJNI_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeClassInit(JNIEnv* env, jclass clazz);

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeInit(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeFinalize(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeSetup(JNIEnv* env, jobject thiz, jint width, jint height);
JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeRelease(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeEnable(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeGetWidth(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeGetHeight(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeGetTexName(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeOnGetBitmap(JNIEnv* env, jobject thiz, jintArray pixels, jint width, jint height);

#ifdef __cplusplus
}
#endif
#endif // ORANGESIGNAL_GLES20FRAMEBUFFEROBJECTJNI_H_
