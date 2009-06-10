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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.server.HttpRequest;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.core.client.server.IHttpServerRequest;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Donald G. Dunne
 */
public class ClientRequestHandler implements IHttpServerRequest {

   private enum RequestCmd {
      exceptions, info
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
               case exceptions:
                  displayResults(getExceptionString(), httpRequest, httpResponse);
                  break;
               case info:
                  displayResults(getInfoString(), httpRequest, httpResponse);
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
      return "info...";
   }

   private String getExceptionString() throws OseeCoreException {
      return "exceptions...";
   }

   private void displayResults(String results, HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
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
