/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_BINDING;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_BINDING_2D;
import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glViewport;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;

/**
 * OpenGL ES 2.0 向け {@link Texture} の基底クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public abstract class GLES20ImageTexture implements Texture {

	/**
	 * 上下を反転させるだけのシンプルなフィルターを提供します。
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	private static final class GLES20FlipVerticalShader extends GLES20Shader {

		/**
		 * 頂点データとテクスチャ座標 (UV マッピング) の構造体配列形式データです。
		 */
		private static final float[] VERTICES_DATA = new float[] {
				// X, Y, Z, U, V
				-1.0f,  1.0f, 0.0f, 0.0f, 0.0f,	// 左上
				 1.0f,  1.0f, 0.0f, 1.0f, 0.0f,	// 右上
				-1.0f, -1.0f, 0.0f, 0.0f, 1.0f,	// 左下
				 1.0f, -1.0f, 0.0f, 1.0f, 1.0f	// 右下
			};

		@Override
		public void setup() {
			super.setup();
			GLES20Utils.updateBufferData(getVertexBufferName(), VERTICES_DATA);
		}

	};

	/**
	 * 入力された画像を描画してテクスチャとして保持するためのフレームバッファオブジェクトです。
	 */
	private final GLES20FramebufferObject mFramebufferObject = new GLES20FramebufferObject();

	/**
	 * 指定された画像データをテクスチャとして関連付けます。
	 * 
	 * @param bitmap 画像データ
	 */
	protected final void attachToTexture(final Bitmap bitmap) {
		if (bitmap == null) {
			throw new IllegalArgumentException("Bitmap must not be  null");
		}
		if (bitmap.isRecycled()) {
			throw new IllegalStateException("Bitmap is recycled");
		}

		// 描画前の設定を保存します。
		final int[] saveFramebuffer = new int[1];
		glGetIntegerv(GL_FRAMEBUFFER_BINDING, saveFramebuffer, 0);
		final int[] saveViewport = new int[4];
		glGetIntegerv(GL_VIEWPORT, saveViewport, 0);
		final int[] saveTexName = new int[1];
		glGetIntegerv(GL_TEXTURE_BINDING_2D, saveTexName, 0);

		/*
		 * Android の Bitmap は画像データが左上座標から開始されます。
		 * しかし OpenGL では左下が開始座標となるため、
		 * そのまま画像データを扱うと上下が逆さまとなってしまいます。
		 * 対応方法はいくつかありますが、ここでは上下を逆さまに描画することで対応します。
		 */
		final GLES20FlipVerticalShader shader = new GLES20FlipVerticalShader();
		final int[] textures = new int[1];
		try {
			glGenTextures(textures.length, textures, 0);
			glBindTexture(GL_TEXTURE_2D, textures[0]);
			GLES20Utils.setupSampler(GL_TEXTURE_2D, GL_LINEAR, GL_NEAREST);

			// 入力となるテクスチャを作成してビットマップデータを与えます。
			GLES20Utils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

			// FBO とシェーダーを用意します。
			mFramebufferObject.setup(bitmap.getWidth(), bitmap.getHeight());
			shader.setup();
			shader.setFrameSize(mFramebufferObject.getWidth(), mFramebufferObject.getHeight());

			// 描画の準備を行います。
			mFramebufferObject.enable();
			glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());
			glClear(GL_COLOR_BUFFER_BIT);

			// 描画します。
			shader.draw(textures[0], null);
		} catch (final RuntimeException e) {
			mFramebufferObject.release();
			throw e;
		} finally {
			// 入力で使用したテクスチャは不要なので削除します。
			glDeleteTextures(textures.length, textures, 0);
			// シェーダーをクリーンアップします。
			shader.release();

			// 描画前のフレームバッファとビューポート設定を復元します。
			glBindFramebuffer(GL_FRAMEBUFFER, saveFramebuffer[0]);
			glViewport(saveViewport[0], saveViewport[1], saveViewport[2], saveViewport[3]);
			glBindTexture(GL_TEXTURE_2D, saveTexName[0]);
		}
	}

	@Override
	public void release() {
		mFramebufferObject.release();
	}

	@Override
	public int getTexName() {
		return mFramebufferObject.getTexName();
	}

	@Override
	public int getWidth() {
		return mFramebufferObject.getWidth();
	}

	@Override
	public int getHeight() {
		return mFramebufferObject.getHeight();
	}

}