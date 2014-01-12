/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20PixellateShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;\n" +	// 演算精度を指定します。
			"varying vec2 vTextureCoord;\n" +
			"uniform lowp sampler2D sTexture;\n" +

			"uniform highp float fractionalWidthOfPixel;\n" +
			"uniform highp float aspectRatio;\n" +

			"void main() {\n" +
				"highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
				"highp vec2 samplePos = vTextureCoord - mod(vTextureCoord, sampleDivisor) + 0.5 * sampleDivisor;\n" +
				"gl_FragColor = texture2D(sTexture, samplePos);\n" +
			"}\n";

	private float mFractionalWidthOfPixel = 1f / 80f;//0.013f;
	private float mAspectRatio = 1.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20PixellateShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	public float getFractionalWidthOfPixel() {
		return mFractionalWidthOfPixel;
	}

	public void setFractionalWidthOfPixel(final float fractionalWidthOfPixel) {
		mFractionalWidthOfPixel = fractionalWidthOfPixel;
	}

	public float getAspectRatio() {
		return mAspectRatio;
	}

	public void setAspectRatio(final float aspectRatio) {
		mAspectRatio = aspectRatio;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	protected void onDraw() {
		glUniform1f(getHandle("fractionalWidthOfPixel"), mFractionalWidthOfPixel);
		glUniform1f(getHandle("aspectRatio"), mAspectRatio);
	}

}