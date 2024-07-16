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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class MassEditTeamVersionItem extends XNavigateItemAction {

   private IAtsTeamDefinition selectedTeamDef;

   public MassEditTeamVersionItem(String name, KeyedImage oseeImage) {
      super(name, oseeImage, AtsNavigateViewItems.ATS_VERSIONS_ADMIN);
   }

   public MassEditTeamVersionItem(String name, AtsImage oseeImage) {
      super(name, oseeImage, AtsNavigateViewItems.ATS_VERSIONS_ADMIN);
   }

   private IAtsTeamDefinition getTeamDefinition() {
      if (selectedTeamDef != null) {
         return selectedTeamDef;
      }
      TeamDefinitionDialog dialog = new TeamDefinitionDialog();
      dialog.setInput(AtsApiService.get().getTeamDefinitionService().getTeamReleaseableDefinitions(Active.Active));
      int result = dialog.open();
      if (result == 0) {
         return dialog.getSelectedFirst();
      }
      return null;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         selectedTeamDef = getTeamDefinition();
         if (selectedTeamDef == null) {
            return;
         }
         if (AtsApiService.get().getTeamDefinitionService().getTeamDefHoldingVersions(selectedTeamDef) == null) {
            AWorkbench.popup("ERROR", "Team is not configured to use versions.");
            return;
         }
         MassArtifactEditor.editArtifacts(getName(), getResults());
         selectedTeamDef = null;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public List<Artifact> getResults() {
      Collection<IAtsVersion> versions =
         AtsApiService.get().getVersionService().getVersionsFromTeamDefHoldingVersions(selectedTeamDef);
      Collection<ArtifactToken> verArtToks = AtsApiService.get().getQueryService().getArtifactsFromObjects(versions);
      return Collections.castAll(verArtToks);
   }

   /**
    * @param selectedTeamDef the selectedTeamDef to set
    */
   public void setSelectedTeamDef(IAtsTeamDefinition selectedTeamDef) {
      this.selectedTeamDef = selectedTeamDef;
   }

}
