/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

/**
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20GaussianBlurShader extends GLES20Shader {

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

		"varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];" +

		"void main() {" +
			"gl_Position = aPosition;" +
			"highp vec2 vTextureCoord = aTextureCoord.xy;" +

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

			"const lowp int GAUSSIAN_SAMPLES = 9;" +
			"varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];" +

			"uniform lowp sampler2D sTexture;" +

			"void main() {" +
				"lowp vec4 sum = vec4(0.0);" +

				"sum += texture2D(sTexture, blurCoordinates[0]) * 0.05;" +
				"sum += texture2D(sTexture, blurCoordinates[1]) * 0.09;" +
				"sum += texture2D(sTexture, blurCoordinates[2]) * 0.12;" +
				"sum += texture2D(sTexture, blurCoordinates[3]) * 0.15;" +
				"sum += texture2D(sTexture, blurCoordinates[4]) * 0.18;" +
				"sum += texture2D(sTexture, blurCoordinates[5]) * 0.15;" +
				"sum += texture2D(sTexture, blurCoordinates[6]) * 0.12;" +
				"sum += texture2D(sTexture, blurCoordinates[7]) * 0.09;" +
				"sum += texture2D(sTexture, blurCoordinates[8]) * 0.05;" +

				"gl_FragColor = sum;" +
			"}";

	private float mTexelWidthOffset = 0.01f;
	private float mTexelHeightOffset = 0.01f;
	private float mBlurSize = 0.2f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20GaussianBlurShader() {
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