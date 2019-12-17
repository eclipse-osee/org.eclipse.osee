/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractRelationResolverServiceImpl implements IRelationResolver {

   @Override
   public Collection<ArtifactToken> getChildren(ArtifactId artifact, ArtifactTypeToken artifactType) {
      return getRelated(artifact, CoreRelationTypes.DefaultHierarchical_Child, artifactType);
   }

   @Override
   public Collection<ArtifactToken> getRelated(IAtsObject atsObject, RelationTypeSide relationTypeSide) {
      return getRelated(atsObject.getStoreObject(), relationTypeSide);
   }

   public abstract ArtifactId getArtifact(Object object);

   @Override
   public Collection<ArtifactToken> getRelatedArtifacts(IAtsWorkItem workItem, RelationTypeSide relationTypeSide) {
      ArtifactId artifact = getArtifact(workItem);
      return Collections.castAll(getRelated(artifact, relationTypeSide));
   }

   @Override
   public Collection<ArtifactToken> getChildren(ArtifactId artifact) {
      return getRelated(artifact, CoreRelationTypes.DefaultHierarchical_Child);
   }

   @Override
   public ArtifactToken getParent(ArtifactId artifact) {
      return getRelatedOrNull(artifact, CoreRelationTypes.DefaultHierarchical_Parent);
   }

   @Override
   public Collection<ArtifactToken> getAncestors(ArtifactToken artifact) {
      List<ArtifactToken> ancestors = new ArrayList<>();
      ArtifactToken parent = getParent(artifact);
      while (parent != null) {
         ancestors.add(parent);
         parent = getParent(artifact);
      }
      return ancestors;
   }

   @Override
   public boolean areNotRelated(ArtifactId artifact1, RelationTypeSide relationType, ArtifactId artifact2) {
      return !areRelated(artifact1, relationType, artifact2);
   }

   @Override
   public boolean areNotRelated(IAtsObject atsObject1, RelationTypeSide relationType, IAtsObject atsObject2) {
      return !areRelated(atsObject1, relationType, atsObject2);
   }

}