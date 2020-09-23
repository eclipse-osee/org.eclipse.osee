/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.review;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

/**
 * @author Donald G. Dunne
 */
public class PeerReviewDefectXViewerColumns {

   public static XViewerColumn Severity_Col = new XViewerColumn("osee.defect.severity", "Severity", 85,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Disposition_Col = new XViewerColumn("osee.defect.disposition", "Disposition", 70,
      XViewerAlign.Center, true, SortDataType.String, false, null);
   public static XViewerColumn Closed_Col =
      new XViewerColumn("osee.defect.closed", "Closed", 70, XViewerAlign.Left, true, SortDataType.Boolean, false, null);
   public static XViewerColumn User_Col = new XViewerColumn("osee.defect.user", "Originator", 100, XViewerAlign.Left,
      true, SortDataType.String, false, null);
   public static XViewerColumn Created_Date_Col = new XViewerColumn("osee.defect.createdDate", "Created Date", 80,
      XViewerAlign.Left, true, SortDataType.Date, false, null);
   public static XViewerColumn Injection_Activity_Col = new XViewerColumn("osee.defect.injectionActivity",
      "Injection Activity", 35, XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Description_Col = new XViewerColumn("osee.defect.description", "Description", 200,
      XViewerAlign.Left, true, SortDataType.String_MultiLine, false, null);
   public static XViewerColumn Location_Col = new XViewerColumn("osee.defect.location", "Location", 100,
      XViewerAlign.Left, true, SortDataType.String_MultiLine, false, null);
   public static XViewerColumn Resolution_Col = new XViewerColumn("osee.defect.resolution", "Resolution", 200,
      XViewerAlign.Left, true, SortDataType.String_MultiLine, false, null);
   public static XViewerColumn DefectId_Col =
      new XViewerColumn("osee.defect.id", "Defect Id", 100, XViewerAlign.Left, false, SortDataType.String, false, null);
   public static XViewerColumn Notes_Col = new XViewerColumn("osee.defect.notes", "Notes", 200, XViewerAlign.Left, true,
      SortDataType.String_MultiLine, false, null);
}
