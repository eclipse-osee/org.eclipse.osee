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
package org.eclipse.osee.ats.task;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.ImportTasksViaSimpleList;
import org.eclipse.osee.ats.actions.ImportTasksViaSpreadsheet;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorAction;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorSelected;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.export.AtsExportManager;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.ats.world.AtsXWidgetActionFormPage;
import org.eclipse.osee.ats.world.WorldAssigneeFilter;
import org.eclipse.osee.ats.world.WorldCompletedFilter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class TaskEditorXWidgetActionPage extends AtsXWidgetActionFormPage implements IActionable {

   private final TaskEditor taskEditor;
   private TaskComposite taskComposite;
   private static String HELP_CONTEXT_ID = "atsWorkflowEditorTaskTab";
   private final WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();
   private WorldAssigneeFilter worldAssigneeFilter = null;
   private Action filterCompletedAction, filterMyAssigneeAction, selectionMetricsAction;

   public TaskEditorXWidgetActionPage(TaskEditor taskEditor) {
      super(taskEditor, "org.eclipse.osee.ats.actionPage", "Actions");
      this.taskEditor = taskEditor;
   }

   @Override
   public Section createResultsSection(Composite body) throws OseeCoreException {
      resultsSection = toolkit.createSection(body, Section.NO_TITLE);
      resultsSection.setText("Results");
      resultsSection.setLayoutData(new GridData(GridData.FILL_BOTH));

      resultsContainer = toolkit.createClientContainer(resultsSection, 1);
      taskComposite = new TaskComposite(taskEditor, resultsContainer, SWT.BORDER, null);
      AtsPlugin.getInstance().setHelp(taskComposite, HELP_CONTEXT_ID, "org.eclipse.osee.ats.help.ui");
      return resultsSection;
   }

   public TaskComposite getTaskComposite() {
      return taskComposite;
   }

   @Override
   public void createPartControl(Composite parent) {
      super.createPartControl(parent);
      scrolledForm.setImage(ImageManager.getImage(AtsImage.TASK));

      Result result = AtsPlugin.areOSEEServicesAvailable();
      if (result.isFalse()) {
         AWorkbench.popup("ERROR", "DB Connection Unavailable");
         return;
      }
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      if (taskEditor.getTaskEditorProvider() instanceof TaskEditorParameterSearchItemProvider) {
         return (((TaskEditorParameterSearchItemProvider) taskEditor.getTaskEditorProvider()).getWorldSearchItem());
      }
      return null;
   }

   @Override
   public Result isResearchSearchValid() throws OseeCoreException {
      return taskEditor.isDirty() ? new Result("Changes un-saved. Save first.") : Result.TrueResult;
   }

   public void reSearch() throws OseeCoreException {
      Result result = isResearchSearchValid();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      taskEditor.handleRefreshAction();
   }

   @Override
   public String getXWidgetsXml() throws OseeCoreException {
      if (taskEditor.getTaskEditorProvider() instanceof TaskEditorParameterSearchItemProvider) {
         return (((TaskEditorParameterSearchItemProvider) taskEditor.getTaskEditorProvider()).getWorldSearchItem()).getParameterXWidgetXml();
      }
      return null;
   }

   @Override
   public void handleSearchButtonPressed() {
      try {
         reSearch();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public String getActionDescription() {
      return taskComposite.getActionDescription();
   }

   @Override
   protected void createToolBar(IToolBarManager toolBarManager) {
      super.createToolBar(toolBarManager);

      toolBarManager.add(taskComposite.getTaskXViewer().getCustomizeAction());
      toolBarManager.add(new Separator());
      toolBarManager.add(new OpenNewAtsTaskEditorAction(taskComposite));
      toolBarManager.add(new OpenNewAtsTaskEditorSelected(taskComposite));
      toolBarManager.add(new Separator());
      toolBarManager.add(new RefreshAction(taskComposite));
      toolBarManager.add(new Separator());
      toolBarManager.add(new NewAction());
      OseeAts.addButtonToEditorToolBar(taskEditor, taskEditor, AtsPlugin.getInstance(), toolBarManager,
            TaskEditor.EDITOR_ID, "ATS Task Tab");
      toolBarManager.add(new Separator());
      createDropDownMenuActions();
      toolBarManager.add(new DropDownAction());
   }
   public class DropDownAction extends Action implements IMenuCreator {
      private Menu fMenu;

      public DropDownAction() {
         setText("Other");
         setMenuCreator(this);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
         addKeyListener();
         addSelectionListener();
      }

      public Menu getMenu(Control parent) {
         if (fMenu != null) fMenu.dispose();

         fMenu = new Menu(parent);
         addActionToMenu(fMenu, selectionMetricsAction);
         addActionToMenu(fMenu, filterCompletedAction);
         addActionToMenu(fMenu, filterMyAssigneeAction);
         new MenuItem(fMenu, SWT.SEPARATOR);
         addActionToMenu(fMenu, new AtsExportManager(taskComposite.getTaskXViewer()));
         try {
            if (taskComposite.getIXTaskViewer().isTaskable()) {
               addActionToMenu(fMenu, new ImportTasksViaSpreadsheet(
                     (TaskableStateMachineArtifact) taskComposite.getIXTaskViewer().getParentSmaMgr().getSma(),
                     new Listener() {
                        @Override
                        public void handleEvent(Event event) {
                           try {
                              taskComposite.loadTable();
                           } catch (OseeCoreException ex) {
                              OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                           }
                        }
                     }));
               addActionToMenu(fMenu, new ImportTasksViaSimpleList(
                     (TaskableStateMachineArtifact) taskComposite.getIXTaskViewer().getParentSmaMgr().getSma(),
                     new Listener() {
                        @Override
                        public void handleEvent(Event event) {
                           try {
                              taskComposite.loadTable();
                           } catch (OseeCoreException ex) {
                              OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                           }
                        }
                     }));

            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

         return fMenu;
      }

      public void dispose() {
         if (fMenu != null) {
            fMenu.dispose();
            fMenu = null;
         }
      }

      public Menu getMenu(Menu parent) {
         return null;
      }

      protected void addActionToMenu(Menu parent, Action action) {
         ActionContributionItem item = new ActionContributionItem(action);
         item.fill(parent, -1);
      }

      public void run() {

      }

      void clear() {
         dispose();
      }

      private void addKeyListener() {
         taskComposite.getTaskXViewer().getTree().addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent event) {
            }

            public void keyReleased(KeyEvent event) {
               if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
                  if (event.keyCode == 'a') {
                     taskComposite.getTaskXViewer().getTree().setSelection(
                           taskComposite.getTaskXViewer().getTree().getItems());
                  } else if (event.keyCode == 'x') {
                     selectionMetricsAction.setChecked(!selectionMetricsAction.isChecked());
                     selectionMetricsAction.run();
                  } else if (event.keyCode == 'f') {
                     filterCompletedAction.setChecked(!filterCompletedAction.isChecked());
                     filterCompletedAction.run();
                  } else if (event.keyCode == 'g') {
                     filterMyAssigneeAction.setChecked(!filterMyAssigneeAction.isChecked());
                     filterMyAssigneeAction.run();
                  } else if (event.keyCode == 'd') {
                     filterMyAssigneeAction.setChecked(!filterMyAssigneeAction.isChecked());
                     filterCompletedAction.setChecked(!filterCompletedAction.isChecked());
                     filterCompletedAction.run();
                     filterMyAssigneeAction.run();
                  }
               }
            }
         });
      }

      private void addSelectionListener() {
         taskComposite.getTaskXViewer().getTree().addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
               if (selectionMetricsAction != null) {
                  if (selectionMetricsAction.isChecked()) {
                     selectionMetricsAction.run();
                  } else {
                     if (taskComposite != null) {
                        taskComposite.showReleaseMetricsLabel.setText("");
                     }
                  }
               }
            }
         });
      }
   }

   public void updateExtraInfoLine() throws OseeCoreException {
      if (selectionMetricsAction != null && selectionMetricsAction.isChecked()) {
         if (taskComposite.getTaskXViewer() != null && taskComposite.getTaskXViewer().getSelectedSMAArtifacts() != null && !taskComposite.getTaskXViewer().getSelectedSMAArtifacts().isEmpty()) {
            taskComposite.showReleaseMetricsLabel.setText(SMAMetrics.getEstRemainMetrics(
                  taskComposite.getTaskXViewer().getSelectedSMAArtifacts(),
                  null,
                  taskComposite.getTaskXViewer().getSelectedSMAArtifacts().iterator().next().getManHrsPerDayPreference(),
                  null));
         } else
            taskComposite.showReleaseMetricsLabel.setText("");
      }
      taskComposite.showReleaseMetricsLabel.getParent().layout();
   }

   public void updateExtendedStatusString() {
      taskComposite.getTaskXViewer().setExtendedStatusString(
      //
            (filterCompletedAction.isChecked() ? "[Complete/Cancel Filter]" : "") +
            //
            (filterMyAssigneeAction.isChecked() ? "[My Assignee Filter]" : ""));
   }

   protected void createDropDownMenuActions() {
      try {
         worldAssigneeFilter = new WorldAssigneeFilter();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      selectionMetricsAction = new Action("Show Release Metrics by Selection - Ctrl-X", Action.AS_CHECK_BOX) {
         @Override
         public void run() {
            try {
               updateExtraInfoLine();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      selectionMetricsAction.setToolTipText("Show Release Metrics by Selection - Ctrl-X");
      selectionMetricsAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.PAGE));

      filterCompletedAction = new Action("Filter Out Completed/Cancelled - Ctrl-F", Action.AS_CHECK_BOX) {
         @Override
         public void run() {
            if (filterCompletedAction.isChecked()) {
               taskComposite.getTaskXViewer().addFilter(worldCompletedFilter);
            } else {
               taskComposite.getTaskXViewer().removeFilter(worldCompletedFilter);
            }
            updateExtendedStatusString();
            taskComposite.getTaskXViewer().refresh();
         }
      };
      filterCompletedAction.setToolTipText("Filter Out Completed/Cancelled - Ctrl-F");
      filterCompletedAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GREEN_PLUS));

      filterMyAssigneeAction = new Action("Filter My Assignee - Ctrl-G", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (filterMyAssigneeAction.isChecked()) {
               taskComposite.getTaskXViewer().addFilter(worldAssigneeFilter);
            } else {
               taskComposite.getTaskXViewer().removeFilter(worldAssigneeFilter);
            }
            updateExtendedStatusString();
            taskComposite.getTaskXViewer().refresh();
         }
      };
      filterMyAssigneeAction.setToolTipText("Filter My Assignee - Ctrl-G");
      filterMyAssigneeAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.USER));
   }

}
