/*
 * Copyright (c) 2010-2012 OrangeSignal.com. All Rights Reserved.
 */

package com.orangesignal.android.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * 位置情報に関するユーティリティを提供します。<p>
 *
 * @author 杉澤 浩二
 */
public final class LocationUtils {

	/**
	 * インスタンス化できない事を強制します。
	 */
	private LocationUtils() {}

	//////////////////////////////////////////////////////////////////////////
	// Last Known Location

	/**
	 * GPS プロバイダとネットワークプロバイダを使用して最良の LastKnownLocation を取得して返します。<p>
	 * このメソッドは利便性の為に提供しています。
	 *
	 * @param context コンテキスト
	 * @return 最良の LastKnownLocation または {@code null}
	 * @throws IllegalArgumentException {@code context} が {@code null} の場合
	 * @see {@link LocationManager#getLastKnownLocation(String)}
	 */
	public static Location getLastKnownLocation(final Context context) {
		if (context == null) {
			throw new IllegalArgumentException("Context must not be null");
		}
		return getLastKnownLocation((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
	}

	/**
	 * GPS プロバイダとネットワークプロバイダを使用して最良の LastKnownLocation を取得して返します。<p>
	 * このメソッドは利便性の為に提供しています。
	 *
	 * @param context コンテキスト
	 * @param providers 位置情報プロバイダ識別子の列挙
	 * @return 最良の LastKnownLocation または {@code null}
	 * @throws IllegalArgumentException {@code context} が {@code null} の場合
	 * @see {@link LocationManager#getLastKnownLocation(String)}
	 */
	public static Location getLastKnownLocation(final Context context, final String...providers) {
		if (context == null) {
			throw new IllegalArgumentException("Context must not be null");
		}
		return getLastKnownLocation((LocationManager) context.getSystemService(Context.LOCATION_SERVICE), providers);
	}

	/**
	 * GPS プロバイダとネットワークプロバイダを使用して最良の LastKnownLocation を取得して返します。<p>
	 * このメソッドは利便性の為に提供しています。
	 *
	 * @param locationManager 位置情報サービス
	 * @return 最良の LastKnownLocation または {@code null}
	 * @see {@link LocationManager#getLastKnownLocation(String)}
	 */
	public static Location getLastKnownLocation(final LocationManager locationManager) {
		return getLastKnownLocation(locationManager, LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER);
	}

	/**
	 * 指定された位置情報プロバイダ識別子の列挙を使用して最良の LastKnownLocation を取得して返します。
	 *
	 * @param locationManager 位置情報サービス
	 * @param providers 位置情報プロバイダ識別子の列挙
	 * @return 最良の LastKnownLocation または {@code null}
	 * @throws IllegalArgumentException 位置情報プロバイダ識別子が不正な場合
	 * @see {@link LocationManager#getLastKnownLocation(String)}
	 */
	public static Location getLastKnownLocation(final LocationManager locationManager, final String...providers) /* throws IllegalArgumentException */ {
		if (locationManager == null || providers == null) {
			return null;
		}

		Location result = null;
		for (final String provider : providers) {
			if (provider != null && locationManager.isProviderEnabled(provider)) {
				final Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
				if (isBetterLocation(result, lastKnownLocation)) {
					result = lastKnownLocation;
				}
			}
		}
		return result;
	}

	//////////////////////////////////////////////////////////////////////////

	/**
	 * 位置情報の情報鮮度が有効とする時間 (ミリ秒) を保持します。
	 */
	private static final long DEFAULT_SIGNIFICANTLY_NEWER = 2 * 60 * 1000L;

	/**
	 * 指定された新しい位置情報が現在の位置情報より有効かどうかを返します。
	 *
	 * @param currentLocation 現在の位置情報
	 * @param newLocation 新しい位置情報
	 * @return 新しい位置情報が有効かどうか
	 * @see <a href="http://developer.android.com/guide/topics/location/obtaining-user-location.html#BestEstimate">Android Dev Guide - Obtaining User Location - Maintaining a current best estimate</a>
	 */
	public static boolean isBetterLocation(final Location currentLocation, final Location newLocation) {
		if (newLocation == null) {
			// 新しい位置情報が null の場合は常に無効と判断します。
			return false;
		}
		if (currentLocation == null) {
			return true;
			// 現在の位置情報が null の場合は、新しい位置情報が現時刻から5分以内のものであれば有効とします。
			//return (new Date().getTime() - newLocation.getTime()) <= FIVE_MINUTES;
		}

		// Check whether the new location fix is newer or older
		final long timeDelta = newLocation.getTime() - currentLocation.getTime();
		if (timeDelta > DEFAULT_SIGNIFICANTLY_NEWER) {
			return true;
		} else if (timeDelta < DEFAULT_SIGNIFICANTLY_NEWER) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
/*
		if (!newLocation.hasAccuracy()) {
			return false;
		}
		if (!currentLocation.hasAccuracy()) {
			return true;
		}
*/

		final int accuracyDelta = (int) (newLocation.getAccuracy() - currentLocation.getAccuracy());
		if (accuracyDelta < 0) {
			return true;
		} else if (timeDelta > 0L && accuracyDelta <= 0L) {
			return true;
		} else if (timeDelta > 0L && accuracyDelta <= 200L && isSameProvider(newLocation.getProvider(), currentLocation.getProvider())) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	public static boolean isSameProvider(final String provider1, final String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}