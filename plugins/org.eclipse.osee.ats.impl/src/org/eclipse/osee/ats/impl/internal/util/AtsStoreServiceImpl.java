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
package org.eclipse.osee.ats.impl.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreServiceImpl implements IAtsStoreService {

   private final IAttributeResolver attributeResolver;
   private final IAtsStateFactory stateFactory;
   private final IAtsLogFactory logFactory;
   private final IAtsNotifier notifier;
   private final IAtsServer atsServer;
   private static Map<String, Long> guidToUuid;

   public AtsStoreServiceImpl(IAttributeResolver attributeResolver, IAtsServer atsServer, IAtsStateFactory stateFactory, IAtsLogFactory logFactory, IAtsNotifier notifier) {
      this.atsServer = atsServer;
      this.attributeResolver = attributeResolver;
      this.logFactory = logFactory;
      this.stateFactory = stateFactory;
      this.notifier = notifier;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, IAtsUser user) {
      return new AtsChangeSet(atsServer, attributeResolver, atsServer.getOrcsApi(), stateFactory, logFactory, comment,
         user, notifier);
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment) {
      return createAtsChangeSet(comment, atsServer.getUserService().getCurrentUser());
   }

   @Override
   public List<IAtsWorkItem> reload(Collection<IAtsWorkItem> inWorkWorkflows) {
      List<IAtsWorkItem> workItems = new ArrayList<>(inWorkWorkflows.size());
      List<String> guids = AtsObjects.toGuids(inWorkWorkflows);
      Iterator<ArtifactReadable> arts =
         atsServer.getOrcsApi().getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andGuids(
            guids).getResults().iterator();
      while (arts.hasNext()) {
         workItems.add(atsServer.getWorkItemFactory().getWorkItem(arts.next()));
      }
      return workItems;
   }

   @Override
   public boolean isDeleted(IAtsObject atsObject) {
      return ((ArtifactReadable) atsObject.getStoreObject()).isDeleted();
   }

   @Override
   public Long getUuidFromGuid(String guid) {
      if (guidToUuid == null) {
         guidToUuid = new HashMap<>(200);
      }
      Long result = null;
      if (guidToUuid.containsKey(guid)) {
         result = guidToUuid.get(guid);
      } else {
         ArtifactReadable art = atsServer.getArtifactByGuid(guid);
         if (art != null) {
            result = art.getUuid();
            guidToUuid.put(guid, result);
         }
      }
      return result;
   }

}
