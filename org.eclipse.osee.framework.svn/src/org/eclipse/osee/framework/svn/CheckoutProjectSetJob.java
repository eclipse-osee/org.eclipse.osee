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
package org.eclipse.osee.framework.svn;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ui.TeamUIMessages;
import org.eclipse.team.internal.ui.TeamUIPlugin;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("restriction")
public class CheckoutProjectSetJob extends Job {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(CheckoutProjectSetJob.class);
   private URL projectSetFile;
   private String workingSetName;

   public CheckoutProjectSetJob(String jobName, String workingSetName, URL projectSetFile) {
      super(jobName);
      this.projectSetFile = projectSetFile;
      this.workingSetName = workingSetName;
   }

   @Override
   protected IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn = Status.OK_STATUS;
      try {
         boolean result = performImportProjectSet(monitor, getProjectSetPath(), workingSetName);
         if (result != true) {
            toReturn = Status.CANCEL_STATUS;
         }
      } catch (Throwable ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return toReturn;
   }

   private String getProjectSetPath() throws Exception {
      URL url = FileLocator.resolve(projectSetFile);
      File file = new File(url.getFile());
      return file.getAbsolutePath();
   }

   private boolean performImportProjectSet(IProgressMonitor monitor, String fileName, String workingSet) {
      boolean result = false;
      try {
         Class<?> clazz = Platform.getBundle("org.eclipse.team.ui").loadClass("org.eclipse.team.internal.ui.wizards.ImportProjectSetOperation");
         Object object = null;
         if (EclipseVersion.isVersion("3.3")) {
            Constructor<?> constructor = clazz.getConstructor(IRunnableContext.class, String.class, String.class);
            object = constructor.newInstance(null, fileName, workingSet);
         } else if (EclipseVersion.isVersion("3.4")) {
            List<IWorkingSet> workingSets = new ArrayList<IWorkingSet>();
            IWorkingSet workingSetObject =
                  PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSet(workingSetName);
            if (workingSetObject != null) {
               workingSets.add(workingSetObject);
            }
            Constructor<?> constructor =
                  clazz.getConstructor(IRunnableContext.class, String.class, IWorkingSet[].class);
            object = constructor.newInstance(null, fileName, workingSets.toArray(new IWorkingSet[workingSets.size()]));
         } else {
            throw new UnsupportedOperationException();
         }
         Method method = clazz.getDeclaredMethod("run", IProgressMonitor.class);
         method.invoke(object, monitor);
         result = true;
      } catch (Exception ex) {
         handleException(ex);
      }
      return result;
   }

   private boolean handleException(Exception ex) {
      Throwable target = ex.getCause();
      if (target instanceof TeamException) {
         displayErrorMessage(true, null, target);
         return false;
      }
      if (target instanceof RuntimeException) {
         throw (RuntimeException) target;
      }
      if (target instanceof Error) {
         throw (Error) target;
      }
      String message =
            target instanceof SAXException ? TeamUIMessages.ProjectSetImportWizard_2 : TeamUIMessages.ProjectSetImportWizard_3;
      displayErrorMessage(false, message, target);
      return false;
   }

   private void displayErrorMessage(final boolean isTeamException, final String message, final Throwable target) {
      PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
         public void run() {
            Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
            if (isTeamException) {
               ErrorDialog.openError(shell, null, null, ((TeamException) target).getStatus());
            } else {
               ErrorDialog.openError(shell, null, null, new Status(IStatus.ERROR, TeamUIPlugin.ID, 0, NLS.bind(message,
                     new String[] {target.getMessage()}), target));
            }
         }
      });
   }
};
