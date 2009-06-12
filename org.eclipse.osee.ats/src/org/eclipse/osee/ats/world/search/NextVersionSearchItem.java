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

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class NextVersionSearchItem extends WorldUISearchItem {

   private final TeamDefinitionArtifact teamDefHoldingVersions;
   private TeamDefinitionArtifact selectedTeamDef;
   private VersionArtifact selectedVersionArt;

   /**
    * @param name
    * @throws OseeArgumentException
    */
   public NextVersionSearchItem(TeamDefinitionArtifact teamDefHoldingVersions, LoadView loadView) {
      this(null, teamDefHoldingVersions, loadView);
   }

   public NextVersionSearchItem(String name, TeamDefinitionArtifact teamDefHoldingVersions, LoadView loadView) {
      super(name != null ? name : "Workflows Targeted-For Next Version", loadView, FrameworkImage.VERSION);
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   public NextVersionSearchItem(NextVersionSearchItem nextVersionSearchItem) {
      super(nextVersionSearchItem, FrameworkImage.VERSION);
      this.teamDefHoldingVersions = nextVersionSearchItem.teamDefHoldingVersions;
      this.selectedTeamDef = nextVersionSearchItem.selectedTeamDef;
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      String name = super.getName();
      TeamDefinitionArtifact teamDef = getTeamDefinition(searchType);
      try {
         if (teamDef != null) {
            name += (teamDef != null ? " - " + teamDef.getDescriptiveName() : "");
            selectedVersionArt = teamDef.getNextReleaseVersion();
            name += (selectedVersionArt != null ? " - " + selectedVersionArt.getDescriptiveName() : "");
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
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
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#performSearch()
    */
   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      if (isCancelled()) return EMPTY_SET;
      if (getTeamDefinition(searchType).getNextReleaseVersion() == null) {
         AWorkbench.popup("ERROR", "No version marked as Next Release for \"" + getTeamDefinition(searchType) + "\"");
         return EMPTY_SET;
      }
      List<Artifact> arts =
            getTeamDefinition(searchType).getNextReleaseVersion().getRelatedArtifacts(
                  AtsRelation.TeamWorkflowTargetedForVersion_Workflow);
      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
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
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      cancelled = true;
   }

   /**
    * @param selectedTeamDef the selectedTeamDef to set
    */
   public void setSelectedTeamDef(TeamDefinitionArtifact selectedTeamDef) {
      this.selectedTeamDef = selectedTeamDef;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new NextVersionSearchItem(this);
   }

   /**
    * @return the selectedVersionArt
    */
   public VersionArtifact getSelectedVersionArt() {
      return selectedVersionArt;
   }

}
