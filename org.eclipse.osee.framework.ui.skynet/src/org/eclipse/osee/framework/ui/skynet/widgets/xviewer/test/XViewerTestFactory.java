/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.test;

import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerTestFactory extends XViewerFactory {

   private static String COLUMN_NAMESPACE = "xviewer.test";
   public static XViewerColumn Run_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".run", "Run", 50, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Name_Col =
         new XViewerColumn(COLUMN_NAMESPACE + ".name", "Name", 150, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Schedule_Time =
         new XViewerColumn(COLUMN_NAMESPACE + ".startTime", "Start Time", 40, SWT.CENTER, true, SortDataType.String,
               false, null);
   public static XViewerColumn Run_Db =
         new XViewerColumn(COLUMN_NAMESPACE + ".runDb", "Run DB", 80, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Task_Type =
         new XViewerColumn(COLUMN_NAMESPACE + ".taskType", "Task Type", 80, SWT.LEFT, true, SortDataType.String, false,
               null);
   public static XViewerColumn Category =
         new XViewerColumn(COLUMN_NAMESPACE + ".category", "Category", 80, SWT.LEFT, false, SortDataType.String, false,
               null);
   public static XViewerColumn Notification =
         new XViewerColumn(COLUMN_NAMESPACE + ".emailResults", "Email Results To", 150, SWT.LEFT, true,
               SortDataType.String, false, null);
   public static XViewerColumn Description =
         new XViewerColumn(COLUMN_NAMESPACE + ".description", "Description", 75, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn Other_Description =
         new XViewerColumn(COLUMN_NAMESPACE + ".otherDescription", "Other Description", 75, SWT.LEFT, false,
               SortDataType.String, false, null);

   public XViewerTestFactory() {
      super("xviewer.test");
      registerColumn(Run_Col, Name_Col, Schedule_Time, Run_Db, Task_Type, Category, Notification, Description,
            Other_Description);
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return new XViewerTestCustomizations();
   }

}
