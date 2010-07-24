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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.eclipse.osee.framework.jdk.core.util.Strings;

public final class OutlineResolution {

   private static final String OUTLINE_NUMBER_DELIMITER = "\\.";

   public final boolean isInvalidOutlineNumber(String currentOutlineNumber, String lastOutlineNumber) {
      if (Strings.isValid(currentOutlineNumber) && Strings.isValid(lastOutlineNumber) && currentOutlineNumber.length() >= 1 && lastOutlineNumber.length() >= 1) {

         String[] lastOutlineNumberArray = lastOutlineNumber.split(OUTLINE_NUMBER_DELIMITER);
         String[] currentOutlineNumberArray = currentOutlineNumber.split(OUTLINE_NUMBER_DELIMITER);

         int minLength = currentOutlineNumberArray.length;
         if (minLength > lastOutlineNumberArray.length) {
            minLength = lastOutlineNumberArray.length;
         }

         //should I show GUI to resolve conflict?
         return decideNextPossibleOutlineNumber(currentOutlineNumberArray, lastOutlineNumberArray, minLength) ? false : true;

      } else {
         return false;
      }
   }

   /**
    * Determines if the incoming number follows a logical next based on the previous outline number found.
    */
   private boolean decideNextPossibleOutlineNumber(String[] currentOutlineNumberArray, String[] lastOutlineNumberArray, int minLength) {
      boolean detectedAtLeastOnePositive = false; // set to true moment a delta of 1 is found.
      boolean finalDecisionIfNumberFollowsPattern = false; // decision that gets returned to calling method, whether the paragraph follows the right pattern
      int zeroDeltaCounter = 0; // counts amount of time delta is 0
      for (int nextNumberIndex = 0; nextNumberIndex < minLength; nextNumberIndex++) {
         int currentDigit = extractDigitsSafely(currentOutlineNumberArray[nextNumberIndex]);
         int lastDigit = extractDigitsSafely(lastOutlineNumberArray[nextNumberIndex]);

         int delta = currentDigit - lastDigit;

         if (delta == 1) {
            if (detectedAtLeastOnePositive) {
               detectedAtLeastOnePositive = false;
               break;
            } else {
               detectedAtLeastOnePositive = true;
            }
         } else if (delta == 0) {
            //made to track difference between previous and next is 0
            //i.e. last=2.1.1.1 current=2.1.1.1.1
            zeroDeltaCounter++;
         } else if (delta < 0) {
            //negative delta, therefore number 
            //does not follow pattern
            //invalidate previous findings.
            if (currentDigit == 0) {
               //current if zero will disqualify a valid outline number
               //example: last: 3.1 current: 4.0
               break;
            } else {
               detectedAtLeastOnePositive = false;
               break;
            }
         }

         //check for last=2.1.1.1 current=2.1.1.1.1, indented paragraphs 
         if (nextNumberIndex == minLength - 1 && zeroDeltaCounter == minLength && currentOutlineNumberArray.length > lastOutlineNumberArray.length) {
            if (Strings.isValid(currentOutlineNumberArray[currentOutlineNumberArray.length - 1])) {
               if (currentOutlineNumberArray[currentOutlineNumberArray.length - 1].compareTo("1") == 0) {
                  detectedAtLeastOnePositive = true;
               } else {
                  detectedAtLeastOnePositive = false;
               }
               break;
            }
         }
      }

      if (detectedAtLeastOnePositive) {
         finalDecisionIfNumberFollowsPattern = true;
      }

      return finalDecisionIfNumberFollowsPattern;
   }

   private int extractDigitsSafely(String stringContainingDigit) {
      int returnValue = -1;
      try {
         returnValue = Integer.parseInt(stringContainingDigit);
      } catch (NumberFormatException ex) {
         //apparently what was passed in is not a valid number
         //System.out.println("OutlineResolution.java: Last string had an invalid digit. Ignoring...");
         returnValue = -1;
      }
      return returnValue;
   }
}
