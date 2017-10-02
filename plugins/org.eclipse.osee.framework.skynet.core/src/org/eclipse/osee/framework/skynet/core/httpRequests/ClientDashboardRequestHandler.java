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
package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpRequest;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.core.client.server.IHttpServerRequest;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * Responds to requests by server for information about client
 * 
 * @author Donald G. Dunne
 */
public class ClientDashboardRequestHandler implements IHttpServerRequest {

   private enum RequestCmd {
      log,
      info,
      pingId
   }

   @Override
   public String getRequestType() {
      return "osee/request";
   }

   @Override
   public void processRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) {
      final String cmd = httpRequest.getParameter("cmd");
      try {
         if (Strings.isValid(cmd)) {
            RequestCmd requestCmd = RequestCmd.valueOf(cmd);
            switch (requestCmd) {
               case log:
                  sendLog(httpRequest, httpResponse);
                  break;
               case info:
                  sendResults(getInfoString(), httpRequest, httpResponse);
                  break;
               case pingId:
                  sendResults(ClientSessionManager.getSession().getId(), httpRequest, httpResponse);
                  break;
               default:
                  break;
            }
         } else {
            httpResponse.outputStandardError(HttpURLConnection.HTTP_BAD_REQUEST,
               String.format("Unable to process request: [%s]", httpRequest.getRawRequest()));
         }
      } catch (Exception ex) {
         httpResponse.outputStandardError(HttpURLConnection.HTTP_BAD_REQUEST, String.format(
            "Unable to process request: [%s]", "Exception processing request: " + ex.getLocalizedMessage()));
      }
   }

   private String getInfoString() {
      StringBuffer sb = new StringBuffer(1000);
      sb.append("\nName: [" + UserManager.getUser().getName() + "]\n");
      sb.append(ClientSessionManager.getSession().toString().replaceAll("] ", "]\n"));
      sb.append("\nOSEE Data Path: [" + OseeData.getPath() + "]");
      sb.append("\nInstallation Location: [" + Platform.getInstallLocation().getURL() + "]");
      for (IHealthStatus status : OseeLog.getStatus()) {
         sb.append("\n" + status.getSourceName() + ": [" + status.getMessage() + "]");
      }
      sb.append("\nRemote Event Service Connected: [" + OseeEventManager.isEventManagerConnected() + "]");
      return sb.toString();
   }

   private List<File> getLogFiles() {
      List<File> files = new ArrayList<>();

      File file = Platform.getLogFileLocation().removeFileExtension().toFile();
      file = new File(file, ".bak_0.log");
      if (file.exists()) {
         files.add(file);
      }

      file = Platform.getLogFileLocation().toFile();
      if (file != null && file.exists()) {
         files.add(file);
      }

      return files;
   }

   private void sendLog(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
      List<File> files = getLogFiles();
      StringBuffer sb = new StringBuffer(getInfoString() + "\n");
      if (files.isEmpty()) {
         sb.append("No [.log] file found");
         sendResults(sb.toString(), httpRequest, httpResponse);
      } else {
         try {
            sb.append(
               "\n------------------------------------------\n-- Log File Contents (oldest at bottom) --\n------------------------------------------\n");
            int length = sb.length();
            for (File file : files) {
               length += file.length();
            }
            //            String name = ".log";
            httpResponse.setContentType("text/plain");
            httpResponse.setContentEncoding("UTF-8");
            //            httpResponse.setContentDisposition(String.format("attachment; filename=%s", ".log.zip"));
            httpResponse.sendResponseHeaders(HttpURLConnection.HTTP_OK, length);

            OutputStream outputStream = null;
            try {
               outputStream = new BufferedOutputStream(httpResponse.getOutputStream());
               //               outputStream.putNextEntry(new ZipEntry(name));

               Lib.inputStreamToOutputStream(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")), outputStream);
               for (File file : files) {
                  FileInputStream inputStream = new FileInputStream(file);
                  try {
                     Lib.inputStreamToOutputStream(inputStream, outputStream);
                  } finally {
                     inputStream.close();
                  }
               }
            } finally {
               if (outputStream != null) {
                  //                  outputStream.closeEntry();
                  outputStream.close();
               }
            }
         } catch (Exception ex) {
            OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error processing request for [%s]",
               httpRequest.toString());
            httpResponse.getPrintStream().println(Lib.exceptionToString(ex));
         } finally {
            httpResponse.getOutputStream().flush();
            httpResponse.getOutputStream().close();
         }
      }
   }

   private void sendResults(String results, HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
      try {
         httpResponse.setContentEncoding("UTF-8");
         httpResponse.setContentType("text/plain");
         httpResponse.sendResponseHeaders(HttpURLConnection.HTTP_OK, results.length());
         httpResponse.getPrintStream().println(results);
      } catch (Exception ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error processing request for [%s]", httpRequest.toString());
         httpResponse.getPrintStream().println(Lib.exceptionToString(ex));
      } finally {
         httpResponse.getOutputStream().flush();
         httpResponse.getOutputStream().close();
      }
   }
}
