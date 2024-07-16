/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class HoursSpentTotalColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static HoursSpentTotalColumn instance = new HoursSpentTotalColumn();

   public static HoursSpentTotalColumn getInstance() {
      return instance;
   }

   private HoursSpentTotalColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".totalHoursSpent", "Total Hours Spent", 40, XViewerAlign.Center,
         false, SortDataType.Float, false, "Hours spent for all work related to all states.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HoursSpentTotalColumn copy() {
      HoursSpentTotalColumn newXCol = new HoursSpentTotalColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsWorkItem) {
            return AtsUtil.doubleToI18nString(
               AtsApiService.get().getWorkItemMetricsService().getHoursSpentTotal((IAtsWorkItem) element));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
