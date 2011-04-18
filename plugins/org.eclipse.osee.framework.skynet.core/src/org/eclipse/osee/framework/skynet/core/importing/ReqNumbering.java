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
package org.eclipse.osee.framework.skynet.core.importing;

/**
 * @author Robert A. Fisher
 */
public class ReqNumbering implements Comparable<ReqNumbering> {
   private static final int ZERO_BASED_NUMBERING = 2;
   private static final int ONE_BASED_NUMBERING = 1;

   private final String number;
   private final String[] values;

   /**
    * Requirement Numbering
    *
    * @note When a number with a separator - is used, i.e. 1.2-1. All - are replaced with . at construction.
    * @param number
    */
   public ReqNumbering(String number) {
      //When additional separators are used (- instead of .)
      this.number = number.replace("-", ".");
      values = tokenize();
   }

   public String getNumberString() {
      return number;
   }

   /**
    * @return returns whether the numbering argument is a child of this number
    */
   public boolean isChild(ReqNumbering numbering) {
      String[] numberVals = numbering.values;

      switch (numberVals.length - values.length) {
         case ZERO_BASED_NUMBERING:
            if (!numberVals[numberVals.length - 2].equals("0")) {
               return false;
            }
            break;
         case ONE_BASED_NUMBERING:
            break;
         default:
            return false;
      }

      for (int i = 0; i < values.length; i++) {
         if (!values[i].equals(numberVals[i])) {
            return false;
         }
      }
      return true;
   }

   public String[] tokenize() {
      String[] returnVal = number.split("\\.");

      // If the very last token is a 0, then chop it off
      if (returnVal[returnVal.length - 1].equals("0")) {
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
      return number.hashCode();
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
      return number;
   }
}