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
package org.eclipse.osee.framework.skynet.core.linking;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.linking.HttpRequest.HttpMethod;

/**
 * @author Roberto E. Escobar
 */
public class HttpDelete {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(HttpDelete.class);
   private static final int CONNECTION_TIMEOUT = 120000;
   private static final int CONNECTION_READ_TIMEOUT = 1000 * 60 * 10;

   private String fileName;

   public HttpDelete(String fileName) {
      this.fileName = fileName;
   }

   private HttpURLConnection setupConnection(String urlRequest) throws Exception {
      URL url = new URL(urlRequest);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(HttpMethod.DELETE.name());
      connection.setAllowUserInteraction(true);
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setReadTimeout(CONNECTION_READ_TIMEOUT);
      connection.setConnectTimeout(CONNECTION_TIMEOUT);
      return connection;
   }

   private String getUrlRequest(String fileName) {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("filename", fileName);
      return HttpUrlBuilder.getInstance().getUrlForRemoteSkynetHttpServer("DELETE", parameters);
   }

   private IStatus handleResponse(HttpURLConnection connection) throws Exception {
      IStatus toReturn = Status.CANCEL_STATUS;
      int response = HttpURLConnection.HTTP_CLIENT_TIMEOUT;
      response = connection.getResponseCode();
      if (response == HttpURLConnection.HTTP_ACCEPTED) {
         toReturn = new Status(Status.OK, SkynetActivator.PLUGIN_ID, HttpResponse.getStatus(response));
      } else {
         toReturn = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, HttpResponse.getStatus(response));
      }
      return toReturn;
   }

   public IStatus execute(IProgressMonitor monitor) throws Exception {
      IStatus toReturn = Status.CANCEL_STATUS;
      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 3);
      String urlRequest = getUrlRequest(fileName);
      HttpURLConnection connection = null;
      try {
         subMonitor.beginTask(String.format("Delete Remote File: [%s]", fileName), 3);
         connection = setupConnection(urlRequest);
         subMonitor.worked(1);
         connection.connect();
         subMonitor.worked(2);
         toReturn = handleResponse(connection);
         subMonitor.worked(3);
      } catch (Exception ex) {
         String message = String.format("Error deleting remote file: [%s]", urlRequest);
         logger.log(Level.SEVERE, message, ex);
         throw new Exception(message, ex);
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
         subMonitor.done();
      }
      return toReturn;
   }
}
