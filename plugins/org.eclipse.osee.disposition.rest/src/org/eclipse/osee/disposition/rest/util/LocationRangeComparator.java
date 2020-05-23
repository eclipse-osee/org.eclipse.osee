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

import java.util.Comparator;
import org.eclipse.osee.disposition.model.LocationRange;

/**
 * @author Angel Avila
 */

public class LocationRangeComparator implements Comparator<LocationRange> {

   @Override
   public int compare(LocationRange range1, LocationRange range2) {
      final int BEFORE = -1;
      final int EQUAL = 0;
      final int AFTER = 1;

      if (range1.getStart() < range2.getStart()) {
         return BEFORE;
      } else if (range1.getStart() > range2.getStart()) {
         return AFTER;
      } else if (range1.getEnd() < range2.getEnd()) {
         return BEFORE;
      } else if (range1.getEnd() > range2.getEnd()) {
         return AFTER;
      } else {
         return EQUAL;
      }
   }

}