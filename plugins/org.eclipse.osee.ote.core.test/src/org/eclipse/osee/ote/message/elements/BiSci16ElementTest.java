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

import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Michael P. Masterson
 */
public class BiSci16ElementTest {
   
   private BiSci16Element uut;

   @Before
   public void setup() {
      final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[64], 2, 64));
      uut = new BiSci16Element(null, "derp", hd, 0, 0, 15);
   }

   @Test
   public void testLongToBiSci() {
      
      long logicalValue ;
      long biSciValue;
      
      logicalValue = 1;
      biSciValue = 0x10;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = 0;
      biSciValue = 0x0;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = -1;
      biSciValue = 0xFFF0;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = 946176;
      biSciValue = 0xE73;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = 25624576;
      biSciValue = 0x1874;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = -25624576;
      biSciValue = 0xE794;
      assertLongToBiSci(logicalValue, biSciValue);

      long sixteenCubed = (long) Math.pow(16, 3);
      logicalValue = (long) Math.pow(16, 5);
      biSciValue = 0x1003;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = (long) (Math.pow(16, 5) + 1);
      biSciValue = 0x1003;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = (long) (Math.pow(16, 5) - 1);
      biSciValue = 0x1003;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = (long) (Math.pow(16, 5) + (sixteenCubed/2) - 1);
      biSciValue = 0x1003;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = (long) (Math.pow(16, 5) + (sixteenCubed/2));
      biSciValue = 0x1013;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = (long) (Math.pow(16, 5) - sixteenCubed/2 - 1);
      biSciValue = 0x0FF3;
      assertLongToBiSci(logicalValue, biSciValue);
      
      logicalValue = (long) (Math.pow(16, 5) - sixteenCubed/2);
      biSciValue = 0x1003;
      assertLongToBiSci(logicalValue, biSciValue);
      
   }

   private void assertLongToBiSci(long input, long expected) {
      long actual = uut.convertLogicalValueToBiSci(input);
      Assert.assertEquals(expected, actual);
   }
   
   @Test
   public void testBiSciToLong() {
      long biSciValue;
      long logicalValue;
      
      logicalValue = 1;
      biSciValue = 0x10;
      assertBiSciToLong(biSciValue, logicalValue);
      
      logicalValue = 0;
      biSciValue = 0x0;
      assertBiSciToLong(biSciValue, logicalValue);
      
      logicalValue = -1;
      biSciValue = 0xFFF0;
      assertBiSciToLong(biSciValue, logicalValue);

      logicalValue = 946176;
      biSciValue = 0xE73;
      assertBiSciToLong(biSciValue, logicalValue);
      
      logicalValue = 25624576;
      biSciValue = 0x1874;
      assertBiSciToLong(biSciValue, logicalValue);
      
      logicalValue = -25624576;
      biSciValue = ((short)0xE794);
      assertBiSciToLong(biSciValue, logicalValue);
      
   }

   /**
    * @param bisciValue
    * @param logicalValue
    */
   private void assertBiSciToLong(long input, long expected) {
      long actual = uut.convertBiSciToLogicalValue(input);
      Assert.assertEquals(expected, actual);
      
   }
   
   @Test
   public void testSetValue() {
      checkSet(25624576);
      checkSet(-25624576);
      checkSet(1);
      checkSet(0);
      checkSet(-1);
      
      checkTiming(0x7FF << (4 * 15));
   }

   private void checkTiming(int input) {
      long timeBefore, timeAfter;
      for( int i = 0 ; i < 10000 ; i++) {
         timeBefore = System.nanoTime();
         checkSet(input);
         timeAfter = System.nanoTime();
         long runningTime = timeAfter - timeBefore;
         Assert.assertTrue(runningTime < 500000);

      }
      
   }

   private void checkSet(long input) {
      uut.setValue(input);
      Assert.assertEquals(input, (long)uut.getValue());
   }
}
