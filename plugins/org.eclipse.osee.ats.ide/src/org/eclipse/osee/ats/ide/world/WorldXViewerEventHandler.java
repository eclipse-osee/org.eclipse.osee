/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.ats.ide.world;

import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Topic event handling for World Editors. Uses registration from WorldXViewerEventManager.
 *
 * @author Donald G. Dunne
 */
public class WorldXViewerEventHandler implements EventHandler {

   @Override
   public void handleEvent(Event event) {
      try {
         // If artifact in opened WorkflowEditor, reload.  Else just decache.
         if (event.getTopic().equals(AtsTopicEvent.WORK_ITEM_TRANSITIONED.getTopic())) {
            String ids = (String) event.getProperty(AtsTopicEvent.WORK_ITEM_IDS_KEY);
            for (Long workItemId : Collections.fromString(ids, ";", Long::valueOf)) {
               Artifact artifact = ArtifactCache.getActive(workItemId, AtsClientService.get().getAtsBranch());
               if (artifact != null) {
                  for (IWorldViewerEventHandler handler : WorldXViewerEventManager.getHandlers()) {
                     try {
                        handler.getWorldXViewer().refresh(artifact);
                     } catch (Exception ex) {
                        // do nothing
                     }
                  }
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
