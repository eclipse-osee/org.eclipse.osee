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

package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeLabelProvider.ConflictState;

/**
 * @author Theron Virgin
 */
public class MergeXViewerSorter extends XViewerSorter {

   protected final XViewer xViewer;
   protected final XMergeLabelProvider labelProvider;

   public MergeXViewerSorter(XViewer xViewer, XMergeLabelProvider labelProvider) {
      super(xViewer);
      this.xViewer = xViewer;
      this.labelProvider = labelProvider;
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2, int sortXColIndex) {
      try {
         if (xViewer == null || !xViewer.getCustomizeMgr().isSorting()) {
            return 0;
         }
         XViewerColumn sortXCol = xViewer.getCustomizeMgr().getSortXCols().get(sortXColIndex);
         String value1 = labelProvider.getColumnText(o1, sortXCol, sortXColIndex);
         String value2 = labelProvider.getColumnText(o2, sortXCol, sortXColIndex);

         if (o1 instanceof Conflict && o2 instanceof Conflict) {
            if (sortXCol.equals(MergeXViewerFactory.Conflict_Resolved)) {
               int compareInt =
                  Integer.valueOf(ConflictState.getValue(value1)).compareTo(Integer.valueOf(ConflictState.getValue(value2)));
               return getCompareBasedOnDirection(sortXCol, compareInt, viewer, o1, o2, sortXColIndex);
            }
         }

         return super.compare(viewer, o1, o2, sortXColIndex);
      } catch (Exception ex) {
         // do nothing
      }
      return 1;
   }
}
