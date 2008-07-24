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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class AutoRunXViewerFactory extends SkynetXViewerFactory {

   public static XViewerColumn Run_Col =
         new XViewerColumn("osee.autoRun.run", "Run", 40, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Name_Col =
         new XViewerColumn("osee.autoRun.name", "Name", 350, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Schedule_Time =
         new XViewerColumn("osee.autoRun.start", "24 Hour Start", 40, SWT.CENTER, true, SortDataType.String, false,
               null);
   public static XViewerColumn Run_Db =
         new XViewerColumn("osee.autoRun.runDb", "Run DB", 80, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Task_Type =
         new XViewerColumn("osee.autoRun.taskType", "Task Type", 80, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Category =
         new XViewerColumn("osee.autoRun.category", "Category", 80, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Notification =
         new XViewerColumn("osee.autoRun.emailTo", "Email Results To", 80, SWT.LEFT, true, SortDataType.String, false,
               null);
   public static XViewerColumn Description =
         new XViewerColumn("osee.autoRun.description", "Description", 700, SWT.LEFT, true, SortDataType.String, false,
               null);
   public List<XViewerColumn> columns =
         Arrays.asList(Run_Col, Name_Col, Schedule_Time, Run_Db, Task_Type, Category, Notification, Description);

   public AutoRunXViewerFactory() {
      super("osee.ats.UserRoleXViewer");
      registerColumn(Run_Col, Name_Col, Schedule_Time, Run_Db, Task_Type, Category, Notification, Description);
   }

}
