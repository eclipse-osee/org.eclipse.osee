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

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.column.AssigneeColumn;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class AtsIdColumnUI extends XViewerValueColumn {

   public static AtsIdColumnUI instance = new AtsIdColumnUI();

   public static AtsIdColumnUI getInstance() {
      return instance;
   }

   public AtsIdColumnUI() {
      this(false);
   }

   public AtsIdColumnUI(boolean show) {
      super(AtsColumnId.AtsId.name(), "ATS ID", 75, SWT.LEFT, show, SortDataType.String, false, "ATS ID");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AtsIdColumnUI copy() {
      AtsIdColumnUI newXCol = new AtsIdColumnUI(isShow());
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsObject) {
            return AssigneeColumn.instance.getAssigneeStr((IAtsObject) element);
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
