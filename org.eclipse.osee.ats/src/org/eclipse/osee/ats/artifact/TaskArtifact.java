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
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class TaskArtifact extends StateMachineArtifact implements IWorldViewArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "Task";

   private SMAManager smaMgr;

   public static String INWORK_STATE = "InWork";

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public TaskArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactSubtypeDescriptor artifactType) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerSMARelation(RelationSide.SmaToTask_Sma);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#onInitializationComplete()
    */
   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      smaMgr = new SMAManager(this);
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
   public String getWorldViewRelatedToState() throws Exception {
      return getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws SQLException {
      super.atsDelete(deleteArts, allRelated);
   }

   public String getWorldViewTeam() throws Exception {
      return "";
   }

   @Override
   public StateMachineArtifact getParentSMA() throws SQLException {
      Collection<StateMachineArtifact> smas = getArtifacts(RelationSide.SmaToTask_Sma, StateMachineArtifact.class);
      if (smas.size() > 0) return smas.iterator().next();
      return null;
   }

   public Boolean isCancelled() {
      return smaMgr.isCancelled();
   }

   public Boolean isInWork() {
      return (smaMgr.getStateMgr().getCurrentStateName().equals(INWORK_STATE));
   }

   public Boolean isCompleted() {
      return smaMgr.isCompleted();
   }

   public void transitionToCancelled(String reason, boolean persist) throws IllegalStateException, SQLException {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Cancelled.name())) return;
      setSoleStringAttributeValue(ATSAttributes.CANCEL_REASON_ATTRIBUTE.getStoreName(), reason);
      Result result = smaMgr.transition(DefaultTeamState.Cancelled.name(), (User) null, persist);
      if (result.isFalse()) result.popup();
   }

   public void transitionToCompleted(boolean persist) {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Completed.name())) return;
      // Assign current user if unassigned
      try {
         if (smaMgr.getStateMgr().getAssignees().size() == 1 && smaMgr.getStateMgr().getAssignees().contains(
               SkynetAuthentication.getInstance().getUser(UserEnum.UnAssigned))) {
            smaMgr.getStateMgr().setAssignee(SkynetAuthentication.getInstance().getAuthenticatedUser());
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      Result result = smaMgr.transition(DefaultTeamState.Completed.name(), (User) null, persist);
      if (result.isFalse()) result.popup();
   }

   public void transitionToInWork(User toUser, boolean persist) throws Exception {
      if (smaMgr.getStateMgr().getCurrentStateName().equals(INWORK_STATE)) return;
      Result result = smaMgr.transition(INWORK_STATE, toUser, false);
      if (smaMgr.getStateMgr().getPercentComplete() == 100) smaMgr.getStateMgr().setPercentComplete(99);
      if (persist) smaMgr.getSma().saveSMA();
      if (result.isFalse()) result.popup();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#statusChanged()
    */
   @Override
   public void statusChanged() throws Exception {
      super.statusChanged();
      if (smaMgr.getStateMgr().getPercentComplete() == 100 && !isCompleted())
         transitionToCompleted(false);
      else if (smaMgr.getStateMgr().getPercentComplete() != 100 && isCompleted()) {
         transitionToInWork(SkynetAuthentication.getInstance().getAuthenticatedUser(), true);
      }
   }

   public void parentWorkFlowTransitioned(AtsWorkPage fromPage, AtsWorkPage toPage, Collection<User> toAssignees, boolean persist) throws Exception {
      if (toPage.isCancelledPage() && isInWork())
         transitionToCancelled("Parent Cancelled", persist);
      else if (fromPage.isCancelledPage() && isCancelled()) transitionToInWork(
            SkynetAuthentication.getInstance().getAuthenticatedUser(), persist);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewVersion()
    */
   public String getWorldViewVersion() throws Exception {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDescription()
    */
   public String getWorldViewDescription() throws Exception {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewNumberOfTasks() throws Exception {
      return "";
   }

   /**
    * @return parent SMA's date if it has one. else return task's date if it has one
    */
   public Date getWorldViewEstimatedReleaseDate() throws Exception {
      if (getParentSMA() instanceof TeamWorkFlowArtifact) return ((TeamWorkFlowArtifact) getParentSMA()).getWorldViewEstimatedReleaseDate();
      return getSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName());
   }

   public Date getWorldViewReleaseDate() throws Exception {
      if (getParentSMA() instanceof TeamWorkFlowArtifact) return ((TeamWorkFlowArtifact) getParentSMA()).getWorldViewReleaseDate();
      return getSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName());
   }

   public VersionArtifact getTargetedForVersion() throws SQLException {
      return getParentSMA().getTargetedForVersion();
   }

   @Override
   public double getWorldViewRemainHours() throws Exception {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) return 0;
      double est = getWorldViewEstimatedHours();
      if (getWorldViewStatePercentComplete() == 0) return getWorldViewEstimatedHours();
      double percent = smaMgr.getStateMgr().getPercentComplete(INWORK_STATE);
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
   public String getWorldViewImplementer() throws Exception {
      return Artifacts.commaArts(getImplementers());
   }

   public Collection<User> getImplementers() {
      return smaMgr.getStateMgr().getAssignees(INWORK_STATE);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   public Date getWorldViewDeadlineDate() throws Exception {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   public String getWorldViewDeadlineDateStr() throws Exception {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewWeeklyBenefit()
    */
   public double getWorldViewWeeklyBenefit() {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#isWorldViewAnnualCostAvoidanceValid()
    */
   @Override
   public Result isWorldViewAnnualCostAvoidanceValid() throws Exception {
      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewLegacyPCR()
    */
   public String getWorldViewLegacyPCR() throws Exception {
      StateMachineArtifact sma = getParentSMA();
      if (sma != null) return sma.getWorldViewLegacyPCR();
      return "";
   }

}
