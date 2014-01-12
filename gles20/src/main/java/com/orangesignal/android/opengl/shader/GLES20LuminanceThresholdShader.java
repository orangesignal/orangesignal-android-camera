/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20LuminanceThresholdShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform highp float threshold;" +

			"const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);" +

			"void main() {" +
				"highp vec4 color = texture2D(sTexture, vTextureCoord);" +
				"highp float luminance = dot(color.rgb, W);" +
				"highp float thresholdResult = step(threshold, luminance);" +

				"gl_FragColor = vec4(vec3(thresholdResult), color.w);" +
			"}";

	private float mThreshold = 0.5f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20LuminanceThresholdShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター /ゲッター

	public float getThreshold() {
		return mThreshold;
	}

	public void setThreshold(final float threshold) {
		mThreshold = threshold;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("threshold"), mThreshold);
	}

}