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
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

/**
 * @author Donald G. Dunne
 */
public class TeamWorldSearchItem extends WorldSearchItem {

   private Collection<TeamDefinitionArtifact> teamDefs;
   private Set<TeamDefinitionArtifact> selectedTeamDefs = new HashSet<TeamDefinitionArtifact>();
   private boolean selectedRecurseChildren; // Used to not corrupt original values
   private boolean recurseChildren;
   private boolean selectedShowFinished; // Used to not corrupt original values
   private boolean showFinished;
   private boolean selectedShowAction; // Used to not corrupt original values
   private boolean showAction;
   private final Collection<String> teamDefNames;
   private final ChangeType changeType;

   public TeamWorldSearchItem(String displayName, String[] teamDefNames, boolean showFinished, boolean showAction, boolean recurseChildren, ChangeType changeType) {
      super(displayName);
      if (teamDefNames != null) {
         this.teamDefNames = Arrays.asList(teamDefNames);
      } else {
         this.teamDefNames = null;
      }
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
      this.changeType = changeType;
   }

   public TeamWorldSearchItem(String displayName, Collection<TeamDefinitionArtifact> teamDefs, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.recurseChildren = recurseChildren;
      this.teamDefNames = null;
      this.teamDefs = teamDefs;
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.changeType = null;
   }

   public TeamWorldSearchItem(String displayName, TeamDefinitionArtifact teamDef, boolean showFinished, boolean showAction, boolean recurseChildren) {
      this(displayName, Arrays.asList(teamDef), showFinished, showAction, recurseChildren);
   }

   public Collection<String> getProductSearchName() {
      if (teamDefNames != null)
         return teamDefNames;
      else if (teamDefs != null)
         return Artifacts.artNames(teamDefs);
      else if (selectedTeamDefs != null) return Artifacts.artNames(selectedTeamDefs);
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

   /**
    * Loads all team definitions if specified by name versus by team definition class
    * 
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public void getTeamDefs() throws OseeCoreException, SQLException {
      if (teamDefNames != null && teamDefs == null) {
         teamDefs = new HashSet<TeamDefinitionArtifact>();
         for (String teamDefName : teamDefNames) {
            TeamDefinitionArtifact aia = AtsCache.getSoleArtifactByName(teamDefName, TeamDefinitionArtifact.class);
            if (aia != null) {
               teamDefs.add(aia);
            }
         }
      }
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
      Set<TeamDefinitionArtifact> items = getSearchTeamDefs();
      List<String> teamDefinitionGuids = new ArrayList<String>(items.size());
      for (TeamDefinitionArtifact art : items) {
         teamDefinitionGuids.add(art.getGuid());
      }
      List<AbstractArtifactSearchCriteria> criteria = new ArrayList<AbstractArtifactSearchCriteria>();
      criteria.add(new AttributeCriteria(ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(),
            teamDefinitionGuids));

      if (!selectedShowFinished) {
         List<String> cancelOrComplete = new ArrayList<String>(2);
         cancelOrComplete.add(DefaultTeamState.Cancelled.name() + ";;;");
         cancelOrComplete.add(DefaultTeamState.Completed.name() + ";;;");
         criteria.add(new AttributeCriteria(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(), cancelOrComplete,
               Operator.NOT_EQUAL));
      }
      if (changeType != null) {
         criteria.add(new AttributeCriteria(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), changeType.name()));
      }

      List<Artifact> artifacts =
            ArtifactQuery.getArtifactsFromCriteria(BranchPersistenceManager.getAtsBranch(), 1000, criteria);

      if (selectedShowAction) {
         return RelationManager.getRelatedArtifacts(artifacts, 1, AtsRelation.ActionToWorkflow_Action);
      } else {
         return artifacts;
      }
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException, SQLException {
      super.performUI(searchType);
      if (teamDefNames != null) return;
      if (teamDefs != null) return;
      if (searchType == SearchType.ReSearch && selectedTeamDefs != null) return;
      TeamDefinitionTreeDialog diag = new TeamDefinitionTreeDialog(Active.Both);
      diag.setShowFinished(showFinished);
      diag.setShowAction(showAction);
      diag.setRecurseChildren(recurseChildren);
      int result = diag.open();
      if (result == 0) {
         selectedShowFinished = diag.isShowFinished();
         selectedShowAction = diag.isShowAction();
         selectedRecurseChildren = diag.isRecurseChildren();
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
    * @param selectedTeamDefs the selectedTeamDefs to set
    */
   public void setSelectedTeamDefs(Set<TeamDefinitionArtifact> selectedTeamDefs) {
      this.selectedTeamDefs = selectedTeamDefs;
   }

}
