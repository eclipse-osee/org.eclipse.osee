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

package org.eclipse.osee.framework.ui.skynet.branch;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * Default sorter for branch. Sorts on descriptive name
 */
@SuppressWarnings("deprecation")
public class BranchNameSorter extends ViewerSorter {

   /**
    * Default sorter for artifacts. Sorts on name
    */
   public BranchNameSorter() {
      super();
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      if (o1 instanceof BranchToken && o2 instanceof BranchToken) {
         return getComparator().compare(((BranchToken) o1).getName(), ((BranchToken) o2).getName());
      }
      return super.compare(viewer, o1, o2);
   }

}