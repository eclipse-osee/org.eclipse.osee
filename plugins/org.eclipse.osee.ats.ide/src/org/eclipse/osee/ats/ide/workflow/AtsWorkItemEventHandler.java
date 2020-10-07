/*********************************************************************
 * Copyright (c) 2015 Boeing
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
import org.eclipse.osee.ats.ide.editor.event.WfeArtifactEventManager;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.event.EventUtil;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Event handler to handle refreshing WorkItems when work item is update remotely an no ArtifactEvent is propogated.
 * Usually used for when changes, like transition or setting Work Package are done on server and not the client.
 * Eventually, all ATS work items changes will be done on server and events handled by this and not ArtifactEvent.
 *
 * @author Donald G. Dunne
 */
public class AtsWorkItemEventHandler implements EventHandler {

   @Override
   public void handleEvent(Event event) {
      try {
         // If artifact in cache, then could be in open editor/view.  Just reload.
         if (event.getTopic().equals(AtsTopicEvent.WORK_ITEM_MODIFIED.getTopic()) || event.getTopic().equals(
            AtsTopicEvent.WORK_ITEM_TRANSITIONED.getTopic())) {
            String ids = (String) event.getProperty(AtsTopicEvent.WORK_ITEM_IDS_KEY);
            for (Long workItemId : Collections.fromString(ids, ";", Long::valueOf)) {
               Artifact artifact = ArtifactCache.getActive(workItemId, AtsApiService.get().getAtsBranch());
               TransactionId transaction = EventUtil.getTransaction(event);
               if (artifact != null && artifact.getTransaction().notEqual(transaction)) {
                  artifact.reloadAttributesAndRelations();
               }
            }
            // And, handle event for those WorkflowEditor listeners to specific attr, rel or arts
            WfeArtifactEventManager.handleEventAfterReload(event);
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
