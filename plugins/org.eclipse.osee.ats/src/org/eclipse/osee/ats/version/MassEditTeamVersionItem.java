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

import org.eclipse.osee.ats.core.client.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionManager;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.util.AtsCacheManager;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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

   private final String teamDefName;
   private final TeamDefinitionArtifact teamDef;
   private TeamDefinitionArtifact selectedTeamDef;

   public MassEditTeamVersionItem(XNavigateItem parent, String teamDefName, KeyedImage oseeImage) {
      this("Show Team Versions", parent, teamDefName, oseeImage);
   }

   public MassEditTeamVersionItem(String name, XNavigateItem parent, String teamDefName, KeyedImage oseeImage) {
      super(parent, name, oseeImage);
      this.teamDefName = teamDefName;
      this.teamDef = null;
   }

   public MassEditTeamVersionItem(XNavigateItem parent, TeamDefinitionArtifact teamDef, KeyedImage oseeImage) {
      this("Show Team Versions", parent, teamDef, oseeImage);
   }

   public MassEditTeamVersionItem(String name, XNavigateItem parent, TeamDefinitionArtifact teamDef, KeyedImage oseeImage) {
      super(parent, name, oseeImage);
      this.teamDef = teamDef;
      this.teamDefName = null;
   }

   private TeamDefinitionArtifact getTeamDefinition() throws OseeCoreException {
      if (selectedTeamDef != null) {
         return selectedTeamDef;
      }
      if (teamDef != null) {
         return teamDef;
      }
      if (Strings.isValid(teamDefName)) {
         TeamDefinitionArtifact teamDef =
            (TeamDefinitionArtifact) AtsCacheManager.getSoleArtifactByName(AtsArtifactTypes.TeamDefinition, teamDefName);
         if (teamDef != null) {
            return teamDef;
         }
      }
      TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
      try {
         ld.setInput(TeamDefinitionManager.getTeamReleaseableDefinitions(Active.Active));
      } catch (MultipleAttributesExist ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      int result = ld.open();
      if (result == 0) {
         return (TeamDefinitionArtifact) ld.getResult()[0];
      }
      return null;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         TeamDefinitionArtifact teamDef = getTeamDefinition();
         if (teamDef == null) {
            return;
         }
         if (teamDef.getTeamDefinitionHoldingVersions() == null) {
            AWorkbench.popup("ERROR", "Team is not configured to use versions.");
            return;
         }
         MassArtifactEditor.editArtifacts(getName(), teamDef.getTeamDefinitionHoldingVersions().getVersionsArtifacts());
         selectedTeamDef = null;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * @param selectedTeamDef the selectedTeamDef to set
    */
   public void setSelectedTeamDef(TeamDefinitionArtifact selectedTeamDef) {
      this.selectedTeamDef = selectedTeamDef;
   }

}
