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
package org.eclipse.osee.ats.core.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.core.artifact.AbstractAtsArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.review.ReviewManager;
import org.eclipse.osee.ats.core.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.version.TargetedVersionUtil;
import org.eclipse.osee.ats.core.version.VersionArtifact;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionMatch;
import org.eclipse.osee.ats.core.workflow.log.ArtifactLog;
import org.eclipse.osee.ats.core.workflow.log.AtsLog;
import org.eclipse.osee.ats.core.workflow.log.LogItem;
import org.eclipse.osee.ats.core.workflow.log.LogType;
import org.eclipse.osee.ats.core.workflow.note.ArtifactNote;
import org.eclipse.osee.ats.core.workflow.note.AtsNote;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.HasCmAccessControl;
import org.eclipse.osee.framework.core.util.IGroupExplorerProvider;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkflowArtifact extends AbstractAtsArtifact implements HasCmAccessControl, IGroupExplorerProvider {

   private final Set<IRelationTypeSide> atsWorldRelations = new HashSet<IRelationTypeSide>();
   private Collection<IBasicUser> transitionAssignees;
   protected AbstractWorkflowArtifact parentAwa;
   protected TeamWorkFlowArtifact parentTeamArt;
   protected ActionArtifact parentAction;
   private StateManager stateMgr;
   private AtsLog atsLog;
   private AtsNote atsNote;
   private boolean inTransition = false;
   private boolean targetedErrorLogged = false;

   public AbstractWorkflowArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public void onBirth() throws OseeCoreException {
      super.onBirth();
      if (getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, null) == null) {
         if (getSoleAttributeValue(AtsAttributeTypes.CurrentState, null) == null) {
            setSoleAttributeValue(AtsAttributeTypes.CurrentState, "");
         }
         if (isAttributeTypeValid(AtsAttributeTypes.CurrentStateType)) {
            setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, WorkPageType.Working.name());
         }
      }
   }

   @Override
   public void onInitializationComplete() throws OseeCoreException {
      super.onInitializationComplete();
      initializeSMA();
   }

   @SuppressWarnings("unused")
   protected void initializeSMA() throws OseeCoreException {
      initalizePreSaveCache();
   }

   public void initalizePreSaveCache() {
      try {
         stateMgr = new StateManager(this);
         atsLog = new AtsLog(new ArtifactLog(this));
         atsNote = new AtsNote(new ArtifactNote(this));
         // TODO Add this back in
         //         AtsNotification.notifyNewAssigneesAndReset(this, true);
         //         AtsNotification.notifyOriginatorAndReset(this, true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void initializeNewStateMachine(Collection<IBasicUser> assignees, Date createdDate, IBasicUser createdBy) throws OseeCoreException {
      StateDefinition startState = getWorkDefinition().getStartState();
      initializeNewStateMachine(startState, assignees, createdDate, createdBy);
   }

   public void initializeNewStateMachine(IWorkPage state, Collection<IBasicUser> assignees, Date createdDate, IBasicUser createdBy) throws OseeCoreException {
      getStateMgr().initializeStateMachine(state, assignees);
      setCreatedBy(createdBy, true, createdDate);
      (new TransitionManager(this)).logStateStartedEvent(state, createdDate, createdBy);
   }

   public boolean isTargetedVersionable() throws OseeCoreException {
      if (!isTeamWorkflow()) {
         return false;
      }
      return ((TeamWorkFlowArtifact) this).getTeamDefinition().getTeamDefinitionHoldingVersions() != null && ((TeamWorkFlowArtifact) this).getTeamDefinition().getTeamDefinitionHoldingVersions().isTeamUsesVersions();
   }

   public String getArtifactSuperTypeName() {
      return getArtifactTypeName();
   }

   @SuppressWarnings("unused")
   public Collection<IBasicUser> getImplementers() throws OseeCoreException {
      return Collections.emptyList();
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

   @SuppressWarnings("unused")
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      return parentTeamArt;
   }

   @SuppressWarnings("unused")
   public String getEditorTitle() throws OseeCoreException {
      return getType() + ": " + getName();
   }

   /**
    * Registers relation as part of the parent/child hierarchy in ATS World
    */
   public void registerAtsWorldRelation(IRelationTypeSide typeSideToken) {
      atsWorldRelations.add(typeSideToken);
   }

   public void clearCaches() {
      implementersStr = null;
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

   public boolean isInState(IWorkPage state) {
      return getStateMgr().isInState(state);
   }

   public String implementersStr = null;

   public double getEstimatedHoursFromArtifact() throws OseeCoreException {
      if (isAttributeTypeValid(AtsAttributeTypes.EstimatedHours)) {
         return getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0);
      }
      return 0;
   }

   public double getEstimatedHoursFromTasks(IWorkPage relatedToState) throws OseeCoreException {
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

   public double getEstimatedHoursFromReviews(IWorkPage relatedToState) throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getEstimatedHours((TeamWorkFlowArtifact) this, relatedToState);
      }
      return 0;
   }

   public double getEstimatedHoursTotal(IWorkPage relatedToState) throws OseeCoreException {
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
   public void transitioned(StateDefinition fromState, StateDefinition toState, Collection<IBasicUser> toAssignees, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      // provided for subclass implementation
   }

   @Override
   public Artifact getParentAtsArtifact() throws OseeCoreException {
      return getParentAWA();
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    */
   public int getPercentCompleteSMAStateTasks(IWorkPage state) throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getPercentCompleteFromTasks(state);
   }

   public Set<IRelationTypeSide> getAtsWorldRelations() {
      return atsWorldRelations;
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

   public AtsLog getLog() {
      return atsLog;
   }

   public AtsNote getNotes() {
      return atsNote;
   }

   public Result getUserInputNeeded() {
      return Result.FalseResult;
   }

   public WorkDefinition getWorkDefinition() {
      return getWorkDefinitionMatch().getWorkDefinition();
   }

   public WorkDefinitionMatch getWorkDefinitionMatch() {
      try {
         return WorkDefinitionFactory.getWorkDefinition(this);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public StateDefinition getStateDefinition() {
      if (getStateMgr().getCurrentStateName() == null) {
         return null;
      }
      return getWorkDefinition().getStateByName(getStateMgr().getCurrentStateName());
   }

   public StateDefinition getStateDefinitionByName(String name) {
      return getWorkDefinition().getStateByName(name);
   }

   public boolean isHistoricalVersion() {
      return isHistorical();
   }

   public List<StateDefinition> getToStates() {
      return getStateDefinition().getToStates();
   }

   public boolean isAccessControlWrite() throws OseeCoreException {
      return AccessControlManager.hasPermission(this, PermissionEnum.WRITE);
   }

   /**
    * @return true if this is a TeamWorkflow and it uses versions
    */
   public boolean isTeamUsesVersions() {
      if (!isTeamWorkflow()) {
         return false;
      }
      try {
         return ((TeamWorkFlowArtifact) this).getTeamDefinition().isTeamUsesVersions();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return false;
      }
   }

   /**
    * Return true if awa is TeamWorkflowArtifact or review of a team workflow and it's TeamDefinitionArtifact has rule
    * set
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
         return teamArt.getTeamDefinition().hasRule(option);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    */
   public boolean isReleased() {
      try {
         VersionArtifact verArt = getTargetedVersion();
         if (verArt != null) {
            return verArt.isReleased();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   public boolean isVersionLocked() {
      try {
         VersionArtifact verArt = getTargetedVersion();
         if (verArt != null) {
            return verArt.isVersionLocked();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   public VersionArtifact getTargetedVersion() throws OseeCoreException {
      return TargetedVersionUtil.getTargetedVersion(this);
   }

   public String getTargetedVersionStr() throws OseeCoreException {
      return TargetedVersionUtil.getTargetedVersionStr(this);
   }

   public void setCreatedBy(IBasicUser user, boolean logChange, Date date) throws OseeCoreException {
      if (logChange) {
         if (getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null) == null) {
            atsLog.addLog(LogType.Originated, "", "", date, user);
         } else {
            atsLog.addLog(LogType.Originated, "", "Changed by " + UserManager.getUser().getName(), date, user);
            atsLog.internalResetOriginator(user);
         }
      }
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedBy, user.getUserId());
      }
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedDate)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedDate, date);
      }
   }

   public void internalSetCreatedBy(IBasicUser user) throws OseeCoreException {
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
      Date date = getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
      if (date == null) {
         // Keep this for backward compatibility
         return getLog().internalGetCreationDate();
      }
      return date;
   }

   public IBasicUser getCreatedBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
      if (!Strings.isValid(userId)) {
         return getLog().internalGetOriginator();
      } else {
         return UserManager.getUserByUserId(userId);
      }
   }

   public Date internalGetCancelledDate() throws OseeCoreException {
      Date date = getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
      if (date == null) {
         // Keep this for backward compatibility
         LogItem item = getLog().internalGetCancelledLogItem();
         if (item != null) {
            return item.getDate();
         }
         return null;
      }
      return date;
   }

   public IBasicUser getCancelledBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CancelledBy, null);
      if (!Strings.isValid(userId)) {
         // Keep this for backward compatibility
         LogItem item = getLog().internalGetCancelledLogItem();
         if (item != null) {
            return item.getUser();
         }
         return null;
      } else {
         return UserManager.getUserByUserId(userId);
      }
   }

   public String getCancelledReason() throws OseeCoreException {
      String reason = getSoleAttributeValue(AtsAttributeTypes.CancelledReason, null);
      if (!Strings.isValid(reason)) {
         reason = getLog().internalGetCancelledReason();
      }
      return reason;
   }

   public void setCancellationReason(String reason) throws OseeCoreException {
      // Keep this for backward compatibility
      getLog().internalSetCancellationReason(reason);
      if (isAttributeTypeValid(AtsAttributeTypes.CancelledReason)) {
         setSoleAttributeValue(AtsAttributeTypes.CancelledReason, reason);
      }
   }

   public String getCancelledFromState() throws OseeCoreException {
      String fromState = getSoleAttributeValue(AtsAttributeTypes.CancelledFromState, null);
      if (!Strings.isValid(fromState)) {
         // Keep this for backward compatibility
         return getLog().internalGetCancelledFromState();
      }
      return fromState;
   }

   public Date getCompletedDate() throws OseeCoreException {
      Date date = getSoleAttributeValue(AtsAttributeTypes.CompletedDate, null);
      if (date == null) {
         // Keep this for backward compatibility
         LogItem item = getLog().internalGetCompletedLogItem();
         if (item != null) {
            return item.getDate();
         }
         return null;
      }
      return date;
   }

   public IBasicUser getCompletedBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CompletedBy, null);
      if (!Strings.isValid(userId)) {
         // Keep this for backward compatibility
         LogItem item = getLog().internalGetCompletedLogItem();
         if (item != null) {
            return item.getUser();
         }
         return null;
      } else {
         return UserManager.getUserByUserId(userId);
      }
   }

   public LogItem getStateCompletedData(IWorkPage state) throws OseeCoreException {
      return getLog().getStateEvent(LogType.StateComplete, state.getPageName());
   }

   public LogItem getStateCancelledData(IWorkPage state) throws OseeCoreException {
      return getLog().getStateEvent(LogType.StateCancelled, state.getPageName());
   }

   public LogItem getStateStartedData(IWorkPage state) throws OseeCoreException {
      return getLog().getStateEvent(LogType.StateEntered, state.getPageName());
   }

   public String getCompletedFromState() throws OseeCoreException {
      String fromState = getSoleAttributeValue(AtsAttributeTypes.CompletedFromState, null);
      if (!Strings.isValid(fromState)) {
         return getLog().internalGetCompletedFromState();
      }
      return fromState;
   }

   public boolean isInWork() throws OseeCoreException {
      // Backward compatibility; remove this once 0.9.7 released
      if (isAttributeTypeValid(AtsAttributeTypes.CurrentStateType) && getSoleAttributeValue(
         AtsAttributeTypes.CurrentStateType, null) != null) {
         return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(WorkPageType.Working.name());
      } else {
         return !isCompletedOrCancelled();
      }

   }

   public boolean isCompleted() throws OseeCoreException {
      if (isAttributeTypeValid(AtsAttributeTypes.CurrentStateType) && getSoleAttributeValue(
         AtsAttributeTypes.CurrentStateType, null) != null) {
         return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(WorkPageType.Completed.name());
      } else {
         return getCurrentStateName().equals(TeamState.Completed.getPageName());
      }
   }

   public boolean isCancelled() throws OseeCoreException {
      // Backward compatibility; remove this once 0.9.7 released
      if (isAttributeTypeValid(AtsAttributeTypes.CurrentStateType) && getSoleAttributeValue(
         AtsAttributeTypes.CurrentStateType, null) != null) {
         return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(WorkPageType.Cancelled.name());
      } else {
         return getCurrentStateName().equals(TeamState.Cancelled.getPageName());
      }
   }

   public boolean isCompletedOrCancelled() throws OseeCoreException {
      return isCompleted() || isCancelled();
   }

   public void setTransitionAssignees(Collection<IBasicUser> assignees) throws OseeCoreException {
      if (assignees.contains(UserManager.getUser(SystemUser.OseeSystem)) || assignees.contains(UserManager.getUser(SystemUser.Guest))) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
      }
      if (assignees.size() > 1 && assignees.contains(UserManager.getUser(SystemUser.UnAssigned))) {
         throw new OseeArgumentException("Can not assign to user and UnAssigned");
      }
      transitionAssignees = assignees;
   }

   public boolean isAssigneeMe() throws OseeCoreException {
      return stateMgr.getAssignees().contains(UserManager.getUser());
   }

   public Collection<IBasicUser> getTransitionAssignees() throws OseeCoreException {
      if (transitionAssignees != null) {
         if (!transitionAssignees.isEmpty() && transitionAssignees.contains(UserManager.getUser(SystemUser.UnAssigned))) {
            transitionAssignees.remove(UserManager.getUser(SystemUser.UnAssigned));
         }
         if (!transitionAssignees.isEmpty()) {
            return transitionAssignees;
         }
      }
      return stateMgr.getAssignees();
   }

   public String getTransitionAssigneesStr() throws OseeCoreException {
      return Artifacts.toString(";", getTransitionAssignees());
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

   protected void addPriviledgedUsersUpTeamDefinitionTree(TeamDefinitionArtifact tda, Set<IBasicUser> users) throws OseeCoreException {
      users.addAll(tda.getLeads());
      users.addAll(tda.getPrivilegedMembers());

      // Walk up tree to get other editors
      if (tda.getParent() != null && tda.getParent() instanceof TeamDefinitionArtifact) {
         addPriviledgedUsersUpTeamDefinitionTree((TeamDefinitionArtifact) tda.getParent(), users);
      }
   }

   @Override
   public CmAccessControl getAccessControl() {
      Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
      BundleContext context = bundle.getBundleContext();
      ServiceReference reference = context.getServiceReference(CmAccessControl.class.getName());
      return (CmAccessControl) context.getService(reference);
   }

   public boolean isTargetedErrorLogged() {
      return targetedErrorLogged;
   }

   public void setTargetedErrorLogged(boolean targetedErrorLogged) {
      this.targetedErrorLogged = targetedErrorLogged;
   }

}
