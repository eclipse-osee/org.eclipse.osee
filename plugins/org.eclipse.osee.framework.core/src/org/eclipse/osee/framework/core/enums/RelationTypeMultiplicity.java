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
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public enum RelationTypeMultiplicity {
   ONE_TO_ONE(0, 1, 1),

   ONE_TO_MANY(1, 1, Integer.MAX_VALUE),

   MANY_TO_ONE(2, Integer.MAX_VALUE, 1),

   MANY_TO_MANY(3, Integer.MAX_VALUE, Integer.MAX_VALUE);

   private final int value;
   private final int alimit;
   private final int blimit;

   RelationTypeMultiplicity(int value, int alimit, int blimit) {
      this.value = value;
      this.alimit = alimit;
      this.blimit = blimit;
   }

   public int getSideALimit() {
      return alimit;
   }

   public int getSideBLimit() {
      return blimit;
   }

   private String limitToString(int limit) {
      return limit == Integer.MAX_VALUE ? "n" : "1";
   }

   public String getSideALimitLabel() {
      return limitToString(getSideALimit());
   }

   public String getSideBLimitLabel() {
      return limitToString(getSideBLimit());
   }

   public int getValue() {
      return value;
   }

   public String asLimitLabel(RelationSide side) {
      String toReturn;
      switch (side) {
         case SIDE_A:
            toReturn = getSideALimitLabel();
            break;
         case SIDE_B:
            toReturn = getSideBLimitLabel();
            break;
         default:
            throw new OseeArgumentException("Expecting SIDE_A or SIDE_B");
      }
      return toReturn;
   }

   public int getLimit(RelationSide side) {
      int limit = -1;
      switch (side) {
         case SIDE_A:
            limit = getSideALimit();
            break;
         case SIDE_B:
            limit = getSideBLimit();
            break;
         default:
            throw new OseeArgumentException("Expecting SIDE_A or SIDE_B");
      }
      return limit;
   }

   public boolean isWithinLimit(RelationSide side, int nextCount) {
      int limit = getLimit(side);
      boolean result;
      if (limit == Integer.MAX_VALUE) {
         result = true;
      } else {
         result = nextCount <= limit;
      }
      return result;
   }

   public static RelationTypeMultiplicity getFromString(String value) {
      RelationTypeMultiplicity toReturn = null;
      if (Strings.isValid(value)) {
         String toMatch = value.toLowerCase();
         for (RelationTypeMultiplicity type : values()) {
            if (type.toString().toLowerCase().equals(toMatch)) {
               toReturn = type;
               break;
            }
         }
      }
      return toReturn;
   }

   public static RelationTypeMultiplicity getRelationMultiplicity(int value) {
      RelationTypeMultiplicity toReturn = null;
      for (RelationTypeMultiplicity type : values()) {
         if (type.getValue() == value) {
            toReturn = type;
            break;
         }
      }
      return toReturn;
   }

}
