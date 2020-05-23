/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.rest.util;

import java.util.StringTokenizer;
import org.eclipse.osee.disposition.model.LocationRange;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Angel Avila
 */

public final class LocationRangeUtil {

   public static boolean isValid(int start, int end) {
      boolean result = false;
      if (start > 0 && end > 0) {
         if (start <= end) {
            result = true;
         }
      }
      return result;
   }

   public static void checkValid(int start, int end) {
      if (start > end) {
         throw new OseeArgumentException("End Index must be equal to or greater than the Start Index");
      }
   }

   public static LocationRange newLocationRange(int startIndex, int endIndex) {
      checkValid(startIndex, endIndex);
      return new LocationRange(startIndex, endIndex);
   }

   public static LocationRange parseLocation(String locationRef) {
      locationRef = locationRef.trim();
      int startFromString;
      int endFromString;

      if (locationRef.matches("\\d+-\\d+")) {
         StringTokenizer tokenizer = new StringTokenizer(locationRef, "-");
         startFromString = Integer.valueOf(tokenizer.nextToken());
         endFromString = Integer.valueOf(tokenizer.nextToken());
      } else if (locationRef.matches("-\\d+--\\d+")) {
         StringTokenizer tokenizer = new StringTokenizer(locationRef, "-");
         startFromString = -1 * Integer.valueOf(tokenizer.nextToken());
         endFromString = -1 * Integer.valueOf(tokenizer.nextToken());
      } else {
         startFromString = Integer.valueOf(locationRef);
         endFromString = Integer.valueOf(locationRef);
      }
      return newLocationRange(startFromString, endFromString);
   }

   public static boolean isLocRefWithinRange(LocationRange range, LocationRange locRefRange) {
      return range.getStart() <= locRefRange.getStart() && range.getEnd() >= locRefRange.getEnd();
   }

   public static boolean isCovered(LocationRange range, Iterable<? extends LocationRange> locations) {
      boolean isFullyCovered = false;

      int firstUncoveredIndex = range.getStart();
      int end = range.getEnd();

      for (LocationRange item : locations) {
         if (item.getStart() == firstUncoveredIndex) {
            if (item.getEnd() >= end) {
               isFullyCovered = true;
               break;
            } else {
               firstUncoveredIndex = item.getEnd() + 1;
            }

         } else {
            // go to next
         }
      }
      return isFullyCovered;
   }

}
