/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.glUniform1f;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20HsvShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision highp float;" + 	// 演算精度を指定します。
			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +

			"uniform highp float hueAdjust;" +
			"uniform highp float chromaAdjust;" +
			"uniform highp float brightness;" +

			"vec3 RGB2HSV(vec4 color) {" +
				"float r = color[0];" +
				"float g = color[1];" +
				"float b = color[2];" +

				"vec3 res;" +

				"float minv = min(min(r, g), b);" +
				"float maxv = max(max(r, g), b);" +
				"res.z = maxv;" +	// v

				"float delta = maxv - minv;" +

				// branch1  maxv == 0.0
				"if (maxv != 0.0) {" +
					"res.y = delta / maxv;" +	// s
				"} else {" +
					// r = g = b = 0      // s = 0, v is undefined
					"res.y = 0.0;" +
					"res.x = -1.0;" +
					"return res;" +
				"}" +

				"if (r == maxv) {" +
					"res.x = (g - b) / delta;" +	// between yellow & magenta
				"} else if (g == maxv) {" +
					"res.x = 2.0 + (b - r) / delta;" +	// between cyan & yellow
				"} else {" +
					"res.x = 4.0 + (r - g) / delta;" +	// between magenta & cyan
				"}" +

				"res.x = res.x * 60.0;" +	// degrees
				"if (res.x < 0.0) {" +
					"res.x = res.x + 360.0;" +
				"}" +

				"return res;" +
			"}" +

			"vec3 HSV2RGB(vec3 hsv) {" +
				"float hue = hsv.x;" +
				"float s = hsv.y;" +
				"float v = hsv.z;" +

				"if (s == 0.0) {" +
					// achromatic (grey)
					"return vec3(v, v, v);" +
				"}" +

				"int h = int(floor(hue / 60.0));" + // sector 0 to 5
				"float f = hue / 60.0 - float(h);" +	//  factorial part of h
				"float p = v * (1.0 - s);" +
				"float q = v * (1.0 - s * f);" +
				"float r = v * (1.0 - s * (1.0 - f));" +

				"if (h == 0)      return vec3(v, r, p);" +
				"else if (h == 1) return vec3(q, v, p);" +
				"else if (h == 2) return vec3(p, v, r);" +
				"else if (h == 3) return vec3(p, q, v);" +
				"else if (h == 4) return vec3(r, p, v);" +
				"else             return vec3(v, p, q);" +
			"}" +

			"void main() {" +
				// Sample the input pixel
				"highp vec4 color = texture2D(sTexture, vTextureCoord);" +
				"highp vec3 hsv = RGB2HSV(color);" + 
				"hsv[0] = hueAdjust;"  +
				"hsv[1] = chromaAdjust;"  +
				"hsv[2] = hsv[2] + brightness;"  +

				"gl_FragColor.rgb = HSV2RGB(hsv);" +
				"gl_FragColor.a = 1.0;" +
			"}";

	/**
	 * 色相を保持します。
	 */
	private float mHue;

	/**
	 * 彩度を保持します。
	 */
	private float mChroma;

	/**
	 * 明度を保持します。
	 */
	private float mBrightness;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param h 色相 (0～360)
	 * @param s 彩度 (0～100)
	 * @param v 明度 (0～100)
	 */
	public GLES20HsvShader(final float h, final float s, final float v) {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);

		mHue = h;
		mChroma = s;
		mBrightness = v / 255f;
	}

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	public float getHue() {
		return mHue;
	}

	public void setHue(final float hue) {
		mHue = hue;
	}

	public float getChroma() {
		return mChroma;
	}

	public void setChroma(final float chroma) {
		mChroma = chroma;
	}

	public float getBrightness() {
		return mBrightness;
	}

	public void setBrightness(final float brightness) {
		mBrightness = brightness;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	protected void onDraw() {
		glUniform1f(getHandle("hueAdjust"), mHue);
		glUniform1f(getHandle("chromaAdjust"), mChroma);
		glUniform1f(getHandle("brightness"), mBrightness);
	}

}