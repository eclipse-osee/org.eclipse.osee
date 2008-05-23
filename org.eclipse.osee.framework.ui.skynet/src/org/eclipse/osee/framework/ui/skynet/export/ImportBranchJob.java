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
package org.eclipse.osee.framework.ui.skynet.export;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exportImport.BranchImporterSaxHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Robert A. Fisher
 */
public class ImportBranchJob extends Job {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(ImportBranchJob.class);
   private final File importFile;
   private final Branch branch;
   private final boolean includeMainLevelBranch;
   private final boolean includeDescendantBranches;

   public ImportBranchJob(File importFile, Branch branch, boolean includeMainLevelBranch, boolean includeDescendantBranches) {
      super("Importing Onto Branch");
      if (branch == null) throw new IllegalArgumentException("branch can not be null");
      if (importFile == null) throw new IllegalArgumentException("file can not be null");
      this.importFile = importFile;
      this.branch = branch;
      this.includeMainLevelBranch = includeMainLevelBranch;
      this.includeDescendantBranches = includeDescendantBranches;
   }

   public IStatus run(final IProgressMonitor monitor) {
      ZipFile zipFile = null;
      try {
         String baseName = Lib.removeExtension(importFile.getName());
         zipFile = new ZipFile(importFile);
         ZipEntry entry = zipFile.getEntry(baseName + ".xml");
         InputStream imputStream = zipFile.getInputStream(entry);
         XMLReader reader = XMLReaderFactory.createXMLReader();
         reader.setContentHandler(new BranchImporterSaxHandler(zipFile, branch, includeMainLevelBranch,
               includeDescendantBranches, monitor));
         reader.parse(new InputSource(imputStream));

         final MutableBoolean isVerificationAllowed = new MutableBoolean(false);
         Display.getDefault().syncExec(new Runnable() {
            public void run() {
               isVerificationAllowed.setValue(MessageDialog.openQuestion(
                     PlatformUI.getWorkbench().getDisplay().getActiveShell(), getName(),
                     "Would you like to run verification?"));
            }
         });

         if (false != isVerificationAllowed.getValue()) {
            ImportBranchVerification verifier =
                  new ImportBranchVerification(importFile, branch, includeMainLevelBranch, includeDescendantBranches);
            verifier.execute(monitor);
         }
         return Status.OK_STATUS;
      } catch (Exception ex) {
         String message = ex.getLocalizedMessage();

         if (message == null) message = "";

         logger.log(Level.SEVERE, message, ex);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.toString(), ex);
      } finally {
         monitor.done();
         if (zipFile != null) {
            try {
               zipFile.close();
            } catch (IOException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
         }
      }
   }
}