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

package org.eclipse.osee.ats.version;

import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      IAtsTeamDefinition teamDefHoldingVersions = getReleaseableTeamDefinition();
      if (teamDefHoldingVersions == null) {
         return;
      }
      try {
         VersionListDialog ld =
            new VersionListDialog("Select Version", "Select Version to Release", teamDefHoldingVersions.getVersions(
               VersionReleaseType.UnReleased, VersionLockedType.Both));
         int result = ld.open();
         if (result == 0) {
            IAtsVersion verArt = (IAtsVersion) ld.getResult()[0];

            // Validate team lead status
            if (!AtsUtilCore.isAtsAdmin() && !AtsVersionService.get().getTeamDefinition(verArt).getLeads().contains(
               AtsClientService.get().getUserAdmin().getCurrentUser())) {
               AWorkbench.popup("ERROR", "Only lead can release version.");
               return;
            }
            // Validate that all Team Workflows are Completed or Cancelled
            String errorStr = null;
            for (TeamWorkFlowArtifact team : AtsClientService.get().getAtsVersionService().getTargetedForTeamWorkflowArtifacts(
               verArt)) {
               if (!team.isCancelled() && !team.isCompleted()) {
                  errorStr =
                     "All Team Workflows must be either Completed or " + "Cancelled before releasing a version.\n\n" + team.getAtsId() + " - is in the\"" + team.getStateMgr().getCurrentStateName() + "\" state.";
               }
            }
            if (errorStr != null) {
               AWorkbench.popup("ERROR", errorStr);
            }
            if (errorStr != null && !AtsUtilCore.isAtsAdmin()) {
               return;
            } else if (errorStr != null && !MessageDialog.openConfirm(Displays.getActiveShell(), "Override",
               "ATS Admin Enabled - Override completed condition and release anyway?")) {
               return;
            }

            verArt.setReleased(true);
            verArt.setReleaseDate(new Date());
            verArt.setNextVersion(false);
            AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
            AtsClientService.get().storeConfigObject(verArt, changes);
            changes.execute();

            if (MessageDialog.openQuestion(Displays.getActiveShell(), "Select NEW Next Release Version",
               "Release Complete.\n\nSelect NEW Next Release Version?")) {
               ld =
                  new VersionListDialog("Select Next Release Version", "Select New Next Release Version",
                     teamDefHoldingVersions.getVersions());
               result = ld.open();
               if (result == 0) {
                  verArt = (IAtsVersion) ld.getResult()[0];
                  verArt.setNextVersion(true);
                  changes.clear();
                  AtsClientService.get().storeConfigObject(verArt, changes);
                  changes.execute();
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error releasing version");
      }
   }

   public IAtsTeamDefinition getReleaseableTeamDefinition() throws OseeCoreException {
      if (teamDefHoldingVersions != null) {
         return teamDefHoldingVersions;
      }
      TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
      ld.setInput(TeamDefinitions.getTeamReleaseableDefinitions(Active.Active));
      int result = ld.open();
      if (result == 0) {
         return (IAtsTeamDefinition) ld.getResult()[0];
      }
      return null;
   }
}
