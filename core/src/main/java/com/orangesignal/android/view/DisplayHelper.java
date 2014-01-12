/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.view;

import android.graphics.Point;

/**
 * {@link android.view.Display} に関するヘルパーのインタフェースを提供します。
 * 
 * @author 杉澤 浩二
 */
public interface DisplayHelper {

	/**
	 * 画面の傾きを返します。
	 * 
	 * @return 画面の傾き
	 */
	int getDisplayAngle();

	/**
	 * 画面サイズを返します。
	 * 
	 * @return 画面サイズ
	 */
	Point getDisplaySize();

	/**
	 * 生の画面サイズを返します。
	 * 
	 * @return 生の画面サイズ
	 */
	Point getRawDisplaySize();

}