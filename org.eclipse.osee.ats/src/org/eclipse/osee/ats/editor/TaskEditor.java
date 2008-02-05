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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class TaskEditor extends AbstractArtifactEditor implements IDirtiableEditor, IEventReceiver, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.TaskEditor";
   private int taskPageIndex;
   private SMATaskComposite taskComposite;
   private Collection<TaskArtifact> tasks = new HashSet<TaskArtifact>();

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      for (TaskArtifact taskArt : tasks)
         taskArt.saveSMA();
      onDirtied();
   }

   public static void editArtifacts(final TaskEditorInput input) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(input, EDITOR_ID);
            } catch (PartInitException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

   }

   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      super.dispose();
      for (TaskArtifact taskArt : tasks)
         if (taskArt != null && !taskArt.isDeleted() && taskArt.isSMADirty()) taskArt.revertSMA();
      SkynetEventManager.getInstance().unRegisterAll(this);
      taskComposite.dispose();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      for (TaskArtifact taskArt : tasks)
         if (taskArt.isDeleted())
            continue;
         else if (taskArt.isSMADirty()) return true;
      return false;
   }

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

      SkynetContributionItem.addTo(this, true);

      IEditorInput editorInput = getEditorInput();
      if (editorInput instanceof TaskEditorInput) {
         TaskEditorInput aei = (TaskEditorInput) editorInput;
         tasks = ((TaskEditorInput) aei).getTaskArts();
      } else
         throw new IllegalArgumentException("Editor Input not TaskEditorInput");

      setPartName(((TaskEditorInput) editorInput).getName());

      // Create Tasks tab
      taskComposite = new SMATaskComposite(getContainer(), SWT.NONE);
      taskComposite.create(this);
      taskPageIndex = addPage(taskComposite);
      setPageText(taskPageIndex, "Tasks");

      setActivePage(taskPageIndex);
   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {

         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   public void onEvent(final Event event) {
      if (getContainer() == null || getContainer().isDisposed()) return;
      onDirtied();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getCurrentStateName()
    */
   public String getCurrentStateName() {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getEditor()
    */
   public IDirtiableEditor getEditor() {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getOptions()
    */
   public List<TaskResOptionDefinition> getResOptions() throws SQLException {
      if (((TaskEditorInput) (IEditorInput) getEditorInput()).getResOptions() != null) return ((TaskEditorInput) (IEditorInput) getEditorInput()).getResOptions();
      return new ArrayList<TaskResOptionDefinition>();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getParentSmaMgr()
    */
   public SMAManager getParentSmaMgr() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTabName()
    */
   public String getTabName() {
      return "Tasks";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTaskArtifacts(java.lang.String)
    */
   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws SQLException {
      return tasks;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isTaskable()
    */
   public boolean isTaskable() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isUsingTaskResolutionOptions()
    */
   public boolean isUsingTaskResolutionOptions() {
      try {
         return getResOptions().size() > 0;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isTasksEditable()
    */
   public boolean isTasksEditable() {
      return true;
   }

   public static void loadTable(WorldSearchItem searchItem, boolean sort) {
      searchItem.setCancelled(false);
      if (!ConnectionHandler.isConnected()) {
         AWorkbench.popup("ERROR", "DB Connection Unavailable");
         return;
      }

      if (searchItem == null) return;

      searchItem.performUI(SearchType.Search);
      if (searchItem.isCancelled()) return;

      LoadTableJob job = null;
      try {
         job = new LoadTableJob(searchItem, SearchType.Search, sort);
         job.setUser(false);
         job.setPriority(Job.LONG);
         job.schedule();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Load Table Failed", ex, true);
      }
   }

   private static class LoadTableJob extends Job {

      @SuppressWarnings("unused")
      private final boolean sort;
      private final WorldSearchItem searchItem;
      private boolean cancel = false;
      private final SearchType searchType;

      public LoadTableJob(WorldSearchItem searchItem, SearchType searchType, boolean sort) {
         super("Loading \"" + searchItem.getSelectedName(searchType) + "\"...");
         this.searchItem = searchItem;
         this.searchType = searchType;
         this.sort = sort;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         cancel = false;
         searchItem.setCancelled(cancel);
         final Collection<Artifact> artifacts;
         try {
            artifacts = searchItem.performSearchGetResults(false, searchType);
            if (artifacts.size() == 0) {
               if (searchItem.isCancelled()) {
                  monitor.done();
                  return Status.CANCEL_STATUS;
               } else {
                  monitor.done();
                  return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "No Tasks Found", null);
               }
            }
            List<TaskArtifact> taskArts = new ArrayList<TaskArtifact>();
            for (Artifact artifact : artifacts)
               if (artifact instanceof TaskArtifact) taskArts.add((TaskArtifact) artifact);
            TaskEditorInput input =
                  new TaskEditorInput(
                        "Tasks for \"" + (searchItem.getSelectedName(searchType) != null ? searchItem.getSelectedName(searchType) : "") + "\"",
                        taskArts);
            TaskEditor.editArtifacts(input);

         } catch (final Exception ex) {
            monitor.done();
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Can't load tasks", ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
