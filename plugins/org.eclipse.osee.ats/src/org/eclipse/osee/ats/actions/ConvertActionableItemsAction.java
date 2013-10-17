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
package org.eclipse.osee.ats.actions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.column.ActionableItemsColumnUI;
import org.eclipse.osee.ats.core.client.action.ActionArtifactRollup;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ConvertActionableItemsAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public ConvertActionableItemsAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Convert to Actionable Item/Team");
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         Collection<TeamWorkFlowArtifact> teamArts =
            Collections.castMatching(TeamWorkFlowArtifact.class, selectedAtsArtifacts.getSelectedSMAArtifacts());
         if (teamArts.isEmpty()) {
            throw new OseeStateException("No TeamWorkflows selected");
         }

         //         TeamWorkFlowArtifact teamArt = teamArts.iterator().next();
         AWorkbench.popup("Capability disabled in this release.  Add Actionable Item and cancel old workflow instead.");
         return;
         //         Result result = convertActionableItems(teamArt);
         //         if (result.isFalse() && !result.getText().equals("")) {
         //            AWorkbench.popup(result);
         //         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @SuppressWarnings("unused")
   private Result convertActionableItems(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Result toReturn = Result.FalseResult;
      AICheckTreeDialog diag =
         new AICheckTreeDialog(
            "Convert Impacted Actionable Items",
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
            "never chosen.\n\n" + "Current Actionable Item(s): " + ActionableItemsColumnUI.getActionableItemsStr(teamArt) + "\n" +
            //
            "Current Team: " + teamArt.getTeamDefinition().getName() + "\n" +
            //
            "Select SINGLE Actionable Item below to convert this workflow to.\n\n" +
            //
            "You will be prompted to confirm this conversion.", Active.Both);

      diag.setInput(ActionableItems.getTopLevelActionableItems(Active.Both));
      if (diag.open() != 0) {
         return Result.FalseResult;
      }
      if (diag.getChecked().isEmpty()) {
         return new Result("At least one actionable item must must be selected.");
      }
      if (diag.getChecked().size() > 1) {
         return new Result("Only ONE actionable item can be selected for converts");
      }
      IAtsActionableItem selectedAia = diag.getChecked().iterator().next();
      Collection<IAtsTeamDefinition> teamDefs = ActionableItems.getImpactedTeamDefs(Arrays.asList(selectedAia));
      if (teamDefs.size() == 1) {
         IAtsTeamDefinition newTeamDef = teamDefs.iterator().next();
         // NEED TO FIX THIS
         /**
          * In order for conversion to work, need to validate work defs are same and valid for new AI, check the
          * attribute work def to see if it's valid anymore and make sure current state is valid for new work def. <br/>
          * <br/>
          * May not want to allow this feature anymore
          */
         if (newTeamDef.equals(teamArt.getTeamDefinition())) {
            toReturn =
               new Result(
                  "Actionable Item selected belongs to same team as currently selected team.\n" + "Use \"Edit Actionable Items\" instead.");
         }
         //         else if (!newTeamDef.getWorkDefinition().equals(teamArt.getTeamDefinition())) {
         //            toReturn =
         //               new Result(
         //                  "Work Definitions configuration is not the same for these teams.  Use \"Edit Actionable Items\" instead.");
         //         }
         Result result = isResultingArtifactTypesSame(newTeamDef, teamArt, selectedAia);
         if (result.isFalse()) {
            return result;
         }
         StringBuffer sb = new StringBuffer("Converting...\nActionable Item(s): ");
         sb.append(ActionableItemsColumnUI.getActionableItemsStr(teamArt));
         sb.append("\nTeam: ");
         sb.append(teamArt.getTeamDefinition().getName());
         sb.append("\nto\nActionable Item(s): ");
         sb.append(selectedAia);
         sb.append("\nTeam: ");
         sb.append(newTeamDef.getName());
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Confirm Convert", sb.toString())) {
            Set<IAtsActionableItem> toProcess = new HashSet<IAtsActionableItem>();
            toProcess.add(selectedAia);
            toReturn = actionableItemsTx(teamArt, AtsUtil.getAtsBranch(), toProcess, newTeamDef);
         }

      } else {
         toReturn = new Result("Single team can not retrieved for " + selectedAia.getName());
      }
      return toReturn;
   }

   private Result isResultingArtifactTypesSame(IAtsTeamDefinition newTeamDef, TeamWorkFlowArtifact teamArt, IAtsActionableItem newAI) throws OseeCoreException {
      IArtifactType newTeamWorkflowArtifactType =
         TeamWorkFlowManager.getTeamWorkflowArtifactType(newTeamDef, Arrays.asList(newAI));
      if (!newTeamWorkflowArtifactType.equals(teamArt.getArtifactType())) {
         return new Result(
            String.format(
               "Can not convert because new workflow type [%s] does not match old type [%s].  Use \"Edit Actionable Items\" instead.",
               newTeamWorkflowArtifactType, teamArt.getArtifactType()));
      }
      return Result.TrueResult;
   }

   private Result actionableItemsTx(TeamWorkFlowArtifact teamArt, Branch branch, Set<IAtsActionableItem> selectedAlias, IAtsTeamDefinition teamDef) throws OseeCoreException {
      Result workResult = teamArt.getActionableItemsDam().setActionableItems(selectedAlias);
      if (workResult.isTrue()) {
         if (teamDef != null) {
            teamArt.setTeamDefinition(teamDef);
         }
         SkynetTransaction transaction = TransactionManager.createTransaction(branch, "Convert Actionable Item");
         ActionArtifactRollup rollup = new ActionArtifactRollup(teamArt.getParentActionArtifact());
         rollup.resetAttributesOffChildren();
         teamArt.getParentActionArtifact().persist(transaction);
         teamArt.persist(transaction);
         transaction.execute();
      }
      return workResult;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TEAM_DEFINITION);
   }

   public void updateEnablement() {
      try {
         Collection<TeamWorkFlowArtifact> teamArts =
            Collections.castMatching(TeamWorkFlowArtifact.class, selectedAtsArtifacts.getSelectedSMAArtifacts());
         setEnabled(teamArts.size() == 1);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
   }
}
