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
package org.eclipse.osee.ats.core.client.review;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.review.role.UserRole;
import org.eclipse.osee.ats.core.client.review.role.UserRoleManager;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.core.client.workflow.StateManager;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractReviewArtifact extends AbstractTaskableArtifact {

   private ActionableItemManagerCore actionableItemsDam;
   private Boolean standAlone = null;

   public AbstractReviewArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      actionableItemsDam = new ActionableItemManagerCore(this);
   }

   @Override
   public List<IBasicUser> getImplementers() throws OseeCoreException {
      if (this.isOfType(AtsArtifactTypes.DecisionReview)) {
         return StateManager.getImplementersByState(this, DecisionReviewState.Decision);
      } else {
         List<IBasicUser> users = StateManager.getImplementersByState(this, PeerToPeerReviewState.Review);
         for (UserRole role : UserRoleManager.getUserRoles(this)) {
            if (!users.contains(role.getUser())) {
               users.add(role.getUser());
            }
         }
         return users;
      }
   }

   @Override
   public String getArtifactSuperTypeName() {
      return "Review";
   }

   public boolean isBlocking() throws OseeCoreException {
      return getReviewBlockType() != ReviewBlockType.None;
   }

   public ReviewBlockType getReviewBlockType() throws OseeCoreException {
      String typeStr = getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, null);
      if (typeStr == null) {
         return ReviewBlockType.None;
      }
      return ReviewBlockType.valueOf(typeStr);
   }

   public Set<TeamDefinitionArtifact> getCorrespondingTeamDefinitionArtifact() throws OseeCoreException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      if (getParentTeamWorkflow() != null) {
         teamDefs.add(getParentTeamWorkflow().getTeamDefinition());
      }
      if (getActionableItemsDam().getActionableItems().size() > 0) {
         teamDefs.addAll(ActionableItemManagerCore.getImpactedTeamDefs(getActionableItemsDam().getActionableItems()));
      }
      return teamDefs;
   }

   public ActionableItemManagerCore getActionableItemsDam() {
      if (actionableItemsDam == null) {
         actionableItemsDam = new ActionableItemManagerCore(this);
      }
      return actionableItemsDam;
   }

   @Override
   public AbstractWorkflowArtifact getParentAWA() throws OseeCoreException {
      if (isStandAloneReview()) {
         return null;
      }
      if (parentAwa != null) {
         return parentAwa;
      }
      parentAwa = getParentTeamWorkflow();
      return parentAwa;
   }

   @Override
   public Artifact getParentActionArtifact() throws OseeCoreException {
      if (isStandAloneReview()) {
         return null;
      }
      if (parentAction != null) {
         return parentAction;
      }
      parentTeamArt = getParentTeamWorkflow();
      if (parentTeamArt != null) {
         parentAction = parentTeamArt.getParentActionArtifact();
      }
      return parentAction;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      if (isStandAloneReview()) {
         return null;
      }
      if (parentTeamArt != null) {
         return parentTeamArt;
      }
      List<TeamWorkFlowArtifact> teams =
         getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Team, TeamWorkFlowArtifact.class);
      if (teams.size() > 1) {
         OseeLog.log(Activator.class, Level.SEVERE,
            getArtifactTypeName() + " " + getHumanReadableId() + " has multiple parent workflows");
      } else if (!isStandAloneReview() && teams.isEmpty()) {
         OseeLog.log(Activator.class, Level.SEVERE,
            getArtifactTypeName() + " " + getHumanReadableId() + " has no parent workflow");
      }
      if (!teams.isEmpty()) {
         parentTeamArt = teams.iterator().next();
      }
      return parentTeamArt;
   }

   public boolean isStandAloneReview() throws OseeCoreException {
      if (standAlone == null) {
         standAlone = getActionableItemsDam().getActionableItemGuids().size() > 0;
      }
      return standAlone;
   }

   @Override
   public double getWorldViewWeeklyBenefit() {
      return 0;
   }

   public Artifact getArtifact() {
      return this;
   }

   public static AbstractReviewArtifact cast(Artifact artifact) {
      if (artifact instanceof AbstractReviewArtifact) {
         return (AbstractReviewArtifact) artifact;
      }
      return null;
   }

}
