/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MathUtilsTest {

	@Test
	public void testIsPowerOfTwo() {
		assertFalse(MathUtils.isPowerOfTwo(0));
		assertTrue(MathUtils.isPowerOfTwo(1));
		assertTrue(MathUtils.isPowerOfTwo(2));
		assertFalse(MathUtils.isPowerOfTwo(3));
		assertTrue(MathUtils.isPowerOfTwo(4));
		assertFalse(MathUtils.isPowerOfTwo(5));

		assertFalse(MathUtils.isPowerOfTwo(7));
		assertTrue(MathUtils.isPowerOfTwo(8));
		assertFalse(MathUtils.isPowerOfTwo(9));

		assertFalse(MathUtils.isPowerOfTwo(15));
		assertTrue(MathUtils.isPowerOfTwo(16));
		assertFalse(MathUtils.isPowerOfTwo(17));

		assertFalse(MathUtils.isPowerOfTwo(31));
		assertTrue(MathUtils.isPowerOfTwo(32));
		assertFalse(MathUtils.isPowerOfTwo(33));

		assertFalse(MathUtils.isPowerOfTwo(63));
		assertTrue(MathUtils.isPowerOfTwo(64));
		assertFalse(MathUtils.isPowerOfTwo(65));

		assertFalse(MathUtils.isPowerOfTwo(127));
		assertTrue(MathUtils.isPowerOfTwo(128));
		assertFalse(MathUtils.isPowerOfTwo(129));

		assertFalse(MathUtils.isPowerOfTwo(255));
		assertTrue(MathUtils.isPowerOfTwo(256));
		assertFalse(MathUtils.isPowerOfTwo(257));

		assertFalse(MathUtils.isPowerOfTwo(511));
		assertTrue(MathUtils.isPowerOfTwo(512));
		assertFalse(MathUtils.isPowerOfTwo(513));

		assertFalse(MathUtils.isPowerOfTwo(1023));
		assertTrue(MathUtils.isPowerOfTwo(1024));
		assertFalse(MathUtils.isPowerOfTwo(1025));
	}

	@Test
	public void testNextPowerOfTwo() {
		assertEquals(MathUtils.nextPowerOfTwo(0), 0);
		assertEquals(MathUtils.nextPowerOfTwo(1), 1);
		assertEquals(MathUtils.nextPowerOfTwo(2), 2);
		assertEquals(MathUtils.nextPowerOfTwo(3), 4);
		assertEquals(MathUtils.nextPowerOfTwo(4), 4);
		assertEquals(MathUtils.nextPowerOfTwo(5), 8);

		assertEquals(MathUtils.nextPowerOfTwo(7), 8);
		assertEquals(MathUtils.nextPowerOfTwo(8), 8);
		assertEquals(MathUtils.nextPowerOfTwo(9), 16);

		assertEquals(MathUtils.nextPowerOfTwo(15), 16);
		assertEquals(MathUtils.nextPowerOfTwo(16), 16);
		assertEquals(MathUtils.nextPowerOfTwo(17), 32);

		assertEquals(MathUtils.nextPowerOfTwo(31), 32);
		assertEquals(MathUtils.nextPowerOfTwo(32), 32);
		assertEquals(MathUtils.nextPowerOfTwo(33), 64);

		assertEquals(MathUtils.nextPowerOfTwo(63), 64);
		assertEquals(MathUtils.nextPowerOfTwo(64), 64);
		assertEquals(MathUtils.nextPowerOfTwo(65), 128);

		assertEquals(MathUtils.nextPowerOfTwo(127), 128);
		assertEquals(MathUtils.nextPowerOfTwo(128), 128);
		assertEquals(MathUtils.nextPowerOfTwo(129), 256);

		assertEquals(MathUtils.nextPowerOfTwo(255), 256);
		assertEquals(MathUtils.nextPowerOfTwo(256), 256);
		assertEquals(MathUtils.nextPowerOfTwo(257), 512);

		assertEquals(MathUtils.nextPowerOfTwo(511), 512);
		assertEquals(MathUtils.nextPowerOfTwo(512), 512);
		assertEquals(MathUtils.nextPowerOfTwo(513), 1024);

		assertEquals(MathUtils.nextPowerOfTwo(1023), 1024);
		assertEquals(MathUtils.nextPowerOfTwo(1024), 1024);
		assertEquals(MathUtils.nextPowerOfTwo(1025), 2048);
	}

}
