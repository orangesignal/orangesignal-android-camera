/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "GLES20PreviewTextureJNI.h"

//#include "GLES20PreviewTexture.h"
#include "YuvDataUtils.h"

using namespace orangesignal;

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeYUV420sp2RGB(JNIEnv* env, jobject thiz, jbyteArray data, jint width, jint height, jobject pixels) {
	unsigned char* _pixels = (unsigned char *) env->GetDirectBufferAddress(pixels);
	unsigned char* _data = (unsigned char *) env->GetPrimitiveArrayCritical(data, 0);

	YuvDataUtils::toRGB(_data, _data + width * height, width, height, _pixels);

	env->ReleasePrimitiveArrayCritical(data, _data, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeYUV420sp2RGBA(JNIEnv* env, jobject thiz, jbyteArray data, jint width, jint height, jobject pixels) {
	unsigned char* _pixels = (unsigned char *) env->GetDirectBufferAddress(pixels);
	unsigned char* _data = (unsigned char *) env->GetPrimitiveArrayCritical(data, 0);

	YuvDataUtils::toRGBA(_data, width, height, _pixels);

	env->ReleasePrimitiveArrayCritical(data, _data, JNI_ABORT);
}
/*
static jfieldID gGLES20PreviewTextureNativeObjectFieldID;

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeClassInit(JNIEnv* env, jclass clazz) {
	gGLES20PreviewTextureNativeObjectFieldID = env->GetFieldID(clazz, "mNativeObject", "I");
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeInit(JNIEnv* env, jobject thiz, jint texName) {
	GLES20PreviewTexture* obj = new GLES20PreviewTexture(texName);
	env->SetIntField(thiz, gGLES20PreviewTextureNativeObjectFieldID, (jint) obj);
}

static inline GLES20PreviewTexture* getNativeObject(JNIEnv* env, jobject thiz) {
	return reinterpret_cast<GLES20PreviewTexture *> (env->GetIntField(thiz, gGLES20PreviewTextureNativeObjectFieldID));
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeFinalize(JNIEnv* env, jobject thiz) {
	delete getNativeObject(env, thiz);
}

JNIEXPORT jint JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_getTextureTarget(JNIEnv* env, jobject thiz) {
	return getNativeObject(env, thiz)->getTextureTarget();
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeSetup(JNIEnv* env, jobject thiz, jint width, jint height, jboolean flipH) {
	if (!getNativeObject(env, thiz)->setup(width, height, flipH == JNI_TRUE)) {
		jclass clazz = env->FindClass("java/lang/RuntimeException");
		env->ThrowNew(clazz, "Failed to initialize PreviewTexture.");
	}
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_updateTexImage(JNIEnv* env, jobject thiz) {
	getNativeObject(env, thiz)->updateTexImage();
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeGetTransformMatrix(JNIEnv* env, jobject thiz, jfloatArray mtx) {
	jfloat* _mtx = (jfloat*) env->GetPrimitiveArrayCritical(mtx, 0);
	getNativeObject(env, thiz)->getTransformMatrix(_mtx);
	env->ReleasePrimitiveArrayCritical(mtx, _mtx, 0);
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_nativeOnPreviewFrame(JNIEnv* env, jobject thiz, jbyteArray data) {
	jbyte* _data = (jbyte*) env->GetPrimitiveArrayCritical(data, 0);
	getNativeObject(env, thiz)->onPreviewFrame((unsigned char *) _data);
	env->ReleasePrimitiveArrayCritical(data, _data, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_GLES20PreviewTexture_release(JNIEnv* env, jobject thiz) {
	getNativeObject(env, thiz)->release();
}
*/
