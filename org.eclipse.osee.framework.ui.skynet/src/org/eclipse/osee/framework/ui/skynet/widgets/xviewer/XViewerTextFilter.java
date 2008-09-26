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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TreeItem;

public class XViewerTextFilter extends ViewerFilter {

   private final XViewer xViewer;
   private final Set<Object> matchedObjects = new HashSet<Object>();
   private Pattern pattern;
   private Matcher matcher;

   public XViewerTextFilter(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   public void setFilterText(String text) {
      pattern = Pattern.compile("\\Q" + text + "\\E", Pattern.CASE_INSENSITIVE);
      matchedObjects.clear();
      for (TreeItem item : xViewer.getTree().getItems()) {
         findRecursively(item);
      }
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      return matchedObjects.contains(element);
   }

   /**
    * Find if item or item's children match
    * 
    * @param item
    * @return true if match
    */
   public boolean findRecursively(TreeItem item) {
      /* determine if found and return true so parent knows to add themselves to matched items if found */
      boolean found = false;
      /* check this item */
      for (int i = 0; i < xViewer.getTree().getColumnCount(); i++) {
         String contents = item.getText(i);
         matcher = pattern.matcher(contents);
         if (matcher.find()) {
            found = true;
            break;
         }
      }

      /* check child items */
      if (item.getExpanded()) {
         for (TreeItem child : item.getItems()) {
            if (findRecursively(child)) {
               found = true;
            }
         }
      }
      /* Add this item if any children matched */
      if (found) {
         matchedObjects.add(item.getData());
      }
      return found;
   }
}
