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

package org.eclipse.osee.ats.ide.world;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerSorter extends XViewerSorter {

   protected final XViewer xViewer;

   public WorldXViewerSorter(XViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2, int sortXColIndex) {
      try {
         if (xViewer == null || !xViewer.getCustomizeMgr().isSorting()) {
            return 0;
         }
         XViewerColumn sortXCol = xViewer.getCustomizeMgr().getSortXCols().get(sortXColIndex);
         IAtsWorkItem m1 = (IAtsWorkItem) o1;
         IAtsWorkItem m2 = (IAtsWorkItem) o2;

         if (sortXCol.equals(AssigneeColumnUI.getInstance())) {
            int compareInt = getComparator().compare(
               AtsApiService.get().getColumnService().getColumnText(AtsColumnTokensDefault.AssigneeColumn, m1).replaceFirst(
                  "\\(", ""),
               AtsApiService.get().getColumnService().getColumnText(AtsColumnTokensDefault.AssigneeColumn, m2).replaceFirst(
                  "\\(", ""));
            return getCompareBasedOnDirection(sortXCol, compareInt, viewer, o1, o2, sortXColIndex);
         }
         return super.compare(viewer, o1, o2, sortXColIndex);
      } catch (Exception ex) {
         // do nothing
      }
      return 1;
   }

}
