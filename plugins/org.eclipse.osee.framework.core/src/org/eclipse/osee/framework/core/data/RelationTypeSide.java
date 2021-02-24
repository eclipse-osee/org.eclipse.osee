/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeSide extends NamedIdBase implements RelationTypeToken {

   public static final RelationTypeSide SENTINEL =
      new RelationTypeSide(RelationTypeToken.SENTINEL, RelationSide.SIDE_A);

   private final RelationTypeToken type;
   private final RelationSide side;
   private RelationTypeSide opposite;

   public RelationTypeSide(RelationTypeToken type, RelationSide side) {
      super(type.getId(), type.getName());
      this.type = type;
      this.side = side;
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

   public boolean isOfType(RelationTypeToken type) {
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

   @Override
   public RelationTypeMultiplicity getMultiplicity() {
      return type.getMultiplicity();
   }

   @Override
   public RelationSorter getOrder() {
      return type.getOrder();
   }

   @Override
   public ArtifactTypeToken getArtifactType(RelationSide relationSide) {
      return type.getArtifactType(relationSide);
   }

   @Override
   public String getSideName(RelationSide relationSide) {
      return type.getSideName(relationSide);
   }

   @Override
   public boolean isArtifactTypeAllowed(RelationSide relationSide, ArtifactTypeToken artifactType) {
      return type.isArtifactTypeAllowed(relationSide, artifactType);
   }

   @Override
   public boolean isOrdered() {
      return type.isOrdered();
   }

}