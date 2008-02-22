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
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XActionableItemsDam;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
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
   public ActionArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
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

   public void resetAttributesOffChildren() throws SQLException {
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
   private void resetValidationOffChildren() throws SQLException {
      boolean validationRequired = false;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (team.getSoleBooleanAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName())) validationRequired =
               true;
      }
      if (validationRequired != getSoleBooleanAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName())) setSoleBooleanAttributeValue(
            ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), validationRequired);
   }

   /**
    * Reset Action title only if all children are titled the same
    * 
    * @throws SQLException
    */
   private void resetDescriptionOffChildren() throws SQLException {
      String desc = "";
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (desc.equals(""))
            desc = team.getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName());
         else if (!desc.equals(team.getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName()))) return;
      }
      if (!desc.equals(getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName()))) setSoleAttributeValue(
            ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
   }

   private Result resetActionItemsOffChildren() throws SQLException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts())
         if (!(new SMAManager(team)).isCancelled()) aias.addAll(team.getActionableItemsDam().getActionableItems());
      return actionableItemsDam.setActionableItems(aias);
   }

   private void resetChangeTypeOffChildren() throws SQLException {
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

   private void resetPriorityOffChildren() throws SQLException {
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
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts())
         if (!(new SMAManager(team)).isCancelled()) userComs.addAll(team.getAttributesToStringCollection(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName()));
      setDamAttributes(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName(), userComs);
   }

   public void setChangeType(ChangeType type) throws IllegalStateException, SQLException {
      setSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), type.name());
   }

   public ChangeType getChangeType() {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName()));
   }

   public PriorityType getPriority() {
      return PriorityType.getPriority(getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName()));
   }

   public void setPriority(PriorityType type) throws IllegalStateException, SQLException {
      setSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), type.getShortName());
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

   public String getWorldViewType() {
      return ARTIFACT_NAME;
   }

   public String getWorldViewTitle() {
      return getDescriptiveName();
   }

   public ChangeType getWorldViewChangeType() {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName()));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewBranchStatus()
    */
   public String getWorldViewBranchStatus() {
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

   public String getWorldViewNumberOfTasks() {
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

   public String getWorldViewState() {
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

   public String getWorldViewActivePoc() {
      Set<User> pocs = new HashSet<User>();
      try {
         // Roll up all assignees
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            pocs.addAll(team.getCurrentState().getAssignees());
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return Artifacts.commaArts(pocs);
   }

   public String getWorldViewCreatedDateStr() {
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

   public String getWorldViewID() {
      return getHumanReadableId();
   }

   public String getWorldViewPriority() {
      try {
         return PriorityType.getPriority(getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName())).getShortName();
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public Image getAssigneeImage() {
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

   public String getWorldViewUserCommunity() {
      try {
         return getAttributesToString(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName());
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public String getWorldViewActionableItems() {
      try {
         StringBuffer sb = new StringBuffer();
         for (ActionableItemArtifact aia : getActionableItemsDam().getActionableItems()) {
            sb.append(aia.getDescriptiveName() + ", ");
         }
         return sb.toString().replaceFirst(", $", "");
      } catch (SQLException ex) {
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

   public String getWorldViewTeam() {
      Set<TeamDefinitionArtifact> teams = new HashSet<TeamDefinitionArtifact>();
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            teams.add(team.getTeamDefinition());
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return Artifacts.commaArts(teams);
   }

   public String getWorldViewOriginator() {
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

   public String getWorldViewResolution() {
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
   public double getWorldViewRemainHours() {
      double hours = 0;
      try {
         // Add up hours for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            hours += team.getWorldViewRemainHours();
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewManDaysNeeded()
    */
   public double getWorldViewManDaysNeeded() {
      double hours = 0;
      try {
         // Add up hours for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            hours += team.getWorldViewManDaysNeeded();
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewEstimatedHours()
    */
   public double getWorldViewEstimatedHours() {
      double hours = 0;
      try {
         // Add up hours for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            hours += team.getWorldViewEstimatedHours();
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewStatePercentComplete()
    */
   public int getWorldViewStatePercentComplete() {
      try {
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
      } catch (SQLException ex) {
         // Do nothing
      }
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewStateHoursSpent()
    */
   public double getWorldViewStateHoursSpent() {
      try {
         if (getTeamWorkFlowArtifacts().size() == 1) return getTeamWorkFlowArtifacts().iterator().next().getWorldViewStateHoursSpent();
      } catch (SQLException ex) {
         // Do nothing
      }
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewTotalPercentComplete()
    */
   public int getWorldViewTotalPercentComplete() {
      try {
         if (getTeamWorkFlowArtifacts().size() == 1)
            return getTeamWorkFlowArtifacts().iterator().next().getWorldViewTotalPercentComplete();
         else {
            double percent = 0;
            int items = 0;
            for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
               SMAManager smaMgr = new SMAManager(team);
               if (!smaMgr.isCancelled()) {
                  percent += team.getWorldViewTotalPercentComplete();
                  items++;
               }
            }
            if (items > 0) {
               Double rollPercent = percent / items;
               return rollPercent.intValue();
            }
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewTotalHoursSpent()
    */
   public double getWorldViewTotalHoursSpent() {
      double hours = 0;
      try {
         // Add up hours for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            hours += team.getWorldViewTotalHoursSpent();
         }
      } catch (SQLException ex) {
         // Do nothing
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewRelatedToState()
    */
   public String getWorldViewRelatedToState() {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewNotes()
    */
   public String getWorldViewNotes() {
      String str = "";
      try {
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (str.equals(""))
               str = team.getWorldViewNotes();
            else if (!str.equals(team.getWorldViewNotes())) return "";
         }
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return str;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewCategory()
    */
   public String getWorldViewCategory() {
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

   public String getWorldViewWorkPackage() {
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

   public String getWorldViewCategory2() {
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

   public String getWorldViewCategory3() {
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
   public String getWorldViewVersion() {
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
   public Image getHyperAssigneeImage() {
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
      if (diag.open() != 0) return Result.FalseResult;

      final StringBuffer sb = new StringBuffer();

      AbstractSkynetTxTemplate transaction = new AbstractSkynetTxTemplate(branchManager.getAtsBranch()) {

         @Override
         protected void handleTxWork() throws Exception {
            for (Object obj : diag.getResult()) {
               ActionableItemArtifact aia = (ActionableItemArtifact) obj;
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

   public TeamWorkFlowArtifact createTeamWorkflow(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems, Collection<User> assignees, String artifactName) throws SQLException {
      return createTeamWorkflow(teamDef, actionableItems, assignees, null, null, artifactName);
   }

   public TeamWorkFlowArtifact createTeamWorkflow(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems, Collection<User> assignees, String guid, String hrid, String artifactName) throws SQLException {

      // Make sure team doesn't already exist
      for (TeamWorkFlowArtifact teamArt : getTeamWorkFlowArtifacts()) {
         if (teamArt.getTeamDefinition().equals(teamDef)) {
            AWorkbench.popup("ERROR", "Team already exist");
            throw new IllegalArgumentException(
                  "Team \"" + teamDef + "\" already exists for Action " + getHumanReadableId());
         }
      }
      TeamWorkFlowArtifact twa = null;
      if (guid == null)
         twa =
               (TeamWorkFlowArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     artifactName).makeNewArtifact(BranchPersistenceManager.getInstance().getAtsBranch());
      else
         twa =
               (TeamWorkFlowArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     artifactName).makeNewArtifact(BranchPersistenceManager.getInstance().getAtsBranch(), guid, hrid);
      setArtifactIdentifyData(this, twa);

      twa.getLog().addLog(LogType.Originated, "", "");

      // Relate Workflow to ActionableItems (by guid) if team is responsible
      // for that AI
      for (ActionableItemArtifact aia : actionableItems)
         if (aia.getImpactedTeamDefs().contains(teamDef)) twa.getActionableItemsDam().addActionableItem(aia);

      // Set current state and POCs
      twa.getCurrentStateDam().setState(new SMAState(DefaultTeamState.Endorse.name(), assignees));
      twa.getLog().addLog(LogType.StateEntered, DefaultTeamState.Endorse.name(), "");

      // Relate WorkFlow to Team Definition (by guid due to relation loading
      // issues)
      twa.setTeamDefinition(teamDef);

      // Relate Action to WorkFlow
      relate(RelationSide.ActionToWorkflow_WorkFlow, twa);

      // Persist
      twa.persist(true);

      return twa;
   }

   /**
    * Set teamworkflowartifact attributes off given action artifact
    * 
    * @param fromAction
    * @param toTeam
    * @throws SQLException
    * @throws IllegalStateException
    */
   public static void setArtifactIdentifyData(ActionArtifact fromAction, TeamWorkFlowArtifact toTeam) throws IllegalStateException, SQLException {
      String priorityStr = fromAction.getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName());
      PriorityType priType = null;
      if (priorityStr.equals(""))
         priType = null;
      else if (!priorityStr.equals(""))
         priType = PriorityType.getPriority(priorityStr);
      else
         throw new IllegalArgumentException("Invalid priority => " + priorityStr);
      setArtifactIdentifyData(
            toTeam,
            fromAction.getDescriptiveName(),
            fromAction.getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName()),
            ChangeType.getChangeType(fromAction.getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName())),
            priType, fromAction.getAttributesToStringCollection(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName()),
            fromAction.getSoleBooleanAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName()),
            fromAction.getSoleDateAttributeValue(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName()));
   }

   /**
    * Since there is no shared attribute yet, action and workflow arts are all populate with identify data
    * 
    * @param art
    * @throws SQLException
    */
   public static void setArtifactIdentifyData(Artifact art, String title, String desc, ChangeType changeType, PriorityType priority, Collection<String> userComms, boolean validationRequired, Date needByDate) throws SQLException {
      art.setDescriptiveName(title);
      if (!desc.equals("")) art.setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
      art.setSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), changeType.name());
      DynamicAttributeManager dam = art.getAttributeManager(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName());
      for (String comm : userComms)
         dam.getNewAttribute().setStringData(comm);
      if (priority != null) art.setSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(),
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
   public String getWorldViewDecision() {
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
   public String getWorldViewDescription() {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewValidationRequired()
    */
   public String getWorldViewValidationRequiredStr() {
      try {
         return String.valueOf(getSoleBooleanAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName()));
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return ex.getLocalizedMessage();
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
   public String getWorldViewCancelledDateStr() {
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

   public String getWorldViewReleaseDateStr() {
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
   public String getWorldViewCompletedDateStr() {
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
   public String getWorldViewEstimatedReleaseDateStr() {
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
   public Result isWorldViewRemainHoursValid() {
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
   public Result isWorldViewManDaysNeededValid() {
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
   public String getWorldViewChangeTypeStr() {
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
   public String getWorldViewImplementer() {
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
   public String getWorldViewDeadlineDateStr() {
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
   public double getWorldViewWeeklyBenefit() {
      double hours = 0;
      try {
         // Add up hours for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            SMAManager smaMgr = new SMAManager(team);
            if (!smaMgr.isCompleted() && !smaMgr.isCancelled()) hours += team.getWorldViewWeeklyBenefit();
         }
         return hours;
      } catch (SQLException ex) {
         // Do nothing
      }
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewAnnualCostAvoidance()
    */
   public double getWorldViewAnnualCostAvoidance() {
      double hours = 0;
      try {
         // Add up hours for all children
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            SMAManager smaMgr = new SMAManager(team);
            if (!smaMgr.isCompleted() && !smaMgr.isCancelled()) hours += team.getWorldViewAnnualCostAvoidance();
         }
         return hours;
      } catch (SQLException ex) {
         // Do nothing
      }
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewAnnualCostAvoidanceValid()
    */
   public Result isWorldViewAnnualCostAvoidanceValid() {
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
   public Result isWorldViewDeadlineAlerting() {
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
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isMetricsFromTasks()
    */
   public boolean isMetricsFromTasks() {
      boolean metricsFromTasks = false;
      try {
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (team.getSoleBooleanAttributeValue(ATSAttributes.METRICS_FROM_TASKS_ATTRIBUTE.getStoreName())) metricsFromTasks =
                  true;
         }
      } catch (SQLException ex) {
         // do nothing
      }
      return metricsFromTasks;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewLegacyPCR()
    */
   public String getWorldViewLegacyPCR() {
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
   public String getWorldViewPercentReworkStr() {
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
   public int getWorldViewPercentRework() {
      return 0;
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
