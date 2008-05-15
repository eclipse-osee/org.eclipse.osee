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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.XActionableItemsDam;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkFlowArtifact extends StateMachineArtifact implements IWorldViewArtifact, IBranchArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "Team Workflow";
   private XActionableItemsDam actionableItemsDam;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public TeamWorkFlowArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactSubtypeDescriptor artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerSMARelation(RelationSide.SmaToTask_Task);
      registerSMARelation(RelationSide.TeamWorkflowToReview_Review);
      registerSMARelation(RelationSide.TeamWorkflowTargetedForVersion_Version);
   }

   @Override
   public boolean showTaskTab() {
      return (isTaskable() || smaMgr.isCompleted());
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
   public void saveSMA() {
      super.saveSMA();
      try {
         getParentActionArtifact().resetAttributesOffChildren();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't reset Action parent of children", ex, true);
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
   public boolean isValidationRequired() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false);
   }

   @Override
   public int getWorldViewPercentRework() throws Exception {
      return getSoleAttributeValue(ATSAttributes.PERCENT_REWORK_ATTRIBUTE.getStoreName(), 0);
   }

   @Override
   public Set<User> getPrivilegedUsers() {
      Set<User> users = new HashSet<User>();
      try {
         addLeadUsersUpTree(getTeamDefinition(), users);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return users;
   }

   private void addLeadUsersUpTree(TeamDefinitionArtifact tda, Set<User> users) {
      try {
         users.addAll(tda.getLeads());
         if (tda.getParent() != null && (tda.getParent() instanceof TeamDefinitionArtifact)) addLeadUsersUpTree(
               (TeamDefinitionArtifact) tda.getParent(), users);
         // Add scenario where user is originator AND user is member of team that workflow is
         // written
         // against
         if (!users.contains(SkynetAuthentication.getInstance().getAuthenticatedUser())) {
            if (smaMgr.getOriginator().equals(SkynetAuthentication.getInstance().getAuthenticatedUser())) {
               if (tda.getMembers().contains(SkynetAuthentication.getInstance().getAuthenticatedUser())) users.add(SkynetAuthentication.getInstance().getAuthenticatedUser());
            }
         }
      } catch (SQLException ex) {
         // Do nothing
      }
   }

   @Override
   public String getEditorTitle() {
      return getTeamTitle();
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      actionableItemsDam = new XActionableItemsDam(this);
   }

   public ChangeType getChangeType() throws SQLException, MultipleAttributesExist {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   public void setChangeType(ChangeType type) throws IllegalStateException, SQLException, MultipleAttributesExist {
      setSoleXAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), type.name());
   }

   public PriorityType getPriority() throws SQLException, MultipleAttributesExist {
      return PriorityType.getPriority(getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   public void setPriority(PriorityType type) throws IllegalStateException, SQLException, MultipleAttributesExist {
      setSoleXAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), type.getShortName());
   }

   /**
    * @return Returns the actionableItemsDam.
    */
   public XActionableItemsDam getActionableItemsDam() {
      return actionableItemsDam;
   }

   public void setTeamDefinition(TeamDefinitionArtifact tda) throws IllegalStateException, SQLException, MultipleAttributesExist {
      this.setSoleXAttributeValue(ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(), tda.getGuid());
   }

   public TeamDefinitionArtifact getTeamDefinition() throws SQLException, MultipleAttributesExist, ArtifactDoesNotExist, MultipleArtifactsExist {
      String guid = this.getSoleAttributeValue(ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(), "");
      if (guid == null || guid.equals("")) throw new IllegalArgumentException(
            "TeamWorkflow has no TeamDefinition associated.");
      return (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromId(guid, BranchPersistenceManager.getAtsBranch());
   }

   public String getTeamTitle() {
      return "[" + getTeamName() + "] - " + getDescriptiveName();
   }

   public String getTeamName() {
      try {
         return getTeamDefinition().getDescriptiveName();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return "Exception: " + ex.getLocalizedMessage() + ". See log for details.";
      }
   }

   public String getWorldViewType() throws Exception {
      return getTeamName() + " Workflow";
   }

   public ChangeType getWorldViewChangeType() throws SQLException, MultipleAttributesExist {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   public String getWorldViewPriority() throws Exception {
      return PriorityType.getPriority(getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), "")).getShortName();
   }

   public String getWorldViewUserCommunity() throws Exception {
      return getAttributesToString(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName());
   }

   public String getWorldViewActionableItems() throws Exception {
      return getActionableItemsDam().getActionableItemsStr();
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws SQLException {
      super.atsDelete(deleteArts, allRelated);
      for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts())
         taskArt.atsDelete(deleteArts, allRelated);
      for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviews())
         reviewArt.atsDelete(deleteArts, allRelated);
   }

   public String getWorldViewTeam() throws Exception {
      return getTeamName();
   }

   public ActionArtifact getParentActionArtifact() throws SQLException {
      Set<ActionArtifact> arts = getArtifacts(RelationSide.ActionToWorkflow_Action, ActionArtifact.class);
      if (arts.size() == 0) {
         throw new IllegalStateException("Team " + getHumanReadableId() + " has no parent Action");
      } else if (arts.size() > 1) {
         throw new IllegalStateException("Team " + getHumanReadableId() + " has multiple parent Actions");
      } else
         return arts.iterator().next();
   }

   @Override
   public void transitioned(AtsWorkPage fromPage, AtsWorkPage toPage, Collection<User> toAssignees, boolean persist) throws Exception {
      super.transitioned(fromPage, toPage, toAssignees, persist);
      for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts())
         taskArt.parentWorkFlowTransitioned(fromPage, toPage, toAssignees, persist);
   }

   public String getWorldViewVersion() throws Exception {
      Collection<VersionArtifact> verArts =
            getArtifacts(RelationSide.TeamWorkflowTargetedForVersion_Version, VersionArtifact.class);
      if (verArts.size() == 0) return "";
      if (verArts.size() > 1) {
         String errStr =
               "Workflow " + smaMgr.getSma().getHumanReadableId() + " targeted for multiple versions: " + Artifacts.commaArts(verArts);
         OSEELog.logException(AtsPlugin.class, errStr, null, false);
         return XViewerCells.getCellExceptionString(errStr);
      }
      VersionArtifact verArt = verArts.iterator().next();
      if (!smaMgr.isCompleted() && verArt.getSoleAttributeValue(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), false)) {
         String errStr =
               "Workflow " + smaMgr.getSma().getHumanReadableId() + " targeted for released version, but not completed: " + verArt;
         OSEELog.logException(AtsPlugin.class, errStr, null, false);
         return XViewerCells.getCellExceptionString(errStr);
      }
      return verArt.getDescriptiveName();
   }

   public void setTargetedForVersion(VersionArtifact version, boolean persist) throws SQLException {
      relateReplace(RelationSide.TeamWorkflowTargetedForVersion_Version, version, persist);
   }

   public VersionArtifact getTargetedForVersion() throws SQLException {
      return (VersionArtifact) getFirstArtifact(RelationSide.TeamWorkflowTargetedForVersion_Version);
   }

   @Override
   public String getHyperName() {
      return getTeamName();
   }

   @Override
   public double getManDayHrsPreference() {
      try {
         return getTeamDefinition().getManDayHrsFromItemAndChildren();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return StateMachineArtifact.MAN_DAY_HOURS;
   }

   public Result addActionableItems() {
      Result toReturn = Result.FalseResult;
      AICheckTreeDialog diag =
            new AICheckTreeDialog(
                  "Add/Remove Impacted Actionable Items",
                  "Select/De-Select Impacted Actionable Items\n\n" + "Note: At least one Actionable Item must remain.\nTeam should be cancelled if no impact exists.",
                  Active.Both);

      try {
         diag.setInput(getTeamDefinition().getArtifacts(RelationSide.TeamActionableItem_ActionableItem,
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

            ActionableItemsTx txWrapper =
                  new ActionableItemsTx(BranchPersistenceManager.getAtsBranch(), selectedAlias, null);
            txWrapper.execute();
            toReturn = txWrapper.getResult();
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         toReturn = Result.FalseResult;
      }
      return toReturn;
   }

   public Result convertActionableItems() throws Exception {
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

      try {
         diag.setInput(ActionableItemArtifact.getTopLevelActionableItems(Active.Both));
         if (diag.open() != 0) return Result.FalseResult;
         if (diag.getChecked().size() == 0) return new Result("At least one actionable item must must be selected.");
         if (diag.getChecked().size() > 1) return new Result("Only ONE actionable item can be selected for converts");
         ActionableItemArtifact selectedAia = (ActionableItemArtifact) diag.getChecked().iterator().next();
         Set<TeamDefinitionArtifact> teamDefs = ActionableItemArtifact.getImpactedTeamDef(selectedAia);
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
                  ActionableItemsTx txWrapper =
                        new ActionableItemsTx(BranchPersistenceManager.getAtsBranch(), toProcess, newTeamDef);
                  txWrapper.execute();
                  toReturn = txWrapper.getResult();
               }
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         throw new SQLException(ex.getLocalizedMessage());
      }
      return toReturn;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentSMA()
    */
   @Override
   public StateMachineArtifact getParentSMA() throws SQLException {
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

   /**
    * If targeted for version exists, return that estimated date. Else, if attribute is set, return that date. Else
    * null.
    */
   public Date getWorldViewEstimatedReleaseDate() throws Exception {
      Collection<VersionArtifact> vers =
            getArtifacts(RelationSide.TeamWorkflowTargetedForVersion_Version, VersionArtifact.class);
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
   public Date getWorldViewReleaseDate() throws Exception {
      Collection<VersionArtifact> vers =
            getArtifacts(RelationSide.TeamWorkflowTargetedForVersion_Version, VersionArtifact.class);
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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewImplementer()
    */
   public String getWorldViewImplementer() throws Exception {
      return Artifacts.commaArts(smaMgr.getStateMgr().getAssignees(DefaultTeamState.Implement.name()));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   public String getWorldViewDeadlineDateStr() throws Exception {
      Date date = getWorldViewDeadlineDate();
      if (date != null) return new XDate(date).getMMDDYY();
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   public Date getWorldViewDeadlineDate() throws Exception {
      return getSoleAttributeValue(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName(), null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewWeeklyBenefit()
    */
   public double getWorldViewWeeklyBenefit() throws Exception {
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
   public double getWorldViewAnnualCostAvoidance() throws Exception {
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
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws SQLException {
      return this;
   }

   private final class ActionableItemsTx extends AbstractSkynetTxTemplate {

      private Result workResult;
      private Set<ActionableItemArtifact> selectedAlias;
      private TeamDefinitionArtifact teamDefinition;

      public ActionableItemsTx(Branch branch, Set<ActionableItemArtifact> selectedAlias, TeamDefinitionArtifact teamDefinition) {
         super(branch);
         this.workResult = Result.TrueResult;
         this.selectedAlias = selectedAlias;
         this.teamDefinition = teamDefinition;
      }

      public Result getResult() {
         return workResult;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws Exception {
         workResult = actionableItemsDam.setActionableItems(selectedAlias);
         if (workResult.isTrue()) {
            if (teamDefinition != null) setTeamDefinition(teamDefinition);
            getParentActionArtifact().resetAttributesOffChildren();
            persistAttributes();
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewBranchStatus()
    */
   public String getWorldViewBranchStatus() throws Exception {
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
   public Branch getWorkingBranch() throws SQLException {
      if (getSmaMgr().getBranchMgr().getWorkingBranch() != null) {
         return getSmaMgr().getBranchMgr().getWorkingBranch();
      }
      return null;
   }
}
