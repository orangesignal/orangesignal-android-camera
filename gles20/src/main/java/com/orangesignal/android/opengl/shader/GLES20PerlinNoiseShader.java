/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl.shader;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4fv;
import static com.orangesignal.android.opengl.GLES20Compat.glVertexAttribPointer;
import android.annotation.TargetApi;
import android.os.Build;

import com.orangesignal.android.opengl.GLES20FramebufferObject;
import com.orangesignal.android.opengl.GLES20Shader;

@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20PerlinNoiseShader extends GLES20Shader {

	/**
	 * 色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	private static final String FRAGMENT_SHADER =
			"precision highp float;" +	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform float scale;" +
			"uniform vec4 colorStart;" +
			"uniform vec4 colorFinish;" +

			// Description : Array and textureless GLSL 2D/3D/4D simplex noise functions.
			// Author : Ian McEwan, Ashima Arts.
			// Maintainer : ijm
			// Lastmod : 20110822 (ijm)
			// License : Copyright (C) 2011 Ashima Arts. All rights reserved.
			// Distributed under the MIT License. See LICENSE file.
			// https://github.com/ashima/webgl-noise

			"vec4 mod289(vec4 x) {" +
				"return x - floor(x * (1.0 / 289.0)) * 289.0;" +
			"}" +

			"vec4 permute(vec4 x) {" +
				"return mod289(((x * 34.0) + 1.0) * x);" +
			"}" +

			"vec4 taylorInvSqrt(vec4 r) {" +
				"return 1.79284291400159 - 0.85373472095314 * r;" +
			"}" +

			"vec2 fade(vec2 t) {" +
				"return t*t*t*(t*(t*6.0-15.0)+10.0);" +
			"}" +

			// Classic Perlin noise
			"float cnoise(vec2 P) {" +
				"vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);" +
				"vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);" +
				"Pi = mod289(Pi);" +	// To avoid truncation effects in permutation

				"vec4 ix = Pi.xzxz;" +
				"vec4 iy = Pi.yyww;" +
				"vec4 fx = Pf.xzxz;" +
				"vec4 fy = Pf.yyww;" +

				"vec4 i = permute(permute(ix) + iy);" +

				"vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;" +
				"vec4 gy = abs(gx) - 0.5;" +
				"vec4 tx = floor(gx + 0.5);" +
				"gx = gx - tx;" +

				"vec2 g00 = vec2(gx.x,gy.x);" +
				"vec2 g10 = vec2(gx.y,gy.y);" +
				"vec2 g01 = vec2(gx.z,gy.z);" +
				"vec2 g11 = vec2(gx.w,gy.w);" +

				"vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));" +
				"g00 *= norm.x;" +
				"g01 *= norm.y;" +
				"g10 *= norm.z;" +
				"g11 *= norm.w;" +

				"float n00 = dot(g00, vec2(fx.x, fy.x));" +
				"float n10 = dot(g10, vec2(fx.y, fy.y));" +
				"float n01 = dot(g01, vec2(fx.z, fy.z));" +
				"float n11 = dot(g11, vec2(fx.w, fy.w));" +

				"vec2 fade_xy = fade(Pf.xy);" +
				"vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);" +
				"float n_xy = mix(n_x.x, n_x.y, fade_xy.y);" +
				"return 2.3 * n_xy;" +
			"}" +

			"void main() {" +
				"float n1 = (cnoise(vTextureCoord * scale) + 1.0) / 2.0;" +

				"vec4 colorDiff = colorFinish - colorStart;" +
				"vec4 color = colorStart + colorDiff * n1;" +

				"gl_FragColor = color;" +
			"}";

	private float mScale = 8.0f;
	private float[] mColorStart = new float[]{ 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] mColorFinish = new float[]{ 1.0f, 1.0f, 1.0f, 1.0f };

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20PerlinNoiseShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void draw(final int texName, final GLES20FramebufferObject fbo) {
		useProgram();

		glBindBuffer(GL_ARRAY_BUFFER, getVertexBufferName());
		glEnableVertexAttribArray(getHandle(DEFAULT_ATTRIB_POSITION));
		glVertexAttribPointer(getHandle(DEFAULT_ATTRIB_POSITION), VERTICES_DATA_POS_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);
		glEnableVertexAttribArray(getHandle(DEFAULT_ATTRIB_TEXTURE_COORDINATE));
		glVertexAttribPointer(getHandle(DEFAULT_ATTRIB_TEXTURE_COORDINATE), VERTICES_DATA_UV_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET);

		glUniform1f(getHandle("scale"), mScale);
		glUniform4fv(getHandle("colorStart"), 0, mColorStart, 0);
		glUniform4fv(getHandle("colorFinish"), 0, mColorFinish, 0);

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

		glDisableVertexAttribArray(getHandle(DEFAULT_ATTRIB_POSITION));
		glDisableVertexAttribArray(getHandle(DEFAULT_ATTRIB_TEXTURE_COORDINATE));
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

}