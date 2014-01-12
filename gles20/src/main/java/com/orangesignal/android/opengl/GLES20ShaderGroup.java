/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glClear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Pair;

/**
 * 複数のシェーダーオブジェクトを管理する OpenGL ES 2.0 向けのシェーダーオブジェクト管理クラスを提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class GLES20ShaderGroup extends GLES20Shader {

	/**
	 * シェーダーオブジェクトのコレクションを保持します。
	 */
	private final Collection<GLES20Shader> mShaders;

	/**
	 * シェーダーオブジェクトとフレームバッファオブジェクトのリストを保持します。
	 */
	private final ArrayList<Pair<GLES20Shader, GLES20FramebufferObject>> mList = new ArrayList<Pair<GLES20Shader, GLES20FramebufferObject>>();

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * 指定されたシェーダーオブジェクトの列挙で、このクラスをインスタンス化するコンストラクタです。
	 * 
	 * @param shaders シェーダーオブジェクトの列挙
	 */
	public GLES20ShaderGroup(final GLES20Shader... shaders) {
		this(Arrays.asList(shaders));
	}

	/**
	 * 指定されたシェーダーオブジェクトのコレクションで、このクラスをインスタンス化するコンストラクタです。
	 * 
	 * @param shaders シェーダーオブジェクトのコレクション
	 */
	public GLES20ShaderGroup(final Collection<GLES20Shader> shaders) {
		mShaders = shaders;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void setup() {
		super.setup();

		if (mShaders != null) {
			// シェーダーとフレームバッファオブジェクトのリストを構築します。
			final int max = mShaders.size();
			int count = 0;

			for (final GLES20Shader shader : mShaders) {
				shader.setup();

				// 最後のシェーダーにはフレームバッファオブジェクトを装着しません。
				final GLES20FramebufferObject fbo;
				if ((count + 1) < max) {
					fbo = new GLES20FramebufferObject();
				} else {
					fbo = null;
				}
				mList.add(Pair.create(shader, fbo));
				count++;
			}
		}
	}

	@Override
	public void release() {
		for (final Pair<GLES20Shader, GLES20FramebufferObject> pair : mList) {
			if (pair.first != null) {
				pair.first.release();
			}
			if (pair.second != null) {
				pair.second.release();
			}
		}
		mList.clear();
		super.release();
	}

	@Override
	public void setFrameSize(final int width, final int height) {
		super.setFrameSize(width, height);

		for (final Pair<GLES20Shader, GLES20FramebufferObject> pair : mList) {
			if (pair.first != null) {
				pair.first.setFrameSize(width, height);
			}
			if (pair.second != null) {
				pair.second.setup(width, height);
			}
		}
	}

	private int mPrevTexName;

	@Override
	public void draw(final int texName, final GLES20FramebufferObject fbo) {
		mPrevTexName = texName;
		// シェーダー単位に処理を行います。
		for (final Pair<GLES20Shader, GLES20FramebufferObject> pair : mList) {
			// 最後以外のシェーダーの場合
			if (pair.second != null) {
				// 描画します。
				if (pair.first != null) {
					// シェーダー単位のフレームバッファオブジェクトへ切り替えます。
					pair.second.enable();
					glClear(GL_COLOR_BUFFER_BIT);

					pair.first.draw(mPrevTexName, pair.second);
				}
				mPrevTexName = pair.second.getTexName();

			// 最後のシェーダーの場合
			} else {
				if (fbo != null) {
					fbo.enable();
				} else {
					glBindFramebuffer(GL_FRAMEBUFFER, 0);
				}

				// 描画します。
				if (pair.first != null) {
					pair.first.draw(mPrevTexName, fbo);
				}
			}
		}
	}

}