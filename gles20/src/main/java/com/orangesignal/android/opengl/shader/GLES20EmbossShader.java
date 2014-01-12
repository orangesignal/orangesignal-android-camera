/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

public class GLES20EmbossShader extends GLES20Threex3ConvolutionShader {

	private float mIntensity;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20EmbossShader() {
		super();
		setIntensity(1f);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	public float getIntensity() {
		return mIntensity;
	}

	public void setIntensity(final float intensity) {
		mIntensity = intensity;
		setConvolutionKernel(new float[]{
				intensity * -2f, -intensity, 0f,
				-intensity, 1f, intensity,
				0f, intensity, intensity * 2f
			});
	}

}