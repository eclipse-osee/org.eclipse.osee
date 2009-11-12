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

import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.junit.Assert;

/**
 * @author Ryan Schmitt
 */
public class HumanReadableIdTest {

   @org.junit.Test
   public void testInvalidHrids() {
      final String[] invalidHrids = {"", // short
            "QRZH", // short
            "QRZHMT", // long
            "QRZHm", // small caps
            "QRZH_", // invalid char
            "CACCX", "CCCAX", "CCACX", // A in middle 
            "CECCX", "CCECX", "CCCEX", // E in middle 
            "CCCIX", "CICCX", "CCICX", // I in middle
            "TOCCX", "CCOCC", "CCCOC", // O in middle
            "CTUTX", "CUTTX", "CTTUX", // U in middle
            "IZZZZ", // starts with I 
            "OZZZZ", // starts with O 
            "ZZZZI", // ends with I
            "ZZZZO" // ends with O
      };

      for (String invalid : invalidHrids) {
         Assert.assertFalse("Invalid HRID " + invalid + " passes validity test", HumanReadableId.isValid(invalid));
      }
   }

   @org.junit.Test
   public void testValidGeneration() {
      for (int i = 0; i < 500000; i++) {
         String hrid = HumanReadableId.generate();
         Assert.assertTrue("Generated HRID " + hrid + " fails validity test", HumanReadableId.isValid(hrid));
      }
   }

   @org.junit.Test
   public void testValidHrids() {
      final String[] validHrids =
            {"F8Z5J", "6V3PH", "UCMXG", "GDWVT", "GJ0Y0", "WV1FV", "E1HT8", "JP6VK", "S36PK", "B7WBP", "H2ML7",
                  "9ZD0A", "1J037", "X30J9", "02T23", "MMV3A", "YRNT0", "ZKBY2", "LYC1M", "RW3N9", "JTRCU", "MCVGX",
                  "KTJ5P", "FBNC4", "57M55", "WY1VG", "UX49X", "E7YF2", "7BWL4", "QQ138", "8Y2GH", "4VYMJ", "5VQTG",
                  "VXT3S", "4WRRX", "41PNL", "VK7FC", "YXHRR", "NFJFJ", "1KDMX", "DK8HZ", "Q946P", "6MM16", "YCTCL",
                  "6J3JG", "ZF29S", "W6RVV", "NQRZ9", "CKD87", "9WCHJ", "SBM27", "09XY9", "2P1S4", "3N0M2", "WR173",
                  "HY1JD", "UH473", "BSQGA", "RH3PS", "DBD0P", "P68WZ", "KGQ25", "YB4WT", "7G8ZC", "44NK5", "3LZ3H",
                  "BDHQJ", "NTZNS", "18WZR", "ZFVV2", "UHXMN", "XMBKG", "9VDXB", "82LVV", "0SVHQ", "MGF89", "JDGYQ",
                  "YKN2W", "9TXV0", "NP04F", "3CLWW", "5KP30", "2TS48", "NM9RH", "DC664", "Q95LE", "A8WP8", "PF7TA",
                  "6TLL3", "G7075", "A4SM5", "Q6H4V", "PDH1R", "BDZ89", "5PVSS", "MGDYZ", "MTH2P", "DB7X5", "K90YY",
                  "7NL4Z", "M4XZR", "2YL1L", "M12SJ", "7SRVE", "4FZ5A", "L9G8J", "AZ556", "23VCK", "SRXVM", "YGBLG",
                  "31WJ4", "0PW2Y", "NTZQA", "X8N3R", "LM74P", "5SMQ9", "H5SQY", "M6H8U", "9F0XB", "Y3NP7", "L98FJ",
                  "S9MHW", "D3WG3", "KXPFY", "H0CYR", "VJYXP", "XTV4R", "BBLQY", "EG57N", "SD2YL", "W8HHM", "H76XQ",
                  "WRMYV", "WKR27", "Y9VJ9", "PZMDJ", "GJ9JS", "QXR3W", "J0MY4", "EPLW6", "4QLXX", "SF781", "NM80A",
                  "ZM37Z", "K8X27", "XGWWC", "RCP1B", "56GN6", "B74LE", "JY3LE", "5XSJK", "8NH89", "F2340", "PKCWT",
                  "1N2VU", "XH7KP", "BV9FC", "8SKWB", "Y9VGS", "BDW01", "YT4FZ", "H7RKJ", "D7F9P", "4NVPW", "BV4BL",
                  "E60S6", "VDVRK", "RMW41", "713CG", "9XXXV", "Z75DZ", "EDP9D", "72WRU", "TZWPG", "X5MZE", "TRKHW",
                  "40HPE", "VDZ8C", "VYN04", "U2060", "C4CPB", "35W70", "XCGMJ", "KS51K", "EXY3R", "D1YRL", "LQP89",
                  "L8KTH", "X11WU", "GDL4S", "RZP8G", "C9WXJ", "J5RSF", "6H4S4", "S1442", "3VV89", "MPKSG", "LLHKX",
                  "CMQGV", "L0J5Q", "JJ72D", "QLVJW", "CM5WB", "TGQ2B", "BZJMV", "X1R89", "PMQ8K", "7CFJV", "NY4MM",
                  "9HT3R", "Q1Q92", "9L098", "YJ5FU", "0L53S", "403K2", "FS009", "DMD3U", "KZ7Y6", "CGYPF", "YB0NF",
                  "LCSQ4", "K3V89", "WK0GP", "ZDY3K", "L3KR4", "26GDN", "NK0LA", "2WVG3", "4H08D", "CY08E", "S9D2S",
                  "T38JK", "UB7L7", "Q264P", "BQGZU", "R0V8A"

            };
      for (String hrid : validHrids) {
         Assert.assertTrue(HumanReadableId.isValid(hrid));
      }
   }
}

/*
 * Mutating org.eclipse.osee.framework.jdk.core.util.HumanReadableId
 * Tests: org.eclipse.osee.framework.jdk.core.util.HumanReadableIdTest
 * Mutation points = 160, unit test time limit 12.0s
 * M FAIL: org.eclipse.osee.framework.jdk.core.util.HumanReadableId:40: CP[12] 34438396 -> 34438397
 * ..M FAIL: org.eclipse.osee.framework.jdk.core.util.HumanReadableId:27: 2 -> 3
 * .M FAIL: org.eclipse.osee.framework.jdk.core.util.HumanReadableId:28: 34 (") -> 35 (#)
 * ..................................................
 * ...................M FAIL: org.eclipse.osee.framework.jdk.core.util.HumanReadableId:30: 31 -> 32 ( )
 * ..................................................
 * ..................................
 * Score: 97%
 */