/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Event handler to handle refreshing Actions when work item is update REMOTELY (eg: server) and no ArtifactEvent is
 * propagated. Usually used for when changes, like new child workflows are created on the server and not the client.
 * Eventually, all ATS work items changes will be done on server and events handled by this and not ArtifactEvent.
 *
 * @author Donald G. Dunne
 */
public class AtsActionRemoteTopicEventHandler implements EventHandler {

   @Override
   public void handleEvent(Event event) {
      try {
         // Need to process for both local and remove events.
         if (event.getTopic().equals(AtsTopicEvent.ACTION_MODIFIED.getTopic())) {
            String ids = (String) event.getProperty(AtsTopicEvent.ACTION_IDS_KEY);
            for (ArtifactId actionId : Collections.fromString(ids, ";", ArtifactId::valueOf)) {
               // If artifact in cache, then could be in open editor/view. Just reload.
               Artifact actionArt = ArtifactCache.getActive(actionId, AtsApiService.get().getAtsBranch());
               if (actionArt != null) {
                  actionArt.reloadAttributesAndRelations();
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
