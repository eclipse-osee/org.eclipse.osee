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
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.widgets.dialog.ActionActionableItemListDialog;
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

/**
 * @author Donald G. Dunne
 */
public class ActionableItemWorldSearchItem extends WorldSearchItem {

   private Set<ActionableItemArtifact> actionItems;
   private Set<ActionableItemArtifact> selectedActionItems;
   private boolean recurseChildren;
   private boolean showFinished;
   private boolean showAction;
   private final String[] actionItemNames;

   public ActionableItemWorldSearchItem(String displayName, String[] actionItemNames, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.actionItemNames = actionItemNames;
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
   }

   public ActionableItemWorldSearchItem(String displayName, Set<ActionableItemArtifact> actionItems, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.actionItemNames = null;
      this.actionItems = actionItems;
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
   }

   public Collection<String> getProductSearchName() {
      if (actionItemNames != null)
         return Arrays.asList(actionItemNames);
      else if (actionItems != null)
         return Artifacts.artNames(actionItems);
      else if (selectedActionItems != null) return Artifacts.artNames(selectedActionItems);
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

   public void getActionableItems() throws SQLException, IllegalArgumentException {
      if (actionItemNames == null) return;
      if (actionItems == null) {
         actionItems = new HashSet<ActionableItemArtifact>();
         for (String name : actionItemNames) {
            ArtifactTypeNameSearch srch =
                  new ArtifactTypeNameSearch(ActionableItemArtifact.ARTIFACT_NAME, name,
                        BranchPersistenceManager.getInstance().getAtsBranch());
            actionItems.add(srch.getSingletonArtifactOrException(ActionableItemArtifact.class));
         }
      }
      if (actionItems == null) throw new IllegalArgumentException(
            "Can't Find ProductDefinitionArtifact for " + getName());
   }

   /**
    * @return All directly specified teamDefs plus if recurse, will get all children
    * @throws SQLException
    */
   private Set<ActionableItemArtifact> getSearchActionableItems() throws SQLException {
      getActionableItems();
      Set<ActionableItemArtifact> srchTeamDefs = new HashSet<ActionableItemArtifact>();
      for (ActionableItemArtifact actionableItem : (actionItems != null ? actionItems : selectedActionItems))
         srchTeamDefs.add(actionableItem);
      if (recurseChildren) {
         for (ActionableItemArtifact actionableItem : (actionItems != null ? actionItems : selectedActionItems)) {
            Artifacts.getChildrenOfType(actionableItem, srchTeamDefs, ActionableItemArtifact.class, true);
         }
      }
      return srchTeamDefs;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {
      getActionableItems();

      // Find all Team Workflows with one of the given AI's
      List<ISearchPrimitive> teamWorkflowToAICriteria = new LinkedList<ISearchPrimitive>();
      for (ActionableItemArtifact ai : getSearchActionableItems())
         teamWorkflowToAICriteria.add(new AttributeValueSearch(
               ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName(), ai.getGuid(), Operator.EQUAL));
      FromArtifactsSearch teamWorkflowToAISearch = new FromArtifactsSearch(teamWorkflowToAICriteria, false);

      // Find all Team Workflows artifact types
      List<ISearchPrimitive> teamWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (String teamArtName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         teamWorkflowCriteria.add(new ArtifactTypeSearch(teamArtName, Operator.EQUAL));
      FromArtifactsSearch teamWorkflowSearch = new FromArtifactsSearch(teamWorkflowCriteria, false);

      // Find all
      List<ISearchPrimitive> allProductCriteria = new LinkedList<ISearchPrimitive>();
      allProductCriteria.add(teamWorkflowToAISearch);
      allProductCriteria.add(teamWorkflowSearch);
      if (!showFinished) {
         allProductCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Cancelled.name() + ";;;", Operator.NOT_EQUAL));
         allProductCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Completed.name() + ";;;", Operator.NOT_EQUAL));
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
      if (actionItemNames != null) return;
      if (actionItems != null) return;
      if (searchType == SearchType.ReSearch && selectedActionItems != null) return;
      ActionActionableItemListDialog diag = new ActionActionableItemListDialog(Active.Both);
      diag.setShowFinished(showFinished);
      diag.setShowAction(showAction);
      diag.setRecurseChildren(recurseChildren);
      int result = diag.open();
      if (result == 0) {
         showFinished = diag.isShowFinished();
         showAction = diag.isShowAction();
         recurseChildren = diag.isRecurseChildren();
         if (selectedActionItems == null)
            selectedActionItems = new HashSet<ActionableItemArtifact>();
         else
            selectedActionItems.clear();
         for (Object obj : diag.getResult())
            selectedActionItems.add((ActionableItemArtifact) obj);
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
