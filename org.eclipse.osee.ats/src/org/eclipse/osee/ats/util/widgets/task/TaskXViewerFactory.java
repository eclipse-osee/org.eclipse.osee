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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewerFactory extends WorldXViewerFactory {

   public static final List<XViewerColumn> columns =
         Arrays.asList(Title_Col, State_Col, Assignees_Col, Percent_Complete_Total_Col, Total_Hours_Spent_Col,
               Resolution_Col, Estimated_Hours_Col, Remaining_Hours_Col, Related_To_State_Col, Notes_Col, Type_Col,
               Priority_Col, Change_Type_Col, Actionable_Items_Col, User_Community_Col, ID_Col, Created_Date_Col,
               Version_Target_Col, Team_Col, Deadline_Col, Annual_Cost_Avoidance_Col, Description_Col, Legacy_PCR_Col,
               Decision_Col, Estimated_Release_Date_Col, Release_Date_Col, Work_Package_Col, Category_Col,
               Category2_Col, Category3_Col, Weekly_Benefit_Hrs_Col, Percent_Complete_State_Col,
               Percent_Complete_State_Task_Col, Percent_Complete_State_Review_Col, Hours_Spent_State_Col,
               Hours_Spent_State_Task_Col, Hours_Spent_State_Review_Col, Hours_Spent_Total_Col, Originator_Col,
               Implementor_Col, Review_Author_Col, Review_Moderator_Col, Review_Reviewer_Col, Review_Decider_Col,
               Completed_Date_Col, Cancelled_Date_Col, Man_Days_Needed_Col, Percent_Rework_Col, Branch_Status_Col,
               Number_of_Tasks_Col, Last_Modified_Col, Last_Statused_Col, Validation_Required_Col);
   public static final List<XViewerColumn> visibleColumns =
         Arrays.asList(Title_Col, State_Col, Assignees_Col, Percent_Complete_Total_Col, Total_Hours_Spent_Col,
               Resolution_Col, Estimated_Hours_Col, Remaining_Hours_Col, Related_To_State_Col, Notes_Col);

   public static Map<String, XViewerColumn> idToColumn = null;

   public TaskXViewerFactory() {
      if (idToColumn == null) {
         idToColumn = new HashMap<String, XViewerColumn>();
         for (XViewerColumn xCol : columns) {
            xCol.setShow(visibleColumns.contains(xCol));
            idToColumn.put(xCol.getId(), xCol);
         }
      }
      // TODO Old default widths for visible columns
      // List<Integer> widths = Arrays.asList(450, 60, 150, 40, 40, 100, 50, 50, 50, 80, 80);

   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new TaskXViewerSorter(xViewer);
   }

   public CustomizeData getDefaultTableCustomizeData(XViewer xViewer) {
      CustomizeData custData = new CustomizeData();
      ArrayList<XViewerColumn> cols = new ArrayList<XViewerColumn>();
      for (XViewerColumn xCol : columns) {
         xCol.setXViewer(xViewer);
         cols.add(xCol);
      }
      custData.getColumnData().setColumns(cols);
      return custData;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultXViewerColumn()
    */
   public XViewerColumn getDefaultXViewerColumn(String id) {
      return idToColumn.get(id);
   }

}
