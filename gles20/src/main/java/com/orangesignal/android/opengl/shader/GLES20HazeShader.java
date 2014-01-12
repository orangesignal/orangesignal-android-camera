/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20HazeShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform lowp float distance;" +
			"uniform highp float slope;" +

			"void main() {" +
				// todo reconsider precision modifiers
				"highp vec4 color = vec4(1.0);" +	// todo reimplement as a parameter

				"highp float  d = vTextureCoord.y * slope  +  distance;" +

				"highp vec4 c = texture2D(sTexture, vTextureCoord);" +
				"c = (c - d * color) / (1.0 -d);" +
				"gl_FragColor = c;" +	// consider using premultiply(c);
			"}";

	private float mDistance = 0.2f;
	private float mSlope = 0.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20HazeShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	public float getDistance() {
		return mDistance;
	}

	public void setDistance(final float distance) {
		mDistance = distance;
	}

	public float getSlope() {
		return mSlope;
	}

	public void setSlope(final float slope) {
		mSlope = slope;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("distance"), mDistance);
		glUniform1f(getHandle("slope"), mSlope);
	}

}