package me.codz;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2017-3-12
 * <p>Time: 10:24
 * <p>Version: 1.0
 */
public class FeatureTest {

	@Test
	public void testIsNumber() {
		Assert.assertTrue(NumberUtils.isNumber("1"));
		Assert.assertTrue(NumberUtils.isNumber("1.2"));
		Assert.assertFalse(NumberUtils.isNumber("a"));
	}
}
