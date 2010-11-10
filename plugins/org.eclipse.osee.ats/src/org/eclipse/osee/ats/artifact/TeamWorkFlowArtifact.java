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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.StateManager;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.XActionableItemsDam;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkFlowArtifact extends AbstractTaskableArtifact implements IBranchArtifact, IATSStateMachineArtifact {

   private XActionableItemsDam actionableItemsDam;
   private final AtsBranchManager branchMgr;

   public TeamWorkFlowArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerAtsWorldRelation(AtsRelationTypes.TeamWorkflowToReview_Review);
      branchMgr = new AtsBranchManager(this);
   }

   @Override
   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) throws OseeCoreException {
      super.getSmaArtifactsOneLevel(smaArtifact, artifacts);
      try {
         if (getTargetedVersion() != null) {
            artifacts.add(getTargetedVersion());
         }
         artifacts.addAll(ReviewManager.getReviews(this));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getArtifactSuperTypeName() {
      return "Team Workflow";
   }

   @Override
   public void saveSMA(SkynetTransaction transaction) {
      super.saveSMA(transaction);
      try {
         getParentActionArtifact().resetAttributesOffChildren(transaction);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't reset Action parent of children", ex);
      }
   }

   @Override
   public String getDescription() {
      try {
         return getSoleAttributeValue(AtsAttributeTypes.Description, "");
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public boolean isValidationRequired() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, false);
   }

   @Override
   public int getWorldViewPercentRework() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.PercentRework, 0);
   }

   @Override
   public String getEditorTitle() throws OseeCoreException {
      try {
         if (getTargetedVersion() != null) {
            return getType() + ": " + "[" + getTargetedVersionStr() + "] - " + getName();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return super.getEditorTitle();
   }

   @Override
   public void onInitializationComplete() throws OseeCoreException {
      super.onInitializationComplete();
      initializeSMA();
   }

   @Override
   protected void initializeSMA() throws OseeCoreException {
      super.initializeSMA();
      actionableItemsDam = new XActionableItemsDam(this);
   }

   public XActionableItemsDam getActionableItemsDam() {
      return actionableItemsDam;
   }

   public void setTeamDefinition(TeamDefinitionArtifact tda) throws OseeCoreException {
      this.setSoleAttributeValue(AtsAttributeTypes.TeamDefinition, tda.getGuid());
   }

   public TeamDefinitionArtifact getTeamDefinition() throws OseeCoreException, OseeCoreException {
      String guid = this.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, "");
      if (!Strings.isValid(guid)) {
         throw new OseeArgumentException("TeamWorkflow [%s] has no TeamDefinition associated.", getHumanReadableId());
      }
      return AtsCacheManager.getTeamDefinitionArtifact(guid);
   }

   public String getTeamName() {
      try {
         return getTeamDefinition().getName();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   @Override
   public String getType() {
      return getTeamName() + " Workflow";
   }

   @Override
   public String getWorldViewPriority() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.PriorityType, "");
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
      for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(this)) {
         reviewArt.atsDelete(deleteArts, allRelated);
      }
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      parentTeamArt = this;
      return parentTeamArt;
   }

   @Override
   public Artifact getParentAtsArtifact() throws OseeCoreException {
      return getParentActionArtifact();
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      if (parentAction != null) {
         return parentAction;
      }
      Collection<ActionArtifact> arts =
         getRelatedArtifacts(AtsRelationTypes.ActionToWorkflow_Action, ActionArtifact.class);
      if (arts.isEmpty()) {
         throw new OseeStateException("Team [%s] has no parent Action", getGuid());
      } else if (arts.size() > 1) {
         throw new OseeStateException("Team [%s] has multiple parent Actions", getGuid());
      }
      parentAction = arts.iterator().next();
      return parentAction;
   }

   @Override
   public AbstractWorkflowArtifact getParentSMA() {
      return null;
   }

   @Override
   public double getManHrsPerDayPreference() throws OseeCoreException {
      try {
         return getTeamDefinition().getManDayHrsFromItemAndChildren();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return super.getManHrsPerDayPreference();
   }

   @Override
   public Collection<User> getImplementers() throws OseeCoreException {
      return StateManager.getImplementersByState(this, DefaultTeamState.Implement.name());
   }

   @Override
   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      if (isAttributeTypeValid(AtsAttributeTypes.WeeklyBenefit)) {
         return 0;
      }
      String value = getSoleAttributeValue(AtsAttributeTypes.WeeklyBenefit, "");
      if (!Strings.isValid(value)) {
         return 0;
      }
      return new Float(value).doubleValue();
   }

   @Override
   public String getWorldViewBranchStatus() {
      try {
         if (getBranchMgr().isWorkingBranchInWork()) {
            return "Working";
         } else if (getBranchMgr().isCommittedBranchExists()) {
            if (!getBranchMgr().isAllObjectsToCommitToConfigured() || !getBranchMgr().isBranchesAllCommitted()) {
               return "Needs Commit";
            }
            return "Committed";
         }
         return "";
      } catch (Exception ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public Artifact getArtifact() {
      return this;
   }

   @Override
   public Branch getWorkingBranch() throws OseeCoreException {
      return getBranchMgr().getWorkingBranch();
   }

   public AtsBranchManager getBranchMgr() {
      return branchMgr;
   }

   /**
    * 5-9 character short name for UI and display purposes
    */
   public String getArtifactTypeShortName() {
      return "";
   }

   public String getBranchName() {
      String smaTitle = getName();
      if (smaTitle.length() > 40) {
         smaTitle = smaTitle.substring(0, 39) + "...";
      }
      if (Strings.isValid(getArtifactTypeShortName())) {
         return String.format("%s - %s - %s", getHumanReadableId(), getArtifactTypeShortName(), smaTitle);
      } else {
         return String.format("%s - %s", getHumanReadableId(), smaTitle);
      }
   }

}
