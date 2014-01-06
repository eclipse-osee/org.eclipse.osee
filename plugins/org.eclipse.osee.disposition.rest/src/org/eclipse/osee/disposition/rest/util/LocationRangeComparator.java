/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

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