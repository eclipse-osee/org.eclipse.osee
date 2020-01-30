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

package org.eclipse.osee.ats.ide.config.version;

import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ReleaseVersionItem extends XNavigateItemAction {

   public static String strs[] = new String[] {};
   private final IAtsTeamDefinition teamDefHoldingVersions;

   /**
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public ReleaseVersionItem(XNavigateItem parent, IAtsTeamDefinition teamDefHoldingVersions) {
      super(parent, "Release " + (teamDefHoldingVersions != null ? teamDefHoldingVersions + " " : "") + "Version",
         FrameworkImage.VERSION);
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      IAtsTeamDefinition teamDefHoldingVersions = getReleaseableTeamDefinition();
      if (teamDefHoldingVersions == null) {
         return;
      }
      try {
         VersionListDialog dialog = new VersionListDialog("Select Version", "Select Version to Release",
            AtsClientService.get().getVersionService().getVersions(teamDefHoldingVersions,
               VersionReleaseType.UnReleased, VersionLockedType.Both));
         int result = dialog.open();
         if (result == 0) {
            IAtsVersion version = dialog.getSelectedFirst();

            // Validate team lead status
            if (!AtsClientService.get().getUserService().isAtsAdmin() && !AtsClientService.get().getTeamDefinitionService().getLeads(
               AtsClientService.get().getVersionService().getTeamDefinition(version)).contains(
                  AtsClientService.get().getUserService().getCurrentUser())) {
               AWorkbench.popup("ERROR", "Only lead can release version.");
               return;
            }
            // Validate that all Team Workflows are Completed or Cancelled
            String errorStr = null;
            for (IAtsTeamWorkflow team : AtsClientService.get().getVersionService().getTargetedForTeamWorkflows(
               version)) {
               if (!team.getStateMgr().getStateType().isCancelled() && !team.getStateMgr().getStateType().isCompleted()) {
                  errorStr =
                     "All Team Workflows must be either Completed or " + "Cancelled before releasing a version.\n\n" + team.getAtsId() + " - is in the\"" + team.getStateMgr().getCurrentStateName() + "\" state.";
               }
            }
            if (errorStr != null) {
               AWorkbench.popup("ERROR", errorStr);
            }
            if (errorStr != null && !AtsClientService.get().getUserService().isAtsAdmin()) {
               return;
            } else if (errorStr != null && !MessageDialog.openConfirm(Displays.getActiveShell(), "Override",
               "ATS Admin Enabled - Override completed condition and release anyway?")) {
               return;
            }

            IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
            if (version != null) {
               changes.setSoleAttributeValue(version, AtsAttributeTypes.NextVersion, false);
               changes.setSoleAttributeValue(version, AtsAttributeTypes.Released, true);
               changes.setSoleAttributeValue(version, AtsAttributeTypes.ReleaseDate, new Date());
            }

            changes.execute();

            if (MessageDialog.openQuestion(Displays.getActiveShell(), "Select NEW Next Release Version",
               "Release Complete.\n\nSelect NEW Next Release Version?")) {
               dialog = new VersionListDialog("Select Next Release Version", "Select New Next Release Version",
                  AtsClientService.get().getVersionService().getVersions(teamDefHoldingVersions));
               result = dialog.open();
               if (result == 0) {
                  version = dialog.getSelectedFirst();
                  if (version == null) {
                     AWorkbench.popup("ERROR", "Select a version.");
                     return;
                  }
                  changes.setSoleAttributeValue(version, AtsAttributeTypes.NextVersion, true);
                  changes.execute();
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error releasing version");
      }
   }

   public IAtsTeamDefinition getReleaseableTeamDefinition() {
      if (teamDefHoldingVersions != null) {
         return teamDefHoldingVersions;
      }
      TeamDefinitionDialog dialog = new TeamDefinitionDialog();
      dialog.setInput(
         TeamDefinitions.getTeamReleaseableDefinitions(Active.Active, AtsClientService.get().getQueryService()));
      int result = dialog.open();
      if (result == 0) {
         return dialog.getSelectedFirst();
      }
      return null;
   }

}
