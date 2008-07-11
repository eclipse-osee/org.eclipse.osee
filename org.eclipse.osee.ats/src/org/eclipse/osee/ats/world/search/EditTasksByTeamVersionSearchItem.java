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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeByVersionDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class EditTasksByTeamVersionSearchItem extends WorldSearchItem {

   private Collection<TeamDefinitionArtifact> teamDefs;
   private Set<TeamDefinitionArtifact> selectedTeamDefs;
   private boolean recurseChildren;
   private VersionArtifact selectedVersion = null;

   public EditTasksByTeamVersionSearchItem(Collection<TeamDefinitionArtifact> teamDefs, boolean recurseChildren) {
      super("Edit Tasks by Team Version", LoadView.TaskEditor);
      this.teamDefs = teamDefs;
      this.recurseChildren = recurseChildren;
   }

   public Collection<String> getProductSearchName() {
      if (teamDefs != null)
         return Artifacts.artNames(teamDefs);
      else if (selectedTeamDefs != null) return Artifacts.artNames(selectedTeamDefs);
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      if (selectedVersion != null)
         return String.format("%s - %s - %s", super.getSelectedName(searchType), getProductSearchName(),
               selectedVersion.getDescriptiveName());
      else
         return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

   /**
    * @return All directly specified teamDefs plus if recurse, will get all children
    * @throws SQLException
    */
   public Set<TeamDefinitionArtifact> getSearchTeamDefs() throws SQLException {
      Set<TeamDefinitionArtifact> srchTeamDefs = new HashSet<TeamDefinitionArtifact>();
      for (TeamDefinitionArtifact teamDef : (teamDefs != null ? teamDefs : selectedTeamDefs))
         srchTeamDefs.add(teamDef);
      if (recurseChildren) {
         for (TeamDefinitionArtifact teamDef : (teamDefs != null ? teamDefs : selectedTeamDefs)) {
            Artifacts.getChildrenOfType(teamDef, srchTeamDefs, TeamDefinitionArtifact.class, true);
         }
      }
      return srchTeamDefs;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException, SQLException {
	   Set<TeamDefinitionArtifact> teamDefs = getSearchTeamDefs();
	   List<Artifact> validWorkflows = new ArrayList<Artifact>();
	   List<Artifact> workflows = selectedVersion.getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Workflow);
	   for(Artifact workflow:workflows){
		   if(teamDefs.contains(((TeamWorkFlowArtifact)workflow).getTeamDefinition())){
			   validWorkflows.add(workflow);
		   }
	   }
	   Set<Artifact> tasks = RelationManager.getRelatedArtifacts(validWorkflows, 1, AtsRelation.SmaToTask_Task);
	   return tasks;
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException, SQLException {
      super.performUI(searchType);
      if (teamDefs != null) return;
      if (searchType == SearchType.ReSearch && selectedTeamDefs != null) return;
      TeamDefinitionTreeByVersionDialog diag = new TeamDefinitionTreeByVersionDialog(Active.Both);
      // set to null will not show the showAction or showFinished checkbox
      diag.setShowAction(null);
      diag.setShowFinished(null);
      diag.setRecurseChildren(recurseChildren);
      int result = diag.open();
      if (result == 0) {
         selectedVersion = diag.getSelectedVersion();
         recurseChildren = diag.isRecurseChildren();
         if (selectedTeamDefs == null)
            selectedTeamDefs = new HashSet<TeamDefinitionArtifact>();
         else
            selectedTeamDefs.clear();
         for (Object obj : diag.getResult())
            selectedTeamDefs.add((TeamDefinitionArtifact) obj);
         return;
      }
      cancelled = true;
   }

   /**
    * @return the recurseChildren
    */
   public boolean isRecurseChildren() {
      return recurseChildren;
   }

   /**
    * @param recurseChildren the recurseChildren to set
    */
   public void setRecurseChildren(boolean recurseChildren) {
      this.recurseChildren = recurseChildren;
   }

   /**
    * @param selectedTeamDefs the selectedTeamDefs to set
    */
   public void setSelectedTeamDefs(Set<TeamDefinitionArtifact> selectedTeamDefs) {
      this.selectedTeamDefs = selectedTeamDefs;
   }

   /**
    * @param selectedVersion the selectedVersion to set
    */
   public void setSelectedVersion(VersionArtifact selectedVersion) {
      this.selectedVersion = selectedVersion;
   }

}
