/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_SIMPLEPREVIEWJNI_H_
#define ORANGESIGNAL_SIMPLEPREVIEWJNI_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_SimplePreview_nativeYUV420sp2ARGB(JNIEnv* env, jclass clazz, jbyteArray yuv420sp, jint width, jint height, jintArray argb);

#ifdef __cplusplus
}
#endif
#endif // ORANGESIGNAL_SIMPLEPREVIEWJNI_H_
