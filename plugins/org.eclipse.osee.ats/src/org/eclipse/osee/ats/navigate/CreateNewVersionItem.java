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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateNewVersionItem extends XNavigateItemAction {

   private final TeamDefinitionArtifact teamDefHoldingVersions;

   /**
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public CreateNewVersionItem(XNavigateItem parent, TeamDefinitionArtifact teamDefHoldingVersions) {
      super(parent,
         "Create New " + (teamDefHoldingVersions != null ? teamDefHoldingVersions + " " : "") + "Version(s)",
         FrameworkImage.VERSION);
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      TeamDefinitionArtifact teamDefHoldingVersions = null;
      try {
         teamDefHoldingVersions = getReleaseableTeamDefinitionArtifact();
      } catch (Exception ex) {
         // do nothing
      }
      if (teamDefHoldingVersions == null) {
         return;
      }
      EntryDialog ed =
         new EntryDialog(Displays.getActiveShell(), "Create New Version", null, "Enter Version name(s) one per line",
            MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      ed.setFillVertically(true);
      if (ed.open() == 0) {
         Set<String> newVersionNames = new HashSet<String>();
         for (String str : ed.getEntry().split(System.getProperty("line.separator"))) {
            newVersionNames.add(str);
         }
         XResultData resultData = new XResultData(false);
         for (String newVer : newVersionNames) {
            if (!Strings.isValid(newVer)) {
               resultData.logError("Version name can't be blank");
            }
            for (VersionArtifact verArt : teamDefHoldingVersions.getVersionsArtifacts()) {
               if (verArt.getName().equals(newVer)) {
                  resultData.logError(String.format("Version [%s] already exists", newVer));
               }
            }
         }
         if (!resultData.isEmpty()) {
            resultData.log(String.format(
               "\nErrors found while creating version(s) for [%s].\nPlease resolve and try again.",
               teamDefHoldingVersions));
            resultData.report("Create New Version Error");
            return;
         }
         try {
            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Create New Version(s)");
            Set<VersionArtifact> newVersions =
               createNewVersionItemTx(transaction, teamDefHoldingVersions, newVersionNames);
            transaction.execute();

            if (newVersions.size() == 1) {
               RendererManager.open(newVersions.iterator().next(), PresentationType.DEFAULT_OPEN);
            } else {
               MassArtifactEditor.editArtifacts(String.format("New Versions for [%s]", teamDefHoldingVersions),
                  newVersions, TableLoadOption.None);
            }

         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public TeamDefinitionArtifact getReleaseableTeamDefinitionArtifact() throws OseeCoreException {
      if (teamDefHoldingVersions != null) {
         return teamDefHoldingVersions;
      }
      TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
      ld.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Active));
      int result = ld.open();
      if (result == 0) {
         return (TeamDefinitionArtifact) ld.getResult()[0];
      }
      return null;
   }

   private Set<VersionArtifact> createNewVersionItemTx(SkynetTransaction transaction, TeamDefinitionArtifact teamDefHoldingVersions, Set<String> newVersionNames) throws OseeCoreException {
      Set<VersionArtifact> newVersions = new HashSet<VersionArtifact>();
      for (String newVer : newVersionNames) {
         VersionArtifact ver =
            (VersionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtil.getAtsBranch(), newVer);
         teamDefHoldingVersions.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, ver);
         ver.persist(transaction);
         newVersions.add(ver);
      }
      return newVersions;
   }
}
