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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class CreateNewVersionItem extends XNavigateItemAction {

   private final TeamDefinitionArtifact teamDefHoldingVersions;

   /**
    * @param parent
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public CreateNewVersionItem(XNavigateItem parent, TeamDefinitionArtifact teamDefHoldingVersions) {
      super(parent, "Create New " + (teamDefHoldingVersions != null ? teamDefHoldingVersions + " " : "") + " Version");
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      final TeamDefinitionArtifact teamDefHoldingVersions = getReleaseableTeamDefinitionArtifact();
      if (teamDefHoldingVersions == null) return;
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Create New Version", null, "Enter Version Name",
                  MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      if (ed.open() == 0) {
         String newVer = ed.getEntry();
         if (newVer.equals("")) {
            AWorkbench.popup("ERROR", "Version name can't be blank");
            return;
         }
         for (VersionArtifact verArt : teamDefHoldingVersions.getVersionsArtifacts()) {
            if (verArt.getDescriptiveName().equals(newVer)) {
               AWorkbench.popup("ERROR", "Version already exists");
               return;
            }
         }

         try {
            Branch branch = BranchPersistenceManager.getInstance().getAtsBranch();
            new CreateNewVersionItemTx(branch, teamDefHoldingVersions, newVer).execute();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
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

   private final class CreateNewVersionItemTx extends AbstractSkynetTxTemplate {

      private TeamDefinitionArtifact teamDefHoldingVersions;
      private String newVersionName;

      public CreateNewVersionItemTx(Branch branch, TeamDefinitionArtifact teamDefHoldingVersions, String newVer) {
         super(branch);
         this.teamDefHoldingVersions = teamDefHoldingVersions;
         this.newVersionName = newVer;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws Exception {
         VersionArtifact ver =
               (VersionArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     VersionArtifact.ARTIFACT_NAME).makeNewArtifact(
                     BranchPersistenceManager.getInstance().getAtsBranch());
         ver.setDescriptiveName(newVersionName);
         teamDefHoldingVersions.relate(RelationSide.TeamDefinitionToVersion_Version, ver);
         ver.persistAttributesAndLinks();
         ArtifactEditor.editArtifact(ver);
      }

   }
}
