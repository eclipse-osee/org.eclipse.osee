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
package org.eclipse.osee.ats.actions.wizard;

import java.util.Locale;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemFilter extends ViewerFilter {
   private String contains = null;
   private final FilteredCheckboxTree treeViewer;

   public ActionableItemFilter(FilteredCheckboxTree treeViewer) {
      super();
      this.treeViewer = treeViewer;
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (!isFiltering()) {
         return true;
      }
      return checkItemAndChildren((IAtsActionableItem) element);
   }

   private boolean checkItemAndChildren(IAtsActionableItem item) {
      boolean show = item.getName().toLowerCase().contains(contains.toLowerCase(Locale.US));
      if (show) {
         return true;
      }
      for (IAtsActionableItem child : item.getChildrenActionableItems()) {
         show = checkItemAndChildren(child);
         if (show) {
            return true;
         }
      }
      return false;
   }

   /**
    * @param contains The contains to set.
    */
   public void setContains(String contains) {
      this.contains = contains;
      if (contains.isEmpty()) {
         treeViewer.getViewer().collapseAll();
      } else {
         treeViewer.getViewer().expandAll();
      }
   }

   /**
    * @return Returns the contains.
    */
   public String getContains() {
      return contains;
   }

   public boolean isFiltering() {
      return contains != null && contains.length() > 0;
   }
}
