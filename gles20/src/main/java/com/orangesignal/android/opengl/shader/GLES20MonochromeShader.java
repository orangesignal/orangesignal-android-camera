/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform3fv;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20MonochromeShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision lowp float;" +	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform float intensity;" +
			"uniform vec3 filterColor;" +

			"const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);" +

			"void main() {" +
				// desat, then apply overlay blend
				"lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);" +
				"float luminance = dot(textureColor.rgb, luminanceWeighting);" +

				"lowp vec4 desat = vec4(vec3(luminance), 1.0);" +

				// overlay
				"lowp vec4 outputColor = vec4(" +
					"(desat.r < 0.5 ? (2.0 * desat.r * filterColor.r) : (1.0 - 2.0 * (1.0 - desat.r) * (1.0 - filterColor.r)))," +
					"(desat.g < 0.5 ? (2.0 * desat.g * filterColor.g) : (1.0 - 2.0 * (1.0 - desat.g) * (1.0 - filterColor.g)))," +
					"(desat.b < 0.5 ? (2.0 * desat.b * filterColor.b) : (1.0 - 2.0 * (1.0 - desat.b) * (1.0 - filterColor.b)))," +
					"1.0" +
				");" +

				// which is better, or are they equal?
				"gl_FragColor = vec4(mix(textureColor.rgb, outputColor.rgb, intensity), textureColor.a);" +
			"}";

	private float mIntensity = 1.0f;
	private float[] mFilterColor = new float[]{ 0.6f, 0.45f, 0.3f };

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20MonochromeShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////


	@Override
	public void onDraw() {
		glUniform1f(getHandle("intensity"), mIntensity);
		glUniform3fv(getHandle("filterColor"), 0, mFilterColor, 0);
	}

}