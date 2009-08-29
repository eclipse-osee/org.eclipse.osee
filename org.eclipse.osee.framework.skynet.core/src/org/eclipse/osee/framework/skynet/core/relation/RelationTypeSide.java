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

import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeSide implements IRelationEnumeration {

   private final RelationType type;
   private final RelationSide side;
   private final Artifact artifact;

   public RelationTypeSide(RelationType type, RelationSide side, Artifact artifact) {
      this.type = type;
      this.side = side;
      this.artifact = artifact;
   }

   public RelationTypeSide(RelationType type, RelationSide side) {
      this(type, side, null);
   }

   public RelationTypeSide(String typeName, String sideName) throws OseeCoreException {
      this.type = RelationTypeManager.getType(typeName);
      this.side = type.isSideAName(sideName) ? RelationSide.SIDE_A : RelationSide.SIDE_B;
      this.artifact = null;
   }

   @Override
   public RelationType getRelationType() {
      return type;
   }

   @Override
   public RelationSide getSide() {
      return side;
   }

   @Override
   public String getSideName() {
      return type.getSideName(side);
   }

   @Override
   public String getTypeName() {
      return type.getTypeName();
   }

   @Override
   public boolean isSideA() {
      return side == RelationSide.SIDE_A;
   }

   @Override
   public boolean isThisType(RelationLink link) {
      return link.getRelationType() == type;
   }

   /**
    * @return the artifact
    */
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof RelationTypeSide) {
         RelationTypeSide arg = (RelationTypeSide) arg0;
         if (artifact == null && arg.artifact == null) {

         } else if (artifact == null) {
            return false;
         } else if (arg.artifact == null) {
            return false;
         }
         return type.equals(arg.type) && side.equals(arg.side) && artifact.equals(arg.artifact);
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hashCode = 11;
      hashCode = hashCode * 31 + type.hashCode();
      hashCode = hashCode * 31 + side.hashCode();
      if (artifact != null) {
         hashCode = hashCode * 31 + artifact.hashCode();
      }
      return hashCode;
   }
}
