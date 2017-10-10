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
package org.eclipse.osee.framework.ui.branch.graph.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditor;
import org.eclipse.osee.framework.ui.branch.graph.model.GraphCache;
import org.eclipse.osee.framework.ui.branch.graph.model.GraphLoader;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Roberto E. Escobar
 */
public class LoadGraphOperation implements IExceptionableRunnable {

   private BranchId resource;
   private final GraphicalViewer viewer;
   private final BranchGraphEditor editor;
   private GraphCache graph;

   private static final int TOTAL_STEPS = Integer.MAX_VALUE;
   private static final int SHORT_TASK_STEPS = TOTAL_STEPS / 50;
   private static final int VERY_LONG_TASK = TOTAL_STEPS / 2;
   private static final int TASK_STEPS = (TOTAL_STEPS - SHORT_TASK_STEPS * 3 - VERY_LONG_TASK) / 2;

   protected LoadGraphOperation(IWorkbenchPart part, GraphicalViewer viewer, BranchGraphEditor editor) {
      super();
      this.viewer = viewer;
      this.editor = editor;
   }

   public LoadGraphOperation(IWorkbenchPart part, GraphicalViewer viewer, BranchGraphEditor editor, BranchId resource) {
      this(part, viewer, editor);
      this.resource = resource;
   }

   public String getName() {
      return "Loading graph information";
   }

   @Override
   public IStatus run(IProgressMonitor monitor) throws Exception {
      boolean error = false;
      monitor.beginTask(getName(), TOTAL_STEPS);
      monitor.worked(SHORT_TASK_STEPS);
      try {
         TransactionToken transaction = TransactionManager.getHeadTransaction(resource);
         BranchId path = transaction.getBranch();

         monitor.setTaskName("Initializating cache");

         monitor.worked(SHORT_TASK_STEPS);

         if (editor != null) {
            if (error == true || monitor.isCanceled()) {
               Displays.pendInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     IWorkbenchWindow window = editor.getEditorSite().getWorkbenchWindow();
                     IWorkbenchPage page = window.getActivePage();
                     page.activate(editor);
                     page.closeEditor(editor, false);
                  }
               });
            } else {
               updateView(monitor, path, transaction);
            }
         }
      } catch (Exception ex) {
         AWorkbench.popup("Error Calculating Revision Graph Information", Lib.exceptionToString(ex));
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }

   private void updateView(IProgressMonitor monitor, BranchId branch, TransactionId revision) {
      monitor.setTaskName("Finding root node");
      int unitWork = (int) (TASK_STEPS / revision.getId());
      if (unitWork < 1) {
         unitWork = 1;
      }
      monitor.setTaskName("Calculating graph");
      graph = new GraphCache(branch);
      GraphLoader.load(graph, new InternalTaskProgressListener(monitor, unitWork));
      monitor.setTaskName("Drawing graph");

      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            viewer.setContents(graph);
            editor.setOutlineContent(graph);
         }
      });
   }
   private final class InternalTaskProgressListener implements IProgressListener {

      private final IProgressMonitor monitor;
      private final int unitWork;

      public InternalTaskProgressListener(IProgressMonitor monitor, int unitWork) {
         this.monitor = monitor;
         this.unitWork = unitWork;
      }

      @Override
      public void worked() {
         monitor.worked(unitWork);
      }
   }
}
