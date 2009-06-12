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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
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
      super(parent, "Create New " + (teamDefHoldingVersions != null ? teamDefHoldingVersions + " " : "") + " Version",
            FrameworkImage.VERSION);
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      TeamDefinitionArtifact teamDefHoldingVersions = null;
      try {
         teamDefHoldingVersions = getReleaseableTeamDefinitionArtifact();
      } catch (Exception ex) {
         // do nothing
      }
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
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            createNewVersionItemTx(transaction, teamDefHoldingVersions, newVer);
            transaction.execute();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public TeamDefinitionArtifact getReleaseableTeamDefinitionArtifact() throws OseeCoreException {
      if (teamDefHoldingVersions != null) return teamDefHoldingVersions;
      TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
      ld.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Active));
      int result = ld.open();
      if (result == 0) {
         return (TeamDefinitionArtifact) ld.getResult()[0];
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate#handleTxWork()
    */
   private void createNewVersionItemTx(SkynetTransaction transaction, TeamDefinitionArtifact teamDefHoldingVersions, String newVer) throws OseeCoreException {
      VersionArtifact ver =
            (VersionArtifact) ArtifactTypeManager.addArtifact(VersionArtifact.ARTIFACT_NAME, AtsPlugin.getAtsBranch(),
                  newVer);
      teamDefHoldingVersions.addRelation(AtsRelation.TeamDefinitionToVersion_Version, ver);
      ver.persistAttributesAndRelations(transaction);
      ArtifactEditor.editArtifact(ver);
   }

}
