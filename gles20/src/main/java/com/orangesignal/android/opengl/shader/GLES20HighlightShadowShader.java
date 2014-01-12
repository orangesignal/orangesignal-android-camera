/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20HighlightShadowShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform lowp float shadows;" +
			"uniform lowp float highlights;" +

			"const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);" +

			"void main() {" +
				"lowp vec4 source = texture2D(sTexture, vTextureCoord);" +
				"mediump float luminance = dot(source.rgb, luminanceWeighting);" +

				"mediump float shadow = clamp((pow(luminance, 1.0 / (shadows + 1.0)) + (-0.76) * pow(luminance, 2.0 / (shadows + 1.0))) - luminance, 0.0, 1.0);" +
				"mediump float highlight = clamp((1.0 - (pow(1.0 - luminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - luminance, 2.0 / (2.0 - highlights)))) - luminance, -1.0, 0.0);" +
				"lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0)) / (luminance - 0.0));" +

				"gl_FragColor = vec4(result.rgb, source.a);" +
			"}";

	private float mShadows = 0.0f;
	private float mHighlights = 1.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20HighlightShadowShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	public float getShadows() {
		return mShadows;
	}

	public void setShadows(final float shadows) {
		mShadows = shadows;
	}

	public float getHighlights() {
		return mHighlights;
	}

	public void setHighlights(final float highlights) {
		mHighlights = highlights;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("shadows"), mShadows);
		glUniform1f(getHandle("highlights"), mHighlights);
	}

}