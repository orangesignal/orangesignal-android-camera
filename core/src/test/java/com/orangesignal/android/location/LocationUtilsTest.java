/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.location;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

//import static org.hamcrest.core.IsNot.not;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLocationManager;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

@RunWith(RobolectricTestRunner.class)
public final class LocationUtilsTest {

	private Location mLastKnownLocation;

	@Before
	public void setUp() {
		// Robolectric の ShadowLocationManager を使用して、検証用の LocationManager を構成します。
		final LocationManager locationManager = (LocationManager) Robolectric.application.getSystemService(Context.LOCATION_SERVICE);

		// LocationManager の各位置情報プロバイダを有効化します。
		final ShadowLocationManager shadowLocationManager = Robolectric.shadowOf(locationManager);
		shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
		shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

		// GPS プロバイダの LastKnownLocation を設定します。
		mLastKnownLocation = new Location(LocationManager.GPS_PROVIDER);
		// 東京駅
		mLastKnownLocation.setLatitude(35.681382D);
		mLastKnownLocation.setLongitude(139.766084D);
		mLastKnownLocation.setAccuracy(200);
		mLastKnownLocation.setTime(System.currentTimeMillis());
		shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, mLastKnownLocation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLastKnownLocationContextIllegalArgumentException() {
		final Context context = null;
		LocationUtils.getLastKnownLocation(context);
	}

	@Test
	public void testGetLastKnownLocationContext() {
		final Location result = LocationUtils.getLastKnownLocation(Robolectric.application);
		assertNotNull(result);
		assertSame(result, mLastKnownLocation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLastKnownLocationContextStringArrayIllegalArgumentException() {
		final Context context = null;
		LocationUtils.getLastKnownLocation(context, LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER);
	}

	@Test
	public void testGetLastKnownLocationContextStringArray() {
		final Location result1 = LocationUtils.getLastKnownLocation(Robolectric.application, LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER);
		assertNotNull(result1);
		assertSame(result1, mLastKnownLocation);

		final Location result2 = LocationUtils.getLastKnownLocation(Robolectric.application, LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER);
		assertNotNull(result2);
		assertSame(result2, mLastKnownLocation);

		final Location result3 = LocationUtils.getLastKnownLocation(Robolectric.application, LocationManager.GPS_PROVIDER);
		assertNotNull(result3);
		assertSame(result3, mLastKnownLocation);

		final Location result4 = LocationUtils.getLastKnownLocation(Robolectric.application, LocationManager.NETWORK_PROVIDER);
		assertNull(result4);
	}

	@Test
	public void testGetLastKnownLocationLocationManagerStringArray() {
		final LocationManager locationManager = (LocationManager) Robolectric.application.getSystemService(Context.LOCATION_SERVICE);
		final LocationManager nullLocationManager = null;
		final String[] nullProviders = null;

		assertNull(LocationUtils.getLastKnownLocation(nullLocationManager, nullProviders));
		assertNull(LocationUtils.getLastKnownLocation(nullLocationManager, LocationManager.GPS_PROVIDER));
		assertNull(LocationUtils.getLastKnownLocation(nullLocationManager, LocationManager.NETWORK_PROVIDER));
		assertNull(LocationUtils.getLastKnownLocation(nullLocationManager, LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER));
		assertNull(LocationUtils.getLastKnownLocation(locationManager, nullProviders));

		assertNotNull(LocationUtils.getLastKnownLocation(locationManager, LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER));
	}

	@Test
	public void testIsBetterLocation() {
		// 新現在地が null の場合
		assertFalse(LocationUtils.isBetterLocation(new Location(LocationManager.GPS_PROVIDER), null));
		// 旧現在地が null の場合
		assertTrue(LocationUtils.isBetterLocation(null, new Location(LocationManager.GPS_PROVIDER)));

		final long now = System.currentTimeMillis();
		assertTrue(now != 0);

		final Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
		currentLocation.setLatitude(35.681382D);
		currentLocation.setLongitude(139.766084D);
		currentLocation.setAccuracy(200);
		currentLocation.setTime(now);

		Location newLocation;

		// 旧現在地と新現在地の時刻情報が離れすぎている場合
		newLocation = new Location(currentLocation);
		newLocation.setTime(now + 2 * 60 * 1000L);
		assertTrue(LocationUtils.isBetterLocation(currentLocation, newLocation));

		// 旧現在地と新現在地の時刻情報が離れすぎていない場合
		newLocation = new Location(currentLocation);
		newLocation.setTime(now + 1 * 60 * 1000L);
		assertFalse(LocationUtils.isBetterLocation(currentLocation, newLocation));

//		newLocation = new Location(currentLocation);
//		newLocation.setAccuracy(100);
//		assertTrue(LocationUtils.isBetterLocation(currentLocation, newLocation));
	}

	@Test
	public void testIsSameProvider() {
		assertTrue(LocationUtils.isSameProvider(null, null));
		assertTrue(LocationUtils.isSameProvider(LocationManager.GPS_PROVIDER, LocationManager.GPS_PROVIDER));
		assertTrue(LocationUtils.isSameProvider(LocationManager.NETWORK_PROVIDER, LocationManager.NETWORK_PROVIDER));
		assertFalse(LocationUtils.isSameProvider(null, LocationManager.NETWORK_PROVIDER));
		assertFalse(LocationUtils.isSameProvider(LocationManager.GPS_PROVIDER, null));
		assertFalse(LocationUtils.isSameProvider(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER));
	}

}
