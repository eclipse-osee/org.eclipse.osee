/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.define.api.importing;

/**
 * Requirement Numbering
 *
 * @see ReqNumberingTest
 * @author Robert A. Fisher
 */
public final class ReqNumbering implements Comparable<ReqNumbering> {
   private static final int ZERO_BASED_NUMBERING = 0; // 1.0-1
   private static final int ONE_BASED_NUMBERING = 1; // 1.1

   private final boolean removeEndingZero;
   private final String numberStr;
   private final String[] values;

   //TODO: to implement support for i. ii. iii. or a. b. c.
   //reimplement the comparators and the below.

   public ReqNumbering(String number) {
      this(number, true);
   }

   /**
    * @note When a number with a separator - is used, i.e. 1.2-1. All - are replaced with . at construction.
    * @param number
    */
   public ReqNumbering(String number, boolean removeEndingZero) {
      //When additional separators are used (- instead of .)
      this.numberStr = number.replace("-", ".");

      this.removeEndingZero = removeEndingZero;
      this.values = tokenize(removeEndingZero);
   }

   public String getNumberString() {
      return numberStr;
   }

   public String getParentString() {
      int finalIndex = values.length - 1;
      for (int i = 0; i < values.length; ++i) {
         if (values[i].equals("0")) {
            finalIndex = i;
            break;
         }
      }
      return unTokenize(values, finalIndex);
   }

   public int getLength() {
      return values.length;
   }

   public String getReqNumberByLevel(int level) {
      return unTokenize(values, level);
   }

   private String unTokenize(String[] input, int index) {
      StringBuilder toReturn = new StringBuilder();
      for (int i = 0; i < index; ++i) {
         if (toReturn.length() > 0) {
            toReturn.append(".");
         }
         toReturn.append(input[i]);
      }
      return toReturn.toString();
   }

   /**
    * @return returns whether the numbering argument is a child of this number
    */
   public boolean isChild(ReqNumbering numbering) {
      String[] numberVals = numbering.values;

      int delta = numberVals.length - values.length;

      switch (delta % 2) {
         case ZERO_BASED_NUMBERING:
            if (delta <= 0) {
               return false;
            }
            break;
         case ONE_BASED_NUMBERING:
            break;
         default:
            return false;
      }

      for (int i = 0; i < Math.min(values.length, numberVals.length); i++) {
         if (!values[i].equals(numberVals[i])) {
            return false;
         }
      }

      return true;
   }

   public String[] tokenize() {
      return tokenize(removeEndingZero);
   }

   public String[] tokenize(boolean chopOffZero) {
      String[] returnVal = numberStr.split("\\.");

      if (chopOffZero && returnVal[returnVal.length - 1].equals("0")) {
         // If the very last token is a 0, then chop it off
         String[] temp = new String[returnVal.length - 1];
         System.arraycopy(returnVal, 0, temp, 0, temp.length);
         returnVal = temp;
      }
      return returnVal;
   }

   @Override
   public int compareTo(ReqNumbering o) {
      for (int i = 0; i < Math.max(values.length, o.values.length); i++) {
         int thisValue = getValue(i);
         int oValue = o.getValue(i);
         if (thisValue > oValue) {
            return 1;
         } else if (thisValue < oValue) {
            return -1;
         }
      }

      return 0;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof ReqNumbering)) {
         return false;
      }
      return this.compareTo((ReqNumbering) o) == 0;
   }

   @Override
   public int hashCode() {
      return numberStr.hashCode();
   }

   private int getValue(int index) {
      if (index <= values.length - 1) {
         return Integer.parseInt(values[index]);
      } else {
         return 0;
      }
   }

   @Override
   public String toString() {
      return numberStr;
   }
}