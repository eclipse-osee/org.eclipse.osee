/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreService implements IAtsStoreService {
   private static Map<String, Long> guidToUuid;
   private final IAtsWorkItemFactory workItemFactory;

   public AtsStoreService(IAtsWorkItemFactory workItemFactory) {
      this.workItemFactory = workItemFactory;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, IAtsUser user) {
      return new AtsChangeSet(comment, user);
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment) {
      return new AtsChangeSet(comment);
   }

   @Override
   public List<IAtsWorkItem> reload(Collection<IAtsWorkItem> workItems) {
      List<IAtsWorkItem> results = new ArrayList<>();
      try {
         List<Artifact> artifacts = new LinkedList<Artifact>();
         for (IAtsWorkItem workItem : workItems) {
            if (workItem.getStoreObject() != null && workItem.getStoreObject() instanceof Artifact) {
               artifacts.add((Artifact) workItem.getStoreObject());
            }
         }
         for (Artifact art : ArtifactQuery.reloadArtifacts(artifacts)) {
            if (!art.isDeleted()) {
               IAtsWorkItem workItem = workItemFactory.getWorkItem(art);
               if (workItem != null) {
                  results.add(workItem);
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return results;
   }

   @Override
   public boolean isDeleted(IAtsObject atsObject) {
      return ((Artifact) atsObject.getStoreObject()).isDeleted();
   }

   @Override
   public Long getUuidFromGuid(String guid) {
      Long result = AtsUtilCore.getUuidFromGuid(guid);
      if (result == null) {
         if (guidToUuid == null) {
            guidToUuid = new HashMap<>(200);
         }
         if (guidToUuid.containsKey(guid)) {
            result = guidToUuid.get(guid);
         } else {
            Artifact art = AtsClientService.get().getArtifactByGuid(guid);
            if (art != null) {
               result = art.getUuid();
               guidToUuid.put(guid, result);
            }
         }
      }
      return result;
   }

   @Override
   public String getTypeName(ArtifactId artifact) {
      return ((Artifact) artifact).getArtifactTypeName();
   }

}
