/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform3fv;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20FalseColorShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision lowp float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform vec3 firstColor;" +
			"uniform vec3 secondColor;" +

			"const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);" +

			"void main() {" +
				"lowp vec4 color = texture2D(sTexture, vTextureCoord);" +
				"float luminance = dot(color.rgb, luminanceWeighting);" +
				"gl_FragColor = vec4(mix(firstColor.rgb, secondColor.rgb, luminance), color.a);" +
			"}";

	private float[] mFirstColor = new float[]{ 0.0f, 0.0f, 0.5f, 1.0f };
	private float[] mSecondColor = new float[]{ 1.0f, 0.0f, 0.0f, 1.0f };

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20FalseColorShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター /ゲッター

	public float[] getFirstColor() {
		return mFirstColor;
	}

	public void setFirstColor(final float[] firstColor) {
		mFirstColor = firstColor;
	}

	public float[] getSecondColor() {
		return mSecondColor;
	}

	public void setSecondColor(final float[] secondColor) {
		mSecondColor = secondColor;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform3fv(getHandle("firstColor"), 0, mFirstColor, 0);
		glUniform3fv(getHandle("secondColor"), 0, mSecondColor, 0);
	}

}