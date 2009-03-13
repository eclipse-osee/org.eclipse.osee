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

import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeSide implements IRelationEnumeration {

   private RelationType type;
   private RelationSide side;
   private Artifact artifact;

   public RelationTypeSide(RelationType type, RelationSide side, Artifact artifact) {
      this.type = type;
      this.side = side;
      this.artifact = artifact;
   }

   public RelationTypeSide(String typeName, String sideName, Artifact artifact) throws OseeTypeDoesNotExist, OseeDataStoreException, OseeArgumentException {
      this.type = RelationTypeManager.getType(typeName);
      this.side = type.isSideAName(sideName) ? RelationSide.SIDE_A : RelationSide.SIDE_B;
      this.artifact = artifact;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getRelationType()
    */
   @Override
   public RelationType getRelationType() {
      return type;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getSide()
    */
   @Override
   public RelationSide getSide() {
      return side;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getSideName(org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   @Override
   public String getSideName() {
      return type.getSideName(side);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getTypeName()
    */
   @Override
   public String getTypeName() {
      return type.getTypeName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#isSideA()
    */
   @Override
   public boolean isSideA() {
      return side == RelationSide.SIDE_A;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#isThisType(org.eclipse.osee.framework.skynet.core.relation.RelationLink)
    */
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

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
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

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
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
