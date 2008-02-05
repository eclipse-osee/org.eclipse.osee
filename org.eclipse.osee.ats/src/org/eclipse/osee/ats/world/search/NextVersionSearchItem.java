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

package org.eclipse.osee.ats.world.search;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class NextVersionSearchItem extends WorldSearchItem {

   private final TeamDefinitionArtifact teamDefHoldingVersions;
   private TeamDefinitionArtifact selectedTeamDef;

   /**
    * @param name
    */
   public NextVersionSearchItem(TeamDefinitionArtifact teamDefHoldingVersions) {
      this(null, teamDefHoldingVersions);
   }

   public NextVersionSearchItem(String name, TeamDefinitionArtifact teamDefHoldingVersions) {
      super(name != null ? name : "Workflows Targeted-For Next Version");
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      String name = super.getName();
      TeamDefinitionArtifact teamDef = getTeamDefinition(searchType);
      try {
         if (teamDef != null) {
            name += (teamDef != null ? " - " + teamDef.getDescriptiveName() : "");
            VersionArtifact verArt = teamDef.getNextReleaseVersion();
            name += (verArt != null ? " - " + verArt.getDescriptiveName() : "");
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return "Exception Occurred - See Log - " + ex.getLocalizedMessage();
      }
      return name;
   }

   private TeamDefinitionArtifact getTeamDefinition(SearchType searchType) {
      if (teamDefHoldingVersions != null) return teamDefHoldingVersions;
      return selectedTeamDef;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.search.WorldSearchItem#performSearch()
    */
   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {
      if (isCancelled()) return EMPTY_SET;
      if (getTeamDefinition(searchType).getNextReleaseVersion() == null) {
         AWorkbench.popup("ERROR", "No version marked as Next Release for \"" + getTeamDefinition(searchType) + "\"");
         return EMPTY_SET;
      }
      Set<Artifact> arts =
            getTeamDefinition(searchType).getNextReleaseVersion().getArtifacts(
                  RelationSide.TeamWorkflowTargetedForVersion_Workflow);
      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      if (teamDefHoldingVersions != null) return;
      if (searchType == SearchType.ReSearch && selectedTeamDef != null) return;
      try {
         TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
         ld.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Both));
         int result = ld.open();
         if (result == 0) {
            selectedTeamDef = (TeamDefinitionArtifact) ld.getResult()[0];
            return;
         } else
            cancelled = true;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      cancelled = true;
   }

}
