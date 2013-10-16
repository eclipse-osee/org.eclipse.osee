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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
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
   public CreateNewVersionItem(XNavigateItem parent, IAtsTeamDefinition teamDefHoldingVersions) {
      super(parent,
         "Create New " + (teamDefHoldingVersions != null ? teamDefHoldingVersions + " " : "") + "Version(s)",
         FrameworkImage.VERSION);
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      IAtsTeamDefinition teamDefHoldingVersions = null;
      try {
         teamDefHoldingVersions = getReleaseableTeamDefinition();
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
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Create New Version(s)");
         Collection<IAtsVersion> newVersions =
            createVersions(resultData, transaction, teamDefHoldingVersions, newVersionNames);
         if (resultData.isErrors()) {
            resultData.log(String.format(
               "\nErrors found while creating version(s) for [%s].\nPlease resolve and try again.",
               teamDefHoldingVersions));
            XResultDataUI.report(resultData, "Create New Version Error");
            return;
         }
         transaction.execute();
         if (newVersions.size() == 1) {
            RendererManager.open(AtsClientService.get().getConfigArtifact(newVersions.iterator().next()),
               PresentationType.DEFAULT_OPEN);
         } else {
            MassArtifactEditor.editArtifacts(String.format("New Versions for [%s]", teamDefHoldingVersions),
               AtsClientService.get().getConfigArtifacts(newVersions), TableLoadOption.None);
         }

      }
   }

   public static Collection<IAtsVersion> createVersions(XResultData resultData, SkynetTransaction transaction, IAtsTeamDefinition teamDefHoldingVersions, Collection<String> newVersionNames) {
      List<IAtsVersion> versions = new ArrayList<IAtsVersion>();
      for (String newVer : newVersionNames) {
         if (!Strings.isValid(newVer)) {
            resultData.logError("Version name can't be blank");
         }
         for (IAtsVersion verArt : teamDefHoldingVersions.getVersions()) {
            if (verArt.getName().equals(newVer)) {
               resultData.logError(String.format("Version [%s] already exists", newVer));
            }
         }
      }
      if (!resultData.isErrors()) {
         try {
            for (String newVer : newVersionNames) {
               IAtsVersion version = AtsClientService.get().createVersion(newVer);
               versions.add(version);
               Artifact verArt = AtsClientService.get().storeConfigObject(version, transaction);
               AtsVersionService.get().setTeamDefinition(version, teamDefHoldingVersions);
               verArt.persist(transaction);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return versions;
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
