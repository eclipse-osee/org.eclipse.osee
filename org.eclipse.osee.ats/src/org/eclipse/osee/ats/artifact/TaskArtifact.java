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
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
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
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws
    */
   public TaskArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#onInitializationComplete()
    */
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
      if (parentSma instanceof TeamWorkFlowArtifact) users.addAll(((TeamWorkFlowArtifact) parentSma).getPrivilegedUsers());
      users.addAll(parentSma.getSmaMgr().getStateMgr().getAssignees());
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
               (parentSMA.getSmaMgr().getStateMgr().getCurrentStateName().equals(getSoleAttributeValue(
                     ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "")));
         if (!unCancellable) return false;
         return super.isUnCancellable();
      } catch (Exception ex) {
         // Do Nothing
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#isTaskable()
    */
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
      return TaskResolutionOptionRule.getTaskResolutionOptions(team.getSmaMgr().getWorkPageDefinition());
   }

   public TaskResOptionDefinition getTaskResolutionOptionDefinition(String optionName) throws OseeCoreException {
      for (TaskResOptionDefinition def : getTaskResolutionOptionDefintions()) {
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
   public StateMachineArtifact getParentSMA() throws OseeCoreException {
      Collection<StateMachineArtifact> smas =
            getRelatedArtifacts(AtsRelation.SmaToTask_Sma, StateMachineArtifact.class);
      if (smas.size() > 0) return smas.iterator().next();
      return null;
   }

   public Boolean isCancelled() throws OseeCoreException {
      return smaMgr.isCancelled();
   }

   public Boolean isInWork() throws OseeCoreException {
      return (smaMgr.getStateMgr().getCurrentStateName().equals(TaskStates.InWork.name()));
   }

   public Boolean isCompleted() throws OseeCoreException {
      return smaMgr.isCompleted();
   }

   public void transitionToCancelled(String reason, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Cancelled.name())) return;
      setSoleAttributeValue(ATSAttributes.CANCEL_REASON_ATTRIBUTE.getStoreName(), reason);
      Result result = smaMgr.transition(DefaultTeamState.Cancelled.name(), (User) null, persist, transaction);
      if (result.isFalse()) result.popup();
   }

   public void transitionToCompleted(boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Completed.name())) return;
      // Assign current user if unassigned
      try {
         if (smaMgr.getStateMgr().getAssignees().size() == 1 && smaMgr.getStateMgr().getAssignees().contains(
               UserManager.getUser(SystemUser.UnAssigned))) {
            smaMgr.getStateMgr().setAssignee(UserManager.getUser());
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      Result result = smaMgr.transition(DefaultTeamState.Completed.name(), (User) null, persist, transaction);
      if (result.isFalse()) result.popup();
   }

   public void transitionToInWork(User toUser, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(TaskStates.InWork.name())) return;
      Result result = smaMgr.transition(TaskStates.InWork.name(), toUser, false, transaction);
      if (smaMgr.getStateMgr().getPercentComplete() == 100) {
         smaMgr.getStateMgr().updateMetrics(0, 99, true);
      }
      if (persist) smaMgr.getSma().saveSMA(transaction);
      if (result.isFalse()) result.popup();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#statusChanged()
    */
   @Override
   public void statusChanged() throws OseeCoreException {
      super.statusChanged();
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      if (smaMgr.getStateMgr().getPercentComplete() == 100 && !isCompleted())
         transitionToCompleted(false, transaction);
      else if (smaMgr.getStateMgr().getPercentComplete() != 100 && isCompleted()) {
         transitionToInWork(UserManager.getUser(), true, transaction);
      }
      transaction.execute();
   }

   public void parentWorkFlowTransitioned(WorkPageDefinition fromWorkPageDefinition, WorkPageDefinition toWorkPageDefinition, Collection<User> toAssignees, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      if (toWorkPageDefinition.getPageName().equals(DefaultTeamState.Cancelled.name()) && isInWork())
         transitionToCancelled("Parent Cancelled", persist, transaction);
      else if (fromWorkPageDefinition.getPageName().equals(DefaultTeamState.Cancelled.name()) && isCancelled()) transitionToInWork(
            UserManager.getUser(), persist, transaction);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDescription()
    */
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
      if (getParentSMA() instanceof TeamWorkFlowArtifact) return ((TeamWorkFlowArtifact) getParentSMA()).getWorldViewReleaseDate();
      return getSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName());
   }

   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      return getParentSMA().getWorldViewTargetedVersion();
   }

   @Override
   public double getWorldViewRemainHours() throws OseeCoreException {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) return 0;
      double est = getWorldViewEstimatedHours();
      if (getWorldViewStatePercentComplete() == 0) return getWorldViewEstimatedHours();
      double percent = smaMgr.getStateMgr().getPercentComplete(TaskStates.InWork.name());
      if (percent == 0) return getWorldViewEstimatedHours();
      double remain = getWorldViewEstimatedHours() - (est * (percent / 100.0));
      return remain;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentActionArtifact()
    */
   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      StateMachineArtifact sma = getParentSMA();
      if (sma instanceof TeamWorkFlowArtifact)
         return ((TeamWorkFlowArtifact) sma).getParentActionArtifact();
      else
         return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentTeamWorkflow()
    */
   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      StateMachineArtifact sma = getParentSMA();
      if (sma instanceof TeamWorkFlowArtifact)
         return ((TeamWorkFlowArtifact) sma);
      else
         return null;
   }

   @Override
   public Collection<User> getImplementers() throws OseeCoreException {
      return getImplementersByState(TaskStates.InWork.name());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   @Override
   public Date getWorldViewDeadlineDate() throws OseeCoreException {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   @Override
   public String getWorldViewDeadlineDateStr() throws OseeCoreException {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewWeeklyBenefit()
    */
   @Override
   public double getWorldViewWeeklyBenefit() {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#isWorldViewAnnualCostAvoidanceValid()
    */
   @Override
   public Result isWorldViewAnnualCostAvoidanceValid() throws OseeCoreException {
      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewLegacyPCR()
    */
   @Override
   public String getWorldViewLegacyPCR() throws OseeCoreException {
      StateMachineArtifact sma = getParentSMA();
      if (sma != null) return sma.getWorldViewLegacyPCR();
      return "";
   }

   @Override
   public String getWorldViewSWEnhancement() throws OseeCoreException {
      StateMachineArtifact sma = getParentSMA();
      if (sma != null) return sma.getWorldViewSWEnhancement();
      return "";
   }

}
