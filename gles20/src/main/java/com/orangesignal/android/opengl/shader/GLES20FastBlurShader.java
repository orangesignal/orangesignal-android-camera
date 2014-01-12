/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20FastBlurShader extends GLES20Shader {

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

		"varying highp vec2 centerTextureCoordinate;" +
		"varying highp vec2 oneStepLeftTextureCoordinate;" +
		"varying highp vec2 twoStepsLeftTextureCoordinate;" +
		"varying highp vec2 oneStepRightTextureCoordinate;" +
		"varying highp vec2 twoStepsRightTextureCoordinate;" +

		//"const float offset[3] = float[](0.0, 1.3846153846, 3.2307692308);" +

		"void main() {" +
			"gl_Position = aPosition;" +

			"vec2 firstOffset = vec2(1.3846153846 * texelWidthOffset, 1.3846153846 * texelHeightOffset) * blurSize;" +
			"vec2 secondOffset = vec2(3.2307692308 * texelWidthOffset, 3.2307692308 * texelHeightOffset) * blurSize;" +

			"centerTextureCoordinate = aTextureCoord.xy;" +
			"oneStepLeftTextureCoordinate = centerTextureCoordinate - firstOffset;" +
			"twoStepsLeftTextureCoordinate = centerTextureCoordinate - secondOffset;" +
			"oneStepRightTextureCoordinate = centerTextureCoordinate + firstOffset;" +
			"twoStepsRightTextureCoordinate = centerTextureCoordinate + secondOffset;" +
		"}";

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision highp float;" +	// 演算精度を指定します。

			"uniform lowp sampler2D sTexture;" +

			"varying highp vec2 centerTextureCoordinate;" +
			"varying highp vec2 oneStepLeftTextureCoordinate;" +
			"varying highp vec2 twoStepsLeftTextureCoordinate;" +
			"varying highp vec2 oneStepRightTextureCoordinate;" +
			"varying highp vec2 twoStepsRightTextureCoordinate;" +

			//"const float weight[3] = float[]( 0.2270270270, 0.3162162162, 0.0702702703 );" +

			"void main() {" +
				"lowp vec4 color = texture2D(sTexture, centerTextureCoordinate) * 0.2270270270;" +
				"color += texture2D(sTexture, oneStepLeftTextureCoordinate) * 0.3162162162;" +
				"color += texture2D(sTexture, oneStepRightTextureCoordinate) * 0.3162162162;" +
				"color += texture2D(sTexture, twoStepsLeftTextureCoordinate) * 0.0702702703;" +
				"color += texture2D(sTexture, twoStepsRightTextureCoordinate) * 0.0702702703;" +
				"gl_FragColor = color;" +
			"}";

	private float mTexelWidthOffset;
	private float mTexelHeightOffset;
	private float mBlurSize = 1.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20FastBlurShader() {
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