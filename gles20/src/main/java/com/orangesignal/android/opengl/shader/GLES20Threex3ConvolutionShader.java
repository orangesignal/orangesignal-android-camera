/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniformMatrix3fv;
import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20Threex3ConvolutionShader extends GLES20Threex3TextureSamplingShader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision highp float;\n" +

			"uniform lowp sampler2D sTexture;\n" +
			"uniform mediump mat3 convolutionMatrix;\n" +

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
				"mediump vec4 bottomColor      = texture2D(sTexture, bottomTextureCoordinate);\n" +
				"mediump vec4 bottomLeftColor  = texture2D(sTexture, bottomLeftTextureCoordinate);\n" +
				"mediump vec4 bottomRightColor = texture2D(sTexture, bottomRightTextureCoordinate);\n" +

				"mediump vec4 centerColor = texture2D(sTexture, textureCoordinate);\n" +
				"mediump vec4 leftColor   = texture2D(sTexture, leftTextureCoordinate);\n" +
				"mediump vec4 rightColor  = texture2D(sTexture, rightTextureCoordinate);\n" +

				"mediump vec4 topColor      = texture2D(sTexture, topTextureCoordinate);\n" +
				"mediump vec4 topRightColor = texture2D(sTexture, topRightTextureCoordinate);\n" +
				"mediump vec4 topLeftColor  = texture2D(sTexture, topLeftTextureCoordinate);\n" +

				"mediump vec4 resultColor = topLeftColor * convolutionMatrix[0][0] + topColor * convolutionMatrix[0][1] + topRightColor * convolutionMatrix[0][2];\n" +
				"resultColor += leftColor * convolutionMatrix[1][0] + centerColor * convolutionMatrix[1][1] + rightColor * convolutionMatrix[1][2];\n" +
				"resultColor += bottomLeftColor * convolutionMatrix[2][0] + bottomColor * convolutionMatrix[2][1] + bottomRightColor * convolutionMatrix[2][2];\n" +
				"gl_FragColor = resultColor;\n" +
			"}";

	private static final float[] DEFAULT_CONVOLUTION_KERNEL = new float[] {
			0f, 0f, 0f,
			0f, 1f, 0f,
			0f, 0f, 0f
		};
	
	private float[] mConvolutionKernel;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20Threex3ConvolutionShader() {
		this(DEFAULT_CONVOLUTION_KERNEL);
	}

	public GLES20Threex3ConvolutionShader(final float[] convolutionKernel) {
		super(FRAGMENT_SHADER);
		mConvolutionKernel = convolutionKernel;
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	public float[] getConvolutionKernel() {
		return mConvolutionKernel;
	}

	public void setConvolutionKernel(final float[] convolutionKernel) {
		mConvolutionKernel = convolutionKernel;
	}

	@Override
	public void onDraw() {
		super.onDraw();
		glUniformMatrix3fv(getHandle("convolutionMatrix"), 1, false, mConvolutionKernel, 0);
	}

}