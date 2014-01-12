/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20TwoInputShader;
import com.orangesignal.android.opengl.Texture;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20AlphaBlendShader extends GLES20TwoInputShader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;\n" +	// 演算精度を指定します。
			"varying highp vec2 vTextureCoord;\n" +
			"uniform lowp sampler2D sTexture;\n" +
			"uniform lowp sampler2D sTexture2;\n" +

			"uniform lowp float mixturePercent;\n" +

			"void main() {" +
				"lowp vec4 color1 = texture2D(sTexture,  vTextureCoord);\n" +
				"lowp vec4 color2 = texture2D(sTexture2, vTextureCoord);\n" +
				"gl_FragColor = vec4(mix(color1.rgb, color2.rgb, color2.a * mixturePercent), color1.a);\n" +
			"}";

	private float mMix = 1f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	public GLES20AlphaBlendShader(final Texture source) {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER, source);
	}

	//////////////////////////////////////////////////////////////////////////

	public float getMix() {
		return mMix;
	}

	public void setMix(final float mix) {
		mMix = mix;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	protected void onDraw() {
		glUniform1f(getHandle("mixturePercent"), mMix);
	}

}