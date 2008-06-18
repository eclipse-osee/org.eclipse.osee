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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeByVersionDialog;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactIdSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.DepricatedOperator;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class TeamVersionWorldSearchItem extends WorldSearchItem {

   private Collection<TeamDefinitionArtifact> teamDefs;
   private Set<TeamDefinitionArtifact> selectedTeamDefs;
   private boolean recurseChildren;
   private boolean showFinished;
   private boolean showAction;
   private final String[] teamDefNames;
   private VersionArtifact selectedVersion = null;

   public TeamVersionWorldSearchItem(String displayName, String[] teamDefNames, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.teamDefNames = teamDefNames;
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
   }

   public Collection<String> getProductSearchName() {
      if (teamDefNames != null)
         return Arrays.asList(teamDefNames);
      else if (teamDefs != null)
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

   public void getTeamDefs() throws OseeCoreException, SQLException {
      if (teamDefNames == null) return;
      if (teamDefs == null) {
         teamDefs = new HashSet<TeamDefinitionArtifact>();
         for (String name : teamDefNames) {
            teamDefs.add(AtsCache.getSoleArtifactByName(name, TeamDefinitionArtifact.class));
         }
      }
      if (teamDefs == null) throw new IllegalArgumentException("Can't Find ProductDefinitionArtifact for " + getName());
   }

   /**
    * @return All directly specified teamDefs plus if recurse, will get all children
    * @throws SQLException
    */
   public Set<TeamDefinitionArtifact> getSearchTeamDefs() throws OseeCoreException, SQLException {
      getTeamDefs();
      Set<TeamDefinitionArtifact> srchTeamDefs = new HashSet<TeamDefinitionArtifact>();
      for (TeamDefinitionArtifact teamDef : (teamDefs != null ? teamDefs : selectedTeamDefs))
         srchTeamDefs.add(teamDef);
      if (selectedRecurseChildren) {
         for (TeamDefinitionArtifact teamDef : (teamDefs != null ? teamDefs : selectedTeamDefs)) {
            Artifacts.getChildrenOfType(teamDef, srchTeamDefs, TeamDefinitionArtifact.class, true);
         }
      }
      return srchTeamDefs;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException, SQLException {
      getTeamDefs();

      FromArtifactsSearch versionWorkflowSearch = null;
      if (selectedVersion != null) {
         List<ISearchPrimitive> versionCriteria = new LinkedList<ISearchPrimitive>();
         versionCriteria.add(new ArtifactIdSearch(selectedVersion.getArtId(), DepricatedOperator.EQUAL));
         versionWorkflowSearch = new FromArtifactsSearch(versionCriteria, true);
      }

      Collection<TeamWorkFlowArtifact> teamWorkflows = selectedVersion.getTargetedForTeamArtifacts();

      if (!selectedShowFinished) {
         for (TeamWorkFlowArtifact team : new CopyOnWriteArrayList<TeamWorkFlowArtifact>(teamWorkflows)) {
            if (team.getSmaMgr().isCancelled() || team.getSmaMgr().isCompleted()) teamWorkflows.remove(team);
         }
      }

      for (TeamWorkFlowArtifact team : new CopyOnWriteArrayList<TeamWorkFlowArtifact>(teamWorkflows)) {
         if (!selectedTeamDefs.contains(team.getTeamDefinition())) teamWorkflows.remove(team);
      }

      if (selectedShowAction) {
         return RelationManager.getRelatedArtifacts(teamWorkflows, 1, AtsRelation.ActionToWorkflow_Action);
      }

      return Collections.castAll(teamWorkflows);
   }

   boolean selectedShowFinished = false;
   boolean selectedShowAction = false;
   boolean selectedRecurseChildren = false;

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException, SQLException {
      super.performUI(searchType);
      if (teamDefNames != null) return;
      if (teamDefs != null) return;
      if (searchType == SearchType.ReSearch && selectedTeamDefs != null) return;
      TeamDefinitionTreeByVersionDialog diag = new TeamDefinitionTreeByVersionDialog(Active.Both);
      diag.setShowFinished(showFinished);
      diag.setShowAction(showAction);
      diag.setRecurseChildren(recurseChildren);
      int result = diag.open();
      if (result == 0) {
         selectedShowFinished = diag.isShowFinished();
         selectedShowAction = diag.isShowAction();
         selectedRecurseChildren = diag.isRecurseChildren();
         selectedVersion = diag.getSelectedVersion();
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
    * @param showAction The showAction to set.
    */
   public void setShowAction(boolean showAction) {
      this.showAction = showAction;
   }

   /**
    * @param showFinished The showFinished to set.
    */
   public void setShowFinished(boolean showFinished) {
      this.showFinished = showFinished;
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
