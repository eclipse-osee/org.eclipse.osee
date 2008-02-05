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

package org.eclipse.osee.ats.navigate;

import java.sql.SQLException;
import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ReleaseVersionItem extends XNavigateItemAction {

   public static String strs[] = new String[] {};
   private final TeamDefinitionArtifact teamDefHoldingVersions;

   /**
    * @param parent
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public ReleaseVersionItem(XNavigateItem parent, TeamDefinitionArtifact teamDefHoldingVersions) {
      super(parent, "Release " + (teamDefHoldingVersions != null ? teamDefHoldingVersions + " " : "") + "Version");
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      TeamDefinitionArtifact teamDefHoldingVersions = getReleaseableTeamDefinitionArtifact();
      if (teamDefHoldingVersions == null) return;
      try {
         VersionListDialog ld =
               new VersionListDialog("Select Version", "Select Version to Release",
                     teamDefHoldingVersions.getVersionsArtifacts());
         int result = ld.open();
         if (result == 0) {
            VersionArtifact verArt = (VersionArtifact) ld.getResult()[0];

            // Validate team lead status
            if (!AtsPlugin.isAtsAdmin() && !verArt.getParentTeamDefinition().getLeads().contains(
                  SkynetAuthentication.getInstance().getAuthenticatedUser())) {
               AWorkbench.popup("ERROR", "Only lead can release version.");
               return;
            }
            // Validate that all Team Workflows are Completed or Cancelled
            String errorStr = null;
            for (TeamWorkFlowArtifact team : verArt.getTargetedForTeamArtifacts()) {
               SMAManager smaMgr = new SMAManager(team);
               if (!smaMgr.isCancelled() && !smaMgr.isCompleted()) {
                  errorStr =
                        "All Team Workflows must be either Completed or " + "Cancelled before releasing a version.\n\n" + team.getHumanReadableId() + " - is in the\"" + team.getCurrentStateName() + "\" state.";
               }
            }
            if (errorStr != null) AWorkbench.popup("ERROR", errorStr);
            if (errorStr != null && !AtsPlugin.isAtsAdmin())
               return;
            else if (errorStr != null && !MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Override",
                  "ATS Admin Enabled - Override completed condition and release anyway?")) {
               return;
            }

            verArt.setSoleBooleanAttributeValue(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), true);
            verArt.setSoleDateAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(), new Date());
            verArt.setNextVersion(false);
            verArt.persistAttributes();

            if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Select NEW Next Release Version",
                  "Release Complete.\n\nSelect NEW Next Release Version?")) {
               ld =
                     new VersionListDialog("Select Next Release Version", "Select New Next Release Version",
                           teamDefHoldingVersions.getVersionsArtifacts());
               result = ld.open();
               if (result == 0) {
                  verArt = (VersionArtifact) ld.getResult()[0];
                  verArt.setNextVersion(true);
                  verArt.persistAttributes();
               }
            }
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, "Error releasing version", ex, true);
      }
   }

   public TeamDefinitionArtifact getReleaseableTeamDefinitionArtifact() throws SQLException {
      if (teamDefHoldingVersions != null) return teamDefHoldingVersions;
      TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
      ld.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Active));
      int result = ld.open();
      if (result == 0) {
         return (TeamDefinitionArtifact) ld.getResult()[0];
      }
      return null;
   }
}
