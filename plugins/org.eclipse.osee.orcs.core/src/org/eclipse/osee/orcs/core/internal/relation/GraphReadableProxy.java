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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.orcs.core.internal.AbstractProxy;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.data.RelationsReadable;

public class GraphReadableProxy extends AbstractProxy<RelationGraphImpl> implements GraphReadable {

   public GraphReadableProxy(RelationGraphImpl proxied) {
      super(proxied);
   }

   private GraphReadable getObjectForRead() {
      return getProxiedObject();
   }

   @Override
   public Collection<IRelationTypeSide> getExistingRelationTypes(ArtifactReadable art) {
      return getObjectForRead().getExistingRelationTypes(art);
   }

   @Override
   public List<RelationType> getValidRelationTypes(ArtifactReadable art) throws OseeCoreException {
      return getObjectForRead().getValidRelationTypes(art);
   }

   @Override
   public ArtifactReadable getParent(ArtifactReadable art) throws OseeCoreException {
      return getObjectForRead().getParent(art);
   }

   @Override
   public RelationType getFullRelationType(IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return getObjectForRead().getFullRelationType(relationTypeSide);
   }

   @Override
   public RelationsReadable getRelatedArtifacts(IRelationTypeSide relationTypeSide, ArtifactReadable art) throws OseeCoreException {
      return getObjectForRead().getRelatedArtifacts(relationTypeSide, art);
   }

   @Override
   public RelationsReadable getChildren(ArtifactReadable art) throws OseeCoreException {
      return getObjectForRead().getChildren(art);
   }

   @Override
   public int getRelationSideMax(RelationType relationType, IArtifactType artifactType, RelationSide relationSide) throws OseeCoreException {
      return getObjectForRead().getRelationSideMax(relationType, artifactType, relationSide);
   }

}
