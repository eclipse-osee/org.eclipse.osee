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
import java.util.Set;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.XDecisionOptions;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class DecisionReviewArtifact extends ReviewSMArtifact implements IReviewArtifact, IWorldViewArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "Decision Review";
   public XDecisionOptions decisionOptions;
   public static enum DecisionReviewState {
      Prepare, Decision, Followup, Completed
   };

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
 * @throws OseeDataStoreException 
    */
   public DecisionReviewArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      decisionOptions = new XDecisionOptions(this);
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      Collection<TeamWorkFlowArtifact> teamArts =
            getRelatedArtifacts(AtsRelation.TeamWorkflowToReview_Team, TeamWorkFlowArtifact.class);
      if (teamArts.size() == 0) throw new OseeStateException(
            "Decision Review " + getHumanReadableId() + " has no parent workflow");
      if (teamArts.size() > 1) throw new OseeStateException(
            "Decision Review " + getHumanReadableId() + " has multiple parent workflows");
      return teamArts.iterator().next();
   }

   @Override
   public String getHelpContext() {
      return "decisionReview";
   }

   @Override
   public Set<User> getPrivilegedUsers() throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      if (getParentTeamWorkflow() != null)
         users.addAll(getParentTeamWorkflow().getPrivilegedUsers());
      else {
         if (AtsUtil.isAtsAdmin()) {
            users.add(UserManager.getUser());
         }
      }
      return users;
   }

   @Override
   public String getHyperName() {
      return getName();
   }

   @Override
   public String getWorldViewTeam() throws OseeCoreException {
      return "";
   }

   @Override
   public StateMachineArtifact getParentSMA() throws OseeCoreException {
      return getParentTeamWorkflow();
   }

   @Override
   public String getWorldViewDecision() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.DECISION_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public String getWorldViewDescription() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public String getWorldViewCategory() throws OseeCoreException {
      return "";
   }

   @Override
   public String getWorldViewCategory2() throws OseeCoreException {
      return "";
   }

   @Override
   public String getWorldViewCategory3() throws OseeCoreException {
      return "";
   }

   @Override
   public Date getWorldViewReleaseDate() throws OseeCoreException {
      return null;
   }

   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      if (getParentSMA() == null) return null;
      return getParentSMA().getWorldViewTargetedVersion();
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      StateMachineArtifact sma = getParentSMA();
      if (sma instanceof TeamWorkFlowArtifact)
         return ((TeamWorkFlowArtifact) sma).getParentActionArtifact();
      else
         return null;
   }

   @Override
   public Collection<User> getImplementers() throws OseeCoreException {
      return getImplementersByState(DecisionReviewState.Decision.name());
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
   public String getWorldViewWorkPackage() throws OseeCoreException {
      return "";
   }

   public Artifact getArtifact() {
      return this;
   }

   @Override
   public String getWorldViewReviewAuthor() throws OseeCoreException {
      return "";
   }

   @Override
   public String getWorldViewReviewDecider() throws OseeCoreException {
      return Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees(DecisionReviewState.Decision.name()));
   }

   @Override
   public String getWorldViewReviewModerator() throws OseeCoreException {
      return "";
   }

   @Override
   public String getWorldViewReviewReviewer() throws OseeCoreException {
      return "";
   }

}