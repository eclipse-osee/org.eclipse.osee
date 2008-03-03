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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.ui.admin.AdminPlugin;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.autoRun.AutoRunStartup;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.autoRun.LaunchAutoRunWorkbench;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask.RunDb;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 */
public class XAutoRunViewer extends XWidget {

   private AutoRunXViewer autoRunXViewer;
   private Label extraInfoLabel;
   private final AutoRunTab autoRunTab;
   private Set<IAutoRunTask> scheduledTasks = new HashSet<IAutoRunTask>();
   private ToolItem startSchedulerAction;
   private ToolItem stopShedulerAction;

   /**
    * @param label
    */
   public XAutoRunViewer(AutoRunTab autoRunTab) {
      super("Auto Run Tasks");
      this.autoRunTab = autoRunTab;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {

      // Create Text Widgets
      if (displayLabel && !label.equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      mainComp.setLayout(ALayout.getZeroMarginLayout());

      createTaskActionBar(mainComp);

      autoRunXViewer = new AutoRunXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      autoRunXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      autoRunXViewer.setContentProvider(new AutoRunContentProvider(autoRunXViewer));
      autoRunXViewer.setLabelProvider(new AutoRunLabelProvider(autoRunXViewer));

      Tree tree = autoRunXViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      loadTable();
   }

   public void createTaskActionBar(Composite parent) {

      // Button composite for state transitions, etc
      Composite bComp = new Composite(parent, SWT.NONE);
      // bComp.setBackground(mainSComp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(leftComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("");

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("run_exc.gif"));
      item.setToolTipText("Run Selected Tasks");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            run();
         }
      });

      startSchedulerAction = new ToolItem(toolBar, SWT.PUSH);
      startSchedulerAction.setImage(SkynetGuiPlugin.getInstance().getImage("clock.gif"));
      startSchedulerAction.setToolTipText("Start Scheduler for Selected Tasks/Times");
      startSchedulerAction.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            startScheduler();
         }
      });

      stopShedulerAction = new ToolItem(toolBar, SWT.PUSH);
      stopShedulerAction.setImage(SkynetGuiPlugin.getInstance().getImage("redRemove.gif"));
      stopShedulerAction.setToolTipText("Stop Scheduler");
      stopShedulerAction.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            stopScheduler();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("chkbox_enabled.gif"));
      item.setToolTipText("Select All");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            autoRunXViewer.selectAll();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("chkbox_disabled.gif"));
      item.setToolTipText("De-Select All");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            autoRunXViewer.delSelectAll();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("refresh.gif"));
      item.setToolTipText("Refresh Roles");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            loadTable();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            autoRunXViewer.getCustomize().handleTableCustomization();
         }
      });
      updateToolBarInfo();
   }

   private void startScheduler() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            Result runnable = autoRunTab.isRunnable();
            if (runnable.isFalse()) {
               runnable.popup();
               return;
            }
            if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Start Scheduler",
                  "Start Scheduler for Selected Tasks Below")) return;
            scheduledTasks.clear();
            scheduledTasks.addAll(autoRunXViewer.getRunList());
            if (!runningSchedule) {
               scheduleThread.start();
            }
            autoRunXViewer.refresh();
            updateToolBarInfo();
         }
      });
   }

   public boolean isScheduled(IAutoRunTask autoRunTask) {
      return scheduledTasks.contains(autoRunTask);
   }

   private void stopScheduler() {
      stopSchedule = true;
   }

   public void updateToolBarInfo() {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (runningSchedule) {
               extraInfoLabel.setText("  Scheduler Running: " + timeStamp);
            } else
               extraInfoLabel.setText("  Scheduler Stopped");
            startSchedulerAction.setEnabled(!runningSchedule);
            stopShedulerAction.setEnabled(runningSchedule);
            extraInfoLabel.getParent().layout();
         };
      });
   }

   private String timeStamp = "";
   private boolean runningSchedule = false;
   private boolean stopSchedule = false;

   Thread scheduleThread = new Thread() {
      /* (non-Javadoc)
        * @see java.lang.Thread#run()
        */
      @Override
      public void run() {
         while (true) {
            if (stopSchedule) {
               runningSchedule = false;
               stopSchedule = false;
               updateToolBarInfo();
               System.out.println("Stopped Scheduler");
               return;
            }
            runningSchedule = true;
            System.out.println("Timestamp " + XDate.getDateNow(XDate.MMDDYYHHMM));
            timeStamp = XDate.getDateNow("HH:mm");
            updateToolBarInfo();

            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {// Process auto-run tasks
                  for (IAutoRunTask autoRunTask : scheduledTasks) {
                     if (autoRunTask.get24HourStartTime().equals(timeStamp)) {
                        try {
                           System.out.println("Running " + autoRunTask.getAutoRunUniqueId());
                           LaunchAutoRunWorkbench.launch(autoRunTask, getDefaultDbConnection(autoRunTask),
                                 autoRunTab.isEmailOverridden() ? autoRunTab.getOverriddenEmail() : null);
                        } catch (Exception ex) {
                           OSEELog.logException(AdminPlugin.class, ex, false);
                        }
                     }
                  }
               };
            });

            try {
               Thread.sleep(60000);
            } catch (Exception ex) {
               ex.printStackTrace();
               stopSchedule = true;
            }
         }
      }
   };

   private void run() {
      Result runnable = autoRunTab.isRunnable();
      if (runnable.isFalse()) {
         runnable.popup();
         return;
      }
      try {
         if (autoRunTab.getLaunchWBCheckBox().isSelected()) {
            StringBuffer sb = new StringBuffer("Launch Auto Tasks:\n\n");
            for (IAutoRunTask autoRunTask : autoRunXViewer.getRunList())
               sb.append(" - " + autoRunTask.getAutoRunUniqueId() + " against " + getDefaultDbConnection(autoRunTask) + "\n");
            sb.append("\nNOTE: All will kickoff immediately");
            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Launch Auto Tasks", sb.toString())) {
               for (IAutoRunTask autoRunTask : autoRunXViewer.getRunList())
                  // TODO NEED TO SYNC WITH CODE ON TIM TO FIX THESE METHOD CALLS
                  LaunchAutoRunWorkbench.launch(autoRunTask, getDefaultDbConnection(autoRunTask),
                        autoRunTab.isEmailOverridden() ? autoRunTab.getOverriddenEmail() : null);
            }
         } else {
            StringBuffer sb = new StringBuffer("Run Auto Tasks in Current Workbench:\n\n");
            for (IAutoRunTask autoRunTask : autoRunXViewer.getRunList())
               sb.append(" - " + autoRunTask.getAutoRunUniqueId() + " against " + getDefaultDbConnection(autoRunTask) + "\n");
            sb.append("\nNOTE: All will kickoff immediately");
            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Launch Auto Tasks", sb.toString())) {
               for (IAutoRunTask autoRunTask : autoRunXViewer.getRunList())
                  AutoRunStartup.runAutoRunTask(autoRunTask.getAutoRunUniqueId(),
                        autoRunTab.isEmailOverridden() ? autoRunTab.getOverriddenEmail() : null);
            }

         }
      } catch (Exception ex) {
         OSEELog.logException(AdminPlugin.class, ex, true);
      }
   }

   private String getDefaultDbConnection(IAutoRunTask autoRunTask) {
      return autoRunTask.getRunDb() == RunDb.Production_Db ? autoRunTab.getProdDbConfigText().getText() : autoRunTab.getTestDbConfigText().getText();
   }

   public boolean isLaunchNewWorkbench() {
      return autoRunTab.getLaunchWBCheckBox().isSelected();
   }

   public void loadTable() {
      try {
         autoRunXViewer.set(AutoRunStartup.getAutoRunTasks());
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      refresh();
   }

   @SuppressWarnings("unchecked")
   public ArrayList<IAutoRunTask> getSelectedAutoRunItems() {
      ArrayList<IAutoRunTask> items = new ArrayList<IAutoRunTask>();
      if (autoRunXViewer == null) return items;
      if (autoRunXViewer.getSelection().isEmpty()) return items;
      Iterator i = ((IStructuredSelection) autoRunXViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         items.add((IAutoRunTask) obj);
      }
      return items;
   }

   @Override
   public Control getControl() {
      return autoRunXViewer.getTree();
   }

   @Override
   public void dispose() {
      autoRunXViewer.dispose();
   }

   @Override
   public void setFocus() {
      autoRunXViewer.getTree().setFocus();
   }

   public void refresh() {
      autoRunXViewer.refresh();
      setLabelError();
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public void setXmlData(String str) {
   }

   @Override
   public String getXmlData() {
      return null;
   }

   public String toHTML(String labelFont) {
      return "";
   }

   @Override
   public String getReportData() {
      return null;
   }

   /**
    * @return Returns the xViewer.
    */
   public AutoRunXViewer getXViewer() {
      return autoRunXViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return autoRunXViewer.getInput();
   }

   public boolean isEditable() {
      return editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

}
