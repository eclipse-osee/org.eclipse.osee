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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
    * @throws SQLException
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
    * Allow parent SMA's assignees and all priviledged users up Team tree
    */
   @Override
   public Set<User> getPrivilegedUsers() throws SQLException {
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

   @Override
   public String getDescription() {
      try {
         return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public String getWorldViewRelatedToState() throws OseeCoreException, SQLException {
      return getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException, SQLException {
      super.atsDelete(deleteArts, allRelated);
   }

   @Override
   public String getWorldViewTeam() throws OseeCoreException, SQLException {
      return "";
   }

   @Override
   public StateMachineArtifact getParentSMA() throws SQLException {
      Collection<StateMachineArtifact> smas = getArtifacts(AtsRelation.SmaToTask_Sma, StateMachineArtifact.class);
      if (smas.size() > 0) return smas.iterator().next();
      return null;
   }

   public Boolean isCancelled() {
      return smaMgr.isCancelled();
   }

   public Boolean isInWork() {
      return (smaMgr.getStateMgr().getCurrentStateName().equals(TaskStates.InWork.name()));
   }

   public Boolean isCompleted() {
      return smaMgr.isCompleted();
   }

   public void transitionToCancelled(String reason, boolean persist) throws OseeCoreException, SQLException {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Cancelled.name())) return;
      setSoleAttributeValue(ATSAttributes.CANCEL_REASON_ATTRIBUTE.getStoreName(), reason);
      Result result = smaMgr.transition(DefaultTeamState.Cancelled.name(), (User) null, persist);
      if (result.isFalse()) result.popup();
   }

   public void transitionToCompleted(boolean persist) {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Completed.name())) return;
      // Assign current user if unassigned
      try {
         if (smaMgr.getStateMgr().getAssignees().size() == 1 && smaMgr.getStateMgr().getAssignees().contains(
               SkynetAuthentication.getUser(UserEnum.UnAssigned))) {
            smaMgr.getStateMgr().setAssignee(SkynetAuthentication.getUser());
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      Result result = smaMgr.transition(DefaultTeamState.Completed.name(), (User) null, persist);
      if (result.isFalse()) result.popup();
   }

   public void transitionToInWork(User toUser, boolean persist) throws OseeCoreException, SQLException {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(TaskStates.InWork.name())) return;
      Result result = smaMgr.transition(TaskStates.InWork.name(), toUser, false);
      if (smaMgr.getStateMgr().getPercentComplete() == 100) {
         smaMgr.getStateMgr().updateMetrics(0, 99, true);
      }
      if (persist) smaMgr.getSma().saveSMA();
      if (result.isFalse()) result.popup();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#statusChanged()
    */
   @Override
   public void statusChanged() throws OseeCoreException, SQLException {
      super.statusChanged();
      if (smaMgr.getStateMgr().getPercentComplete() == 100 && !isCompleted())
         transitionToCompleted(false);
      else if (smaMgr.getStateMgr().getPercentComplete() != 100 && isCompleted()) {
         transitionToInWork(SkynetAuthentication.getUser(), true);
      }
   }

   public void parentWorkFlowTransitioned(WorkPageDefinition fromWorkPageDefinition, WorkPageDefinition toWorkPageDefinition, Collection<User> toAssignees, boolean persist) throws OseeCoreException, SQLException {
      if (toWorkPageDefinition.getPageName().equals(DefaultTeamState.Cancelled.name()) && isInWork())
         transitionToCancelled("Parent Cancelled", persist);
      else if (fromWorkPageDefinition.getPageName().equals(DefaultTeamState.Cancelled.name()) && isCancelled()) transitionToInWork(
            SkynetAuthentication.getUser(), persist);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewVersion()
    */
   @Override
   public String getWorldViewVersion() throws OseeCoreException, SQLException {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDescription()
    */
   @Override
   public String getWorldViewDescription() throws OseeCoreException, SQLException {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public String getWorldViewNumberOfTasks() throws OseeCoreException, SQLException {
      return "";
   }

   /**
    * @return parent SMA's date if it has one. else return task's date if it has one
    */
   @Override
   public Date getWorldViewEstimatedReleaseDate() throws OseeCoreException, SQLException {
      if (getParentSMA() instanceof TeamWorkFlowArtifact) return ((TeamWorkFlowArtifact) getParentSMA()).getWorldViewEstimatedReleaseDate();
      return getSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName());
   }

   @Override
   public Date getWorldViewReleaseDate() throws OseeCoreException, SQLException {
      if (getParentSMA() instanceof TeamWorkFlowArtifact) return ((TeamWorkFlowArtifact) getParentSMA()).getWorldViewReleaseDate();
      return getSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName());
   }

   @Override
   public VersionArtifact getTargetedForVersion() throws SQLException {
      return getParentSMA().getTargetedForVersion();
   }

   @Override
   public double getWorldViewRemainHours() throws OseeCoreException, SQLException {
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
   public ActionArtifact getParentActionArtifact() throws SQLException {
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
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws SQLException {
      StateMachineArtifact sma = getParentSMA();
      if (sma instanceof TeamWorkFlowArtifact)
         return ((TeamWorkFlowArtifact) sma);
      else
         return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewImplementer()
    */
   @Override
   public String getWorldViewImplementer() throws OseeCoreException, SQLException {
      return Artifacts.toString("; ", getImplementers());
   }

   public Collection<User> getImplementers() {
      return smaMgr.getStateMgr().getAssignees(TaskStates.InWork.name());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   @Override
   public Date getWorldViewDeadlineDate() throws OseeCoreException, SQLException {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   @Override
   public String getWorldViewDeadlineDateStr() throws OseeCoreException, SQLException {
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
   public Result isWorldViewAnnualCostAvoidanceValid() throws OseeCoreException, SQLException {
      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewLegacyPCR()
    */
   @Override
   public String getWorldViewLegacyPCR() throws OseeCoreException, SQLException {
      StateMachineArtifact sma = getParentSMA();
      if (sma != null) return sma.getWorldViewLegacyPCR();
      return "";
   }

   @Override
   public String getWorldViewSWEnhancement() throws OseeCoreException, SQLException {
      StateMachineArtifact sma = getParentSMA();
      if (sma != null) return sma.getWorldViewSWEnhancement();
      return "";
   }

}
