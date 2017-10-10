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
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Robert A. Fisher
 */
@SuppressWarnings("deprecation")
public class ColumnSorter extends ViewerSorter {
   private final ITableLabelProvider labelProvider;
   private int columnToSort;
   private boolean ascending;

   public ColumnSorter(ITableLabelProvider labelProvider) {
      this(labelProvider, 0);
   }

   public ColumnSorter(ITableLabelProvider labelProvider, int columnToSort) {
      this.labelProvider = labelProvider;
      this.columnToSort = columnToSort;
      this.ascending = true;
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      int value;

      String str1 = labelProvider.getColumnText(o1, columnToSort);
      String str2 = labelProvider.getColumnText(o2, columnToSort);

      // Try to do numeric sorting
      Integer val1 = null;
      Integer val2 = null;
      try {
         val1 = getIntValue(str1);
      } catch (NumberFormatException ex) {
         // don't really care
      }
      try {
         val2 = getIntValue(str2);
      } catch (NumberFormatException ex) {
         // don't really care
      }

      if (val1 != null && val2 != null) {
         value = val1 - val2;
      } else if (val1 == null ^ val2 == null) {
         // Text goes after numbers
         return val1 == null ? 1 : -1;
      } else {
         value = getComparator().compare(str1, str2);
      }
      return value * (ascending ? 1 : -1);
   }

   /**
    * Try and retrieve a number from a string. If a "..." is in the string, then only the portion of the string prior to
    * the "..." will be used.
    */
   private int getIntValue(String string) throws NumberFormatException {
      int elipseIndex;

      elipseIndex = string.indexOf("...");
      if (elipseIndex != -1) {
         return Integer.parseInt(string.substring(0, elipseIndex));
      } else {
         return Integer.parseInt(string);
      }
   }

   /**
    * @param columnToSort The columnToSort to set.
    */
   public void setColumnToSort(int columnToSort) {
      if (this.columnToSort == columnToSort) {
         ascending = !ascending;
      } else {
         this.columnToSort = columnToSort;
         ascending = true;
      }
   }

   /**
    * @return Returns the ascending.
    */
   public boolean isAscending() {
      return ascending;
   }

   /**
    * @param ascending The ascending to set.
    */
   public void setAscending(boolean ascending) {
      this.ascending = ascending;
   }
}
