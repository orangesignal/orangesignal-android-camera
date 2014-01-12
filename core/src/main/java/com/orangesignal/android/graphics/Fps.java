/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.graphics;

import android.os.Handler;

/**
 * 1秒間のフレーム数を計測して通知するクラスを提供します。
 * 
 * @author 杉澤 浩二
 */
public class Fps implements Runnable {

	/**
	 * FPS を通知するコールバックインタフェースを提供します。
	 */
	public interface Callback {

		/**
		 * FPS を通知します。
		 * 
		 * @param fps FPS
		 */
		void onFps(final int fps);

	}

	/**
	 * FPS を通知するコールバックインタフェースを保持します。
	 */
	Callback mCallback;

	private final Handler mHandler = new Handler();
	private final Runnable mCallbackRunner = new Runnable() {
		@Override
		public void run() {
			mCallback.onFps(mFrameCount);
			mFrameCount = 0;
		}
	};

	/**
	 * 秒間のフレーム数を保持します。
	 */
	volatile int mFrameCount;

	/**
	 * FPS 計測用のスレッドを保持します。
	 */
	private Thread mThread;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param callback FPS を通知するコールバック
	 * @throws NullPointerException {@code callback} が {@code null} の場合
	 */
	public Fps(final Callback callback) {
		if (callback == null) {
			throw new NullPointerException("Callback must not be null");
		}
		mCallback = callback;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * FPS の計測を開始します。
	 */
	public void start() {
		synchronized (this) {
			stop();
			mFrameCount = 0;
			mThread = new Thread(this);
			mThread.start();
		}
	}

	/**
	 * FPS の計測を終了します。
	 */
	public void stop() {
		synchronized (this) {
			mThread = null;
		}
	}

	/**
	 * フレーム数をカウントアップします。
	 */
	public void countup() {
		mFrameCount++;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000L);

				synchronized (this) {
					if (mThread == null || mThread != Thread.currentThread()) {
						break;
					}
				}

				mHandler.post(mCallbackRunner);
			} catch (final InterruptedException e) {
				break;
			}
		}
	}

}