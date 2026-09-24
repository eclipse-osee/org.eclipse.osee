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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.event.ArtifactChangeType;
import org.eclipse.osee.framework.core.event.TransactionCommitTopic;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * OSGi EventHandler that listens for transaction commit events and broadcasts
 * lightweight notifications to connected SSE web clients.
 * <p>
 * Clients receive the notification and call their existing REST endpoint to
 * fetch fresh data (pull model). This keeps the system simple and avoids
 * issues with serialization, payload size, and stale-data overrides.
 * <p>
 * Broadcasts to all connected clients including the author's; the originating client recognizes
 * its own echo via {@code originId} and ignores it (client-side self-dedup), so no server-side
 * connection exclusion is needed.
 */
@Component(immediate = true, service = EventHandler.class, property = {
   EventConstants.EVENT_TOPIC + "=" + TransactionCommitTopic.TOPIC})
public class SseTransactionCommitHandler implements EventHandler {

   @Override
   public void handleEvent(Event event) {
      try {
         String branchId = (String) event.getProperty(TransactionCommitTopic.BRANCH_ID);
         String transactionId = (String) event.getProperty(TransactionCommitTopic.TRANSACTION_ID);
         String authorUserId = (String) event.getProperty(TransactionCommitTopic.AUTHOR_USER_ID);
         String[] artifactIdsArray = (String[]) event.getProperty(TransactionCommitTopic.ARTIFACT_IDS);

         if (branchId == null || transactionId == null || artifactIdsArray == null || artifactIdsArray.length == 0) {
            return;
         }

         List<String> artifactIds = Arrays.asList(artifactIdsArray);

         // Use change types from the event if available, otherwise default
         List<String> changeTypes;
         String[] changeTypesArray = (String[]) event.getProperty(TransactionCommitTopic.CHANGE_TYPES);
         if (changeTypesArray != null && changeTypesArray.length > 0) {
            changeTypes = Arrays.asList(changeTypesArray);
         } else {
            changeTypes = new ArrayList<>();
            changeTypes.add(ArtifactChangeType.ATTRIBUTE_MODIFIED);
         }

         List<ArtifactChangeMessage.AssociatedUsers> associatedUsers =
            SseBroadcastService.parseAssociatedUsers((String) event.getProperty(TransactionCommitTopic.ASSOCIATED_USERS));

         // Broadcast to ALL SSE sinks (including the author's). The originating client recognizes
         // its own echo via originId and ignores it; no server-side connection exclusion.
         String originId = (String) event.getProperty(TransactionCommitTopic.ORIGIN_ID);

         SseBroadcastService.broadcastArtifactChange(branchId, artifactIds, transactionId, authorUserId, changeTypes,
            associatedUsers, originId);
      } catch (Exception ex) {
         OseeLog.logf(SseTransactionCommitHandler.class, Level.WARNING,
            "Failed to handle transaction commit event for SSE broadcast: %s", ex.getMessage());
      }
   }
}
