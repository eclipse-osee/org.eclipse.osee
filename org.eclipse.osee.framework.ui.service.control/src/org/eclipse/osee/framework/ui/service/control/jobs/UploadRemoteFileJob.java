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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.pages.UploadPage.LabelEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class UploadRemoteFileJob extends Job {

   private ServiceLaunchingInformation serviceInfo;
   private TextDisplayHelper display;
   private ProgressBar progress;
   private Map<LabelEnum, Text> dataMap;

   public UploadRemoteFileJob(String name, ServiceLaunchingInformation serviceInfo, TextDisplayHelper display, ProgressBar progress, Map<LabelEnum, Text> dataMap) {
      super(name);
      if (serviceInfo == null) throw new IllegalArgumentException("serviceInfo can not be null");
      if (display == null) throw new IllegalArgumentException("display can not be null");
      if (progress == null) throw new IllegalArgumentException("progress can not be null");
      this.serviceInfo = serviceInfo;
      this.display = display;
      this.progress = progress;
      this.dataMap = dataMap;

      progress.setSelection(0);
      progress.setMaximum(0);
      progress.setMaximum(4);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.OK_STATUS;
      display.clear();

      incrementProgress(1);
      if (true != createDestinationFolder()) {
         incrementProgress(4);
         toReturn = Status.CANCEL_STATUS;
      } else {
         try {
            Bundle bundle = Platform.getBundle(serviceInfo.getServiceItem().getPlugin());
            URL url = bundle.getEntry(serviceInfo.getServiceItem().getZipName());
            URL resolvedURL = FileLocator.resolve(url);
            File zipLocation = new File(resolvedURL.getFile());

            display.clear();
            display.addText("\t\t ----------- Unzip -------------\n\n", SWT.BOLD, SWT.COLOR_BLACK, false);
            display.addText("\tFrom Location: ", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
            display.addText(String.format("\t%s\n", zipLocation.getAbsolutePath()), SWT.NORMAL, SWT.COLOR_BLACK, false);
            display.addText("\tTo Location: ", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
            display.addText(
                  String.format("\t%s://%s\n", serviceInfo.getSelectedHost(), serviceInfo.getUnzipLocation()),
                  SWT.NORMAL, SWT.COLOR_BLACK, false);

            incrementProgress(1);

            this.serviceInfo.getSSHConnection().uploadFiles(new String[] {zipLocation.getAbsolutePath()},
                  serviceInfo.getUnzipLocation());
            incrementProgress(1);

            String toExec =
                  String.format("cd %s\nunzip -o %s", serviceInfo.getUnzipLocation(),
                        serviceInfo.getServiceItem().getZipName());

            display.addText("\n\tUnzip Cmd: ", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
            display.addText("\t" + toExec.split("\n")[1] + "\n", SWT.NORMAL, SWT.COLOR_BLACK, false);

            String output = this.serviceInfo.getSSHConnection().executeCommandList(toExec.split("\n"));
            display.addText("\n\t" + output + "\n", SWT.NORMAL, SWT.COLOR_BLACK, false);
            incrementProgress(1);

         } catch (Exception ex) {
            display.addText(String.format("\n%s\n\n", ControlPlugin.getStackMessages(ex)), SWT.NORMAL, SWT.COLOR_RED,
                  false);
            incrementProgress(4);
            toReturn = Status.CANCEL_STATUS;
         }
      }
      if (true != toReturn.equals(Status.OK_STATUS)) {
         PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
               MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Upload Error",
                     "Upload of files to remote host failed.");
            }
         });
      }

      return toReturn;
   }

   private boolean createDestinationFolder() {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
         public void run() {
            serviceInfo.setUnzipLocation(dataMap.get(LabelEnum.Host_Upload_Location).getText());
         }
      });
      if (true != confirmRemoteHostLocation()) {
         // the requested path does not exist
         if (true != mkdir(serviceInfo.getUnzipLocation())) {
            return false;
         }
      }
      return true;
   }

   private void incrementProgress(final int increment) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
         public void run() {
            int position = progress.getSelection();
            progress.setSelection(position + increment);
         }
      });
   }

   private boolean confirmRemoteHostLocation() {
      try {
         String output =
               serviceInfo.getSSHConnection().executeCommandList(
                     new String[] {"ls -la " + serviceInfo.getUnzipLocation()});
         output = output.replaceAll("\r", "");
         Pattern p = Pattern.compile(".*?No\\ssuch\\sfile\\sor\\sdirectory.*", Pattern.DOTALL);
         Matcher m = p.matcher(output);
         if (m.matches()) {
            display.addText(output, SWT.NORMAL, SWT.COLOR_RED, false);
            return false;
         } else {
            display.addText(output, SWT.NORMAL, SWT.COLOR_BLACK, false);
            display.updateScrollBar();
         }
      } catch (IOException ex) {
         display.addText(String.format("\n%s\n\n", ControlPlugin.getStackMessages(ex)), SWT.NORMAL, SWT.COLOR_RED,
               false);
      }
      return true;
   }

   private boolean mkdir(String path) {
      try {
         String output =
               serviceInfo.getSSHConnection().executeCommandList(new String[] {"mkdir -p " + path, "ls -la " + path});
         output = output.replaceAll("\r", "");
         if (output.contains("Permission denied") || output.contains("Operation not applicable")) {
            display.addText(output.toString(), SWT.NORMAL, SWT.COLOR_RED, false);
            return false;
         } else {
            display.addText(output.toString(), SWT.NORMAL, SWT.COLOR_BLACK, false);
            display.updateScrollBar();
         }
      } catch (IOException ex) {
         display.addText(String.format("\n%s\n\n", ControlPlugin.getStackMessages(ex)), SWT.NORMAL, SWT.COLOR_RED,
               false);
      }
      return true;
   }

}
