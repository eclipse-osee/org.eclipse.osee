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
package org.eclipse.osee.coverage.editor.xcover;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class CoverageXViewerFactory extends SkynetXViewerFactory {

   public static XViewerColumn Eng_Build_Id_Col =
         new XViewerColumn("lba.promote.engBldId", "Engineering Build ID", 150, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn Plan_CM_Build_Id_Col =
         new XViewerColumn("lba.promote.planCmBldId", "Planned CM Build ID", 150, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn View_Compare_Col =
         new XViewerColumn("lba.promote.viewCompare", "View Comparison", 150, SWT.CENTER, true, SortDataType.String,
               false, null);
   public static XViewerColumn Sub_System_Col =
         new XViewerColumn("lba.promote.subSystem", "View Comparison SubSystem", 50, SWT.LEFT, true,
               SortDataType.String, false, "This field is populated by the search of the View Comparison");
   public static XViewerColumn Promoted_Col =
         new XViewerColumn("lba.promote.promoted", "Promoted", 70, SWT.LEFT, true, SortDataType.Boolean, false, null);
   public static XViewerColumn User_Col =
         new XViewerColumn("lba.promote.user", "User", 100, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Date_Col =
         new XViewerColumn("lba.promote.date", "Date", 90, SWT.LEFT, true, SortDataType.Date, false, null);
   public static XViewerColumn Promoted_Date_Col =
         new XViewerColumn("lba.promote.promotedDate", "Promoted Date", 90, SWT.LEFT, true, SortDataType.Date, false,
               null);
   public static XViewerColumn Notes_Col =
         new XViewerColumn("lba.promote.notes", "Notes", 200, SWT.LEFT, true, SortDataType.String_MultiLine, false,
               null);
   public static XViewerColumn Code_Workflow_Id_Col =
         new XViewerColumn("lba.promote.codeWorkflow", "Code Workflow", 80, SWT.LEFT, true, SortDataType.String, false,
               "");
   public static XViewerColumn Code_Workflow_Title_Col =
         new XViewerColumn("lba.promote.codeTitle", "Code Title", 200, SWT.LEFT, true, SortDataType.String, false, "");
   public static XViewerColumn Code_Workflow_Pcr_Col =
         new XViewerColumn("lba.promote.codePcr", "Code PCR", 80, SWT.LEFT, true, SortDataType.String, false, "");
   public static XViewerColumn CM_Build_Col =
         new XViewerColumn("lba.promote.cmBuild", "CM Build", 80, SWT.LEFT, true, SortDataType.String, false, "");
   public static XViewerColumn View_Comp_Groups_Col =
         new XViewerColumn("lba.promote.viewGroups", "View Groups", 40, SWT.LEFT, true, SortDataType.String, false,
               "Sorting on this column shows common view comparisons.");
   public static XViewerColumn CSCI_Col =
         new XViewerColumn("lba.promote.csci", "CSCI", 80, SWT.LEFT, true, SortDataType.String, false, "");
   public static XViewerColumn Lba_Change_Type_Col =
         new XViewerColumn("lba.promote.changeType", "Type", 80, SWT.LEFT, true, SortDataType.String, false, "");
   private static String NAMESPACE = "lba.ats.PromoteXViewer";

   public CoverageXViewerFactory() {
      super(NAMESPACE);
      registerColumns(User_Col, Date_Col, CM_Build_Col, Plan_CM_Build_Id_Col, Eng_Build_Id_Col, View_Comp_Groups_Col,
            View_Compare_Col, Sub_System_Col, Promoted_Col, Promoted_Date_Col, Notes_Col, Code_Workflow_Id_Col,
            Code_Workflow_Title_Col, Code_Workflow_Pcr_Col);
   }

}
