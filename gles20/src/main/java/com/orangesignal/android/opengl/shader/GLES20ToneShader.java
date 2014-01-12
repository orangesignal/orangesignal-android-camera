/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

/**
 * This uses Sobel edge detection to place a black border around objects,
 * and then it quantizes the colors present in the image to give a cartoon-like quality to the image.
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20ToneShader extends GLES20Threex3TextureSamplingShader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision highp float;\n" +

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

//			"uniform highp float intensity;" +
			"uniform highp float threshold;" +
			"uniform highp float quantizationLevels;" +

			"const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);" +

			"void main() {\n" +
				"vec4 textureColor = texture2D(sTexture, textureCoordinate);" +

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
				"vec3 posterizedImageColor = floor((textureColor.rgb * quantizationLevels) + 0.5) / quantizationLevels;" +
				"float thresholdTest = 1.0 - step(threshold, mag);" +
				"gl_FragColor = vec4(posterizedImageColor * thresholdTest, textureColor.a);" +
			"}";

	/**
	 * The threshold at which to apply the edges, default of 0.2
	 */
	private float mThreshold = 0.2f;

	/**
	 * The levels of quantization for the posterization of colors within the scene, with a default of 10.0
	 */
	private float mQuantizationLevels = 10f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20ToneShader() {
		super(FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////

	public float getThreshold() {
		return mThreshold;
	}

	public void setThreshold(final float threshold) {
		mThreshold = threshold;
	}

	public float getQuantizationLevels() {
		return mQuantizationLevels;
	}

	public void setQuantizationLevels(final float quantizationLevels) {
		mQuantizationLevels = quantizationLevels;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("threshold"), mThreshold);
		glUniform1f(getHandle("quantizationLevels"), mQuantizationLevels);
	}

}