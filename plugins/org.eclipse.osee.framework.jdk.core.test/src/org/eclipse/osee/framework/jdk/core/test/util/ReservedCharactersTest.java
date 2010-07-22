/*******************************************************************************
 * Copyright (c) 2004, 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.test.util;

import org.eclipse.osee.framework.jdk.core.util.ReservedCharacters;
import org.junit.Assert;
import org.junit.Test;

/**
 * @formatter:off
 * $link:{ReservedCharacters} Tests for static class ReservedCharacters.
 * 
 * @author Karol M. Wilk
 */
//formatter:on
public class ReservedCharactersTest {

	private static String ampCase = "Apples & Oranges";
	private static String ampAnswer = "Apples &amp; Oranges";
	private static String lessThanGreaterThanCase = "Apples > Oranges, sometimes nice Apples < Oranges.";
	private static String lessThanGreaterThanAnswer = "Apples &gt; Oranges, sometimes nice Apples &lt; Oranges.";
	private static String apostropheCase = "French orange is 'orange'";
	private static String apostropheAnswer = "French orange is &apos;orange&apos;";
	private static String quoteCase = "Some fruit are not really \"fruit\" per say";
	private static String quoteAnswer = "Some fruit are not really &quot;fruit&quot; per say";
	
	@Test
	public final void testTextCharsToXmlChars() {
		Assert.assertTrue(ampAnswer.equals(ReservedCharacters.encodeXmlEntities(ampCase)));
		Assert.assertTrue(lessThanGreaterThanAnswer.equals(ReservedCharacters.encodeXmlEntities(lessThanGreaterThanCase)));
		Assert.assertTrue(apostropheAnswer.equals(ReservedCharacters.encodeXmlEntities(apostropheCase)));
		Assert.assertTrue(quoteAnswer.equals(ReservedCharacters.encodeXmlEntities(quoteCase)));
	}
}
