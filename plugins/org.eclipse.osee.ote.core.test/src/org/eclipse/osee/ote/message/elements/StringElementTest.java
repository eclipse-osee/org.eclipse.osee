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
package org.eclipse.osee.ote.message.elements;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class StringElementTest {
   @Test
   public void testZeroize() {
      for (int i = 0; i < 10; i++) {
         final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[128], i, 128));
         StringElement e1 = new StringElement(null, "test string1", hd, 0, 0, 8 * 10 - 1);
         StringElement e2 = new StringElement(null, "test string2", hd, 10, 0, 8 * 10 - 1);
         StringElement e3 = new StringElement(null, "test string3", hd, 20, 0, 8 * 10 - 1);

         String s1 = "aaaa bb  c";
         String s2 = "zeroizing2";
         String s3 = "1234567890";

         e1.setValue(s1);
         e2.setValue(s2);
         e3.setValue(s3);

         check(e1, s1);
         check(e2, s2);
         check(e3, s3);

         e2.zeroize();
         checkEmpty(e2);

         check(e1, s1);
         check(e2, "");
         check(e3, s3);

         e2.setValue(s2);
         e1.zeroize();
         checkEmpty(e1);

         check(e1, "");
         check(e2, s2);
         check(e3, s3);

         e1.setValue(s1);
         e3.zeroize();

         check(e1, s1);
         check(e2, s2);
         check(e3, "");
      }
   }

   @Test
   public void testStringsTooBig() {
      for (int i = 0; i < 10; i++) {
         final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[128], i, 128));

         String s1 = "aaaa bb  c";
         String s2 = "zeroizing2";
         String s3 = "1234567890";
         for (int j = 1; j < 10; j++) {
            StringElement e1 = new StringElement(null, "test string1", hd, 0, 0, 8 * j - 1);
            StringElement e2 = new StringElement(null, "test string2", hd, 10, 0, 8 * j - 1);
            StringElement e3 = new StringElement(null, "test string3", hd, 20, 0, 8 * j - 1);

            e1.setValue(s1);
            e2.setValue(s2);
            e3.setValue(s3);

            check(e1, s1.substring(0, j));
            check(e2, s2.substring(0, j));
            check(e3, s3.substring(0, j));

            e3.setValue(s3);
            e2.setValue(s2);
            e1.setValue(s1);

            check(e1, s1.substring(0, j));
            check(e2, s2.substring(0, j));
            check(e3, s3.substring(0, j));
         }
      }
   }

   @Test
   public void testStringsTooSmall() {
      for (int i = 0; i < 10; i++) {
         final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[128], i, 128));
         StringElement e1 = new StringElement(null, "test string1", hd, 0, 0, 8 * 10 - 1);
         StringElement e2 = new StringElement(null, "test string2", hd, 10, 0, 8 * 10 - 1);
         StringElement e3 = new StringElement(null, "test string3", hd, 20, 0, 8 * 10 - 1);
         String ss1 = "aaaa bb  c";
         String ss2 = "zeroizing2";
         String ss3 = "1234567890";
         for (int j = 1; j <= 10; j++) {
            String s1 = ss1.substring(0, j);
            String s2 = ss2.substring(0, j);
            String s3 = ss3.substring(0, j);
            e1.setValue(s1);
            e2.setValue(s2);
            e3.setValue(s3);

            check(e1, s1);
            check(e2, s2);
            check(e3, s3);

            e3.setValue(s3);
            e2.setValue(s2);
            e1.setValue(s1);

            check(e1, s1);
            check(e2, s2);
            check(e3, s3);
         }
      }
   }

   @Test
   public void testSetChars() {
      final char[] testData = {'A', 'B', '\0', 'C', 'D'};
      final MessageData hd = new HeaderData("test_data", new MemoryResource(new byte[5], 0, 5));
      final StringElement sut = new StringElement(null, "test string element", hd, 0, 0, 8 * 5 - 1);
      assertEquals("Empty string to start", "", sut.getValue());
      sut.setChars(testData);
      assertEquals("New value is cut off by the null", "AB", sut.getValue());
      char[] result = new char[5];
      assertEquals("Five bytes in, five bytes back", 5, sut.getChars(result));
      assertEquals("Exact same array comes back with getChars()", Arrays.toString(testData), Arrays.toString(result));
   }

   @Test
   public void testSetCharsOverflow() {
      final char[] testData = {'A', 'B', '\0', 'C', 'D', 'E'};
      // buffer is big enough for test data
      final MessageData hd = new HeaderData("test_data", new MemoryResource(new byte[6], 0, 6));
      // element is not big enough for test data
      final StringElement sut = new StringElement(null, "test string element", hd, 0, 0, 8 * 5 - 1);
      assertEquals("Empty string to start", "", sut.getValue());
      sut.setChars(testData);
      assertEquals("New value is cut off by the null", "AB", sut.getValue());
      char[] result = new char[5];
      assertEquals("Five bytes in, five bytes back", 5, sut.getChars(result));
      final char[] expected = {'A', 'B', '\0', 'C', 'D'};
      assertEquals("Exact same array comes back with getChars()", Arrays.toString(expected), Arrays.toString(result));
   }

   @Test
   public void testSetCharsUnderflow() {
      final char[] testData = {'A', 'B', '\0', 'C'};
      // buffer is big enough for test data
      final MessageData hd = new HeaderData("test_data", new MemoryResource(new byte[5], 0, 5));
      // element is not big enough for test data
      final StringElement sut = new StringElement(null, "test string element", hd, 0, 0, 8 * 5 - 1);
      assertEquals("Empty string to start", "", sut.getValue());
      sut.setChars(testData);
      assertEquals("New value is cut off by the null", "AB", sut.getValue());
      char[] result = new char[5];
      assertEquals("Five bytes in, five bytes back", 5, sut.getChars(result));
      final char[] expected = {'A', 'B', '\0', 'C', '\0'};
      assertEquals("Exact same array comes back with getChars()", Arrays.toString(expected), Arrays.toString(result));
   }

   private void check(StringElement elem, String value) {
      if (!elem.getValue().equals(value)) {
         Assert.assertEquals(elem.getName(), value, elem.getValue());
      }
   }

   private void checkEmpty(StringElement elem) {
      if (!elem.isEmpty()) {
         Assert.assertEquals(elem.getName(), "rmpty", "not empty");
      }
   }
}
