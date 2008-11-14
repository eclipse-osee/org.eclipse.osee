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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;

/**
 * @author Ryan D. Brooks
 */
public class BlamJob extends Job {
   private final BlamWorkflow workflow;
   private final VariableMap variableMap;
   private final BlamEditor editor;
   private final Collection<IBlamEventListener> listeners;

   public BlamJob(BlamEditor editor) {
      super(editor.getWorkflow().getDescriptiveName());
      this.editor = editor;
      this.variableMap = editor.getBlamVariableMap();
      this.workflow = editor.getWorkflow();
      this.listeners = new LinkedList<IBlamEventListener>();
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      long startTime = System.currentTimeMillis();
      notifyListeners(new BlamStartedEvent());
      try {
         List<BlamOperation> operations = workflow.getOperations();
         if (operations.size() == 0) {
            throw new IllegalStateException("No operations were found for this workflow");
         }
         monitor.beginTask(workflow.getDescriptiveName(), operations.size());

         for (BlamOperation operation : operations) {
            IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);

            operation.setBlamEditor(editor);
            operation.runOperation(variableMap, subMonitor);
            monitor.worked(1);
         }

         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         toReturn = new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      } finally {
         monitor.done();
         notifyListeners(new BlamFinishedEvent(System.currentTimeMillis() - startTime));
      }
      return toReturn;
   }

   public void addListener(IBlamEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }

      listeners.add(listener);
   }

   public boolean removeListener(IBlamEventListener listener) {
      return listeners.remove(listener);
   }

   private void notifyListeners(IBlamEvent event) {
      for (IBlamEventListener listener : listeners) {
         listener.onEvent(event);
      }
   }
}