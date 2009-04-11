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
package org.eclipse.osee.define.traceability.action;

import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.jobs.FindTraceUnitJob;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class FindTraceUnitEditorAction implements IEditorActionDelegate {

   public void run(IAction action) {
      IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
      IEditorInput editorInput = editorPart.getEditorInput();
      IFile iFile = null;
      if (editorInput instanceof IFileEditorInput) {
         iFile = ((IFileEditorInput) editorInput).getFile();
         if (iFile != null) {
            OseeLog.log(DefinePlugin.class, Level.INFO, "iFile *" + iFile + "*");
         }
      }
      if (iFile == null) {
         AWorkbench.popup("ERROR", "Can't retrieve IFile");
         return;
      }
      Jobs.startJob(new FindTraceUnitJob("Resource To Trace Unit Artifact", iFile), true);
   }

   public void selectionChanged(IAction action, ISelection selection) {
   }

   public void setActiveEditor(IAction action, IEditorPart targetEditor) {
   }
}