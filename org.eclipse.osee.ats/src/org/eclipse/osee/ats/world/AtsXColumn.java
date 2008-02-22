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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public enum AtsXColumn {

   Type_Col("Type", 80, SWT.LEFT, true, SortDataType.String, false),

   State_Col("State", 70, SWT.LEFT, true, SortDataType.String, false),

   Priority_Col("Priority", 20, SWT.CENTER, true, SortDataType.String, false),

   Change_Type_Col("Change Type", 22, SWT.LEFT, true, SortDataType.String, false),

   Assignees_Col("Assignees", 100, SWT.LEFT, true, SortDataType.String, false),

   Title_Col("Title", 200, SWT.LEFT, true, SortDataType.String, false),

   Actionable_Items_Col("Actionable Items", 80, SWT.LEFT, true, SortDataType.String, false, "Actionable Items that are impacted by this change."),

   User_Community_Col("User Community", 60, SWT.LEFT, true, SortDataType.String, false, "Program, Project or Group that caused the creation of this Action."),

   ID_Col("ID", 40, SWT.LEFT, true, SortDataType.String, false, "Human Readable ID"),

   Created_Date_Col("Created Date", 80, SWT.LEFT, true, SortDataType.Date, false),

   Version_Target_Col("Version Target", 40, SWT.LEFT, true, SortDataType.String, false),

   Team_Col("Team", 50, SWT.LEFT, true, SortDataType.String, false, "Team that has been assigned to work this Action."),

   Notes_Col("Notes", 80, SWT.LEFT, true, SortDataType.String, true),

   Deadline_Col("Deadline", 80, SWT.LEFT, true, SortDataType.Date, true, "Date the changes need to be completed by."),

   // Aren't shown by default

   Annual_Cost_Avoidance_Col("Annual Cost Avoidance", 50, SWT.LEFT, false, SortDataType.Float, false, "Hours that would be saved for the first year if this change were completed.\n\n" + "(Weekly Benefit Hours * 52 weeks) - Remaining Hours\n\n" + "If number is high, benefit is great given hours remaining."),

   Description_Col("Description", 150, SWT.LEFT, false, SortDataType.String, true),

   Metrics_from_Tasks_Col("Metrics from Tasks", 40, SWT.LEFT, false, SortDataType.Boolean, false, "Determine Estimated Hours and Percent Complete from Implement Tasks."),

   Legacy_PCR_Col("Legacy PCR", 40, SWT.LEFT, false, SortDataType.String, false),

   Decision_Col("Decision", 150, SWT.LEFT, false, SortDataType.String, false),

   Resolution_Col("Resolution", 150, SWT.LEFT, false, SortDataType.String, false),

   Estimated_Release_Date_Col(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getDisplayName(), 80, SWT.LEFT, false, SortDataType.Date, false, "Date the changes will be made available to the users."),

   Release_Date_Col(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getDisplayName(), 80, SWT.LEFT, false, SortDataType.Date, false, "Date the changes were made available to the users."),

   Work_Package_Col("Work Package", 80, SWT.LEFT, false, SortDataType.String, true),

   Category_Col("Category", 80, SWT.LEFT, false, SortDataType.String, true, "Open field for user to be able to enter text to use for categorizing/sorting."),

   Category2_Col("Category2", 80, SWT.LEFT, false, SortDataType.String, true, "Open field for user to be able to enter text to use for categorizing/sorting."),

   Category3_Col("Category3", 80, SWT.LEFT, false, SortDataType.String, true, "Open field for user to be able to enter text to use for categorizing/sorting."),

   Related_To_State_Col("Related To State", 80, SWT.LEFT, false, SortDataType.String, true, "State of the parent State Machine that this object is related to."),

   Total_Percent_Complete_Col("Total Percent Complete", 40, SWT.CENTER, false, SortDataType.Percent, false),

   Estimated_Hours_Col("Estimated Hours", 40, SWT.CENTER, false, SortDataType.Float, true, "Hours estimated to implement the changes associated with this Action."),

   Weekly_Benefit_Hrs_Col("Weekly Benefit Hrs", 40, SWT.CENTER, false, SortDataType.Float, false, "Estimated number of hours that will be saved over a single year if this change is completed."),

   Total_Hours_Spent_Col("Total Hours Spent", 40, SWT.CENTER, false, SortDataType.Float, false, "Total Hours spent for all work done in states and tasks."),

   Remaining_Hours_Col("Remaining Hours", 40, SWT.CENTER, false, SortDataType.Float, false, "Hours that remain to complete the changes.\n\nEstimated Hours - (Estimated Hours * Percent Complete)."),

   State_Percent_Col("State Percent", 40, SWT.CENTER, false, SortDataType.Percent, false, "Percent Complete for the changes to the current state."),

   State_Hours_Spent_Col("State Hours Spent", 40, SWT.CENTER, false, SortDataType.Float, false, "Hours spent in performing the changes to the current state."),

   Originator_Col("Originator", 80, SWT.LEFT, false, SortDataType.String, false),

   Implementor_Col("Implementer", 80, SWT.LEFT, false, SortDataType.String, false, "User assigned to the Implementation of the changes."),

   Review_Author_Col("Review Author", 100, SWT.LEFT, false, SortDataType.String, false, "Review Author(s)"),

   Review_Moderator_Col("Review Moderator", 100, SWT.LEFT, false, SortDataType.String, false, "Review Moderator(s)"),

   Review_Reviewer_Col("Review Reviewer", 100, SWT.LEFT, false, SortDataType.String, false, "Review Reviewer(s)"),

   Review_Decider_Col("Review Decider", 100, SWT.LEFT, false, SortDataType.String, false, "Review Decider"),

   Completed_Date_Col("Completed Date", 80, SWT.CENTER, false, SortDataType.Date, false),

   Cancelled_Date_Col("Cancelled Date", 80, SWT.CENTER, false, SortDataType.Date, false),

   Man_Days_Needed_Col("Man Days Needed", 40, SWT.CENTER, false, SortDataType.Float, false),

   Percent_Rework_Col("Percent Rework", 40, SWT.CENTER, false, SortDataType.Integer, false),

   Branch_Status_Col("Branch Status", 40, SWT.CENTER, false, SortDataType.String, false),

   Number_of_Tasks_Col("Number of Tasks", 40, SWT.CENTER, false, SortDataType.String, false),

   Validation_Required_Col("Validation Required", 80, SWT.LEFT, false, SortDataType.String, false, "If set, Originator will be asked to perform a review to\nensure changes are as expected.");

   private final String name;
   private final int width;
   private final int align;
   private final boolean show;
   private final SortDataType sortDataType;
   private final String desc;
   private static Map<String, AtsXColumn> nameToAtsXColumn = new HashMap<String, AtsXColumn>();
   private final boolean multiColumnEditable;

   public static AtsXColumn getAtsXColumn(XViewerColumn xCol) {
      if (nameToAtsXColumn.size() == 0) {
         for (AtsXColumn atsCol : AtsXColumn.values())
            nameToAtsXColumn.put(atsCol.getName(), atsCol);
      }
      return nameToAtsXColumn.get(xCol.getSystemName());
   }

   public XViewerColumn getXViewerColumn(AtsXColumn atsXCol) {
      XViewerColumn xCol =
            new XViewerColumn(atsXCol.name, atsXCol.width, atsXCol.width, atsXCol.align, atsXCol.isShow(),
                  atsXCol.sortDataType, 0);
      if (atsXCol.getDesc() != null)
         xCol.setToolTip(atsXCol.getName() + ":\n" + atsXCol.getDesc());
      else
         xCol.setToolTip(atsXCol.getDesc());
      return xCol;
   }

   private AtsXColumn(String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(name, width, align, show, sortDataType, multiColumnEditable, null);
   }

   private AtsXColumn(String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String desc) {
      this.name = name;
      this.width = width;
      this.align = align;
      this.show = show;
      this.sortDataType = sortDataType;
      this.multiColumnEditable = multiColumnEditable;
      this.desc = desc;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the align
    */
   public int getAlign() {
      return align;
   }

   /**
    * @return the show
    */
   public boolean isShow() {
      return show;
   }

   /**
    * @return the sortDataType
    */
   public SortDataType getSortDataType() {
      return sortDataType;
   }

   /**
    * @return the width
    */
   public int getWidth() {
      return width;
   }

   /**
    * @return the desc
    */
   public String getDesc() {
      return desc;
   }

   public boolean isMultiColumnEditable() {
      return multiColumnEditable;
   }

}
