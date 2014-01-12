/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

/**
 * Simple sepia tone filter
 * 
 * @author 杉澤 浩二
 */
public class GLES20SepiaShader extends GLES20ColorMatrixShader {

	private static final float[] SEPIA_COLOR_MATRIX = new float[]{
			0.3588f, 0.7044f, 0.1368f, 0f,
			0.2990f, 0.5870f, 0.1140f, 0f,
			0.2392f, 0.4696f, 0.0912f, 0f,
			0f, 0f, 0f, 1f
		};

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20SepiaShader() {
		super();
		setIntensity(1f);
		setColorMatrix(SEPIA_COLOR_MATRIX);
	}

}