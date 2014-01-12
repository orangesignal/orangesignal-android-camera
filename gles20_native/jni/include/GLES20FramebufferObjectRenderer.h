/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20FRAMEBUFFEROBJECTRENDERER_H_
#define ORANGESIGNAL_GLES20FRAMEBUFFEROBJECTRENDERER_H_

#include <GLES2/gl2.h>

#include "GLES20FramebufferObject.h"
#include "GLES20Shader.h"

namespace orangesignal {

class GLES20FramebufferObjectRenderer {
private:

	/**
	 * オフスクリーン描画用のフレームバッファオブジェクトを保持します。
	 */
	GLES20FramebufferObject mFramebufferObject;

	/**
	 * オンスクリーン描画用の GLSL シェーダーオブジェクトを保持します。
	 */
	GLES20Shader mShader;

public:

	GLES20FramebufferObjectRenderer();
	virtual ~GLES20FramebufferObjectRenderer();

	virtual void onSurfaceCreated();
	virtual void onSurfaceChanged(const GLsizei width, const GLsizei height);
	virtual void onDrawFrame();

};

} // namespace orangesignal
#endif // ORANGESIGNAL_GLES20FRAMEBUFFEROBJECTRENDERER_H_
