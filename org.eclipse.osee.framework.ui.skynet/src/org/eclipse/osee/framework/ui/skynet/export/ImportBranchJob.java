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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exportImport.BranchExporter;
import org.eclipse.osee.framework.skynet.core.exportImport.BranchImporterSaxHandler;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
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
   private static final int BUFFER_SIZE = 4000000;
   private static final int ERROR_LINE_NUMBERS_FOR_BUFFER = 17;
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(ImportBranchJob.class);
   private final File importFile;
   private final Branch branch;
   private final boolean includeMainLevelBranch;
   private final boolean includeDescendantBranches;
   private Pair<String, String> verificationErrorInfo;
   private OseeConsole console;

   public ImportBranchJob(File importFile, Branch branch, boolean includeMainLevelBranch, boolean includeDescendantBranches) {
      super("Importing Onto Branch");
      if (branch == null) throw new IllegalArgumentException("branch can not be null");
      if (importFile == null) throw new IllegalArgumentException("file can not be null");
      this.importFile = importFile;
      this.branch = branch;
      this.includeMainLevelBranch = includeMainLevelBranch;
      this.includeDescendantBranches = includeDescendantBranches;
      this.verificationErrorInfo = new Pair<String, String>("", "");
      this.console = new OseeConsole("Importing Onto Branch", true);
   }

   public IStatus run(final IProgressMonitor monitor) {
      try {

         XMLReader reader = XMLReaderFactory.createXMLReader();
         reader.setContentHandler(new BranchImporterSaxHandler(branch, includeMainLevelBranch,
               includeDescendantBranches, monitor));
         reader.parse(new InputSource(new FileInputStream(importFile)));

         final MutableBoolean isVerificationAllowed = new MutableBoolean(false);
         Display.getDefault().syncExec(new Runnable() {
            public void run() {
               isVerificationAllowed.setValue(MessageDialog.openQuestion(
                     PlatformUI.getWorkbench().getDisplay().getActiveShell(), getName(),
                     "Would you like to run verification?"));
            }
         });

         if (false != isVerificationAllowed.getValue()) {
            File verificationFile = getVerificationFile();
            try {
               // Export database to a temporary file
               boolean descendantsOnly = (includeMainLevelBranch == false && includeDescendantBranches == true);
               new BranchExporter(monitor, verificationFile, branch, new Timestamp(0),
                     GlobalTime.GreenwichMeanTimestamp(), descendantsOnly).export();

               // Compare ImportFile to Export File
               final boolean result = compareFiles(monitor, importFile, verificationFile);
               final String message =
                     String.format("Branch Import Verification: %s\n", true != result ? "FAILED" : "PASSED");
               Display.getDefault().syncExec(new Runnable() {
                  public void run() {
                     if (true != result) {
                        String errorMessage =
                              String.format("%s%s\n%s", message, verificationErrorInfo.getKey(),
                                    verificationErrorInfo.getValue());
                        logger.log(Level.SEVERE, errorMessage);
                        console.writeError(errorMessage);
                     }
                     MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), getName(),
                           message);
                  }
               });
            } finally {
               verificationFile.delete();
            }
         }
         return Status.OK_STATUS;
      } catch (Exception ex) {
         String message = ex.getLocalizedMessage();

         if (message == null) message = "";

         logger.log(Level.SEVERE, message, ex);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.toString(), ex);
      } finally {
         monitor.done();
      }
   }

   private File getVerificationFile() {
      String filePath = importFile.getAbsolutePath();
      String extension = Lib.getExtension(filePath);
      filePath = filePath.replaceAll("\\." + extension, "\\.verify\\." + extension);
      return new File(filePath);
   }

   private boolean compareFiles(IProgressMonitor monitor, File file1, File file2) {
      monitor = (monitor != null) ? monitor : new NullProgressMonitor();
      LineNumberReader fis1 = null;
      LineNumberReader fis2 = null;
      boolean toReturn = true;
      monitor.setTaskName("Comparing Data: " + branch.getBranchName());
      if (file1.length() != file2.length()) {
         toReturn &= false;
         verificationErrorInfo.setKey("EXPECTED:\nFile Size: " + file1.length());
         verificationErrorInfo.setValue("ACTUAL:\nFile Size: " + file2.length());
      } else {
         try {
            fis1 = new LineNumberReader(new FileReader(file1));
            fis2 = new LineNumberReader(new FileReader(file2));
            long totalSize = file1.length() / BUFFER_SIZE + 1;
            monitor.beginTask("Verification", IProgressMonitor.UNKNOWN);
            char[] buffer1 = new char[BUFFER_SIZE];
            char[] buffer2 = new char[BUFFER_SIZE];
            int count = 0;

            // Check all bytes are equal
            while (fis1.read(buffer1) != -1 && fis2.read(buffer2) != -1) {
               if (!Arrays.equals(buffer1, buffer2)) {
                  toReturn &= false;
                  verificationErrorInfo = getErrorSection(file1.length(), fis1, fis2);
                  break;
               }
               count++;
               monitor.subTask(String.format("Checking %s of %s", count, totalSize));
               monitor.worked(1);
               if (monitor.isCanceled()) {
                  toReturn &= false;
                  verificationErrorInfo.setKey("Verification Cancelled");
                  verificationErrorInfo.setValue(" ");
                  break;
               }
            }
         } catch (Exception e) {
            toReturn &= false;
            verificationErrorInfo.setKey("Unexpected Exception:");
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            verificationErrorInfo.setValue(writer.toString());
         } finally {
            try {
               if (fis1 != null) {
                  fis1.close();
               }
               if (fis2 != null) {
                  fis2.close();
               }
            } catch (IOException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
            monitor.done();
         }
      }
      return toReturn;
   }

   private Pair<String, String> getErrorSection(long fileLength, LineNumberReader fis1, LineNumberReader fis2) throws IOException {
      StringBuilder expected = new StringBuilder();
      StringBuilder actual = new StringBuilder();
      int offset = ERROR_LINE_NUMBERS_FOR_BUFFER / 2;
      long start = 0;
      long stop = 0;
      int errorLineNum = fis1.getLineNumber();
      if ((errorLineNum - offset) > 0) {
         start = errorLineNum - offset;
      }

      if ((errorLineNum + offset) < fileLength) {
         stop = errorLineNum + offset;
      } else {
         stop = fileLength;
      }
      fis1.setLineNumber((int) start);
      fis2.setLineNumber((int) start);
      for (long index = start; index <= stop; index++) {
         if (errorLineNum == fis1.getLineNumber()) {
            expected.append("<<<<<<<<<<<<<<< ");
            actual.append("<<<<<<<<<<<<<<< ");
         }

         expected.append(fis1.getLineNumber());
         expected.append(": ");
         expected.append(fis1.readLine());
         expected.append("\n");

         actual.append(fis2.getLineNumber());
         actual.append(": ");
         actual.append(fis2.readLine());
         actual.append("\n");
      }
      return new Pair<String, String>("EXPECTED:\n" + expected.toString(), "ACTUAL:\n" + actual.toString());
   }
}