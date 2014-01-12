/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20SaturationShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform lowp float saturation;" +

			// Values from "Graphics Shaders: Theory and Practice" by Bailey and Cunningham
			"const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);" +

			"void main() {" +
				"lowp vec4 color = texture2D(sTexture, vTextureCoord);" +
				"lowp float luminance = dot(color.rgb, luminanceWeighting);" +
				"lowp vec3 grey = vec3(luminance);" +

				"gl_FragColor = vec4(mix(grey, color.rgb, saturation), color.w);" +
			"}";

	private float mSaturation = 1.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20SaturationShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター /ゲッター

	public float getSaturation() {
		return mSaturation;
	}

	public void setSaturation(final float saturation) {
		mSaturation = saturation;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("saturation"), mSaturation);
	}

}