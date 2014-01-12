/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import com.orangesignal.android.opengl.GLES20Shader;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20CGAColorspaceShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +

			"void main() {" +
				"highp vec2 sampleDivisor = vec2(1.0 / 200.0, 1.0 / 320.0);" +
				//"highp vec4 colorDivisor = vec4(colorDepth);" +

				"highp vec2 samplePos = vTextureCoord - mod(vTextureCoord, sampleDivisor);" +
				"highp vec4 color = texture2D(sTexture, samplePos);" +

				//"gl_FragColor = texture2D(sTexture, samplePos);" +
				"mediump vec4 colorCyan = vec4(85.0 / 255.0, 1.0, 1.0, 1.0);" +
				"mediump vec4 colorMagenta = vec4(1.0, 85.0 / 255.0, 1.0, 1.0);" +
				"mediump vec4 colorWhite = vec4(1.0, 1.0, 1.0, 1.0);" +
				"mediump vec4 colorBlack = vec4(0.0, 0.0, 0.0, 1.0);" +

				"mediump vec4 endColor;" +
				"highp float blackDistance = distance(color, colorBlack);" +
				"highp float whiteDistance = distance(color, colorWhite);" +
				"highp float magentaDistance = distance(color, colorMagenta);" +
				"highp float cyanDistance = distance(color, colorCyan);" +

				"mediump vec4 finalColor;" +

				"highp float colorDistance = min(magentaDistance, cyanDistance);" +
				"colorDistance = min(colorDistance, whiteDistance);" +
				"colorDistance = min(colorDistance, blackDistance);" +

				"if (colorDistance == blackDistance) {" +
					"finalColor = colorBlack;" +
				"} else if (colorDistance == whiteDistance) {" +
					"finalColor = colorWhite;" +
				"} else if (colorDistance == cyanDistance) {" +
					"finalColor = colorCyan;" +
				"} else {" +
					"finalColor = colorMagenta;" +
				"}" +

				"gl_FragColor = finalColor;" +
			"}";

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20CGAColorspaceShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

}