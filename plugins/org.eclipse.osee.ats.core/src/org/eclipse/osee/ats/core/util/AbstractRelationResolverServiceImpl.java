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

import java.util.Collection;
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

}