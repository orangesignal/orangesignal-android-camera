/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static com.orangesignal.android.opengl.GLES20Compat.glVertexAttribPointer;
import android.annotation.TargetApi;
import android.os.Build;

/**
 * 合成するテクスチャを指定可能な OpenGL ES 2.0 向けのシェーダーオブジェクト管理クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public abstract class GLES20TwoInputShader extends GLES20Shader {

	public static final String UNIFORM_SAMPLER2 = "sTexture2";

	private final Texture mTexture;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param vertexShaderSource ポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコード
	 * @param fragmentShaderSource 色描画用のピクセル/フラグメントシェーダのソースコード
	 * @param texture テクスチャオブジェクト
	 */
	protected GLES20TwoInputShader(final String vertexShaderSource, final String fragmentShaderSource, final Texture texture) {
		super(vertexShaderSource, fragmentShaderSource);
		mTexture = texture;
	}

	//////////////////////////////////////////////////////////////////////////
	// パブリックメソッド

	@Override
	public void setup() {
		release();
		super.setup();
		mTexture.setup();
	}

	@Override
	public void release() {
		mTexture.release();
		super.release();
	}

	@Override
	public void draw(final int texName, final GLES20FramebufferObject fbo) {
		useProgram();

		glBindBuffer(GL_ARRAY_BUFFER, getVertexBufferName());
		glEnableVertexAttribArray(getHandle(DEFAULT_ATTRIB_POSITION));
		glVertexAttribPointer(getHandle(DEFAULT_ATTRIB_POSITION), VERTICES_DATA_POS_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);
		glEnableVertexAttribArray(getHandle(DEFAULT_ATTRIB_TEXTURE_COORDINATE));
		glVertexAttribPointer(getHandle(DEFAULT_ATTRIB_TEXTURE_COORDINATE), VERTICES_DATA_UV_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texName);
		glUniform1i(getHandle(DEFAULT_UNIFORM_SAMPLER), 0);

		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, mTexture.getTexName());
		glUniform1i(getHandle(UNIFORM_SAMPLER2), 1);

		onDraw();

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

		glDisableVertexAttribArray(getHandle(DEFAULT_ATTRIB_POSITION));
		glDisableVertexAttribArray(getHandle(DEFAULT_ATTRIB_TEXTURE_COORDINATE));
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

}