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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.XActionableItemsDam;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkFlowArtifact extends TaskableStateMachineArtifact implements IWorldViewArtifact, IBranchArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "Team Workflow";
   private XActionableItemsDam actionableItemsDam;
   public static enum DefaultTeamState {
      Endorse, Analyze, Authorize, Implement, Completed, Cancelled
   }

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    */
   public TeamWorkFlowArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerSMAEditorRelation(AtsRelation.TeamWorkflowTargetedForVersion_Version);
      registerAtsWorldRelation(AtsRelation.TeamWorkflowToReview_Review);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getArtifactSuperTypeName()
    */
   @Override
   public String getArtifactSuperTypeName() {
      return "Team Workflow";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#saveSMA()
    */
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
         return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#isValidationRequired()
    */
   @Override
   public boolean isValidationRequired() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false);
   }

   @Override
   public int getWorldViewPercentRework() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.PERCENT_REWORK_ATTRIBUTE.getStoreName(), 0);
   }

   @Override
   public Set<User> getPrivilegedUsers() {
      Set<User> users = new HashSet<User>();
      try {
         addPriviledgedUsersUpTeamDefinitionTree(getTeamDefinition(), users);

         WorkPageDefinition workPageDefinition = smaMgr.getWorkPageDefinition();

         // Add user if allowing privileged edit to all users
         if (!users.contains(UserManager.getUser()) && (workPageDefinition.hasWorkRule(RuleWorkItemId.atsAllowPriviledgedEditToAll.name()) || getTeamDefinition().hasWorkRule(
               RuleWorkItemId.atsAllowPriviledgedEditToAll.name()))) {
            users.add(UserManager.getUser());
         }

         // Add user if user is team member and rule exists
         if (!users.contains(UserManager.getUser()) && (workPageDefinition.hasWorkRule(RuleWorkItemId.atsAllowPriviledgedEditToTeamMember.name()) || getTeamDefinition().hasWorkRule(
               RuleWorkItemId.atsAllowPriviledgedEditToTeamMember.name()))) {
            if (getTeamDefinition().getMembers().contains(UserManager.getUser())) {
               users.add(UserManager.getUser());
            }
         }

         // Add user if team member is originator and rule exists
         if (!users.contains(UserManager.getUser()) && (workPageDefinition.hasWorkRule(RuleWorkItemId.atsAllowPriviledgedEditToTeamMemberAndOriginator.name()) || getTeamDefinition().hasWorkRule(
               RuleWorkItemId.atsAllowPriviledgedEditToTeamMemberAndOriginator.name()))) {
            if (smaMgr.getOriginator().equals(UserManager.getUser()) && getTeamDefinition().getMembers().contains(
                  UserManager.getUser())) {
               users.add(UserManager.getUser());
            }
         }

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return users;
   }

   private void addPriviledgedUsersUpTeamDefinitionTree(TeamDefinitionArtifact tda, Set<User> users) throws OseeCoreException {
      users.addAll(tda.getLeads());
      users.addAll(tda.getPrivilegedMembers());

      // Walk up tree to get other editors
      if (tda.getParent() != null && (tda.getParent() instanceof TeamDefinitionArtifact)) {
         addPriviledgedUsersUpTeamDefinitionTree((TeamDefinitionArtifact) tda.getParent(), users);
      }
   }

   @Override
   public String getEditorTitle() throws OseeCoreException {
      return getTeamTitle();
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      initializeSMA();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#initializeSMA()
    */
   @Override
   protected void initializeSMA() {
      super.initializeSMA();
      actionableItemsDam = new XActionableItemsDam(this);
   }

   public ChangeType getChangeType() throws OseeCoreException {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   public void setChangeType(ChangeType type) throws OseeCoreException {
      setSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), type.name());
   }

   public PriorityType getPriority() throws OseeCoreException {
      return PriorityType.getPriority(getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   public void setPriority(PriorityType type) throws OseeCoreException {
      setSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), type.getShortName());
   }

   /**
    * @return Returns the actionableItemsDam.
    */
   public XActionableItemsDam getActionableItemsDam() {
      return actionableItemsDam;
   }

   public void setTeamDefinition(TeamDefinitionArtifact tda) throws OseeCoreException {
      this.setSoleAttributeValue(ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(), tda.getGuid());
   }

   public TeamDefinitionArtifact getTeamDefinition() throws OseeCoreException, OseeCoreException {
      String guid = this.getSoleAttributeValue(ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(), "");
      if (guid == null || guid.equals("")) throw new IllegalArgumentException(
            "TeamWorkflow has no TeamDefinition associated.");
      return AtsCache.getTeamDefinitionArtifact(guid);
   }

   public String getTeamTitle() throws OseeCoreException {
      if (getWorldViewTargetedVersion() != null) {
         return "[" + getTeamName() + "][" + getWorldViewTargetedVersionStr() + "] - " + getDescriptiveName();
      } else {
         return "[" + getTeamName() + "] - " + getDescriptiveName();
      }
   }

   public String getTeamName() {
      try {
         return getTeamDefinition().getDescriptiveName();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   @Override
   public String getWorldViewType() throws OseeCoreException {
      return getTeamName() + " Workflow";
   }

   @Override
   public ChangeType getWorldViewChangeType() throws OseeCoreException {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   @Override
   public String getWorldViewPriority() throws OseeCoreException {
      return PriorityType.getPriority(getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), "")).getShortName();
   }

   @Override
   public String getWorldViewUserCommunity() throws OseeCoreException {
      return getAttributesToString(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName());
   }

   @Override
   public String getWorldViewActionableItems() throws OseeCoreException {
      return getActionableItemsDam().getActionableItemsStr();
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
      for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviews())
         reviewArt.atsDelete(deleteArts, allRelated);
   }

   @Override
   public String getWorldViewTeam() throws OseeCoreException {
      return getTeamName();
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      Collection<ActionArtifact> arts = getRelatedArtifacts(AtsRelation.ActionToWorkflow_Action, ActionArtifact.class);
      if (arts.size() == 0) {
         throw new OseeStateException("Team " + getHumanReadableId() + " has no parent Action");
      } else if (arts.size() > 1) {
         throw new OseeStateException("Team " + getHumanReadableId() + " has multiple parent Actions");
      } else
         return arts.iterator().next();
   }

   @Override
   public String getWorldViewTargetedVersionStr() throws OseeCoreException {
      Collection<VersionArtifact> verArts =
            getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Version, VersionArtifact.class);
      if (verArts.size() == 0) return "";
      if (verArts.size() > 1) {
         String errStr =
               "Workflow " + smaMgr.getSma().getHumanReadableId() + " targeted for multiple versions: " + Artifacts.commaArts(verArts);
         OseeLog.log(AtsPlugin.class, Level.SEVERE, errStr, null);
         return XViewerCells.getCellExceptionString(errStr);
      }
      VersionArtifact verArt = verArts.iterator().next();
      if (!smaMgr.isCompleted() && !smaMgr.isCancelled() && verArt.getSoleAttributeValue(
            ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), false)) {
         String errStr =
               "Workflow " + smaMgr.getSma().getHumanReadableId() + " targeted for released version, but not completed: " + verArt;
         OseeLog.log(AtsPlugin.class, Level.SEVERE, errStr, null);
         return XViewerCells.getCellExceptionString(errStr);
      }
      return verArt.getDescriptiveName();
   }

   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      if (getRelatedArtifactsCount(AtsRelation.TeamWorkflowTargetedForVersion_Version) > 0) {
         return (VersionArtifact) getRelatedArtifact(AtsRelation.TeamWorkflowTargetedForVersion_Version);
      }
      return null;
   }

   @Override
   public String getHyperName() {
      return getTeamName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperTargetVersion()
    */
   @Override
   public String getHyperTargetVersion() {
      try {
         return getWorldViewTargetedVersionStr().equals("") ? null : getWorldViewTargetedVersionStr();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
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

   public Result addActionableItems() {
      Result toReturn = Result.FalseResult;
      AICheckTreeDialog diag =
            new AICheckTreeDialog(
                  "Add/Remove Impacted Actionable Items",
                  "Select/De-Select Impacted Actionable Items\n\n" + "Note: At least one Actionable Item must remain.\nTeam should be cancelled if no impact exists.",
                  Active.Both);

      try {
         diag.setInput(getTeamDefinition().getRelatedArtifacts(AtsRelation.TeamActionableItem_ActionableItem,
               ActionableItemArtifact.class));
         diag.setInitialSelections(actionableItemsDam.getActionableItems());
         if (diag.open() != 0) {
            toReturn = new Result("Add/Remove Cancelled");
         } else if (diag.getChecked().size() == 0) {
            toReturn = new Result("At least one actionable item must remain.");
         } else {
            Set<ActionableItemArtifact> selectedAlias = new HashSet<ActionableItemArtifact>();
            for (Object obj : diag.getChecked()) {
               selectedAlias.add((ActionableItemArtifact) obj);
            }

            toReturn = actionableItemsTx(AtsPlugin.getAtsBranch(), selectedAlias, null);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         toReturn = Result.FalseResult;
      }
      return toReturn;
   }

   public Result convertActionableItems() throws OseeCoreException {
      Result toReturn = Result.FalseResult;
      AICheckTreeDialog diag =
            new AICheckTreeDialog("Convert Impacted Actionable Items",
                  "NOTE: This should NOT be the normal path to changing actionable items.\n\nIf a team has " +
                  //
                  "determined " + "that there is NO impact and that another actionable items IS impacted:\n" +
                  //
                  "   1) Cancel this operation\n" + "   2) Select \"Edit Actionable Items\" to add/remove " +
                  //
                  "impacted items \n" + "      which will create new teams as needed.\n" +
                  //
                  "   3) Then cancel the team that has no impacts.\n   Doing this will show that the original " +
                  //
                  "team analyzed the impact\n" + "   and determined that there was no change.\n\n" + "However, " +
                  //
                  "there are some cases where an impacted item was incorrectly chosen\n" + "and the original team " +
                  //
                  "does not need to do anything, this dialog will purge the\n" + "team from the DB as if it was " +
                  //
                  "never chosen.\n\n" + "Current Actionable Item(s): " + getWorldViewActionableItems() + "\n" +
                  //
                  "Current Team: " + getTeamDefinition().getDescriptiveName() + "\n" +
                  //
                  "Select SINGLE Actionable Item below to convert this workflow to.\n\n" +
                  //
                  "You will be prompted to confirm this conversion.", Active.Both);

      diag.setInput(ActionableItemArtifact.getTopLevelActionableItems(Active.Both));
      if (diag.open() != 0) return Result.FalseResult;
      if (diag.getChecked().size() == 0) return new Result("At least one actionable item must must be selected.");
      if (diag.getChecked().size() > 1) return new Result("Only ONE actionable item can be selected for converts");
      ActionableItemArtifact selectedAia = diag.getChecked().iterator().next();
      Collection<TeamDefinitionArtifact> teamDefs =
            ActionableItemArtifact.getImpactedTeamDefs(Arrays.asList(selectedAia));
      if (teamDefs.size() != 1) {
         toReturn = new Result("Single team can not retrieved for " + selectedAia.getDescriptiveName());
      } else {
         TeamDefinitionArtifact newTeamDef = teamDefs.iterator().next();
         if (newTeamDef.equals(getTeamDefinition())) {
            toReturn =
                  new Result(
                        "Actionable Item selected belongs to same team as currently selected team.\n" + "Use \"Edit Actionable Items\" instaed.");
         } else {
            StringBuffer sb = new StringBuffer();
            sb.append("Converting...");
            sb.append("\nActionable Item(s): " + getWorldViewActionableItems());
            sb.append("\nTeam: " + getTeamDefinition().getDescriptiveName());
            sb.append("\nto\nActionable Item(s): " + selectedAia);
            sb.append("\nTeam: " + newTeamDef.getDescriptiveName());
            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Confirm Convert", sb.toString())) {
               Set<ActionableItemArtifact> toProcess = new HashSet<ActionableItemArtifact>();
               toProcess.add(selectedAia);
               toReturn = actionableItemsTx(AtsPlugin.getAtsBranch(), toProcess, newTeamDef);
            }
         }
      }
      return toReturn;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentSMA()
    */
   @Override
   public StateMachineArtifact getParentSMA() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentAtsArtifact()
    */
   @Override
   public Artifact getParentAtsArtifact() throws OseeCoreException {
      return getParentActionArtifact();
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

   /**
    * If targeted for version exists, return that estimated date. Else, if attribute is set, return that date. Else
    * null.
    */
   @Override
   public Date getWorldViewEstimatedReleaseDate() throws OseeCoreException {
      Collection<VersionArtifact> vers =
            getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Version, VersionArtifact.class);
      Date date = null;
      if (vers.size() > 0) {
         date = vers.iterator().next().getEstimatedReleaseDate();
         if (date == null) {
            date = getSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
         }
      } else
         date = getSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
      return date;
   }

   /**
    * If targeted for version exists, return that estimated date. Else, if attribute is set, return that date. Else
    * null.
    */
   @Override
   public Date getWorldViewReleaseDate() throws OseeCoreException {
      Collection<VersionArtifact> vers =
            getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Version, VersionArtifact.class);
      Date date = null;
      if (vers.size() > 0) {
         date = vers.iterator().next().getReleaseDate();
         if (date == null) {
            date = getSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
         }
      } else
         date = getSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
      return date;
   }

   @Override
   public Collection<User> getImplementers() throws OseeCoreException {
      return getImplementersByState(DefaultTeamState.Implement.name());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   @Override
   public String getWorldViewDeadlineDateStr() throws OseeCoreException {
      Date date = getWorldViewDeadlineDate();
      if (date != null) {
         return XDate.getDateStr(date, XDate.MMDDYY);
      }
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   @Override
   public Date getWorldViewDeadlineDate() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName(), null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewWeeklyBenefit()
    */
   @Override
   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      if (isAttributeTypeValid(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE.getStoreName())) return 0;
      String value = getSoleAttributeValue(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE.getStoreName(), "");
      if (value == null || value.equals("")) return 0;
      return new Float(value).doubleValue();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewAnnualCostAvoidance()
    */
   @Override
   public double getWorldViewAnnualCostAvoidance() throws OseeCoreException {
      double benefit = getWorldViewWeeklyBenefit();
      double remainHrs = getRemainHoursTotal();
      return (benefit * 52) - remainHrs;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentTeamWorkflow()
    */
   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      return this;
   }

   private Result actionableItemsTx(Branch branch, Set<ActionableItemArtifact> selectedAlias, TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      Result workResult = actionableItemsDam.setActionableItems(selectedAlias);
      if (workResult.isTrue()) {
         if (teamDefinition != null) setTeamDefinition(teamDefinition);
         SkynetTransaction transaction = new SkynetTransaction(branch);
         getParentActionArtifact().resetAttributesOffChildren(transaction);
         persistAttributes(transaction);
         transaction.execute();
      }
      return workResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewBranchStatus()
    */
   @Override
   public String getWorldViewBranchStatus() throws OseeCoreException {
      try {
         if (getSmaMgr().getBranchMgr().isWorkingBranch())
            return "Working";
         else if (getSmaMgr().getBranchMgr().isCommittedBranch()) return "Committed";
         return "";
      } catch (Exception ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact#getArtifact()
    */
   public Artifact getArtifact() {
      return this;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact#getCommitManagerBranch()
    */
   public Branch getWorkingBranch() throws OseeCoreException {
      if (getSmaMgr().getBranchMgr().getWorkingBranch() != null) {
         return getSmaMgr().getBranchMgr().getWorkingBranch();
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewParentID()
    */
   @Override
   public String getWorldViewParentID() throws OseeCoreException {
      return getParentActionArtifact().getHumanReadableId();
   }

}
