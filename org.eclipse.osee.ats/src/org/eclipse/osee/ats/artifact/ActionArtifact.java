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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.XActionableItemsDam;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifact extends ATSArtifact implements IWorldViewArtifact {

   public static String ARTIFACT_NAME = "Action";

   private XActionableItemsDam actionableItemsDam;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public ActionArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactSubtypeDescriptor artifactType) throws SQLException {
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
      actionableItemsDam = new XActionableItemsDam(this);
   }

   public void resetAttributesOffChildren() throws SQLException, MultipleAttributesExist, ArtifactDoesNotExist, MultipleArtifactsExist {
      resetActionItemsOffChildren();
      resetChangeTypeOffChildren();
      resetPriorityOffChildren();
      resetUserCommunityOffChildren();
      resetTitleOffChildren();
      resetValidationOffChildren();
      resetDescriptionOffChildren();
      if (isDirty()) persistAttributes();
   }

   /**
    * Reset Action title only if all children are titled the same
    * 
    * @throws SQLException
    */
   private void resetTitleOffChildren() throws SQLException {
      String title = "";
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (title.equals(""))
            title = team.getDescriptiveName();
         else if (!title.equals(team.getDescriptiveName())) return;
      }
      if (!title.equals(getDescriptiveName())) setDescriptiveName(title);
   }

   // Set validation to true if any require validation
   private void resetValidationOffChildren() throws SQLException, MultipleAttributesExist {
      boolean validationRequired = false;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (team.getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false)) validationRequired =
               true;
      }
      if (validationRequired != getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false)) setSoleBooleanAttributeValue(
            ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), validationRequired);
   }

   /**
    * Reset Action title only if all children are titled the same
    * 
    * @throws SQLException
    */
   private void resetDescriptionOffChildren() throws SQLException, MultipleAttributesExist {
      String desc = "";
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (desc.equals(""))
            desc = team.getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
         else if (!desc.equals(team.getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), ""))) return;
      }
      if (!desc.equals(getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), ""))) setSoleStringAttributeValue(
            ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
   }

   private Result resetActionItemsOffChildren() throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts())
         if (!(new SMAManager(team)).isCancelled()) aias.addAll(team.getActionableItemsDam().getActionableItems());
      return actionableItemsDam.setActionableItems(aias);
   }

   private void resetChangeTypeOffChildren() throws SQLException, MultipleAttributesExist {
      ChangeType changeType = null;
      Collection<TeamWorkFlowArtifact> teamArts = getTeamWorkFlowArtifacts();
      if (teamArts.size() == 1)
         changeType = teamArts.iterator().next().getChangeType();
      else
         for (TeamWorkFlowArtifact team : teamArts)
            if (!(new SMAManager(team)).isCancelled()) {
               if (changeType == null)
                  changeType = team.getChangeType();
               // if change type of this team is different than others, can't
               // change to common type so just return
               else if (changeType != team.getChangeType()) return;
            }
      if (changeType != null && getChangeType() != changeType) setChangeType(changeType);
      return;
   }

   private void resetPriorityOffChildren() throws SQLException, MultipleAttributesExist {
      PriorityType priorityType = null;
      Collection<TeamWorkFlowArtifact> teamArts = getTeamWorkFlowArtifacts();
      if (teamArts.size() == 1)
         priorityType = teamArts.iterator().next().getPriority();
      else
         for (TeamWorkFlowArtifact team : teamArts)
            if (!(new SMAManager(team)).isCancelled()) {
               if (priorityType == null)
                  priorityType = team.getPriority();
               // if change type of this team is different than others, can't
               // change to common type so just return
               else if (priorityType != team.getPriority()) return;
            }
      if (priorityType != null && getPriority() != priorityType) setPriority(priorityType);
      return;
   }

   private void resetUserCommunityOffChildren() throws SQLException {
      Set<String> userComs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!(new SMAManager(team)).isCancelled()) {
            userComs.addAll(team.getAttributesToStringCollection(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName()));
         }
      }
      try {
         setDamAttributes(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName(), userComs);
      } catch (Exception ex) {
         throw new SQLException(ex);
      }
   }

   public void setChangeType(ChangeType type) throws IllegalStateException, SQLException {
      setSoleStringAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), type.name());
   }

   public ChangeType getChangeType() throws SQLException, MultipleAttributesExist {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   public PriorityType getPriority() throws SQLException, MultipleAttributesExist {
      return PriorityType.getPriority(getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   public void setPriority(PriorityType type) throws IllegalStateException, SQLException {
      setSoleStringAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), type.getShortName());
   }

   /**
    * @return Returns the actionableItemsDam.
    */
   public XActionableItemsDam getActionableItemsDam() {
      return actionableItemsDam;
   }

   public Collection<TeamWorkFlowArtifact> getTeamWorkFlowArtifacts() throws SQLException {
      return getArtifacts(RelationSide.ActionToWorkflow_WorkFlow, TeamWorkFlowArtifact.class);
   }

   public String getWorldViewType() throws Exception {
      return ARTIFACT_NAME;
   }

   public String getWorldViewTitle() throws Exception {
      return getDescriptiveName();
   }

   public ChangeType getWorldViewChangeType() throws SQLException, MultipleAttributesExist {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewBranchStatus()
    */
   public String getWorldViewBranchStatus() throws Exception {
      try {
         StringBuffer sb = new StringBuffer();
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (!team.getWorldViewBranchStatus().equals("")) sb.append(team.getWorldViewBranchStatus() + ", ");
         }
         return sb.toString().replaceFirst(", $", "");
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public String getWorldViewNumberOfTasks() throws Exception {
      try {
         StringBuffer sb = new StringBuffer();
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (!team.getWorldViewNumberOfTasks().equals("")) sb.append(team.getWorldViewNumberOfTasks() + ", ");
         }
         return sb.toString().replaceFirst(", $", "");
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public String getWorldViewState() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewState();
            else if (!str.equals(team.getWorldViewState())) return "";
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   public String getWorldViewActivePoc() throws Exception {
      Set<User> pocs = new HashSet<User>();
      try {
         // Roll up all assignees
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            pocs.addAll(team.getSmaMgr().getStateMgr().getAssignees());
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return Artifacts.commaArts(pocs);
   }

   public String getWorldViewCreatedDateStr() throws Exception {
      try {
         Date date = getWorldViewCreatedDate();
         if (date == null) return XViewerCells.getCellExceptionString("No Creation Date Found");
         return XDate.getDateStr(date, XDate.MMDDYYHHMM);
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public Date getWorldViewCreatedDate() throws Exception {
      return getTeamWorkFlowArtifacts().iterator().next().getWorldViewCreatedDate();
   }

   public String getWorldViewID() throws Exception {
      return getHumanReadableId();
   }

   public String getWorldViewPriority() throws Exception {
      try {
         return PriorityType.getPriority(
               getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), "")).getShortName();
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public Image getAssigneeImage() throws Exception {
      try {
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            Image image = team.getAssigneeImage();
            if (image != null) return image;
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return null;
   }

   public String getWorldViewUserCommunity() throws Exception {
      try {
         return getAttributesToString(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName());
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public String getWorldViewActionableItems() throws Exception {
      try {
         StringBuffer sb = new StringBuffer();
         for (ActionableItemArtifact aia : getActionableItemsDam().getActionableItems()) {
            sb.append(aia.getDescriptiveName() + ", ");
         }
         return sb.toString().replaceFirst(", $", "");
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws SQLException {
      super.atsDelete(deleteArts, allRelated);
      // Delete all products
      for (TeamWorkFlowArtifact art : getArtifacts(RelationSide.ActionToWorkflow_WorkFlow, TeamWorkFlowArtifact.class))
         art.atsDelete(deleteArts, allRelated);
   }

   public String getWorldViewTeam() throws Exception {
      Set<TeamDefinitionArtifact> teams = new HashSet<TeamDefinitionArtifact>();
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            teams.add(team.getTeamDefinition());
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return Artifacts.commaArts(teams);
   }

   public String getWorldViewOriginator() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewOriginator();
            else if (!str.equals(team.getWorldViewOriginator())) return "";
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   public Date getWorldViewCompletedDate() throws Exception {
      Date date = null;
      try {
         // Roll up date if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (date == null)
               date = team.getWorldViewCompletedDate();
            else {
               if (!XDate.getDateStr(date, XDate.MMDDYY).equals(
                     XDate.getDateStr(team.getWorldViewCompletedDate(), XDate.MMDDYY))) return null;
            }
         }
         return date;
      } catch (SQLException ex) {
         // Do nothing
      }
      return null;
   }

   public Date getWorldViewCancelledDate() throws Exception {
      Date date = null;
      try {
         // Roll up date if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (date == null)
               date = team.getWorldViewCancelledDate();
            else {
               if (!XDate.getDateStr(date, XDate.MMDDYY).equals(
                     XDate.getDateStr(team.getWorldViewCancelledDate(), XDate.MMDDYY))) return null;
            }
         }
         return date;
      } catch (SQLException ex) {
         // Do nothing
      }
      return null;
   }

   public String getWorldViewResolution() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewResolution();
            else if (!str.equals(team.getWorldViewResolution())) return "";
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewRemainHours()
    */
   public double getWorldViewRemainHours() throws Exception {
      double hours = 0;
      // Add up hours for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         hours += team.getWorldViewRemainHours();
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewManDaysNeeded()
    */
   public double getWorldViewManDaysNeeded() throws Exception {
      double hours = 0;
      // Add up hours for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         hours += team.getWorldViewManDaysNeeded();
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewEstimatedHours()
    */
   public double getWorldViewEstimatedHours() throws Exception {
      double hours = 0;
      // Add up hours for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         hours += team.getWorldViewEstimatedHours();
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewStatePercentComplete()
    */
   public int getWorldViewStatePercentComplete() throws Exception {
      if (getTeamWorkFlowArtifacts().size() == 1)
         return getTeamWorkFlowArtifacts().iterator().next().getWorldViewStatePercentComplete();
      else {
         double percent = 0;
         int items = 0;
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            SMAManager smaMgr = new SMAManager(team);
            if (!smaMgr.isCancelled()) {
               percent += team.getWorldViewStatePercentComplete();
               items++;
            }
         }
         if (items > 0) {
            Double rollPercent = percent / items;
            return rollPercent.intValue();
         }
      }
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewRelatedToState()
    */
   public String getWorldViewRelatedToState() throws Exception {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewNotes()
    */
   public String getWorldViewNotes() throws Exception {
      String str = "";
      // Roll up if same for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (str.equals(""))
            str = team.getWorldViewNotes();
         else if (!str.equals(team.getWorldViewNotes())) return "";
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewCategory()
    */
   public String getWorldViewCategory() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewWorkPackage();
            else if (!str.equals(team.getWorldViewWorkPackage())) return "";
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   public String getWorldViewWorkPackage() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewWorkPackage();
            else if (!str.equals(team.getWorldViewWorkPackage())) return "";
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   public String getWorldViewCategory2() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewCategory2();
            else if (!str.equals(team.getWorldViewCategory2())) return "";
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   public String getWorldViewCategory3() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewCategory3();
            else if (!str.equals(team.getWorldViewCategory3())) return "";
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewVersion()
    */
   public String getWorldViewVersion() throws Exception {
      Set<String> versions = new HashSet<String>();
      try {
         // Roll up version if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (!team.getWorldViewVersion().equals("")) versions.add(team.getWorldViewVersion());
         }
         return Collections.toString(",", versions);
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperName()
    */
   public String getHyperName() {
      return getDescriptiveName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperType()
    */
   public String getHyperType() {
      return "Team";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperState()
    */
   public String getHyperState() {
      try {
         if (getTeamWorkFlowArtifacts().size() == 1) return getTeamWorkFlowArtifacts().iterator().next().getHyperState();
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperAssignee()
    */
   public String getHyperAssignee() {
      try {
         if (getTeamWorkFlowArtifacts().size() == 1) return getTeamWorkFlowArtifacts().iterator().next().getHyperAssignee();
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperImage()
    */
   public Image getHyperImage() {
      return getImage();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperAssigneeImage()
    */
   public Image getHyperAssigneeImage() throws Exception {
      try {
         if (getTeamWorkFlowArtifacts().size() == 1) return getTeamWorkFlowArtifacts().iterator().next().getHyperAssigneeImage();
      } catch (SQLException ex) {
         // Do nothing
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperArtifact()
    */
   public Artifact getHyperArtifact() {
      return this;
   }

   public Result addActionableItems() throws Exception {
      final AICheckTreeDialog diag =
            new AICheckTreeDialog(
                  "Add Impacted Actionable Items",
                  "Select New Impacted Actionable Items\n\n" + "Note: Un-selecting existing items will NOT remove the impact.\n" + "Team Workflow with no impact should be transitioned to Cancelled.",
                  Active.Active);

      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         aias.addAll(team.getActionableItemsDam().getActionableItems());
      }
      diag.setInitialSelections(aias);
      if (diag.open() != 0) return Result.FalseResult;

      final StringBuffer sb = new StringBuffer();

      AbstractSkynetTxTemplate transaction = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {

         @Override
         protected void handleTxWork() throws Exception {
            for (ActionableItemArtifact aia : diag.getChecked()) {
               Result result = addActionableItemToTeamsOrAddTeams(aia);
               sb.append(result.getText());
            }
         }
      };
      transaction.execute();
      return new Result(true, sb.toString());
   }

   public Result addActionableItemToTeamsOrAddTeams(ActionableItemArtifact aia) throws Exception {
      StringBuffer sb = new StringBuffer();
      for (TeamDefinitionArtifact tda : TeamDefinitionArtifact.getImpactedTeamDef(aia)) {
         boolean teamExists = false;
         // Look for team workflow that is associated with this tda
         for (TeamWorkFlowArtifact teamArt : getTeamWorkFlowArtifacts()) {
            // If found
            if (teamArt.getTeamDefinition().equals(tda)) {
               // And workflow doesn't already have this actionable item,
               // ADD it
               if (!teamArt.getActionableItemsDam().getActionableItems().contains(aia)) {
                  teamArt.getActionableItemsDam().addActionableItem(aia);
                  teamArt.persistAttributes();
                  sb.append(aia.getDescriptiveName() + " => added to existing team workflow \"" + tda.getDescriptiveName() + "\"\n");
                  teamExists = true;
               } else {
                  sb.append(aia.getDescriptiveName() + " => already exists in team workflow \"" + tda.getDescriptiveName() + "\"\n");
                  teamExists = true;
               }
            }
         }
         if (!teamExists) {
            createTeamWorkflow(tda, Arrays.asList(new ActionableItemArtifact[] {aia}), tda.getLeads());
            sb.append(aia.getDescriptiveName() + " => added team workflow \"" + tda.getDescriptiveName() + "\"\n");
         }
      }
      return new Result(true, sb.toString());
   }

   public TeamWorkFlowArtifact createTeamWorkflow(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems, Collection<User> assignees) throws Exception {
      String teamWorkflowArtifactName = TeamWorkFlowArtifact.ARTIFACT_NAME;
      IAtsTeamWorkflow teamExt = null;

      // Check if any plugins want to create the team workflow themselves
      for (IAtsTeamWorkflow teamExtension : TeamWorkflowExtensions.getInstance().getAtsTeamWorkflowExtensions()) {
         boolean isResponsible = false;
         try {
            isResponsible = teamExtension.isResponsibleForTeamWorkflowCreation(teamDef, actionableItems);
         } catch (Exception ex) {
            OSEELog.logWarning(AtsPlugin.class, ex, false);
         }
         if (isResponsible) {
            teamWorkflowArtifactName = teamExtension.getTeamWorkflowArtifactName(teamDef, actionableItems);
            teamExt = teamExtension;
         }
      }

      // NOTE: The persist of the workflow will auto-email the assignees
      TeamWorkFlowArtifact teamArt = createTeamWorkflow(teamDef, actionableItems, assignees, teamWorkflowArtifactName);
      // Notify extension that workflow was created
      if (teamExt != null) teamExt.teamWorkflowCreated(teamArt);
      return teamArt;
   }

   public TeamWorkFlowArtifact createTeamWorkflow(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems, Collection<User> assignees, String artifactName) throws Exception {
      return createTeamWorkflow(teamDef, actionableItems, assignees, null, null, artifactName);
   }

   public TeamWorkFlowArtifact createTeamWorkflow(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems, Collection<User> assignees, String guid, String hrid, String artifactName) throws Exception {

      // Make sure team doesn't already exist
      for (TeamWorkFlowArtifact teamArt : getTeamWorkFlowArtifacts()) {
         if (teamArt.getTeamDefinition().equals(teamDef)) {
            AWorkbench.popup("ERROR", "Team already exist");
            throw new IllegalArgumentException(
                  "Team \"" + teamDef + "\" already exists for Action " + getHumanReadableId());
         }
      }
      TeamWorkFlowArtifact teamArt = null;
      if (guid == null)
         teamArt =
               (TeamWorkFlowArtifact) ArtifactTypeManager.addArtifact(artifactName,
                     BranchPersistenceManager.getAtsBranch());
      else
         teamArt =
               (TeamWorkFlowArtifact) ArtifactTypeManager.addArtifact(artifactName,
                     BranchPersistenceManager.getAtsBranch(), guid, hrid);
      setArtifactIdentifyData(this, teamArt);

      teamArt.getLog().addLog(LogType.Originated, "", "");

      // Relate Workflow to ActionableItems (by guid) if team is responsible
      // for that AI
      for (ActionableItemArtifact aia : actionableItems)
         if (aia.getImpactedTeamDefs().contains(teamDef)) teamArt.getActionableItemsDam().addActionableItem(aia);

      // Initialize state machine
      teamArt.getSmaMgr().getStateMgr().initializeStateMachine(DefaultTeamState.Endorse.name());
      teamArt.getLog().addLog(LogType.StateEntered, DefaultTeamState.Endorse.name(), "");

      // Relate WorkFlow to Team Definition (by guid due to relation loading
      // issues)
      teamArt.setTeamDefinition(teamDef);

      // Relate Action to WorkFlow
      relate(RelationSide.ActionToWorkflow_WorkFlow, teamArt);

      // Persist
      teamArt.persist(true);

      return teamArt;
   }

   /**
    * Set teamworkflowartifact attributes off given action artifact
    * 
    * @param fromAction
    * @param toTeam
    * @throws SQLException
    * @throws IllegalStateException
    */
   public static void setArtifactIdentifyData(ActionArtifact fromAction, TeamWorkFlowArtifact toTeam) throws Exception {
      String priorityStr = fromAction.getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), "");
      PriorityType priType = null;
      if (priorityStr.equals(""))
         priType = null;
      else if (!priorityStr.equals(""))
         priType = PriorityType.getPriority(priorityStr);
      else
         throw new IllegalArgumentException("Invalid priority => " + priorityStr);
      setArtifactIdentifyData(toTeam, fromAction.getDescriptiveName(), fromAction.getSoleAttributeValue(
            ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), ""),
            ChangeType.getChangeType(fromAction.getSoleAttributeValue(
                  ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), "")), priType,
            fromAction.getAttributesToStringCollection(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName()),
            fromAction.getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false),
            fromAction.getSoleAttributeValue(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName(), null, Date.class));
   }

   /**
    * Since there is no shared attribute yet, action and workflow arts are all populate with identify data
    * 
    * @param art
    * @throws SQLException
    */
   public static void setArtifactIdentifyData(Artifact art, String title, String desc, ChangeType changeType, PriorityType priority, Collection<String> userComms, Boolean validationRequired, Date needByDate) throws Exception {
      art.setDescriptiveName(title);
      if (!desc.equals("")) art.setSoleStringAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
      art.setSoleStringAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), changeType.name());
      art.setDamAttributes(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName(), userComms);
      if (priority != null) art.setSoleStringAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(),
            priority.getShortName());
      if (needByDate != null) art.setSoleDateAttributeValue(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName(), needByDate);
      if (validationRequired) art.setSoleBooleanAttributeValue(
            ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), true);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDecision()
    */
   public String getWorldViewDecision() throws Exception {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact#getParentSMArt()
    */
   public Artifact getParentSMArt() throws SQLException {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDescription()
    */
   public String getWorldViewDescription() throws Exception {
      try {
         return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewValidationRequired()
    */
   public String getWorldViewValidationRequiredStr() throws Exception {
      try {
         return String.valueOf(getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false));
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public Date getWorldViewEstimatedReleaseDate() throws Exception {
      if (getTeamWorkFlowArtifacts().size() == 1) return getTeamWorkFlowArtifacts().iterator().next().getWorldViewEstimatedReleaseDate();
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewCancelledDateStr()
    */
   public String getWorldViewCancelledDateStr() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewState();
            else if (!str.equals(team.getWorldViewCancelledDateStr())) return "";
         }
         return str;
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public String getWorldViewReleaseDateStr() throws Exception {
      try {
         Date date = getWorldViewReleaseDate();
         if (date == null) return "";
         return XDate.getDateStr(date, XDate.MMDDYY);
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public Date getWorldViewReleaseDate() throws Exception {
      Date date = null;
      try {
         // Roll up date if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (date == null)
               date = team.getWorldViewReleaseDate();
            else {
               if (!XDate.getDateStr(date, XDate.MMDDYY).equals(
                     XDate.getDateStr(team.getWorldViewReleaseDate(), XDate.MMDDYY))) return null;
            }
         }
         return date;
      } catch (SQLException ex) {
         // do nothing
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewCompletedDateStr()
    */
   public String getWorldViewCompletedDateStr() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewCompletedDateStr();
            else if (!str.equals(team.getWorldViewCompletedDateStr())) return "";
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewEstimatedReleaseDateStr()
    */
   public String getWorldViewEstimatedReleaseDateStr() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewEstimatedReleaseDateStr();
            else if (!str.equals(team.getWorldViewEstimatedReleaseDateStr())) return "";
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewRemainHoursValid()
    */
   public Result isWorldViewRemainHoursValid() throws Exception {
      try {
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts())
            if (team.isWorldViewRemainHoursValid().isFalse()) return team.isWorldViewRemainHoursValid();
      } catch (SQLException ex) {
         // Do nothing
      }
      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewManDaysNeededValid()
    */
   public Result isWorldViewManDaysNeededValid() throws Exception {
      try {
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts())
            if (team.isWorldViewManDaysNeededValid().isFalse()) return team.isWorldViewManDaysNeededValid();
      } catch (SQLException ex) {
         // Do nothing
      }
      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewChangeTypeStr()
    */
   public String getWorldViewChangeTypeStr() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewChangeTypeStr();
            else if (!str.equals(team.getWorldViewChangeTypeStr())) return "";
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewImplementer()
    */
   public String getWorldViewImplementer() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewImplementer();
            else if (!str.equals(team.getWorldViewImplementer())) return "";
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   public Date getWorldViewDeadlineDate() throws Exception {
      Date date = null;
      try {
         // Roll up date if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (date == null)
               date = team.getWorldViewDeadlineDate();
            else {
               if (!XDate.getDateStr(date, XDate.MMDDYY).equals(
                     XDate.getDateStr(team.getWorldViewDeadlineDate(), XDate.MMDDYY))) return null;
            }
         }
         return date;
      } catch (SQLException ex) {
         // Do nothing
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   public String getWorldViewDeadlineDateStr() throws Exception {
      try {
         Date date = getWorldViewDeadlineDate();
         if (date == null) return "";
         return XDate.getDateStr(date, XDate.MMDDYY);
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewWeeklyBenefit()
    */
   public double getWorldViewWeeklyBenefit() throws Exception {
      double hours = 0;
      // Add up hours for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         SMAManager smaMgr = new SMAManager(team);
         if (!smaMgr.isCompleted() && !smaMgr.isCancelled()) hours += team.getWorldViewWeeklyBenefit();
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewAnnualCostAvoidance()
    */
   public double getWorldViewAnnualCostAvoidance() throws Exception {
      double hours = 0;
      // Add up hours for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         SMAManager smaMgr = new SMAManager(team);
         if (!smaMgr.isCompleted() && !smaMgr.isCancelled()) hours += team.getWorldViewAnnualCostAvoidance();
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewAnnualCostAvoidanceValid()
    */
   public Result isWorldViewAnnualCostAvoidanceValid() throws Exception {
      try {
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            Result result = team.isWorldViewAnnualCostAvoidanceValid();
            if (result.isFalse()) return result;
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return Result.TrueResult;
   }

   @Override
   public Set<ArtifactAnnotation> getAnnotations() {
      Set<ArtifactAnnotation> notifications = super.getAnnotations();
      try {
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            notifications.addAll(team.getAnnotations());
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return notifications;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewDeadlineAlerting()
    */
   public Result isWorldViewDeadlineAlerting() throws Exception {
      try {
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            Result result = team.isWorldViewDeadlineAlerting();
            if (result.isTrue()) return result;
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return Result.FalseResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewLegacyPCR()
    */
   public String getWorldViewLegacyPCR() throws Exception {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewLegacyPCR();
            else if (!str.equals(team.getWorldViewLegacyPCR())) return "";
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentReworkStr()
    */
   public String getWorldViewPercentReworkStr() throws Exception {
      Set<String> reworks = new HashSet<String>();
      try {
         // Roll up version if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (team.getWorldViewPercentRework() > 0) reworks.add(String.valueOf(team.getWorldViewPercentRework()));
         }
         return Collections.toString(",", reworks);
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentRework()
    */
   public int getWorldViewPercentRework() throws Exception {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewAuthor()
    */
   public String getWorldViewReviewAuthor() throws Exception {
      return "";
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
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewReviewer()
    */
   public String getWorldViewReviewReviewer() throws Exception {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentState()
    */
   @Override
   public double getWorldViewHoursSpentState() throws Exception {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            hours += team.getWorldViewHoursSpentState();
         }
      }
      return hours;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentStateReview()
    */
   @Override
   public double getWorldViewHoursSpentStateReview() throws Exception {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            hours += team.getWorldViewHoursSpentStateReview();
         }
      }
      return hours;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentStateTask()
    */
   @Override
   public double getWorldViewHoursSpentStateTask() throws Exception {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            hours += team.getWorldViewHoursSpentStateTask();
         }
      }
      return hours;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentTotal()
    */
   @Override
   public double getWorldViewHoursSpentTotal() throws Exception {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            hours += team.getWorldViewHoursSpentTotal();
         }
      }
      return hours;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentCompleteState()
    */
   @Override
   public int getWorldViewPercentCompleteState() throws Exception {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            percent += team.getWorldViewPercentCompleteState();
         }
      }
      if (percent == 0) return 0;
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentCompleteStateReview()
    */
   @Override
   public int getWorldViewPercentCompleteStateReview() throws Exception {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            percent += team.getWorldViewPercentCompleteStateReview();
         }
      }
      if (percent == 0) return 0;
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentCompleteStateTask()
    */
   @Override
   public int getWorldViewPercentCompleteStateTask() throws Exception {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            percent += team.getWorldViewPercentCompleteStateTask();
         }
      }
      if (percent == 0) return 0;
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentCompleteTotal()
    */
   @Override
   public int getWorldViewPercentCompleteTotal() throws Exception {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            percent += team.getWorldViewPercentCompleteTotal();
         }
      }
      if (percent == 0) return 0;
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

}
