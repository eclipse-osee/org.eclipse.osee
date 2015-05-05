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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreService implements IAtsStoreService {
   private static Map<String, Long> guidToUuid;

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, IAtsUser user) {
      return new AtsChangeSet(comment);
   }

   @Override
   public List<IAtsWorkItem> reload(List<IAtsWorkItem> inWorkWorkflows) {
      throw new UnsupportedOperationException("Not Available");
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
            guidToUuid = new HashMap<String, Long>(200);
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

}
