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

package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.DeleteTasksAction;
import org.eclipse.osee.ats.actions.DeleteTasksAction.TaskArtifactProvider;
import org.eclipse.osee.ats.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.actions.ImportTasksViaSimpleList;
import org.eclipse.osee.ats.actions.ImportTasksViaSpreadsheet;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorAction;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorSelected;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelectedAction;
import org.eclipse.osee.ats.actions.TaskAddAction;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.export.AtsExportAction;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtilClient;
import org.eclipse.osee.ats.util.IAtsClient;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.workflow.task.IXTaskViewer;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.workflow.task.TaskComposite;
import org.eclipse.osee.ats.workflow.task.TaskXViewerFactory;
import org.eclipse.osee.ats.workflow.task.internal.AtsTaskCache;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.IMenuActionProvider;
import org.eclipse.osee.ats.world.IWorldEditor;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.IWorldViewerEventHandler;
import org.eclipse.osee.ats.world.WorldAssigneeFilter;
import org.eclipse.osee.ats.world.WorldCompletedFilter;
import org.eclipse.osee.ats.world.WorldComposite;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.WorldXViewerEventManager;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class WfeTasksTab extends FormPage implements IArtifactEventListener, IWorldEditor, ISelectedAtsArtifacts, IWorldViewerEventHandler, IMenuActionProvider, IXTaskViewer, IOseeTreeReportProvider {
   private IManagedForm managedForm;
   private Composite bodyComp;
   private ScrolledForm scrolledForm;
   private TaskComposite taskComposite;
   private LoadingComposite loadingComposite;
   public final static String ID = "ats.tasks.tab";
   private final WorkflowEditor editor;
   private static Map<Long, Integer> idToScrollLocation = new HashMap<>();
   private final ReloadJobChangeAdapter reloadAdapter;
   private final IAtsClient client;
   private final IAtsTeamWorkflow teamWf;
   private final TeamWorkFlowArtifact teamArt;
   private final WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();
   private WorldAssigneeFilter worldAssigneeFilter = null;
   private Action filterCompletedAction, filterMyAssigneeAction;

   public WfeTasksTab(WorkflowEditor editor, IAtsTeamWorkflow teamWf, IAtsClient client) {
      super(editor, ID, "Tasks");
      this.editor = editor;
      this.teamWf = teamWf;
      this.client = client;
      reloadAdapter = new ReloadJobChangeAdapter(editor);
      teamArt = (TeamWorkFlowArtifact) teamWf.getStoreObject();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      scrolledForm = managedForm.getForm();
      final WfeTasksTab listener = this;
      try {
         scrolledForm.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
               storeScrollLocation();
               OseeEventManager.removeListener(listener);
            }
         });
         bodyComp = scrolledForm.getBody();
         GridLayout gridLayout = new GridLayout(1, true);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, false, false);
         bodyComp.setLayoutData(gd);

         setLoading(true);
         refreshData();
         WorldXViewerEventManager.add(this);

         scrolledForm.setText("Team Workflow Tasks");
         scrolledForm.setImage(ImageManager.getImage(AtsImage.PEER_REVIEW));

         managedForm.reflow(true);
      } catch (Exception ex) {
         handleException(ex);
      }
      OseeEventManager.addListener(this);
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (getManagedForm() != null && Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   public void refreshData() {
      if (Widgets.isAccessible(bodyComp)) {
         List<IOperation> ops = AtsBulkLoad.getConfigLoadingOperations();
         IOperation operation = Operations.createBuilder("Load Tasks Tab").addAll(ops).build();
         Operations.executeAsJob(operation, false, Job.LONG, reloadAdapter);
      }
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final WorkflowEditor editor;
      boolean firstTime = true;

      private ReloadJobChangeAdapter(WorkflowEditor editor) {
         this.editor = editor;
         showBusy(true);
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         Job job = new UIJob("Draw Tasks Tab") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               if (firstTime) {
                  try {
                     if (Widgets.isAccessible(scrolledForm)) {
                        setLoading(false);
                        boolean createdAndLoaded = createMembersBody();
                        if (!createdAndLoaded) {
                           reload();
                        }
                        refreshTabName();
                        jumptoScrollLocation();
                        FormsUtil.addHeadingGradient(editor.getToolkit(), scrolledForm, true);
                        refreshToolbar();
                        editor.onDirtied();
                     }
                     firstTime = false;
                  } catch (OseeCoreException ex) {
                     handleException(ex);
                  } finally {
                     showBusy(false);
                  }
               } else {
                  showBusy(false);
                  if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
                     refresh();
                  }
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   private void handleException(Exception ex) {
      setLoading(false);
      if (Widgets.isAccessible(taskComposite)) {
         taskComposite.dispose();
      }
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   private void setLoading(boolean set) {
      if (set) {
         loadingComposite = new LoadingComposite(bodyComp);
         bodyComp.layout();
      } else {
         if (Widgets.isAccessible(loadingComposite)) {
            loadingComposite.dispose();
         }
      }
      showBusy(set);
   }

   /**
    * @return true if created; false if skipped
    */
   private boolean createMembersBody() {
      if (!Widgets.isAccessible(taskComposite)) {
         taskComposite = new TaskComposite(this, this, new TaskXViewerFactory(this), bodyComp, SWT.BORDER, editor,
            teamWf.isInWork(), teamWf);
         taskComposite.getWorldXViewer().addMenuActionProvider(this);
         getSite().setSelectionProvider(taskComposite.getWorldXViewer());
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 100;
         gd.heightHint = 100;
         taskComposite.setLayoutData(gd);

         getSite().setSelectionProvider(taskComposite.getWorldXViewer());

         reload();
         return true;
      }
      return false;
   }

   public void reload() {
      if (isTableDisposed()) {
         return;
      }
      String getLoadingString = String.format("Loading Tasks for %s", editor.getAwa());
      Job job = new Job(getLoadingString) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            if (isTableDisposed()) {
               return Status.OK_STATUS;
            }
            try {
               Collection<TaskArtifact> taskArts = getTaskArtifacts();
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     if (isTableDisposed()) {
                        return;
                     }
                     taskComposite.load("Tasks", taskArts, (CustomizeData) null, TableLoadOption.None);
                     refreshTabName();
                  }

               });
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                  String.format("Exception loading tasks for %s", teamWf.toStringWithId()), ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, false);
   }

   private boolean isTableDisposed() {
      return taskComposite == null || taskComposite.getXViewer() == null || taskComposite.getXViewer().getTree() == null || taskComposite.getXViewer().getTree().isDisposed();
   }

   private void jumptoScrollLocation() {
      // Jump to scroll location if set
      Integer selection = idToScrollLocation.get(teamWf.getId());
      if (selection != null) {
         JumpScrollbarJob job = new JumpScrollbarJob("");
         job.schedule(500);
      }
   }

   @Override
   public void dispose() {
      if (taskComposite != null) {
         taskComposite.dispose();
      }
      if (editor.getToolkit() != null) {
         editor.getToolkit().dispose();
      }
   }

   private final Control control = null;

   private void storeScrollLocation() {
      if (managedForm != null && managedForm.getForm() != null) {
         Integer selection = managedForm.getForm().getVerticalBar().getSelection();
         idToScrollLocation.put(teamWf.getId(), selection);
      }
   }

   private class JumpScrollbarJob extends Job {
      public JumpScrollbarJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               Integer selection = idToScrollLocation.get(teamWf.getId());

               // Find the ScrolledComposite operating on the control.
               ScrolledComposite sComp = null;
               if (control == null || control.isDisposed()) {
                  return;
               }
               Composite parent = control.getParent();
               while (parent != null) {
                  if (parent instanceof ScrolledComposite) {
                     sComp = (ScrolledComposite) parent;
                     break;
                  }
                  parent = parent.getParent();
               }

               if (sComp != null) {
                  sComp.setOrigin(0, selection);
               }
            }
         });
         return Status.OK_STATUS;

      }
   }

   public void refresh() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (Widgets.isAccessible(taskComposite)) {
               taskComposite.getXViewer().setInput(getTaskArtifacts());
            }
         }
      });
   }

   private void refreshToolbar() {
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      toolBarMgr.removeAll();
      try {
         if (taskComposite.getIXTaskViewer().isTasksEditable()) {
            toolBarMgr.add(new TaskAddAction(taskComposite));
            TaskArtifactProvider taskProvider = new TaskArtifactProvider() {

               @Override
               public List<TaskArtifact> getSelectedArtifacts() {
                  return taskComposite.getSelectedTaskArtifacts();
               }
            };
            toolBarMgr.add(new DeleteTasksAction(taskProvider));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      toolBarMgr.add(new Separator());

      toolBarMgr.add(getWorldXViewer().getCustomizeAction());
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new OpenNewAtsTaskEditorAction(taskComposite));
      toolBarMgr.add(new OpenNewAtsTaskEditorSelected(taskComposite));
      toolBarMgr.add(new OpenNewAtsWorldEditorSelectedAction(taskComposite));
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new RefreshAction(taskComposite));
      toolBarMgr.add(new NewAction());
      toolBarMgr.add(new Separator());
      createDropDownMenuActions();
      toolBarMgr.add(new DropDownAction());
      scrolledForm.updateToolBar();
   }

   protected void createDropDownMenuActions() {
      try {
         worldAssigneeFilter = new WorldAssigneeFilter();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      filterCompletedAction = new Action("Filter Out Completed/Cancelled - Ctrl-F", IAction.AS_CHECK_BOX) {

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

      filterMyAssigneeAction = new Action("Filter My Assignee - Ctrl-G", IAction.AS_CHECK_BOX) {

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

   public void updateExtendedStatusString() {
      taskComposite.getTaskXViewer().setExtendedStatusString(
         //
         (filterCompletedAction.isChecked() ? "[Complete/Cancel Filter]" : "") +
         //
            (filterMyAssigneeAction.isChecked() ? "[My Assignee Filter]" : ""));
   }

   public class DropDownAction extends Action implements IMenuCreator {
      private Menu fMenu;

      public DropDownAction() {
         setText("Other");
         setMenuCreator(this);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
         addKeyListener();
      }

      @Override
      public Menu getMenu(Control parent) {
         if (fMenu != null) {
            fMenu.dispose();
         }

         fMenu = new Menu(parent);

         addActionToMenu(fMenu, filterCompletedAction);
         addActionToMenu(fMenu, filterMyAssigneeAction);
         new MenuItem(fMenu, SWT.SEPARATOR);
         addActionToMenu(fMenu, new AtsExportAction(taskComposite.getTaskXViewer()));
         try {
            if (taskComposite.getIXTaskViewer().isTasksEditable()) {
               addActionToMenu(fMenu, new ImportTasksViaSpreadsheet(taskComposite.getTeamArt(), null));
               addActionToMenu(fMenu, new ImportTasksViaSimpleList(taskComposite.getTeamArt(), null));

            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }

         return fMenu;
      }

      @Override
      public void dispose() {
         if (fMenu != null) {
            fMenu.dispose();
            fMenu = null;
         }
      }

      @Override
      public Menu getMenu(Menu parent) {
         return null;
      }

      protected void addActionToMenu(Menu parent, Action action) {
         ActionContributionItem item = new ActionContributionItem(action);
         item.fill(parent, -1);
      }

      void clear() {
         dispose();
      }

      private void addKeyListener() {
         taskComposite.getTaskXViewer().getTree().addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent event) {
               // do nothing
            }

            @Override
            public void keyReleased(KeyEvent event) {
               if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
                  if (event.keyCode == 'a') {
                     taskComposite.getTaskXViewer().getTree().setSelection(
                        taskComposite.getTaskXViewer().getTree().getItems());
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
   }

   public WorldComposite getMembersSection() {
      return taskComposite;
   }

   @Override
   public WorldXViewer getWorldXViewer() {
      if (taskComposite == null) {
         return null;
      }
      return taskComposite.getWorldXViewer();
   }

   @Override
   public void relationsModifed(Collection<Artifact> relModifiedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered) {
      if (relModifiedArts.contains(teamArt)) {
         refresh();
      }
   }

   @Override
   public boolean isDisposed() {
      return editor.isDisposed();
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = taskComposite.getXViewer().getMenuManager();
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, new Separator());
   }

   @Override
   public Set<Artifact> getSelectedWorkflowArtifacts() {
      Set<Artifact> artifacts = new HashSet<>();
      for (Artifact art : taskComposite.getSelectedArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      List<Artifact> artifacts = new ArrayList<>();
      for (Artifact art : taskComposite.getSelectedArtifacts()) {
         if (art.isOfType(AtsArtifactTypes.AtsArtifact)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<TaskArtifact> getSelectedTaskArtifacts() {
      List<TaskArtifact> tasks = new ArrayList<>();
      for (Artifact art : taskComposite.getSelectedArtifacts()) {
         if (art instanceof TaskArtifact) {
            tasks.add((TaskArtifact) art);
         }
      }
      return tasks;
   }

   @Override
   public void reflow() {
      // do nothing
   }

   @Override
   public void setTableTitle(String title, boolean warning) {
      // do nothing
   }

   @Override
   public void reSearch() {
      JobChangeAdapter listener = new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            super.done(event);
            reload();
         }

      };
      deCacheAndReload(listener);
   }

   private void deCacheAndReload(JobChangeAdapter listener) {
      ArtifactQuery.reloadArtifacts(getTaskArtifacts());
      AtsTaskCache.decache(teamArt);
      ArtifactQuery.reloadArtifacts(Collections.singleton(teamArt));
   }

   @Override
   public IWorldEditorProvider getWorldEditorProvider() {
      return null;
   }

   @Override
   public void createToolBarPulldown(Menu menu) {
      // do nothing
   }

   @Override
   public String getCurrentTitleLabel() {
      return null;
   }

   private Collection<IAtsTask> getTasks() {
      return client.getTaskService().getTasks(teamWf);
   }

   public Collection<TaskArtifact> getTaskArtifacts() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
         AtsObjects.getArtifacts(client.getTaskService().getTasks(teamWf)));
   }

   public void refreshTabName() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            String tabName = "Tasks";
            try {
               tabName = String.format("Tasks (%d)", getTasks().size());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            // Because of reload page, tasks tab moves around; find real page/index
            int index = 0;
            for (Object obj : editor.getPages()) {
               if (obj instanceof WfeTasksTab) {
                  break;
               }
               index++;
            }
            editor.setTabName(index, tabName);
         }
      });
   }

   public TaskComposite getTaskComposite() {
      return taskComposite;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf() {
      return teamArt;
   }

   @Override
   public boolean isTasksEditable() {
      return editor.isTasksEditable();
   }

   @Override
   public String getEditorTitle() {
      try {
         return String.format("Table Report - Tasks for [%s]", getTeamWf());
      } catch (Exception ex) {
         // do nothing
      }
      return "Table Report - Tasks";
   }

   @Override
   public String getReportTitle() {
      return getEditorTitle();
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return AtsUtilClient.getAtsObjectEventFilters();
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      refreshTabName();
   }

}
