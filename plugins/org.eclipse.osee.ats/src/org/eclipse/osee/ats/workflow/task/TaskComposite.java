/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.editor.WorkflowEditor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workflow.task.internal.TaskMover;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.IWorldEditor;
import org.eclipse.osee.ats.world.WorldComposite;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
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
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class TaskComposite extends WorldComposite {

   private final IXTaskViewer iXTaskViewer;
   private TaskXViewer taskXViewer;
   private final IDirtiableEditor dirtiableEditor;
   boolean tasksEditable;
   private final IAtsTeamWorkflow teamWf;

   public TaskComposite(IXTaskViewer iXTaskViewer, IWorldEditor worldEditor, IXViewerFactory xViewerFactory, Composite parent, int style, IDirtiableEditor dirtiableEditor, boolean tasksEditable, IAtsTeamWorkflow teamWf) {
      super(worldEditor, xViewerFactory, parent, style, false);
      this.iXTaskViewer = iXTaskViewer;
      this.dirtiableEditor = dirtiableEditor;
      this.tasksEditable = tasksEditable;
      this.teamWf = teamWf;
      taskXViewer.setTasksEditable(tasksEditable);
      taskXViewer.setNewTaskSelectionEnabled(tasksEditable);
      taskXViewer.setTeamWf(teamWf);
   }

   @Override
   protected WorldXViewer createXViewer(IXViewerFactory xViewerFactory, Composite mainComp) {
      taskXViewer = new TaskXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, xViewerFactory, null);
      return taskXViewer;
   }

   @Override
   protected void setupDragAndDropSupport(boolean createDragAndDrop) {
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
               event.data = new ArtifactData(arts.toArray(new Artifact[arts.size()]), "", WorkflowEditor.EDITOR_ID);
            }
         }

         @Override
         public void dragStart(DragSourceEvent event) {
            // do nothing
         }
      });

      DropTarget target = new DropTarget(taskXViewer.getTree(), DND.DROP_COPY);
      target.setTransfer(
         new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance(), ArtifactTransfer.getInstance()});
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
            if (iXTaskViewer.getTeamWf() == null) {
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
            TaskMover mover = new TaskMover(iXTaskViewer.getTeamWf(), taskArts);
            mover.moveTasks();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   @Override
   public WorldXViewer getXViewer() {
      return taskXViewer;
   }

   public IXTaskViewer getIXTaskViewer() {
      return iXTaskViewer;
   }

   public TaskXViewer getTaskXViewer() {
      return taskXViewer;
   }

   public TeamWorkFlowArtifact getTeamArt() {
      return (TeamWorkFlowArtifact) teamWf.getStoreObject();
   }

}
