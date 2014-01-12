/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20HueShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform mediump float hueAdjust;" +

			"const highp vec4 kRGBToYPrime = vec4(0.299,     0.587,     0.114,    0.0);" +
			"const highp vec4 kRGBToI      = vec4(0.595716, -0.274453, -0.321263, 0.0);" +
			"const highp vec4 kRGBToQ      = vec4(0.211456, -0.522591,  0.31135,  0.0);" +

			"const highp vec4 kYIQToR = vec4(1.0,  0.9563,  0.6210, 0.0);" +
			"const highp vec4 kYIQToG = vec4(1.0, -0.2721, -0.6474, 0.0);" +
			"const highp vec4 kYIQToB = vec4(1.0, -1.1070,  1.7046, 0.0);" +

			"void main() {" +
				// Sample the input pixel
				"highp vec4 color = texture2D(sTexture, vTextureCoord);" +

				// Convert to YIQ
				"highp float YPrime = dot(color, kRGBToYPrime);" +
				"highp float I      = dot(color, kRGBToI);" +
				"highp float Q      = dot(color, kRGBToQ);" +

				// Calculate the hue and chroma
				"highp float hue    = atan(Q, I);" +
				"highp float chroma = sqrt(I * I + Q * Q);" +

				// Make the user's adjustments
				"hue += (-hueAdjust);" +		// why negative rotation?

				// Convert back to YIQ
				"Q = chroma * sin(hue);" +
				"I = chroma * cos(hue);" +

				// Convert back to RGB
				"highp vec4 yIQ = vec4(YPrime, I, Q, 0.0);" +
				"color.r = dot(yIQ, kYIQToR);" +
				"color.g = dot(yIQ, kYIQToG);" +
				"color.b = dot(yIQ, kYIQToB);" +

				// Save the result
				"gl_FragColor = color;" +
			"}";

	private float mHue = 90f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20HueShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////

	public void setHue(final float hue) {
		mHue = hue % 360;// * (float) Math.PI / 180f;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("hueAdjust"), mHue);
	}

}