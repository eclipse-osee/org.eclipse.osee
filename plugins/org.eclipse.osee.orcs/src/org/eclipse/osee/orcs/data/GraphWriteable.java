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
package org.eclipse.osee.orcs.data;

import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface GraphWriteable extends GraphReadable {

   // Relations
   ArtifactWriteable getWriteableParent(ArtifactReadable otherArtifact) throws OseeCoreException;

   RelationsWriteable getWriteableChildren(ArtifactReadable otherArtifact) throws OseeCoreException;

   RelationsWriteable getWriteableRelatedArtifacts(IRelationTypeSide relationTypeSide) throws OseeCoreException;

   void createRelation(ArtifactReadable aArtifact, IRelationTypeSide relationTypeSide, ArtifactReadable otherArtifact) throws OseeCoreException;

   void createRelation(ArtifactReadable aArtifact, IRelationSorterId sorterId, IRelationTypeSide relationTypeSide, ArtifactReadable otherArtifact) throws OseeCoreException;

   void deleteRelation(ArtifactReadable aArtifact, IRelationType relationTypeSide, ArtifactReadable otherArtifact) throws OseeCoreException;

   void deleteRelations(ArtifactReadable aArtifact, IRelationTypeSide relationTypeSide) throws OseeCoreException;

   //   setRelations(IRelationSorterId, IRelationTypeSide, Collection<? extends Artifact>)
   //   setRelations(IRelationTypeSide, Collection<? extends Artifact>)
   //   setRelationsOfTypeUseCurrentOrder(IRelationTypeSide, Collection<? extends Artifact>, Class<?>)
   //
   /////////////////////////////////////////////////////
}
