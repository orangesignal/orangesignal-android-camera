/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.media;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;

/**
 * 音認識機能を提供します。
 * 
 * @author 杉澤 浩二
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public final class SoundDetector implements Runnable {

	/**
	 * 音認識機能のコールバックインタフェースです。
	 */
	public interface Callback {

		/**
		 * 音認識機能が有効な間呼び出されます。
		 * 
		 * @param volume 音量
		 * @param detect ノイズ計算された閾値
		 */
		void onSoundVolume(float volume, float detect);

		/**
		 * ワンショットでコールバックが設定されている場合に、音認識された場合に呼び出されます。<p>
		 * このメソッドが呼び出された場合、自動的にコールバックが解除されます。
		 * 
		 * @param volume 音量
		 */
		void onSoundDetected(float volume);

	}

	/**
	 * この内部クラスは、音認識用のスレッドから UI スレッドでコールバックを呼び出すためのクラスを提供します。
	 */
	private static final class CallbackRunner implements Runnable {

		public static final int TYPE_ON_SOUND_VOLUME			= 0;
		public static final int TYPE_ON_SOUND_DETECTED		= 1;

		private final Callback mCallback;
		private final int mType;
		private final float mVolume;
		private final float mDetect;

		/**
		 * コンストラクタです。
		 * 
		 * @param type 実行するコールバックの種類
		 * @param cb 実行するコールバック
		 * @param volume コールバックに渡す音量パラメータ
		 * @param detect ノイズ計算された閾値
		 */
		public CallbackRunner(final int type, final Callback cb, final float volume, final float detect) {
			mCallback = cb;
			mType = type;
			mVolume = volume;
			mDetect = detect;
		}

		@Override
		public void run() {
			switch (mType) {
				case TYPE_ON_SOUND_VOLUME:
					mCallback.onSoundVolume(mVolume, mDetect);
					break;
				case TYPE_ON_SOUND_DETECTED:
					mCallback.onSoundDetected(mVolume);
					break;
			}
		}
		
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * ログ出力用のタグです。
	 */
	protected static final String TAG = SoundDetector.class.getSimpleName();

	/**
	 * コールバックを保持します。
	 */
	private Callback mCallback;

	private static final short DEFAULT_DETECT_VOLUME = 3000;

	private short mDetectVolume = DEFAULT_DETECT_VOLUME;

	private final long mDelay = 250L;
	private boolean mRecoding;

	private final Handler mHandler = new Handler();

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public SoundDetector() {}

	//////////////////////////////////////////////////////////////////////////
	// setter / getter

	/**
	 * コールバックを設定します。
	 * 
	 * @param cb コールバック
	 * @return このクラスのインスタンス
	 */
	public SoundDetector setCallback(final Callback cb) {
		mCallback = cb;
		return this;
	}

	/**
	 * 音認識の閾値を設定します。
	 * 
	 * @param volume 音認識の閾値
	 * @return このクラスのインスタンス
	 */
	public SoundDetector setDetectVolume(final short volume) {
		mDetectVolume = volume;
		return this;
	}

	/**
	 * 音認識の閾値を返します。
	 * 
	 * @return 音認識の閾値
	 */
	public short getDetectVolume() {
		return mDetectVolume;
	}

	//////////////////////////////////////////////////////////////////////////
	// public method

	/**
	 * 音認識を開始します。
	 */
	public void start() {
		synchronized (this) {
			mRecoding = true;
			new Thread(this).start();
		}
	}

	public boolean isRecoding() {
		return mRecoding;
	}

	/**
	 * 音認識を終了します。
	 */
	public void stop() {
		synchronized (this) {
			mRecoding = false;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// Runnable

	/**
	 * Hz 単位のサンプルレートです。<p>
	 * ※エミュレータでは最低値の <code>8000</code> (80KHz) 以外のサンプルレートを指定すると落ちます。
	 */
	private static final int SAMPLE_RATE = 8000;// 80.0KHz
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

	/*
	 * 音認識が開始されると呼び出され、音認識を終了するまで処理内部で繰り返し処理します。
	 */
	@Override
	public void run() {
		// この非同期スレッドの優先順位をオーディオ向けに調整します。
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		// 必要なバッファサイズを求めます。
		final int bufferSize = AudioRecord.getMinBufferSize(
				SAMPLE_RATE,
				CHANNEL_CONFIG,
				AUDIO_FORMAT);
		// AudioRecord を構成して開始します。
		final AudioRecord recorder = new AudioRecord(
				MediaRecorder.AudioSource.MIC,
				SAMPLE_RATE,
				CHANNEL_CONFIG,
				AUDIO_FORMAT,
				bufferSize);
		final short[] buffer = new short[bufferSize];
		recorder.startRecording();

		// 雑音の音量を測定します。
		float noise = 0f;
		{
			final long startTimeMillis = System.currentTimeMillis();
			while (true) {
				final int len = recorder.read(buffer, 0, bufferSize);
				if (len > 0) {
					long sum = 0;
					for (int i = 0; i < bufferSize; i++) {
						sum += Math.abs(buffer[i]);
					}
					final float avg = sum / bufferSize;
					noise = (noise + avg) / 2f;
				}
				if ((System.currentTimeMillis() - startTimeMillis) >= mDelay) {
					break;
				}
			}
		}

		// 音認識の主繰り返し処理を実行します。
		while (mRecoding) {
			final int len = recorder.read(buffer, 0, bufferSize);
			if (len > 0) {
				long sum = 0;
				for (int i = 0; i < bufferSize; i++) {
					sum += Math.abs(buffer[i]);
				}
				final float avg = sum / bufferSize;// * mBoostLevel;

				final float detect = mDetectVolume + noise;
				if (mCallback != null) {
					mHandler.post(new CallbackRunner(CallbackRunner.TYPE_ON_SOUND_VOLUME, mCallback, avg, detect));
					if (avg > detect) {
						mHandler.post(new CallbackRunner(CallbackRunner.TYPE_ON_SOUND_DETECTED, mCallback, avg, 0f));
						mCallback = null;
						break;
					}
				}
			}
		}
		try {
			recorder.stop();
		} catch (final IllegalStateException e) {
			// 無視する
		}
		recorder.release();
	}

}