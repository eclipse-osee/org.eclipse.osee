/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.List;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.access.context.IParentProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Donald G. Dunne
 */
public interface IRelationResolver extends IParentProvider {

   Collection<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeSide relationType);

   Collection<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeSide relationType,
      ArtifactTypeToken artifactType);

   Collection<ArtifactToken> getRelated(IAtsObject atsObject, RelationTypeSide relationType,
      ArtifactTypeToken artifactType);

   <T extends IAtsObject> Collection<T> getRelated(IAtsObject atsObject, RelationTypeSide relationType, Class<T> clazz);

   <T extends IAtsObject> Collection<T> getRelated(IAtsObject atsObject, RelationTypeSide relationType,
      DeletionFlag flag, Class<T> clazz);

   boolean areRelated(ArtifactId artifact1, RelationTypeSide relationType, ArtifactId artifact2);

   boolean areRelated(IAtsObject atsObject1, RelationTypeSide relationType, IAtsObject atsObject2);

   boolean areNotRelated(ArtifactId artifact1, RelationTypeSide relationType, ArtifactId artifact2);

   boolean areNotRelated(IAtsObject atsObject1, RelationTypeSide relationType, IAtsObject atsObject2);

   ArtifactToken getRelatedOrNull(ArtifactId artifact, RelationTypeSide relationType);

   ArtifactToken getRelatedOrSentinel(ArtifactId artifact, RelationTypeSide relationType);

   ArtifactToken getRelatedOrNull(IAtsObject atsObject, RelationTypeSide relationType);

   ArtifactToken getRelatedOrSentinel(IAtsObject atsObject, RelationTypeSide relationSide);

   @Nullable
   <T> T getRelatedOrNull(IAtsObject atsObject, RelationTypeSide relationType, Class<T> clazz);

   int getRelatedCount(IAtsWorkItem workItem, RelationTypeSide relationType);

   Collection<ArtifactToken> getRelatedArtifacts(IAtsWorkItem workItem, RelationTypeSide relationTypeSide);

   Collection<ArtifactToken> getRelated(IAtsObject atsObject, RelationTypeSide relationTypeSide);

   Collection<ArtifactToken> getRelatedArtifacts(ArtifactId artifact, RelationTypeSide relationTypeSide);

   Collection<ArtifactToken> getChildren(ArtifactId artifact, ArtifactTypeToken artifactType);

   Collection<ArtifactToken> getChildren(ArtifactId artifact);

   int getRelatedCount(ArtifactToken artifact, RelationTypeSide relationTypeSide);

   List<ArtifactId> getRelatedIds(ArtifactId artifact, RelationTypeSide relationTypeSide);

   default ArtifactToken getChildNamedOrNull(IAtsObject atsObject, String name) {
      for (ArtifactToken child : getChildren(atsObject.getStoreObject())) {
         if (child.getName().equals(name)) {
            return child;
         }
      }
      return null;
   }

   Collection<ArtifactToken> getAncestors(ArtifactToken artifact);

   Collection<IRelationLink> getRelations(ArtifactId artifact, RelationTypeSide relationTypeSide);

}
