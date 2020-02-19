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
package org.eclipse.osee.branch.gantt.views;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.osee.branch.gantt.Activator;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

/**
 * Use GANTT chart to show time-sequenced visual of baseline branches
 *
 * @author Donald G. Dunne
 */
public class BranchVisualizationView extends ViewPart {

   public static final String ID = "org.eclipse.osee.branch.gantt.views.BranchVisualizationView";
   private GanttChart ganttChart;
   private Action zoomOutAction;
   private Action zoomInAction;
   private Action refreshAction;
   private Composite parent;
   private BranchGanttSettings settings;
   private XBranchSelectWidget xBranchSelectWidget;
   private static String BRANCH_KEY = "branch.visualization.default";
   private Composite bodyComposite;
   private Composite bottomGroup1;
   private Composite bottomGroup2;

   public BranchVisualizationView() {
   }

   @Override
   public void createPartControl(Composite parent) {
      this.parent = parent;

      setTitleImage(ImageManager.getImage(FrameworkImage.BRANCH));

      bodyComposite = new Composite(parent, SWT.None);
      bodyComposite.setLayout(new GridLayout(1, false));
      bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      bodyComposite.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            storeSelectedBranch();
         }

      });

      addBranchSelection();
      drawChart();
      makeActions();
      contributeToActionBars();
   }

   private void storeSelectedBranch() {
      if (xBranchSelectWidget != null && !xBranchSelectWidget.getControl().isDisposed()) {
         BranchId selectedBranch = xBranchSelectWidget.getData();
         if (selectedBranch != null) {
            try {
               UserManager.getUser().setSetting(BRANCH_KEY, selectedBranch.getId());
               UserManager.getUser().persist("Store Branch Visualization Default Branch");
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   private void addBranchSelection() {

      xBranchSelectWidget = new XBranchSelectWidget("");
      xBranchSelectWidget.setDisplayLabel(false);
      xBranchSelectWidget.createWidgets(bodyComposite, 1);
      xBranchSelectWidget.addListener(new Listener() {

         @Override
         public void handleEvent(Event event) {
            drawChart();
         }
      });

      loadLastSelectedBranch();
   }

   private void loadLastSelectedBranch() {
      try {
         String branchUuid = UserManager.getUser().getSetting(BRANCH_KEY);
         if (Strings.isValid(branchUuid)) {
            try {
               IOseeBranch branch = BranchManager.getBranchToken(Long.valueOf(branchUuid));
               xBranchSelectWidget.setSelection(branch);
            } catch (Exception ex) {
               // do nothing
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private BranchId getSelectedBranch() {
      return xBranchSelectWidget.getData();
   }

   public void drawChart() {
      if (ganttChart != null && !ganttChart.isDisposed()) {
         ganttChart.dispose();
      }
      if (bottomGroup1 != null && !bottomGroup1.isDisposed()) {
         bottomGroup1.dispose();
      }
      if (bottomGroup2 != null && !bottomGroup2.isDisposed()) {
         bottomGroup2.dispose();
      }
      if (settings == null) {
         settings = new BranchGanttSettings();
      }
      ganttChart = new GanttChart(bodyComposite, SWT.NONE, settings);
      ganttChart.setLayout(new GridLayout(1, false));
      ganttChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      try {
         createEvents(null, BranchManager.getBranchToken(getSelectedBranch()), true);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      createBottom(bodyComposite);

      bodyComposite.layout();
      parent.layout();
   }

   public GanttEvent createEvents(GanttEvent parentEvent, IOseeBranch branch, boolean recurse) {
      GanttEvent gantEvent = createGantEvent(parentEvent, branch);

      // Create connections
      if (recurse) {
         for (IOseeBranch childBranch : BranchManager.getChildBranches(branch, false)) {
            if (BranchManager.getType(childBranch).isBaselineBranch()) {
               createEvents(gantEvent, childBranch, recurse);
            }
         }
      }
      return gantEvent;
   }

   private GanttEvent createGantEvent(GanttEvent parentEvent, IOseeBranch branch) {
      Calendar creationDateCal = Calendar.getInstance();
      Date createDate = BranchManager.getBaseTransaction(branch).getTimeStamp();
      creationDateCal.setTime(createDate);

      Calendar endDate = Calendar.getInstance();
      List<TransactionRecord> transactionsForBranch = TransactionManager.getTransactionsForBranch(branch);
      Date lastTransactionDate = getLastTransactionDate(transactionsForBranch);
      endDate.setTime(lastTransactionDate);

      // Create event and link to parent, if applicable
      GanttEvent gantEvent = new GanttEvent(ganttChart, branch.getName(), creationDateCal, endDate, 35);
      if (parentEvent != null) {
         ganttChart.addConnection(parentEvent, gantEvent);
      }

      return gantEvent;
   }

   private void contributeToActionBars() {
      IActionBars bars = getViewSite().getActionBars();
      fillLocalToolBar(bars.getToolBarManager());
   }

   private void fillLocalToolBar(IToolBarManager manager) {
      manager.add(zoomInAction);
      manager.add(zoomOutAction);
      manager.add(refreshAction);
   }

   private void makeActions() {
      zoomInAction = new Action() {
         @Override
         public void run() {
            ganttChart.getGanttComposite().zoomIn();
         }
      };
      zoomInAction.setText("Zoom In");
      zoomInAction.setToolTipText("Zoom In");
      zoomInAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ZOOM_IN));

      zoomOutAction = new Action() {
         @Override
         public void run() {
            ganttChart.getGanttComposite().zoomOut();
         }
      };
      zoomOutAction.setText("Zoom Out");
      zoomOutAction.setToolTipText("Zoom Out");
      zoomOutAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ZOOM_OUT));

      refreshAction = new Action() {
         @Override
         public void run() {
            drawChart();
         }
      };
      refreshAction.setText("Refresh");
      refreshAction.setToolTipText("Refresh");
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));

   }

   private Date getLastTransactionDate(List<TransactionRecord> transactionsForBranch) {
      Date lastDate = null;
      for (TransactionRecord record : transactionsForBranch) {
         if (lastDate == null || lastDate.before(record.getTimeStamp())) {
            lastDate = record.getTimeStamp();
         }
      }
      return lastDate;
   }

   public void original(Composite parent) {
      ganttChart = new GanttChart(parent, SWT.NONE);

      // Create some calendars
      Calendar sdEventOne = Calendar.getInstance();
      Calendar edEventOne = Calendar.getInstance();
      edEventOne.add(Calendar.DATE, 10);

      Calendar sdEventTwo = Calendar.getInstance();
      Calendar edEventTwo = Calendar.getInstance();
      sdEventTwo.add(Calendar.DATE, 11);
      edEventTwo.add(Calendar.DATE, 15);

      Calendar cpDate = Calendar.getInstance();
      cpDate.add(Calendar.DATE, 16);

      // Create events
      GanttEvent eventOne = new GanttEvent(ganttChart, "Scope Event 1", sdEventOne, edEventOne, 35);
      GanttEvent eventTwo = new GanttEvent(ganttChart, "Scope Event 2", sdEventTwo, edEventTwo, 10);
      GanttEvent eventThree = new GanttEvent(ganttChart, "Checkpoint", cpDate, cpDate, 75);
      eventThree.setCheckpoint(true);

      // Create connections
      ganttChart.addConnection(eventOne, eventTwo);
      ganttChart.addConnection(eventTwo, eventThree);
      parent.layout();
   }

   @Override
   public void setFocus() {
      ganttChart.setFocus();
   }

   private void createBottom(final Composite parent) {
      bottomGroup1 = new Composite(parent, SWT.NONE);
      GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
      layoutData.widthHint = 200;
      bottomGroup1.setLayoutData(layoutData);
      bottomGroup1.setLayout(new GridLayout(8, false));

      final Button bJumpEarliest = new Button(bottomGroup1, SWT.PUSH);
      bJumpEarliest.setText("Jump to earliest event");
      final Button bJumpLatest = new Button(bottomGroup1, SWT.PUSH);
      bJumpLatest.setText("Jump to latest event");

      final Button bSelectFirstEvent = new Button(bottomGroup1, SWT.PUSH);
      bSelectFirstEvent.setText("Show first event");
      final Button bSelectMidEvent = new Button(bottomGroup1, SWT.PUSH);
      bSelectMidEvent.setText("Show middle event");
      final Button bSelectLastEvent = new Button(bottomGroup1, SWT.PUSH);
      bSelectLastEvent.setText("Show last event");

      final Button bJumpToCurrentTimeLeft = new Button(bottomGroup1, SWT.PUSH);
      bJumpToCurrentTimeLeft.setText("Today [Left]");
      final Button bJumpToCurrentTimeCenter = new Button(bottomGroup1, SWT.PUSH);
      bJumpToCurrentTimeCenter.setText("Today [Center]");
      final Button bJumpToCurrentTimeRight = new Button(bottomGroup1, SWT.PUSH);
      bJumpToCurrentTimeRight.setText("Today [Right]");

      bottomGroup2 = new Composite(parent, SWT.NONE);
      layoutData = new GridData(GridData.FILL_HORIZONTAL);
      layoutData.widthHint = 200;
      bottomGroup2.setLayoutData(layoutData);
      bottomGroup2.setLayout(new GridLayout(8, false));

      final Button zIn = new Button(bottomGroup2, SWT.PUSH);
      final Button zOut = new Button(bottomGroup2, SWT.PUSH);
      zIn.setText("Zoom In");
      zOut.setText("Zoom Out");

      final Button bShowPlanned = new Button(bottomGroup2, SWT.PUSH);
      bShowPlanned.setText("Toggle Planned Dates");

      final Button bShowDays = new Button(bottomGroup2, SWT.PUSH);
      bShowDays.setText("Toggle Dates On Events");

      final GanttComposite _ganttComposite = ganttChart.getGanttComposite();

      bJumpEarliest.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            _ganttComposite.jumpToEarliestEvent();
            moveFocus();
         }
      });

      bJumpLatest.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            _ganttComposite.jumpToLatestEvent();
            moveFocus();
         }
      });

      bSelectFirstEvent.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            if (_ganttComposite.getEvents().size() == 0) {
               return;
            }

            _ganttComposite.setTopItem((GanttEvent) _ganttComposite.getEvents().get(0), SWT.CENTER);
            moveFocus();
         }
      });

      bSelectLastEvent.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            if (_ganttComposite.getEvents().size() == 0) {
               return;
            }

            _ganttComposite.setTopItem(
               (GanttEvent) _ganttComposite.getEvents().get(_ganttComposite.getEvents().size() - 1), SWT.CENTER);
            moveFocus();
         }
      });

      bSelectMidEvent.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            if (_ganttComposite.getEvents().size() < 2) {
               return;
            }

            final GanttEvent ge = (GanttEvent) _ganttComposite.getEvents().get(_ganttComposite.getEvents().size() / 2);
            _ganttComposite.setTopItem(ge, SWT.CENTER);
            moveFocus();
         }
      });

      bJumpToCurrentTimeLeft.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            final Calendar currentDate = Calendar.getInstance();

            _ganttComposite.setDate(currentDate, SWT.LEFT);
            moveFocus();
         }
      });
      bJumpToCurrentTimeCenter.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            final Calendar currentDate = Calendar.getInstance();

            _ganttComposite.setDate(currentDate, SWT.CENTER);
            moveFocus();
         }
      });
      bJumpToCurrentTimeRight.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            final Calendar currentDate = Calendar.getInstance();

            _ganttComposite.setDate(currentDate, SWT.RIGHT);
            moveFocus();
         }
      });

      zIn.addListener(SWT.Selection, new Listener() {

         @Override
         public void handleEvent(final Event event) {
            _ganttComposite.zoomIn();
            moveFocus();
         }

      });

      zOut.addListener(SWT.Selection, new Listener() {

         @Override
         public void handleEvent(final Event event) {
            _ganttComposite.zoomOut();
            moveFocus();
         }

      });

      bShowPlanned.addListener(SWT.Selection, new Listener() {

         @Override
         public void handleEvent(final Event event) {
            _ganttComposite.setShowPlannedDates(!_ganttComposite.isShowingPlannedDates());
            moveFocus();
         }

      });

      bShowDays.addListener(SWT.Selection, new Listener() {

         @Override
         public void handleEvent(final Event event) {
            _ganttComposite.setShowDaysOnEvents(!_ganttComposite.isShowingDaysOnEvents());
            moveFocus();
         }

      });

   }

   private void moveFocus() {
      ganttChart.getGanttComposite().setFocus();
   }

}