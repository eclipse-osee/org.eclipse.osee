/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ev;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageReportColumns {

   protected static XViewerColumn countryColumn =
      new XViewerColumn("Country", "Country", 40, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn programColumn =
      new XViewerColumn("Program", "Program", 100, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn insertionColumn =
      new XViewerColumn("Insertion", "Insertion", 80, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn insertionActivityColumn = new XViewerColumn("Insertion Activity",
      "Insertion Activity", 80, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn workPackageNameColumn =
      new XViewerColumn("Work Package", "Work Package", 80, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn wpColorTeamColumn = new XViewerColumn("Work Package Color Team",
      "Work Package Color Team", 80, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn wpProgramColumn = new XViewerColumn("Work Package Program", "Work Package Program",
      80, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn wpIdColumn = new XViewerColumn("Work Package Id", "Work Package Id", 80,
      XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn wpActivityIdColumn = new XViewerColumn("Work Package Activity Id",
      "Work Package Activity Id", 80, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn wpTeamAiNames = new XViewerColumn("Work Package Teams/AIs", "Work Package Teams/AIs",
      200, XViewerAlign.Left, true, SortDataType.String, false, "");
   protected static XViewerColumn wpId = new XViewerColumn("Work Package Id", "Work Package Id", 60, XViewerAlign.Left,
      true, SortDataType.Long, false, "");
   public static XViewerColumn wpActiveColumn = new XViewerColumn("Work Package Active", "Work Package Active", 40,
      XViewerAlign.Left, true, SortDataType.Boolean, false, "");
   public static XViewerColumn wpPercentComplete = new XViewerColumn("Work Package Percent Complete",
      "Work Package Percent Complete", 40, XViewerAlign.Left, true, SortDataType.Percent, false, "");
   public static XViewerColumn wpPointsNumeric = new XViewerColumn("Work Package Points", "Work Package Points", 40,
      XViewerAlign.Left, true, SortDataType.Float, false, "");
   public static XViewerColumn wpType = new XViewerColumn("Work Package Type", "Work Package Type", 80,
      XViewerAlign.Left, true, SortDataType.String, false, "");
   public static XViewerColumn wpBac = new XViewerColumn("Work Package BAC", "Work Package BAC", 40, XViewerAlign.Left,
      true, SortDataType.Float, false, "");
   public static XViewerColumn wpIpt = new XViewerColumn("Work Package IPT", "Work Package IPT", 120, XViewerAlign.Left,
      true, SortDataType.String, false, "");
   public static XViewerColumn wpStartDate = new XViewerColumn("Work Package Start Date", "Work Start Date", 40,
      XViewerAlign.Left, true, SortDataType.Date, false, "");
   public static XViewerColumn wpEndDate = new XViewerColumn("Work Package End Date", "Work Package End Date", 40,
      XViewerAlign.Left, true, SortDataType.Date, false, "");
   public static XViewerColumn wpNotes = new XViewerColumn("Work Package Nots", "Work Package Notes", 200,
      XViewerAlign.Left, true, SortDataType.String, false, "");
   public static XViewerColumn wpAnnotation = new XViewerColumn("Work Package Annotation", "Work Package Annotation",
      40, XViewerAlign.Left, true, SortDataType.String, false, "");

   protected static XViewerColumn getDefaultColumn(String nameId, int length) {
      return new XViewerColumn(nameId, nameId, length, XViewerAlign.Left, true, SortDataType.String, false, "");
   }
}
