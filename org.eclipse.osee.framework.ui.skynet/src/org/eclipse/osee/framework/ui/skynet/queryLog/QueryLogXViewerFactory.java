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
package org.eclipse.osee.framework.ui.skynet.queryLog;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class QueryLogXViewerFactory extends SkynetXViewerFactory {

   public QueryLogXViewerFactory() {
   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      List<XViewerColumn> defaultColumns = new ArrayList<XViewerColumn>();
      defaultColumns.add(new XViewerColumn(QueryLogView.ITEM, 400, 400, SWT.LEFT, true, SortDataType.String, 0));
      defaultColumns.add(new XViewerColumn(QueryLogView.TIME, 100, 100, SWT.CENTER, true, SortDataType.String, 1));
      defaultColumns.add(new XViewerColumn(QueryLogView.DURATION, 100, 100, SWT.CENTER, true, SortDataType.Float, 2));
      custData.getColumnData().setColumns(defaultColumns);
      return custData;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultXViewerColumn()
    */
   public XViewerColumn getDefaultXViewerColumn(String name) {
      for (XViewerColumn xCol : getDefaultTableCustomizeData().getColumnData().getColumns()) {
         if (xCol.getSystemName().equals(name)) {
            return xCol;
         }
      }
      return null;
   }

}
