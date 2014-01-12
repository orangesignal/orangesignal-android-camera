/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20FRAMEBUFFEROBJECT_H_
#define ORANGESIGNAL_GLES20FRAMEBUFFEROBJECT_H_

#include <GLES2/gl2.h>

namespace orangesignal {

/**
 * オフスクリーン描画用の OpenGL ES 2.0 のフレームバッファオブジェクト管理クラスを提供します。
 *
 * @author 杉澤 浩二
 */
class GLES20FramebufferObject {
private:

	/**
	 * 幅を保持します。
	 */
	GLsizei mWidth;

	/**
	 * 高さを保持します。
	 */
	GLsizei mHeight;

	/**
	 * フレームバッファ識別子を保持します。
	 */
	GLuint mFramebufferName;

	/**
	 * レンダーバッファ識別子を保持します。
	 */
	GLuint mRenderbufferName;

	/**
	 * テクスチャ識別子を保持します。
	 */
	GLuint mTexName;

public:

	/**
	 * コンストラクタです。
	 */
	GLES20FramebufferObject();

	/**
	 * デストラクタです。
	 */
	virtual ~GLES20FramebufferObject();

	/**
	 * 幅を返します。
	 *
	 * @return 幅
	 */
	GLsizei getWidth();

	/**
	 * 高さを返します。
	 *
	 * @return 高さ
	 */
	GLsizei getHeight();

	/**
	 * テクスチャ識別子を返します。
	 *
	 * @return テクスチャ識別子
	 */
	GLuint getTexName();

	/**
	 * 指定された幅と高さでフレームバッファオブジェクト (FBO) を構成します。<p>
	 * 既にフレームバッファオブジェクト (FBO) が構成されている場合は、
	 * 現在のフレームバッファオブジェクト (FBO) を削除して新しいフレームバッファオブジェクト (FBO) を構成します。
	 *
	 * @param width 幅
	 * @param height 高さ
	 * @return フレームバッファオブジェクト (FBO) を構成できた場合は {@code true} を、それ以外の場合は {@code false} を返します。
	 */
	virtual bool setup(const GLsizei width, const GLsizei height);

	/**
	 * クリーンアップを行います。
	 */
	virtual void release();

	/**
	 * このフレームバッファオブジェクトをバインドして有効にします。
	 */
	void enable();

	void pixels(GLvoid* pixels);

};

} // namespace orangesignal
#endif // ORANGESIGNAL_GLES20FRAMEBUFFEROBJECT_H_
