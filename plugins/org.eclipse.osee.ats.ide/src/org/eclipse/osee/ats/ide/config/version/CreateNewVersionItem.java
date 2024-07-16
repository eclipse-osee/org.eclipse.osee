/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.config.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.util.AtsRelationChange;
import org.eclipse.osee.ats.core.util.AtsRelationChange.RelationOperation;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateNewVersionItem extends XNavigateItemAction {

   private final IAtsTeamDefinition teamDefHoldingVersions;

   /**
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public CreateNewVersionItem(IAtsTeamDefinition teamDefHoldingVersions) {
      this(teamDefHoldingVersions, AtsNavigateViewItems.ATS_VERSIONS);
   }

   public CreateNewVersionItem(IAtsTeamDefinition teamDefHoldingVersions, XNavItemCat xNavItemCat) {
      super("Create New " + (teamDefHoldingVersions != null ? teamDefHoldingVersions + " " : "") + "Version(s) (Admin)",
         FrameworkImage.VERSION, xNavItemCat);
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      IAtsTeamDefinition teamDefHoldingVersions = null;
      try {
         teamDefHoldingVersions = getReleaseableTeamDefinition();
      } catch (Exception ex) {
         // do nothing
      }
      if (teamDefHoldingVersions == null) {
         return;
      }
      EntryDialog ed = new EntryDialog(Displays.getActiveShell(), "Create New Version", null,
         "Enter Version name(s) one per line", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      ed.setFillVertically(true);
      if (ed.open() == 0) {
         Set<String> newVersionNames = new HashSet<>();
         for (String str : ed.getEntry().split(System.getProperty("line.separator"))) {
            newVersionNames.add(str);
         }
         XResultData resultData = new XResultData(false);
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Create New Version(s)");
         Collection<IAtsVersion> newVersions =
            createVersions(resultData, changes, teamDefHoldingVersions, newVersionNames);
         if (resultData.isErrors()) {
            resultData.log(
               String.format("\nErrors found while creating version(s) for [%s].\nPlease resolve and try again.",
                  teamDefHoldingVersions));
            XResultDataUI.report(resultData, "Create New Version Error");
            return;
         }
         changes.execute();
         if (newVersions.size() == 1) {
            RendererManager.open(AtsApiService.get().getQueryServiceIde().getArtifact(newVersions.iterator().next()),
               PresentationType.DEFAULT_OPEN);
         } else {
            Collection<ArtifactToken> artToks =
               AtsApiService.get().getQueryService().getArtifactsFromObjects(newVersions);
            List<Artifact> arts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(artToks);
            MassArtifactEditor.editArtifacts(String.format("New Versions for [%s]", teamDefHoldingVersions), arts,
               TableLoadOption.None);
         }

      }
   }

   public static Collection<IAtsVersion> createVersions(XResultData resultData, IAtsChangeSet changes, IAtsTeamDefinition teamDefHoldingVersions, Collection<String> newVersionNames) {
      List<IAtsVersion> versions = new ArrayList<>();
      for (String newVer : newVersionNames) {
         if (!Strings.isValid(newVer)) {
            resultData.error("Version name can't be blank");
         }
         for (IAtsVersion verArt : AtsApiService.get().getVersionService().getVersions(teamDefHoldingVersions)) {
            if (verArt.getName().equals(newVer)) {
               resultData.error(String.format("Version [%s] already exists", newVer));
            }
         }
      }
      if (!resultData.isErrors()) {
         try {
            for (String newVer : newVersionNames) {
               IAtsVersion version = AtsApiService.get().getVersionService().createVersion(newVer, changes);
               versions.add(version);
               changes.add(version);
               changes.add(
                  new AtsRelationChange(teamDefHoldingVersions, AtsRelationTypes.TeamDefinitionToVersion_Version,
                     Collections.singleton(version), RelationOperation.Add));
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return versions;
   }

   public IAtsTeamDefinition getReleaseableTeamDefinition() {
      if (teamDefHoldingVersions != null) {
         return teamDefHoldingVersions;
      }
      TeamDefinitionDialog dialog = new TeamDefinitionDialog();
      dialog.setInput(AtsApiService.get().getTeamDefinitionService().getTeamReleaseableDefinitions(Active.Active));
      int result = dialog.open();
      if (result == 0) {
         return dialog.getSelectedFirst();
      }
      return null;
   }

}
