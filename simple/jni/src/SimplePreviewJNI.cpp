/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

#include "SimplePreviewJNI.h"

JNIEXPORT void JNICALL Java_com_orangesignal_android_camera_SimplePreview_nativeYUV420sp2ARGB(JNIEnv* env, jclass clazz, jbyteArray yuv420sp, jint width, jint height, jintArray argb) {
	jbyte* _yuv = (jbyte*) env->GetPrimitiveArrayCritical(yuv420sp, 0);
	jint*  _argb = (jint*) env->GetPrimitiveArrayCritical(argb, 0);

	const int frameSize = width * height;

	for (int j = 0, yp = 0; j < height; j++) {
		int uvp = frameSize + (j >> 1) * width;
		int u = 0;
		int v = 0;
		for (int i = 0; i < width; i++, yp++) {
			int y = (0xff & ((int) _yuv[yp])) - 16;
			if (y < 0) {
				y = 0;
			}
			if ((i & 1) == 0) {
				v = (0xff & _yuv[uvp++]) - 128;
				u = (0xff & _yuv[uvp++]) - 128;
			}

			int y1192 = 1192 * y;
			int r = (y1192 + 1634 * v);
			int g = (y1192 - 833 * v - 400 * u);
			int b = (y1192 + 2066 * u);

			if (r < 0) r = 0; else if (r > 262143) r = 262143;
			if (g < 0) g = 0; else if (g > 262143) g = 262143;
			if (b < 0) b = 0; else if (b > 262143) b = 262143;

			_argb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
		}
	}

	env->ReleasePrimitiveArrayCritical(yuv420sp, _yuv, JNI_ABORT);
	env->ReleasePrimitiveArrayCritical(argb, _argb, 0);
}
