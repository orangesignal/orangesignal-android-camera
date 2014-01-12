/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20ColorMatrixShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform lowp mat4 colorMatrix;" +
			"uniform lowp float intensity;" +

			"void main() {" +
				"lowp vec4 color = texture2D(sTexture, vTextureCoord);" +
				"lowp vec4 outputColor = color * colorMatrix;" +
				"gl_FragColor = (intensity * outputColor) + ((1.0 - intensity) * color);" +
			"}";

	private float[] mColorMatrix = new float[]{
			1f, 0f, 0f, 0f,
			0f, 1f, 0f, 0f,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f
		};

	private float mIntensity = 1.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20ColorMatrixShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	public float[] getColorMatrix() {
		return mColorMatrix;
	}

	public void setColorMatrix(float[] colorMatrix) {
		mColorMatrix = colorMatrix;
	}

	public float getIntensity() {
		return mIntensity;
	}

	public void setIntensity(float intensity) {
		mIntensity = intensity;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniformMatrix4fv(getHandle("colorMatrix"), 0, false, mColorMatrix, 0);
		glUniform1f(getHandle("intensity"), mIntensity);
	}

}