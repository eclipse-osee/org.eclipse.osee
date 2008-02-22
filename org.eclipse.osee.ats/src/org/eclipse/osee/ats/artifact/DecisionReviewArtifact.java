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
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XDecisionOptions;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class DecisionReviewArtifact extends ReviewSMArtifact implements IReviewArtifact, IWorldViewArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "Decision Review";
   public XDecisionOptions decisionOptions;
   public static enum StateNames {
      Prepare, Decision, Followup, Completed
   };

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public DecisionReviewArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
      registerSMARelation(RelationSide.TeamWorkflowToReview_Team);
      decisionOptions = new XDecisionOptions(this);
   }

   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      try {
         Collection<TeamWorkFlowArtifact> teamArts =
               getArtifacts(RelationSide.TeamWorkflowToReview_Team, TeamWorkFlowArtifact.class);
         if (teamArts.size() == 0) throw new IllegalStateException(
               "Decision Review " + getHumanReadableId() + " has no parent workflow");
         if (teamArts.size() > 1) throw new IllegalStateException(
               "Decision Review " + getHumanReadableId() + " has multiple parent workflows");
         return teamArts.iterator().next();
      } catch (SQLException ex) {
         return null;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#isCurrentSectionExpanded(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isCurrentSectionExpanded(AtsWorkPage page) {
      // Always expand the decision state
      if (page.getName().endsWith(StateNames.Decision.name())) return true;
      // If current state is decision and this is prepare state, don't expand the Prepare state
      if (getCurrentStateName().equals(StateNames.Decision.name()) && page.getName().contains(StateNames.Prepare.name())) return false;
      return super.isCurrentSectionExpanded(page);
   }

   @Override
   public String getHelpContext() {
      return "decisionReview";
   }

   public String getWorldViewVersion() {
      return "";
   }

   @Override
   public Set<User> getPrivilegedUsers() throws SQLException {
      Set<User> users = new HashSet<User>();
      if (getParentTeamWorkflow() != null)
         users.addAll(getParentTeamWorkflow().getPrivilegedUsers());
      else {
         if (AtsPlugin.isAtsAdmin()) {
            users.add(SkynetAuthentication.getInstance().getAuthenticatedUser());
         }
      }
      return users;
   }

   public int getWorldViewTotalPercentComplete() {
      if (smaMgr.isCompleted()) return 100;
      if (smaMgr.isCancelled()) return 0;
      SMAState state = smaMgr.getSMAState(StateNames.Decision.name(), false);
      if (state != null) return state.getPercentComplete();
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.artifact.StateMachineArtifact#isTaskable()
    */
   @Override
   public boolean isTaskable() {
      return false;
   }

   @Override
   public String getHyperName() {
      return getDescriptiveName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewTeam()
    */
   public String getWorldViewTeam() {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.artifact.StateMachineArtifact#getParentSMA()
    */
   @Override
   public StateMachineArtifact getParentSMA() throws SQLException {
      return getParentTeamWorkflow();
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewDecision()
    */
   public String getWorldViewDecision() {
      return getSoleAttributeValue(ATSAttributes.DECISION_ATTRIBUTE.getStoreName());
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewDescription()
    */
   public String getWorldViewDescription() {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName());
   }

   public String getWorldViewCategory() {
      return "";
   }

   public String getWorldViewCategory2() {
      return "";
   }

   public String getWorldViewCategory3() {
      return "";
   }

   public Date getWorldViewEstimatedReleaseDate() throws Exception {
      return null;
   }

   public Date getWorldViewReleaseDate() throws Exception {
      return null;
   }

   @Override
   public VersionArtifact getTargetedForVersion() throws SQLException {
      return getParentSMA().getTargetedForVersion();
   }

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
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewImplementer()
    */
   public String getWorldViewImplementer() {
      SMAState state = getStateDam().getState(StateNames.Decision.name(), false);
      if (state != null) Artifacts.commaArts(state.getAssignees());
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   public Date getWorldViewDeadlineDate() throws Exception {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   public String getWorldViewDeadlineDateStr() {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewWeeklyBenefit()
    */
   public double getWorldViewWeeklyBenefit() {
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewWorkPackage()
    */
   public String getWorldViewWorkPackage() {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.world.IWorldViewArtifact#isMetricsFromTasks()
    */
   public boolean isMetricsFromTasks() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.ats.IReviewArtifact#getArtifact()
    */
   public Artifact getArtifact() {
      return this;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewAuthor()
    */
   public String getWorldViewReviewAuthor() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewDecider()
    */
   public String getWorldViewReviewDecider() {
      SMAState state = getStateDam().getState(StateNames.Decision.name(), false);
      if (state != null) return Artifacts.commaArts(state.getAssignees());
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewModerator()
    */
   public String getWorldViewReviewModerator() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewReviewer()
    */
   public String getWorldViewReviewReviewer() {
      return "";
   }

}
