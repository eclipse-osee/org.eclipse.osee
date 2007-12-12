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
package org.eclipse.osee.framework.ui.service.control.jobs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchDataPersist;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.ServiceItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class StandAloneApplicationLaunchJob extends Job {

   private TextDisplayHelper display;
   private ServiceLaunchingInformation serviceInfo;
   private String javaCompiler;
   private File localLocation;
   private ProgressBar progress;

   public StandAloneApplicationLaunchJob(String name, String javaCompiler, File localLocation, ServiceLaunchingInformation serviceInfo, TextDisplayHelper display, ProgressBar progress) {
      super(name);
      this.javaCompiler = javaCompiler;
      this.serviceInfo = serviceInfo;
      this.display = display;
      this.localLocation = localLocation;
      this.progress = progress;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.OK_STATUS;

      ServiceLaunchDataPersist data = ServiceLaunchDataPersist.getInstance();
      data.saveLastServiceLaunched(serviceInfo.getServiceItem().getName());

      display.clear();
      display.addText("\t\t ----------- Unzip -------------\n\n", SWT.BOLD, SWT.COLOR_BLACK, false);

      try {
         Bundle bundle = Platform.getBundle(serviceInfo.getServiceItem().getPlugin());
         URL url = bundle.getEntry(serviceInfo.getServiceItem().getZipName());
         URL resolvedURL = FileLocator.resolve(url);
         File zipLocation = new File(resolvedURL.getFile());

         display.addText("\tFrom Location: ", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
         display.addText("\t" + zipLocation.getAbsolutePath() + "\n", SWT.NORMAL, SWT.COLOR_BLACK, false);
         display.addText("\tTo Location: ", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
         display.addText("\t" + localLocation.getAbsolutePath() + "\n", SWT.NORMAL, SWT.COLOR_BLACK, false);

         System.out.println(localLocation);
         unzip(zipLocation, localLocation);

      } catch (Exception ex) {
         display.addText(ControlPlugin.getStackMessages(ex) + "\n\n", SWT.NORMAL, SWT.COLOR_RED, false);
      }

      String exec = serviceInfo.getServiceItem().getStandAloneExecution();
      String compilerExec = javaCompiler.trim();
      if (compilerExec.endsWith(".exe")) {
         compilerExec = compilerExec.substring(0, compilerExec.lastIndexOf(".exe"));
      }

      if (Lib.isWindows()) {
         compilerExec = "\"" + compilerExec + "\"";
         exec = exec.replaceAll("-?nohup" + ServiceItem.EXEC_SEPARATOR + "?", "");
      }
      exec = exec.replace("java", compilerExec);

      display.addText("\n\t\t ----------- Execute -------------\n\n", SWT.BOLD, SWT.COLOR_BLACK, false);

      display.addText("\tExecuting:", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
      display.addText("\t" + exec.replaceAll(ServiceItem.EXEC_SEPARATOR, " ") + "\n", SWT.BOLD, SWT.COLOR_BLACK, false);

      ProcessBuilder runner = new ProcessBuilder();
      runner.directory(localLocation);
      runner.command(exec.split(ServiceItem.EXEC_SEPARATOR));
      final Process process;
      try {
         process = runner.start();

         display.startProcessHandling(process);
      } catch (IOException ex) {
         display.addText(ControlPlugin.getStackMessages(ex) + "\n", SWT.NORMAL, SWT.COLOR_RED, false);
      }
      return toReturn;
   }

   private Map<String, String> unzip(File zipFile, File destinationDir) throws IOException {
      int BUFFER = 2048;
      BufferedOutputStream dest = null;
      BufferedInputStream is = null;
      ZipEntry entry = null;
      Map<String, String> unzippedFiles = new HashMap<String, String>();
      try {
         ZipFile zipfile = new ZipFile(zipFile.getAbsolutePath());
         StringBuffer statusBuffer = new StringBuffer();
         final int totalEntries = zipfile.size();
         int countEntries = 0;

         PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            public void run() {
               progress.setMinimum(0);
               progress.setMaximum(totalEntries);
               progress.setSelection(0);
            }
         });

         Enumeration<? extends ZipEntry> e = zipfile.entries();
         while (e.hasMoreElements()) {
            incrementProgress(1);
            entry = e.nextElement();
            is = new BufferedInputStream(zipfile.getInputStream(entry));
            int count;
            byte data[] = new byte[BUFFER];
            File fileDir = new File(destinationDir.getAbsolutePath() + File.separator + entry.getName());
            if (entry.isDirectory()) {
               fileDir.mkdirs();
               continue;
            } else {
               fileDir.getParentFile().mkdirs();
            }

            if (!fileDir.exists() || (fileDir.exists() && fileDir.canWrite())) {
               FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath());
               dest = new BufferedOutputStream(fos, BUFFER);
               while ((count = is.read(data, 0, BUFFER)) != -1) {
                  dest.write(data, 0, count);
               }
               dest.flush();
               dest.close();
            }
            is.close();

            statusBuffer.append("\n\t\tUNZIPPED: " + entry.getName());

            if (++countEntries >= 10 || !e.hasMoreElements()) {
               display.addText(statusBuffer.toString(), SWT.NORMAL, SWT.COLOR_BLACK, false);
               display.updateScrollBar();
               statusBuffer.delete(0, statusBuffer.length());
               countEntries = 0;
            }
            unzippedFiles.put(fileDir.getAbsolutePath(), entry.getName());

         }
      } catch (Exception ex) {
         String information =
               "ZipFile: " + (zipFile != null ? zipFile.getAbsolutePath() : "NULL") + "\n" + "DestinationDir: " + (destinationDir != null ? destinationDir.getAbsolutePath() : "NULL") + "\n" + "Entry Processed: " + (entry != null ? entry.toString() : "NULL") + "\n";
         throw new IOException(information + ex.getMessage());
      }
      return unzippedFiles;
   }

   private void incrementProgress(final int increment) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
         public void run() {
            int position = progress.getSelection();
            progress.setSelection(position + increment);
         }
      });
   }
}
