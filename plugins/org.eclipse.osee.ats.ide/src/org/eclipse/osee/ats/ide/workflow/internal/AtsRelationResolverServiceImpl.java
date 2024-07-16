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

package org.eclipse.osee.ats.ide.workflow.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.util.AbstractRelationResolverServiceImpl;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsRelationResolverServiceImpl extends AbstractRelationResolverServiceImpl {

   private final AtsApiIde atsApi;

   public AtsRelationResolverServiceImpl(AtsApiIde atsClient) {
      this.atsApi = atsClient;
   }

   @Override
   public Collection<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeSide relationType) {
      List<ArtifactToken> results = new ArrayList<>();
      Artifact useArt = getArtifact(artifact);
      if (useArt != null) {
         for (Artifact art : useArt.getRelatedArtifacts(relationType)) {
            results.add(art);
         }
      }
      return results;
   }

   @Override
   public <T extends IAtsObject> Collection<T> getRelated(IAtsObject atsObject, RelationTypeSide relationType,
      Class<T> clazz) {
      return getRelated(atsObject, relationType, DeletionFlag.EXCLUDE_DELETED, clazz);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends IAtsObject> Collection<T> getRelated(IAtsObject atsObject, RelationTypeSide relationType,
      DeletionFlag flag, Class<T> clazz) {
      List<T> results = new ArrayList<>();
      if (atsObject == null || atsObject.isInvalid()) {
         return results;
      }
      Artifact useArt = getArtifact(atsObject);

      if (useArt != null) {
         for (Artifact art : useArt.getRelatedArtifacts(relationType, flag)) {
            IAtsObject object = AtsObjects.getAtsObject(art, atsApi);
            if (object != null) {
               results.add((T) object);
            }
         }
      }
      return results;
   }

   @Override
   public Artifact getArtifact(Object object) {
      Artifact useArt = null;
      if (object instanceof Artifact) {
         useArt = (Artifact) atsApi.getQueryService().getArtifact((Artifact) object);
      } else if (object instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) object;
         if (atsObject.getStoreObject() instanceof Artifact) {
            useArt = (Artifact) atsApi.getQueryService().getArtifact(atsObject);
         } else {
            useArt = (Artifact) atsApi.getQueryService().getArtifact(atsObject.getId());
         }
      } else if (object instanceof ArtifactId) {
         useArt = (Artifact) atsApi.getQueryService().getArtifact(((ArtifactId) object).getId());
      }
      return useArt;
   }

   @Override
   public boolean areRelated(ArtifactId artifact1, RelationTypeSide relationType, ArtifactId artifact2) {
      boolean related = false;
      Artifact useArt1 = getArtifact(artifact1);
      Artifact useArt2 = getArtifact(artifact2);
      if (useArt1 != null && useArt2 != null) {
         related = useArt1.isRelated(relationType, useArt2);
      }
      return related;
   }

   @Override
   public boolean areRelated(IAtsObject atsObject1, RelationTypeSide relationType, IAtsObject atsObject2) {
      boolean related = false;
      Artifact useArt1 = getArtifact(atsObject1);
      Artifact useArt2 = getArtifact(atsObject2);
      if (useArt1 != null && useArt2 != null) {
         related = useArt1.isRelated(relationType, useArt2);
      }
      return related;
   }

   @Override
   public ArtifactToken getRelatedOrNull(ArtifactId artifact, RelationTypeSide relationType) {
      ArtifactToken related = null;
      Artifact art = getArtifact(artifact);
      if (art != null) {
         try {
            related = art.getRelatedArtifact(relationType);
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
      }
      return related;
   }

   @Override
   public ArtifactToken getRelatedOrSentinel(ArtifactId artifact, RelationTypeSide relationType) {
      ArtifactToken related = ArtifactReadable.SENTINEL;
      Artifact art = getArtifact(artifact);
      if (art != null) {
         try {
            related = art.getRelatedArtifact(relationType);
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
      }
      return related;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getRelatedOrNull(IAtsObject atsObject, RelationTypeSide relationType, Class<T> clazz) {
      T related = null;
      Artifact art = getArtifact(atsObject);
      if (art != null) {
         try {
            Artifact artifact = art.getRelatedArtifact(relationType);
            if (artifact != null) {
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
   public int getRelatedCount(IAtsWorkItem workItem, RelationTypeSide relationType) {
      Artifact artifact = getArtifact(workItem);
      int count = 0;
      if (artifact != null) {
         count = artifact.getRelatedArtifactsCount(relationType);
      }
      return count;
   }

   @Override
   public ArtifactToken getRelatedOrNull(IAtsObject atsObject, RelationTypeSide relationSide) {
      Artifact art = getArtifact(atsObject);
      if (art != null) {
         return art.getRelatedArtifactOrNull(relationSide);
      }
      return null;
   }

   @Override
   public ArtifactToken getRelatedOrSentinel(IAtsObject atsObject, RelationTypeSide relationSide) {
      ArtifactToken related = ArtifactReadable.SENTINEL;
      Artifact art = getArtifact(atsObject);
      if (art != null) {
         related = art.getRelatedArtifactOrNull(relationSide);
         return related == null ? ArtifactReadable.SENTINEL : related;
      }
      return ArtifactReadable.SENTINEL;
   }

   @Override
   public List<ArtifactToken> getRelatedArtifacts(IAtsWorkItem workItem, RelationTypeSide relationTypeSide) {
      Artifact artifact = getArtifact(workItem);
      return Collections.castAll(getRelated(artifact, relationTypeSide));
   }

   @Override
   public Collection<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeSide relationType,
      ArtifactTypeToken artifactType) {
      List<ArtifactToken> results = new LinkedList<>();
      Artifact art = getArtifact(artifact);

      if (art == null) {
         return results;
      }

      for (ArtifactToken related : art.getRelatedArtifacts(relationType)) {
         if (atsApi.getQueryService().getArtifact(related).isOfType(artifactType)) {
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
      Artifact art = getArtifact(artifact);
      return art.getRelatedArtifactsCount(relationTypeSide);
   }

   @Override
   public List<ArtifactId> getRelatedIds(ArtifactId artifact, RelationTypeSide relationTypeSide) {
      List<ArtifactId> related = new LinkedList<>();
      Artifact art = getArtifact(artifact);

      if (art == null) {
         return related;
      }

      for (Artifact rel : art.getRelatedArtifacts(relationTypeSide)) {
         related.add(rel);
      }
      return related;
   }

   @Override
   public Collection<IRelationLink> getRelations(ArtifactId artifact, RelationTypeSide relationTypeSide) {
      Artifact art = getArtifact(artifact);
      return Collections.castAll(art.getRelations(relationTypeSide));
   }

}
