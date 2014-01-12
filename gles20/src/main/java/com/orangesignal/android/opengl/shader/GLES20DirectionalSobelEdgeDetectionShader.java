/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20DirectionalSobelEdgeDetectionShader extends GLES20Threex3TextureSamplingShader {

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

				"vec2 gradientDirection;" +
				"gradientDirection.x = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;" +
				"gradientDirection.y = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;" +

				"float gradientMagnitude = length(gradientDirection);" +
				"vec2 normalizedDirection = normalize(gradientDirection);" +
				"normalizedDirection = sign(normalizedDirection) * floor(abs(normalizedDirection) + 0.617316);" +	// Offset by 1-sin(pi/8) to set to 0 if near axis, 1 if away
				"normalizedDirection = (normalizedDirection + 1.0) * 0.5;" +	// Place -1.0 - 1.0 within 0 - 1.0

				"gl_FragColor = vec4(gradientMagnitude, normalizedDirection.x, normalizedDirection.y, 1.0);" +
			"}";

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20DirectionalSobelEdgeDetectionShader() {
		super(FRAGMENT_SHADER);
	}

}