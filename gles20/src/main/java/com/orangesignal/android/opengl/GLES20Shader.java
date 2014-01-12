/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static com.orangesignal.android.opengl.GLES20Compat.glVertexAttribPointer;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;

/**
 * OpenGL ES 2.0 向けのシェーダーオブジェクト管理クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20Shader {

	/**
	 * デフォルトの頂点データのハンドル名です。
	 */
	public static final String DEFAULT_ATTRIB_POSITION = "aPosition";

	/**
	 * デフォルトの UV マッピングデータのハンドル名です。
	 */
	public static final String DEFAULT_ATTRIB_TEXTURE_COORDINATE = "aTextureCoord";

	/**
	 * デフォルトのサンプラーのハンドル名です。
	 */
	public static final String DEFAULT_UNIFORM_SAMPLER = "sTexture";

	/**
	 * デフォルトのポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコードです。
	 */
	protected static final String DEFAULT_VERTEX_SHADER =
			"attribute vec4 aPosition;\n" +
			"attribute vec4 aTextureCoord;\n" +
			"varying highp vec2 vTextureCoord;\n" +
			"void main() {\n" +
				"gl_Position = aPosition;\n" +
				"vTextureCoord = aTextureCoord.xy;\n" +
			"}\n";

	/**
	 * デフォルトの色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	protected static final String DEFAULT_FRAGMENT_SHADER =
			"precision mediump float;\n" +	// 演算精度を指定します。
			"varying highp vec2 vTextureCoord;\n" +
			"uniform lowp sampler2D sTexture;\n" +
			"void main() {\n" +
				"gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
			"}\n";

	/**
	 * 頂点データとテクスチャ座標 (UV マッピング) の構造体配列形式データです。
	 */
	private static final float[] VERTICES_DATA = new float[] {
		// X, Y, Z, U, V
		-1.0f,  1.0f, 0.0f, 0.0f, 1.0f,	// 左上
		 1.0f,  1.0f, 0.0f, 1.0f, 1.0f,	// 右上
		-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,	// 左下
		 1.0f, -1.0f, 0.0f, 1.0f, 0.0f	// 右下
	};

	private static final int FLOAT_SIZE_BYTES = 4;
	protected static final int VERTICES_DATA_POS_SIZE = 3;
	protected static final int VERTICES_DATA_UV_SIZE = 2;
	protected static final int VERTICES_DATA_STRIDE_BYTES = (VERTICES_DATA_POS_SIZE + VERTICES_DATA_UV_SIZE) * FLOAT_SIZE_BYTES;
	protected static final int VERTICES_DATA_POS_OFFSET = 0 * FLOAT_SIZE_BYTES;
	protected static final int VERTICES_DATA_UV_OFFSET = VERTICES_DATA_POS_OFFSET + VERTICES_DATA_POS_SIZE * FLOAT_SIZE_BYTES;

	//////////////////////////////////////////////////////////////////////////

	/**
	 * 頂点シェーダーのソースコードを保持します。
	 */
	private final String mVertexShaderSource;

	/**
	 * フラグメントシェーダーのソースコードを保持します。
	 */
	private final String mFragmentShaderSource;

	/**
	 * プログラム識別子を保持します。
	 */
	private int mProgram;

	/**
	 * 頂点シェーダーの識別子を保持します。
	 */
	private int mVertexShader;

	/**
	 * フラグメントシェーダーの識別子を保持します。
	 */
	private int mFragmentShader;

	/**
	 * 頂点バッファオブジェクト名を保持します。
	 */
	private int mVertexBufferName;

	/**
	 * 変数名とハンドル識別子のマッピングを保持します。
	 */
	private final HashMap<String, Integer> mHandleMap = new HashMap<String, Integer>();

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public GLES20Shader() {
		this(DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER);
	}

	/**
	 * シェーダーのソースコードの文字列リソースを指定してこのクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param res {@link Resources}
	 * @param vertexShaderSourceResId ポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコードの文字列リソースID
	 * @param fragmentShaderSourceResId 色描画用のピクセル/フラグメントシェーダのソースコードのソースコードの文字列リソースID
	 */
	public GLES20Shader(final Resources res, final int vertexShaderSourceResId, final int fragmentShaderSourceResId) {
		this(res.getString(vertexShaderSourceResId), res.getString(fragmentShaderSourceResId));
	}

	/**
	 * シェーダーのソースコードを指定してこのクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param vertexShaderSource ポリゴン描画用のバーテックスシェーダ (頂点シェーダ) のソースコード
	 * @param fragmentShaderSource 色描画用のピクセル/フラグメントシェーダのソースコード
	 */
	public GLES20Shader(final String vertexShaderSource, final String fragmentShaderSource) {
		mVertexShaderSource = vertexShaderSource;
		mFragmentShaderSource = fragmentShaderSource;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * 指定された GLSL ソースコードをコンパイルしてプログラムオブジェクトを構成します。
	 */
	public void setup() {
		release();
		mVertexShader     = GLES20Utils.loadShader(GL_VERTEX_SHADER,   mVertexShaderSource);
		mFragmentShader   = GLES20Utils.loadShader(GL_FRAGMENT_SHADER, mFragmentShaderSource);
		mProgram          = GLES20Utils.createProgram(mVertexShader, mFragmentShader);
		mVertexBufferName = GLES20Utils.createBuffer(VERTICES_DATA);
	}

	/**
	 * フレームサイズを指定します。
	 * 
	 * @param width フレームの幅
	 * @param height フレームの高さ
	 */
	public void setFrameSize(final int width, final int height) {
	}

	/**
	 * このシェーダーオブジェクトの構成を破棄します。
	 */
	public void release() {
		glDeleteProgram(mProgram);
		mProgram = 0;
		glDeleteShader(mVertexShader);
		mVertexShader = 0;
		glDeleteShader(mFragmentShader);
		mFragmentShader = 0;
		glDeleteBuffers(1, new int[]{ mVertexBufferName }, 0);
		mVertexBufferName = 0;

		mHandleMap.clear();
	}

	/**
	 * 指定されたテクスチャ識別子を入力データとして描画します。
	 * このシェーダーによる描画がフレームバッファオブジェクトの切り替えを行う場合は、
	 * 最終的に指定されたフレームバッファオブジェクトへ切り替えます。
	 * 
	 * @param texName テクスチャ識別子
	 * @param fbo フレームバッファオブジェクト (オプショナル)
	 */
	public void draw(final int texName, final GLES20FramebufferObject fbo) {
		useProgram();

		glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferName);
		glEnableVertexAttribArray(getHandle("aPosition"));
		glVertexAttribPointer(getHandle("aPosition"), VERTICES_DATA_POS_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);
		glEnableVertexAttribArray(getHandle("aTextureCoord"));
		glVertexAttribPointer(getHandle("aTextureCoord"), VERTICES_DATA_UV_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texName);
		glUniform1i(getHandle("sTexture"), 0);

		onDraw();

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

		glDisableVertexAttribArray(getHandle("aPosition"));
		glDisableVertexAttribArray(getHandle("aTextureCoord"));
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	/**
	 * 描画する場合に呼び出されます。<p>
	 * サブクラスは追加のパラメータ設定などを行って下さい。
	 */
	protected void onDraw() {}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * プログラムを有効にします。
	 */
	protected final void useProgram() {
		glUseProgram(mProgram);
	}

	/**
	 * 頂点バッファオブジェクトの識別子を返します。
	 * 
	 * @return 頂点バッファオブジェクトの識別子。または {@code 0}
	 */
	protected final int getVertexBufferName() {
		return mVertexBufferName;
	}

	/**
	 * 指定された変数のハンドルを返します。
	 * 
	 * @param name 変数
	 * @return 変数のハンドル
	 */
	protected final int getHandle(final String name) {
		final Integer value = mHandleMap.get(name);
		if (value != null) {
			return value.intValue();
		}

		int location = glGetAttribLocation(mProgram, name);
		if (location == -1) {
			location = glGetUniformLocation(mProgram, name);
		}
		if (location == -1) {
			throw new IllegalStateException("Could not get attrib or uniform location for " + name);
		}
		mHandleMap.put(name, Integer.valueOf(location));
		return location;
	}

	/**
	 * 指定された変数の列挙に対応するハンドルの列挙を返します。
	 * 変数の列挙に {@code null} が指定された場合は {@code null} を返します。
	 * 
	 * @param names 変数の列挙
	 * @return ハンドルの列挙。または {@code null}
	 */
	protected final int[] getHandles(final String...names) {
		if (names == null) {
			return null;
		}

		final int[] results = new int[names.length];
		int count = 0;
		for (final String name : names) {
			results[count] = getHandle(name);
			count++;
		}

		return results;
	}

}