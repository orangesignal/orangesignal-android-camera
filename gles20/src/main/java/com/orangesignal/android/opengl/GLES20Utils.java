/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.os.Build;
import android.util.Log;

import com.orangesignal.android.camera.BuildConfig;

/**
 * OpenGL ES 2.0 に関するユーティリティを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public final class GLES20Utils {

	static {
		System.loadLibrary("orangesignal-gles20");
	}

	/**
	 * ログ出力用のタグです。
	 */
	private static String TAG = "GLES20Utils";

	/**
	 * オブジェクトが無効であることを表します。<p>
	 * 
	 * @see {@link #createProgram(String, String)}
	 * @see {@link #loadShader(int, String)}
	 */
	public static final int INVALID = 0;

	/**
	 * 指定された直前の OpenGL API 操作についてエラーが発生しているかどうか検証します。
	 * 
	 * @param op 検証する直前に操作した OpenGL API 名
	 * @throws GLException 直前の OpenGL API 操作でエラーが発生している場合
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static void checkGlError(final String op) throws GLException {
		if (BuildConfig.DEBUG) {
			int error;
			while ((error = glGetError()) != GL_NO_ERROR) {
				Log.e(TAG, op + ": glError " + error);
				throw new GLException(error, op + ": glError " + error);
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// Program object

	/**
	 * 指定されたバーテックスシェーダのソースコードとフラグメントシェーダのソースコードを使用してプログラムオブジェクトを作成します。
	 * 
	 * @param vertexSource ポリゴン描画用バーテックスシェーダのソースコード
	 * @param fragmentSource 色描画用のフラグメントシェーダのソースコード
	 * @return プログラムオブジェクトの識別子または {@link #INVALID}
	 * @throws GLException OpenGL API の操作に失敗した場合
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static int createProgram(final String vertexSource, final String fragmentSource) throws GLException {
		// バーテックスシェーダをコンパイルします。
		final int vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
		// フラグメントシェーダをコンパイルします。
		final int pixelShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);

		return createProgram(vertexShader, pixelShader);
	}

	/**
	 * 指定されたバーテックスシェーダとフラグメントシェーダを使用してプログラムを生成します。
	 * 
	 * @param vertexSource ポリゴン描画用バーテックスシェーダのソースコード
	 * @param fragmentSource 色描画用のフラグメントシェーダのソースコード
	 * @return プログラムハンドラまたは {@link #INVALID}
	 * @throws GLException OpenGL API の操作に失敗した場合
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static int createProgram(final int vertexShader, final int pixelShader) throws GLException {
		// プログラムを生成して、プログラムへバーテックスシェーダとフラグメントシェーダを関連付けます。
		final int program = glCreateProgram();
		if (program == 0) {
			throw new RuntimeException("Could not create program");
		}

		// プログラムへバーテックスシェーダを関連付けます。
		glAttachShader(program, vertexShader);
		// プログラムへフラグメントシェーダを関連付けます。
		glAttachShader(program, pixelShader);

		glLinkProgram(program);
		final int[] linkStatus = new int[1];
		glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] != GL_TRUE) {
			Log.e(TAG, "Could not link program: ");
			Log.e(TAG, glGetProgramInfoLog(program));
			glDeleteProgram(program);
			throw new RuntimeException("Could not link program");
		}

		return program;
	}

	/**
	 * 指定されたシェーダのソースコードをコンパイルします。
	 * 
	 * @param shaderType シェーダの種類
	 * @param source シェーダのソースコード
	 * @return シェーダハンドラまたは {@link #INVALID}
	 * @see {@link GLES20#GL_VERTEX_SHADER}
	 * @see {@link GLES20.GL_FRAGMENT_SHADER}
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static int loadShader(final int shaderType, final String source) {
		final int shader = glCreateShader(shaderType);
		if (shader == 0) {
			throw new RuntimeException("Could not create shader " + shaderType + ":" + source);
		}

		glShaderSource(shader, source);
		glCompileShader(shader);
		final int[] compiled = new int[1];
		glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			Log.e(TAG, "Could not compile shader " + shaderType + ":");
			Log.e(TAG, glGetShaderInfoLog(shader));
			glDeleteShader(shader);
			throw new RuntimeException("Could not compile shader " + shaderType + ":" + source);
		}

		return shader;
	}

	//////////////////////////////////////////////////////////////////////////
	// VBO (Buffer object)

	/**
	 * 指定されたサイズのバッファー容量を持つ {@link ByteBuffer} を作成して返します。
	 * 
	 * @param size サイズ
	 * @return 作成された {@link ByteBuffer}
	 */
	public static ByteBuffer toByteBuffer(final int size) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(size);
		buffer.position(0);
		return buffer;
	}

	private static final int FLOAT_SIZE_BYTES = 4;

	/**
	 * 指定されたプリミティブ型配列のバッファーデータから {@link FloatBuffer} を作成して返します。
	 * 
	 * @param array バッファーデータ
	 * @return 作成された {@link FloatBuffer}
	 */
	public static FloatBuffer toFloatBuffer(final float[] data) {
		final FloatBuffer buffer = ByteBuffer
				.allocateDirect(data.length * FLOAT_SIZE_BYTES)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		buffer.put(data).position(0);
		return buffer;
	}

	/**
	 * 指定されたデータでバッファオブジェクトを新規に作成します。
	 * 
	 * @param data データ
	 * @return バッファオブジェクト名
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static int createBuffer(final float[] data) {
		return createBuffer(toFloatBuffer(data));
	}

	/**
	 * 指定されたデータでバッファオブジェクトを新規に作成します。
	 * 
	 * @param data データ
	 * @return バッファオブジェクト名
	 */
	public static int createBuffer(final FloatBuffer data) {
		final int[] buffers = new int[1];
		glGenBuffers(buffers.length, buffers, 0);
		updateBufferData(buffers[0], data);
		return buffers[0];
	}

	/**
	 * 指定されたバッファオブジェクト名を指定されたデータで更新します。
	 * 
	 * @param bufferName バッファオブジェクト名
	 * @param data 更新するデータ
	 */
	public static void updateBufferData(final int bufferName, final float[] data) {
		updateBufferData(bufferName, toFloatBuffer(data));
	}

	/**
	 * 指定されたバッファオブジェクト名を指定されたデータで更新します。
	 * 
	 * @param bufferName バッファオブジェクト名
	 * @param data 更新するデータ
	 */
	public static void updateBufferData(final int bufferName, final FloatBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, bufferName);
		glBufferData(GL_ARRAY_BUFFER, data.capacity() * FLOAT_SIZE_BYTES, data, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	//////////////////////////////////////////////////////////////////////////
	// Texture & Sampler

	/**
	 * サンプラーを構成します。
	 * 
	 * @param target 
	 * @param mag GL_TEXTURE_MAG_FILTER
	 * @param min GL_TEXTURE_MIN_FILTER
	 */
	public static void setupSampler(final int target, final int mag, final int min) {
		// テクスチャを拡大/縮小する方法を設定します。
		glTexParameterf(target, GL_TEXTURE_MAG_FILTER, mag);		// 拡大するときピクセルの中心付近の線形で補完
		glTexParameterf(target, GL_TEXTURE_MIN_FILTER, min);		// 縮小するときピクセルの中心に最も近いテクスチャ要素で補完
		// テクスチャの繰り返し方法を設定します。
		glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void texImage2D(final int target, final int level, final Bitmap bitmap, final int border) {
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		texImage2D(target, level, width, height, border, pixels);
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private static native void texImage2D(int target, int level, int width, int height, int border, int[] pixels);

	//////////////////////////////////////////////////////////////////////////
	// Bitmap

	public static final Bitmap createBitmap(final int[] pixels, final int width, final int height, final Bitmap.Config config) {
		return createBitmap(pixels, width, height, config, 0, false);
	}

	/*
	 * @see http://www.anddev.org/how_to_get_opengl_screenshot__useful_programing_hint-t829.html
	 */
	public static final Bitmap createBitmap(final int[] pixels, final int width, final int height, final Bitmap.Config config, final int orientation, final boolean mirror) {
		// 取得したピクセルデータは R (赤) と B (青) が逆になっています。
		// また垂直方向も逆になっているので以下のように ColorMatrix と Matrix を使用して修正します。

		/*
		 * カラーチャネルを交換するために ColorMatrix と ColorMatrixFilter を使用します。
		 * 
		 * 5x4 のマトリックス: [
		 *   a, b, c, d, e,
		 *   f, g, h, i, j,
		 *   k, l, m, n, o,
		 *   p, q, r, s, t
		 * ]
		 * 
		 * RGBA カラーへ適用する場合、以下のように計算します:
		 * 
		 * R' = a * R + b * G + c * B + d * A + e;
		 * G' = f * R + g * G + h * B + i * A + j;
		 * B' = k * R + l * G + m * B + n * A + o;
		 * A' = p * R + q * G + r * B + s * A + t;
		 * 
		 * R (赤) と B (青) を交換したいので以下の様になります。
		 * 
		 * R' = B => 0, 0, 1, 0, 0
		 * G' = G => 0, 1, 0, 0, 0
		 * B' = R => 1, 0, 0, 0, 0
		 * A' = A => 0, 0, 0, 1, 0
		 */
		final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		// R (赤) と B (青) が逆なので交換します。
		paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
				0, 0, 1, 0, 0,
				0, 1, 0, 0, 0,
				1, 0, 0, 0, 0,
				0, 0, 0, 1, 0
			})));

		final Bitmap bitmap;
		final int diff;
		if ((orientation % 180) == 0) {
			bitmap = Bitmap.createBitmap(width, height, config);
			diff = 0;
		} else {
			bitmap = Bitmap.createBitmap(height, width, config);
			diff = (width - height) / 2;
		}
		final Canvas canvas = new Canvas(bitmap);

		final Matrix matrix = new Matrix();
		// 上下が逆さまなので垂直方向に反転させます。
		matrix.postScale(mirror ? -1.0f : 1.0f, -1.0f, width / 2, height / 2);
		// 傾きを付けます。
		matrix.postRotate(-orientation, width / 2, height / 2);
		if (diff != 0) {
			// 垂直方向の場合は回転による座標のずれを修正します。
			matrix.postTranslate(-diff, diff);
		}

		canvas.concat(matrix);

		// 描画します。
		canvas.drawBitmap(pixels, 0, width, 0, 0, width, height, false, paint);

		return bitmap;
	}

	/**
	 * インスタンス化できない事を強制します。
	 */
	private GLES20Utils() {}

}