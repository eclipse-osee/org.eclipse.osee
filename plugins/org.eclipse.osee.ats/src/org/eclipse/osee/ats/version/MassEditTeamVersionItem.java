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

import java.util.List;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class MassEditTeamVersionItem extends XNavigateItemAction {

   private final IAtsTeamDefinition teamDef;
   private IAtsTeamDefinition selectedTeamDef;

   public MassEditTeamVersionItem(String name, XNavigateItem parent, String teamDefName, KeyedImage oseeImage) {
      super(parent, name, oseeImage);
      this.teamDef = null;
   }

   public MassEditTeamVersionItem(XNavigateItem parent, IAtsTeamDefinition teamDef, KeyedImage oseeImage) {
      this("Show Team Versions", parent, teamDef, oseeImage);
   }

   public MassEditTeamVersionItem(String name, XNavigateItem parent, IAtsTeamDefinition teamDef, KeyedImage oseeImage) {
      super(parent, name, oseeImage);
      this.teamDef = teamDef;
   }

   private IAtsTeamDefinition getTeamDefinition() throws OseeCoreException {
      if (selectedTeamDef != null) {
         return selectedTeamDef;
      }
      if (teamDef != null) {
         return teamDef;
      }
      TeamDefinitionDialog dialog = new TeamDefinitionDialog();
      dialog.setInput(TeamDefinitions.getTeamReleaseableDefinitions(Active.Active, AtsClientService.get().getConfig()));
      int result = dialog.open();
      if (result == 0) {
         return dialog.getSelectedFirst();
      }
      return null;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         IAtsTeamDefinition teamDef = getTeamDefinition();
         if (teamDef == null) {
            return;
         }
         if (teamDef.getTeamDefinitionHoldingVersions() == null) {
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
      return AtsClientService.get().getConfigArtifacts(teamDef.getTeamDefinitionHoldingVersions().getVersions());
   }

   /**
    * @param selectedTeamDef the selectedTeamDef to set
    */
   public void setSelectedTeamDef(IAtsTeamDefinition selectedTeamDef) {
      this.selectedTeamDef = selectedTeamDef;
   }

}
