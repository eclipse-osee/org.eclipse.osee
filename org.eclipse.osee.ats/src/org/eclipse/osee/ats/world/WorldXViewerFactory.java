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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeColumn;
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
public class WorldXViewerFactory extends SkynetXViewerFactory {

   private static String COLUMN_NAMESPACE = "ats.column.";
   public static final XViewerColumn Type_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".type", "Type", 80, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn State_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".state", "State", 70, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Priority_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".priority", "Priority", 20, SWT.CENTER, true, SortDataType.String, false);
   public static final XViewerColumn Change_Type_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Change Type", 22, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Assignees_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Assignees", 100, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Title_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Title", 200, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Actionable_Items_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Actionable Items", 80, SWT.LEFT, true, SortDataType.String,
               false, "Actionable Items that are impacted by this change.");
   public static final XViewerColumn User_Community_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "User Community", 60, SWT.LEFT, true, SortDataType.String, false,
               "Program, Project or Group that caused the creation of this Action.");
   public static final XViewerColumn ID_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "ID", 40, SWT.LEFT, true, SortDataType.String, false,
               "Human Readable ID");
   public static final XViewerColumn Created_Date_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Created Date", 80, SWT.LEFT, true, SortDataType.Date, false);
   public static final XViewerColumn Version_Target_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Version Target", 40, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Team_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Team", 50, SWT.LEFT, true, SortDataType.String, false,
               "Team that has been assigned to work this Action.");
   public static final XViewerColumn Notes_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Notes", 80, SWT.LEFT, true, SortDataType.String, true);
   public static final XViewerColumn Deadline_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Deadline", 80, SWT.LEFT, true, SortDataType.Date, true,
               "Date the changes need to be completed by.");

   // Aren't shown by default
   public static final XViewerColumn Annual_Cost_Avoidance_Col =
         new XViewerColumn(
               COLUMN_NAMESPACE + ".run",
               "Annual Cost Avoidance",
               50,
               SWT.LEFT,
               false,
               SortDataType.Float,
               false,
               "Hours that would be saved for the first year if this change were completed.\n\n" + "(Weekly Benefit Hours * 52 weeks) - Remaining Hours\n\n" + "If number is high, benefit is great given hours remaining.");
   public static final XViewerColumn Description_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Description", 150, SWT.LEFT, false, SortDataType.String, true);
   public static XViewerAtsAttributeColumn Legacy_PCR_Col =
         new XViewerAtsAttributeColumn(COLUMN_NAMESPACE + ".run", ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE, 40, 40,
               SWT.LEFT, false, SortDataType.String);
   public static final XViewerColumn Decision_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Decision", 150, SWT.LEFT, false, SortDataType.String, false);
   public static final XViewerColumn Resolution_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Resolution", 150, SWT.LEFT, false, SortDataType.String, false);
   public static XViewerAtsAttributeColumn Estimated_Release_Date_Col =
         new XViewerAtsAttributeColumn(null, COLUMN_NAMESPACE + ".run", ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE,
               80, 80, SWT.LEFT, false, SortDataType.Date, false,
               "Date the changes will be made available to the users.");
   public static final XViewerColumn Release_Date_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", ATSAttributes.RELEASE_DATE_ATTRIBUTE.getDisplayName(), 80,
               SWT.LEFT, false, SortDataType.Date, false, "Date the changes were made available to the users.");
   public static final XViewerColumn Work_Package_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Work Package", 80, SWT.LEFT, false, SortDataType.String, true);
   public static final XViewerColumn Category_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Category", 80, SWT.LEFT, false, SortDataType.String, true,
               "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final XViewerColumn Category2_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Category2", 80, SWT.LEFT, false, SortDataType.String, true,
               "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final XViewerColumn Category3_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Category3", 80, SWT.LEFT, false, SortDataType.String, true,
               "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static XViewerAtsAttributeColumn Related_To_State_Col =
         new XViewerAtsAttributeColumn(null, COLUMN_NAMESPACE + ".run", ATSAttributes.RELATED_TO_STATE_ATTRIBUTE, 80,
               80, SWT.LEFT, false, SortDataType.String, true,
               "State of the parent State Machine that this object is related to.");
   public static final XViewerColumn Estimated_Hours_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Estimated Hours", 40, SWT.CENTER, false, SortDataType.Float,
               true, "Hours estimated to implement the changes associated with this Action.");
   public static final XViewerColumn Weekly_Benefit_Hrs_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Weekly Benefit Hrs", 40, SWT.CENTER, false, SortDataType.Float,
               false, "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final XViewerColumn Remaining_Hours_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Remaining Hours", 40, SWT.CENTER, false, SortDataType.Float,
               false,
               "Hours that remain to complete the changes.\n\nEstimated Hours - (Estimated Hours * Percent Complete).");

   public static final XViewerColumn Percent_Complete_State_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "State Percent Complete", 40, SWT.CENTER, false,
               SortDataType.Percent, false,
               "Percent Complete for the changes to the current state.\n\nAmount entered from user.");
   public static final XViewerColumn Percent_Complete_State_Task_Col =
         new XViewerColumn(
               COLUMN_NAMESPACE + ".run",
               "State Task Percent Complete",
               40,
               SWT.CENTER,
               false,
               SortDataType.Percent,
               false,
               "Percent Complete for the tasks related to the current state.\n\nCalculation: total percent of all tasks related to state / number of tasks related to state");
   public static final XViewerColumn Percent_Complete_State_Review_Col =
         new XViewerColumn(
               COLUMN_NAMESPACE + ".run",
               "State Review Percent Complete",
               40,
               SWT.CENTER,
               false,
               SortDataType.Percent,
               false,
               "Percent Complete for the reviews related to the current state.\n\nCalculation: total percent of all reviews related to state / number of reviews related to state");
   public static final XViewerColumn Percent_Complete_Total_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Total Percent Complete", 40, SWT.CENTER, false,
               SortDataType.Percent, false, "Percent Complete for the reviews related to the current state.");

   public static final XViewerColumn Hours_Spent_State_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "State Hours Spent", 40, SWT.CENTER, false, SortDataType.Float,
               false, "Hours spent in performing the changes to the current state.");
   public static final XViewerColumn Hours_Spent_State_Task_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "State Task Hours Spent", 40, SWT.CENTER, false,
               SortDataType.Float, false,
               "Hours spent in performing the changes for the tasks related to the current state.");
   public static final XViewerColumn Hours_Spent_State_Review_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "State Review Hours Spent", 40, SWT.CENTER, false,
               SortDataType.Float, false,
               "Hours spent in performing the changes for the reveiws related to the current state.");
   public static final XViewerColumn Hours_Spent_Total_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "State Total Hours Spent", 40, SWT.CENTER, false,
               SortDataType.Percent, false, "Hours spent for all work related to the current state.");

   public static final XViewerColumn Total_Hours_Spent_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Total Hours Spent", 40, SWT.CENTER, false, SortDataType.Percent,
               false, "Hours spent for all work related to all states.");

   public static final XViewerColumn Originator_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Originator", 80, SWT.LEFT, false, SortDataType.String, false);
   public static final XViewerColumn Implementor_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Implementer", 80, SWT.LEFT, false, SortDataType.String, false,
               "User assigned to the Implementation of the changes.");
   public static final XViewerColumn Review_Author_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Review Author", 100, SWT.LEFT, false, SortDataType.String,
               false, "Review Author(s)");
   public static final XViewerColumn Review_Moderator_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Review Moderator", 100, SWT.LEFT, false, SortDataType.String,
               false, "Review Moderator(s)");
   public static final XViewerColumn Review_Reviewer_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Review Reviewer", 100, SWT.LEFT, false, SortDataType.String,
               false, "Review Reviewer(s)");
   public static final XViewerColumn Review_Decider_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Review Decider", 100, SWT.LEFT, false, SortDataType.String,
               false, "Review Decider");
   public static final XViewerColumn Completed_Date_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Completed Date", 80, SWT.CENTER, false, SortDataType.Date, false);
   public static final XViewerColumn Cancelled_Date_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Cancelled Date", 80, SWT.CENTER, false, SortDataType.Date, false);
   public static final XViewerColumn Man_Days_Needed_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Man Days Needed", 40, SWT.CENTER, false, SortDataType.Float,
               false);
   public static final XViewerColumn Percent_Rework_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Percent Rework", 40, SWT.CENTER, false, SortDataType.Integer,
               false);
   public static final XViewerColumn Branch_Status_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Branch Status", 40, SWT.CENTER, false, SortDataType.String,
               false);
   public static final XViewerColumn Number_of_Tasks_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Number of Tasks", 40, SWT.CENTER, false, SortDataType.String,
               false);
   public static final XViewerColumn Last_Modified_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Last Modified", 40, SWT.CENTER, false, SortDataType.Date, false,
               "Retrieves timestamp of last database update of this artifact.");
   public static final XViewerColumn Last_Statused_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Last Statused", 40, SWT.CENTER, false, SortDataType.Date, false,
               "Retrieves timestamp of status (percent completed or hours spent).");
   public static final XViewerColumn Validation_Required_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Validation Required", 80, SWT.LEFT, false, SortDataType.String,
               false, "If set, Originator will be asked to perform a review to\nensure changes are as expected.");
   public static final List<XViewerColumn> columns =
         Arrays.asList(Type_Col, State_Col, Priority_Col, Change_Type_Col, Assignees_Col, Title_Col,
               Actionable_Items_Col, User_Community_Col, ID_Col, Created_Date_Col, Version_Target_Col, Team_Col,
               Notes_Col, Deadline_Col, Annual_Cost_Avoidance_Col, Description_Col, Legacy_PCR_Col, Decision_Col,
               Resolution_Col, Estimated_Release_Date_Col, Release_Date_Col, Work_Package_Col, Category_Col,
               Category2_Col, Category3_Col, Related_To_State_Col, Estimated_Hours_Col, Weekly_Benefit_Hrs_Col,
               Remaining_Hours_Col, Percent_Complete_State_Col, Percent_Complete_State_Task_Col,
               Percent_Complete_State_Review_Col, Percent_Complete_Total_Col, Hours_Spent_State_Col,
               Hours_Spent_State_Task_Col, Hours_Spent_State_Review_Col, Hours_Spent_Total_Col, Total_Hours_Spent_Col,
               Originator_Col, Implementor_Col, Review_Author_Col, Review_Moderator_Col, Review_Reviewer_Col,
               Review_Decider_Col, Completed_Date_Col, Cancelled_Date_Col, Man_Days_Needed_Col, Percent_Rework_Col,
               Branch_Status_Col, Number_of_Tasks_Col, Last_Modified_Col, Last_Statused_Col, Validation_Required_Col);
   public static Map<String, XViewerColumn> idToColumn = null;

   public WorldXViewerFactory() {
      if (idToColumn == null) {
         idToColumn = new HashMap<String, XViewerColumn>();
         for (XViewerColumn xCol : columns) {
            idToColumn.put(xCol.getId(), xCol);
         }
      }
   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
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
