/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "GLES20FramebufferObjectJNI.h"

#include <stdlib.h>

#include "GLES20FramebufferObject.h"

using namespace orangesignal;

/*
static JNINativeMethod gMethods[] = {
		{"nativeClassInit", "()V", (void*)nativeClassInit },
};
*/

// http://d.hatena.ne.jp/tueda_wolf/20100913/p4
// http://www.netmite.com/android/mydroid/frameworks/base/core/jni/android_os_FileUtils.cpp
static jfieldID gGLES20FramebufferObjectNativeObjectFieldID;

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeClassInit(JNIEnv* env, jclass clazz) {
	gGLES20FramebufferObjectNativeObjectFieldID = env->GetFieldID(clazz, "mNativeObject", "I");
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeInit(JNIEnv* env, jobject thiz) {
	GLES20FramebufferObject* fbo = new GLES20FramebufferObject();
	env->SetIntField(thiz, gGLES20FramebufferObjectNativeObjectFieldID, (jint) fbo);
/*
	jlong entity = (jlong) new orangesignal::android::GLES20FramebufferObject();
	jclass clazz = env->GetObjectClass(thiz);
	jfieldID fid = env->GetFieldID(clazz, "entity", "J");
	env->SetLongField(thiz, fid, entity);

	//entity = env->NewGlobalRef (obj);
	//env->DeleteGlobalRef (entity);

	// 例外スローする場合
	jclass clazz = env->FindClass("java/lang/IllegalArgumentException");
	env->ThrowNew(clazz, "Exception: Hello world.");
*/
}

static inline GLES20FramebufferObject* getNativeObject(JNIEnv* env, jobject thiz) {
	return reinterpret_cast<GLES20FramebufferObject *> (env->GetIntField(thiz, gGLES20FramebufferObjectNativeObjectFieldID));
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeFinalize(JNIEnv* env, jobject thiz) {
	delete getNativeObject(env, thiz);
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeSetup(JNIEnv* env, jobject thiz, jint width, jint height) {
	const bool result = getNativeObject(env, thiz)->setup(width, height);
	if (!result) {
		jclass clazz = env->FindClass("java/lang/RuntimeException");
		env->ThrowNew(clazz, "Failed to initialize framebuffer object.");
	}
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeRelease(JNIEnv* env, jobject thiz) {
	getNativeObject(env, thiz)->release();
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeEnable(JNIEnv* env, jobject thiz) {
	getNativeObject(env, thiz)->enable();
}

JNIEXPORT jint JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeGetWidth(JNIEnv* env, jobject thiz) {
	return getNativeObject(env, thiz)->getWidth();
}

JNIEXPORT jint JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeGetHeight(JNIEnv* env, jobject thiz) {
	return getNativeObject(env, thiz)->getHeight();
}

JNIEXPORT jint JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeGetTexName(JNIEnv* env, jobject thiz) {
	return getNativeObject(env, thiz)->getTexName();
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_opengl_GLES20FramebufferObject_nativeOnGetBitmap(JNIEnv* env, jobject thiz, jintArray pixels, jint width, jint height) {
	jint* _pixels = env->GetIntArrayElements(pixels, 0);
	getNativeObject(env, thiz)->pixels((GLvoid*) _pixels);
	env->ReleaseIntArrayElements(pixels, _pixels, 0);
}
