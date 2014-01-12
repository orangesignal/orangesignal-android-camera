/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

/**
 * テクスチャオブジェクトのインタフェースを提供します。
 * 
 * @author 杉澤 浩二
 */
public interface Texture {

	/**
	 * テクスチャ識別子を返します。
	 * 
	 * @return テクスチャ識別子。または {@code 0}
	 */
	int getTexName();

	/**
	 * テクスチャの幅を返します。
	 * 
	 * @return テクスチャの幅
	 */
	int getWidth();

	/**
	 * テクスチャの高さを返します。
	 * 
	 * @return テクスチャの高さ
	 */
	int getHeight();

	/**
	 * テクスチャオブジェクトを構成します。<p>
	 * 既にテクスチャオブジェクトが構成されている場合は、
	 * 現在のテクスチャオブジェクトを削除して新しいテクスチャオブジェクトを構成します。
	 */
	void setup();

	/**
	 * クリーンアップを行います。
	 */
	void release();

}