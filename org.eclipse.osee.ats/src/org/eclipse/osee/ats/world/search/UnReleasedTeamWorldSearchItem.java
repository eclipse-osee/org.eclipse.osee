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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
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
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class UnReleasedTeamWorldSearchItem extends WorldSearchItem {

   private Set<TeamDefinitionArtifact> teamDefs;
   private Set<TeamDefinitionArtifact> selectedTeamDefs;
   private boolean recurseChildren;
   private boolean selectedRecurseChildren;
   private boolean showFinished;
   private boolean selectedShowFinished;
   private boolean showAction;
   private boolean selectedShowAction;
   private final String[] teamDefNames;

   public UnReleasedTeamWorldSearchItem(String displayName, String[] teamDefNames, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.teamDefNames = teamDefNames;
      this.showFinished = showFinished;
      this.selectedShowFinished = showFinished;
      this.showAction = showAction;
      this.selectedShowAction = showAction;
      this.recurseChildren = recurseChildren;
      this.selectedRecurseChildren = recurseChildren;
   }

   public UnReleasedTeamWorldSearchItem(String displayName, TeamDefinitionArtifact teamDef, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.teamDefNames = null;
      this.teamDefs = new HashSet<TeamDefinitionArtifact>();
      this.teamDefs.add(teamDef);
      this.showFinished = showFinished;
      this.selectedShowFinished = showFinished;
      this.showAction = showAction;
      this.selectedShowAction = showAction;
      this.recurseChildren = recurseChildren;
      this.selectedRecurseChildren = recurseChildren;
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

      List<Artifact> artifacts =
            ArtifactQuery.getArtifactsFromCriteria(BranchPersistenceManager.getAtsBranch(), 1000, criteria);

      List<Artifact> unReleasedArtifacts = new ArrayList<Artifact>();
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact team = (TeamWorkFlowArtifact) artifact;
            if (team.getTargetedForVersion() == null || !team.getTargetedForVersion().isReleased()) {
               unReleasedArtifacts.add(artifact);
            }
         }
      }

      if (selectedShowAction) {
         return RelationManager.getRelatedArtifacts(unReleasedArtifacts, 1, AtsRelation.ActionToWorkflow_Action);
      } else {
         return unReleasedArtifacts;
      }

   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException, SQLException {
      super.performUI(searchType);
      if (teamDefNames != null) return;
      if (teamDefs != null) return;
      if (searchType == SearchType.ReSearch && selectedTeamDefs != null) return;
      TeamDefinitionTreeDialog diag = new TeamDefinitionTreeDialog(Active.Both);
      try {
         diag.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Both));
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      diag.setShowFinished(showFinished);
      diag.setRecurseChildren(recurseChildren);
      diag.setShowAction(showAction);
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
}
