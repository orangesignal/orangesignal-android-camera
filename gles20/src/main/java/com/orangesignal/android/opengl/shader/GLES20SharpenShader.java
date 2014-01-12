/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20SharpenShader extends GLES20Shader {

	/**
	 * ポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコードです。
	 */
	private static final String VERTEX_SHADER =
		"attribute vec4 aPosition;" +
		"attribute vec4 aTextureCoord;" +

		"uniform float imageWidthFactor;" +
		"uniform float imageHeightFactor;" +
		"uniform float sharpness;" +

		"varying highp vec2 textureCoordinate;" +
		"varying highp vec2 leftTextureCoordinate;" +
		"varying highp vec2 rightTextureCoordinate;" +
		"varying highp vec2 topTextureCoordinate;" +
		"varying highp vec2 bottomTextureCoordinate;" +

		"varying float centerMultiplier;" +
		"varying float edgeMultiplier;" +

		"void main() {" +
			"gl_Position = aPosition;" +

			"mediump vec2 widthStep = vec2(imageWidthFactor, 0.0);" +
			"mediump vec2 heightStep = vec2(0.0, imageHeightFactor);" +

			"textureCoordinate       = aTextureCoord.xy;" +
			"leftTextureCoordinate   = textureCoordinate - widthStep;" +
			"rightTextureCoordinate  = textureCoordinate + widthStep;" +
			"topTextureCoordinate    = textureCoordinate + heightStep;" +
			"bottomTextureCoordinate = textureCoordinate - heightStep;" +

			"centerMultiplier = 1.0 + 4.0 * sharpness;" +
			"edgeMultiplier = sharpness;" +
		"}";

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision highp float;" +	// 演算精度を指定します。

			"uniform lowp sampler2D sTexture;" +

			"varying highp vec2 textureCoordinate;" +
			"varying highp vec2 leftTextureCoordinate;" +
			"varying highp vec2 rightTextureCoordinate;" +
			"varying highp vec2 topTextureCoordinate;" +
			"varying highp vec2 bottomTextureCoordinate;" +

			"varying float centerMultiplier;" +
			"varying float edgeMultiplier;" +

			"void main() {" +
				"mediump vec3 textureColor       = texture2D(sTexture, textureCoordinate).rgb;" +
				"mediump vec3 leftTextureColor   = texture2D(sTexture, leftTextureCoordinate).rgb;" +
				"mediump vec3 rightTextureColor  = texture2D(sTexture, rightTextureCoordinate).rgb;" +
				"mediump vec3 topTextureColor    = texture2D(sTexture, topTextureCoordinate).rgb;" +
				"mediump vec3 bottomTextureColor = texture2D(sTexture, bottomTextureCoordinate).rgb;" +

				"gl_FragColor = vec4((textureColor * centerMultiplier - (leftTextureColor * edgeMultiplier + rightTextureColor * edgeMultiplier + topTextureColor * edgeMultiplier + bottomTextureColor * edgeMultiplier)), texture2D(sTexture, bottomTextureCoordinate).w);" +
			"}";

	private float mImageWidthFactor = 0.004f;
	private float mImageHeightFactor = 0.004f;
	private float mSharpness;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20SharpenShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター /ゲッター

	public float getSharpness() {
		return mSharpness;
	}

	public void setSharpness(final float sharpness) {
		mSharpness = sharpness;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void setFrameSize(final int width, final int height) {
		mImageWidthFactor = 1f / width;
		mImageHeightFactor = 1f / height;
	}

	@Override
	public void onDraw() {
		glUniform1f(getHandle("imageWidthFactor"), mImageWidthFactor);
		glUniform1f(getHandle("imageHeightFactor"), mImageHeightFactor);
		glUniform1f(getHandle("sharpness"), mSharpness);
	}

}