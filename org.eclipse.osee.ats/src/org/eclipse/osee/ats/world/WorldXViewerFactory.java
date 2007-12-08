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
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerFactory extends SkynetXViewerFactory {

   private XViewer xViewer;

   /**
    * 
    */
   public WorldXViewerFactory() {
   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      this.xViewer = xViewer;
      return new WorldXViewerSorter(xViewer);
   }

   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      int x = 0;
      ArrayList<XViewerColumn> cols = new ArrayList<XViewerColumn>();
      for (AtsXColumn atsXCol : AtsXColumn.values()) {
         XViewerColumn newCol = atsXCol.getXViewerColumn(atsXCol);
         newCol.setOrderNum(x++);
         newCol.setTreeViewer(xViewer);
         cols.add(newCol);
      }
      custData.getColumnData().setColumns(cols);
      return custData;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultXViewerColumn()
    */
   public XViewerColumn getDefaultXViewerColumn(String name) {
      for (AtsXColumn atsXCol : AtsXColumn.values()) {
         if (atsXCol.getName().equals(name)) {
            return atsXCol.getXViewerColumn(atsXCol);
         }
      }
      return null;
   }

}
