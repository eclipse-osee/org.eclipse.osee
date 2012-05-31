/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.relation;

import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.GraphWriteable;
import org.eclipse.osee.orcs.data.RelationsWriteable;

public class GraphWriteableProxy extends GraphReadableProxy implements GraphWriteable {

   private boolean isCopyRequired;
   private RelationGraphImpl original;

   public GraphWriteableProxy(RelationGraphImpl proxied) {
      super(proxied);
   }

   public RelationGraphImpl getOriginal() {
      return original;
   }

   @Override
   public void setProxiedObject(RelationGraphImpl object) {
      isCopyRequired = true;
      this.original = object;
      super.setProxiedObject(object);
   }

   private synchronized RelationGraphImpl getObjectForWrite() throws OseeCoreException {
      if (isCopyRequired) {
         try {
            RelationGraphImpl copy = getOriginal().clone();
            super.setProxiedObject(copy);
            isCopyRequired = false;
         } catch (CloneNotSupportedException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return getProxiedObject();
   }

   @Override
   public void createRelation(ArtifactReadable aArtifact, IRelationTypeSide relationTypeSide, ArtifactReadable otherArtifact) throws OseeCoreException {
      getObjectForWrite().createRelation(aArtifact, relationTypeSide, otherArtifact);
   }

   @Override
   public void createRelation(ArtifactReadable aArtifact, IRelationSorterId sorterId, IRelationTypeSide relationTypeSide, ArtifactReadable otherArtifact) throws OseeCoreException {
      getObjectForWrite().createRelation(aArtifact, sorterId, relationTypeSide, otherArtifact);
   }

   @Override
   public void deleteRelation(ArtifactReadable aArtifact, IRelationType relationTypeSide, ArtifactReadable otherArtifact) throws OseeCoreException {
      getObjectForWrite().deleteRelation(aArtifact, relationTypeSide, otherArtifact);
   }

   @Override
   public void deleteRelations(ArtifactReadable aArtifact, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      getObjectForWrite().deleteRelations(aArtifact, relationTypeSide);
   }

   @Override
   public RelationsWriteable getWriteableChildren(ArtifactReadable artifact) throws OseeCoreException {
      return getObjectForWrite().getWriteableChildren(artifact);
   }

   @Override
   public RelationsWriteable getWriteableRelatedArtifacts(IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return getObjectForWrite().getWriteableRelatedArtifacts(relationTypeSide);
   }

   @Override
   public ArtifactWriteable getWriteableParent(ArtifactReadable artifact) throws OseeCoreException {
      return getObjectForWrite().getWriteableParent(artifact);
   }
}
