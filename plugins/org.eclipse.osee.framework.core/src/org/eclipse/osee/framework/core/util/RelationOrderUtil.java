/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.core.util;

import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;

/**
 * @author Audrey Denk
 */
public class RelationOrderUtil {

   private static final long SPACING = (long) Math.pow(2.0, 18.0);

   public static int getRelOrder(RelationTypeToken relType, String insertType, int afterIndex, int beforeIndex, int minOrder, int maxOrder) {
      int relOrder = 0;
      RelationTypeMultiplicity mult = relType.getMultiplicity();
      if (mult.equals(RelationTypeMultiplicity.MANY_TO_MANY) || mult.equals(RelationTypeMultiplicity.ONE_TO_MANY)) {

         if (insertType.equals("start")) {
            relOrder = calculateHeadInsertionOrderIndex(minOrder);
         } else if (insertType.equals("insert")) {
            relOrder = calculateInsertionOrderIndex(afterIndex, beforeIndex);
         } else {
            relOrder = calculateEndInsertionOrderIndex(maxOrder);
         }
      }
      return relOrder;
   }

   static int calculateHeadInsertionOrderIndex(int currentHeadIndex) {
      long idealIndex = currentHeadIndex - SPACING;
      if (idealIndex > Integer.MIN_VALUE) {
         return (int) idealIndex;
      }
      return calculateInsertionOrderIndex(Integer.MIN_VALUE, currentHeadIndex);
   }

   static int calculateEndInsertionOrderIndex(int currentEndIndex) {
      long idealIndex = currentEndIndex + SPACING;
      if (idealIndex < Integer.MAX_VALUE) {
         return (int) idealIndex;
      }
      return calculateInsertionOrderIndex(currentEndIndex, Integer.MAX_VALUE);
   }

   static int calculateInsertionOrderIndex(int afterIndex, int beforeIndex) {
      return (int) ((long) (afterIndex) + beforeIndex) / 2;
   }
}
