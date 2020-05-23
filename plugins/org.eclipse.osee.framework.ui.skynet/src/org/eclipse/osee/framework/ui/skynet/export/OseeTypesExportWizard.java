/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesExportWizard extends Wizard implements IImportWizard {
   private ResourceSelectionPage mainPage;

   public OseeTypesExportWizard() {
      super();
      setDialogSettings(Activator.getInstance().getDialogSettings());
      setWindowTitle("OSEE Types Export Wizard");
      setNeedsProgressMonitor(true);
      setHelpAvailable(true);
   }

   @Override
   public boolean performFinish() {
      File folder = mainPage.getFile();

      File file = new File(folder, getOseeFileName());
      FileOutputStream fos = null;
      try {
         fos = new FileOutputStream(file);
         final OutputStream outputStream = new BufferedOutputStream(fos);
         IOperation op = ArtifactTypeManager.newExportTypesOp(outputStream);
         Operations.executeAsJob(op, true, Job.LONG, new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
               Lib.close(outputStream);
            }
         });
      } catch (FileNotFoundException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      } finally {
         Lib.close(fos);
      }
      return true;
   }

   private String getOseeFileName() {
      return "OseeTypes_" + Lib.getDateTimeString() + ".osee";
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      mainPage = new ResourceSelectionPage(getWindowTitle());
   }

   @Override
   public void addPages() {
      addPage(mainPage);
   }
}
