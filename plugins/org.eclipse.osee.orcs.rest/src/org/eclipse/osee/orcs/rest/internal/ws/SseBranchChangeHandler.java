/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal.ws;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.event.BranchChangeTopic;
import org.eclipse.osee.framework.core.event.WebBranchChangeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * Fans a {@link BranchChangeTopic} event out to web SSE clients (and, via {@link
 * SseBroadcastService}'s relays, to peer web servers and desktop clients).
 * <p>
 * The topic is fired from the ORCS branch layer for every branch create/state/type/name/archive
 * change regardless of origin (REST, ATS, internal), so this is the single server-side branch
 * broadcast source -- the branch analogue of {@link SseTransactionCommitHandler} for artifacts.
 */
@Component(immediate = true, service = EventHandler.class, property = {
   EventConstants.EVENT_TOPIC + "=" + BranchChangeTopic.TOPIC})
public class SseBranchChangeHandler implements EventHandler {

   @Override
   public void handleEvent(Event event) {
      try {
         String branchId = (String) event.getProperty(BranchChangeTopic.BRANCH_ID);
         String changeTypeValue = (String) event.getProperty(BranchChangeTopic.CHANGE_TYPE);
         String userId = (String) event.getProperty(BranchChangeTopic.USER_ID);
         String originId = (String) event.getProperty(BranchChangeTopic.ORIGIN_ID);
         String associatedArtifactId = (String) event.getProperty(BranchChangeTopic.ASSOCIATED_ARTIFACT_ID);

         if (branchId == null || changeTypeValue == null) {
            return;
         }
         WebBranchChangeType changeType = WebBranchChangeType.fromWebValue(changeTypeValue);
         if (changeType == null) {
            // Unknown/unmapped change type -- nothing the web vocabulary can act on.
            return;
         }

         // Broadcast to ALL SSE sinks; the originating client ignores its own echo via originId.
         SseBroadcastService.broadcastBranchChange(branchId, changeType, userId, originId, associatedArtifactId);
      } catch (Exception ex) {
         OseeLog.logf(SseBranchChangeHandler.class, Level.WARNING,
            "Failed to handle branch change event for SSE broadcast: %s", ex.getMessage());
      }
   }
}
