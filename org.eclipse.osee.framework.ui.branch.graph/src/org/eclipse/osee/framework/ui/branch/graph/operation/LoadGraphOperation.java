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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditor;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditorInput;
import org.eclipse.osee.framework.ui.branch.graph.model.GraphCache;
import org.eclipse.osee.framework.ui.branch.graph.model.GraphLoader;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Roberto E. Escobar
 */
public class LoadGraphOperation implements IExceptionableRunnable {

   private Branch resource;
   private GraphicalViewer viewer;
   private BranchGraphEditor editor;
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

   public LoadGraphOperation(IWorkbenchPart part, GraphicalViewer viewer, BranchGraphEditor editor, Branch resource) {
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
         TransactionId transaction = TransactionIdManager.getlatestTransactionForBranch(resource);
         if (editor != null) {
            ((BranchGraphEditorInput) editor.getEditorInput()).setTransactionId(transaction);
         }
         Branch path = transaction.getBranch();

         monitor.setTaskName("Initializating cache");

         monitor.worked(SHORT_TASK_STEPS);

         if (editor != null) {
            if (error == true || monitor.isCanceled()) {
               Display.getDefault().syncExec(new Runnable() {
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

   private void updateView(IProgressMonitor monitor, Branch branch, TransactionId revision) throws OseeCoreException {
      monitor.setTaskName("Finding root node");
      int unitWork = TASK_STEPS / (int) (revision.getTransactionNumber());
      if (unitWork < 1) {
         unitWork = 1;
      }
      monitor.setTaskName("Calculating graph");
      graph = new GraphCache(branch);
      GraphLoader.load(graph, new InternalTaskProgressListener(monitor, unitWork));
      monitor.setTaskName("Drawing graph");

      Display.getDefault().syncExec(new Runnable() {
         public void run() {
            viewer.setContents(graph);
            editor.setOutlineContent(graph);
         }
      });
   }
   private final class InternalTaskProgressListener implements IProgressListener {

      private IProgressMonitor monitor;
      private int unitWork;

      public InternalTaskProgressListener(IProgressMonitor monitor, int unitWork) {
         this.monitor = monitor;
         this.unitWork = unitWork;
      }

      public void worked() {
         monitor.worked(unitWork);
      }
   }
}
