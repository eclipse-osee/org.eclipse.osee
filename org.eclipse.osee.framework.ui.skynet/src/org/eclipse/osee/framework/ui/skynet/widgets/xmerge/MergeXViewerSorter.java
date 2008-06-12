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
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeLabelProvider.ConflictState;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;

/**
 * @author Theron Virgin
 */
public class MergeXViewerSorter extends XViewerSorter {

   protected final XViewer xViewer;

   /**
    * @param xViewer
    */
   public MergeXViewerSorter(XViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter#compare(org.eclipse.jface.viewers.Viewer,
    *      java.lang.Object, java.lang.Object, int)
    */
   @SuppressWarnings("unchecked")
   @Override
   public int compare(Viewer viewer, Object o1, Object o2, int sortXColIndex) {
      try {
         if (xViewer == null || !xViewer.getCustomize().getCurrentCustData().getSortingData().isSorting()) return 0;
         XViewerColumn sortXCol =
               xViewer.getCustomize().getCurrentCustData().getSortingData().getSortXCols().get(sortXColIndex);
         MergeColumn aCol = MergeColumn.getXColumn(sortXCol);

         if (aCol == MergeColumn.Conflict_Resolved) {
            return ConflictState.valueOf(o1.toString()).compareTo(ConflictState.valueOf(o2.toString()));
         }

         return super.compare(viewer, o1, o2, sortXColIndex);
      } catch (Exception ex) {
         // do nothing
      }
      return 1;
   }

}
