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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.event.ArtifactChangeType;
import org.eclipse.osee.framework.core.event.BranchEventGuids;
import org.eclipse.osee.framework.core.event.EventModType;
import org.eclipse.osee.framework.core.event.RelationEventGuids;
import org.eclipse.osee.framework.core.event.TransactionCommitTopic;
import org.eclipse.osee.framework.core.event.WebBranchChangeType;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelation1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * Bridge between ActiveMQ (desktop clients) and SSE (web clients).
 * <p>
 * Responsibilities:
 * <ul>
 *   <li><b>Desktop -> Web:</b> Subscribes to {@code RemotePersistEvent1} on ActiveMQ.
 *       When a desktop client commits directly to the DB and fires an ActiveMQ event,
 *       this bridge receives it and relays to SSE web clients.</li>
 *   <li><b>Web -> Desktop:</b> Listens to the OSGi EventAdmin transaction commit topic.
 *       When a web client commits via REST, this bridge constructs a {@code RemotePersistEvent1}
 *       and sends it via ActiveMQ so desktop clients update their cache.</li>
 *   <li><b>Multi-Server:</b> Publishes lightweight server-to-server events via
 *       {@link ServerToServerEventPublisher} and receives them via
 *       {@link ServerToServerEventListener}.</li>
 * </ul>
 */
@Component(immediate = true, service = EventHandler.class, property = {
   EventConstants.EVENT_TOPIC + "=" + TransactionCommitTopic.TOPIC})
public class ActiveMqSseBridge implements EventHandler, IFrameworkEventListener {

   /** Identifier for this server as a network sender on ActiveMQ. */
   private static final String SERVER_SOURCE_ID = "osee-web-server";
   private static final String SERVER_SESSION_ID = "server-session";
   private static final String SERVER_CLIENT_VERSION = "web-server-1.0";

   private static final ObjectMapper MAPPER = new ObjectMapper();

   @Reference
   private IOseeCoreModelEventService messagingService;

   @Reference
   private OrcsTokenService tokenService;

   private volatile boolean connected = false;

   // --- Lifecycle ---

   @Activate
   public void activate() {
      // The desktop<->web bridge and server-to-server events require an ActiveMQ broker. OSEE
      // connects to an EXTERNAL broker (osee.default.broker.uri) and never hosts one. When no broker
      // is configured, skip ActiveMQ wiring and run web-only: SSE fan-out to web clients still works,
      // but there is no desktop relay or cross-server propagation. Logged clearly so it is obvious.
      String brokerUri = OseeProperties.getOseeDefaultBrokerUri();
      if (brokerUri == null || brokerUri.trim().isEmpty()) {
         OseeLog.log(ActiveMqSseBridge.class, Level.WARNING,
            "ActiveMqSseBridge: No osee.default.broker.uri configured -- running web-only. "
               + "Desktop<->web relay and server-to-server events are DISABLED. "
               + "Set osee.default.broker.uri=tcp://<host>:61616 to enable cross-client messaging.");
         return;
      }
      try {
         // Subscribe to ActiveMQ events from desktop clients (desktop -> web).
         messagingService.addFrameworkListener(this);
         messagingService.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(ConnectionNode node) {
               connected = true;
               OseeLog.log(ActiveMqSseBridge.class, Level.INFO,
                  "ActiveMqSseBridge: Connected to ActiveMQ broker");
            }

            @Override
            public void notConnected(ConnectionNode node) {
               connected = false;
               OseeLog.log(ActiveMqSseBridge.class, Level.WARNING,
                  "ActiveMqSseBridge: Disconnected from ActiveMQ broker");
            }
         });
         // Relay web-originated branch changes to desktop clients (web -> desktop).
         SseBroadcastService.setBranchChangeRelay(this::relayBranchChangeToDesktop);
         OseeLog.logf(ActiveMqSseBridge.class, Level.INFO,
            "ActiveMqSseBridge: Activated -- listening for desktop and server events (broker=%s)", brokerUri);
      } catch (Exception ex) {
         OseeLog.logf(ActiveMqSseBridge.class, Level.SEVERE,
            "ActiveMqSseBridge: Failed to activate: %s", ex.getMessage());
      }
   }

   @Deactivate
   public void deactivate() {
      try {
         SseBroadcastService.setBranchChangeRelay(null);
         messagingService.removeFrameworkListener(this);
      } catch (Exception ex) {
         OseeLog.logf(ActiveMqSseBridge.class, Level.WARNING,
            "ActiveMqSseBridge: Error during deactivation: %s", ex.getMessage());
      }
   }

   // --- Desktop -> Web (ActiveMQ -> SSE) ---

   /**
    * Called when a RemoteEvent arrives from ActiveMQ. Desktop clients fire
    * {@code RemotePersistEvent1} after committing directly to the DB; relay the event data to SSE
    * web clients.
    */
   @Override
   public void onEvent(RemoteEvent remoteEvent) {
      if (remoteEvent instanceof RemoteBranchEvent1) {
         relayDesktopBranchEventToWeb((RemoteBranchEvent1) remoteEvent);
         return;
      }

      if (!(remoteEvent instanceof RemotePersistEvent1)) {
         return;
      }

      RemotePersistEvent1 persistEvent = (RemotePersistEvent1) remoteEvent;
      RemoteNetworkSender1 sender = persistEvent.getNetworkSender();

      // Ignore events that originated from this server (web -> desktop path already
      // handled by SseTransactionCommitHandler)
      if (sender != null && SERVER_SOURCE_ID.equals(sender.getSourceObject())) {
         return;
      }

      try {
         String branchId = persistEvent.getBranchGuid();
         String transactionId = String.valueOf(persistEvent.getTransaction().getId());
         String userId = sender != null ? sender.getUserId() : "";

         // Extract numeric artifact IDs for the web client from the new artId field.
         // Desktop clients (this release+) always set artId alongside artGuid.
         List<String> artifactIds = new ArrayList<>();
         for (RemoteBasicGuidArtifact1 art : persistEvent.getArtifacts()) {
            long numericId = art.getArtId();
            if (numericId > 0) {
               artifactIds.add(String.valueOf(numericId));
            }
         }

         if (artifactIds.isEmpty()) {
            return;
         }

         // Map desktop modType GUIDs to web-friendly change type strings
         List<String> changeTypes = new ArrayList<>();
         for (RemoteBasicGuidArtifact1 art : persistEvent.getArtifacts()) {
            String modGuid = art.getModTypeGuid();
            String webType = mapModTypeGuidToWebType(modGuid);
            if (!changeTypes.contains(webType)) {
               changeTypes.add(webType);
            }
         }

         // Collect user references from changed user-valued attributes, grouped by attribute
         // type. Recognized generically via the type-level user-reference DisplayHint -- no ATS
         // knowledge here. Lets interested views (e.g. Actra /world) decide relevance client-side.
         List<ArtifactChangeMessage.AssociatedUsers> associatedUsers = collectAssociatedUsers(persistEvent);

         // Distinct attribute type ids changed, so web clients can do targeted refreshes.
         List<String> changedAttributeTypeIds = collectChangedAttributeTypeIds(persistEvent);

         // Broadcast to all SSE web clients (no self-exclusion needed since
         // desktop clients don't have SSE sinks).
         SseBroadcastService.broadcastArtifactChange(branchId, artifactIds, transactionId, userId, changeTypes,
            associatedUsers, changedAttributeTypeIds, null);

         OseeLog.logf(ActiveMqSseBridge.class, Level.FINE,
            "Relayed desktop RemotePersistEvent1 to SSE: branch=%s, tx=%s, artifacts=%d",
            branchId, transactionId, artifactIds.size());
      } catch (Exception ex) {
         OseeLog.logf(ActiveMqSseBridge.class, Level.WARNING,
            "Failed to relay desktop event to SSE: %s", ex.getMessage());
      }
   }

   // --- Web -> Desktop (EventAdmin -> ActiveMQ) ---

   /**
    * Handles the OSGi EventAdmin transaction commit event fired by TxCallableFactory.
    * Constructs a {@code RemotePersistEvent1} and publishes it to ActiveMQ so
    * desktop Eclipse clients receive the notification and update their cache.
    */
   @Override
   public void handleEvent(Event event) {
      if (!connected) {
         return;
      }

      try {
         String branchId = (String) event.getProperty(TransactionCommitTopic.BRANCH_ID);
         String transactionId = (String) event.getProperty(TransactionCommitTopic.TRANSACTION_ID);
         String authorUserId = (String) event.getProperty(TransactionCommitTopic.AUTHOR_USER_ID);
         String[] artifactIdsArray = (String[]) event.getProperty(TransactionCommitTopic.ARTIFACT_IDS);

         if (branchId == null || transactionId == null || artifactIdsArray == null || artifactIdsArray.length == 0) {
            return;
         }

         RemotePersistEvent1 remoteEvent = new RemotePersistEvent1();
         remoteEvent.setBranchGuid(BranchId.valueOf(branchId));
         remoteEvent.setTransactionId(Integer.parseInt(transactionId));

         String attrChangesJson = (String) event.getProperty(TransactionCommitTopic.ATTRIBUTE_CHANGES);
         Map<String, List<AttrChangeInfo>> attrChangesByArtifact = parseAttributeChanges(attrChangesJson);

         // Build artifact entries with type info and attribute changes
         String[] artifactTypeIdsArray = (String[]) event.getProperty(TransactionCommitTopic.ARTIFACT_TYPE_IDS);
         String[] artifactModTypesArray = (String[]) event.getProperty(TransactionCommitTopic.ARTIFACT_MOD_TYPES);
         for (int i = 0; i < artifactIdsArray.length; i++) {
            long artTypeGuid = 0;
            if (artifactTypeIdsArray != null && i < artifactTypeIdsArray.length) {
               artTypeGuid = Long.parseLong(artifactTypeIdsArray[i]);
            }

            RemoteBasicGuidArtifact1 art = new RemoteBasicGuidArtifact1();
            // Set per-artifact mod type from event data, default to Modified
            String modTypeGuid = EventModType.Modified.getGuid();
            if (artifactModTypesArray != null && i < artifactModTypesArray.length) {
               modTypeGuid = modTypeNameToEventModTypeGuid(artifactModTypesArray[i]);
            }
            art.setModTypeGuid(modTypeGuid);
            art.setBranch(BranchId.valueOf(branchId));
            art.setArtGuid(artifactIdsArray[i]);
            art.setArtId(Long.parseLong(artifactIdsArray[i]));
            art.setArtTypeGuid(artTypeGuid);

            List<AttrChangeInfo> attrChanges = attrChangesByArtifact.get(artifactIdsArray[i]);
            if (attrChanges != null) {
               for (AttrChangeInfo attrChange : attrChanges) {
                  RemoteAttributeChange1 remAttrChange = new RemoteAttributeChange1();
                  remAttrChange.setAttributeId(attrChange.attrId);
                  remAttrChange.setAttrTypeGuid(attrChange.attrTypeId);
                  remAttrChange.setGammaId(attrChange.gammaId);
                  remAttrChange.setModTypeGuid(attrChange.modTypeGuid);
                  for (String dataEntry : attrChange.data) {
                     remAttrChange.getData().add(dataEntry);
                  }
                  art.getAttributes().add(remAttrChange);
               }
            }

            remoteEvent.getArtifacts().add(art);
         }

         // Build relation entries from the relation changes JSON
         String relationChangesJson = (String) event.getProperty(TransactionCommitTopic.RELATION_CHANGES);
         if (relationChangesJson != null && !relationChangesJson.equals("[]")) {
            List<RelChangeInfo> relChanges = parseRelationChanges(relationChangesJson);
            for (RelChangeInfo rel : relChanges) {
               RemoteBasicGuidRelation1 remRel = new RemoteBasicGuidRelation1();
               remRel.setRelTypeGuid(rel.relTypeId);
               remRel.setRelationId((int) rel.relId);
               remRel.setArtAId((int) rel.artIdA);
               remRel.setArtBId((int) rel.artIdB);
               remRel.setGammaId((int) rel.gammaId);
               remRel.setBranchGuid(BranchId.valueOf(branchId));
               remRel.setModTypeGuid(relModTypeNameToGuid(rel.modType));
               remRel.setRationale(rel.rationale);
               remRel.setRelOrder(rel.relOrder);
               RemoteBasicGuidArtifact1 artA = new RemoteBasicGuidArtifact1();
               artA.setArtId(rel.artIdA);
               artA.setArtGuid(String.valueOf(rel.artIdA));
               artA.setBranch(BranchId.valueOf(branchId));
               remRel.setArtA(artA);
               RemoteBasicGuidArtifact1 artB = new RemoteBasicGuidArtifact1();
               artB.setArtId(rel.artIdB);
               artB.setArtGuid(String.valueOf(rel.artIdB));
               artB.setBranch(BranchId.valueOf(branchId));
               remRel.setArtB(artB);
               remoteEvent.getRelations().add(remRel);
            }
         }

         // Set server as the network sender so desktop clients can identify the source
         RemoteNetworkSender1 networkSender = new RemoteNetworkSender1();
         networkSender.setSourceObject(SERVER_SOURCE_ID);
         networkSender.setSessionId(SERVER_SESSION_ID);
         networkSender.setMachineName(getHostName());
         networkSender.setUserId(authorUserId != null ? authorUserId : "");
         networkSender.setMachineIp(getHostAddress());
         networkSender.setClientVersion(SERVER_CLIENT_VERSION);
         networkSender.setPort(0);
         remoteEvent.setNetworkSender(networkSender);

         messagingService.sendRemoteEvent(remoteEvent);

         OseeLog.logf(ActiveMqSseBridge.class, Level.FINE,
            "Sent RemotePersistEvent1 to ActiveMQ: branch=%s, tx=%s, artifacts=%d, attrChanges=%s",
            branchId, transactionId, artifactIdsArray.length,
            attrChangesByArtifact.isEmpty() ? "none" : String.valueOf(attrChangesByArtifact.size()) + " artifacts");
      } catch (Exception ex) {
         OseeLog.logf(ActiveMqSseBridge.class, Level.WARNING,
            "Failed to send web commit event to ActiveMQ: %s", ex.getMessage());
      }
   }

   // --- Branch Events (cross-client) ---

   /**
    * Desktop -> Web: a desktop client changed a branch (commit, rename, archive, state,
    * type, delete, purge) and published a {@code RemoteBranchEvent1} to ActiveMQ. Relay it
    * to SSE web clients as a {@code branchChanged} notification.
    */
   private void relayDesktopBranchEventToWeb(RemoteBranchEvent1 branchEvent) {
      RemoteNetworkSender1 sender = branchEvent.getNetworkSender();

      // Skip our own web -> desktop relay echoed back from the broker.
      if (sender != null && SERVER_SOURCE_ID.equals(sender.getSourceObject())) {
         return;
      }

      String webChangeType = branchEventGuidToWebChangeType(branchEvent.getEventTypeGuid());
      if (webChangeType == null) {
         // Transient/UI-only branch event type with no web equivalent -- nothing to relay.
         return;
      }

      String branchId = branchEvent.getBranchGuid();
      String userId = sender != null ? sender.getUserId() : "";
      SseBroadcastService.broadcastBranchChangeFromRemoteClient(branchId, webChangeType, userId);
   }

   /**
    * Web -> Desktop: a web client changed a branch. Publish a {@code RemoteBranchEvent1} to
    * ActiveMQ so desktop clients update their branch cache/UI. Registered as the branch
    * relay hook on {@link SseBroadcastService}.
    */
   private void relayBranchChangeToDesktop(String branchId, String changeType, String userId) {
      if (!connected) {
         return;
      }
      String eventTypeGuid = webChangeTypeToBranchEventGuid(changeType);
      if (eventTypeGuid == null) {
         return;
      }
      try {
         RemoteBranchEvent1 remoteEvent = new RemoteBranchEvent1();
         remoteEvent.setEventTypeGuid(eventTypeGuid);
         remoteEvent.setBranch(BranchId.valueOf(branchId));
         remoteEvent.setNetworkSender(buildServerNetworkSender(userId));
         messagingService.sendRemoteEvent(remoteEvent);
      } catch (Exception ex) {
         OseeLog.logf(ActiveMqSseBridge.class, Level.WARNING,
            "Failed to relay web branch change to ActiveMQ: %s", ex.getMessage());
      }
   }

   // Package-private (not private) so the fragment test bundle can exercise the branch-event
   // vocabulary mapping directly. This is a pure static mapping with asymmetric/lossy edges
   // (see WebBranchChangeType) worth pinning with tests.
   static String webChangeTypeToBranchEventGuid(String changeType) {
      WebBranchChangeType type = WebBranchChangeType.fromWebValue(changeType);
      if (type == null) {
         return null;
      }
      switch (type) {
         case CREATED:
            return BranchEventGuids.ADDED;
         case COMMITTED:
            return BranchEventGuids.COMMITTED;
         case RENAMED:
            return BranchEventGuids.RENAMED;
         case ARCHIVED:
         case UNARCHIVED:
            return BranchEventGuids.ARCHIVE_STATE_UPDATED;
         case STATE_CHANGED:
            return BranchEventGuids.STATE_UPDATED;
         case TYPE_CHANGED:
            return BranchEventGuids.TYPE_UPDATED;
         case DELETED:
            return BranchEventGuids.DELETED;
         case PURGED:
            return BranchEventGuids.PURGED;
         // REBASELINED is web-specific (a swap the desktop derives from its own granular
         // events); it has no single desktop GUID, so it is not relayed to the desktop bus.
         default:
            return null;
      }
   }

   // Package-private (not private) so the fragment test bundle can exercise the reverse mapping.
   static String branchEventGuidToWebChangeType(String eventTypeGuid) {
      if (eventTypeGuid == null) {
         return null;
      }
      switch (eventTypeGuid) {
         case BranchEventGuids.ADDED:
            return WebBranchChangeType.CREATED.getWebValue();
         case BranchEventGuids.COMMITTED:
            return WebBranchChangeType.COMMITTED.getWebValue();
         case BranchEventGuids.RENAMED:
            return WebBranchChangeType.RENAMED.getWebValue();
         case BranchEventGuids.ARCHIVE_STATE_UPDATED:
            return WebBranchChangeType.ARCHIVED.getWebValue();
         case BranchEventGuids.TYPE_UPDATED:
            return WebBranchChangeType.TYPE_CHANGED.getWebValue();
         case BranchEventGuids.DELETED:
            return WebBranchChangeType.DELETED.getWebValue();
         case BranchEventGuids.PURGED:
            return WebBranchChangeType.PURGED.getWebValue();
         case BranchEventGuids.STATE_UPDATED:
            return WebBranchChangeType.STATE_CHANGED.getWebValue();
         // Transient/in-progress markers (Committing/Deleting/Purging/CommitFailed) and
         // local-only types have no web equivalent -- return null so they are not relayed.
         default:
            return null;
      }
   }

   private static RemoteNetworkSender1 buildServerNetworkSender(String userId) {
      RemoteNetworkSender1 networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(SERVER_SOURCE_ID);
      networkSender.setSessionId(SERVER_SESSION_ID);
      networkSender.setMachineName(getHostName());
      networkSender.setUserId(userId != null ? userId : "");
      networkSender.setMachineIp(getHostAddress());
      networkSender.setClientVersion(SERVER_CLIENT_VERSION);
      networkSender.setPort(0);
      return networkSender;
   }

   /**
    * Builds the {@code associatedUsers} groups from a desktop {@code RemotePersistEvent1} by
    * inspecting each changed attribute's type for a user-reference {@link DisplayHint}. Generic:
    * no ATS/MIM knowledge -- the marker lives on the attribute type, resolved via the token service.
    */
   private List<ArtifactChangeMessage.AssociatedUsers> collectAssociatedUsers(RemotePersistEvent1 persistEvent) {
      Map<String, String> typeEncoding = new HashMap<>();
      Map<String, LinkedHashSet<String>> usersByType = new HashMap<>();
      for (RemoteBasicGuidArtifact1 art : persistEvent.getArtifacts()) {
         for (RemoteAttributeChange1 attrChange : art.getAttributes()) {
            long attrTypeId = attrChange.getAttrTypeGuid();
            AttributeTypeToken attrType;
            try {
               attrType = tokenService.getAttributeType(attrTypeId);
            } catch (Exception ex) {
               continue; // unknown type on this server -- skip
            }
            if (attrType == null || !attrType.isUserReference() || attrChange.getData().isEmpty()) {
               continue;
            }
            String value = attrChange.getData().get(0);
            if (value == null || value.isEmpty()) {
               continue;
            }
            String typeId = String.valueOf(attrTypeId);
            typeEncoding.putIfAbsent(typeId, attrType.isUserArtId() ? "artId" : "userId");
            usersByType.computeIfAbsent(typeId, k -> new LinkedHashSet<>()).add(value);
         }
      }
      if (usersByType.isEmpty()) {
         return null;
      }
      List<ArtifactChangeMessage.AssociatedUsers> result = new ArrayList<>();
      for (Map.Entry<String, LinkedHashSet<String>> entry : usersByType.entrySet()) {
         result.add(new ArtifactChangeMessage.AssociatedUsers(entry.getKey(), typeEncoding.get(entry.getKey()),
            new ArrayList<>(entry.getValue())));
      }
      return result;
   }

   /**
    * Collects the distinct attribute type ids changed in a desktop {@code RemotePersistEvent1}, so
    * web clients can do targeted refreshes (mirrors the web-commit path's changedAttributeTypeIds).
    */
   private List<String> collectChangedAttributeTypeIds(RemotePersistEvent1 persistEvent) {
      LinkedHashSet<String> typeIds = new LinkedHashSet<>();
      for (RemoteBasicGuidArtifact1 art : persistEvent.getArtifacts()) {
         for (RemoteAttributeChange1 attrChange : art.getAttributes()) {
            typeIds.add(String.valueOf(attrChange.getAttrTypeGuid()));
         }
      }
      return typeIds.isEmpty() ? null : new ArrayList<>(typeIds);
   }

   // --- Attribute Change Parsing ---

   private static class AttrChangeInfo {
      int attrId;
      long attrTypeId;
      int gammaId;
      String modTypeGuid;
      List<String> data = new ArrayList<>();
   }

   private static class RelChangeInfo {
      long relTypeId;
      long relId;
      long artIdA;
      long artIdB;
      long gammaId;
      String modType;
      String rationale;
      int relOrder;
   }

   /**
    * Maps ModificationType.getName() to RelationEventType GUIDs for relations.
    */
   private static String relModTypeNameToGuid(String modTypeName) {
      if (modTypeName == null) {
         return RelationEventGuids.ADDED;
      }
      switch (modTypeName) {
         case "New":
            return RelationEventGuids.ADDED;
         case "Deleted":
            return RelationEventGuids.DELETED;
         case "Undeleted":
            return RelationEventGuids.UNDELETED;
         case "Modified":
            return RelationEventGuids.MODIFIED_RATIONALE;
         default:
            return RelationEventGuids.ADDED;
      }
   }

   private List<RelChangeInfo> parseRelationChanges(String json) {
      List<RelChangeInfo> result = new ArrayList<>();
      if (json == null || json.equals("[]") || json.isEmpty()) return result;
      try {
         List<Map<String, Object>> parsed =
            MAPPER.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
         for (Map<String, Object> map : parsed) {
            RelChangeInfo info = new RelChangeInfo();
            info.relTypeId = ((Number) map.getOrDefault("relTypeId", 0)).longValue();
            info.relId = ((Number) map.getOrDefault("relId", 0)).longValue();
            info.artIdA = ((Number) map.getOrDefault("artIdA", 0)).longValue();
            info.artIdB = ((Number) map.getOrDefault("artIdB", 0)).longValue();
            info.gammaId = ((Number) map.getOrDefault("gammaId", 0)).longValue();
            info.modType = (String) map.getOrDefault("modType", "");
            info.rationale = (String) map.getOrDefault("rationale", "");
            info.relOrder = ((Number) map.getOrDefault("relOrder", 0)).intValue();
            result.add(info);
         }
      } catch (Exception ex) {
         OseeLog.logf(ActiveMqSseBridge.class, Level.WARNING,
            "Failed to parse relation changes JSON: %s", ex.getMessage());
      }
      return result;
   }

   private static String modTypeNameToGuid(String modTypeName) {
      if (modTypeName == null) {
         return AttributeEventModificationType.Modified.getGuid();
      }
      switch (modTypeName) {
         case "New":
            return AttributeEventModificationType.New.getGuid();
         case "Modified":
            return AttributeEventModificationType.Modified.getGuid();
         case "Deleted":
            return AttributeEventModificationType.Deleted.getGuid();
         case "Artifact Deleted":
            return AttributeEventModificationType.Artifact_Deleted.getGuid();
         case "Introduced":
            return AttributeEventModificationType.Introduced.getGuid();
         case "Merged":
            return AttributeEventModificationType.Merged.getGuid();
         case "Undeleted":
            return AttributeEventModificationType.Undeleted.getGuid();
         case "Replace_with_version":
            return AttributeEventModificationType.replaceWithVersion.getGuid();
         default:
            return AttributeEventModificationType.Modified.getGuid();
      }
   }

   /**
    * Maps ModificationType.getName() values to EventModType GUIDs.
    * EventModType GUIDs are different from AttributeEventModificationType GUIDs.
    */
   private static String modTypeNameToEventModTypeGuid(String modTypeName) {
      if (modTypeName == null) {
         return EventModType.Modified.getGuid();
      }
      switch (modTypeName) {
         case "New":
            return EventModType.Added.getGuid();
         case "Deleted":
         case "Artifact Deleted":
            return EventModType.Deleted.getGuid();
         default:
            return EventModType.Modified.getGuid();
      }
   }

   @SuppressWarnings("unchecked")
   private Map<String, List<AttrChangeInfo>> parseAttributeChanges(String json) {
      Map<String, List<AttrChangeInfo>> result = new HashMap<>();
      if (json == null || json.equals("{}") || json.isEmpty()) {
         return result;
      }
      try {
         Map<String, List<Map<String, Object>>> parsed =
            MAPPER.readValue(json, new TypeReference<Map<String, List<Map<String, Object>>>>() {});
         for (Map.Entry<String, List<Map<String, Object>>> entry : parsed.entrySet()) {
            List<AttrChangeInfo> changes = new ArrayList<>();
            for (Map<String, Object> map : entry.getValue()) {
               AttrChangeInfo info = new AttrChangeInfo();
               info.attrId = ((Number) map.getOrDefault("attrId", 0)).intValue();
               info.attrTypeId = ((Number) map.getOrDefault("attrTypeId", 0)).longValue();
               info.gammaId = ((Number) map.getOrDefault("gammaId", 0)).intValue();
               String modTypeName = (String) map.getOrDefault("modType", "Modified");
               info.modTypeGuid = modTypeNameToGuid(modTypeName);
               Object dataObj = map.get("data");
               if (dataObj instanceof List) {
                  for (Object item : (List<Object>) dataObj) {
                     info.data.add(item != null ? item.toString() : "");
                  }
               }
               changes.add(info);
            }
            if (!changes.isEmpty()) {
               result.put(entry.getKey(), changes);
            }
         }
      } catch (Exception ex) {
         OseeLog.logf(ActiveMqSseBridge.class, Level.WARNING,
            "Failed to parse attribute changes JSON: %s", ex.getMessage());
      }
      return result;
   }

   // --- Utilities ---

   private static String mapModTypeGuidToWebType(String modGuid) {
      EventModType modType = EventModType.getType(modGuid);
      if (modType == null) {
         return ArtifactChangeType.ATTRIBUTE_MODIFIED;
      }
      switch (modType) {
         case Added:
            return ArtifactChangeType.ARTIFACT_CREATED;
         case Deleted:
         case Purged:
            return ArtifactChangeType.ARTIFACT_DELETED;
         case Modified:
         case Reloaded:
         case ChangeType:
         default:
            return ArtifactChangeType.ATTRIBUTE_MODIFIED;
      }
   }

   private static String getHostName() {
      try {
         return InetAddress.getLocalHost().getHostName();
      } catch (Exception ex) {
         return "unknown";
      }
   }

   private static String getHostAddress() {
      try {
         return InetAddress.getLocalHost().getHostAddress();
      } catch (Exception ex) {
         return "0.0.0.0";
      }
   }
}
