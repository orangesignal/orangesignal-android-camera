/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static com.orangesignal.android.opengl.GLES20Compat.GL_TEXTURE_EXTERNAL_OES;
import static com.orangesignal.android.opengl.GLES20Compat.glVertexAttribPointer;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

/**
 * アスペクト非を保って描画する OpenGL ES 2.0 向けのプレビュー用シェーダーオブジェクト管理クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
final class GLES20PreviewShader extends GLES20Shader {

	/**
	 * デフォルトのポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコードです。
	 */
	private static final String VERTEX_SHADER =
			"uniform mat4 uMVPMatrix;\n" +
			"uniform mat4 uSTMatrix;\n" +
			"uniform float uCRatio;\n" +

			"attribute vec4 aPosition;\n" +
			"attribute vec4 aTextureCoord;\n" +
			"varying highp vec2 vTextureCoord;\n" +

			"void main() {\n" +
				"vec4 scaledPos = aPosition;\n" +
				"scaledPos.x = scaledPos.x * uCRatio;\n" +
				"gl_Position = uMVPMatrix * scaledPos;\n" +
				"vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
			"}\n";

	//////////////////////////////////////////////////////////////////////////

	/**
	 * テクスチャターゲットを保持します。
	 */
	private final int mTexTarget;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param texTarget テクスチャターゲット
	 */
	public GLES20PreviewShader(final int texTarget) {
		super(VERTEX_SHADER, createFragmentShaderSourceOESIfNeed(texTarget));
		mTexTarget = texTarget;
	}

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードを返します。<p>
	 * テクスチャターゲットに {@code GL_TEXTURE_EXTERNAL_OES} が指定された場合は、
	 * サンプラーを {@code sampler2D} から {@code samplerExternalOES} へ置換して、
	 * ソースコードの先頭に {@code GL_OES_EGL_image_external} 拡張を必須とするソースコードを返します。
	 * 
	 * @param texTarget テクスチャターゲット
	 * @return 色描画用のピクセル/フラグメントシェーダのソースコード
	 */
	private static String createFragmentShaderSourceOESIfNeed(final int texTarget) {
		if (texTarget == GL_TEXTURE_EXTERNAL_OES) {
			return new StringBuilder()
					.append("#extension GL_OES_EGL_image_external : require\n")
					.append(DEFAULT_FRAGMENT_SHADER.replace("sampler2D", "samplerExternalOES"))
					.toString();
		}
		return DEFAULT_FRAGMENT_SHADER;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * 描画します。
	 * 
	 * @param texName テクスチャ識別子
	 * @param mvpMatrix MVP マトリックス
	 * @param stMatrix S/T マトリックス
	 * @param aspectRatio テクスチャのアスペクト比
	 */
	public void draw(final int texName, final float[] mvpMatrix, final float[] stMatrix, final float aspectRatio) {
		useProgram();

		glUniformMatrix4fv(getHandle("uMVPMatrix"), 1, false, mvpMatrix, 0);
		glUniformMatrix4fv(getHandle("uSTMatrix"),  1, false, stMatrix,  0);
		glUniform1f(getHandle("uCRatio"), aspectRatio);

		glBindBuffer(GL_ARRAY_BUFFER, getVertexBufferName());
		glEnableVertexAttribArray(getHandle("aPosition"));
		glVertexAttribPointer(getHandle("aPosition"), VERTICES_DATA_POS_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);
		glEnableVertexAttribArray(getHandle("aTextureCoord"));
		glVertexAttribPointer(getHandle("aTextureCoord"), VERTICES_DATA_UV_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(mTexTarget, texName);
		glUniform1i(getHandle(DEFAULT_UNIFORM_SAMPLER), 0);

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

		glDisableVertexAttribArray(getHandle("aPosition"));
		glDisableVertexAttribArray(getHandle("aTextureCoord"));
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

}