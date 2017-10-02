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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public enum RelationSide {
   SIDE_A,
   SIDE_B;

   public RelationSide oppositeSide() {
      return this == SIDE_A ? SIDE_B : SIDE_A;
   }

   public boolean isSideA() {
      return this == RelationSide.SIDE_A;
   }

   public boolean isOppositeSide(RelationSide side) {
      return this != side;
   }

   public static RelationSide fromString(String name)  {
      if (!Strings.isValid(name)) {
         throw new OseeArgumentException("Name cannot be null or empty");
      }
      String toMatch = name.toUpperCase();
      for (RelationSide side : RelationSide.values()) {
         if (side.name().equals(toMatch)) {
            return side;
         }
      }
      throw new OseeCoreException("Invalid name - Relation Side was not found");
   }
}
