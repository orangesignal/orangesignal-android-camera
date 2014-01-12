/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20Threex3TextureSamplingShader extends GLES20Shader {

	/**
	 * ポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコードです。
	 */
	private static final String THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER =
		"attribute vec4 aPosition;" +
		"attribute vec4 aTextureCoord;" +

		"uniform highp float texelWidth;" +
		"uniform highp float texelHeight;" +

		"varying highp vec2 textureCoordinate;" +
		"varying highp vec2 leftTextureCoordinate;" +
		"varying highp vec2 rightTextureCoordinate;" +

		"varying highp vec2 topTextureCoordinate;" +
		"varying highp vec2 topLeftTextureCoordinate;" +
		"varying highp vec2 topRightTextureCoordinate;" +

		"varying highp vec2 bottomTextureCoordinate;" +
		"varying highp vec2 bottomLeftTextureCoordinate;" +
		"varying highp vec2 bottomRightTextureCoordinate;" +

		"void main() {" +
			"gl_Position = aPosition;" +

			"vec2 widthStep = vec2(texelWidth, 0.0);" +
			"vec2 heightStep = vec2(0.0, texelHeight);" +
			"vec2 widthHeightStep = vec2(texelWidth, texelHeight);" +
			"vec2 widthNegativeHeightStep = vec2(texelWidth, -texelHeight);" +

			"textureCoordinate = aTextureCoord.xy;" +
			"leftTextureCoordinate = textureCoordinate - widthStep;" +
			"rightTextureCoordinate = textureCoordinate + widthStep;" +

			"topTextureCoordinate = textureCoordinate - heightStep;" +
			"topLeftTextureCoordinate = textureCoordinate - widthHeightStep;" +
			"topRightTextureCoordinate = textureCoordinate + widthNegativeHeightStep;" +

			"bottomTextureCoordinate = textureCoordinate + heightStep;" +
			"bottomLeftTextureCoordinate = textureCoordinate - widthNegativeHeightStep;" +
			"bottomRightTextureCoordinate = textureCoordinate + widthHeightStep;" +
		"}";

	private float mTexelWidth;
	private float mTexelHeight;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20Threex3TextureSamplingShader(final String fragmentShaderSource) {
		super(THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER, fragmentShaderSource);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター /ゲッター

	public float getTexelWidth() {
		return mTexelWidth;
	}

	public void setTexelWidth(float texelWidth) {
		mTexelWidth = texelWidth;
	}

	public float getTexelHeight() {
		return mTexelHeight;
	}

	public void setTexelHeight(float texelHeight) {
		mTexelHeight = texelHeight;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void setFrameSize(final int width, final int height) {
		mTexelWidth = 1f / width;
		mTexelHeight = 1f / height;
	}

	@Override
	public void onDraw() {
		glUniform1f(getHandle("texelWidth"), mTexelWidth);
		glUniform1f(getHandle("texelHeight"), mTexelHeight);
	}

}