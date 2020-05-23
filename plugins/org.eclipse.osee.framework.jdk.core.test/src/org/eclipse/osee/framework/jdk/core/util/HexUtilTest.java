/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link HexUtil}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class HexUtilTest {

   private final String expectedString;
   private final long expectedLong;

   public HexUtilTest(String expectedString, long expectedLong) {
      super();
      this.expectedString = expectedString;
      this.expectedLong = expectedLong;
   }

   @Test
   public void testToLong() {
      long actualLong = HexUtil.toLong(expectedString);
      Assert.assertEquals(expectedLong, actualLong);
   }

   @Test
   public void testToString() {
      String actualString = HexUtil.toString(expectedLong);
      Assert.assertEquals(expectedString, actualString);
   }

   @Parameters
   public static List<Object[]> data() {
      return Arrays.asList(new Object[][] {
         {"0x1000000000000057", 1152921504606847063L},
         {"0x2000000000000167", 2305843009213694311L},
         {"0x3123449801273557", 3540749151688406359L},
         {"0x00000001EDFBC123", 8287666467L}});
   }
}
