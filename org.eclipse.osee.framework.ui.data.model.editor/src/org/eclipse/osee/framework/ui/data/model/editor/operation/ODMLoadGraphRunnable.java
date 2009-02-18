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
package org.eclipse.osee.framework.ui.data.model.editor.operation;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditor;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditorInput;
import org.eclipse.osee.framework.ui.data.model.editor.input.OseeDataTypeFactory;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Roberto E. Escobar
 */
public class ODMLoadGraphRunnable implements IExceptionableRunnable {

   private GraphicalViewer viewer;
   private ODMEditor editor;
   private ODMEditorInput input;

   private static final int TOTAL_STEPS = Integer.MAX_VALUE;
   private static final int SHORT_TASK_STEPS = TOTAL_STEPS / 50; // 2%
   private static final int VERY_LONG_TASK = TOTAL_STEPS / 2; // 50%
   private static final int TASK_STEPS = (TOTAL_STEPS - SHORT_TASK_STEPS * 3 - VERY_LONG_TASK) / 2;

   public ODMLoadGraphRunnable(GraphicalViewer viewer, ODMEditor editor, ODMEditorInput input) {
      super();
      this.viewer = viewer;
      this.editor = editor;
      this.input = input;
   }

   public String getName() {
      return "Loading Osee Data Type Graph";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void run(IProgressMonitor monitor) throws Exception {
      boolean error = false;
      monitor.beginTask(getName(), TOTAL_STEPS);
      monitor.worked(SHORT_TASK_STEPS);
      try {

         monitor.setTaskName("Initializating cache");
         DataTypeCache dataTypeCache = input.getDataTypeCache();
         dataTypeCache.clear();
         monitor.worked(SHORT_TASK_STEPS);

         OseeDataTypeFactory.addTypesFromDataStore(dataTypeCache);
         monitor.worked(SHORT_TASK_STEPS);

         IResource resource = input.getResource();
         if (resource != null) {
            DataTypeSource dataTypeSource = OseeDataTypeFactory.loadFromFile(resource.getFullPath());
            dataTypeCache.addDataTypeSource(dataTypeSource);
         }
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
               updateView(monitor, dataTypeCache);
            }
         }
         monitor.done();
      } catch (Exception ex) {
         AWorkbench.popup("Error Calculating Revision Graph Information", Lib.exceptionToString(ex));
      }
   }

   private void updateView(IProgressMonitor monitor, DataTypeCache dataTypeCache) throws OseeCoreException {
      monitor.setTaskName("Finding root node");
      int unitWork = TASK_STEPS / (int) (dataTypeCache.getNumberOfSources());
      if (unitWork < 1) {
         unitWork = 1;
      }
      monitor.setTaskName("Calculating graph");
      final ODMDiagram graph = new ODMDiagram(dataTypeCache);
      //      String id = dataTypeCache.getDataTypeSourceIds().iterator().next();
      //      DataTypeSource source = dataTypeCache.getDataTypeSourceById(id);

      //      TypeManager<ArtifactDataType> manager = source.getArtifactTypeManager();
      //      ArtifactDataType artifact = manager.getAll().iterator().next();
      //
      //      graph.add(artifact);
      //      graph.add(artifact.getParent());

      monitor.setTaskName("Drawing graph");

      Display.getDefault().syncExec(new Runnable() {
         public void run() {
            viewer.setContents(graph);
            editor.updatePalette();
         }
      });
   }

   //   private final class InternalTaskProgressListener implements IProgressListener {
   //
   //      private IProgressMonitor monitor;
   //      private int unitWork;
   //
   //      public InternalTaskProgressListener(IProgressMonitor monitor, int unitWork) {
   //         this.monitor = monitor;
   //         this.unitWork = unitWork;
   //      }
   //
   //      public void worked() {
   //         monitor.worked(unitWork);
   //      }
   //   }
}
