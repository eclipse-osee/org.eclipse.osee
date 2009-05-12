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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditor;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditorInput;
import org.eclipse.osee.framework.ui.data.model.editor.input.OseeDataTypeFactory;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class ODMLoadGraphRunnable implements IExceptionableRunnable {

   private GraphicalViewer viewer;
   private ODMEditor editor;
   private ODMEditorInput input;

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
   public IStatus run(IProgressMonitor monitor) throws Exception {
      monitor.beginTask(getName(), ODMConstants.TOTAL_STEPS);
      monitor.worked(ODMConstants.SHORT_TASK_STEPS);
      monitor.setTaskName("Initializating cache");
      DataTypeCache dataTypeCache = input.getDataTypeCache();
      dataTypeCache.clear();
      monitor.worked(ODMConstants.SHORT_TASK_STEPS);

      OseeDataTypeFactory.addTypesFromDataStore(dataTypeCache);
      monitor.worked(ODMConstants.SHORT_TASK_STEPS);

      IResource resource = input.getResource();
      if (resource != null) {
         DataTypeSource dataTypeSource = OseeDataTypeFactory.loadFromFile(resource.getFullPath());
         dataTypeCache.addDataTypeSource(dataTypeSource);
      }
      monitor.worked(ODMConstants.SHORT_TASK_STEPS);

      if (editor != null) {
         monitor.setTaskName("Drawing graph");
         Display.getDefault().syncExec(new Runnable() {
            public void run() {
               viewer.setContents(new ODMDiagram(input.getDataTypeCache()));
               editor.updatePalette();
            }
         });
      }
      return Status.OK_STATUS;
   }
}
