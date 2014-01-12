/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20ShaderGroup;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20SobelEdgeDetectionShader extends GLES20ShaderGroup {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;\n" +

			"uniform lowp sampler2D sTexture;\n" +

			"varying vec2 textureCoordinate;\n" +
			"varying vec2 leftTextureCoordinate;\n" +
			"varying vec2 rightTextureCoordinate;\n" +

			"varying vec2 topTextureCoordinate;\n" +
			"varying vec2 topLeftTextureCoordinate;\n" +
			"varying vec2 topRightTextureCoordinate;\n" +

			"varying vec2 bottomTextureCoordinate;\n" +
			"varying vec2 bottomLeftTextureCoordinate;\n" +
			"varying vec2 bottomRightTextureCoordinate;\n" +

			"void main() {\n" +
				"float bottomLeftIntensity = texture2D(sTexture, bottomLeftTextureCoordinate).r;" +
				"float topRightIntensity = texture2D(sTexture, topRightTextureCoordinate).r;" +
				"float topLeftIntensity = texture2D(sTexture, topLeftTextureCoordinate).r;" +
				"float bottomRightIntensity = texture2D(sTexture, bottomRightTextureCoordinate).r;" +
				"float leftIntensity = texture2D(sTexture, leftTextureCoordinate).r;" +
				"float rightIntensity = texture2D(sTexture, rightTextureCoordinate).r;" +
				"float bottomIntensity = texture2D(sTexture, bottomTextureCoordinate).r;" +
				"float topIntensity = texture2D(sTexture, topTextureCoordinate).r;" +

				"float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;" +
				"float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;" +

				"float mag = length(vec2(h, v));" +
				"gl_FragColor = vec4(vec3(mag), 1.0);" +
			"}";

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20SobelEdgeDetectionShader() {
		this(FRAGMENT_SHADER);
	}

	protected GLES20SobelEdgeDetectionShader(final String fragmentShaderSource) {
		super(new GLES20GrayscaleShader(), new GLES20Threex3TextureSamplingShader(fragmentShaderSource));
	}

}