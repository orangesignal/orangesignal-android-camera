/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import com.orangesignal.android.opengl.GLES20Shader;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20GrayscaleShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。
			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"const highp vec3 weight = vec3(0.2125, 0.7154, 0.0721);" +
			"void main() {" +
				"float luminance = dot(texture2D(sTexture, vTextureCoord).rgb, weight);" +
				"gl_FragColor = vec4(vec3(luminance), 1.0);" +
			"}";

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20GrayscaleShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

}