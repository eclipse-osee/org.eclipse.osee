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

package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.type.RelationType;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeSide extends NamedIdentity implements IRelationEnumeration {

   private final RelationType type;
   private final RelationSide side;

   public RelationTypeSide(RelationType type, RelationSide side) {
      super(type.getGuid(), type.getName());
      this.type = type;
      this.side = side;
   }

   public RelationType getRelationType() {
      return type;
   }

   @Override
   public RelationSide getSide() {
      return side;
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof RelationTypeSide) {
         RelationTypeSide arg = (RelationTypeSide) arg0;
         return type.equals(arg.type) && side.equals(arg.side);
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hashCode = 11;
      hashCode = hashCode * 31 + type.hashCode();
      hashCode = hashCode * 31 + side.hashCode();
      return hashCode;
   }

   @Override
   public String toString() {
      return "RelationTypeSide [type=" + type + ", side=" + side + "]";
   }
}