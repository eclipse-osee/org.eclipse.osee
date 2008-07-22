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
package org.eclipse.osee.framework.ui.admin.autoRun;

import java.util.ArrayList;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class AutoRunXViewerFactory extends SkynetXViewerFactory {

   public AutoRunXViewerFactory() {
      super("osee.ats.UserRoleXViewer");
   }

   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      ArrayList<XViewerColumn> cols = new ArrayList<XViewerColumn>();
      for (AutoRunColumn atsXCol : AutoRunColumn.values()) {
         XViewerColumn newCol = atsXCol.getXViewerColumn(atsXCol);
         cols.add(newCol);
      }
      custData.getColumnData().setColumns(cols);
      return custData;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.xviewer.IXViewerFactory#getDefaultXViewerColumn()
    */
   public XViewerColumn getDefaultXViewerColumn(String id) {
      for (AutoRunColumn atsXCol : AutoRunColumn.values()) {
         if (atsXCol.getName().equals(id)) {
            return atsXCol.getXViewerColumn(atsXCol);
         }
      }
      return null;
   }

}
