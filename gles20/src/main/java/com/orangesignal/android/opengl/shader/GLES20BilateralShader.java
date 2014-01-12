/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20BilateralShader extends GLES20Shader {

	/**
	 * ポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコードです。
	 */
	private static final String VERTEX_SHADER =
		"attribute vec4 aPosition;" +
		"attribute vec4 aTextureCoord;" +

		"const lowp int GAUSSIAN_SAMPLES = 9;" +

		"uniform highp float texelWidthOffset;" +
		"uniform highp float texelHeightOffset;" +
		"uniform highp float blurSize;" +

		"varying highp vec2 vTextureCoord;" +
		"varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];" +

		"void main() {" +
			"gl_Position = aPosition;" +
			"vTextureCoord = aTextureCoord.xy;" +

			// Calculate the positions for the blur
			"int multiplier = 0;" +
			"highp vec2 blurStep;" +
			"highp vec2 singleStepOffset = vec2(texelHeightOffset, texelWidthOffset) * blurSize;" +

			"for (lowp int i = 0; i < GAUSSIAN_SAMPLES; i++) {" +
				"multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));" +
				// Blur in x (horizontal)
				"blurStep = float(multiplier) * singleStepOffset;" +
				"blurCoordinates[i] = vTextureCoord.xy + blurStep;" +
			"}" +
		"}";

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"uniform lowp sampler2D sTexture;" +

			"const lowp int GAUSSIAN_SAMPLES = 9;" +
			"varying highp vec2 vTextureCoord;" +
			"varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];" +

			//"const mediump float distanceNormalizationFactor = 0.6933613;" +
			"const mediump float distanceNormalizationFactor = 1.5;" +

			"void main() {" +
				"lowp vec4 centralColor = texture2D(sTexture, blurCoordinates[4]);" +
				"lowp float gaussianWeightTotal = 0.18;" +
				"lowp vec4 sum = centralColor * 0.18;" +

				"lowp vec4 sampleColor = texture2D(sTexture, blurCoordinates[0]);" +
				"lowp float distanceFromCentralColor;" +
				//"distanceFromCentralColor = abs(centralColor.g - sampleColor.g);" +
				//"distanceFromCentralColor = smoothstep(0.0, 1.0, abs(centralColor.g - sampleColor.g));" +
				//"distanceFromCentralColor = smoothstep(0.0, 1.0, distance(centralColor, sampleColor) * distanceNormalizationFactor);" +
				"distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);" +

				"lowp float gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);" +
				"gaussianWeightTotal += gaussianWeight;" +
				"sum += sampleColor * gaussianWeight;" +

				"sampleColor = texture2D(sTexture, blurCoordinates[1]);" +
				"distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);" +
				"gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);" +
				"gaussianWeightTotal += gaussianWeight;" +
				"sum += sampleColor * gaussianWeight;" +

				"sampleColor = texture2D(sTexture, blurCoordinates[2]);" +
				"distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);" +
				"gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);" +
				"gaussianWeightTotal += gaussianWeight;" +
				"sum += sampleColor * gaussianWeight;" +

				"sampleColor = texture2D(sTexture, blurCoordinates[3]);" +
				"distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);" +
				"gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);" +
				"gaussianWeightTotal += gaussianWeight;" +
				"sum += sampleColor * gaussianWeight;" +

				"sampleColor = texture2D(sTexture, blurCoordinates[5]);" +
				"distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);" +
				"gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);" +
				"gaussianWeightTotal += gaussianWeight;" +
				"sum += sampleColor * gaussianWeight;" +

				"sampleColor = texture2D(sTexture, blurCoordinates[6]);" +
				"distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);" +
				"gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);" +
				"gaussianWeightTotal += gaussianWeight;" +
				"sum += sampleColor * gaussianWeight;" +

				"sampleColor = texture2D(sTexture, blurCoordinates[7]);" +
				"distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);" +
				"gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);" +
				"gaussianWeightTotal += gaussianWeight;" +
				"sum += sampleColor * gaussianWeight;" +

				"sampleColor = texture2D(sTexture, blurCoordinates[8]);" +
				"distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);" +
				"gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);" +
				"gaussianWeightTotal += gaussianWeight;" +
				"sum += sampleColor * gaussianWeight;" +

				"gl_FragColor = sum / gaussianWeightTotal;" +
			"}";

	private float mTexelWidthOffset = 0.004f;
	private float mTexelHeightOffset = 0.004f;
	private float mBlurSize = 1.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20BilateralShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター /ゲッター

	public float getTexelWidthOffset() {
		return mTexelWidthOffset;
	}

	public void setTexelWidthOffset(final float texelWidthOffset) {
		mTexelWidthOffset = texelWidthOffset;
	}

	public float getTexelHeightOffset() {
		return mTexelHeightOffset;
	}

	public void setTexelHeightOffset(final float texelHeightOffset) {
		mTexelHeightOffset = texelHeightOffset;
	}

	public float getBlurSize() {
		return mBlurSize;
	}

	public void setBlurSize(final float blurSize) {
		mBlurSize = blurSize;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("texelWidthOffset"), mTexelWidthOffset);
		glUniform1f(getHandle("texelHeightOffset"), mTexelHeightOffset);
		glUniform1f(getHandle("blurSize"), mBlurSize);
	}

}