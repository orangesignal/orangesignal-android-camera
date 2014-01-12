/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20RGBShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform highp float red;" +
			"uniform highp float green;" +
			"uniform highp float blue;" +

			"void main() {" +
				"highp vec4 color = texture2D(sTexture, vTextureCoord);" +
				"gl_FragColor = vec4(color.r * red, color.g * green, color.b * blue, 1.0);" +
			"}";

	private float mRed = 1.0f;
	private float mGreen = 1.0f;
	private float mBlue = 1.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20RGBShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター /ゲッター

	public float getRed() {
		return mRed;
	}

	public void setRed(final float red) {
		mRed = red;
	}

	public float getGreen() {
		return mGreen;
	}

	public void setGreen(final float green) {
		mGreen = green;
	}

	public float getBlue() {
		return mBlue;
	}

	public void setBlue(final float blue) {
		mBlue = blue;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("red"), mRed);
		glUniform1f(getHandle("green"), mGreen);
		glUniform1f(getHandle("blue"), mBlue);
	}

}