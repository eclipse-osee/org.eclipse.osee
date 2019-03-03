/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class TaskRelatedArtifactTypeColumn extends AbstractServicesColumn {

   public TaskRelatedArtifactTypeColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         ArtifactTypeToken artifactType = artIdToRelatedArtTypeCache.get((IAtsWorkItem) atsObject);
         if (artifactType.isValid()) {
            return artifactType.toString();
         }
      }
      return "";
   }

   private final LoadingCache<IAtsWorkItem, ArtifactTypeToken> artIdToRelatedArtTypeCache =
      CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build(
         new CacheLoader<IAtsWorkItem, ArtifactTypeToken>() {
            @Override
            public ArtifactTypeToken load(IAtsWorkItem workItem) throws Exception {
               ArtifactId relatedArtId = TaskRelatedArtifactTypeColumn.this.getRelatedArtId(workItem);
               if (relatedArtId.isValid()) {
                  Map<ArtifactId, ArtifactTypeToken> results =
                     atsApi.getStoreService().getArtifactTypes(Collections.singleton(relatedArtId));
                  return results.values().iterator().next();
               }
               return ArtifactTypeToken.SENTINEL;
            }
         });

   public void populateCache(List<IAtsWorkItem> workItems) {
      Map<ArtifactId, IAtsWorkItem> workItemMap = new HashMap<>();
      List<ArtifactId> relatedArtIds = new LinkedList<>();

      for (IAtsWorkItem workItem : workItems) {
         if (artIdToRelatedArtTypeCache.getIfPresent(workItem) == null) {
            ArtifactId relatedArtId = getRelatedArtId(workItem);
            if (relatedArtId.isValid()) {
               relatedArtIds.add(relatedArtId);
               workItemMap.put(relatedArtId, workItem);
            }
         }
      }
      if (!relatedArtIds.isEmpty()) {
         Map<ArtifactId, ArtifactTypeToken> results = atsApi.getStoreService().getArtifactTypes(relatedArtIds);
         for (ArtifactId relatedArtId : relatedArtIds) {
            if (relatedArtId.isValid()) {
               ArtifactTypeToken artifactType = results.get(relatedArtId);
               artIdToRelatedArtTypeCache.put(workItemMap.get(relatedArtId), artifactType);
            }
         }
      }
   }

   private ArtifactId getRelatedArtId(IAtsWorkItem workItem) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(workItem,
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
   }
}