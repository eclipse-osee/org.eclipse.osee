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

package org.eclipse.osee.ats.ide.util.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsValColumn;
import org.eclipse.osee.ats.ide.column.AtsColumnIdUi;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class XViewerAtsColumnIdColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   private final AtsValColumn column;

   public XViewerAtsColumnIdColumn(AtsValColumn column) {
      super(column.getId(), column.getName(), column.getWidth(), AtsColumnIdUi.getXViewerAlign(column.getAlign()),
         column.isVisible(), SortDataType.valueOf(column.getSortDataType()), column.isColumnMultiEdit(),
         column.getDescription());
      this.column = column;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsColumnIdColumn copy() {
      XViewerAtsColumnIdColumn newXCol = new XViewerAtsColumnIdColumn(column);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      String result = "";
      try {
         if (element instanceof IAtsObject) {
            result =
               AtsApiService.get().getColumnService().getColumnText(column.getColumnId(), (IAtsObject) element);
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return result;
   }

}
