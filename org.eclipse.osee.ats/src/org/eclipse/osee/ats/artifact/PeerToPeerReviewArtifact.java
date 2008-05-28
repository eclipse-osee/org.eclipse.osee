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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.defect.DefectManager;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewArtifact extends ReviewSMArtifact implements IReviewArtifact, IWorldViewArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "PeerToPeer Review";
   public static enum State {
      Prepare, Review, Completed
   };

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public PeerToPeerReviewArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerSMARelation(CoreRelationEnumeration.TeamWorkflowToReview_Team);
      defectManager = new DefectManager(this);
   }

   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      try {
         Set<TeamWorkFlowArtifact> teams =
               getArtifacts(CoreRelationEnumeration.TeamWorkflowToReview_Team, TeamWorkFlowArtifact.class);
         if (teams.size() > 0) return teams.iterator().next();
         return null;
      } catch (SQLException ex) {
         return null;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.ReviewSMArtifact#isUserRoleValid()
    */
   @Override
   public Result isUserRoleValid() throws SQLException, MultipleAttributesExist {
      if (getUserRoleManager().getUserRoles(Role.Author).size() <= 0) return new Result("Must have at least one Author");
      if (getUserRoleManager().getUserRoles(Role.Reviewer).size() <= 0) return new Result(
            "Must have at least one Reviewer");
      // If in review state, all roles must have hours spent entered
      if (smaMgr.getStateMgr().getCurrentStateName().equals(PeerToPeerReviewArtifact.State.Review.name())) {
         for (UserRole uRole : userRoleManager.getUserRoles()) {
            if (uRole.getHoursSpent() == null) return new Result("Hours spent must be entered for each role.");
         }
      }
      return super.isUserRoleValid();
   }

   @Override
   public String getHelpContext() {
      return "peerToPeerReview";
   }

   public String getWorldViewVersion() throws Exception {
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
   public String getWorldViewTeam() throws Exception {
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
    * @see osee.ats.world.IWorldViewArtifact#getWorldViewDescription()
    */
   public String getWorldViewDescription() throws Exception {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewCategory() throws Exception {
      return "";
   }

   public String getWorldViewCategory2() throws Exception {
      return "";
   }

   public String getWorldViewCategory3() throws Exception {
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
      if (getParentSMA() == null) return null;
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
   public String getWorldViewImplementer() throws Exception {
      return Artifacts.commaArts(smaMgr.getStateMgr().getAssignees(State.Review.name()));
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
   public String getWorldViewDeadlineDateStr() throws Exception {
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
   public String getWorldViewWorkPackage() throws Exception {
      return "";
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
   public String getWorldViewReviewAuthor() throws Exception {
      return Artifacts.commaArts(getUserRoleManager().getRoleUsers(Role.Author));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewDecider()
    */
   public String getWorldViewReviewDecider() throws Exception {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewModerator()
    */
   public String getWorldViewReviewModerator() throws Exception {
      return Artifacts.commaArts(getUserRoleManager().getRoleUsers(Role.Moderator));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewReviewer()
    */
   public String getWorldViewReviewReviewer() throws Exception {
      return Artifacts.commaArts(getUserRoleManager().getRoleUsers(Role.Reviewer));
   }

}
