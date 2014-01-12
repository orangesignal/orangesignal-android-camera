/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_BINDING;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_MAX_RENDERBUFFER_SIZE;
import static android.opengl.GLES20.GL_MAX_TEXTURE_SIZE;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_RENDERBUFFER_BINDING;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_BINDING_2D;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glDeleteFramebuffers;
import static android.opengl.GLES20.glDeleteRenderbuffers;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glReadPixels;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glTexImage2D;

import java.nio.IntBuffer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;

/**
 * オフスクリーン描画用の OpenGL ES 2.0 のフレームバッファオブジェクト管理クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20FramebufferObject {

	/**
	 * 幅を保持します。
	 */
	private int mWidth;

	/**
	 * 高さを保持します。
	 */
	private int mHeight;

	/**
	 * フレームバッファ識別子を保持します。
	 */
	private int mFramebufferName;

	/**
	 * レンダーバッファ識別子を保持します。
	 */
	private int mRenderbufferName;

	/**
	 * テクスチャ識別子を保持します。
	 */
	private int mTexName;

	//////////////////////////////////////////////////////////////////////////
	// セッター / ゲッター

	/**
	 * 幅を返します。
	 * 
	 * @return 幅
	 */
	public int getWidth() {
		return mWidth;
	}

	/**
	 * 高さを返します。
	 * 
	 * @return 高さ
	 */
	public int getHeight() {
		return mHeight;
	}

	/**
	 * テクスチャ識別子を返します。
	 * 
	 * @return テクスチャ識別子
	 */
	public int getTexName() {
		return mTexName;
	}

	//////////////////////////////////////////////////////////////////////////
	// パブリック メソッド

	/**
	 * 指定された幅と高さでフレームバッファオブジェクト (FBO) を構成します。<p>
	 * 既にフレームバッファオブジェクト (FBO) が構成されている場合は、
	 * 現在のフレームバッファオブジェクト (FBO) を削除して新しいフレームバッファオブジェクト (FBO) を構成します。
	 * 
	 * @param width 幅
	 * @param height 高さ
	 * @throws IllegalArgumentException {@code width} または {@code height} が {@code GL_MAX_TEXTURE_SIZE} または {@code GL_MAX_RENDERBUFFER_SIZE} よりも大きい場合。
	 * @throws RuntimeException フレームバッファの構成に失敗した場合。
	 */
	public void setup(final int width, final int height) {
		final int[] args = new int[1];

		// パラメータを検証します。

		glGetIntegerv(GL_MAX_TEXTURE_SIZE, args, 0);
		if (width > args[0] || height > args[0]) {
			throw new IllegalArgumentException("GL_MAX_TEXTURE_SIZE " + args[0]);
		}

		glGetIntegerv(GL_MAX_RENDERBUFFER_SIZE, args, 0);
		if (width > args[0] || height > args[0]) {
			throw new IllegalArgumentException("GL_MAX_RENDERBUFFER_SIZE " + args[0]);
		}

		// 現在の構成を保存します。
		glGetIntegerv(GL_FRAMEBUFFER_BINDING, args, 0);
		final int saveFramebuffer = args[0];
		glGetIntegerv(GL_RENDERBUFFER_BINDING, args, 0);
		final int saveRenderbuffer = args[0];
		glGetIntegerv(GL_TEXTURE_BINDING_2D, args, 0);
		final int saveTexName = args[0];

		// 現在のフレームバッファオブジェクトを削除します。
		release();

		try {
			mWidth = width;
			mHeight = height;

			// フレームバッファ識別子を生成します。
			glGenFramebuffers(args.length, args, 0);
			mFramebufferName = args[0];
			// フレームバッファ識別子に対応したフレームバッファオブジェクトを生成します。
			glBindFramebuffer(GL_FRAMEBUFFER, mFramebufferName);

			// レンダーバッファ識別子を生成します。
			glGenRenderbuffers(args.length, args, 0);
			mRenderbufferName = args[0];
			// レンダーバッファ識別子に対応したレンダーバッファオブジェクトを生成します。
			glBindRenderbuffer(GL_RENDERBUFFER, mRenderbufferName);
			// レンダーバッファの幅と高さを指定します。
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);
			// フレームバッファのアタッチメントとしてレンダーバッファをアタッチします。
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, mRenderbufferName);

			// Offscreen position framebuffer texture target
			glGenTextures(args.length, args, 0);
			mTexName = args[0];
			glBindTexture(GL_TEXTURE_2D, mTexName);
			GLES20Utils.setupSampler(GL_TEXTURE_2D, GL_LINEAR, GL_NEAREST);
			// PowerVR SGX530 などは RGB など RGBA 以外の形式を FBO ではサポートしないので RGBA 固定とします。
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
			// フレームバッファのアタッチメントとして 2D テクスチャをアタッチします。
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mTexName, 0);

			// フレームバッファが完全かどうかチェックします。
			final int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
			if (status != GL_FRAMEBUFFER_COMPLETE) {
				throw new RuntimeException("Failed to initialize framebuffer object " + status);
			}
		} catch (final RuntimeException e) {
			release();
			throw e;
		}

		// 保存した構成を復元します。
		glBindFramebuffer(GL_FRAMEBUFFER, saveFramebuffer);
		glBindRenderbuffer(GL_RENDERBUFFER, saveRenderbuffer);
		glBindTexture(GL_TEXTURE_2D, saveTexName);
	}

	/**
	 * クリーンアップを行います。
	 */
	public void release() {
		// フレームバッファとレンダーバッファを削除してしまうとテクスチャの描画がうまくいかない GPU があるので削除する場合はテクスチャのみ残すとかせずに必ず全部削除します。
		final int[] args = new int[1];
		args[0] = mTexName;
		glDeleteTextures(args.length, args, 0);
		mTexName = 0;
		args[0] = mRenderbufferName;
		glDeleteRenderbuffers(args.length, args, 0);
		mRenderbufferName = 0;
		args[0] = mFramebufferName;
		glDeleteFramebuffers(args.length, args, 0);
		mFramebufferName = 0;
	}

	/**
	 * このフレームバッファオブジェクトをバインドして有効にします。
	 */
	public void enable() {
		glBindFramebuffer(GL_FRAMEBUFFER, mFramebufferName);
	}

	//////////////////////////////////////////////////////////////////////////
	// 利便性のためのメソッド

	/**
	 * このフレームバッファオブジェクトの描画内容を {@link Bitmap} として返します。
	 * 
	 * @return {@link Bitmap} オブジェクト
	 */
	public Bitmap getBitmap() {
		return getBitmap(0, false);
	}

	/**
	 * このフレームバッファオブジェクトの描画内容を {@link Bitmap} として返します。
	 * 
	 * @param orientation 傾き
	 * @return {@link Bitmap} オブジェクト
	 */
	public Bitmap getBitmap(final int orientation) {
		return getBitmap(orientation, false);
	}

	/**
	 * このフレームバッファオブジェクトの描画内容を {@link Bitmap} として返します。
	 * 
	 * @param orientation 傾き
	 * @param mirror ミラーモードかどうか
	 * @return {@link Bitmap} オブジェクト
	 */
	public Bitmap getBitmap(final int orientation, final boolean mirror) {
		final int[] pixels = new int[mWidth * mHeight];
		final IntBuffer buffer = IntBuffer.wrap(pixels);
		buffer.position(0);

		// キャプチャします。
		enable();
		glReadPixels(0, 0, mWidth, mHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		return GLES20Utils.createBitmap(pixels, mWidth, mHeight, Bitmap.Config.ARGB_8888, orientation, mirror);
	}

}