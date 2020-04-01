/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.transition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class TransitionData {

   private String cancellationReason;
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

}
