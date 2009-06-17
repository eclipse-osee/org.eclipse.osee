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
package org.eclipse.osee.ote.ui.test.manager.actions;

import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.operations.AddIFileToTestManager;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class AddToTestManagerEditorAction implements IEditorActionDelegate {


   public void run(IAction action) {
      // Get IFile of current editor
      IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
      IEditorInput editorInput = editorPart.getEditorInput();
      IFile iFile = null;
      if (editorInput instanceof IFileEditorInput) {
         iFile = ((IFileEditorInput) editorInput).getFile();
         if (iFile != null) {
            OseeLog.log(TestManagerPlugin.class, Level.INFO, "iFile *" + iFile + "*");
         }
      }
      if (iFile == null) {
         AWorkbench.popup("ERROR", "Can't retrieve IFile");
         return;
      }
      AddIFileToTestManager.getOperation().addIFileToScriptsPage(iFile.getLocation().toOSString());
   }

   public void selectionChanged(IAction action, ISelection selection) {
   }

   public void setActiveEditor(IAction action, IEditorPart targetEditor) {
   }

}