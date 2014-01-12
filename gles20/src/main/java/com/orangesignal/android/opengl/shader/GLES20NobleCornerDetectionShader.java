/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20NobleCornerDetectionShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision mediump float;\n" + 	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform lowp float sensitivity;" +

			"void main() {" +
				"mediump vec3 derivativeElements = texture2D(sTexture, vTextureCoord).rgb;" +
				"mediump float derivativeSum = derivativeElements.x + derivativeElements.y;" +

				// R = (Ix^2 * Iy^2 - Ixy * Ixy) / (Ix^2 + Iy^2)
				"mediump float zElement = (derivativeElements.z * 2.0) - 1.0;" +
				//	"mediump float harrisIntensity = (derivativeElements.x * derivativeElements.y - (derivativeElements.z * derivativeElements.z)) / (derivativeSum);" +
				"mediump float cornerness = (derivativeElements.x * derivativeElements.y - (zElement * zElement)) / (derivativeSum);" +

				// Original Harris detector
				// R = Ix^2 * Iy^2 - Ixy * Ixy - k * (Ix^2 + Iy^2)^2
				//	"highp float harrisIntensity = derivativeElements.x * derivativeElements.y - (derivativeElements.z * derivativeElements.z) - harrisConstant * derivativeSum * derivativeSum;" +

				//	"gl_FragColor = vec4(vec3(harrisIntensity * 7.0), 1.0);" +
				"gl_FragColor = vec4(vec3(cornerness * sensitivity), 1.0);" +
			"}";

	private float mSensitivity = 2.0f;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20NobleCornerDetectionShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	public float getSensitivity() {
		return mSensitivity;
	}

	public void setSensitivity(final float sensitivity) {
		mSensitivity = sensitivity;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("sensitivity"), mSensitivity);
	}

}