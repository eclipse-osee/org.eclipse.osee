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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.artifact.VersionArtifact;
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
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class UnReleasedTeamWorldSearchItem extends WorldSearchItem {

   private Set<TeamDefinitionArtifact> teamDefs;
   private Set<TeamDefinitionArtifact> selectedTeamDefs;
   private boolean recurseChildren;
   private boolean showFinished;
   private boolean showAction;
   private final String[] teamDefNames;

   public UnReleasedTeamWorldSearchItem(String displayName, String[] teamDefNames, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.teamDefNames = teamDefNames;
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
   }

   public UnReleasedTeamWorldSearchItem(String displayName, Set<TeamDefinitionArtifact> teamDefs, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.teamDefNames = null;
      this.teamDefs = teamDefs;
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
   }

   public UnReleasedTeamWorldSearchItem(String displayName, TeamDefinitionArtifact teamDef, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.teamDefNames = null;
      this.teamDefs = new HashSet<TeamDefinitionArtifact>();
      this.teamDefs.add(teamDef);
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
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

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

      List<ISearchPrimitive> allTeamCriteria = new LinkedList<ISearchPrimitive>();
      allTeamCriteria.add(teamDefWorkflowSearch);
      allTeamCriteria.add(teamWorkflowSearch);
      if (!showFinished) {
         allTeamCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Cancelled.name() + ";;;", Operator.NOT_EQUAL));
         allTeamCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Completed.name() + ";;;", Operator.NOT_EQUAL));
      }
      FromArtifactsSearch allTeamWorkflows = new FromArtifactsSearch(allTeamCriteria, true);

      // Get un-targeted workflows
      List<ISearchPrimitive> untargetedTeamsCriteria = new LinkedList<ISearchPrimitive>();
      untargetedTeamsCriteria.add(teamWorkflowSearch);
      untargetedTeamsCriteria.add(new NotSearch(new InRelationSearch(allTeamWorkflows,
            RelationSide.TeamWorkflowTargetedForVersion_Workflow)));
      FromArtifactsSearch untargetedTeamsCriteriaArts = new FromArtifactsSearch(untargetedTeamsCriteria, true);

      // Get un-released version artifacts
      List<ISearchPrimitive> unReleasedVersionCriteria = new LinkedList<ISearchPrimitive>();
      unReleasedVersionCriteria.add(new ArtifactTypeSearch(VersionArtifact.ARTIFACT_NAME, Operator.EQUAL));
      unReleasedVersionCriteria.add(new AttributeValueSearch(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), "no",
            Operator.EQUAL));
      FromArtifactsSearch unReleasedVersionCriteriaArts = new FromArtifactsSearch(unReleasedVersionCriteria, true);

      List<ISearchPrimitive> unReleasedTeamsCriteria = new LinkedList<ISearchPrimitive>();
      unReleasedTeamsCriteria.add(new InRelationSearch(unReleasedVersionCriteriaArts,
            RelationSide.TeamWorkflowTargetedForVersion_Workflow));
      unReleasedTeamsCriteria.add(allTeamWorkflows);
      FromArtifactsSearch unReleasedTeamsCriteriaArts = new FromArtifactsSearch(unReleasedTeamsCriteria, true);

      // Get all un-released and un-targeted workflows
      List<ISearchPrimitive> unReleasedAndUntargetedTeamsCriteria = new LinkedList<ISearchPrimitive>();
      unReleasedAndUntargetedTeamsCriteria.add(untargetedTeamsCriteriaArts);
      unReleasedAndUntargetedTeamsCriteria.add(unReleasedTeamsCriteriaArts);
      FromArtifactsSearch unReleasedAndUntargetedTeamsCriteriaArts =
            new FromArtifactsSearch(unReleasedAndUntargetedTeamsCriteria, false);

      if (!showAction) {
         if (isCancelled()) return EMPTY_SET;
         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(unReleasedAndUntargetedTeamsCriteria, false,
                     BranchPersistenceManager.getInstance().getAtsBranch());
         if (isCancelled()) return EMPTY_SET;
         return arts;
      }

      List<ISearchPrimitive> actionCriteria = new LinkedList<ISearchPrimitive>();
      actionCriteria.add(new InRelationSearch(unReleasedAndUntargetedTeamsCriteriaArts,
            RelationSide.ActionToWorkflow_Action));

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
      try {
         diag.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Both));
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      diag.setShowFinished(showFinished);
      diag.setShowFinished(true);
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
