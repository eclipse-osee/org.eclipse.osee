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
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class AtsIdColumn extends XViewerValueColumn {

   public static AtsIdColumn instance = new AtsIdColumn();

   public static AtsIdColumn getInstance() {
      return instance;
   }

   public AtsIdColumn() {
      this(false);
   }

   public AtsIdColumn(boolean show) {
      super("ats.id", "ATS ID", 75, SWT.LEFT, show, SortDataType.String, false, "ATS ID");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AtsIdColumn copy() {
      AtsIdColumn newXCol = new AtsIdColumn(isShow());
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      try {
         if (element instanceof IAtsWorkItem) {
            result = ((IAtsWorkItem) element).getAtsId();
         } else if (element instanceof IAtsAction) {
            result = ((IAtsAction) element).getAtsId();
         } else if (element instanceof IAtsObject) {
            result = ((IAtsObject) element).getGuid();
         }
      } catch (Exception ex) {
         result = XViewerCells.getCellExceptionString(ex);
      }
      return result;
   }

}
