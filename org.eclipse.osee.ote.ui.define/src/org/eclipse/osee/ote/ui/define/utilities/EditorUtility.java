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
package org.eclipse.osee.ote.ui.define.utilities;

import java.io.File;
import java.net.URL;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.ote.define.jobs.RemoteResourceRequestJob;
import org.eclipse.osee.ote.ui.define.reports.HttpReportRequest;
import org.eclipse.osee.ote.ui.define.reports.output.OutputFactory;
import org.eclipse.osee.ote.ui.define.reports.output.OutputFormat;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Roberto E. Escobar
 */
public class EditorUtility {

   private EditorUtility() {
   }

   public static void openEditor(String reportId, String format) {
      try {
         OutputFormat outputFormat = OutputFormat.fromString(format);
         String url = HttpReportRequest.getUrl(reportId, outputFormat.name(), "local");
         String tempFileName = OutputFactory.getOutputFilename(outputFormat, reportId);
         switch (outputFormat) {
            case HTML:
               openAsHtml(reportId, url);
               break;
            case EXCEL:
            case PDF:
            default:
               openAsNative(tempFileName, url);
               break;
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private static void openAsHtml(String fileName, String url) throws Exception {
      IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
      IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.AS_EDITOR, fileName, fileName, "");
      browser.openURL(new URL(url));
   }

   private static void openAsNative(String fileName, final String url) throws Exception {
      Job job = new RemoteResourceRequestJob(url, fileName);
      job.addJobChangeListener(new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            org.eclipse.core.runtime.IStatus status = event.getResult();
            if (status.equals(Status.OK_STATUS) || status.getCode() == Status.OK) {
               IFile file = ((RemoteResourceRequestJob) event.getJob()).getDownloadedFile();
               if (file != null && file.exists()) {
                  Program.launch(new File(file.getLocationURI()).getAbsolutePath());
               }
            }
         }
      });
      job.schedule();
   }

}
