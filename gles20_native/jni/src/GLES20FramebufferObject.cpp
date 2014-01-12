/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "GLES20FramebufferObject.h"

#include <stdexcept>

#include "GLES20Utils.h"

namespace orangesignal {

GLES20FramebufferObject::GLES20FramebufferObject() {
	mWidth = 0;
	mHeight = 0;
	mFramebufferName = 0;
	mRenderbufferName = 0;
	mTexName = 0;
}

GLES20FramebufferObject::~GLES20FramebufferObject() {
}

GLsizei GLES20FramebufferObject::getWidth() {
	return mWidth;
}

GLsizei GLES20FramebufferObject::getHeight() {
	return mHeight;
}

GLuint GLES20FramebufferObject::getTexName() {
	return mTexName;
}

bool GLES20FramebufferObject::setup(const GLsizei width, const GLsizei height) {
	// パラメータを検証します。

	GLint maxTextureSize;
	glGetIntegerv(GL_MAX_TEXTURE_SIZE, &maxTextureSize);
	if (width > maxTextureSize || height > maxTextureSize) {
		return false;
	}

	GLint maxRenderbufferSize;
	glGetIntegerv(GL_MAX_RENDERBUFFER_SIZE, &maxRenderbufferSize);
	if (width > maxRenderbufferSize || height > maxRenderbufferSize) {
		return false;
	}

	// 現在の構成を保存します。
	GLint saveFramebuffer;
	glGetIntegerv(GL_FRAMEBUFFER_BINDING, &saveFramebuffer);
	GLint saveRenderbuffer;
	glGetIntegerv(GL_RENDERBUFFER_BINDING, &saveRenderbuffer);
	GLint saveTexName;
	glGetIntegerv(GL_TEXTURE_BINDING_2D, &saveTexName);

	// 現在のフレームバッファオブジェクトを削除します。
	release();

	try {
		mWidth = width;
		mHeight = height;

		// フレームバッファ
		glGenFramebuffers(1, &mFramebufferName);
		glBindFramebuffer(GL_FRAMEBUFFER, mFramebufferName);

		// レンダーバッファ
		glGenRenderbuffers(1, &mRenderbufferName);
		glBindRenderbuffer(GL_RENDERBUFFER, mRenderbufferName);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, mRenderbufferName);

		// テクスチャバッファ
		glGenTextures(1, &mTexName);
		glBindTexture(GL_TEXTURE_2D, mTexName);
		GLES20Utils::setupSampler(GL_TEXTURE_2D, GL_LINEAR, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mTexName, 0);
	} catch (...) {
		release();
		return false;
	}

	// フレームバッファが完全かどうかチェックします。
	const GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
	if (status != GL_FRAMEBUFFER_COMPLETE) {
		release();
		return false;
	}

	// 保存した構成を復元します。
	glBindFramebuffer(GL_FRAMEBUFFER, saveFramebuffer);
	glBindRenderbuffer(GL_RENDERBUFFER, saveRenderbuffer);
	glBindTexture(GL_TEXTURE_2D, saveTexName);

	return true;
}

void GLES20FramebufferObject::release() {
	glDeleteTextures(1, &mTexName);
	mTexName = 0;
	glDeleteRenderbuffers(1, &mRenderbufferName);
	mRenderbufferName = 0;
	glDeleteFramebuffers(1, &mFramebufferName);
	mFramebufferName = 0;
}

void GLES20FramebufferObject::enable() {
	glBindFramebuffer(GL_FRAMEBUFFER, mFramebufferName);
}

void GLES20FramebufferObject::pixels(GLvoid* pixels) {
	GLint saveFramebuffer;
	glGetIntegerv(GL_FRAMEBUFFER_BINDING, &saveFramebuffer);
	enable();
	glReadPixels(0, 0, mWidth, mHeight, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
	glBindFramebuffer(GL_FRAMEBUFFER, saveFramebuffer);
}

} // namespace orangesignal
