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

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.ReqNumbering;

public final class OutlineResolution {

   public final boolean isInvalidOutlineNumber(String currentOutlineNumber, String lastOutlineNumber) {
      if (Strings.isValid(currentOutlineNumber, lastOutlineNumber)) {

         ReqNumbering current = new ReqNumbering(currentOutlineNumber, false);
         ReqNumbering last = new ReqNumbering(lastOutlineNumber, false);

         switch (last.compareTo(current)) {
            case 1:
            case -1:
               boolean check = !generateNextSet(last).contains(current.getNumberString());
               return check;
            case 0:
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   /**
    * @param lastNumberParagrah i.e. new ReqNumbering("4.0");
    * @return set of combinations i.e ["4.1, 5.0"]
    */
   public Collection<String> generateNextSet(ReqNumbering lastNumberParagrah) {
      String last = lastNumberParagrah.getNumberString();
      Collection<String> nextParagraphs = new HashSet<String>();

      for (int i = last.length() - 1; i >= 0; i--) {

         if (last.charAt(i) != '.') {
            int currentInt = extractDigitsSafely(last.subSequence(i, i + 1).toString());

            if (i == last.length() - 1) {
               nextParagraphs.add(String.format("%s%s", last, ".0.1"));
               nextParagraphs.add(String.format("%s%s", last, ".1"));
            }

            nextParagraphs.add(String.format("%s%s", last.subSequence(0, i),
               (i != 0) ? currentInt + 1 : String.format("%s.0", currentInt + 1)));
         }
      }

      return nextParagraphs;
   }

   private int extractDigitsSafely(String stringContainingDigit) {
      int returnValue = 0;
      try {
         returnValue = Integer.parseInt(stringContainingDigit);
      } catch (NumberFormatException ex) {
         returnValue = 0;
      }
      return returnValue;
   }
}
