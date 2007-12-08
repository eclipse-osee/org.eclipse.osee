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
package org.eclipse.osee.framework.ui.admin.dbtabletab;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Sorter for the WorldTableViewer that displays items of type <code>ExampleTask</code>. The sorter supports three
 * sort criteria:
 * <p>
 * <code>DESCRIPTION</code>: Task description (String)
 * </p>
 * <p>
 * <code>OWNER</code>: Task Owner (String)
 * </p>
 * <p>
 * <code>PERCENT_COMPLETE</code>: Task percent completed (int).
 * </p>
 * 
 * @author Donald G. Dunne
 */
public class DbTableSorter extends ViewerSorter {

   // Criteria that the instance uses
   private int criteria;

   /**
    * Creates a resource sorter that will use the given sort criteria.
    * 
    * @param criteria the sort criterion to use: one of <code>NAME</code> or <code>TYPE</code>
    */
   public DbTableSorter(int criteria) {
      super();
      this.criteria = criteria;
   }

   /*
    * (non-Javadoc) Method declared on ViewerSorter.
    */
   public int compare(Viewer viewer, Object o1, Object o2) {

      Object obj1 = ((DbModel) o1).getColumn(criteria);
      Object obj2 = ((DbModel) o2).getColumn(criteria);
      if (obj1 instanceof Long) {
         return compareStrings(((Long) obj1).toString(), ((Long) obj2).toString());
      } else if (obj1 instanceof String) {
         return compareStrings((String) obj1, (String) obj2);
      }

      return 0;
   }

   @SuppressWarnings("unchecked")
   protected int compareStrings(String str1, String str2) {
      if (str1 == null) str1 = "";
      if (str2 == null) str2 = "";
      return getComparator().compare(str1, str2);
   }

   /**
    * Returns the sort criteria of this this sorter.
    * 
    * @return the sort criterion
    */
   public int getCriteria() {
      return criteria;
   }
}