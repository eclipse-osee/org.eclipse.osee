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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorAction.IOpenNewAtsTaskEditorHandler;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorSelected.IOpenNewAtsTaskEditorSelectedHandler;
import org.eclipse.osee.ats.actions.TaskAddAction.ITaskAddActionHandler;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.column.RelatedToStateColumn;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.TaskManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsTaskCache;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.IWorldViewerEventHandler;
import org.eclipse.osee.ats.world.WorldContentProvider;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.WorldXViewerEventManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryComboDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 */
public class TaskComposite extends Composite implements IWorldViewerEventHandler, IOpenNewAtsTaskEditorSelectedHandler, ITaskAddActionHandler, IOpenNewAtsTaskEditorHandler, IRefreshActionHandler {

   private TaskXViewer taskXViewer;
   private final IXTaskViewer iXTaskViewer;
   protected Label showReleaseMetricsLabel;
   private final Set<TaskArtifact> taskArts = new HashSet<>(200);

   public TaskComposite(IXTaskViewer iXTaskViewer, Composite parent, int style) {
      super(parent, style);
      this.iXTaskViewer = iXTaskViewer;

      setLayout(ALayout.getZeroMarginLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      try {
         if (DbConnectionExceptionComposite.dbConnectionIsOk(this)) {

            showReleaseMetricsLabel = new Label(this, SWT.NONE);
            showReleaseMetricsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            taskXViewer = new TaskXViewer(this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, iXTaskViewer.getEditor());
            taskXViewer.setTasksEditable(iXTaskViewer.isTasksEditable());
            taskXViewer.setNewTaskSelectionEnabled(iXTaskViewer.isTasksEditable());
            taskXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

            taskXViewer.setContentProvider(new WorldContentProvider(taskXViewer));
            taskXViewer.setLabelProvider(new WorldLabelProvider(taskXViewer));

            Tree tree = taskXViewer.getTree();
            GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
            gridData.heightHint = 100;
            gridData.widthHint = 100;
            tree.setLayoutData(gridData);
            tree.setHeaderVisible(true);
            tree.setLinesVisible(true);

            setupDragAndDropSupport();
            parent.layout();

            WorldXViewerEventManager.add(this);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void disposeComposite() {
      if (taskXViewer != null && !taskXViewer.getTree().isDisposed()) {
         taskXViewer.dispose();
      }
      WorldXViewerEventManager.remove(this);
   }

   public IXTaskViewer getIXTaskViewer() {
      return iXTaskViewer;
   }

   private void add(Collection<TaskArtifact> newTasks) {
      this.taskArts.addAll(newTasks);
      if (Widgets.isAccessible(getTaskXViewer().getControl())) {
         if (getTaskXViewer().getInput() != this.taskArts) {
            getTaskXViewer().setInputXViewer(this.taskArts);
         }
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               taskXViewer.refresh();
            }
         });
      }
   }

   public void loadTable() throws OseeCoreException {
      this.taskArts.clear();
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            add(iXTaskViewer.getTaskArtifacts());
         }
      });
   }

   public TaskArtifact handleNewTask() {
      TaskArtifact taskArt = null;
      try {
         EntryComboDialog ed =
            new EntryComboDialog("Create New Task", "Enter Task Title", RelatedToStateColumn.RELATED_TO_STATE_SELECTION);
         List<String> validStates =
            RelatedToStateColumn.getValidInWorkStates((TeamWorkFlowArtifact) iXTaskViewer.getAwa());
         ed.setOptions(validStates);
         if (ed.open() == 0) {
            AtsChangeSet changes = new AtsChangeSet("Create New Task");
            taskArt =
               ((AbstractTaskableArtifact) iXTaskViewer.getAwa()).createNewTask(ed.getEntry(), new Date(),
                  AtsClientService.get().getUserService().getCurrentUser(), ed.getSelection(), changes);
            changes.execute();
            AtsTaskCache.decache((AbstractTaskableArtifact) iXTaskViewer.getAwa());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return taskArt;
   }

   public List<TaskArtifact> getSelectedTaskArtifactItems() {
      Iterator<?> i = ((IStructuredSelection) taskXViewer.getSelection()).iterator();
      ArrayList<TaskArtifact> items = new ArrayList<>();
      while (i.hasNext()) {
         Object obj = i.next();
         if (Artifacts.isOfType(obj, AtsArtifactTypes.Task)) {
            items.add((TaskArtifact) obj);
         }
      }
      return items;
   }

   public TaskXViewer getTaskXViewer() {
      return taskXViewer;
   }

   @Override
   public Object getData() {
      return taskXViewer.getInput();
   }

   private void setupDragAndDropSupport() {
      DragSource source = new DragSource(taskXViewer.getTree(), DND.DROP_COPY);
      source.setTransfer(new Transfer[] {ArtifactTransfer.getInstance()});
      source.addDragListener(new DragSourceListener() {

         @Override
         public void dragFinished(DragSourceEvent event) {
            // do nothing
         }

         @Override
         public void dragSetData(DragSourceEvent event) {
            Collection<TaskArtifact> arts = taskXViewer.getSelectedTaskArtifacts();
            if (arts.size() > 0) {
               event.data = new ArtifactData(arts.toArray(new Artifact[arts.size()]), "", SMAEditor.EDITOR_ID);
            }
         }

         @Override
         public void dragStart(DragSourceEvent event) {
            // do nothing
         }
      });

      DropTarget target = new DropTarget(taskXViewer.getTree(), DND.DROP_COPY);
      target.setTransfer(new Transfer[] {
         FileTransfer.getInstance(),
         TextTransfer.getInstance(),
         ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         @Override
         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         @Override
         public void dragOver(DropTargetEvent event) {
            event.detail = DND.DROP_COPY;
         }

         @Override
         public void dropAccept(DropTargetEvent event) {
            // do nothing
         }
      });
   }

   private void performDrop(DropTargetEvent e) {
      if (e.data instanceof ArtifactData) {
         try {
            if (iXTaskViewer.getAwa() == null) {
               return;
            }
            List<TaskArtifact> taskArts = new LinkedList<>();
            for (Artifact art : ((ArtifactData) e.data).getArtifacts()) {
               if (art.isOfType(AtsArtifactTypes.Task)) {
                  taskArts.add((TaskArtifact) art);
               }
            }
            if (taskArts.isEmpty()) {
               AWorkbench.popup("No Tasks To Drop");
               return;
            }
            TaskManager.moveTasks((TeamWorkFlowArtifact) iXTaskViewer.getAwa(), taskArts);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public String getActionDescription() {
      return null;
   }

   @Override
   public void refreshActionHandler() {
      try {
         if (iXTaskViewer.isRefreshActionHandled()) {
            iXTaskViewer.handleRefreshAction();
         } else {
            loadTable();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public CustomizeData getCustomizeDataCopy() {
      return taskXViewer.getCustomizeMgr().generateCustDataFromTable();
   }

   @Override
   public ITaskEditorProvider getTaskEditorProviderCopy() throws OseeCoreException {
      return ((TaskEditorInput) ((TaskEditor) iXTaskViewer.getEditor()).getEditorInput()).getItaskEditorProvider().copyProvider();
   }

   @Override
   public List<? extends Artifact> getSelectedArtifacts() {
      return getSelectedTaskArtifactItems();
   }

   @Override
   public void taskAddActionHandler() {
      handleNewTask();
   }

   @Override
   public WorldXViewer getWorldXViewer() {
      return taskXViewer;
   }

   @Override
   public void removeItems(Collection<? extends Object> objects) {
      taskArts.removeAll(objects);
   }

   @Override
   public void relationsModifed(Collection<Artifact> relModifiedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered) {
      try {
         loadTable();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void onTasksDeleted() {
      try {
         AtsTaskCache.decache((AbstractTaskableArtifact) iXTaskViewer.getAwa());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.WARNING, ex);
      }
   }
}
