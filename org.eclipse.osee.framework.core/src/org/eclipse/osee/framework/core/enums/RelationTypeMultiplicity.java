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

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public enum RelationTypeMultiplicity {
   ONE_TO_ONE(0),

   ONE_TO_MANY(1),

   MANY_TO_ONE(2),

   MANY_TO_MANY(3);

   private final int value;

   RelationTypeMultiplicity(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
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
