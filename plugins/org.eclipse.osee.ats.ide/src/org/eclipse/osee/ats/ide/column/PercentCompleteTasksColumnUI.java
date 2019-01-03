/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteTasksColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteTasksColumnUI instance = new PercentCompleteTasksColumnUI();

   public static PercentCompleteTasksColumnUI getInstance() {
      return instance;
   }

   private PercentCompleteTasksColumnUI() {
      super(AtsColumnId.PercentCompleteTasks.getId(), "Task Percent Complete", 40, XViewerAlign.Center, false,
         SortDataType.Percent, false,
         "Percent Complete for the tasks related to the workflow.\n\nCalculation: total percent of all tasks / number of tasks");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteTasksColumnUI copy() {
      PercentCompleteTasksColumnUI newXCol = new PercentCompleteTasksColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsObject) {
            return AtsClientService.get().getColumnService().getColumnText(AtsColumnId.PercentCompleteTasks,
               (IAtsObject) element);
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
