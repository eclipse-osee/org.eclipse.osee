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

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.ReservedCharacters;
import org.junit.Assert;
import org.junit.Test;

/**
 * $link:{ReservedCharacters} Tests for static class ReservedCharacters.
 * 
 * @author Karol M. Wilk
 */
public class ReservedCharactersTest {

   private static final int RESERVED_CHAR_COUNT = 105;
   private static final int XML_ENTITIES_COUNT = 5;

   private static final String ampCase = "Apples & Oranges";
   private static final String ampAnswer = "Apples &amp; Oranges";

   private static final String lessThanGreaterThanCase = "Apples > Oranges, sometimes nice Apples < Oranges.";
   private static final String lessThanGreaterThanAnswer = "Apples &gt; Oranges, sometimes nice Apples &lt; Oranges.";

   private static final String apostropheCase = "French orange is 'orange'";
   private static final String apostropheAnswer = "French orange is &apos;orange&apos;";

   private static final String quoteCase = "Some fruit are not really \"fruit\" per say";
   private static final String quoteAnswer = "Some fruit are not really &quot;fruit&quot; per say";

   @Test
   public final void testTextCharsToXmlChars() {
      Assert.assertEquals(ampAnswer, ReservedCharacters.encodeXmlEntities(ampCase));
      Assert.assertEquals(lessThanGreaterThanAnswer, ReservedCharacters.encodeXmlEntities(lessThanGreaterThanCase));
      Assert.assertEquals(apostropheAnswer, ReservedCharacters.encodeXmlEntities(apostropheCase));
      Assert.assertEquals(quoteAnswer, ReservedCharacters.encodeXmlEntities(quoteCase));
   }

   @Test
   public void testToCharater() {
      String data = "&acute;";
      char expected = 180;
      char actual = ReservedCharacters.toCharacter(data);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testGetChars() {
      int expectedTotal = RESERVED_CHAR_COUNT + XML_ENTITIES_COUNT;
      Collection<Character> characters = ReservedCharacters.getChars();
      Assert.assertEquals(expectedTotal, characters.size());
   }
}
