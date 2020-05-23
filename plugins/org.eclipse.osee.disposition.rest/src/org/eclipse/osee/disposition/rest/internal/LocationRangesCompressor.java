/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.disposition.rest.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.disposition.model.LocationRange;

/**
 * @author Angel Avila
 */
public class LocationRangesCompressor {

   public static String compress(List<Integer> locationPoints) {
      Collections.sort(locationPoints);
      StringBuilder workingLocRefs = new StringBuilder();

      boolean isRange = false;
      boolean endOfRange = false;
      int startOfRange = -1;
      int previous = -1;

      Iterator<Integer> iterator = locationPoints.iterator();

      while (iterator.hasNext()) {
         int currentTestPoint = iterator.next();
         boolean isLastElement = !iterator.hasNext();

         // Starting a Range
         if (currentTestPoint != 0 && previous == currentTestPoint - 1 && startOfRange == -1) { // if the previous is 1 less than our current we are in a range
            isRange = true;
            endOfRange = false;
            startOfRange = previous;
            int lastIndexOf = workingLocRefs.lastIndexOf(",");
            if (lastIndexOf > 0) {
               workingLocRefs.replace(lastIndexOf, workingLocRefs.length(), "");
            } else {
               workingLocRefs.setLength(0);
            }
         }
         if (isRange && (previous != currentTestPoint - 1 || isLastElement)) { // End Range
            endOfRange = true;
         }

         StringBuilder toAppend = new StringBuilder();

         if (!isRange) { // If we are not in a range just add the single point
            toAppend.append(currentTestPoint);
         } else if (endOfRange) { // other wise check to see if we ended the range
            if (previous != currentTestPoint - 1) {
               toAppend.append(new LocationRange(startOfRange, previous).toString()); // append the range ending with the previous point and append this current point
               toAppend.append(", ");
               toAppend.append(currentTestPoint);
            } else {
               toAppend.append(new LocationRange(startOfRange, currentTestPoint).toString()); // append the range ending with the previous point and append this current point
            }

            isRange = false;
            startOfRange = -1;
         }

         if (toAppend.length() != 0) {
            if (workingLocRefs.length() > 0) {
               workingLocRefs.append(", ");
            }
            workingLocRefs.append(toAppend);
         }

         previous = currentTestPoint;
      }
      return workingLocRefs.toString();
   }

}
