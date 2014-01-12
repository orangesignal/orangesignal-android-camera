/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.location;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

/**
 * 現在地取得用のユーティティを提供します。
 *
 * @author 杉澤 浩二
 */
public final class MyLocationManager implements LocationListener {

	/**
	 * コールバックインタフェースを提供します。
	 */
	public interface Callback {

		/**
		 * 位置情報プロバイダが見つからない場合に呼び出されます。
		 */
		void onLocationProviderNotFound();

		/**
		 * 最後の位置情報が存在する場合に呼び出されます。
		 * 
		 * @param location 最後の位置情報
		 * @return 位置情報取得を中断するかどうか
		 */
		boolean onLastKnownLocationSuccess(Location location);

		/**
		 * 位置情報の取得を開始した場合に呼び出されます。
		 */
		void onStartLocationUpdates();

		/**
		 * 位置情報の取得を終了した場合に呼び出されます。
		 */
		void onStopLocationUpdates();

		/**
		 * 現在地情報が取得できた場合に呼び出されます。
		 *
		 * @param location 現在地情報
		 */
		void onMyLocationSuccess(Location location);

		/**
		 * 現在地情報が取得できなかった場合に呼び出されます。
		 */
		void onMyLocationFailure();

	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * 位置情報取得サービスを保持します。
	 */
	private LocationManager mLocationManager;

	Callback mCallback;

	private long mMinTime;
	private float mMinDistance;

	private static final int DEFAULT_UPDATE_LIMIT = 2;

	/**
	 * 位置情報の更新限度数を保持します。
	 */
	private int mUpdateLimit = DEFAULT_UPDATE_LIMIT;

	private static final long DEFAULT_TIME_LIMIT = 15 * 1000L;

	/**
	 * 位置情報取得の最大待ち時間 (ミリ秒) を保持します。
	 */
	long mTimeLimit = DEFAULT_TIME_LIMIT;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタです。
	 *
	 * @param context コンテキスト
	 */
	public MyLocationManager(final Context context) {
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	//////////////////////////////////////////////////////////////////////////
	// setter / getter

	/**
	 * {@link LocationManager} オブジェクトを返します。
	 *
	 * @return {@link LocationManager} オブジェクト
	 */
	public LocationManager getLocationManager() {
		return mLocationManager;
	}

	public long getMinTime() {
		return mMinTime;
	}

	public void setMinTime(final long minTime) {
		mMinTime = minTime;
	}

	public float getMinDistance() {
		return mMinDistance;
	}

	public void setMinDistance(final float minDistance) {
		mMinDistance = minDistance;
	}

	/**
	 * 位置情報の更新限度数を返します。
	 *
	 * @return 位置情報の更新限度数
	 */
	public int getUpdateLimit() {
		return mUpdateLimit;
	}

	/**
	 * 位置情報の更新限度数を設定します。
	 *
	 * @param updateLimit 位置情報の更新限度数
	 */
	public void setUpdateLimit(final int updateLimit) {
		mUpdateLimit = updateLimit;
	}

	/**
	 * 位置情報取得の最大待ち時間 (ミリ秒) を返します。
	 *
	 * @return timeLimit 位置情報取得の最大待ち時間 (ミリ秒)
	 */
	public long getTimeLimit() {
		return mTimeLimit;
	}

	/**
	 * 位置情報取得の最大待ち時間 (ミリ秒) を設定します。
	 *
	 * @param timeLimit 位置情報取得の最大待ち時間 (ミリ秒)
	 */
	public void setTimeLimit(final long timeLimit) {
		mTimeLimit = timeLimit;
	}

	/**
	 * コールバックを設定します。
	 *
	 * @param callback コールバック
	 * @return このクラスのインスタンス
	 */
	public MyLocationManager setCallback(final Callback callback) {
		mCallback = callback;
		return this;
	}

	//////////////////////////////////////////////////////////////////////////

	private int mUpdateCount;

	private Timer mTimer;
	long mTime;
	Location mLocation;

	/**
	 * [現在地機能を改善]ダイアログを最後に表示した時間のキーです。
	 */
	/* package */ static final String LAST_ALERT_TIME = "net.jalan.android.location_alert";

	/**
	 * 位置情報取得タイマを起動して位置情報の取得を開始します。
	 */
	public void startMyLocation() {
		if (mLocationManager == null) {
			return;
		}
		stopMyLocation();

		final boolean gps     = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		final boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// 使用可能な位置情報プロバイダが見つからない場合
		if (!gps && !network) {
			mCallback.onLocationProviderNotFound();
			return;
		}

		mUpdateCount = 0;

		final Location lastKnownLocation = getLastKnownLocation();
		if (lastKnownLocation != null) {
			// 最後に取得できた位置情報があれば、とりあえず設定します。
			updateMyLocation(lastKnownLocation);
			// 最後に取得できた位置情報が5分以内のものであれば、これ以上処理を実行しません。
			if (mCallback.onLastKnownLocationSuccess(lastKnownLocation)) {
				return;
			}
		}

		mCallback.onStartLocationUpdates();

		mTime = 0L;
		final Handler handler = new Handler();
		final long INTERVAL = 1000L;

		synchronized (this) {
			if (mTimeLimit > 0) {
				mTimer = new Timer(true);
				mTimer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (mTime > mTimeLimit) {
									stopMyLocation();
									if (mLocation == null) {
										mCallback.onMyLocationFailure();
									} else {
										mCallback.onMyLocationSuccess(mLocation);
									}
									return;
								}
								mTime = mTime + INTERVAL;

							}
						});

					}
				}, 0, INTERVAL);
			}
			// 位置情報の取得を開始します。
			if (gps) {
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mMinTime, mMinDistance, this);
			}
			if (network) {
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
			}
		}
	}

	/**
	 * 位置情報取得タイマを停止して破棄します。<p>
	 * 位置情報取得タイマが起動していない場合は何も行いません。
	 */
	public void stopMyLocation() {
		if (mLocationManager != null) {
			synchronized (this) {
				if (mTimer != null) {
					mTimer.cancel();
					mTimer.purge();
					mTimer = null;
					mLocationManager.removeUpdates(this);
				}
			}
		}
		mCallback.onStopLocationUpdates();
	}

	/**
	 * GPS プロバイダとネットワークプロバイダを使用して最良の LastKnownLocation を取得して返します。<p>
	 * このメソッドは利便性の為に提供しています。
	 *
	 * @return 最良の LastKnownLocation または <code>null</code>
	 * @see {@link LocationUtils#getLastKnownLocation(LocationManager)}
	 */
	public Location getLastKnownLocation() {
		return LocationUtils.getLastKnownLocation(mLocationManager);
	}

	public Location getLocation() {
		return mLocation;
	}

	/**
	 * 指定された位置情報を現在地として設定します。<p>
	 *
	 * @param location 位置情報
	 */
	private void updateMyLocation(final Location location) {
		mLocation = location;
	}

	//////////////////////////////////////////////////////////////////////////
	// android.location.LocationListener

	@Override
	public void onLocationChanged(final Location location) {
		if (LocationUtils.isBetterLocation(mLocation, location)) {
			updateMyLocation(location);
			mUpdateCount++;
			// 位置情報が有効な位置情報として指定された場合に、有効な位置情報の更新可能限界数に達した場合、位置情報の取得が停止されます。
			if (mUpdateCount >= mUpdateLimit) {
				stopMyLocation();
				mCallback.onMyLocationSuccess(mLocation);
			}
		}
	}
	@Override public void onProviderDisabled(final String provider) {}
	@Override public void onProviderEnabled(final String provider) {}
	@Override public void onStatusChanged(final String provider, final int status, final Bundle extras) {}

}