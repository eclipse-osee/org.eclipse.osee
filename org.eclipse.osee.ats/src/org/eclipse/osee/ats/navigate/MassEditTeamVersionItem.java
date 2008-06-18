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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class MassEditTeamVersionItem extends XNavigateItemAction {

   private final String teamDefName;
   private final TeamDefinitionArtifact teamDef;
   private TeamDefinitionArtifact selectedTeamDef;

   public MassEditTeamVersionItem(XNavigateItem parent, String teamDefName) {
      this("Show Team Versions", parent, teamDefName);
   }

   public MassEditTeamVersionItem(String name, XNavigateItem parent, String teamDefName) {
      super(parent, name);
      this.teamDefName = teamDefName;
      this.teamDef = null;
   }

   public MassEditTeamVersionItem(XNavigateItem parent, TeamDefinitionArtifact teamDef) {
      this("Show Team Versions", parent, teamDef);
   }

   /**
    * @param name
    * @param parent
    * @param teamDef Team Definition Artifact that is related to versions or null for popup selection
    */
   public MassEditTeamVersionItem(String name, XNavigateItem parent, TeamDefinitionArtifact teamDef) {
      super(parent, name);
      this.teamDef = teamDef;
      this.teamDefName = null;
   }

   private TeamDefinitionArtifact getTeamDefinition() throws OseeCoreException, SQLException {
      if (selectedTeamDef != null) return selectedTeamDef;
      if (teamDef != null) return teamDef;
      if (teamDefName != null && !teamDefName.equals("")) {
         try {
            TeamDefinitionArtifact teamDef = AtsCache.getSoleArtifactByName(teamDefName, TeamDefinitionArtifact.class);
            if (teamDef != null) return teamDef;
         } catch (ArtifactDoesNotExist ex) {
            // do nothing, going to get team below
         }
      }
      TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
      try {
         ld.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Active));
      } catch (MultipleAttributesExist ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      int result = ld.open();
      if (result == 0) {
         return (TeamDefinitionArtifact) ld.getResult()[0];
      }
      return null;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws SQLException {
      try {
         TeamDefinitionArtifact teamDef = getTeamDefinition();
         if (teamDef == null) return;
         if (teamDef.getTeamDefinitionHoldingVersions() == null) {
            AWorkbench.popup("ERROR", "Team is not configured to use versions.");
            return;
         }
         MassArtifactEditor.editArtifacts(getName(), teamDef.getTeamDefinitionHoldingVersions().getVersionsArtifacts());
         selectedTeamDef = null;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /**
    * @param selectedTeamDef the selectedTeamDef to set
    */
   public void setSelectedTeamDef(TeamDefinitionArtifact selectedTeamDef) {
      this.selectedTeamDef = selectedTeamDef;
   }

}
