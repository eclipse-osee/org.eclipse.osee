/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.util.IGroupExplorerProvider;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkflowArtifact extends AbstractAtsArtifact implements IAtsWorkItem, IGroupExplorerProvider {

   private Collection<AtsUser> transitionAssignees;
   protected AbstractWorkflowArtifact parentAwa;
   protected TeamWorkFlowArtifact parentTeamArt;
   protected IAtsAction parentAction;
   private IAtsLog atsLog;
   private TransactionId atsLogTx;

   public AbstractWorkflowArtifact(Long id, String guid, BranchToken branch, ArtifactTypeToken artifactType) {
      super(id, guid, branch, artifactType);
   }

   @Override
   public List<AtsUser> getImplementers() {
      return AtsApiService.get().getImplementerService().getImplementers(this);
   }

   public AbstractWorkflowArtifact getParentAWA() {
      return parentAwa;
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() {
      return parentTeamArt;
   }

   @Override
   public IAtsAction getParentAction() {
      Artifact actionArt = (Artifact) parentAction;
      if (actionArt != null) {
         return AtsApiService.get().getWorkItemService().getAction(actionArt);
      }
      return null;
   }

   public String getEditorTitle() {
      return getArtifactType() + ": " + getName();
   }

   @Override
   public void clearCaches() {
      implementersStr = null;
      parentAction = null;
      parentAwa = null;
      parentTeamArt = null;
      AtsApiService.get().getStateFactory().clearStateManager(this);
      atsLog = null;
      AtsApiService.get().getWorkDefinitionService().internalClearWorkDefinition(this);
   }

   public String getCurrentStateName() {
      return getStateMgr().getCurrentStateName();
   }

   @Override
   public boolean isInState(IStateToken state) {
      return getStateMgr().getCurrentStateName().equals(state.getName());
   }

   public String implementersStr = null;

   public double getManHrsPerDayPreference() {
      return AtsUtil.DEFAULT_HOURS_PER_WORK_DAY;
   }

   /**
    * Return true if this artifact, it's ATS relations and any of the other side artifacts are dirty
    *
    * @return true if any object in SMA tree is dirty
    */
   public XResultData isWfeDirty(XResultData rd) {
      try {
         Set<Artifact> artifacts = new HashSet<>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               if (awa.getStateMgr() == null) {
                  rd.errorf("StateManager can not be null for %s", artifact.toStringWithId());
               }
               awa.getStateMgr().isDirty();

               if (awa.getLog().isDirty()) {
                  rd.error("Log is dirty");
               }
            }
            if (artifact.isDirty()) {
               for (Attribute<?> attribute : artifact.internalGetAttributes()) {
                  if (attribute.isDirty()) {
                     rd.errorf("Dirty Attribute: " + attribute.getNameValueDescription() + "\n");
                  }
               }

               String rString = RelationManager.reportHasDirtyLinks(artifact);
               if (Strings.isValid(rString)) {
                  rd.errorf("Dirty Relation Tab: " + rString + " \n");
               }

            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Unable to determine if artifact is dirty " + getAtsId(), ex);
         rd.errorf("Exception in isWfeDirty [%s]", Lib.exceptionToString(ex));
      }
      return rd;
   }

   public void save(IAtsChangeSet changes) {
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

   public void revert() {
      try {
         Set<Artifact> artifacts = new HashSet<>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            artifact.reloadAttributesAndRelations();
            if (artifact instanceof IAtsWorkItem) {
               AtsApiService.get().getStateFactory().load((IAtsWorkItem) artifact, getStateMgr());
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't revert artifact " + getAtsId(), ex);
      }
   }

   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) {
      artifacts.add(smaArtifact);
   }

   @Override
   public Artifact getParentAtsArtifact() {
      return getParentAWA();
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    */
   public int getPercentCompleteSMAStateTasks(IStateToken state) {
      if (!(this instanceof TeamWorkFlowArtifact)) {
         return 0;
      }
      return AtsApiService.get().getEarnedValueService().getPercentCompleteFromTasks(this, state);
   }

   @Override
   public String getGroupExplorerName() {
      return String.format("[%s] %s", getStateMgr().getCurrentStateName(), getName());
   }

   @Override
   public IAtsLog getLog() {
      if (atsLog == null || !getTransaction().equals(atsLogTx)) {
         atsLog = AtsApiService.get().getLogFactory().getLogLoaded(this, AtsApiService.get().getAttributeResolver());
         atsLogTx = getTransaction();
      }
      return atsLog;
   }

   public Result getUserInputNeeded() {
      return Result.FalseResult;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition() {
      try {
         return AtsApiService.get().getWorkDefinitionService().getWorkDefinition(this);
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

   public boolean isAccessControlWrite() {
      return AtsApiService.get().getAccessControlService().hasArtifactPermission(this, PermissionEnum.WRITE,
         null).isSuccess();
   }

   /**
    * Return true if workItem is TeamWorkflowArtifact or review of a team workflow and it's IAtsTeamDefinition has rule
    * set
    */
   public boolean teamDefHasRule(RuleDefinitionOption option) {
      TeamWorkFlowArtifact teamArt = null;
      if (isTeamWorkflow()) {
         teamArt = (TeamWorkFlowArtifact) this;
      }
      if (this instanceof AbstractReviewArtifact) {
         teamArt = (TeamWorkFlowArtifact) ((AbstractReviewArtifact) this).getParentTeamWorkflow();
      }
      if (teamArt == null) {
         return false;
      }
      try {
         return AtsApiService.get().getTeamDefinitionService().hasRule(teamArt.getTeamDefinition(), option.name());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }

   }

   @Override
   public Date getCreatedDate() {
      return getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
   }

   @Override
   public Date getCancelledDate() {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public AtsUser getCreatedBy() {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
      if (Strings.isValid(userId)) {
         return AtsApiService.get().getUserService().getUserByUserId(userId);
      }
      return null;
   }

   public Date internalGetCancelledDate() {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public AtsUser getCancelledBy() {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CancelledBy, null);
      if (Strings.isValid(userId)) {
         return AtsApiService.get().getUserService().getUserByUserId(userId);
      }
      return null;
   }

   @Override
   public String getCancelledReason() {
      String reason = getSoleAttributeValue(AtsAttributeTypes.CancelledReason, null);
      if (!Strings.isValid(reason)) {
         reason = getSoleAttributeValue(AtsAttributeTypes.CancelledReasonEnum, null);
      }
      if (!Strings.isValid(reason)) {
         reason = getLog().internalGetCancelledReason();
      }
      return reason;
   }

   @Override
   public String getCancelledFromState() {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledFromState, null);
   }

   @Override
   public Date getCompletedDate() {
      return getSoleAttributeValue(AtsAttributeTypes.CompletedDate, null);
   }

   @Override
   public AtsUser getCompletedBy() {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CompletedBy, null);
      if (Strings.isValid(userId)) {
         return AtsApiService.get().getUserService().getUserByUserId(userId);
      }
      return null;
   }

   public IAtsLogItem getStateCompletedData(IStateToken state) {
      return getStateCompletedData(state.getName());
   }

   public IAtsLogItem getStateCompletedData(String stateName) {
      return getLog().getStateEvent(LogType.StateComplete, stateName);
   }

   public IAtsLogItem getStateCancelledData(IStateToken state) {
      return getStateCancelledData(state.getName());
   }

   public IAtsLogItem getStateCancelledData(String stateName) {
      return getLog().getStateEvent(LogType.StateCancelled, stateName);
   }

   @Override
   public String getCompletedFromState() {
      String fromState = getSoleAttributeValue(AtsAttributeTypes.CompletedFromState, null);
      if (!Strings.isValid(fromState)) {
         return getLog().internalGetCompletedFromState();
      }
      return fromState;
   }

   public void setTransitionAssignees(Collection<AtsUser> assignees) {
      if (assignees.contains(AtsCoreUsers.SYSTEM_USER)) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Anonymous");
      }
      if (assignees.size() > 1 && assignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
         throw new OseeArgumentException("Can not assign to user and UnAssigned");
      }
      transitionAssignees = assignees;
   }

   public boolean isAssigneeMe() {
      return getStateMgr().getAssignees().contains(AtsApiService.get().getUserService().getCurrentUser());
   }

   /*
    * getTransitionAssignees() is a method that is ONLY to be used in the WETransitionComposition class. Eventually this
    * method needs to be incorporated into WETransitionComposition.
    */
   public Collection<AtsUser> getTransitionAssignees() {
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

   public String getTransitionAssigneesStr() {
      return AtsObjects.toString(";  ", getTransitionAssignees());
   }

   @Override
   public IAtsStateManager getStateMgr() {
      return AtsApiService.get().getStateFactory().getStateManager(this);
   }

   public void clearImplementersCache() {
      implementersStr = null;
   }

   @Override
   public List<AtsUser> getAssignees() {
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
   public void setStateMgr(IAtsStateManager stateMgr) {
      Conditions.assertNotNull(stateMgr, "StateManager");
      AtsApiService.get().getStateFactory().setStateMgr(this, stateMgr);
   }

   @Override
   public Collection<WorkType> getWorkTypes() {
      Set<WorkType> workTypes = new HashSet<>();
      IAtsTeamWorkflow teamWf = getParentTeamWorkflow();
      if (teamWf != null) {
         for (IAtsActionableItem ai : AtsApiService.get().getActionableItemService().getActionableItems(
            teamWf.getTeamDefinition())) {
            Collection<String> workTypeStrs =
               AtsApiService.get().getAttributeResolver().getAttributeValues(ai, AtsAttributeTypes.WorkType);
            for (String workTypeStr : workTypeStrs) {
               try {
                  WorkType workType = WorkType.valueOfOrNone(workTypeStr);
                  workTypes.add(workType);
               } catch (Exception ex) {
                  // do nothing
               }
            }
         }
      }
      return workTypes;
   }

   public void setWorkTypes(List<WorkType> workTypes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isWorkType(WorkType workType) {
      return getWorkTypes().contains(workType);
   }

   @Override
   public void setTags(List<String> tags) {
      throw new UnsupportedOperationException();
   }

}
