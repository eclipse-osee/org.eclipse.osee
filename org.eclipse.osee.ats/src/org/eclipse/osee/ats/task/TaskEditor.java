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
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMATaskComposite;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.world.AtsMetricsComposite;
import org.eclipse.osee.ats.world.IAtsMetricsProvider;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class TaskEditor extends AbstractArtifactEditor implements IDirtiableEditor, IAtsMetricsProvider, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.TaskEditor";
   private int taskPageIndex, metricsPageIndex;
   private SMATaskComposite taskComposite;
   private final Collection<TaskArtifact> tasks = new HashSet<TaskArtifact>();
   private AtsMetricsComposite metricsComposite;
   private Label warningLabel, searchNameLabel;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         for (TaskArtifact taskArt : tasks) {
            taskArt.saveSMA(transaction);
         }
         transaction.execute();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      onDirtied();
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      return taskComposite.getTaskComposite().getTaskXViewer().getLoadedArtifacts();
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   public void setTableTitle(final String title, final boolean warning) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (warning)
               warningLabel.setImage(AtsPlugin.getInstance().getImage("warn.gif"));
            else
               warningLabel.setImage(null);
            searchNameLabel.setText(title);
            searchNameLabel.getParent().layout();
         };
      });
   }

   @Override
   public void dispose() {
      for (TaskArtifact taskArt : tasks)
         if (taskArt != null && !taskArt.isDeleted() && taskArt.isSMAEditorDirty().isTrue()) taskArt.revertSMA();
      if (taskComposite != null) taskComposite.disposeTaskComposite();
      if (metricsComposite != null) metricsComposite.disposeComposite();

      super.dispose();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {

      try {
         OseeContributionItem.addTo(this, true);

         IEditorInput editorInput = getEditorInput();
         if (!(editorInput instanceof TaskEditorInput)) {
            throw new IllegalArgumentException("Editor Input not TaskEditorInput");
         }

         createTaskTab();

         metricsComposite = new AtsMetricsComposite(this, getContainer(), SWT.NONE);
         metricsPageIndex = addPage(metricsComposite);
         setPageText(metricsPageIndex, "Metrics");

         setActivePage(taskPageIndex);
         loadTable();

      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createTaskTab() throws OseeCoreException {
      // Create Tasks tab
      Composite comp = AtsLib.createCommonPageComposite(getContainer());
      ToolBar toolBar = AtsLib.createCommonToolBar(comp);

      Composite headerComp = new Composite(comp, SWT.NONE);
      headerComp.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      headerComp.setLayoutData(gd);

      warningLabel = new Label(headerComp, SWT.NONE);
      searchNameLabel = new Label(headerComp, SWT.NONE);

      taskComposite = new SMATaskComposite(this, comp, SWT.NONE, toolBar);
      taskPageIndex = addPage(comp);
      setPageText(taskPageIndex, "Tasks");
   }

   private void loadTable() throws OseeCoreException {
      TaskEditorInput aei = (TaskEditorInput) getEditorInput();
      ITaskEditorProvider provider = aei.getItaskEditorProvider();
      setPartName(provider.getTaskEditorLabel(SearchType.Search));

      LoadTableJob job = null;
      job = new LoadTableJob(provider, SearchType.ReSearch, this);
      job.setUser(false);
      job.setPriority(Job.LONG);
      job.schedule();
      if (provider.getTableLoadOptions().contains(TableLoadOption.ForcePend)) {
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
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new TaskEditorInput(provider), EDITOR_ID);
            } catch (PartInitException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      }, provider.getTableLoadOptions().contains(TableLoadOption.ForcePend));
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

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
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
               /* (non-Javadoc)
                * @see java.lang.Runnable#run()
                */
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
                     taskEditor.taskComposite.getTaskComposite().loadTable();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  }
               }
            });
         } catch (final Exception ex) {
            monitor.done();
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Can't load tasks", ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getMetricsArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() {
      return tasks;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getMetricsVersionArtifact()
    */
   @Override
   public VersionArtifact getMetricsVersionArtifact() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getCurrentStateName()
    */
   public String getCurrentStateName() throws OseeCoreException {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getEditor()
    */
   public IDirtiableEditor getEditor() throws OseeCoreException {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getParentSmaMgr()
    */
   public SMAManager getParentSmaMgr() throws OseeCoreException {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTabName()
    */
   public String getTabName() throws OseeCoreException {
      return "Tasks";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTaskArtifacts(java.lang.String)
    */
   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException {
      return tasks;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isTaskable()
    */
   public boolean isTaskable() throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isTasksEditable()
    */
   public boolean isTasksEditable() throws OseeCoreException {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isRefreshHandled()
    */
   @Override
   public boolean isRefreshActionHandled() throws OseeCoreException {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#refresh()
    */
   @Override
   public void handleRefreshAction() throws OseeCoreException {
      loadTable();
   }

}
