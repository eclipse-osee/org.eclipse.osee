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
package org.eclipse.osee.ats.util.widgets.defect;

import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class DefectXViewerFactory extends SkynetXViewerFactory {

   public static XViewerColumn Severity_Col =
         new XViewerColumn("osee.defect.severity", "Severity", 70, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Disposition_Col =
         new XViewerColumn("osee.defect.disposition", "Disposition", 70, SWT.CENTER, true, SortDataType.String, false,
               null);
   public static XViewerColumn Closed_Col =
         new XViewerColumn("osee.defect.closed", "Closed", 70, SWT.LEFT, true, SortDataType.Boolean, false, null);
   public static XViewerColumn User_Col =
         new XViewerColumn("osee.defect.user", "User", 100, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Created_Date_Col =
         new XViewerColumn("osee.defect.createdDate", "Created Date", 80, SWT.LEFT, true, SortDataType.Date, false,
               null);
   public static XViewerColumn Injection_Activity_Col =
         new XViewerColumn("osee.defect.injectionActivity", "Injection Activity", 35, SWT.LEFT, true,
               SortDataType.String, false, null);
   public static XViewerColumn Description_Col =
         new XViewerColumn("osee.defect.description", "Description", 200, SWT.LEFT, true,
               SortDataType.String_MultiLine, false, null);
   public static XViewerColumn Location_Col =
         new XViewerColumn("osee.defect.location", "Location", 200, SWT.LEFT, true, SortDataType.String_MultiLine,
               false, null);
   public static XViewerColumn Resolution_Col =
         new XViewerColumn("osee.defect.resolution", "Resolution", 200, SWT.LEFT, true, SortDataType.String_MultiLine,
               false, null);

   private static String NAMESPACE = "osee.ats.DefectXViewer";

   public DefectXViewerFactory() {
      super(NAMESPACE);
      registerColumn(Severity_Col, Disposition_Col, Closed_Col, User_Col, Created_Date_Col, Injection_Activity_Col,
            Description_Col, Location_Col, Resolution_Col);
   }

}
