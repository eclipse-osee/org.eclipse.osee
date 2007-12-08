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
package org.eclipse.osee.framework.ui.skynet.branch;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class BranchSorter extends ViewerSorter {

   // Criteria that the instance uses
   @SuppressWarnings("unused")
   private int column;

   /**
    * Creates a resource sorter that will use the given sort criteria.
    * 
    * @param column the sort criterion to use: one of <code>NAME</code> or <code>TYPE</code>
    */
   public BranchSorter(int column) {
      super();
      this.column = column;
   }

   /*
    * (non-Javadoc) Method declared on ViewerSorter.
    */
   public int compare(Viewer viewer, Object o1, Object o2) {

      //      if (column == 0)
      //         return collator.compare(BranchLabelProvider.getColumnTextLabel(o1, column),
      //               BranchLabelProvider.getColumnTextLabel(o2, column));

      return 0;
   }

}