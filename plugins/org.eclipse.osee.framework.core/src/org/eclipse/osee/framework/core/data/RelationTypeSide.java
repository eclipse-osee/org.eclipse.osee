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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeSide extends NamedId implements IRelationType {

   private final IRelationType type;
   private final RelationSide side;
   private RelationTypeSide opposite;

   public RelationTypeSide(IRelationType type, RelationSide side) {
      super(type.getId(), type.getName());
      this.type = type;
      this.side = side;
   }

   public static RelationTypeSide create(RelationSide side, long id, String name) {
      return new RelationTypeSide(IRelationType.valueOf(id, name), side);
   }

   public static RelationTypeSide create(IRelationType type, RelationSide side) {
      return new RelationTypeSide(type, side);
   }

   public synchronized RelationTypeSide getOpposite() {
      if (opposite == null) {
         opposite = new RelationTypeSide(type, side.oppositeSide());
      }
      return opposite;
   }

   public IRelationType getRelationType() {
      return type;
   }

   public boolean isOfType(IRelationType type) {
      return this.type.equals(type);
   }

   public RelationSide getSide() {
      return side;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationTypeSide) {
         RelationTypeSide rel = (RelationTypeSide) obj;
         return type.equals(rel.type) && side.equals(rel.side);
      }
      return super.equals(obj);
   }

   @Override
   public String toString() {
      return "RelationTypeSide [type=" + type + ", side=" + side + "]";
   }

   public Long getGuid() {
      return getId();
   }
}