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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * @author Roberto E. Escobar
 */
public class UploadRemoteFileJob extends Job {

   private ServiceLaunchingInformation serviceInfo;
   private TextDisplayHelper display;
   private ProgressBar progress;
   private Map<LabelEnum, Text> dataMap;
   private boolean isUploadDirCreationAllowed;

   public UploadRemoteFileJob(String name, ServiceLaunchingInformation serviceInfo, TextDisplayHelper display, ProgressBar progress, Map<LabelEnum, Text> dataMap) {
      super(name);
      if (serviceInfo == null) throw new IllegalArgumentException("serviceInfo can not be null");
      if (display == null) throw new IllegalArgumentException("display can not be null");
      if (progress == null) throw new IllegalArgumentException("progress can not be null");
      this.serviceInfo = serviceInfo;
      this.display = display;
      this.progress = progress;
      this.dataMap = dataMap;
      this.isUploadDirCreationAllowed = false;
      progress.setSelection(0);
      progress.setMaximum(0);
      progress.setMaximum(5);
   }

   private void incrementProgress(final int increment) {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            int position = progress.getSelection();
            progress.setSelection(position + increment);
         }
      });
   }

   private void displayInitMessage(File zipLocation) {
      display.clear();
      display.addText("\t\t ----------- Unzip -------------\n\n", SWT.BOLD, SWT.COLOR_BLACK, false);
      display.addText("\tFrom Location: ", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
      display.addText(String.format("\t%s\n", zipLocation.getAbsolutePath()), SWT.NORMAL, SWT.COLOR_BLACK, false);
      display.addText("\tTo Location: ", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
      display.addText(String.format("\t%s://%s\n", serviceInfo.getSelectedHost(), serviceInfo.getUnzipLocation()),
            SWT.NORMAL, SWT.COLOR_BLACK, false);
   }

   private ChannelSftp getScpConnection() throws Exception {
      display.addText("\n\tStarting SCP...", SWT.NORMAL, SWT.COLOR_BLACK, false);
      ChannelSftp sftp = this.serviceInfo.getSSHConnection().getScpConnection();
      incrementProgress(1);
      return sftp;
   }

   private void createRemotePathOrCdIntoIt(IProgressMonitor monitor, ChannelSftp sftp) throws Exception {
      if (monitor.isCanceled() != true) {
         try {
            sftp.cd(serviceInfo.getUnzipLocation());
         } catch (SftpException ex1) {
            this.isUploadDirCreationAllowed = false;
            Display.getDefault().syncExec(new Runnable() {
               public void run() {
                  Shell shell = Display.getDefault().getActiveShell();
                  isUploadDirCreationAllowed =
                        MessageDialog.openQuestion(shell, "Scp",
                              "Unable to find remote path. Would you like to create it?");
               }
            });

            if (isUploadDirCreationAllowed == true) {
               try {
                  sftp.mkdir(serviceInfo.getUnzipLocation());
               } catch (SftpException ex2) {
                  throw new Exception("Unable to create remote path.");
               }
            } else {
               throw new Exception("Unable to find remote path.");
            }

         } finally {
            incrementProgress(1);
         }
      }
   }

   private void uploadFile(IProgressMonitor monitor, ChannelSftp sftp, File fileToUpload) throws Exception {
      if (monitor.isCanceled() != true) {
         display.addText(String.format("\n\tUploading [%s]", fileToUpload.getAbsolutePath()), SWT.NORMAL,
               SWT.COLOR_BLACK, false);
         InputStream input = null;
         OutputStream output = null;
         try {
            input = new FileInputStream(fileToUpload);
            output = sftp.put(fileToUpload.getName());
            byte[] buffer = new byte[1024];
            int count = -1;
            while ((count = input.read(buffer)) != -1) {
               output.write(buffer, 0, count);
            }
            display.addText(String.format("\n\tTransferred [%s] bytes", fileToUpload.length()), SWT.NORMAL,
                  SWT.COLOR_BLACK, false);
         } catch (SftpException ex1) {
            throw new Exception("Error uploading file.");
         } finally {
            incrementProgress(1);
            if (input != null) {
               input.close();
            }
            if (output != null) {
               output.close();
            }
         }
      }
   }

   private void unzipRemoteFiles(IProgressMonitor monitor) throws Exception {
      if (monitor.isCanceled() != true) {
         String toExec =
               String.format("cd %s\nunzip -o %s", serviceInfo.getUnzipLocation(),
                     serviceInfo.getServiceItem().getZipName());

         display.addText("\n\tUnzip Cmd: ", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
         display.addText("\t" + toExec.split("\n")[1] + "\n", SWT.NORMAL, SWT.COLOR_BLACK, false);

         String output = this.serviceInfo.getSSHConnection().executeCommandList(toExec.split("\n"));
         display.addText("\n\t" + output + "\n", SWT.NORMAL, SWT.COLOR_BLACK, false);
         incrementProgress(1);
      }
   }

   public File getFile(Bundle bundle, String path) throws FileNotFoundException, IOException {
      URL url = bundle.getEntry(path);
      if (url == null) {
         throw new FileNotFoundException("Could not locate the file " + path);
      }
      try {
         url = FileLocator.toFileURL(url);
         File file = new File(url.getFile());
         return file;
      } catch (Throwable e) {
         throw new IOException("Invalid URL format for the URL " + url.toString(), e);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.OK_STATUS;
      ChannelSftp sftp = null;
      try {
         Display.getDefault().syncExec(new Runnable() {
            public void run() {
               serviceInfo.setUnzipLocation(dataMap.get(LabelEnum.Host_Upload_Location).getText());
            }
         });
         Bundle bundle = Platform.getBundle(serviceInfo.getServiceItem().getPlugin());
         File zipLocation = getFile(bundle, serviceInfo.getServiceItem().getZipName());

         displayInitMessage(zipLocation);

         sftp = getScpConnection();

         createRemotePathOrCdIntoIt(monitor, sftp);

         uploadFile(monitor, sftp, zipLocation);

         unzipRemoteFiles(monitor);

      } catch (Exception ex) {
         display.addText(String.format("\n\t%s\n\n", ex.getLocalizedMessage()), SWT.NORMAL, SWT.COLOR_RED, false);
         toReturn = new Status(Status.ERROR, ControlPlugin.PLUGIN_PREFERENCE_SCOPE, "Error during upload.", ex);
      } finally {
         incrementProgress(5);
         if (sftp != null) {
            sftp.exit();
            sftp.disconnect();
         }
      }

      //      incrementProgress(1);
      //      if (true != createDestinationFolder()) {
      //         incrementProgress(4);
      //         toReturn = Status.CANCEL_STATUS;
      //      } else {

      //      if (true != toReturn.equals(Status.OK_STATUS)) {
      //         PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
      //            public void run() {
      //               MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Upload Error",
      //                     "Upload of files to remote host failed.");
      //            }
      //         });
      //      }
      return toReturn;
   }
}
