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
package org.eclipse.osee.framework.skynet.core.relation;

/**
 * @author Ryan D. Brooks
 */
public enum RelationSide {
   SIDE_A, SIDE_B, OPPOSITE;

   private static RelationSide[] sides = new RelationSide[] {SIDE_A, SIDE_B};

   public RelationSide oppositeSide() {
      if (this == OPPOSITE) {
         return OPPOSITE;
      } else {
         return this == SIDE_A ? SIDE_B : SIDE_A;
      }
   }

   public boolean isSideA() {
      return this == RelationSide.SIDE_A;
   }

   /**
    * @return RelationSide
    */
   public static RelationSide[] getSides() {
      return sides;
   }
}
