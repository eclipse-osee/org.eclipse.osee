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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.Collections;

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
   @JsonIgnore
   private final List<IAtsTransitionHook> transitionHooks = new ArrayList<>();
   @JsonIgnore
   private IAtsChangeSet changes;
   private boolean debug = false;

   public TransitionData() {
      // for jax-rs
   }

   public TransitionData(String name, Collection<? extends IAtsWorkItem> workItems, String toStateName, //
      Collection<AtsUser> toAssignees, String cancellationReason, IAtsChangeSet changes, TransitionOption... transitionOption) {
      //      transData.setDebug(isDebug());
      setName(name);
      setWorkItems(Collections.castAll(workItems));
      setToStateName(toStateName);
      setCancellationReason(cancellationReason);
      setCancellationReasonAttrType(AtsAttributeTypes.CancelledReason);
      setToAssignees(toAssignees);
      for (TransitionOption opt : transitionOption) {
         getTransitionOptions().add(opt);
      }
      this.changes = changes;
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

   public void addTransitionOption(TransitionOption transitionOption) {
      transitionOptions.add(transitionOption);
   }

   public void removeTransitionOption(TransitionOption transitionOption) {
      transitionOptions.remove(transitionOption);
   }

   public boolean isOverrideAssigneeCheck() {
      return transitionOptions.contains(TransitionOption.OverrideAssigneeCheck);
   }

   public boolean isOverrideWorkingBranchCheck() {
      return transitionOptions.contains(TransitionOption.OverrideWorkingBranchCheck);
   }

   public boolean isOverrideTransitionValidityCheck() {
      return transitionOptions.contains(TransitionOption.OverrideTransitionValidityCheck);
   }

   public void addTransitionHook(IAtsTransitionHook transitionHook) {
      transitionHooks.add(transitionHook);
   }

   public List<IAtsTransitionHook> getTransitionHooks() {
      return transitionHooks;
   }

   public IAtsChangeSet getChanges() {
      return changes;
   }

   public boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      return atsApi.getBranchService().isWorkingBranchInWork(teamWf);
   }

   public boolean isBranchInCommit(IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      return atsApi.getBranchService().isBranchInCommit(teamWf);
   }

   public boolean isSystemUserAssingee(IAtsWorkItem workItem) {
      return workItem.getAssignees().stream().anyMatch(SystemUser.OseeSystem::equals);
   }

   public boolean isSystemUser() {
      return AtsCoreUsers.isAtsCoreUser(getTransitionUser());
   }

   public boolean isDebug() {
      return debug;
   }

   public void setDebug(boolean debug) {
      this.debug = debug;
   }

}
