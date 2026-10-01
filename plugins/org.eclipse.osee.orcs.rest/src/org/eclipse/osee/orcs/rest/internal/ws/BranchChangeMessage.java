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

/**
 * JSON-serializable message sent over SSE to notify connected web clients
 * of branch metadata changes. Matches the {@code branchChangeEvent} type on the Angular side.
 * <p>
 * Branch events are notification-only -- the client performs a GET to retrieve
 * current state. No push optimization because branch metadata has no authoritative
 * ordering key (no transaction ID or gamma on the branch table).
 */
public class BranchChangeMessage {

   private String branchId;
   private String changeType;
   private String userId;
   private String originId;
   private String newBranchId;
   private String associatedArtifactId;

   public BranchChangeMessage() {
      // for Jackson
   }

   public BranchChangeMessage(String branchId, String changeType, String userId, String originId) {
      this(branchId, changeType, userId, originId, null, null);
   }

   public BranchChangeMessage(String branchId, String changeType, String userId, String originId,
      String newBranchId) {
      this(branchId, changeType, userId, originId, newBranchId, null);
   }

   public BranchChangeMessage(String branchId, String changeType, String userId, String originId, String newBranchId,
      String associatedArtifactId) {
      this.branchId = branchId;
      this.changeType = changeType;
      this.userId = userId;
      this.originId = originId;
      this.newBranchId = newBranchId;
      this.associatedArtifactId = associatedArtifactId;
   }

   public String getBranchId() {
      return branchId;
   }

   public void setBranchId(String branchId) {
      this.branchId = branchId;
   }

   public String getChangeType() {
      return changeType;
   }

   public void setChangeType(String changeType) {
      this.changeType = changeType;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   /**
    * @return the client-minted origin id of the tab that initiated the branch change, or null.
    * The originating client uses this to recognize and ignore its own echo (self-dedup).
    */
   public String getOriginId() {
      return originId;
   }

   public void setOriginId(String originId) {
      this.originId = originId;
   }

   /**
    * @return for a {@code rebaselined} change, the id of the new working branch that replaced
    * {@link #getBranchId()} (which is now deleted/rebaselined). Null for other change types.
    * Consumers keyed on the old branch id use this to re-point to the branch going forward.
    */
   public String getNewBranchId() {
      return newBranchId;
   }

   public void setNewBranchId(String newBranchId) {
      this.newBranchId = newBranchId;
   }

   /**
    * @return the changed branch's associated artifact id, or null. Present on {@code created} and
    * {@code state_changed}/{@code deleted} events (where the server already had the branch loaded),
    * letting a consumer match the event to its artifact without tracking the branch id. Absent on
    * other change types and on desktop-relayed events.
    */
   public String getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   public void setAssociatedArtifactId(String associatedArtifactId) {
      this.associatedArtifactId = associatedArtifactId;
   }
}
