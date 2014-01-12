/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import com.orangesignal.android.opengl.GLES20Shader;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20ColorInvertShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。
			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"void main() {" +
				"lowp vec4 color = texture2D(sTexture, vTextureCoord);" +
				"gl_FragColor = vec4((1.0 - color.rgb), color.w);" +
			"}";

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20ColorInvertShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

}