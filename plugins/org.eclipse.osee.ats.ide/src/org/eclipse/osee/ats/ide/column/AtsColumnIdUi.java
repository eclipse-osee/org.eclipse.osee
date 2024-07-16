/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.Collection;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsValColumn;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * XViewerAtsColumn for columns that provide their text through AtsColumnService and are not strictly attribute based.
 *
 * @author Donald G. Dunne
 */
public class AtsColumnIdUi extends XViewerAtsColumn implements IAtsXViewerPreComputedColumn {

   private final AtsValColumn columnIdColumn;
   private final AtsApi atsApi;

   public AtsColumnIdUi(AtsValColumn columnIdColumn, AtsApi atsApi) {
      super(columnIdColumn.getId(), columnIdColumn.getName(), columnIdColumn.getWidth(),
         getXViewerAlign(columnIdColumn.getAlign()), columnIdColumn.isVisible(),
         SortDataType.valueOf(columnIdColumn.getSortDataType()), columnIdColumn.isColumnMultiEdit(),
         columnIdColumn.getDescription());
      this.columnIdColumn = columnIdColumn;
      this.atsApi = atsApi;
      setInheritParent(columnIdColumn.isInheritParent());
      setActionRollup(columnIdColumn.isActionRollup());
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AtsColumnIdUi copy() {
      AtsColumnIdUi newXCol = new AtsColumnIdUi(columnIdColumn, atsApi);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object element : objects) {
         String value = "";
         try {
            if (element instanceof IAtsObject) {
               value = atsApi.getColumnService().getColumnText(columnIdColumn.getColumnId(), (IAtsObject) element);
            }
         } catch (Exception ex) {
            value = LogUtil.getCellExceptionString(ex);
         }
         preComputedValueMap.put(getKey(element), value);
      }
   }

   public static XViewerAlign getXViewerAlign(ColumnAlign align) {
      if (align == ColumnAlign.Center) {
         return XViewerAlign.Center;
      }
      if (align == ColumnAlign.Right) {
         return XViewerAlign.Right;
      }
      return XViewerAlign.Left;
   }

}
