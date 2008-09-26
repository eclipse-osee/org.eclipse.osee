/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.test.IXViewerTestTask.RunDb;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.test.IXViewerTestTask.TaskType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class XViewerTest extends XViewer {
   private final Set<IXViewerTestTask> runList = new HashSet<IXViewerTestTask>();

   /**
    * @param parent
    * @param style
    * @param namespace
    * @param viewerFactory
    */
   public XViewerTest(Composite parent, int style) {
      super(parent, style, new XViewerTestFactory());
   }

   public boolean isScheduled(IXViewerTestTask autoRunTask) {
      return true;
   }

   public boolean isRun(IXViewerTestTask autoRunTask) {
      return runList.contains(autoRunTask);
   }

   public void setRun(IXViewerTestTask autoRunTask, boolean run) {
      if (run)
         runList.add(autoRunTask);
      else
         runList.remove(autoRunTask);
   }

   private static XViewerTest xViewerTest = null;

   /**
    * @param args
    */
   public static void main(String[] args) {
      Display Display_1 = Display.getDefault();
      Shell Shell_1 = new Shell(Display_1, SWT.SHELL_TRIM);
      Shell_1.setText("XViewer Test");
      Shell_1.setBounds(0, 0, 1000, 500);
      Shell_1.setLayout(new GridLayout());
      Shell_1.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING));

      Label label = new Label(Shell_1, SWT.None);
      label.setText("Refresh");
      label.addListener(SWT.MouseUp, new Listener() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         @Override
         public void handleEvent(Event event) {
            List<Object> tasks = new ArrayList<Object>();
            for (int x = 0; x < 1; x++) {
               tasks.addAll(getTestTasks());
            }
            System.err.println("Refreshing Input...");
            xViewerTest.setInput(tasks);
         }
      });

      xViewerTest = new XViewerTest(Shell_1, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
      xViewerTest.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      xViewerTest.setContentProvider(new XViewerTestContentProvider());
      xViewerTest.setLabelProvider(new XViewerTestLabelProvider(xViewerTest));

      List<Object> tasks = new ArrayList<Object>();
      for (int x = 0; x < 1; x++) {
         tasks.addAll(getTestTasks());
      }
      System.err.println("Setting Input...");
      xViewerTest.setInput(tasks);
      Shell_1.open();
      while (!Shell_1.isDisposed()) {
         if (!Display_1.readAndDispatch()) {
            Display_1.sleep();
         }
      }

      Display_1.dispose();
   }

   private static List<IXViewerTestTask> getTestTasks() {
      List<IXViewerTestTask> tasks = new ArrayList<IXViewerTestTask>();
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Backup, "org.eclipse.osee.test1", "10:03",
            "run to test this", "Suite A", "mark@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Data_Exchange, "org.eclipse.osee.test2", "9:22",
            "run to test that", "Suite B", "john@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Backup, "org.eclipse.osee.test4", "8:23",
            "in this world", "Suite A", "john@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Backup, "org.eclipse.osee.test3", "23:01",
            "now is the time", "Suite B", "mike@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Db_Health, "org.eclipse.osee.test5", "7:32",
            "may be never", "Suite A", "steve@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Data_Exchange, "org.eclipse.osee.test14", "6:11",
            "how can this solve the problem", "Suite A", "steve@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Backup, "org.eclipse.osee.test6", "5:13",
            "run to test this", "Suite B", "john@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Db_Health, "org.eclipse.osee.test12", "23:15",
            "run to test this", "Suite A", "mike@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Backup, "org.eclipse.osee.test13", "4:01",
            "run to test this", "Suite B", "steve@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Data_Exchange, "org.eclipse.osee.test11", "3:16",
            "run to test this", "Suite A", "steve@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Backup, "org.eclipse.osee.test10", "5:01",
            "run to test this", "Suite C", "mike@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Data_Exchange, "org.eclipse.osee.test9", "4:27",
            "run to test this", "Suite C", "steve@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Regression, "org.eclipse.osee.test7", "2:37",
            "run to test this", "Suite C", "john@eclipse.com"));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Db_Health, "org.eclipse.osee.test8", "24:00",
            "run to test this", "Suite C", "mike@eclipse.com"));
      return tasks;
   }
}
