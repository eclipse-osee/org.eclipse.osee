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
package org.eclipse.osee.ats.ide.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.HasCmAccessControl;
import org.eclipse.osee.framework.core.util.IGroupExplorerProvider;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkflowArtifact extends AbstractAtsArtifact implements IAtsWorkItem, HasCmAccessControl, IGroupExplorerProvider {

   private Collection<IAtsUser> transitionAssignees;
   protected AbstractWorkflowArtifact parentAwa;
   protected TeamWorkFlowArtifact parentTeamArt;
   protected ActionArtifact parentAction;
   private IAtsLog atsLog;
   private TransactionId atsLogTx;

   public AbstractWorkflowArtifact(Long id, String guid, BranchId branch, ArtifactTypeToken artifactType) {
      super(id, guid, branch, artifactType);
   }

   @Override
   public List<IAtsUser> getImplementers() {
      return AtsClientService.get().getImplementerService().getImplementers(this);
   }

   public AbstractWorkflowArtifact getParentAWA() {
      return parentAwa;
   }

   public ActionArtifact getParentActionArtifact() {
      return parentAction;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      return parentTeamArt;
   }

   @Override
   public IAtsAction getParentAction() {
      Artifact actionArt = getParentActionArtifact();
      if (actionArt != null) {
         return AtsClientService.get().getWorkItemService().getAction(actionArt);
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
      AtsClientService.get().getStateFactory().clearStateManager(this);
      atsLog = null;
      AtsClientService.get().getWorkDefinitionService().internalClearWorkDefinition(this);
   }

   public String getCurrentStateName() {
      return getStateMgr().getCurrentStateName();
   }

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
   public Result isWfeDirty() {
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
               return new Result(true, String.format("Artifact %s is dirty\n\n%s", artifact.toStringWithId(), rString));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Unable to determine if artifact is dirty " + getAtsId(), ex);
      }
      return Result.FalseResult;
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
               AtsClientService.get().getStateFactory().load((IAtsWorkItem) artifact, getStateMgr());
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
      return AtsClientService.get().getEarnedValueService().getPercentCompleteFromTasks(this, state);
   }

   @Override
   public String getGroupExplorerName() {
      return String.format("[%s] %s", getStateMgr().getCurrentStateName(), getName());
   }

   @Override
   public IAtsLog getLog() {
      if (atsLog == null || !getTransaction().equals(atsLogTx)) {
         atsLog =
            AtsClientService.get().getLogFactory().getLogLoaded(this, AtsClientService.get().getAttributeResolver());
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
         return AtsClientService.get().getWorkDefinitionService().getWorkDefinition((IAtsWorkItem) this);
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
      return AccessControlManager.hasPermission(this, PermissionEnum.WRITE);
   }

   /**
    * Return true if awa is TeamWorkflowArtifact or review of a team workflow and it's IAtsTeamDefinition has rule set
    */
   public boolean teamDefHasRule(RuleDefinitionOption option) {
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

   @Override
   public Date getCreatedDate() {
      return getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
   }

   @Override
   public Date getCancelledDate() {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public IAtsUser getCreatedBy() {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserService().getUserById(userId);
      }
      return null;
   }

   public Date internalGetCancelledDate() {
      return getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public IAtsUser getCancelledBy() {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CancelledBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserService().getUserById(userId);
      }
      return null;
   }

   @Override
   public String getCancelledReason() {
      String reason = getSoleAttributeValue(AtsAttributeTypes.CancelledReason, null);
      if (!Strings.isValid(reason)) {
         reason = getLog().internalGetCancelledReason();
      }
      return reason;
   }

   public void setCancellationReason(String reason, IAtsChangeSet changes) {
      if (changes == null) {
         setSoleAttributeValue(AtsAttributeTypes.CancelledReason, reason);
      } else {
         changes.setSoleAttributeValue((IAtsWorkItem) this, AtsAttributeTypes.CancelledReason, reason);
      }
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
   public IAtsUser getCompletedBy() {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CompletedBy, null);
      if (Strings.isValid(userId)) {
         return AtsClientService.get().getUserService().getUserById(userId);
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

   public void setTransitionAssignees(Collection<IAtsUser> assignees) {
      if (assignees.contains(AtsCoreUsers.SYSTEM_USER) || assignees.contains(AtsCoreUsers.ANONYMOUS_USER)) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
      }
      if (assignees.size() > 1 && assignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
         throw new OseeArgumentException("Can not assign to user and UnAssigned");
      }
      transitionAssignees = assignees;
   }

   public boolean isAssigneeMe() {
      return getStateMgr().getAssignees().contains(AtsClientService.get().getUserService().getCurrentUser());
   }

   /*
    * getTransitionAssignees() is a method that is ONLY to be used in the WETransitionComposition class. Eventually this
    * method needs to be incorporated into WETransitionComposition.
    */
   public Collection<IAtsUser> getTransitionAssignees() {
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
      return AtsObjects.toString(";", getTransitionAssignees());
   }

   @Override
   public IAtsStateManager getStateMgr() {
      return AtsClientService.get().getStateFactory().getStateManager(this);
   }

   protected void addPrivilegedUsersUpTeamDefinitionTree(IAtsTeamDefinition tda, Set<IAtsUser> users) {
      users.addAll(tda.getLeads());
      users.addAll(tda.getPrivilegedMembers());

      // Walk up tree to get other editors
      if (tda.getParentTeamDef() != null) {
         addPrivilegedUsersUpTeamDefinitionTree(tda.getParentTeamDef(), users);
      }
   }

   @Override
   public CmAccessControl getAccessControl() {
      return OsgiUtil.getService(getClass(), CmAccessControl.class);
   }

   public List<IAtsStateDefinition> getToStatesWithCompleteCancelReturnStates() {
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
   public List<IAtsUser> getAssignees() {
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
      AtsClientService.get().getStateFactory().setStateMgr(this, stateMgr);
   }

}
