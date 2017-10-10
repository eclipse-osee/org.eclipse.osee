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
import org.eclipse.osee.framework.core.data.IOseeBranch;

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
      if (o1 instanceof IOseeBranch && o2 instanceof IOseeBranch) {
         return getComparator().compare(((IOseeBranch) o1).getName(), ((IOseeBranch) o2).getName());
      }
      return super.compare(viewer, o1, o2);
   }

}