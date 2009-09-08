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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.AtsMetricsComposite;
import org.eclipse.osee.ats.world.IAtsMetricsProvider;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItemProvider;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class TaskEditor extends AbstractArtifactEditor implements IDirtiableEditor, IActionable, IAtsMetricsProvider, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.TaskEditor";
   private int mainPageIndex, metricsPageIndex;
   private TaskEditorXWidgetActionPage taskActionPage;
   private final Collection<TaskArtifact> tasks = new HashSet<TaskArtifact>();
   private AtsMetricsComposite metricsComposite;

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
         for (TaskArtifact taskArt : tasks) {
            taskArt.saveSMA(transaction);
         }
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      onDirtied();
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      return taskActionPage.getTaskComposite().getTaskXViewer().getLoadedArtifacts();
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   public static Collection<TaskEditor> getEditors() {
      final List<TaskEditor> editors = new ArrayList<TaskEditor>();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((TaskEditor) editor.getEditor(false));
            }
         }
      }, true);
      return editors;
   }

   public static void closeAll() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               AWorkbench.getActivePage().closeEditor((editor.getEditor(false)), false);
            }
         }
      });
   }

   public void setTableTitle(final String title, final boolean warning) {
      taskActionPage.setTableTitle(title, warning);
   }

   @Override
   public void dispose() {
      for (TaskArtifact taskArt : tasks)
         if (taskArt != null && !taskArt.isDeleted() && taskArt.isSMAEditorDirty().isTrue()) taskArt.revertSMA();
      if (taskActionPage.getTaskComposite() != null) taskActionPage.getTaskComposite().dispose();
      if (metricsComposite != null) metricsComposite.disposeComposite();

      super.dispose();
   }

   @Override
   public boolean isDirty() {
      for (TaskArtifact taskArt : tasks) {
         if (taskArt.isDeleted())
            continue;
         else if (taskArt.isSMAEditorDirty().isTrue()) return true;
      }
      return false;
   }

   @Override
   public String toString() {
      return "TaskEditor";
   }

   /**
    * @return the taskActionPage
    */
   public TaskEditorXWidgetActionPage getTaskActionPage() {
      return taskActionPage;
   }

   @Override
   protected void addPages() {

      try {
         OseeContributionItem.addTo(this, true);

         IEditorInput editorInput = getEditorInput();
         if (!(editorInput instanceof TaskEditorInput)) {
            throw new IllegalArgumentException("Editor Input not TaskEditorInput");
         }

         createMainTab();
         createMetricsTab();

         setActivePage(mainPageIndex);
         loadTable();

      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      } catch (PartInitException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createMainTab() throws OseeCoreException, PartInitException {
      taskActionPage = new TaskEditorXWidgetActionPage(this);
      mainPageIndex = addPage(taskActionPage);
   }

   private void createMetricsTab() throws OseeCoreException {
      Composite comp = AtsUtil.createCommonPageComposite(getContainer());
      AtsUtil.createCommonToolBar(comp);
      metricsComposite = new AtsMetricsComposite(this, comp, SWT.NONE);
      metricsPageIndex = addPage(comp);
      setPageText(metricsPageIndex, "Metrics");
   }

   public ITaskEditorProvider getTaskEditorProvider() {
      TaskEditorInput aei = (TaskEditorInput) getEditorInput();
      return aei.getItaskEditorProvider();
   }

   private void loadTable() throws OseeCoreException {
      ITaskEditorProvider provider = getTaskEditorProvider();
      setPartName(provider.getTaskEditorLabel(SearchType.Search));

      if (provider instanceof TaskEditorParameterSearchItemProvider && ((TaskEditorParameterSearchItemProvider) provider).isFirstTime()) {
         setPartName(provider.getName());
         setTableTitle(WorldEditorParameterSearchItemProvider.ENTER_OPTIONS_AND_SELECT_SEARCH, false);
         return;
      }
      if (provider instanceof TaskEditorParameterSearchItemProvider) {
         Result result =
               ((TaskEditorParameterSearchItemProvider) provider).getWorldSearchItem().isParameterSelectionValid();
         if (result.isFalse()) {
            result.popup();
            return;
         }
      }
      LoadTableJob job = null;
      job = new LoadTableJob(provider, SearchType.ReSearch, this);
      job.setUser(false);
      job.setPriority(Job.LONG);
      job.schedule();
      if (provider.getTableLoadOptions() != null && provider.getTableLoadOptions().contains(TableLoadOption.ForcePend)) {
         try {
            job.join();
         } catch (InterruptedException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   public static void open(final ITaskEditorProvider provider) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new TaskEditorInput(provider), EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }, (provider.getTableLoadOptions() != null && provider.getTableLoadOptions().contains(TableLoadOption.ForcePend)));
   }

   private static class LoadTableJob extends Job {

      private final ITaskEditorProvider itaskEditorProvider;
      private final TaskEditor taskEditor;
      private final SearchType searchType;

      public LoadTableJob(ITaskEditorProvider itaskEditorProvider, SearchType searchType, TaskEditor taskEditor) throws OseeCoreException {
         super("Loading \"" + itaskEditorProvider.getTaskEditorLabel(searchType) + "\"...");
         this.searchType = searchType;
         this.taskEditor = taskEditor;
         taskEditor.setPartName(itaskEditorProvider.getTaskEditorLabel(searchType));
         taskEditor.setTableTitle("Loading \"" + itaskEditorProvider.getTaskEditorLabel(searchType) + "\"...", false);
         this.itaskEditorProvider = itaskEditorProvider;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            final List<TaskArtifact> taskArts = new ArrayList<TaskArtifact>();
            for (Artifact artifact : itaskEditorProvider.getTaskEditorTaskArtifacts()) {
               if (artifact instanceof TaskArtifact) {
                  taskArts.add((TaskArtifact) artifact);
               }
            }
            taskEditor.tasks.clear();
            taskEditor.tasks.addAll(taskArts);
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  try {
                     taskEditor.setPartName(itaskEditorProvider.getTaskEditorLabel(searchType));
                     if (taskArts.size() == 0) {
                        taskEditor.setTableTitle(
                              "No Results Found - " + itaskEditorProvider.getTaskEditorLabel(searchType), true);
                     } else {
                        taskEditor.setTableTitle(itaskEditorProvider.getTaskEditorLabel(searchType), false);
                     }
                     taskEditor.getTaskActionPage().getTaskComposite().loadTable();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  }
               }
            }, true);
         } catch (final Exception ex) {
            monitor.done();
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Can't load tasks", ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() throws OseeCoreException {
      return tasks;
   }

   @Override
   public VersionArtifact getMetricsVersionArtifact() throws OseeCoreException {
      for (TaskArtifact taskArt : tasks) {
         if (taskArt.getWorldViewTargetedVersion() != null) {
            return taskArt.getWorldViewTargetedVersion();
         }
      }
      return null;
   }

   public String getCurrentStateName() throws OseeCoreException {
      return "";
   }

   public IDirtiableEditor getEditor() throws OseeCoreException {
      return this;
   }

   public SMAManager getParentSmaMgr() throws OseeCoreException {
      return null;
   }

   public String getTabName() throws OseeCoreException {
      return "Tasks";
   }

   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException {
      return tasks;
   }

   public boolean isTaskable() throws OseeCoreException {
      return false;
   }

   public boolean isTasksEditable() throws OseeCoreException {
      return true;
   }

   @Override
   public boolean isRefreshActionHandled() throws OseeCoreException {
      return true;
   }

   @Override
   public void handleRefreshAction() throws OseeCoreException {
      loadTable();
   }

   @Override
   public double getManHoursPerDayPreference() throws OseeCoreException {
      if (tasks.size() == 0) return StateMachineArtifact.DEFAULT_MAN_HOURS_PER_DAY;
      return tasks.iterator().next().getManHrsPerDayPreference();
   }

   @Override
   public String getActionDescription() {
      return taskActionPage.getActionDescription();
   }

   @Override
   public IActionable getActionable() throws OseeCoreException {
      return this;
   }

}
