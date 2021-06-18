/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.workflow.task.widgets.estimates;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class TaskEstFactory extends SkynetXViewerFactory {

   public final static String NAMESPACE = "TaskEstFactory";

   public static XViewerColumn Check_Col = new XViewerColumn("ats.taskest.check", "Select", 60, XViewerAlign.Left, true,
      SortDataType.String, false, "Check and plus to create canned tasks.  Add task to create manual tasks.");
   public static XViewerColumn Name_Col =
      new XViewerColumn("ats.taskest.name", "Name", 180, XViewerAlign.Left, true, SortDataType.String, false, "");
   public static XViewerColumn Status_Col = new XViewerColumn("ats.taskest.status", "Status", 80, XViewerAlign.Left,
      true, SortDataType.String, false, "State of the Estimating task.  Double-click to open task.");
   public static XViewerColumn Assignee_Col =
      new XViewerColumn("ats.taskest.assignee", "Assignees", 120, XViewerAlign.Left, true, SortDataType.String, false,
         "Assignee(s) of Estimating task.  Double-click to open task.");
   public static XViewerColumn Estimated_Points_Col = new XViewerColumn("ats.taskest.est.points", "Est Points", 70,
      XViewerAlign.Left, true, SortDataType.Float, false, "Double-click to open task.");
   public static XViewerColumn Tle_Reviewed_Col = new XViewerColumn("ats.taskest.tle.reviewed", "TLE Reviewed", 50,
      XViewerAlign.Left, true, SortDataType.Boolean, false, "Double-click to open task.");
   public static XViewerColumn Estimated_Completion_Date_Col = new XViewerColumn("ats.taskest.est.completion.date",
      "Est Completion Date", 60, XViewerAlign.Left, true, SortDataType.Date, false, null);
   public static XViewerColumn Description_Col = new XViewerColumn("ats.taskest.description", "Description", 120,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Assumptions_Col = new XViewerColumn("ats.taskest.assumptions", "Assumptions", 120,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Notes_Col =
      new XViewerColumn("ats.taskest.notes", "Notes", 120, XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Attachments_Col = new XViewerColumn("ats.taskest.attachments", "Attachments", 120,
      XViewerAlign.Left, true, SortDataType.String, false, "Shows number of attachments.  Double-click to open task.");

   public TaskEstFactory(IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      registerColumns(Check_Col, Name_Col, Status_Col, Assignee_Col, Estimated_Points_Col, Tle_Reviewed_Col,
         Estimated_Completion_Date_Col, Description_Col, Assumptions_Col, Notes_Col, Attachments_Col);
   }

}
