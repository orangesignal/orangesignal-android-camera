/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20WhiteBalanceShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform lowp float temperature;" +
			"uniform lowp float tint;" +

			"const lowp vec3 warmFilter = vec3(0.93, 0.54, 0.0);" +

			"const mediump mat3 RGBtoYIQ = mat3(0.299, 0.587, 0.114, 0.596, -0.274, -0.322, 0.212, -0.523, 0.311);" +
			"const mediump mat3 YIQtoRGB = mat3(1.0, 0.956, 0.621, 1.0, -0.272, -0.647, 1.0, -1.105, 1.702);" +

			"void main() {" +
				"lowp vec4 source = texture2D(sTexture, vTextureCoord);" +

				"mediump vec3 yiq = RGBtoYIQ * source.rgb;" +	// adjusting tint
				"yiq.b = clamp(yiq.b + tint*0.5226*0.1, -0.5226, 0.5226);" +
				"lowp vec3 rgb = YIQtoRGB * yiq;" +

				"lowp vec3 processed = vec3(" +
					"(rgb.r < 0.5 ? (2.0 * rgb.r * warmFilter.r) : (1.0 - 2.0 * (1.0 - rgb.r) * (1.0 - warmFilter.r)))," +	// adjusting temperature
					"(rgb.g < 0.5 ? (2.0 * rgb.g * warmFilter.g) : (1.0 - 2.0 * (1.0 - rgb.g) * (1.0 - warmFilter.g)))," +
					"(rgb.b < 0.5 ? (2.0 * rgb.b * warmFilter.b) : (1.0 - 2.0 * (1.0 - rgb.b) * (1.0 - warmFilter.b))));" +

				"gl_FragColor = vec4(mix(rgb, processed, temperature), source.a);" +
			"}";

	private float mTemperature = 5000f;
	private float mTint;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20WhiteBalanceShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター /ゲッター

	public float getTemperature() {
		return mTemperature;
	}

	public void setTemperature(final float temperature) {
		mTemperature = temperature;
	}

	public float getTint() {
		return mTint;
	}

	public void setTint(final float tint) {
		mTint = tint;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("temperature"), mTemperature);
		glUniform1f(getHandle("tint"), mTint);
	}

}