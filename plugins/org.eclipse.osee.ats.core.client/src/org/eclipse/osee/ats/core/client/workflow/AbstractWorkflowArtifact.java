/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.IWorkDefinitionMatch;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.artifact.AbstractAtsArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.note.ArtifactNote;
import org.eclipse.osee.ats.core.client.workflow.note.AtsNote;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.core.workflow.state.StateManagerUtility;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.HasCmAccessControl;
import org.eclipse.osee.framework.core.util.IGroupExplorerProvider;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkflowArtifact extends AbstractAtsArtifact implements IAtsWorkItem, HasCmAccessControl, IGroupExplorerProvider {

   private Collection<IAtsUser> transitionAssignees;
   protected AbstractWorkflowArtifact parentAwa;
   protected TeamWorkFlowArtifact parentTeamArt;
   protected ActionArtifact parentAction;
   private IAtsLog atsLog;
   private int atsLogTransactionNumber;
   private int stateMgrTransactionNumber;
   private AtsNote atsNote;
   private IAtsStateManager stateMgr;

   public AbstractWorkflowArtifact(String guid, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(guid, branch, artifactType);
   }

   public void initializeNewStateMachine(List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) throws OseeCoreException {
      Conditions.checkNotNull(createdDate, "createdDate");
      Conditions.checkNotNull(createdBy, "createdBy");
      Conditions.checkNotNull(changes, "changes");
      IAtsStateDefinition startState = getWorkDefinition().getStartState();
      StateManagerUtility.initializeStateMachine(getStateMgr(), startState, assignees,
         (createdBy == null ? AtsClientService.get().getUserService().getCurrentUser() : createdBy), changes);
      IAtsUser user = createdBy == null ? AtsClientService.get().getUserService().getCurrentUser() : createdBy;
      setCreatedBy(user, true, createdDate, changes);
      TransitionManager.logStateStartedEvent(this, startState, createdDate, user);
   }

   public boolean isTargetedVersionable() throws OseeCoreException {
      if (!isTeamWorkflow()) {
         return false;
      }
      return ((TeamWorkFlowArtifact) this).getTeamDefinition().isTeamUsesVersions();
   }

   public String getArtifactSuperTypeName() {
      return getArtifactTypeName();
   }

   @Override
   public List<IAtsUser> getImplementers() throws OseeCoreException {
      List<IAtsUser> implementers = new ArrayList<>();
      if (isCompleted()) {
         String completedFromState = getSoleAttributeValue(AtsAttributeTypes.CompletedFromState, "");
         if (Strings.isValid(completedFromState)) {
            IAtsStateDefinition stateDef = getWorkDefinition().getStateByName(completedFromState);
            if (stateDef != null) {
               for (IAtsUser user : getStateMgr().getAssignees(stateDef)) {
                  if (!implementers.contains(user)) {
                     implementers.add(user);
                  }
               }
            } else {
               OseeLog.log(Activator.class, Level.SEVERE,
                  String.format("Invalid CompletedFromState [%s] for Worklfow [%s] and WorkDefinition [%s]",
                     completedFromState, toStringWithId(), getWorkDefinition().getName()));
            }
         }
      }
      return implementers;
   }

   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      return 0;
   }

   @Override
   public String getDescription() {
      return "";
   }

   public AbstractWorkflowArtifact getParentAWA() throws OseeCoreException {
      return parentAwa;
   }

   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      return parentAction;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      return parentTeamArt;
   }

   @Override
   public IAtsAction getParentAction() {
      Artifact actionArt = getParentActionArtifact();
      if (actionArt != null) {
         return AtsClientService.get().getWorkItemFactory().getAction(actionArt);
      }
      return null;
   }

   public String getEditorTitle() throws OseeCoreException {
      return getType() + ": " + getName();
   }

   public void clearCaches() {
      implementersStr = null;
      parentAction = null;
      parentAwa = null;
      parentTeamArt = null;
      stateMgr = null;
      atsLog = null;
   }

   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      deleteArts.add(this);
      for (Artifact relative : getBSideArtifacts()) {
         allRelated.put(relative, this);
      }
   }

   private List<Artifact> getBSideArtifacts() throws OseeCoreException {
      List<Artifact> sideBArtifacts = new ArrayList<>();
      List<RelationLink> relatives = getRelationsAll(DeletionFlag.EXCLUDE_DELETED);
      for (RelationLink link : relatives) {
         Artifact sideB = link.getArtifactB();
         if (!sideB.equals(this)) {
            sideBArtifacts.add(sideB);
         }
      }

      return sideBArtifacts;
   }

   public String getType() {
      return getArtifactTypeName();
   }

   public String getCurrentStateName() {
      return getStateMgr().getCurrentStateName();
   }

   public boolean isInState(IStateToken state) {
      return getStateMgr().getCurrentStateName().equals(state.getName());
   }

   public String implementersStr = null;

   public double getEstimatedHoursFromArtifact() throws OseeCoreException {
      if (isAttributeTypeValid(AtsAttributeTypes.EstimatedHours)) {
         return getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0);
      }
      return 0;
   }

   public double getEstimatedHoursFromTasks(IStateToken relatedToState) throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getEstimatedHoursFromTasks(relatedToState);
   }

   public double getEstimatedHoursFromTasks() throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getEstimatedHoursFromTasks();
   }

   public double getEstimatedHoursFromReviews() throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getEstimatedHours((TeamWorkFlowArtifact) this);
      }
      return 0;
   }

   public double getEstimatedHoursFromReviews(IStateToken relatedToState) throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getEstimatedHours((TeamWorkFlowArtifact) this, relatedToState);
      }
      return 0;
   }

   public double getEstimatedHoursTotal(IStateToken relatedToState) throws OseeCoreException {
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks(
         relatedToState) + getEstimatedHoursFromReviews(relatedToState);
   }

   public double getEstimatedHoursTotal() throws OseeCoreException {
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks() + getEstimatedHoursFromReviews();
   }

   public double getRemainHoursFromArtifact() throws OseeCoreException {
      if (isCompleted() || isCancelled()) {
         return 0;
      }
      double est = getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0);
      if (est == 0) {
         return getEstimatedHoursFromArtifact();
      }
      return est - est * PercentCompleteTotalUtil.getPercentCompleteTotal(this,
         AtsClientService.get().getServices()) / 100.0;
   }

   public double getRemainHoursTotal() throws OseeCoreException {
      return getRemainHoursFromArtifact() + getRemainFromTasks() + getRemainFromReviews();
   }

   public double getRemainFromTasks() throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getRemainHoursFromTasks();
   }

   public double getRemainFromReviews() throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getRemainHours((TeamWorkFlowArtifact) this);
      }
      return 0;
   }

   public double getManHrsPerDayPreference() throws OseeCoreException {
      return AtsUtilCore.DEFAULT_HOURS_PER_WORK_DAY;
   }

   /**
    * Return true if this artifact, it's ATS relations and any of the other side artifacts are dirty
    *
    * @return true if any object in SMA tree is dirty
    */
   public Result isSMAEditorDirty() {
      try {
         Set<Artifact> artifacts = new HashSet<>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               if (awa.getStateMgr() == null) {
                  throw new OseeStateException("StateManager can not be null for %s", artifact.toStringWithId());
               }
               if (awa.getStateMgr().isDirty()) {
                  return new Result(true, "StateManager is dirty");
               }
               if (awa.getLog().isDirty()) {
                  return new Result(true, "Log is dirty");
               }
            }
            if (artifact.isDirty()) {
               String rString = null;
               for (Attribute<?> attribute : artifact.internalGetAttributes()) {
                  if (attribute.isDirty()) {
                     rString = "Attribute: " + attribute.getNameValueDescription();
                     break;
                  }
               }

               if (rString == null) {
                  rString = RelationManager.reportHasDirtyLinks(artifact);
               }
               return new Result(true,
                  String.format("Artifact [%s][%s] is dirty\n\n%s", artifact, artifact.getGuid(), rString));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Unable to determine if artifact is dirty " + getAtsId(), ex);
      }
      return Result.FalseResult;
   }

   public void saveSMA(IAtsChangeSet changes) {
      try {
         Set<Artifact> artifacts = new HashSet<>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            if (artifact instanceof AbstractWorkflowArtifact) {
               changes.add(artifact);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't save artifact " + getAtsId(), ex);
      }
   }

   public void revertSMA() {
      try {
         Set<Artifact> artifacts = new HashSet<>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            artifact.reloadAttributesAndRelations();
            if (artifact instanceof IAtsWorkItem) {
               AtsClientService.get().getStateFactory().load((IAtsWorkItem) artifact, getStateMgr());
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't revert artifact " + getAtsId(), ex);
      }
   }

   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) throws OseeCoreException {
      artifacts.add(smaArtifact);
   }

   /**
    * Called at the end of a transition just before transaction manager persist. SMAs can override to perform tasks due
    * to transition.
    */
   public void transitioned(IAtsStateDefinition fromState, IAtsStateDefinition toState, Collection<? extends IAtsUser> toAssignees, IAtsChangeSet changes) throws OseeCoreException {
      // provided for subclass implementation
   }

   @Override
   public Artifact getParentAtsArtifact() throws OseeCoreException {
      return getParentAWA();
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    */
   public int getPercentCompleteSMAStateTasks(IStateToken state) throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getPercentCompleteFromTasks(state);
   }

   public String getWorldViewLastUpdated() throws OseeCoreException {
      return DateUtil.getMMDDYYHHMM(getLastModified());
   }

   public String getWorldViewSWEnhancement() throws OseeCoreException {
      return "";
   }

   @Override
   public String getGroupExplorerName() {
      return String.format("[%s] %s", getStateMgr().getCurrentStateName(), getName());
   }

   @Override
   public IAtsLog getLog() {
      if (atsLog == null || atsLogTransactionNumber != getTransactionNumber()) {
         atsLog =
            AtsClientService.get().getLogFactory().getLogLoaded(this, AtsClientService.get().getAttributeResolver());
         atsLogTransactionNumber = getTransactionNumber();
      }
      return atsLog;
   }

   public AtsNote getNotes() {
      if (atsNote == null) {
         atsNote = new AtsNote(new ArtifactNote(this));
      }
      return atsNote;
   }

   public Result getUserInputNeeded() {
      return Result.FalseResult;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition() {
      IWorkDefinitionMatch match = getWorkDefinitionMatch();
      if (match == null) {
         return null;
      }
      if (!match.isMatched()) {
         OseeLog.log(Activator.class, Level.SEVERE, match.toString());
         return null;
      }
      return match.getWorkDefinition();
   }

   public IWorkDefinitionMatch getWorkDefinitionMatch() {
      try {
         return AtsClientService.get().getWorkDefinitionAdmin().getWorkDefinition(this);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public IAtsStateDefinition getStateDefinition() {
      if (getStateMgr().getCurrentStateName() == null) {
         return null;
      }
      return getWorkDefinition().getStateByName(getStateMgr().getCurrentStateName());
   }

   public IAtsStateDefinition getStateDefinitionByName(String name) {
      return getWorkDefinition().getStateByName(name);
   }

   public boolean isHistoricalVersion() {
      return isHistorical();
   }

   public List<IAtsStateDefinition> getToStates() {
      return getStateDefinition().getToStates();
   }

   public boolean isAccessControlWrite() throws OseeCoreException {
      return AccessControlManager.hasPermission(this, PermissionEnum.WRITE);
   }

   /**
    * Return true if awa is TeamWorkflowArtifact or review of a team workflow and it's IAtsTeamDefinition has rule set
    */
   public boolean teamDefHasRule(RuleDefinitionOption option) throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = null;
      if (isTeamWorkflow()) {
         teamArt = (TeamWorkFlowArtifact) this;
      }
      if (this instanceof AbstractReviewArtifact) {
         teamArt = ((AbstractReviewArtifact) this).getParentTeamWorkflow();
      }
      if (teamArt == null) {
         return false;
      }
      try {
         return teamArt.getTeamDefinition().hasRule(option.name());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }

   }

   public void setCreatedBy(IAtsUser user, boolean logChange, Date date, IAtsChangeSet changes) throws OseeCoreException {
      if (logChange) {
         logCreatedByChange(user);
      }
      if (changes == null) {
         if (isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
            setSoleAttributeValue(AtsAttributeTypes.CreatedBy, user.getUserId());
         }
         if (date != null && isAttributeTypeValid(AtsAttributeTypes.CreatedDate)) {
            setSoleAttributeValue(AtsAttributeTypes.CreatedDate, date);
         }
      } else {
         if (changes.isAttributeTypeValid(this, AtsAttributeTypes.CreatedBy)) {
            changes.setSoleAttributeValue(this, AtsAttributeTypes.CreatedBy, user.getUserId());
         }
         if (date != null && changes.isAttributeTypeValid(this, AtsAttributeTypes.CreatedDate)) {
            changes.setSoleAttributeValue(this, AtsAttributeTypes.CreatedDate, date);
         }
      }
      try {
         changes.getNotifications().addWorkItemNotificationEvent(
            AtsNotificationEventFactory.getWorkItemNotificationEvent(
               AtsClientService.get().getUserService().getCurrentUser(), this, AtsNotifyType.Originator));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error adding ATS Notification Event", ex);
      }
   }

   private void logCreatedByChange(IAtsUser user) throws OseeCoreException {
      if (getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null) == null) {
         getLog().addLog(LogType.Originated, "", "", new Date(), user.getUserId());
      } else {
         getLog().addLog(LogType.Originated, "",
            "Changed by " + AtsClientService.get().getUserService().getCurrentUser().getName(), new Date(),
            user.getUserId());
      }
   }

   public void internalSetCreatedBy(IAtsUser user, IAtsChangeSet changes) throws OseeCoreException {
      if (changes.isAttributeTypeValid(this, AtsAttributeTypes.CreatedBy)) {
         changes.setSoleAttributeValue(this, AtsAttributeTypes.CreatedBy, user.getUserId());
      }
   }

   public void internalSetCreatedDate(Date date, IAtsChangeSet changes) throws OseeCoreException {
      getLog().internalResetCreatedDate(date);
      if (changes.isAttributeTypeValid(this, AtsAttributeTypes.CreatedDate)) {
         changes.setSoleAttributeValue(this, AtsAttributeTypes.CreatedDate, date);
      }
   }

   @Override
   public Date getCreatedDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
   }

   @Override
   public Date getCancelledDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public IAtsUser getCreatedBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserService().getUserById(userId);
      }
      return null;
   }

   public Date internalGetCancelledDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public IAtsUser getCancelledBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CancelledBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserService().getUserById(userId);
      }
      return null;
   }

   @Override
   public String getCancelledReason() throws OseeCoreException {
      String reason = getSoleAttributeValue(AtsAttributeTypes.CancelledReason, null);
      if (!Strings.isValid(reason)) {
         reason = getLog().internalGetCancelledReason();
      }
      return reason;
   }

   public void setCancellationReason(String reason, IAtsChangeSet changes) throws OseeCoreException {
      if (changes == null) {
         setSoleAttributeValue(AtsAttributeTypes.CancelledReason, reason);
      } else {
         changes.setSoleAttributeValue(this, AtsAttributeTypes.CancelledReason, reason);
      }
   }

   @Override
   public String getCancelledFromState() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledFromState, null);
   }

   @Override
   public Date getCompletedDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CompletedDate, null);
   }

   @Override
   public IAtsUser getCompletedBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CompletedBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserService().getUserById(userId);
      }
      return null;
   }

   public IAtsLogItem getStateCompletedData(IStateToken state) throws OseeCoreException {
      return getStateCompletedData(state.getName());
   }

   public IAtsLogItem getStateCompletedData(String stateName) throws OseeCoreException {
      return getLog().getStateEvent(LogType.StateComplete, stateName);
   }

   public IAtsLogItem getStateCancelledData(IStateToken state) throws OseeCoreException {
      return getStateCancelledData(state.getName());
   }

   public IAtsLogItem getStateCancelledData(String stateName) throws OseeCoreException {
      return getLog().getStateEvent(LogType.StateCancelled, stateName);
   }

   @Override
   public String getCompletedFromState() throws OseeCoreException {
      String fromState = getSoleAttributeValue(AtsAttributeTypes.CompletedFromState, null);
      if (!Strings.isValid(fromState)) {
         return getLog().internalGetCompletedFromState();
      }
      return fromState;
   }

   public boolean isInWork() {
      return getStateDefinition().getStateType().isWorkingState();
   }

   public boolean isCompleted() {
      return getStateDefinition().getStateType().isCompletedState();
   }

   public boolean isCancelled() {
      return getStateDefinition().getStateType().isCancelledState();
   }

   public boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   public void setTransitionAssignees(Collection<IAtsUser> assignees) throws OseeCoreException {
      if (assignees.contains(AtsCoreUsers.SYSTEM_USER) || assignees.contains(AtsCoreUsers.GUEST_USER)) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
      }
      if (assignees.size() > 1 && assignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
         throw new OseeArgumentException("Can not assign to user and UnAssigned");
      }
      transitionAssignees = assignees;
   }

   public boolean isAssigneeMe() throws OseeCoreException {
      return getStateMgr().getAssignees().contains(AtsClientService.get().getUserService().getCurrentUser());
   }

   /*
    * getTransitionAssignees() is a method that is ONLY to be used in the WETransitionComposition class. Eventually this
    * method needs to be incorporated into WETransitionComposition.
    */
   public Collection<? extends IAtsUser> getTransitionAssignees() throws OseeCoreException {
      if (transitionAssignees != null) {
         if (!transitionAssignees.isEmpty() && transitionAssignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
            transitionAssignees.remove(AtsCoreUsers.UNASSIGNED_USER);
         }
         if (!transitionAssignees.isEmpty()) {
            return transitionAssignees;
         }
      }
      return getStateMgr().getAssignees();
   }

   public String getTransitionAssigneesStr() throws OseeCoreException {
      return AtsObjects.toString(";", getTransitionAssignees());
   }

   @Override
   public IAtsStateManager getStateMgr() {
      if (stateMgr == null || stateMgrTransactionNumber != getTransactionNumber()) {
         try {
            stateMgr = AtsClientService.get().getStateFactory().getStateManager(this, isInDb());
            stateMgrTransactionNumber = getTransactionNumber();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return stateMgr;
   }

   @Override
   public boolean isTeamWorkflow() {
      return this.isOfType(AtsArtifactTypes.TeamWorkflow);
   }

   @Override
   public boolean isTask() {
      return this.isOfType(AtsArtifactTypes.Task);
   }

   @Override
   public boolean isReview() {
      return this instanceof AbstractReviewArtifact;
   }

   @Override
   public boolean isGoal() {
      return this instanceof IAtsGoal;
   }

   protected void addPrivilegedUsersUpTeamDefinitionTree(IAtsTeamDefinition tda, Set<IAtsUser> users) throws OseeCoreException {
      users.addAll(tda.getLeads());
      users.addAll(tda.getPrivilegedMembers());

      // Walk up tree to get other editors
      if (tda.getParentTeamDef() != null) {
         addPrivilegedUsersUpTeamDefinitionTree(tda.getParentTeamDef(), users);
      }
   }

   @Override
   public CmAccessControl getAccessControl() {
      Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
      BundleContext context = bundle.getBundleContext();
      ServiceReference<?> reference = context.getServiceReference(CmAccessControl.class.getName());
      return (CmAccessControl) context.getService(reference);
   }

   public List<IAtsStateDefinition> getToStatesWithCompleteCancelReturnStates() throws OseeCoreException {
      List<IAtsStateDefinition> allPages = new ArrayList<>();
      IAtsStateDefinition currState = getStateDefinition();
      allPages.addAll(currState.getToStates());
      if (currState.getStateType().isCompletedState()) {
         IAtsStateDefinition completedFromState = getWorkDefinition().getStateByName(getCompletedFromState());
         if (completedFromState != null && !allPages.contains(completedFromState)) {
            allPages.add(completedFromState);
         }
      }
      if (currState.getStateType().isCancelledState()) {
         IAtsStateDefinition cancelledFromState = getWorkDefinition().getStateByName(getCancelledFromState());
         if (cancelledFromState != null && !allPages.contains(cancelledFromState)) {
            allPages.add(cancelledFromState);
         }
      }
      return allPages;
   }

   public void clearImplementersCache() {
      implementersStr = null;
   }

   @Override
   public List<IAtsUser> getAssignees() throws OseeCoreException {
      return getStateMgr().getAssignees();
   }

   @Override
   public String getAtsId() {
      String toReturn = getGuid();
      try {
         toReturn = getSoleAttributeValueAsString(AtsAttributeTypes.AtsId, toReturn);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.WARNING, ex);
      }
      return toReturn;
   }

   @Override
   public void setAtsId(String atsId, IAtsChangeSet changes) throws OseeCoreException {
      changes.setSoleAttributeValue(this, AtsAttributeTypes.AtsId, atsId);
   }

   @Override
   public void setStateManager(IAtsStateManager stateManager) {
      this.stateMgr = stateManager;
   }

}
