/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20PREVIEWTEXTUREJNI_H_
#define ORANGESIGNAL_GLES20PREVIEWTEXTUREJNI_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeYUV420sp2RGB(JNIEnv* env, jobject thiz, jbyteArray data, jint width, jint height, jobject pixels);
JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeYUV420sp2RGBA(JNIEnv* env, jobject thiz, jbyteArray data, jint width, jint height, jobject pixels);
/*
JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeClassInit(JNIEnv* env, jclass clazz);

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeInit(JNIEnv* env, jobject thiz, jint texName);
JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeFinalize(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_getTextureTarget(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeSetup(JNIEnv* env, jobject thiz, jint width, jint height, jboolean flipH);
JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_updateTexImage(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeGetTransformMatrix(JNIEnv* env, jobject thiz, jfloatArray mtx);
JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeOnPreviewFrame(JNIEnv* env, jobject thiz, jbyteArray data);
JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_release(JNIEnv* env, jobject thiz);
*/
#ifdef __cplusplus
}
#endif

#endif // ORANGESIGNAL_GLES20PREVIEWTEXTUREJNI_H_
