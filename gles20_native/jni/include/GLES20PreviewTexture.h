/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20PREVIEWTEXTURE_H_
#define ORANGESIGNAL_GLES20PREVIEWTEXTURE_H_

#include <GLES2/gl2.h>

#include "Mutex.h"

namespace orangesignal {

/**
 * オフスクリーン描画用の OpenGL ES 2.0 のフレームバッファオブジェクト管理クラスを提供します。
 *
 * @author 杉澤 浩二
 */
class GLES20PreviewTexture {
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
	 * テクスチャ識別子を保持します。
	 */
	GLuint mTexName;

	/**
	 * テクスチャターゲットを保持します。
	 */
	GLenum mTexTarget;

	GLubyte* mPreviewData;

	GLint mFormat;

	float mTransformMatrix[16];

	long mTimestamp;

	Mutex mMutex;

public:

	/**
	 * Construct a new GLES20PreviewTexture to stream images to a given OpenGL texture.
	 *
	 * @param texName the OpenGL texture object name (e.g. generated via glGenTextures)
	 */
	GLES20PreviewTexture(const GLuint texName);

	/**
	 * デストラクタです。
	 */
	virtual ~GLES20PreviewTexture();

	/**
	 * getTextureTarget returns the texture target of the current
	 * texture as returned by updateTexImage().
	 *
	 * @return the OpenGL texture target
	 */
	virtual GLenum getTextureTarget();

	/**
	 * Sets the surface to be used for live preview.
	 * Either a surface or surface texture is necessary for preview, and preview is necessary to take pictures.
	 * The same surface texture can be re-set without harm.
	 *
	 * @param width
	 * @param height
	 * @param flipH
	 */
	virtual bool setup(const GLsizei width, const GLsizei height, const bool flipH);

	/**
	 * Update the texture image to the most recent frame from the image stream.  This may only be
	 * called while the OpenGL ES context that owns the texture is bound to the thread.  It will
	 * implicitly bind its texture to the GL_TEXTURE_2D texture target.
	 */
	virtual void updateTexImage();

	/**
	 * Retrieve the 4x4 texture coordinate transform matrix associated with the texture image set by
	 * the most recent call to updateTexImage.
	 *
	 * This transform matrix maps 2D homogeneous texture coordinates of the form (s, t, 0, 1) with s
	 * and t in the inclusive range [0, 1] to the texture coordinate that should be used to sample
	 * that location from the texture.  Sampling the texture outside of the range of this transform
	 * is undefined.
	 *
	 * The matrix is stored in column-major order so that it may be passed directly to OpenGL ES via
	 * the glLoadMatrixf or glUniformMatrix4fv functions.
	 *
	 * @param mtx the array into which the 4x4 matrix will be stored.  The array must have exactly 16 elements.
	 */
	virtual void getTransformMatrix(float mtx[16]);

	virtual void onPreviewFrame(const unsigned char* data);

	virtual void release();

};

} // namespace orangesignal

#endif /* ORANGESIGNAL_GLES20PREVIEWTEXTURE_H_ */
