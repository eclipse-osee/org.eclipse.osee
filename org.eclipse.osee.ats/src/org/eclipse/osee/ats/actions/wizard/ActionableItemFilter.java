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

import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ActionableItemFilter extends ViewerFilter {
   private String contains = null;
   private final CheckboxTreeViewer treeViewer;

   public ActionableItemFilter(CheckboxTreeViewer treeViewer) {
      this.treeViewer = treeViewer;
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (!isFiltering()) return true;
      return checkItemAndChildren((ActionableItemArtifact) element);
   }

   private boolean checkItemAndChildren(ActionableItemArtifact item) {
      try {
         boolean show = item.getDescriptiveName().toLowerCase().contains(contains.toLowerCase());
         if (show) return true;
         for (Artifact child : item.getChildren()) {
            show = checkItemAndChildren((ActionableItemArtifact) child);
            if (show) return true;
         }
      } catch (SQLException ex) {
         AtsPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return false;
   }

   /**
    * @param contains The contains to set.
    */
   public void setContains(String contains) {
      this.contains = contains;
      if (contains.equals(""))
         treeViewer.collapseAll();
      else
         treeViewer.expandAll();
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
