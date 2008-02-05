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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeByVersionDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactIdSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;

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
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {

      List<ISearchPrimitive> teamDefWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (TeamDefinitionArtifact tda : getSearchTeamDefs())
         teamDefWorkflowCriteria.add(new AttributeValueSearch(
               ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(), tda.getGuid(), Operator.EQUAL));
      FromArtifactsSearch teamDefWorkflowSearch = new FromArtifactsSearch(teamDefWorkflowCriteria, false);

      FromArtifactsSearch versionWorkflowSearch = null;
      if (selectedVersion != null) {
         List<ISearchPrimitive> versionCriteria = new LinkedList<ISearchPrimitive>();
         versionCriteria.add(new ArtifactIdSearch(selectedVersion.getArtId(), Operator.EQUAL));
         versionWorkflowSearch = new FromArtifactsSearch(versionCriteria, true);
      }

      // Find all Team Workflows artifact types
      List<ISearchPrimitive> teamWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (String teamArtName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         teamWorkflowCriteria.add(new ArtifactTypeSearch(teamArtName, Operator.EQUAL));
      FromArtifactsSearch teamWorkflowSearch = new FromArtifactsSearch(teamWorkflowCriteria, false);

      List<ISearchPrimitive> allProductCriteria = new LinkedList<ISearchPrimitive>();
      allProductCriteria.add(teamDefWorkflowSearch);
      allProductCriteria.add(teamWorkflowSearch);
      if (selectedVersion != null) allProductCriteria.add(new InRelationSearch(versionWorkflowSearch,
            RelationSide.TeamWorkflowTargetedForVersion_Workflow));
      FromArtifactsSearch allTeamWorkflows = new FromArtifactsSearch(allProductCriteria, true);

      List<ISearchPrimitive> taskCriteria = new LinkedList<ISearchPrimitive>();
      taskCriteria.add(new InRelationSearch(allTeamWorkflows, RelationSide.SmaToTask_Task));

      if (isCancelled()) return EMPTY_SET;
      Collection<Artifact> arts =
            ArtifactPersistenceManager.getInstance().getArtifacts(taskCriteria, true,
                  BranchPersistenceManager.getInstance().getAtsBranch());

      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

   @Override
   public void performUI(SearchType searchType) {
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

}
