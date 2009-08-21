/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.test.util;

import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Ryan Schmitt
 */
public class HumanReadableIdTest extends TestCase {

   @org.junit.Test
   public void testInvalidHrids() {
      final String[] invalidHrids = {"", // short
            "QRZH", // short
            "QRZHMT", // long
            "QRZHm", // small caps
            "QRZH_", // invalid char
            "CAAAX", // A in middle
            "CEEEX", // E in middle
            "CIIIX", // I in middle
            "COOOX", // O in middle
            "CUUUX", // U in middle
            "IZZZZ", // starts with I 
            "OZZZZ", // starts with O 
            "ZZZZI", // ends with I
            "ZZZZO" // ends with O
      };

      for (String invalid : invalidHrids) {
         assertFalse("Invalid HRID " + invalid + " passes validity test", HumanReadableId.isValid(invalid));
      }
   }

   @org.junit.Test
   public void testValidGeneration() {
      for (int i = 0; i < 500000; i++) {
         String hrid = HumanReadableId.generate();
         assertTrue("Generated HRID " + hrid + " fails validity test", HumanReadableId.isValid(hrid));
      }
   }

   @org.junit.Test
   public void testValidHrids() {
      final String[] validHrids =
            {"F8Z5J", "6V3PH", "UCMXG", "GDWVT", "GJ0Y0", "WV1FV", "E1HT8", "JP6VK", "S36PK", "B7WBP", "H2ML7",
                  "9ZD0A", "1J037", "X30J9", "02T23", "MMV3A", "YRNT0", "ZKBY2", "LYC1M", "RW3N9", "JTRCU", "MCVGX",
                  "KTJ5P", "FBNC4", "57M55", "WY1VG", "UX49X", "E7YF2", "7BWL4", "QQ138"};
      for (String hrid : validHrids) {
         assertTrue(HumanReadableId.isValid(hrid));
      }
   }
}
