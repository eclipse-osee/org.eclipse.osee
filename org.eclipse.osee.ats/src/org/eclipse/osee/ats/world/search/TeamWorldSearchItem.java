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
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

/**
 * @author Donald G. Dunne
 */
public class TeamWorldSearchItem extends WorldSearchItem {

   private Collection<TeamDefinitionArtifact> teamDefs;
   private Set<TeamDefinitionArtifact> selectedTeamDefs;
   private boolean recurseChildren;
   private boolean showFinished;
   private boolean showAction;
   private final String[] teamDefNames;
   private final ChangeType changeType;

   public TeamWorldSearchItem(String displayName, String[] teamDefNames, boolean showFinished, boolean showAction, boolean recurseChildren, ChangeType changeType) {
      super(displayName);
      this.teamDefNames = teamDefNames;
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
      this(displayName, Arrays.asList(new TeamDefinitionArtifact[] {teamDef}), showFinished, showAction,
            recurseChildren);
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

   /**
    * Loads all team definitions if specified by name versus by team definition class
    * 
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public void getTeamDefs() throws SQLException, IllegalArgumentException {
      if (teamDefNames == null) return;
      if (teamDefs == null) {
         teamDefs = new HashSet<TeamDefinitionArtifact>();
         for (String name : teamDefNames) {
            ArtifactTypeNameSearch srch =
                  new ArtifactTypeNameSearch(TeamDefinitionArtifact.ARTIFACT_NAME, name,
                        BranchPersistenceManager.getInstance().getAtsBranch());
            teamDefs.add(srch.getSingletonArtifactOrException(TeamDefinitionArtifact.class));
         }
      }
      if (teamDefs == null) throw new IllegalArgumentException("Can't Find ProductDefinitionArtifact for " + getName());
   }

   /**
    * @return All directly specified teamDefs plus if recurse, will get all children
    * @throws SQLException
    */
   public Set<TeamDefinitionArtifact> getSearchTeamDefs() throws SQLException {
      getTeamDefs();
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
      getTeamDefs();
      List<ISearchPrimitive> teamDefWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (TeamDefinitionArtifact tda : getSearchTeamDefs())
         teamDefWorkflowCriteria.add(new AttributeValueSearch(
               ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(), tda.getGuid(), Operator.EQUAL));
      FromArtifactsSearch teamDefWorkflowSearch = new FromArtifactsSearch(teamDefWorkflowCriteria, false);

      // Find all Team Workflows artifact types
      List<ISearchPrimitive> teamWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (String teamArtName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         teamWorkflowCriteria.add(new ArtifactTypeSearch(teamArtName, Operator.EQUAL));
      FromArtifactsSearch teamWorkflowSearch = new FromArtifactsSearch(teamWorkflowCriteria, false);

      List<ISearchPrimitive> allProductCriteria = new LinkedList<ISearchPrimitive>();
      allProductCriteria.add(teamDefWorkflowSearch);
      allProductCriteria.add(teamWorkflowSearch);
      if (!showFinished) {
         allProductCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Cancelled.name() + ";;;", Operator.NOT_EQUAL));
         allProductCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Completed.name() + ";;;", Operator.NOT_EQUAL));
      }
      if (changeType != null) {
         allProductCriteria.add(new AttributeValueSearch(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(),
               changeType.name(), Operator.EQUAL));
      }
      FromArtifactsSearch allTeamWorkflows = new FromArtifactsSearch(allProductCriteria, true);

      if (!showAction) {
         if (isCancelled()) return EMPTY_SET;
         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(allProductCriteria, true,
                     BranchPersistenceManager.getInstance().getAtsBranch());
         if (isCancelled()) return EMPTY_SET;
         return arts;
      }

      List<ISearchPrimitive> actionCriteria = new LinkedList<ISearchPrimitive>();
      actionCriteria.add(new InRelationSearch(allTeamWorkflows, RelationSide.ActionToWorkflow_Action));

      if (isCancelled()) return EMPTY_SET;
      Collection<Artifact> arts =
            ArtifactPersistenceManager.getInstance().getArtifacts(actionCriteria, true,
                  BranchPersistenceManager.getInstance().getAtsBranch());

      if (isCancelled()) return EMPTY_SET;
      return arts;

   }

   @Override
   public void performUI(SearchType searchType) {
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
         showFinished = diag.isShowFinished();
         showAction = diag.isShowAction();
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

}
