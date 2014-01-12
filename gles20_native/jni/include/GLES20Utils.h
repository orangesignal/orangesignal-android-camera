/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_GLES20UTILS_H_
#define ORANGESIGNAL_GLES20UTILS_H_

#include <GLES2/gl2.h>

namespace orangesignal {

/**
 * OpenGL ES 2.0 に関するユーティリティを提供します。
 *
 * @author 杉澤 浩二
 */
class GLES20Utils {
private:

	/**
	 * インスタンス化できない事を強制します。
	 */
	GLES20Utils();

public:

	/**
	 * 指定されたバーテックスシェーダとフラグメントシェーダを使用してプログラムを生成します。
	 *
	 * @param vertexSource ポリゴン描画用バーテックスシェーダのソースコード
	 * @param fragmentSource 色描画用のフラグメントシェーダのソースコード
	 * @return プログラムハンドラまたは {@link #INVALID}
	 * @throws GLException OpenGL API の操作に失敗した場合
	 */
	static GLuint createProgram(const char* vertexSource, const char* fragmentSource);

	/**
	 * 指定されたバーテックスシェーダとフラグメントシェーダを使用してプログラムを生成します。
	 *
	 * @param vertexSource ポリゴン描画用バーテックスシェーダのソースコード
	 * @param fragmentSource 色描画用のフラグメントシェーダのソースコード
	 * @return プログラムハンドラまたは {@link #INVALID}
	 * @throws GLException OpenGL API の操作に失敗した場合
	 */
	static GLuint createProgram(const GLuint vertexShader, const GLuint pixelShader);

	/**
	 * 指定されたシェーダのソースコードをコンパイルします。
	 *
	 * @param shaderType シェーダの種類
	 * @param source シェーダのソースコード
	 * @return シェーダハンドラまたは {@link #INVALID}
	 * @see {@link GLES20#GL_VERTEX_SHADER}
	 * @see {@link GLES20.GL_FRAGMENT_SHADER}
	 */
	static GLuint loadShader(const GLenum shaderType, const char* source);

	/**
	 * 指定された直前の OpenGL API 操作についてエラーが発生しているかどうか検証します。
	 *
	 * @param op 検証する直前に操作した OpenGL API 名
	 * @throws GLException 直前の OpenGL API 操作でエラーが発生している場合
	 */
	static void checkGlError(const char* op);

	/**
	 * サンプラーを構成します。
	 *
	 * @param target
	 * @param mag GL_TEXTURE_MAG_FILTER
	 * @param min GL_TEXTURE_MIN_FILTER
	 */
	static void setupSampler(const GLenum target, const GLfloat mag, const GLfloat min);

	/**
	 * 指定されたデータでバッファオブジェクトを新規に作成します。
	 *
	 * @param data データ
	 * @return バッファオブジェクト名
	 */
	static GLuint createBuffer(const GLfloat* data);

	/**
	 * 指定されたバッファオブジェクト名を指定されたデータで更新します。
	 *
	 * @param bufferName バッファオブジェクト名
	 * @param data 更新するデータ
	 */
	static void updateBufferData(const GLuint bufferName, const GLfloat* data);

};

} // namespace orangesignal
#endif // ORANGESIGNAL_GLES20UTILS_H_
