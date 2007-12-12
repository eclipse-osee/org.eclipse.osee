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
package org.eclipse.osee.ats.util.widgets.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.world.AtsXColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewerFactory extends SkynetXViewerFactory {

   private XViewer xViewer;

   public TaskXViewerFactory() {
   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      this.xViewer = xViewer;
      return new TaskXViewerSorter(xViewer);
   }

   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      // Title, State, POC, Percent_Complete, Hours_Spent, Resolution, Est_Hours, Remain_Hours
      List<AtsXColumn> taskColumnOrder =
            Arrays.asList(new AtsXColumn[] {AtsXColumn.Title_Col, AtsXColumn.State_Col, AtsXColumn.Assignees_Col,
                  AtsXColumn.Total_Percent_Complete_Col, AtsXColumn.Total_Hours_Spent_Col, AtsXColumn.Resolution_Col,
                  AtsXColumn.Estimated_Hours_Col, AtsXColumn.Remaining_Hours_Col, AtsXColumn.Related_To_State_Col,
                  AtsXColumn.Notes_Col});
      List<Integer> widths = Arrays.asList(new Integer[] {450, 60, 150, 40, 40, 100, 50, 50, 50, 80, 80});

      int x = 0;
      ArrayList<XViewerColumn> cols = new ArrayList<XViewerColumn>();

      // Set visible and first columns
      ArrayList<AtsXColumn> handled = new ArrayList<AtsXColumn>();
      for (AtsXColumn atsXCol : taskColumnOrder) {
         XViewerColumn newCol = atsXCol.getXViewerColumn(atsXCol);
         newCol.setWidth(widths.get(x));
         newCol.setOrderNum(x++);
         newCol.setTreeViewer(xViewer);
         newCol.setShow(true);
         cols.add(newCol);
         handled.add(atsXCol);
      }

      // Reset the remainder of the columns to the order and non-visible
      for (AtsXColumn atsXCol : AtsXColumn.values()) {
         if (!handled.contains(atsXCol)) {
            XViewerColumn newCol = atsXCol.getXViewerColumn(atsXCol);
            newCol.setOrderNum(x++);
            newCol.setTreeViewer(xViewer);
            newCol.setShow(false);
            cols.add(newCol);
            handled.add(atsXCol);
         }
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
