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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.util.AbstractRelationResolverServiceImpl;
import org.eclipse.osee.ats.rest.IAtsServer;
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

   private final IAtsServer atsServer;

   public AtsRelationResolverServiceImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public Collection<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeSide relationType) {
      List<ArtifactToken> results = new ArrayList<>();
      if (artifact instanceof ArtifactReadable) {
         for (ArtifactReadable art : ((ArtifactReadable) artifact).getRelated(relationType)) {
            results.add(art);
         }
      } else if (artifact instanceof IAtsObject) {
         IAtsObject iAtsObject = (IAtsObject) artifact;
         for (ArtifactReadable art : ((ArtifactReadable) iAtsObject.getStoreObject()).getRelated(relationType)) {
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
      ArtifactReadable useArt = getArtifact(atsObject);
      if (useArt != null) {
         for (ArtifactReadable art : useArt.getRelated(relationType, flag)) {
            IAtsObject object = getAtsObject(art);
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
         related = ((ArtifactReadable) artifact1).areRelated(relationType, (ArtifactReadable) artifact2);
      }
      return related;
   }

   @Override
   public ArtifactToken getRelatedOrNull(ArtifactId artifact, RelationTypeSide relationType) {
      ArtifactToken related = null;
      try {
         related = ((ArtifactReadable) artifact).getRelated(relationType).getAtMostOneOrNull();
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
            ArtifactReadable artifact = art.getRelated(relationType).getOneOrNull();
            if (artifact != null) {
               IAtsObject object = getAtsObject(artifact);
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

   private IAtsObject getAtsObject(ArtifactReadable artifact) {
      IAtsObject result = null;
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         result = atsServer.getWorkItemFactory().getWorkItem(artifact);
      } else if (atsServer.getConfigItemFactory().isAtsConfigArtifact(artifact)) {
         result = atsServer.getConfigItemFactory().getConfigObject(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.Action)) {
         result = atsServer.getWorkItemFactory().getAction(artifact);
      }
      return result;
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
         }
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

}
