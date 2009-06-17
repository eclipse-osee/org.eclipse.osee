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
package org.eclipse.osee.ote.ui.test.manager.batches.actions;

import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.batches.TestBatchProjectNature;
import org.eclipse.osee.ote.ui.test.manager.batches.util.SelectionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class AddTestBatchProjectNature implements IObjectActionDelegate {
   private static final String NATURE_ADDED = "Test Batch Nature Added";
   private static final String ADDING_NATURE = "Adding Test Batch Nature";

   private IJavaProject currentJavaProject;
   private IProject currentProject;

   /* (non-Javadoc)
    * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
    */
   public void setActivePart(IAction action, IWorkbenchPart targetPart) {
      // Do Nothing
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
    */
   public void run(IAction action) {
      IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      Cursor waitCursor = new Cursor(window.getShell().getDisplay(), SWT.CURSOR_WAIT);
      try {
         window.getShell().setCursor(waitCursor);
         ((ApplicationWindow) window).setStatus(ADDING_NATURE);

         //new way
         if (currentJavaProject == null) {
            // if the java nature is not present
            // it must be added, along with the test batch nature
            IProjectDescription description = currentProject.getDescription();
            String[] natureIds = description.getNatureIds();
            String[] newNatures = new String[natureIds.length + 2];
            System.arraycopy(natureIds, 0, newNatures, 0, natureIds.length);
            newNatures[newNatures.length - 2] = JavaCore.NATURE_ID;
            newNatures[newNatures.length - 1] = TestBatchProjectNature.NATURE_ID;
            description.setNatureIds(newNatures);
            currentProject.setDescription(description, null);

            currentJavaProject = (IJavaProject) JavaCore.create((IProject) currentProject);
         } else {
            //add the test batch nature, the java nature is already present
            IProjectDescription description = currentJavaProject.getProject().getDescription();
            String[] natures = description.getNatureIds();
            String[] newNatures = new String[natures.length + 1];
            System.arraycopy(natures, 0, newNatures, 0, natures.length);
            newNatures[natures.length] = TestBatchProjectNature.NATURE_ID;
            description.setNatureIds(newNatures);
            currentJavaProject.getProject().setDescription(description, null);
         }

         // refresh project so user sees new files, libraries, etc
         currentJavaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
         ((ApplicationWindow) window).setStatus(NATURE_ADDED);

      } catch (Exception ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, String.format("Error adding test batch nature on [%s]",
               currentJavaProject.getProject().getName()), ex);
         Shell shell = new Shell();
         MessageDialog.openInformation(shell, TestManagerPlugin.PLUGIN_ID,
               "Error adding test batch nature:\n" + SelectionUtil.getStatusMessages(ex));
      } finally {
         window.getShell().setCursor(null);
         waitCursor.dispose();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
    */
   public void selectionChanged(IAction action, ISelection selection) {
      currentJavaProject = SelectionUtil.findSelectedJavaProject(selection);
      if (currentJavaProject == null) {
         currentProject = SelectionUtil.findSelectedProject(selection);
      }

   }
}
