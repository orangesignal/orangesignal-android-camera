/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20SHADER_H_
#define ORANGESIGNAL_GLES20SHADER_H_

#include <map>
#include <string>

#include <GLES2/gl2.h>

#include "GLES20FramebufferObject.h"

namespace orangesignal {

/**
 * OpenGL ES 2.0 向けのシェーダーオブジェクト管理クラスを提供します。
 *
 * @author 杉澤 浩二
 */
class GLES20Shader {
public:

	/**
	 * デフォルトの頂点データのハンドル名です。
	 */
	static const std::string ATTRIB_POSITION;

	/**
	 * デフォルトの UV マッピングデータのハンドル名です。
	 */
	static const std::string ATTRIB_TEXTURE_COORDINATE;

	/**
	 * デフォルトのサンプラーのハンドル名です。
	 */
	static const std::string UNIFORM_SAMPLER;

protected:

	/**
	 * デフォルトのポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコードです。
	 */
	static const char DEFAULT_VERTEX_SHADER[];

	/**
	 * デフォルトの色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	static const char DEFAULT_FRAGMENT_SHADER[];

private:

	/**
	 * 頂点データとテクスチャ座標 (UV マッピング) の構造体配列形式データです。
	 */
	static const float VERTICES_DATA[];

protected:

	static const GLint VERTICES_DATA_POS_SIZE;
	static const GLint VERTICES_DATA_UV_SIZE;
	static const GLsizei VERTICES_DATA_STRIDE_BYTES;
	static const GLint VERTICES_DATA_POS_OFFSET;
	static const GLint VERTICES_DATA_UV_OFFSET;

private:

	/**
	 * 頂点シェーダーのソースコードを保持します。
	 */
	const char* mVertexShaderSource;

	/**
	 * フラグメントシェーダーのソースコードを保持します。
	 */
	const char* mFragmentShaderSource;

	/**
	 * プログラム識別子を保持します。
	 */
	GLuint mProgram;

	/**
	 * 頂点シェーダーの識別子を保持します。
	 */
	GLuint mVertexShader;

	/**
	 * フラグメントシェーダーの識別子を保持します。
	 */
	GLuint mFragmentShader;

	/**
	 * 頂点バッファオブジェクト名を保持します。
	 */
	GLuint mVertexBufferName;

	/**
	 * 変数名とハンドル識別子のマッピングを保持します。
	 */
	std::map<std::string, GLuint> mHandleMap;

public:

	/**
	 * デフォルトコンストラクタです。
	 */
	GLES20Shader();

	/**
	 * シェーダーのソースコードを指定してこのクラスのインスタンスを構築するコンストラクタです。
	 *
	 * @param vertexShaderSource ポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコード
	 * @param fragmentShaderSource 色描画用のピクセル/フラグメントシェーダのソースコード
	 */
	GLES20Shader(const char* vertexShaderSource, const char* fragmentShaderSource);

	virtual ~GLES20Shader();

	/**
	 * 指定された GLSL ソースコードをコンパイルしてプログラムオブジェクトを構成します。
	 */
	virtual void setup();

	virtual void setFrameSize(const GLsizei width, const GLsizei height);

	/**
	 * このシェーダーオブジェクトの構成を破棄します。
	 */
	virtual void release();

	/**
	 * 描画します。
	 *
	 * @param texName テクスチャ識別子
	 */
	virtual void draw(const GLuint texName, GLES20FramebufferObject fbo);

protected:

	/**
	 * 描画する場合に呼び出されます。<p>
	 * サブクラスは追加のパラメータ設定などを行って下さい。
	 */
	virtual void onDraw();

	/**
	 * プログラムを有効にします。
	 */
	void useProgram();

	/**
	 * 頂点バッファオブジェクトの識別子を返します。
	 *
	 * @return 頂点バッファオブジェクトの識別子。または {@code 0}
	 */
	GLuint getVertexBufferName();

	/**
	 * 指定された変数のハンドルを返します。
	 *
	 * @param name 変数
	 * @return 変数のハンドル
	 */
	GLuint getHandle(const std::string name);

};

} // namespace orangesignal
#endif // ORANGESIGNAL_GLES20SHADER_H_
