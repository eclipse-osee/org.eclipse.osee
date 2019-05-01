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
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeSide extends NamedIdBase implements RelationTypeToken {

   private final RelationTypeToken type;
   private final RelationSide side;
   private RelationTypeSide opposite;

   public RelationTypeSide(RelationTypeToken type, RelationSide side) {
      super(type.getId(), type.getName());
      this.type = type;
      this.side = side;
   }

   public static RelationTypeSide create(RelationSide side, long id, String name) {
      return new RelationTypeSide(RelationTypeToken.create(id, name), side);
   }

   public static RelationTypeSide create(RelationTypeToken type, RelationSide side) {
      return new RelationTypeSide(type, side);
   }

   public synchronized RelationTypeSide getOpposite() {
      if (opposite == null) {
         opposite = new RelationTypeSide(type, side.oppositeSide());
      }
      return opposite;
   }

   public RelationTypeToken getRelationType() {
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
      return "RelationTypeSide [type=" + type.getName() + ", side=" + side + "]";
   }

   public Long getGuid() {
      return getId();
   }
}