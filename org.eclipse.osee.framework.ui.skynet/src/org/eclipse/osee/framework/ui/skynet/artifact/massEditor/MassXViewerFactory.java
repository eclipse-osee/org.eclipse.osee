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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.ArrayList;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class MassXViewerFactory extends SkynetXViewerFactory {

   private XViewer xViewer;
   private CustomizeData custData;

   /**
    * 
    */
   public MassXViewerFactory() {
   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      this.xViewer = xViewer;
      return new XViewerSorter(xViewer);
   }

   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      int x = 0;
      ArrayList<XViewerColumn> cols = new ArrayList<XViewerColumn>();

      XViewerColumn newCol = new XViewerColumn(xViewer, "Name", 150, 150, SWT.CENTER);
      newCol.setOrderNum(x++);
      newCol.setTreeViewer(xViewer);
      cols.add(newCol);

      custData.getColumnData().setColumns(cols);
      return custData;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultXViewerColumn()
    */
   public XViewerColumn getDefaultXViewerColumn(String name) {
      if (custData != null) for (XViewerColumn xCol : custData.getColumnData().getColumns())
         if (xCol.getDisplayName().equals(name)) return xCol;
      return new XViewerColumn(xViewer, "Name", 50, 50, SWT.CENTER);
   }

   /**
    * @param custData the custData to set
    */
   public void setDefaultCustData(CustomizeData custData) {
      this.custData = custData;
   }

}
