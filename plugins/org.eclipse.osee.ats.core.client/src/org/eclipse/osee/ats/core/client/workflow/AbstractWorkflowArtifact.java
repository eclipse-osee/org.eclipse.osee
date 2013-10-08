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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.api.workflow.WorkStateProvider;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.artifact.AbstractAtsArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.notify.AtsNotifyType;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.log.ArtifactLog;
import org.eclipse.osee.ats.core.client.workflow.note.ArtifactNote;
import org.eclipse.osee.ats.core.client.workflow.note.AtsNote;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionMatch;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.HasCmAccessControl;
import org.eclipse.osee.framework.core.util.IGroupExplorerProvider;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
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
   private final StateManager stateMgr;
   private final IAtsLog atsLog;
   private final AtsNote atsNote;
   private boolean inTransition = false;
   private IAtsWorkData atsWorkData;

   public AbstractWorkflowArtifact(String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(guid, humanReadableId, branch, artifactType);
      stateMgr = new StateManager(this);
      atsLog = AtsCore.getLogFactory().getLog(new ArtifactLog(this), AtsCore.getUserService());
      atsNote = new AtsNote(new ArtifactNote(this));
   }

   public void initializeNewStateMachine(List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy) throws OseeCoreException {
      initializeNewStateMachine(getWorkDefinition(), assignees, createdDate, createdBy);
   }

   public void initializeNewStateMachine(IAtsWorkDefinition workDefinition, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy) throws OseeCoreException {
      IAtsStateDefinition startState = workDefinition.getStartState();
      initializeNewStateMachine(startState, assignees, createdDate, createdBy);
   }

   private void initializeNewStateMachine(IStateToken state, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy) throws OseeCoreException {
      getStateMgr().initializeStateMachine(state, assignees,
         (createdBy == null ? AtsClientService.get().getUserAdmin().getCurrentUser() : createdBy));
      IAtsUser user = createdBy == null ? AtsClientService.get().getUserAdmin().getCurrentUser() : createdBy;
      setCreatedBy(user, true, createdDate);
      TransitionManager.logStateStartedEvent(this, state, createdDate, user);
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
      List<IAtsUser> implementers = new ArrayList<IAtsUser>();
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
               OseeLog.log(Activator.class, Level.SEVERE, String.format(
                  "Invalid CompletedFromState [%s] for Worklfow [%s] and WorkDefinition [%s]", completedFromState,
                  toStringWithId(), getWorkDefinition().getName()));
            }
         }
      }
      return implementers;
   }

   @SuppressWarnings("unused")
   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      return 0;
   }

   @SuppressWarnings("unused")
   public boolean isValidationRequired() throws OseeCoreException {
      return false;
   }

   @Override
   public String getDescription() {
      return "";
   }

   @SuppressWarnings("unused")
   public AbstractWorkflowArtifact getParentAWA() throws OseeCoreException {
      return parentAwa;
   }

   @SuppressWarnings("unused")
   public Artifact getParentActionArtifact() throws OseeCoreException {
      return parentAction;
   }

   @Override
   @SuppressWarnings("unused")
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      return parentTeamArt;
   }

   @SuppressWarnings("unused")
   public String getEditorTitle() throws OseeCoreException {
      return getType() + ": " + getName();
   }

   public void clearCaches() {
      implementersStr = null;
      parentAction = null;
      parentAwa = null;
      parentTeamArt = null;
   }

   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      deleteArts.add(this);
      for (Artifact relative : getBSideArtifacts()) {
         allRelated.put(relative, this);
      }
   }

   private List<Artifact> getBSideArtifacts() throws OseeCoreException {
      List<Artifact> sideBArtifacts = new ArrayList<Artifact>();
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
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks(relatedToState) + getEstimatedHoursFromReviews(relatedToState);
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
      return est - est * PercentCompleteTotalUtil.getPercentCompleteTotal(this) / 100.0;
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

   @SuppressWarnings("unused")
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
         Set<Artifact> artifacts = new HashSet<Artifact>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            if (artifact instanceof AbstractWorkflowArtifact) {
               Result result = ((AbstractWorkflowArtifact) artifact).getStateMgr().isDirtyResult();
               if (result.isTrue()) {
                  return result;
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
               return new Result(true, String.format("Artifact [%s][%s] is dirty\n\n%s", artifact.getHumanReadableId(),
                  artifact, rString));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't save artifact " + getHumanReadableId(), ex);
      }
      return Result.FalseResult;
   }

   public void saveSMA(SkynetTransaction transaction) {
      try {
         Set<Artifact> artifacts = new HashSet<Artifact>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            if (artifact instanceof AbstractWorkflowArtifact) {
               ((AbstractWorkflowArtifact) artifact).getStateMgr().writeToArtifact();
            }
            artifact.persist(transaction);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't save artifact " + getHumanReadableId(), ex);
      }
   }

   public void revertSMA() {
      try {
         Set<Artifact> artifacts = new HashSet<Artifact>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            artifact.reloadAttributesAndRelations();
            if (artifact instanceof AbstractWorkflowArtifact) {
               ((AbstractWorkflowArtifact) artifact).getStateMgr().reload();
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't revert artifact " + getHumanReadableId(), ex);
      }
   }

   @SuppressWarnings("unused")
   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) throws OseeCoreException {
      artifacts.add(smaArtifact);
   }

   /**
    * Called at the end of a transition just before transaction manager persist. SMAs can override to perform tasks due
    * to transition.
    */
   @SuppressWarnings("unused")
   public void transitioned(IAtsStateDefinition fromState, IAtsStateDefinition toState, Collection<? extends IAtsUser> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
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

   @SuppressWarnings("unused")
   public String getWorldViewSWEnhancement() throws OseeCoreException {
      return "";
   }

   @Override
   public String getGroupExplorerName() {
      return String.format("[%s] %s", getStateMgr().getCurrentStateName(), getName());
   }

   public IAtsLog getLog() {
      return atsLog;
   }

   public AtsNote getNotes() {
      return atsNote;
   }

   public Result getUserInputNeeded() {
      return Result.FalseResult;
   }

   public IAtsWorkDefinition getWorkDefinition() {
      WorkDefinitionMatch match = getWorkDefinitionMatch();
      if (match == null) {
         return null;
      }
      if (!match.isMatched()) {
         OseeLog.log(Activator.class, Level.SEVERE, match.toString());
         return null;
      }
      return match.getWorkDefinition();
   }

   public WorkDefinitionMatch getWorkDefinitionMatch() {
      try {
         return AtsClientService.get().getWorkDefinitionAdmin().getWorkDefinition(this);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

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

   public void setCreatedBy(IAtsUser user, boolean logChange, Date date) throws OseeCoreException {
      if (logChange) {
         logCreatedByChange(user, date);
      }
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedBy, user.getUserId());
      }
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedDate)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedDate, date);
      }
      AtsNotificationManager.notify(this, AtsNotifyType.Originator);
   }

   private void logCreatedByChange(IAtsUser user, Date date) throws OseeCoreException {
      if (getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null) == null) {
         atsLog.addLog(LogType.Originated, "", "", date, user);
      } else {
         atsLog.addLog(LogType.Originated, "",
            "Changed by " + AtsClientService.get().getUserAdmin().getCurrentUser().getName(), date, user);
         atsLog.internalResetOriginator(user);
      }
   }

   public void setCreatedBy(IAtsUser user, boolean logChange) throws OseeCoreException {
      Date date = new Date();
      if (logChange) {
         logCreatedByChange(user, date);
      }
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedBy, user.getUserId());
      }
      AtsNotificationManager.notify(this, AtsNotifyType.Originator);
   }

   public void internalSetCreatedBy(IAtsUser user) throws OseeCoreException {
      atsLog.internalResetOriginator(user);
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedBy, user.getUserId());
      }
   }

   public void internalSetCreatedDate(Date date) throws OseeCoreException {
      atsLog.internalResetCreatedDate(date);
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedDate)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedDate, date);
      }
   }

   public Date getCreatedDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
   }

   public Date getCancelledDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   public IAtsUser getCreatedBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserAdmin().getUserById(userId);
      }
      return null;
   }

   public Date internalGetCancelledDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   public IAtsUser getCancelledBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CancelledBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserAdmin().getUserById(userId);
      }
      return null;
   }

   public String getCancelledReason() throws OseeCoreException {
      String reason = getSoleAttributeValue(AtsAttributeTypes.CancelledReason, null);
      if (!Strings.isValid(reason)) {
         reason = getLog().internalGetCancelledReason();
      }
      return reason;
   }

   public void setCancellationReason(String reason) throws OseeCoreException {
      setSoleAttributeValue(AtsAttributeTypes.CancelledReason, reason);
   }

   public String getCancelledFromState() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledFromState, null);
   }

   public Date getCompletedDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CompletedDate, null);
   }

   public IAtsUser getCompletedBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CompletedBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserAdmin().getUserById(userId);
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

   public IAtsLogItem getStateStartedData(IStateToken state) throws OseeCoreException {
      return getStateStartedData(state.getName());
   }

   public IAtsLogItem getStateStartedData(String stateName) throws OseeCoreException {
      return getLog().getStateEvent(LogType.StateEntered, stateName);
   }

   public String getCompletedFromState() throws OseeCoreException {
      String fromState = getSoleAttributeValue(AtsAttributeTypes.CompletedFromState, null);
      if (!Strings.isValid(fromState)) {
         return getLog().internalGetCompletedFromState();
      }
      return fromState;
   }

   public boolean isInWork() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(StateType.Working.name());
   }

   public boolean isCompleted() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(StateType.Completed.name());
   }

   public boolean isCancelled() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(StateType.Cancelled.name());
   }

   public boolean isCompletedOrCancelled() throws OseeCoreException {
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
      return stateMgr.getAssignees().contains(AtsClientService.get().getUserAdmin().getCurrentUser());
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
      return stateMgr.getAssignees();
   }

   public String getTransitionAssigneesStr() throws OseeCoreException {
      return AtsObjects.toString(";", getTransitionAssignees());
   }

   public boolean isInTransition() {
      return inTransition;
   }

   public void setInTransition(boolean inTransition) {
      this.inTransition = inTransition;
   }

   public StateManager getStateMgr() {
      return stateMgr;
   }

   public boolean isTeamWorkflow() {
      return this.isOfType(AtsArtifactTypes.TeamWorkflow);
   }

   public boolean isTask() {
      return this.isOfType(AtsArtifactTypes.Task);
   }

   public boolean isReview() {
      return this instanceof AbstractReviewArtifact;
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
      List<IAtsStateDefinition> allPages = new ArrayList<IAtsStateDefinition>();
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
   public WorkStateProvider getStateData() {
      return stateMgr;
   }

   @Override
   public IAtsWorkData getWorkData() {
      if (atsWorkData == null) {
         atsWorkData = new AtsWorkData(this);
      }
      return atsWorkData;
   }
}
