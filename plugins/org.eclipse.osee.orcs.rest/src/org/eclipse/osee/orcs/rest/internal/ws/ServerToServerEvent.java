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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Lightweight JSON message for the server-to-server web-cluster notification bus.
 * <p>
 * Carries every cross-server change type a web client cares about, distinguished by
 * {@link #getEventType()}: {@value #ARTIFACT_CHANGED}, {@value #BRANCH_CHANGED}, and
 * {@value #BRANCH_REBASELINED}. When a change happens on one server, it publishes this event; peer
 * servers receive it and fan out an SSE notification to their local web clients (GET-on-notify --
 * no deltas). This is distinct from the desktop {@code RemotePersistEvent1}/{@code
 * RemoteBranchEvent1} bus handled by {@link ActiveMqSseBridge}, which is web&harr;desktop only.
 * <p>
 * Fields not relevant to a given event type are null (e.g. {@code artifactIds}/{@code
 * transactionId} for branch events; {@code changeType}/{@code newBranchId} for artifact events).
 */
public class ServerToServerEvent {

   /** {@link #getEventType()} value for an artifact/attribute/relation change. */
   public static final String ARTIFACT_CHANGED = "artifact_changed";
   /** {@link #getEventType()} value for a branch metadata change. */
   public static final String BRANCH_CHANGED = "branch_changed";
   /** {@link #getEventType()} value for an update-from-parent branch swap. */
   public static final String BRANCH_REBASELINED = "branch_rebaselined";
   /** {@link #getEventType()} value for a presence update (this server's users in a context). */
   public static final String PRESENCE = "presence";

   private final String branchId;
   private final List<String> artifactIds;
   private final String transactionId;
   private final String eventType;
   private final String authorUserId;
   private final String originServerId;
   private final String associatedUsersJson;
   private final String originId;
   private final String changeType;
   private final String newBranchId;
   private final String associatedArtifactId;
   private final List<String> changeTypes;
   private final String context;
   private final List<PresenceEntry> presenceUsers;

   /**
    * Canonical all-fields constructor. Used by Jackson for deserialization; application code should
    * use the per-event-type factories ({@link #artifactChanged}, {@link #branchChanged},
    * {@link #branchRebaselined}, {@link #presence}) so the many event-type-specific null fields are
    * not passed positionally at call sites.
    */
   @JsonCreator
   private ServerToServerEvent(
      @JsonProperty("branchId") String branchId,
      @JsonProperty("artifactIds") List<String> artifactIds,
      @JsonProperty("transactionId") String transactionId,
      @JsonProperty("eventType") String eventType,
      @JsonProperty("authorUserId") String authorUserId,
      @JsonProperty("originServerId") String originServerId,
      @JsonProperty("associatedUsersJson") String associatedUsersJson,
      @JsonProperty("originId") String originId,
      @JsonProperty("changeType") String changeType,
      @JsonProperty("newBranchId") String newBranchId,
      @JsonProperty("associatedArtifactId") String associatedArtifactId,
      @JsonProperty("changeTypes") List<String> changeTypes,
      @JsonProperty("context") String context,
      @JsonProperty("presenceUsers") List<PresenceEntry> presenceUsers) {
      this.branchId = branchId;
      this.artifactIds = artifactIds;
      this.transactionId = transactionId;
      this.eventType = eventType;
      this.authorUserId = authorUserId;
      this.originServerId = originServerId;
      this.associatedUsersJson = associatedUsersJson;
      this.originId = originId;
      this.changeType = changeType;
      this.newBranchId = newBranchId;
      this.associatedArtifactId = associatedArtifactId;
      this.changeTypes = changeTypes;
      this.context = context;
      this.presenceUsers = presenceUsers;
   }

   /** An artifact/attribute/relation change committed on {@code branchId}. */
   public static ServerToServerEvent artifactChanged(String branchId, List<String> artifactIds, String transactionId,
      String authorUserId, String originServerId, String associatedUsersJson, String originId,
      List<String> changeTypes) {
      return new ServerToServerEvent(branchId, artifactIds, transactionId, ARTIFACT_CHANGED, authorUserId,
         originServerId, associatedUsersJson, originId, null, null, null, changeTypes, null, null);
   }

   /** A branch metadata change (created/committed/renamed/state/etc.) on {@code branchId}. */
   public static ServerToServerEvent branchChanged(String branchId, String changeType, String authorUserId,
      String originServerId, String originId, String associatedArtifactId) {
      return new ServerToServerEvent(branchId, null, null, BRANCH_CHANGED, authorUserId, originServerId, null,
         originId, changeType, null, associatedArtifactId, null, null, null);
   }

   /** An update-from-parent branch swap: {@code oldBranchId} retired for {@code newBranchId}. */
   public static ServerToServerEvent branchRebaselined(String oldBranchId, String newBranchId, String changeType,
      String authorUserId, String originServerId, String originId) {
      return new ServerToServerEvent(oldBranchId, null, null, BRANCH_REBASELINED, authorUserId, originServerId, null,
         originId, changeType, newBranchId, null, null, null, null);
   }

   /** This server's current user set for a presence {@code context}. */
   public static ServerToServerEvent presence(String context, String originServerId,
      List<PresenceEntry> presenceUsers) {
      return new ServerToServerEvent(null, null, null, PRESENCE, null, originServerId, null, null, null, null, null,
         null, context, presenceUsers);
   }

   public String getBranchId() {
      return branchId;
   }

   public List<String> getArtifactIds() {
      return artifactIds;
   }

   public String getTransactionId() {
      return transactionId;
   }

   /**
    * One of {@link #ARTIFACT_CHANGED}, {@link #BRANCH_CHANGED}, {@link #BRANCH_REBASELINED}.
    */
   public String getEventType() {
      return eventType;
   }

   public String getAuthorUserId() {
      return authorUserId;
   }

   /**
    * Unique identifier of the server that originated this event.
    * Used to prevent echo (server ignores events from itself).
    */
   public String getOriginServerId() {
      return originServerId;
   }

   /**
    * Serialized associated-users JSON ({@code [{typeId, encoding, userIds}]}) carried verbatim
    * from the originating commit so the receiving server can preserve client-side relevance
    * (e.g. Actra "My World"). May be null when the change touched no user-valued attributes.
    */
   public String getAssociatedUsersJson() {
      return associatedUsersJson;
   }

   /**
    * Client-minted origin id of the tab that initiated the change, carried across servers so a
    * peer server's SSE client can recognize and ignore its own echo when the originating tab is
    * connected to a different server. May be null for desktop/internal-originated changes.
    */
   public String getOriginId() {
      return originId;
   }

   /**
    * Branch change type string (e.g. "renamed", "committed", "rebaselined") for branch events;
    * null for artifact events.
    */
   public String getChangeType() {
      return changeType;
   }

   /**
    * For {@link #BRANCH_REBASELINED}: the branch id going forward after an update-from-parent swap.
    * Null for other event types.
    */
   public String getNewBranchId() {
      return newBranchId;
   }

   /**
    * The changed branch's associated artifact id for a {@link #BRANCH_CHANGED} event (e.g. the ATS
    * workflow a working branch was created for), carried across servers so a peer server's client
    * that keys on it -- like the workflow editor awaiting a branch created for its workflow -- can
    * match. Null when not applicable or unknown.
    */
   public String getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   /**
    * The kinds of changes in this transaction for an {@link #ARTIFACT_CHANGED} event (e.g.
    * {@code artifact_created}, {@code relation_added}, {@code attribute_modified}), carried across
    * servers so a peer server's clients see the true change types -- structural consumers (hierarchy
    * trees, attachment lists) key on these. Null for branch events; the receiver falls back to
    * {@code attribute_modified} when absent.
    */
   public List<String> getChangeTypes() {
      return changeTypes;
   }

   /**
    * For a {@link #PRESENCE} event: the presence context (e.g. {@code workflow/<id>}) whose user
    * set on the originating server is reported by {@link #getPresenceUsers()}. Null otherwise.
    */
   public String getContext() {
      return context;
   }

   /**
    * For a {@link #PRESENCE} event: the users the originating server currently has present in
    * {@link #getContext()}. A peer server merges these with its own local presence when it
    * broadcasts the context's presence to its local clients. Null/empty means the originating
    * server has no users in that context (used to clear the peer's remote view).
    */
   public List<PresenceEntry> getPresenceUsers() {
      return presenceUsers;
   }

   /** A single present user (id + display name) carried on a {@link #PRESENCE} event. */
   public static class PresenceEntry {
      private String userId;
      private String userName;

      public PresenceEntry() {
         // for Jackson
      }

      public PresenceEntry(String userId, String userName) {
         this.userId = userId;
         this.userName = userName;
      }

      public String getUserId() {
         return userId;
      }

      public void setUserId(String userId) {
         this.userId = userId;
      }

      public String getUserName() {
         return userName;
      }

      public void setUserName(String userName) {
         this.userName = userName;
      }
   }
}
