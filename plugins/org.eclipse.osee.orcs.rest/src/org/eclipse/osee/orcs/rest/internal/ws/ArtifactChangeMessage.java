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
import java.util.Collection;
import java.util.List;

/**
 * JSON-serializable message sent over SSE to notify connected web clients
 * of artifact changes. Matches the {@code artifactChangeEvent} type on the Angular side.
 * <p>
 * The {@code transactionId} serves as the authoritative freshness key -- it is
 * globally monotonic (DB-allocated from {@code SKYNET_TRANSACTION_ID_SEQ}) and
 * unambiguous across all servers. Clients ignore events with transactionId <=
 * the last applied for a given artifact context.
 */
public class ArtifactChangeMessage {

   private String branchId;
   private List<String> artifactIds;
   private String transactionId;
   private String userId;
   private List<String> changeTypes;
   private List<AssociatedUsers> associatedUsers;
   private List<String> changedAttributeTypeIds;
   private String originId;

   public ArtifactChangeMessage() {
      // for Jackson
   }

   public ArtifactChangeMessage(String branchId, Collection<String> artifactIds, String transactionId, String userId,
      List<String> changeTypes, List<AssociatedUsers> associatedUsers, List<String> changedAttributeTypeIds,
      String originId) {
      this.branchId = branchId;
      this.artifactIds = new ArrayList<>(artifactIds);
      this.transactionId = transactionId;
      this.userId = userId;
      this.changeTypes = changeTypes;
      this.associatedUsers = associatedUsers;
      this.changedAttributeTypeIds = changedAttributeTypeIds;
      this.originId = originId;
   }

   public String getBranchId() {
      return branchId;
   }

   public void setBranchId(String branchId) {
      this.branchId = branchId;
   }

   public List<String> getArtifactIds() {
      return artifactIds;
   }

   public void setArtifactIds(List<String> artifactIds) {
      this.artifactIds = artifactIds;
   }

   public String getTransactionId() {
      return transactionId;
   }

   public void setTransactionId(String transactionId) {
      this.transactionId = transactionId;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public List<String> getChangeTypes() {
      return changeTypes;
   }

   public void setChangeTypes(List<String> changeTypes) {
      this.changeTypes = changeTypes;
   }

   public List<AssociatedUsers> getAssociatedUsers() {
      return associatedUsers;
   }

   public void setAssociatedUsers(List<AssociatedUsers> associatedUsers) {
      this.associatedUsers = associatedUsers;
   }

   /**
    * @return the distinct attribute type ids changed in this transaction, or null. Lets clients do
    * targeted refreshes (e.g. only when the Name attribute type changed) rather than reacting to
    * every {@code attribute_modified}.
    */
   public List<String> getChangedAttributeTypeIds() {
      return changedAttributeTypeIds;
   }

   public void setChangedAttributeTypeIds(List<String> changedAttributeTypeIds) {
      this.changedAttributeTypeIds = changedAttributeTypeIds;
   }

   /**
    * @return the client-minted origin id of the tab that initiated the change, or null. The
    * originating client uses this to recognize and ignore its own echo (self-dedup).
    */
   public String getOriginId() {
      return originId;
   }

   public void setOriginId(String originId) {
      this.originId = originId;
   }

   /**
    * User references carried by a changed attribute, grouped by the attribute type they came
    * from. Populated generically for attribute types marked with a user-reference
    * {@code DisplayHint} ({@code UserArtId} or {@code UserId}) -- no domain (ATS/MIM) knowledge
    * is required on the server. Consumers filter by the {@code typeId} they care about and match
    * {@code userIds} against the current user; {@code encoding} tells which identity space the
    * ids are in ({@code artId} vs {@code userId}).
    */
   public static class AssociatedUsers {
      private String typeId;
      private String encoding;
      private List<String> userIds;

      public AssociatedUsers() {
         // for Jackson
      }

      public AssociatedUsers(String typeId, String encoding, List<String> userIds) {
         this.typeId = typeId;
         this.encoding = encoding;
         this.userIds = userIds;
      }

      public String getTypeId() {
         return typeId;
      }

      public void setTypeId(String typeId) {
         this.typeId = typeId;
      }

      public String getEncoding() {
         return encoding;
      }

      public void setEncoding(String encoding) {
         this.encoding = encoding;
      }

      public List<String> getUserIds() {
         return userIds;
      }

      public void setUserIds(List<String> userIds) {
         this.userIds = userIds;
      }
   }
}
