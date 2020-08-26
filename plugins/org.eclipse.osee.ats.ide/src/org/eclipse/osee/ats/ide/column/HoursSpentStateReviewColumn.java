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
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class HoursSpentStateReviewColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static HoursSpentStateReviewColumn instance = new HoursSpentStateReviewColumn();

   public static HoursSpentStateReviewColumn getInstance() {
      return instance;
   }

   private HoursSpentStateReviewColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".stateReviewHoursSpent", "State Review Hours Spent", 40,
         XViewerAlign.Center, false, SortDataType.Float, false,
         "Hours spent in performing the changes for the reveiws related to the current state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HoursSpentStateReviewColumn copy() {
      HoursSpentStateReviewColumn newXCol = new HoursSpentStateReviewColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsWorkItem) {
            return AtsUtil.doubleToI18nString(
               HoursSpentUtil.getHoursSpentStateReview((IAtsWorkItem) element, AtsApiService.get()));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
