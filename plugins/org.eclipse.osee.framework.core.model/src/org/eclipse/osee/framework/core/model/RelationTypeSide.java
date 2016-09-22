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

import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeSide extends NamedId implements IRelationTypeSide {

   private final IRelationType type;
   private final RelationSide side;
   private RelationTypeSide opposite;

   public RelationTypeSide(IRelationType type, RelationSide side) {
      super(type.getId(), type.getName());
      this.type = type;
      this.side = side;
   }

   @Override
   public synchronized RelationTypeSide getOpposite() {
      if (opposite == null) {
         opposite = new RelationTypeSide(type, side.oppositeSide());
      }
      return opposite;
   }

   public IRelationType getRelationType() {
      return type;
   }

   @Override
   public boolean isOfType(IRelationType type) {
      return this.type.equals(type);
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