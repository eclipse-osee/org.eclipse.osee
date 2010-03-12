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
package org.eclipse.osee.framework.ui.skynet.search.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.search.ui.IFilterListViewer;

public class FilterModelList {

   private ArrayList<FilterModel> filters = new ArrayList<FilterModel>();
   private Set<IFilterListViewer> changeListeners = new HashSet<IFilterListViewer>();
   private boolean isAllSelected;

   /**
    * Constructor
    */
   public FilterModelList() {
      super();
      filters = new ArrayList<FilterModel>();
      changeListeners = new HashSet<IFilterListViewer>();
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
      if (top)
         filters.add(0, filter);
      else
         filters.add(filter);
      for (IFilterListViewer flv : changeListeners)
         flv.addFilter(filter);
   }

   /**
    * @param filter -
    */
   public void removeFilter(FilterModel filter) {
      filters.remove(filter);
      for (IFilterListViewer flv : changeListeners)
         flv.removeFilter(filter);
   }

   public String toString() {
      String str = "";
      for (int i = 0; i < filters.size(); i++) {
         String name = filters.toString();
         str += "\nTask " + name;
      }
      return str + "\n\n";
   }

   /**
    * @param filter
    */
   public void filterChanged(FilterModel filter) {
      for (IFilterListViewer flv : changeListeners)
         flv.updateFilter(filter);
   }

   /**
    * @param viewer
    */
   public void removeChangeListener(IFilterListViewer viewer) {
      changeListeners.remove(viewer);
   }

   /**
    * @param viewer
    */
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
