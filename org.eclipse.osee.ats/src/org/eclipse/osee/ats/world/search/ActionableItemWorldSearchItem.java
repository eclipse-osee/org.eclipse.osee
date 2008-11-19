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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.ActionActionableItemListDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemWorldSearchItem extends WorldUISearchItem {

   private Collection<ActionableItemArtifact> actionItems;
   private Set<ActionableItemArtifact> selectedActionItems;
   private boolean recurseChildren;
   private boolean selectedRecurseChildren; // Used to not corrupt original values
   private boolean showFinished;
   private boolean selectedShowFinished; // Used to not corrupt original values
   private boolean showAction;
   private boolean selectedShowAction; // Used to not corrupt original values
   private final Collection<String> actionItemNames;

   public ActionableItemWorldSearchItem(Collection<String> actionItemNames, String displayName, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.actionItemNames = actionItemNames;
      this.showFinished = showFinished;
      this.selectedShowFinished = showFinished; // Set as default in case UI is not used
      this.showAction = showAction;
      this.selectedShowAction = showAction; // Set as default in case UI is not used
      this.recurseChildren = recurseChildren;
      this.selectedRecurseChildren = recurseChildren; // Set as default in case UI is not used
   }

   public ActionableItemWorldSearchItem(String displayName, Collection<ActionableItemArtifact> actionItems, boolean showFinished, boolean showAction, boolean recurseChildren) {
      super(displayName);
      this.actionItemNames = null;
      this.actionItems = actionItems;
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
   }

   public ActionableItemWorldSearchItem(ActionableItemWorldSearchItem item) {
      super(item);
      this.actionItemNames = item.actionItemNames;
      this.actionItems = item.actionItems;
      this.showFinished = item.showFinished;
      this.showAction = item.showAction;
      this.recurseChildren = item.recurseChildren;
   }

   public Collection<String> getProductSearchName() {
      if (actionItemNames != null)
         return actionItemNames;
      else if (actionItems != null)
         return Artifacts.artNames(actionItems);
      else if (selectedActionItems != null) return Artifacts.artNames(selectedActionItems);
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

   public void getActionableItems() throws OseeCoreException {
      if (actionItemNames != null && actionItems == null) {
         for (String actionItemName : actionItemNames) {
            ActionableItemArtifact aia = AtsCache.getSoleArtifactByName(actionItemName, ActionableItemArtifact.class);
            if (aia != null) {
               actionItems.add(aia);
            }
         }
      }
   }

   /**
    * @return All directly specified teamDefs plus if recurse, will get all children
    */
   private Set<ActionableItemArtifact> getSearchActionableItems() throws OseeCoreException {
      getActionableItems();
      Set<ActionableItemArtifact> srchTeamDefs = new HashSet<ActionableItemArtifact>();
      for (ActionableItemArtifact actionableItem : (actionItems != null ? actionItems : selectedActionItems))
         srchTeamDefs.add(actionableItem);
      if (selectedRecurseChildren) {
         for (ActionableItemArtifact actionableItem : (actionItems != null ? actionItems : selectedActionItems)) {
            Artifacts.getChildrenOfType(actionableItem, srchTeamDefs, ActionableItemArtifact.class, true);
         }
      }
      return srchTeamDefs;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      Set<ActionableItemArtifact> items = getSearchActionableItems();
      List<String> actionItemGuids = new ArrayList<String>(items.size());
      for (ActionableItemArtifact ai : items) {
         actionItemGuids.add(ai.getGuid());
      }
      List<AbstractArtifactSearchCriteria> criteria = new ArrayList<AbstractArtifactSearchCriteria>();

      criteria.add(new AttributeCriteria(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName(), actionItemGuids));

      if (!selectedShowFinished) {
         List<String> cancelOrComplete = new ArrayList<String>(2);
         cancelOrComplete.add(DefaultTeamState.Cancelled.name() + ";;;");
         cancelOrComplete.add(DefaultTeamState.Completed.name() + ";;;");
         criteria.add(new AttributeCriteria(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(), cancelOrComplete,
               Operator.NOT_EQUAL));
      }

      List<Artifact> artifacts = ArtifactQuery.getArtifactsFromCriteria(AtsPlugin.getAtsBranch(), 1000, criteria);

      if (selectedShowAction) {
         return RelationManager.getRelatedArtifacts(artifacts, 1, AtsRelation.ActionToWorkflow_Action);
      } else {
         return artifacts;
      }
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
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
         selectedShowFinished = diag.isShowFinished();
         selectedShowAction = diag.isShowAction();
         selectedRecurseChildren = diag.isRecurseChildren();
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

   /**
    * @param selectedActionItems the selectedActionItems to set
    */
   public void setSelectedActionItems(Set<ActionableItemArtifact> selectedActionItems) {
      this.selectedActionItems = selectedActionItems;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new ActionableItemWorldSearchItem(this);
   }

}
