/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.util.AbstractRelationResolverServiceImpl;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsRelationResolverServiceImpl extends AbstractRelationResolverServiceImpl {
   private final AtsApi atsApi;

   public AtsRelationResolverServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public Collection<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeSide relationType) {
      List<ArtifactToken> results = new ArrayList<>();
      for (ArtifactReadable art : getArtifact(artifact).getRelated(relationType)) {
         if (art != null) {
            results.add(art);
         }
      }
      return results;
   }

   @Override
   public <T extends IAtsObject> Collection<T> getRelated(IAtsObject atsObject, RelationTypeSide relationType, Class<T> clazz) {
      return getRelated(atsObject, relationType, DeletionFlag.EXCLUDE_DELETED, clazz);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends IAtsObject> Collection<T> getRelated(IAtsObject atsObject, RelationTypeSide relationType, DeletionFlag flag, Class<T> clazz) {
      List<T> results = new ArrayList<>();
      if (atsObject.isInvalid()) {
         return results;
      }
      ArtifactReadable useArt = getArtifact(atsObject);
      if (useArt != null) {
         for (ArtifactReadable art : useArt.getRelated(relationType, flag)) {
            IAtsObject object = AtsObjects.getAtsObject(art, atsApi);
            if (object != null) {
               results.add((T) object);
            }
         }
      }
      return results;
   }

   @Override
   public boolean areRelated(ArtifactId artifact1, RelationTypeSide relationType, ArtifactId artifact2) {
      boolean related = false;
      if (artifact1 instanceof ArtifactReadable && artifact2 instanceof ArtifactReadable) {
         related = getArtifact(artifact1).areRelated(relationType, getArtifact(artifact2));
      }
      return related;
   }

   @Override
   public ArtifactToken getRelatedOrNull(ArtifactId artifact, RelationTypeSide relationType) {
      ArtifactToken related = null;
      try {
         related = getArtifact(artifact).getRelated(relationType).getAtMostOneOrNull();
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return related;
   }

   @Override
   public ArtifactToken getRelatedOrSentinel(ArtifactId artifact, RelationTypeSide relationType) {
      ArtifactToken related = ArtifactReadable.SENTINEL;
      try {
         related = getArtifact(artifact).getRelated(relationType).getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return related;
   }

   @Override
   public boolean areRelated(IAtsObject atsObject1, RelationTypeSide relationType, IAtsObject atsObject2) {
      boolean related = false;
      ArtifactReadable useArt1 = getArtifact(atsObject1);
      ArtifactReadable useArt2 = getArtifact(atsObject2);
      if (useArt1 != null && useArt2 != null) {
         related = useArt1.areRelated(relationType, useArt2);
      }
      return related;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getRelatedOrNull(IAtsObject atsObject, RelationTypeSide relationType, Class<T> clazz) {
      T related = null;
      ArtifactReadable art = getArtifact(atsObject);
      if (art != null) {
         try {
            ArtifactReadable artifact = art.getRelated(relationType).getOneOrDefault(ArtifactReadable.SENTINEL);
            if (!artifact.getId().equals(ArtifactReadable.SENTINEL.getId())) {
               IAtsObject object = AtsObjects.getAtsObject(artifact, atsApi);
               if (object != null) {
                  related = (T) object;
               }
            }
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
      }
      return related;
   }

   @Override
   public ArtifactReadable getArtifact(Object object) {
      ArtifactReadable useArt = null;
      if (object instanceof ArtifactReadable) {
         useArt = (ArtifactReadable) object;
      } else if (object instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) object;
         if (atsObject.getStoreObject() instanceof ArtifactReadable) {
            useArt = (ArtifactReadable) atsObject.getStoreObject();
         } else {
            useArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(atsObject.getId());
         }
      } else if (object instanceof ArtifactId) {
         useArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(((ArtifactId) object).getId());
      }
      return useArt;
   }

   @Override
   public int getRelatedCount(IAtsWorkItem workItem, RelationTypeSide relationType) {
      ArtifactReadable artifact = getArtifact(workItem);
      int count = 0;
      if (artifact != null) {
         count = artifact.getRelatedCount(relationType);
      }
      return count;
   }

   @Override
   public ArtifactToken getRelatedOrNull(IAtsObject atsObject, RelationTypeSide relationSide) {
      ArtifactReadable art = getArtifact(atsObject);
      if (art != null) {
         return art.getRelated(relationSide).getAtMostOneOrNull();
      }
      return null;
   }

   @Override
   public ArtifactToken getRelatedOrSentinel(IAtsObject atsObject, RelationTypeSide relationSide) {
      ArtifactReadable art = getArtifact(atsObject);
      if (art != null) {
         return art.getRelated(relationSide).getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      }
      return ArtifactReadable.SENTINEL;
   }

   @Override
   public Collection<ArtifactToken> getRelatedArtifacts(IAtsWorkItem workItem, RelationTypeSide relationTypeSide) {
      ArtifactReadable artifact = getArtifact(workItem);
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getRelated(artifact, relationTypeSide));
   }

   @Override
   public Collection<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeSide relationType, IArtifactType artifactType) {
      List<ArtifactToken> results = new LinkedList<>();
      ArtifactReadable art = getArtifact(artifact);
      for (ArtifactToken related : art.getRelated(relationType)) {
         if (((ArtifactReadable) related).isOfType(artifactType)) {
            results.add(related);
         }
      }
      return results;
   }

   @Override
   public Collection<ArtifactToken> getRelatedArtifacts(ArtifactId artifact, RelationTypeSide relationTypeSide) {
      return getRelated(artifact, relationTypeSide);
   }

   @Override
   public int getRelatedCount(ArtifactToken artifact, RelationTypeSide relationTypeSide) {
      ArtifactReadable art = getArtifact(artifact);
      return art.getRelatedCount(relationTypeSide);
   }

   @Override
   public Collection<Long> getRelatedIds(ArtifactId artifact, RelationTypeSide relationTypeSide) {
      List<Long> related = new LinkedList<>();
      ArtifactReadable art = getArtifact(artifact);
      for (ArtifactReadable rel : art.getRelated(relationTypeSide)) {
         related.add(rel.getId());
      }
      return related;
   }
}
