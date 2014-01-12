/*
 * Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.graphics;

/**
 * 
 * @author 杉澤 浩二
 */
public final class MathUtils {

	/**
	 * インスタンス化できない事を強制します。
	 */
	private MathUtils() {}

	/**
	 * 指定された値が 2 の累乗かどうかを返します。
	 * 
	 * @param value 検査する値
	 * @return 指定された値が 2 の累乗かどうか
	 * @see http://www.devmaster.net/forums/showthread.php?t=1728
	 */
	public static boolean isPowerOfTwo(final int value)	{
		if (value != 0) {
			return (value & -value) == value;
		}
		return false;
	}

	/**
	 * 引数の値以上で、最小の {@code 2} の累乗の値を返します。
	 * 
	 * @param x 値
	 * @return 引数の値以上で、最小の {@code 2} の累乗の値
	 */
	public static int nextPowerOfTwo(final int x) {
		return (int) Math.pow(2, Math.ceil(log2(x)));
	}

	/**
	 * 指定された値の {@code 2} を底とする対数を返します。
	 * 
	 * @param x 値
	 * @return {@code x} の {@code 2} を底とする対数
	 */
	public static double log2(final double x) {
		return Math.log(x) / Math.log(2);
	}

}