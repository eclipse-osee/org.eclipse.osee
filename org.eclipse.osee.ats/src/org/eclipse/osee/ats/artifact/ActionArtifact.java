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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.XActionableItemsDam;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
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
    */
   public ActionArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
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

   public void resetAttributesOffChildren(SkynetTransaction transaction) throws OseeCoreException {
      resetActionItemsOffChildren();
      resetChangeTypeOffChildren();
      resetPriorityOffChildren();
      resetUserCommunityOffChildren();
      resetTitleOffChildren();
      resetValidationOffChildren();
      resetDescriptionOffChildren();
      persistAttributes(transaction);
   }

   public boolean hasAtsWorldChildren() throws OseeCoreException {
      return true;
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetTitleOffChildren() throws OseeCoreException {
      String title = "";
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (title.equals(""))
            title = team.getDescriptiveName();
         else if (!title.equals(team.getDescriptiveName())) return;
      }
      if (!title.equals(getDescriptiveName())) {
         setDescriptiveName(title);
      }
   }

   // Set validation to true if any require validation
   private void resetValidationOffChildren() throws OseeCoreException {
      boolean validationRequired = false;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (team.getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false)) validationRequired =
               true;
      }
      if (validationRequired != getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false)) setSoleAttributeValue(
            ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), validationRequired);
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetDescriptionOffChildren() throws OseeCoreException {
      String desc = "";
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (desc.equals(""))
            desc = team.getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
         else if (!desc.equals(team.getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), ""))) return;
      }
      if (!desc.equals(getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), ""))) {
         setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
      }
      if (desc.equals("")) {
         deleteSoleAttribute(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName());
      }
   }

   private Result resetActionItemsOffChildren() throws OseeCoreException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts())
         if (getTeamWorkFlowArtifacts().size() == 1 || !(new SMAManager(team)).isCancelled()) {
            aias.addAll(team.getActionableItemsDam().getActionableItems());
         }
      return actionableItemsDam.setActionableItems(aias);
   }

   private void resetChangeTypeOffChildren() throws OseeCoreException {
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

   private void resetPriorityOffChildren() throws OseeCoreException {
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

   private void resetUserCommunityOffChildren() throws OseeCoreException {
      Set<String> userComs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!(new SMAManager(team)).isCancelled()) {
            userComs.addAll(team.getAttributesToStringList(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName()));
         }
      }
      setAttributeValues(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName(), userComs);
   }

   public void setChangeType(ChangeType type) throws OseeCoreException {
      setSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), type.name());
   }

   public ChangeType getChangeType() throws OseeCoreException {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ""));
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

   public Collection<TeamWorkFlowArtifact> getTeamWorkFlowArtifacts() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.ActionToWorkflow_WorkFlow, TeamWorkFlowArtifact.class);
   }

   public String getWorldViewType() throws OseeCoreException {
      return ARTIFACT_NAME;
   }

   public String getWorldViewTitle() throws OseeCoreException {
      return getDescriptiveName();
   }

   public ChangeType getWorldViewChangeType() throws OseeCoreException {
      return ChangeType.getChangeType(getSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ""));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewBranchStatus()
    */
   public String getWorldViewBranchStatus() throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getWorldViewBranchStatus().equals("")) sb.append(team.getWorldViewBranchStatus() + ", ");
      }
      return sb.toString().replaceFirst(", $", "");
   }

   public String getWorldViewNumberOfTasks() throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getWorldViewNumberOfTasks().equals("")) {
            sb.append(team.getWorldViewNumberOfTasks() + ", ");
         }
      }
      return sb.toString().replaceFirst(", $", "");
   }

   public String getWorldViewState() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewState());
      }
      return Collections.toString(";", strs);
   }

   @Override
   public String getWorldViewActivePoc() throws OseeCoreException {
      Set<User> pocs = new HashSet<User>();
      Set<User> implementers = new HashSet<User>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (team.getSmaMgr().isCancelledOrCompleted()) {
            implementers.addAll(team.getImplementers());
         } else {
            pocs.addAll(team.getSmaMgr().getStateMgr().getAssignees());
         }
      }
      return Artifacts.toString("; ", pocs) + (implementers.size() > 0 ? "(" + Artifacts.toString("; ", implementers) + ")" : "");
   }

   public String getWorldViewCreatedDateStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         Date date = team.getWorldViewCreatedDate();
         if (date == null) {
            strs.add("");
         } else {
            strs.add(XDate.getDateStr(team.getWorldViewCreatedDate(), XDate.MMDDYYHHMM));
         }
      }
      return Collections.toString(";", strs);
   }

   public Date getWorldViewCreatedDate() throws OseeCoreException {
      return getTeamWorkFlowArtifacts().iterator().next().getWorldViewCreatedDate();
   }

   public String getWorldViewID() throws OseeCoreException {
      return getHumanReadableId();
   }

   public String getWorldViewPriority() throws OseeCoreException {
      return PriorityType.getPriority(getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), "")).getShortName();
   }

   public Image getAssigneeImage() throws OseeCoreException {
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         Image image = team.getAssigneeImage();
         if (image != null) return image;
      }
      return null;
   }

   public String getWorldViewUserCommunity() throws OseeCoreException {
      return getAttributesToString(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName());
   }

   public String getWorldViewActionableItems() throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (ActionableItemArtifact aia : getActionableItemsDam().getActionableItems()) {
         sb.append(aia.getDescriptiveName() + ", ");
      }
      return sb.toString().replaceFirst(", $", "");
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
      // Delete all products
      for (TeamWorkFlowArtifact art : getRelatedArtifacts(AtsRelation.ActionToWorkflow_WorkFlow,
            TeamWorkFlowArtifact.class))
         art.atsDelete(deleteArts, allRelated);
   }

   public String getWorldViewTeam() throws OseeCoreException {
      Set<TeamDefinitionArtifact> teams = new HashSet<TeamDefinitionArtifact>();
      // Roll up if same for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         teams.add(team.getTeamDefinition());
      }
      return Artifacts.commaArts(teams);
   }

   public String getWorldViewOriginator() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewOriginator());
      }
      return Collections.toString(";", strs);
   }

   @Override
   public String getWorldViewCompletedDateStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewCompletedDateStr());
      }
      return Collections.toString(";", strs);
   }

   @Override
   public Date getWorldViewCompletedDate() throws OseeCoreException {
      return getTeamWorkFlowArtifacts().iterator().next().getWorldViewCompletedDate();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewCancelledDateStr()
    */
   public String getWorldViewCancelledDateStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewCancelledDateStr());
      }
      return Collections.toString(";", strs);
   }

   public Date getWorldViewCancelledDate() throws OseeCoreException {
      return getTeamWorkFlowArtifacts().iterator().next().getWorldViewCancelledDate();
   }

   public String getWorldViewResolution() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewResolution());
      }
      return Collections.toString(";", strs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewRemainHours()
    */
   public double getWorldViewRemainHours() throws OseeCoreException {
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
   public double getWorldViewManDaysNeeded() throws OseeCoreException {
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
   public double getWorldViewEstimatedHours() throws OseeCoreException {
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
   public int getWorldViewStatePercentComplete() throws OseeCoreException {
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
   public String getWorldViewRelatedToState() throws OseeCoreException {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewNotes()
    */
   @Override
   public String getWorldViewNotes() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewNotes());
      }
      return Collections.toString(";", strs);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewGroups()
    */
   @Override
   public String getWorldViewGroups() throws OseeCoreException {
      Set<Artifact> groups = new HashSet<Artifact>();
      groups.addAll(getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__GROUP));
      // Roll up if same for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         groups.addAll(team.getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__GROUP));
      }
      return Artifacts.toString("; ", groups);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewCategory()
    */
   public String getWorldViewCategory() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewCategory());
      }
      return Collections.toString(";", strs);
   }

   public String getWorldViewWorkPackage() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewWorkPackage());
      }
      return Collections.toString(";", strs);
   }

   public String getWorldViewCategory2() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewCategory2());
      }
      return Collections.toString(";", strs);
   }

   public String getWorldViewCategory3() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewCategory3());
      }
      return Collections.toString(";", strs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewVersion()
    */
   public String getWorldViewTargetedVersionStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewTargetedVersionStr());
      }
      return Collections.toString(";", strs);
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
      try {
         return getArtifactTypeName();
      } catch (Exception ex) {
         return ex.getLocalizedMessage();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperState()
    */
   public String getHyperState() {
      try {
         if (getTeamWorkFlowArtifacts().size() == 1) {
            return getTeamWorkFlowArtifacts().iterator().next().getHyperState();
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperTargetVersion()
    */
   @Override
   public String getHyperTargetVersion() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperAssignee()
    */
   public String getHyperAssignee() {
      try {
         if (getTeamWorkFlowArtifacts().size() == 1) {
            return getTeamWorkFlowArtifacts().iterator().next().getHyperAssignee();
         }
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
   public Image getHyperAssigneeImage() throws OseeCoreException {
      if (getTeamWorkFlowArtifacts().size() == 1) {
         return getTeamWorkFlowArtifacts().iterator().next().getHyperAssigneeImage();
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

   public Result addActionableItems() throws OseeCoreException {
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

      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());

      for (ActionableItemArtifact aia : diag.getChecked()) {
         Result result = addActionableItemToTeamsOrAddTeams(aia, transaction);
         sb.append(result.getText());
      }
      transaction.execute();
      return new Result(true, sb.toString());
   }

   public Result addActionableItemToTeamsOrAddTeams(ActionableItemArtifact aia, SkynetTransaction transaction) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (TeamDefinitionArtifact tda : TeamDefinitionArtifact.getImpactedTeamDefs(Arrays.asList(aia))) {
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
            createTeamWorkflow(tda, Arrays.asList(aia), tda.getLeads(), transaction);
            sb.append(aia.getDescriptiveName() + " => added team workflow \"" + tda.getDescriptiveName() + "\"\n");
         }
      }
      return new Result(true, sb.toString());
   }

   public TeamWorkFlowArtifact createTeamWorkflow(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems, Collection<User> assignees, SkynetTransaction transaction) throws OseeCoreException {
      String teamWorkflowArtifactName = TeamWorkFlowArtifact.ARTIFACT_NAME;
      IAtsTeamWorkflow teamExt = null;

      // Check if any plugins want to create the team workflow themselves
      for (IAtsTeamWorkflow teamExtension : TeamWorkflowExtensions.getInstance().getAtsTeamWorkflowExtensions()) {
         boolean isResponsible = false;
         try {
            isResponsible = teamExtension.isResponsibleForTeamWorkflowCreation(teamDef, actionableItems);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.WARNING, ex);
         }
         if (isResponsible) {
            teamWorkflowArtifactName = teamExtension.getTeamWorkflowArtifactName(teamDef, actionableItems);
            teamExt = teamExtension;
         }
      }

      // NOTE: The persist of the workflow will auto-email the assignees
      TeamWorkFlowArtifact teamArt =
            createTeamWorkflow(teamDef, actionableItems, assignees, teamWorkflowArtifactName, transaction);
      // Notify extension that workflow was created
      if (teamExt != null) teamExt.teamWorkflowCreated(teamArt);
      return teamArt;
   }

   public TeamWorkFlowArtifact createTeamWorkflow(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems, Collection<User> assignees, String artifactName, SkynetTransaction transaction) throws OseeCoreException {
      return createTeamWorkflow(teamDef, actionableItems, assignees, null, null, artifactName, transaction);
   }

   public TeamWorkFlowArtifact createTeamWorkflow(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems, Collection<User> assignees, String guid, String hrid, String artifactName, SkynetTransaction transaction) throws OseeCoreException {

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
         teamArt = (TeamWorkFlowArtifact) ArtifactTypeManager.addArtifact(artifactName, AtsPlugin.getAtsBranch());
      else
         teamArt =
               (TeamWorkFlowArtifact) ArtifactTypeManager.addArtifact(artifactName, AtsPlugin.getAtsBranch(), guid,
                     hrid);
      setArtifactIdentifyData(this, teamArt);

      teamArt.getSmaMgr().getLog().addLog(LogType.Originated, "", "");

      // Relate Workflow to ActionableItems (by guid) if team is responsible
      // for that AI
      for (ActionableItemArtifact aia : actionableItems)
         if (aia.getImpactedTeamDefs().contains(teamDef)) teamArt.getActionableItemsDam().addActionableItem(aia);

      // Initialize state machine
      teamArt.getSmaMgr().getStateMgr().initializeStateMachine(DefaultTeamState.Endorse.name(), assignees);
      teamArt.getSmaMgr().getLog().addLog(LogType.StateEntered, DefaultTeamState.Endorse.name(), "");

      // Relate WorkFlow to Team Definition (by guid due to relation loading
      // issues)
      teamArt.setTeamDefinition(teamDef);

      // Relate Action to WorkFlow
      addRelation(AtsRelation.ActionToWorkflow_WorkFlow, teamArt);

      teamArt.persistAttributesAndRelations(transaction);

      return teamArt;
   }

   /**
    * Set Team Workflow attributes off given action artifact
    * 
    * @param fromAction
    * @param toTeam
    * @throws OseeArgumentException
    */
   public static void setArtifactIdentifyData(ActionArtifact fromAction, TeamWorkFlowArtifact toTeam) throws OseeCoreException {
      String priorityStr = fromAction.getSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), "");
      PriorityType priType = null;
      if (priorityStr.equals(""))
         priType = null;
      else if (!priorityStr.equals(""))
         priType = PriorityType.getPriority(priorityStr);
      else
         throw new OseeArgumentException("Invalid priority => " + priorityStr);
      setArtifactIdentifyData(toTeam, fromAction.getDescriptiveName(), fromAction.getSoleAttributeValue(
            ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), ""),
            ChangeType.getChangeType(fromAction.getSoleAttributeValue(
                  ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), "")), priType,
            fromAction.getAttributesToStringList(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName()),
            fromAction.getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false),
            fromAction.getSoleAttributeValue(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName(), null, Date.class));
   }

   /**
    * Since there is no shared attribute yet, action and workflow arts are all populate with identify data
    * 
    * @param art
    */
   public static void setArtifactIdentifyData(Artifact art, String title, String desc, ChangeType changeType, PriorityType priority, Collection<String> userComms, Boolean validationRequired, Date needByDate) throws OseeCoreException {
      art.setDescriptiveName(title);
      if (!desc.equals("")) art.setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
      art.setSoleAttributeValue(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), changeType.name());
      art.setAttributeValues(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName(), userComms);
      if (priority != null) art.setSoleAttributeValue(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(),
            priority.getShortName());
      if (needByDate != null) art.setSoleAttributeValue(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName(), needByDate);
      if (validationRequired) art.setSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(),
            true);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDecision()
    */
   public String getWorldViewDecision() throws OseeCoreException {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact#getParentSMArt()
    */
   public Artifact getParentAtsArtifact() throws OseeCoreException {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDescription()
    */
   public String getWorldViewDescription() throws OseeCoreException {
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
   public String getWorldViewValidationRequiredStr() throws OseeCoreException {
      try {
         return String.valueOf(getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false));
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   @Override
   public Date getWorldViewEstimatedReleaseDate() throws OseeCoreException {
      return getTeamWorkFlowArtifacts().iterator().next().getWorldViewEstimatedReleaseDate();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewEstimatedCompletionDateStr()
    */
   public String getWorldViewEstimatedCompletionDateStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewEstimatedCompletionDateStr());
      }
      return Collections.toString(";", strs);
   }

   @Override
   public Date getWorldViewEstimatedCompletionDate() throws OseeCoreException {
      return getTeamWorkFlowArtifacts().iterator().next().getWorldViewEstimatedCompletionDate();
   }

   @Override
   public String getWorldViewReleaseDateStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewReleaseDateStr());
      }
      return Collections.toString(";", strs);
   }

   public Date getWorldViewReleaseDate() throws OseeCoreException {
      return getTeamWorkFlowArtifacts().iterator().next().getWorldViewReleaseDate();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewEstimatedReleaseDateStr()
    */
   public String getWorldViewEstimatedReleaseDateStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewEstimatedReleaseDateStr());
      }
      return Collections.toString(";", strs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewRemainHoursValid()
    */
   public Result isWorldViewRemainHoursValid() throws OseeCoreException {
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (team.isWorldViewRemainHoursValid().isFalse()) {
            return team.isWorldViewRemainHoursValid();
         }
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
      } catch (OseeCoreException ex) {
         // Do nothing
      }
      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewChangeTypeStr()
    */
   public String getWorldViewChangeTypeStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewChangeTypeStr());
      }
      return Collections.toString(";", strs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewImplementer()
    */
   public String getWorldViewImplementer() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewImplementer());
      }
      return Collections.toString(";", strs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   public Date getWorldViewDeadlineDate() throws OseeCoreException {
      return getTeamWorkFlowArtifacts().iterator().next().getWorldViewDeadlineDate();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   public String getWorldViewDeadlineDateStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewDeadlineDateStr());
      }
      return Collections.toString(";", strs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewWeeklyBenefit()
    */
   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      double hours = 0;
      // Add up hours for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         SMAManager smaMgr = new SMAManager(team);
         if (!smaMgr.isCompleted() && !smaMgr.isCancelled()) {
            hours += team.getWorldViewWeeklyBenefit();
         }
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewAnnualCostAvoidance()
    */
   public double getWorldViewAnnualCostAvoidance() throws OseeCoreException {
      double hours = 0;
      // Add up hours for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         SMAManager smaMgr = new SMAManager(team);
         if (!smaMgr.isCompleted() && !smaMgr.isCancelled()) {
            hours += team.getWorldViewAnnualCostAvoidance();
         }
      }
      return hours;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewAnnualCostAvoidanceValid()
    */
   public Result isWorldViewAnnualCostAvoidanceValid() throws OseeCoreException {
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         Result result = team.isWorldViewAnnualCostAvoidanceValid();
         if (result.isFalse()) return result;
      }
      return Result.TrueResult;
   }

   @Override
   public Set<ArtifactAnnotation> getAnnotations() throws OseeCoreException {
      Set<ArtifactAnnotation> notifications = super.getAnnotations();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         notifications.addAll(team.getAnnotations());
      }
      return notifications;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewDeadlineAlerting()
    */
   public Result isWorldViewDeadlineAlerting() throws OseeCoreException {
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         Result result = team.isWorldViewDeadlineAlerting();
         if (result.isTrue()) return result;
      }
      return Result.FalseResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewLegacyPCR()
    */
   public String getWorldViewLegacyPCR() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewLegacyPCR());
      }
      return Collections.toString(";", strs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentReworkStr()
    */
   public String getWorldViewPercentReworkStr() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getWorldViewPercentReworkStr());
      }
      return Collections.toString(";", strs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentRework()
    */
   public int getWorldViewPercentRework() throws OseeCoreException {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewAuthor()
    */
   public String getWorldViewReviewAuthor() throws OseeCoreException {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewDecider()
    */
   public String getWorldViewReviewDecider() throws OseeCoreException {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewModerator()
    */
   public String getWorldViewReviewModerator() throws OseeCoreException {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewReviewer()
    */
   public String getWorldViewReviewReviewer() throws OseeCoreException {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentState()
    */
   @Override
   public double getWorldViewHoursSpentState() throws OseeCoreException {
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
   public double getWorldViewHoursSpentStateReview() throws OseeCoreException {
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
   public double getWorldViewHoursSpentStateTask() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            hours += team.getWorldViewHoursSpentStateTask();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentStateTotal() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.getSmaMgr().isCancelled()) {
            hours += team.getWorldViewHoursSpentStateTotal();
         }
      }
      return hours;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentTotal()
    */
   @Override
   public double getWorldViewHoursSpentTotal() throws OseeCoreException {
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
   public int getWorldViewPercentCompleteState() throws OseeCoreException {
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
   public int getWorldViewPercentCompleteStateReview() throws OseeCoreException {
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
   public int getWorldViewPercentCompleteStateTask() throws OseeCoreException {
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
   public int getWorldViewPercentCompleteTotal() throws OseeCoreException {
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

   public String getWorldViewLastUpdated() throws OseeCoreException {
      return XDate.getDateStr(getLastModified(), XDate.MMDDYYHHMM);
   }

   public String getWorldViewLastStatused() throws OseeCoreException {
      return "(see children)";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewTargetedVersion()
    */
   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      return null;
   }

   public String getWorldViewNumberOfReviewIssueDefects() throws OseeCoreException {
      return "";
   }

   public String getWorldViewNumberOfReviewMajorDefects() throws OseeCoreException {
      return "";
   }

   public String getWorldViewNumberOfReviewMinorDefects() throws OseeCoreException {
      return "";
   }

   @Override
   public String getWorldViewActionsIntiatingWorkflow() throws OseeCoreException {
      Date earliestDate = null;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (earliestDate == null || team.getSmaMgr().getLog().getCreationDate().before(earliestDate)) {
            earliestDate = team.getSmaMgr().getLog().getCreationDate();
         }
      }
      List<String> teamNames = new ArrayList<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (team.getSmaMgr().getLog().getCreationDate().equals(earliestDate)) {
            teamNames.add(team.getTeamName());
         }
      }
      return Collections.toString("; ", teamNames);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewParentID()
    */
   @Override
   public String getWorldViewParentID() throws OseeCoreException {
      return "";
   }
}
