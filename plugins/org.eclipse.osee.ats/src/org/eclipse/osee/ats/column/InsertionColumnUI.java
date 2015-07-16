/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.column.InsertionColumn;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class InsertionColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static InsertionColumnUI instance = new InsertionColumnUI();

   public static InsertionColumnUI getInstance() {
      return instance;
   }

   private InsertionColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".insertion", "Insertion", 80, SWT.LEFT, false, SortDataType.String,
         false, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public InsertionColumnUI copy() {
      InsertionColumnUI newXCol = new InsertionColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      if (element instanceof IAtsObject) {
         result = InsertionColumn.getInsertionStr((IAtsObject) element, AtsClientService.get().getServices());
      }
      return result;
   }
}
