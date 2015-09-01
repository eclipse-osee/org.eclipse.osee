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
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.util.widgets.dialog.ActionActionableItemListDialog;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemWorldSearchItem extends WorldUISearchItem {

   private final Collection<IAtsActionableItem> actionItems;
   private Set<IAtsActionableItem> selectedActionItems;
   private boolean recurseChildren;
   private boolean selectedRecurseChildren; // Used to not corrupt original values
   private boolean showFinished;
   private boolean selectedShowFinished; // Used to not corrupt original values
   private boolean showAction;
   private boolean selectedShowAction; // Used to not corrupt original values

   public ActionableItemWorldSearchItem(String displayName, Collection<IAtsActionableItem> actionItems, boolean showFinished, boolean recurseChildren, boolean showAction) {
      super(displayName, AtsImage.ACTIONABLE_ITEM);
      this.actionItems = actionItems;
      this.showFinished = showFinished;
      this.recurseChildren = recurseChildren;
      this.showAction = showAction;
   }

   public ActionableItemWorldSearchItem(ActionableItemWorldSearchItem item) {
      super(item, AtsImage.ACTIONABLE_ITEM);
      this.actionItems = item.actionItems;
      this.showFinished = item.showFinished;
      this.recurseChildren = item.recurseChildren;
      this.showAction = item.showAction;
   }

   public Collection<String> getProductSearchName() {
      if (actionItems != null) {
         return ActionableItems.getNames(actionItems);
      } else if (selectedActionItems != null) {
         return ActionableItems.getNames(selectedActionItems);
      }
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return Strings.truncate(String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName()),
         WorldEditor.TITLE_MAX_LENGTH, true);
   }

   /**
    * @return All directly specified teamDefs plus if recurse, will get all children
    */
   private Set<IAtsActionableItem> getSearchActionableItems() throws OseeCoreException {
      Set<IAtsActionableItem> srchTeamDefs = new HashSet<IAtsActionableItem>();
      for (IAtsActionableItem actionableItem : actionItems != null ? actionItems : selectedActionItems) {
         srchTeamDefs.add(actionableItem);
      }
      if (selectedRecurseChildren) {
         for (IAtsActionableItem actionableItem : actionItems != null ? actionItems : selectedActionItems) {
            ActionableItems.getChildrenOfType(actionableItem, srchTeamDefs, IAtsActionableItem.class, true);
         }
      }
      return srchTeamDefs;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      Set<IAtsActionableItem> items = getSearchActionableItems();
      List<String> actionItemGuids = new ArrayList<String>(items.size());
      for (IAtsActionableItem ai : items) {
         actionItemGuids.add(AtsUtilCore.getGuid(ai));
      }
      List<ArtifactSearchCriteria> criteria = new ArrayList<ArtifactSearchCriteria>();

      criteria.add(new AttributeCriteria(AtsAttributeTypes.ActionableItem, actionItemGuids));
      // exclude completed or canceled
      if (!selectedShowFinished) {
         TeamWorldSearchItem.addIncludeCompletedCancelledCriteria(criteria, false, false);
      }
      Collection<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromCriteria(AtsUtilCore.getAtsBranch(), 1000, criteria);
      // show as actions
      if (selectedShowAction) {
         Set<Artifact> arts = new HashSet<Artifact>();
         for (Artifact art : artifacts) {
            if (art.isOfType(AtsArtifactTypes.Action)) {
               arts.add(art);
            } else if (art instanceof AbstractWorkflowArtifact) {
               Artifact parentAction = ((AbstractWorkflowArtifact) art).getParentActionArtifact();
               if (parentAction != null) {
                  arts.add(parentAction);
               }
            }
         }
         return arts;
      } else {
         return artifacts;
      }
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
      super.performUI(searchType);
      if (actionItems != null) {
         return;
      }
      if (searchType == SearchType.ReSearch && selectedActionItems != null) {
         return;
      }
      ActionActionableItemListDialog diag = new ActionActionableItemListDialog(Active.Both);
      diag.setShowFinished(showFinished);
      diag.setRecurseChildren(recurseChildren);
      diag.setShowAction(showAction);
      int result = diag.open();
      if (result == 0) {
         selectedShowFinished = diag.isShowFinished();
         selectedRecurseChildren = diag.isRecurseChildren();
         selectedShowAction = diag.isShowAction();
         if (selectedActionItems == null) {
            selectedActionItems = new HashSet<IAtsActionableItem>();
         } else {
            selectedActionItems.clear();
         }
         for (Object obj : diag.getResult()) {
            selectedActionItems.add((IAtsActionableItem) obj);
         }
         return;
      }
      cancelled = true;
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

   public void setShowAction(boolean showAction) {
      this.showAction = showAction;
   }

   /**
    * @param selectedActionItems the selectedActionItems to set
    */
   public void setSelectedActionItems(Set<IAtsActionableItem> selectedActionItems) {
      this.selectedActionItems = selectedActionItems;
   }

   @Override
   public WorldUISearchItem copy() {
      return new ActionableItemWorldSearchItem(this);
   }

}
