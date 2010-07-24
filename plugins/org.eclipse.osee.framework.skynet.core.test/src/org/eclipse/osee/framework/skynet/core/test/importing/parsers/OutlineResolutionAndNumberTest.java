/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.importing.parsers;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.importing.parsers.OutlineResolution;
import org.junit.Test;

/**
 * @link OutlineResolution
 * @author Karol M. Wilk
 */
public final class OutlineResolutionAndNumberTest extends TestCase {

	private static final String LAST_OUTLINE_NUMBER = "3.1.1.2.1.7"; /* realistic outline number */
	private final OutlineResolution outlineResolution = new OutlineResolution();

	/**
	 * ! Asserting true in general means GUI should be called for user to make decision; false - opposite
	 */

	@Test
	public void testLowerOutlineNumbers() {
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.1.1.2.1.6", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.0", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.1.1.1", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.1.13.1", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.1.0", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.9", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.10", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.1", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("1.8", LAST_OUTLINE_NUMBER));
	}

	@Test
	public void testHigherOutlineNumbers() {
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("3.1.1.2.1.8", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.2.1.3", LAST_OUTLINE_NUMBER));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("4.0", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("4.2", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("4.2.5.10", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.1.1.4.1.1", LAST_OUTLINE_NUMBER));
	}

	@Test
	public void testRandomOutlineNumbers() {
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("6.5", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("0.25", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("10.48", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("-5.6", LAST_OUTLINE_NUMBER));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("34.fin3bbtb34fm34", LAST_OUTLINE_NUMBER));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("", ""));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber(null, null));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("200.12.3", LAST_OUTLINE_NUMBER));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("4.0", "3.1"));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("2.1.1.1.2", "2.1.1.1.1"));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("2.1.1.1.1.1", "2.1.1.1.1"));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.1.1.1.1.2", "2.1.1.1.1"));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("3.0", "2.1.1.1.1"));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("2.2", "2.1.1.1.1"));
		Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("2.1.2", "2.1.1.1.1"));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.2", "2.1.1.1.1"));
		Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("b", "c"));
	}

}
