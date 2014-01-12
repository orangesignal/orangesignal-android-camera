/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "GLES20FramebufferObjectRenderer.h"

#include <stdlib.h>

namespace orangesignal {

GLES20FramebufferObjectRenderer::GLES20FramebufferObjectRenderer() {
	mFramebufferObject = new GLES20FramebufferObject();
	mShader = new GLES20Shader();
}

GLES20FramebufferObjectRenderer::~GLES20FramebufferObjectRenderer() {
	delete mFramebufferObject;
	delete mShader;
}

void GLES20FramebufferObjectRenderer::onSurfaceCreated() {
	mShader.setup();
}

void GLES20FramebufferObjectRenderer::onSurfaceChanged(const GLsizei width, const GLsizei height) {
	mFramebufferObject.setup(width, height);
	mShader.setFrameSize(width, height);
}

void GLES20FramebufferObjectRenderer::onDrawFrame() {

	////////////////////////////////////////////////////////////
	// オフスクリーンレンダリング

	// FBO へ切り替えます。
	mFramebufferObject.enable();
	glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());

	// オフスクリーン描画を行います。
//	onDrawFrame(mFramebufferObject);

	////////////////////////////////////////////////////////////
	// オンスクリーンレンダリング

	// ウィンドウシステムが提供するフレームバッファへ切り替えます。
	glBindFramebuffer(GL_FRAMEBUFFER, 0);
	glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());

	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//	mShader.draw(mFramebufferObject.getTexName(), NULL);
}

} /* namespace orangesignal */
