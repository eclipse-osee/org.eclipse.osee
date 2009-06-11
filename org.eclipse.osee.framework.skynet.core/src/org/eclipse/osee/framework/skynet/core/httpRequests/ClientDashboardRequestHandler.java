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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpRequest;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.core.client.server.IHttpServerRequest;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;

/**
 * Responds to requests by server for information about client
 * 
 * @author Donald G. Dunne
 */
public class ClientDashboardRequestHandler implements IHttpServerRequest {

   private enum RequestCmd {
      log, info, ping
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.client.server.IHttpServerRequest#getRequestType()
    */
   @Override
   public String getRequestType() {
      return "osee/request";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.client.server.IHttpServerRequest#processRequest(org.eclipse.osee.framework.core.client.server.HttpRequest, org.eclipse.osee.framework.core.client.server.HttpResponse)
    */
   @Override
   public void processRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) {
      final String cmd = httpRequest.getParameter("cmd");
      try {
         if (Strings.isValid(cmd)) {
            RequestCmd requestCmd = RequestCmd.valueOf(cmd);
            switch (requestCmd) {
               case log:
                  sendResults(getLogString(), httpRequest, httpResponse);
                  break;
               case info:
                  sendResults(getInfoString(), httpRequest, httpResponse);
                  break;
               case ping:
                  sendResults("ping", httpRequest, httpResponse);
                  break;
               default:
                  break;
            }
         } else {
            httpResponse.outputStandardError(HttpURLConnection.HTTP_BAD_REQUEST, String.format(
                  "Unable to process request: [%s]", httpRequest.getRawRequest()));
         }
      } catch (Exception ex) {
         httpResponse.outputStandardError(HttpURLConnection.HTTP_BAD_REQUEST, String.format(
               "Unable to process request: [%s]", "Exception processing request: " + ex.getLocalizedMessage()));
      }
   }

   private String getInfoString() throws OseeCoreException {
      StringBuffer sb = new StringBuffer(1000);
      sb.append("\nName: [" + UserManager.getUser().getName() + "]\n");
      sb.append(ClientSessionManager.getSession().toString().replaceAll("] ", "]\n"));
      sb.append("\nWorkpace: [" + AWorkspace.getWorkspacePath() + "]");
      sb.append("\nInstallation Location: [" + Platform.getInstallLocation().getURL() + "]");
      for (IHealthStatus status : OseeLog.getStatus()) {
         sb.append("\n" + status.getSourceName() + ": [" + status.getMessage() + "]");
      }
      sb.append("\nRemote Event Service: [" + RemoteEventManager.isConnected() + "]");
      return sb.toString();
   }

   private String getLogString() throws Exception {
      StringBuffer sb = new StringBuffer(getInfoString() + "\n");
      // Add log file
      File file = Platform.getLogFileLocation().toFile();
      if (file == null) {
         sb.append("No .log file found");
      }
      sb.append("\nLog File Contents:\n--------------------------------\n" + AFile.readFile(file));
      // Add first backup log file
      file = AWorkspace.getWorkspaceFile(".metadata/.bak_0.log");
      if (file != null && file.exists()) {
         sb.append("\n" + AFile.readFile(file));
      }
      return sb.toString();
   }

   private void sendResults(String results, HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
      try {
         httpResponse.getPrintStream().println(results);
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, String.format("Error processing request for [%s]",
               httpRequest.toString()), ex);
         httpResponse.getPrintStream().println(Lib.exceptionToString(ex));
      } finally {
         httpResponse.getOutputStream().flush();
         httpResponse.getOutputStream().close();
      }
   }
}
