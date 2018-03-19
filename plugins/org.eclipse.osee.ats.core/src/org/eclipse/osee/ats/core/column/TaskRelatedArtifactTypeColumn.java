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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Donald G. Dunne
 */
public class TaskRelatedArtifactTypeColumn extends AbstractServicesColumn {

   private static AtsApi atsApi;
   private static final IArtifactType nullArtifactType = TokenFactory.createArtifactType(3824729235692L, "");

   public TaskRelatedArtifactTypeColumn(AtsApi atsApi) {
      super(atsApi);
      TaskRelatedArtifactTypeColumn.atsApi = atsApi;
   }

   @Override
   String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         Object obj = artIdToRelatedArtTypeCache.get((IAtsWorkItem) atsObject);
         if (obj != null) {
            return obj.toString();
         }
      }
      return "";
   }

   private static final LoadingCache<IAtsWorkItem, IArtifactType> artIdToRelatedArtTypeCache =
      CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build(
         new CacheLoader<IAtsWorkItem, IArtifactType>() {
            @Override
            public IArtifactType load(IAtsWorkItem workItem) throws Exception {
               Long relatedArtId = getRelatedArtId(workItem);
               if (relatedArtId != null) {
                  Map<Long, IArtifactType> results =
                     atsApi.getStoreService().getArtifactTypes(java.util.Collections.singleton(relatedArtId));
                  if (results.isEmpty()) {
                     return nullArtifactType;
                  }
                  return results.values().iterator().next();
               }
               return nullArtifactType;
            }
         });

   public void populateCache(List<IAtsWorkItem> workItems) {
      List<Long> relatedArtIds = new LinkedList<>();
      for (IAtsWorkItem workItem : workItems) {
         try {
            if (artIdToRelatedArtTypeCache.get(workItem) == null) {
               Long relatedArtId = getRelatedArtId(workItem);
               if (relatedArtId != null) {
                  relatedArtIds.add(relatedArtId);
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      if (!relatedArtIds.isEmpty()) {
         Map<Long, IArtifactType> results = atsApi.getStoreService().getArtifactTypes(relatedArtIds);
         for (IAtsWorkItem workItem : workItems) {
            Long relatedArtId = getRelatedArtId(workItem);
            if (relatedArtId != null) {
               IArtifactType artifactType = results.get(relatedArtId);
               artIdToRelatedArtTypeCache.put(workItem, artifactType);
            }
         }
      }
   }

   private static Long getRelatedArtId(IAtsWorkItem workItem) {
      ArtifactId artifact = atsApi.getAttributeResolver().getSoleAttributeValue(workItem,
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
      if (artifact.isValid()) {
         return artifact.getId();
      }
      return null;
   }

}
