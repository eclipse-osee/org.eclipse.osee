/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.workflow;

import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.client.TopicEventUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.event.EventUtil;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Event handler to handle refreshing WorkItems when work item is update REMOTELY and no ArtifactEvent is propogated.
 * Usually used for when changes, like transition or setting Work Package, are done on server and not the client.
 * Eventually, all ATS work items changes will be done on server and events handled by this and not ArtifactEvent.
 *
 * @author Donald G. Dunne
 */
public class AtsWorkItemRemoteTopicEventHandler implements EventHandler {

   @Override
   public void handleEvent(Event event) {
      try {
         /**
          * Need to process ONLY remote events. Do NOT reload on transition or you'll get 2 reloads.
          */
         Boolean isRemote = TopicEventUtil.isRemoteOrNull(event);
         if (isRemote != null && isRemote) {
            if (event.getTopic().equals(AtsTopicEvent.WORK_ITEM_MODIFIED.getTopic())) {
               String ids = (String) event.getProperty(AtsTopicEvent.WORK_ITEM_IDS_KEY);
               for (ArtifactId workItemId : Collections.fromString(ids, ";", ArtifactId::valueOf)) {
                  // If artifact in cache, then could be in open editor/view. Just reload.
                  Artifact artifact = ArtifactCache.getActive(workItemId, AtsApiService.get().getAtsBranch());
                  TransactionId transaction = EventUtil.getTransaction(event);
                  if (artifact != null && artifact.getTransaction().notEqual(transaction)) {
                     artifact.reloadAttributesAndRelations();
                  }
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
