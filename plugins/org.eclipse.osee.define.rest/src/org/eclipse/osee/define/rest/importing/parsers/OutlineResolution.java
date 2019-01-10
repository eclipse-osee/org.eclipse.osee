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
package org.eclipse.osee.define.rest.importing.parsers;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.define.api.importing.ReqNumbering;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Decides whether a outline number is valid or invalid.
 *
 * @see OutlineResolutionAndNumberTest
 * @author Karol M. Wilk
 */
public final class OutlineResolution {

   /**
    * Converts <code>currentOutlineNumber</code> and <code>lastOutlineNumber</code> to ReqNumbering (without trimming
    * ending 0s) and runs
    * <code>isInvalidOutlineNumber(ReqNumbering currentOutlineNumber, ReqNumbering lastOutlineNumber)</code>
    *
    * @param currentOutlineNumber
    * @param lastOutlineNumber
    * @return
    */
   public boolean isInvalidOutlineNumber(String currentOutlineNumber, String lastOutlineNumber) {

      boolean resolution = Strings.isValid(currentOutlineNumber, lastOutlineNumber);

      if (resolution) {
         ReqNumbering current = new ReqNumbering(currentOutlineNumber, false);
         ReqNumbering last = new ReqNumbering(lastOutlineNumber, false);

         resolution = isInvalidOutlineNumber(current, last);
      }

      return resolution;
   }

   /**
    * Compares <code>currentOutlineNumber</code> and <code>lastOutlineNumber</code> to determine if current is not next
    * in outline numbering sequence. Assumes that <code>last</code> is the last valid outline number. <b>NOTE</b>
    * Accepts larger paragraph numbers
    *
    * @param currentOutlineNumber
    * @param lastOutlineNumber
    * @return
    */
   public boolean isInvalidOutlineNumber(ReqNumbering current, ReqNumbering last) {
      boolean invalid = Conditions.notNull(current, last);
      if (invalid) {
         switch (last.compareTo(current)) {
            case -1: //just test upper bound of last, assuming last is last correct paragraph no
               Set<String> nextUp = generateNextSet(last);
               invalid = !nextUp.contains(current.getNumberString());
               //and if the current is generally larger but has not been generated, due to sequence of from:
               // $current.n.k, st "n -> oo, k -> oo", "n, k in Z", "oo is infinity"
               if (invalid) {
                  invalid = current.getNumberString().length() - last.getNumberString().length() < 4;
                  //TODO: another check could be delta should be at most 1?
               }
               break;
            case 1:
            case 0:
            default:
               invalid = true;
               break;
         }
      }
      return invalid;
   }

   /**
    * @param lastNumberParagrah i.e. new ReqNumbering("4.0");
    * @return set of combinations i.e ["4.1, 5.0, 5., 4.0.1"]
    */
   public Set<String> generateNextSet(ReqNumbering lastNumberParagrah) {

      Set<String> nextParagraphs = new HashSet<>();

      String last = lastNumberParagrah.getNumberString();
      if (last.endsWith(".0")) {
         last = last.substring(0, last.length() - ".0".length());
      }

      last = Strings.truncateEndChar(last, '.');

      if (!last.endsWith(".0.1")) { //special s p
         nextParagraphs.add(last + ".0.1");
         nextParagraphs.add(last + ".1");
      }

      String[] digits = last.split("\\.");
      for (int i = 0; i < digits.length; i++) {
         int incDigit = extractDigitsSafely(digits[i]) + 1;

         if (i == 0) {
            nextParagraphs.add(incDigit + ".0");
            nextParagraphs.add(Integer.toString(incDigit));
         } else {
            StringBuilder nextNew = new StringBuilder(digits.length * 2);
            for (int j = 0; j < i; j++) {
               nextNew.append(digits[j] + ".");
            }
            nextParagraphs.add(nextNew.toString() + Integer.toString(incDigit));
         }

      }

      Set<String> dotEnding = new HashSet<>(nextParagraphs.size());
      for (String next : nextParagraphs) {
         dotEnding.add(next + ".");
      }
      nextParagraphs.addAll(dotEnding);

      return nextParagraphs;
   }

   private int extractDigitsSafely(String stringContainingDigit) {
      int returnValue = 0;
      try {
         returnValue = Integer.parseInt(stringContainingDigit);
      } catch (NumberFormatException ex) {
         //Do nothing
      }
      return returnValue;
   }
}
