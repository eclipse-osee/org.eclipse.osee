/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.search.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.search.ui.IFilterListViewer;

public class FilterModelList {

   private ArrayList<FilterModel> filters = new ArrayList<>();
   private Set<IFilterListViewer> changeListeners = new HashSet<>();
   private boolean isAllSelected;

   /**
    * Constructor
    */
   public FilterModelList() {
      super();
      filters = new ArrayList<>();
      changeListeners = new HashSet<>();
   }

   /**
    * Return the collection of ItemTask
    */
   public ArrayList<FilterModel> getFilters() {
      return filters;
   }

   /**
    * Add a new task to the collection of tasks
    */
   public void addFilter(FilterModel filter, boolean top) {
      if (top) {
         filters.add(0, filter);
      } else {
         filters.add(filter);
      }
      for (IFilterListViewer flv : changeListeners) {
         flv.addFilter(filter);
      }
   }

   /**
    * @param filter -
    */
   public void removeFilter(FilterModel filter) {
      filters.remove(filter);
      for (IFilterListViewer flv : changeListeners) {
         flv.removeFilter(filter);
      }
   }

   @Override
   public String toString() {
      String str = "";
      for (int i = 0; i < filters.size(); i++) {
         String name = filters.toString();
         str += "\nTask " + name;
      }
      return str + "\n\n";
   }

   public void filterChanged(FilterModel filter) {
      for (IFilterListViewer flv : changeListeners) {
         flv.updateFilter(filter);
      }
   }

   public void removeChangeListener(IFilterListViewer viewer) {
      changeListeners.remove(viewer);
   }

   public void addChangeListener(IFilterListViewer viewer) {
      changeListeners.add(viewer);
   }

   public boolean isAllSelected() {
      return isAllSelected;
   }

   public void setAllSelected(boolean isAllSelected) {
      this.isAllSelected = isAllSelected;
   }
}
