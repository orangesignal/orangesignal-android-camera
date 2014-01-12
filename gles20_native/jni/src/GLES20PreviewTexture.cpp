/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

#include "GLES20PreviewTexture.h"

#include <stdlib.h>

#include <android/log.h>

#include "Matrix.h"
#include "YuvDataUtils.h"

namespace orangesignal {

#define LOG_TAG    "GLES20PreviewTexture"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

//////////////////////////////////////////////////////////////////////////////
// Transform matrices

static const float mtxIdentity[16] = {
	 1,  0,  0,  0,
	 0,  1,  0,  0,
	 0,  0,  1,  0,
	 0,  0,  0,  1,
};

static const float mtxFlipH[16] = {
	-1,  0,  0,  0,
	 0,  1,  0,  0,
	 0,  0,  1,  0,
	 1,  0,  0,  1,
};

static const float mtxFlipV[16] = {
	 1,  0,  0,  0,
	 0, -1,  0,  0,
	 0,  0,  1,  0,
	 0,  1,  0,  1,
};
/*
static const float mtxRot90[16] = {
	 0,  1,  0,  0,
	-1,  0,  0,  0,
	 0,  0,  1,  0,
	 1,  0,  0,  1,
};

static const float mtxRot180[16] = {
	-1,  0,  0,  0,
	 0, -1,  0,  0,
	 0,  0,  1,  0,
	 1,  1,  0,  1,
};

static const float mtxRot270[16] = {
	 0, -1,  0,  0,
	 1,  0,  0,  0,
	 0,  0,  1,  0,
	 0,  1,  0,  1,
};
*/

//////////////////////////////////////////////////////////////////////////////
// 生成と破棄

GLES20PreviewTexture::GLES20PreviewTexture(const GLuint texName) {
	mWidth = 0;
	mHeight = 0;
	mTexName = texName;
	mTexTarget = GL_TEXTURE_2D;
	mPreviewData = NULL;
	mFormat = GL_RGBA;
	memcpy(mTransformMatrix, mtxIdentity, sizeof(mTransformMatrix));
	mTimestamp = 0;
//	mMutex = new Mutex();
}

GLES20PreviewTexture::~GLES20PreviewTexture() {
	free(mPreviewData);
//	delete mMutex;
}

//////////////////////////////////////////////////////////////////////////////

GLenum GLES20PreviewTexture::getTextureTarget() {
	return mTexTarget;
}

bool GLES20PreviewTexture::setup(const GLsizei width, const GLsizei height, const bool flipH) {
	mWidth = width;
	mHeight = height;

	free(mPreviewData);
	int data_size = mWidth * mHeight * 4;
	mPreviewData = (GLubyte *) calloc(sizeof(GLubyte), data_size);
	if (mPreviewData != NULL) {
		mFormat = GL_RGBA;
	} else {
		data_size = mWidth * mHeight * 3;
		mPreviewData = (GLubyte *) calloc(sizeof(GLubyte), data_size);
		if (mPreviewData == NULL) {
			return false;
		}
		mFormat = GL_RGB;
	}
	memset(mPreviewData, 0, data_size);

	// プレビュー用のテクスチャイメージを初期化します。
	glBindTexture(mTexTarget, mTexName);
	glTexImage2D(mTexTarget, 0, mFormat, mWidth, mHeight, 0, mFormat, GL_UNSIGNED_BYTE, 0);

	// TransformMatrix を計算します。
	float mtx[16];
	if (flipH) {
		Matrix::multiplyMM(mtx, mtxIdentity, mtxFlipH);
	} else {
		memcpy(mtx, mtxIdentity, sizeof(mtx));
	}
	Matrix::multiplyMM(mTransformMatrix, mtx, mtxFlipV);

	return true;
}

void GLES20PreviewTexture::updateTexImage() {
	mMutex.lock();
//	try {
		glBindTexture(mTexTarget, mTexName);
		glTexSubImage2D(mTexTarget, 0, 0, 0, mWidth, mHeight, mFormat, GL_UNSIGNED_BYTE, (GLvoid*) mPreviewData);
//	} catch (...) {
//	}
	mMutex.unlock();
}

void GLES20PreviewTexture::getTransformMatrix(float mtx[16]) {
	memcpy(mtx, mTransformMatrix, sizeof(mTransformMatrix));
}

void GLES20PreviewTexture::onPreviewFrame(const unsigned char* data) {
	mMutex.lock();
//	try {
		if (mFormat == GL_RGBA) {
			YuvDataUtils::toRGBA(data, (int) mWidth, (int) mHeight, (unsigned char *) mPreviewData);
		} else {
			YuvDataUtils::toRGB(data, (int) mWidth, (int) mHeight, (unsigned char *) mPreviewData);
		}
//	} catch (...) {
//	}
	mMutex.unlock();
}

void GLES20PreviewTexture::release() {
	mWidth = 0;
	mHeight = 0;
	free(mPreviewData);
}

//////////////////////////////////////////////////////////////////////////////

} // namespace orangesignal
