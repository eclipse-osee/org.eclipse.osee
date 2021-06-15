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

package org.eclipse.osee.ats.api.workflow.transition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class TransitionData {

   private String cancellationReason;
   private AttributeTypeToken cancellationReasonAttrType = AttributeTypeToken.SENTINEL;
   private String cancellationReasonDetails;
   private Collection<ArtifactToken> workItemIds = new HashSet<>();
   private String name;
   private Collection<TransitionOption> transitionOptions = new HashSet<>();
   private Collection<AtsUser> toAssignees = new HashSet<>();
   private String toStateName;
   private boolean executeChanges = false;
   @JsonIgnore
   private Collection<IAtsWorkItem> workItems = new HashSet<>();
   private AtsUser transitionUser;
   private ArtifactId transitionUserArtId;
   private boolean workflowsReloaded = false;
   private boolean dialogCancelled = false;

   public TransitionData() {
      // for jax-rs
   }

   public String getCancellationReason() {
      return cancellationReason;
   }

   public void setCancellationReason(String cancellationReason) {
      this.cancellationReason = cancellationReason;
   }

   public Collection<ArtifactToken> getWorkItemIds() {
      return workItemIds;
   }

   public void setWorkItemIds(Collection<ArtifactToken> workItemIds) {
      this.workItemIds = workItemIds;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Collection<AtsUser> getToAssignees() {
      return toAssignees;
   }

   public void setToAssignees(Collection<AtsUser> toAssignees) {
      this.toAssignees = toAssignees;
   }

   public String getToStateName() {
      return toStateName;
   }

   public void setToStateName(String toStateName) {
      this.toStateName = toStateName;
   }

   public boolean isExecuteChanges() {
      return executeChanges;
   }

   public void setExecuteChanges(boolean executeChanges) {
      this.executeChanges = executeChanges;
   }

   @JsonIgnore
   public Collection<IAtsWorkItem> getWorkItems() {
      return workItems;
   }

   public void setWorkItems(Collection<IAtsWorkItem> workItems) {
      this.workItems = workItems;
   }

   public AtsUser getTransitionUser() {
      return transitionUser;
   }

   public void setTransitionUser(AtsUser transitionUser) {
      this.transitionUser = transitionUser;
   }

   public boolean isWorkflowsReloaded() {
      return workflowsReloaded;
   }

   public void setWorkflowsReloaded(boolean workflowsReloaded) {
      this.workflowsReloaded = workflowsReloaded;
   }

   public Collection<TransitionOption> getTransitionOptions() {
      return transitionOptions;
   }

   public void setTransitionOptions(Collection<TransitionOption> transitionOptions) {
      this.transitionOptions = transitionOptions;
   }

   public String getCancellationReasonDetails() {
      return cancellationReasonDetails;
   }

   public void setCancellationReasonDetails(String cancellationReasonDetails) {
      this.cancellationReasonDetails = cancellationReasonDetails;
   }

   public boolean isDialogCancelled() {
      return dialogCancelled;
   }

   public void setDialogCancelled(boolean dialogCancelled) {
      this.dialogCancelled = dialogCancelled;
   }

   public ArtifactId getTransitionUserArtId() {
      return transitionUserArtId;
   }

   public void setTransitionUserArtId(ArtifactId transitionUserArtId) {
      this.transitionUserArtId = transitionUserArtId;
   }

   public AttributeTypeToken getCancellationReasonAttrType() {
      return cancellationReasonAttrType;
   }

   public void setCancellationReasonAttrType(AttributeTypeToken cancellationReasonAttrType) {
      this.cancellationReasonAttrType = cancellationReasonAttrType;
   }

}
