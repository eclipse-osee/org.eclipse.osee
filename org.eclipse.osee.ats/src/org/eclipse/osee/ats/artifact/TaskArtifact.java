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
package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class TaskArtifact extends StateMachineArtifact implements IWorldViewArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "Task";

   public static enum TaskStates {
      InWork, Completed, Cancelled
   };

   /**
    * @throws OseeDataStoreException
    * @throws
    */
   public TaskArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public boolean isRelatedToParentWorkflowCurrentState() throws OseeCoreException {
      return getWorldViewRelatedToState().equals(getParentSMA().getStateMgr().getCurrentStateName());
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
   }

   /**
    * Allow parent SMA's assignees and all privileged users up Team tree
    * 
    * @throws OseeCoreException
    */
   @Override
   public Set<User> getPrivilegedUsers() throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      StateMachineArtifact parentSma = getParentSMA();
      if (parentSma.isTeamWorkflow()) users.addAll(((TeamWorkFlowArtifact) parentSma).getPrivilegedUsers());
      users.addAll(parentSma.getStateMgr().getAssignees());
      return users;
   }

   /**
    * Can only un-cancel task when it's related to an active state
    */
   @Override
   public boolean isUnCancellable() {
      try {
         StateMachineArtifact parentSMA = getParentSMA();
         boolean unCancellable =
               (parentSMA.getStateMgr().getCurrentStateName().equals(getSoleAttributeValue(
                     ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "")));
         if (!unCancellable) return false;
         return super.isUnCancellable();
      } catch (Exception ex) {
         // Do Nothing
      }
      return false;
   }

   @Override
   public boolean isTaskable() {
      return false;
   }

   public boolean isUsingTaskResolutionOptions() throws OseeCoreException {
      return (getTaskResolutionOptionDefintions().size() > 0);
   }

   public List<TaskResOptionDefinition> getTaskResolutionOptionDefintions() throws OseeCoreException {
      TeamWorkFlowArtifact team = getParentTeamWorkflow();
      if (team == null) return TaskResolutionOptionRule.EMPTY_TASK_RESOLUTION_OPTIONS;
      return TaskResolutionOptionRule.getTaskResolutionOptions(team.getWorkPageDefinition());
   }

   public TaskResOptionDefinition getTaskResolutionOptionDefinition(String optionName) throws OseeCoreException {
      for (TaskResOptionDefinition def : getTaskResolutionOptionDefintions()) {
         if (def.getName().equals(optionName)) return def;
      }
      return null;
   }

   public List<TaskResOptionDefinition> getTaskResolutionOptionDefintions(String stateName) throws OseeCoreException {
      TeamWorkFlowArtifact team = getParentTeamWorkflow();
      if (team == null) return TaskResolutionOptionRule.EMPTY_TASK_RESOLUTION_OPTIONS;
      return TaskResolutionOptionRule.getTaskResolutionOptions(team.getWorkPageDefinitionByName(stateName));
   }

   public TaskResOptionDefinition getTaskResolutionOptionDefinition(String stateName, String optionName) throws OseeCoreException {
      for (TaskResOptionDefinition def : getTaskResolutionOptionDefintions(stateName)) {
         if (def.getName().equals(optionName)) return def;
      }
      return null;
   }

   @Override
   public String getDescription() {
      try {
         return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public String getWorldViewRelatedToState() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
   }

   @Override
   public String getWorldViewTeam() throws OseeCoreException {
      return "";
   }

   @Override
   public String getWorldViewWorkPackage() throws OseeCoreException {
      String value = super.getWorldViewWorkPackage();
      if (value != null && !value.equals("")) return value;
      return getParentSMA().getWorldViewWorkPackage();
   }

   @Override
   public Date getWorldViewEstimatedCompletionDate() throws OseeCoreException {
      Date value = super.getWorldViewEstimatedCompletionDate();
      if (value != null) return value;
      return getParentSMA().getWorldViewEstimatedCompletionDate();
   }

   @Override
   public Date getWorldViewEstimatedReleaseDate() throws OseeCoreException {
      Date value = super.getWorldViewEstimatedReleaseDate();
      if (value != null) return value;
      return getParentSMA().getWorldViewEstimatedReleaseDate();
   }

   public Boolean isInWork() throws OseeCoreException {
      return (getStateMgr().getCurrentStateName().equals(TaskStates.InWork.name()));
   }

   public void transitionToCancelled(String reason, SkynetTransaction transaction, TransitionOption transitionOption) throws OseeCoreException {
      if (getStateMgr().getCurrentStateName().equals(DefaultTeamState.Cancelled.name())) return;
      setSoleAttributeValue(ATSAttributes.CANCEL_REASON_ATTRIBUTE.getStoreName(), reason);
      Result result = transition(DefaultTeamState.Cancelled.name(), (User) null, transaction, transitionOption);
      if (result.isFalse()) result.popup();
   }

   public void transitionToCompleted(double additionalHours, SkynetTransaction transaction, TransitionOption... transitionOption) throws OseeCoreException {
      if (getStateMgr().getCurrentStateName().equals(DefaultTeamState.Completed.name())) return;
      // Assign current user if unassigned
      try {
         if (getStateMgr().isUnAssigned()) {
            getStateMgr().setAssignee(UserManager.getUser());
         }
         getStateMgr().updateMetrics(additionalHours, 100, true);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      Result result = transition(DefaultTeamState.Completed.name(), (User) null, transaction, transitionOption);
      if (result.isFalse()) result.popup();
   }

   public void transitionToInWork(User toUser, int percentComplete, double additionalHours, SkynetTransaction transaction, TransitionOption... transitionOption) throws OseeCoreException {
      if (getStateMgr().getCurrentStateName().equals(TaskStates.InWork.name())) return;
      Result result = transition(TaskStates.InWork.name(), toUser, transaction, transitionOption);
      if (getStateMgr().getPercentComplete() != percentComplete || additionalHours > 0) {
         getStateMgr().updateMetrics(additionalHours, percentComplete, true);
      }
      if (Collections.getAggregate(transitionOption).contains(TransitionOption.Persist)) {
         saveSMA(transaction);
      }
      if (result.isFalse()) result.popup();
   }

   /**
    * Tasks must transition in/out of completed when percent changes between 100 and <100. This method will handle these
    * cases.
    * 
    * @param percentComplete
    * @param transaction
    * @throws OseeCoreException
    */
   public void statusPercentChanged(double additionalHours, int percentComplete, SkynetTransaction transaction) throws OseeCoreException {
      if (percentComplete == 100 && !isCompleted()) {
         transitionToCompleted(additionalHours, transaction, TransitionOption.None);
      } else if (percentComplete != 100 && isCompleted()) {
         transitionToInWork(UserManager.getUser(), percentComplete, additionalHours, transaction,
               TransitionOption.Persist);
      }
      // Case where already completed and statusing, just add additional hours to InWork state
      else if (percentComplete == 100 && isCompleted()) {
         if (additionalHours > 0) {
            getStateMgr().updateMetrics(TaskStates.InWork.name(), additionalHours, percentComplete, true);
         }
      } else {
         getStateMgr().updateMetrics(additionalHours, percentComplete, true);
      }
   }

   public void parentWorkFlowTransitioned(WorkPageDefinition fromWorkPageDefinition, WorkPageDefinition toWorkPageDefinition, Collection<User> toAssignees, boolean persist, SkynetTransaction transaction, TransitionOption... transitionOption) throws OseeCoreException {
      if (toWorkPageDefinition.getPageName().equals(DefaultTeamState.Cancelled.name()) && isInWork())
         transitionToCancelled("Parent Cancelled", transaction, transitionOption);
      else if (fromWorkPageDefinition.getPageName().equals(DefaultTeamState.Cancelled.name()) && isCancelled()) {
         transitionToInWork(UserManager.getUser(), 99, 0, transaction, transitionOption);
      }
   }

   @Override
   public String getWorldViewDescription() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public String getWorldViewNumberOfTasks() throws OseeCoreException {
      return "";
   }

   @Override
   public Date getWorldViewReleaseDate() throws OseeCoreException {
      if (getParentSMA().isTeamWorkflow()) return ((TeamWorkFlowArtifact) getParentSMA()).getWorldViewReleaseDate();
      return getSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName());
   }

   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      return getParentSMA().getWorldViewTargetedVersion();
   }

   @Override
   public double getManHrsPerDayPreference() throws OseeCoreException {
      return getParentSMA().getManHrsPerDayPreference();
   }

   @Override
   public double getWorldViewRemainHours() throws OseeCoreException {
      if (isCompleted() || isCancelled()) return 0;
      double est = getWorldViewEstimatedHours();
      if (getWorldViewStatePercentComplete() == 0) return getWorldViewEstimatedHours();
      double percent = getStateMgr().getPercentComplete(TaskStates.InWork.name());
      if (percent == 0) return getWorldViewEstimatedHours();
      double remain = getWorldViewEstimatedHours() - (est * (percent / 100.0));
      return remain;
   }

   @Override
   public StateMachineArtifact getParentSMA() throws OseeCoreException {
      if (parentSma != null) return parentSma;
      Collection<StateMachineArtifact> smas =
            getRelatedArtifacts(AtsRelationTypes.SmaToTask_Sma, StateMachineArtifact.class);
      if (smas.size() == 0) throw new OseeStateException("Task has no parent - " + getHumanReadableId());
      parentSma = smas.iterator().next();
      return parentSma;
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      if (parentAction != null) return parentAction;
      parentAction = getParentTeamWorkflow().getParentActionArtifact();
      return parentAction;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      if (parentTeamArt != null) return parentTeamArt;
      StateMachineArtifact sma = getParentSMA();
      if (sma.isTeamWorkflow()) parentTeamArt = ((TeamWorkFlowArtifact) sma);
      return parentTeamArt;
   }

   @Override
   public Collection<User> getImplementers() throws OseeCoreException {
      return getImplementersByState(TaskStates.InWork.name());
   }

   @Override
   public Date getWorldViewDeadlineDate() throws OseeCoreException {
      return null;
   }

   @Override
   public String getWorldViewDeadlineDateStr() throws OseeCoreException {
      return "";
   }

   @Override
   public double getWorldViewWeeklyBenefit() {
      return 0;
   }

   @Override
   public Result isWorldViewAnnualCostAvoidanceValid() throws OseeCoreException {
      return Result.TrueResult;
   }

   @Override
   public String getWorldViewLegacyPCR() throws OseeCoreException {
      StateMachineArtifact sma = getParentSMA();
      if (sma != null) return sma.getWorldViewLegacyPCR();
      return "";
   }

   @Override
   public String getHyperTargetVersion() {
      return null;
   }

   @Override
   public String getWorldViewSWEnhancement() throws OseeCoreException {
      StateMachineArtifact sma = getParentSMA();
      if (sma != null) return sma.getWorldViewSWEnhancement();
      return "";
   }

   @Override
   public String getWorldViewParentID() throws OseeCoreException {
      return getParentTeamWorkflow().getHumanReadableId();
   }

   @Override
   public boolean hasAtsWorldChildren() throws OseeCoreException {
      return false;
   }
}
