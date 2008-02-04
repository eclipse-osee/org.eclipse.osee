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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.linking.HttpRequest.HttpMethod;

/**
 * @author Roberto E. Escobar
 */
public class HttpUploader {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(HttpUploader.class);
   private static final int CONNECTION_TIMEOUT = 120000;
   private static final int CONNECTION_READ_TIMEOUT = 1000 * 60 * 10;
   private static final String CONTENT_LENGTH = "Content-Length";
   private static final String CONTENT_TYPE = "Content-Type";
   private static final String CONTENT_ENCODING = "Content-Encoding";

   private String urlRequest;
   private InputStream inputStream;
   private String dataType;
   private String encoding;
   private String remoteLocation;
   private String lastUploaded;

   public HttpUploader(String urlRequest, InputStream inputStream, String dataType, String encoding) {
      this.urlRequest = urlRequest;
      this.inputStream = inputStream;
      this.dataType = dataType;
      this.encoding = encoding;
      this.remoteLocation = "";
      this.lastUploaded = "";
   }

   private HttpURLConnection setupConnection() throws Exception {
      URL url = new URL(urlRequest);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty(CONTENT_LENGTH, Integer.toString(inputStream.available()));
      connection.setRequestProperty(CONTENT_TYPE, dataType);
      connection.setRequestProperty(CONTENT_ENCODING, encoding);
      connection.setRequestMethod(HttpMethod.PUT.name());
      connection.setAllowUserInteraction(true);
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setReadTimeout(CONNECTION_READ_TIMEOUT);
      connection.setConnectTimeout(CONNECTION_TIMEOUT);
      return connection;
   }

   public IStatus execute(IProgressMonitor monitor) throws Exception {
      IStatus toReturn = Status.CANCEL_STATUS;
      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      HttpURLConnection connection = null;
      try {
         monitor.subTask(String.format("Uploading to server: [%s] bytes", inputStream.available()));
         connection = setupConnection();
         connection.connect();

         if (monitor.isCanceled() != true) {
            sendInputStream(monitor, inputStream, connection.getOutputStream());
            if (monitor.isCanceled() != true) {
               toReturn = handleResponse(connection);
            }
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error uploading to server: [%s]", urlRequest), ex);
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
      return toReturn;
   }

   public String getDateUploaded() {
      return lastUploaded;
   }

   public String getRemoteLocation() {
      return remoteLocation;
   }

   private IStatus handleResponse(HttpURLConnection connection) throws Exception {
      IStatus toReturn = Status.CANCEL_STATUS;
      int response = HttpURLConnection.HTTP_CLIENT_TIMEOUT;
      response = connection.getResponseCode();
      if (response == HttpURLConnection.HTTP_CREATED) {
         lastUploaded = connection.getHeaderField("Last-Modified");
         remoteLocation = connection.getHeaderField("Content-Location");
         toReturn = new Status(Status.OK, SkynetActivator.PLUGIN_ID, HttpResponse.getStatus(response));
      }
      if (response != HttpURLConnection.HTTP_CREATED) {
         toReturn = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, HttpResponse.getStatus(response));
      }
      return toReturn;
   }

   private void sendInputStream(IProgressMonitor monitor, InputStream inputStream, OutputStream outputStream) throws IOException {
      byte[] buf = new byte[8092];
      int total = inputStream.available();
      int count = -1;
      int tracker = 0;
      while ((count = inputStream.read(buf)) != -1) {
         monitor.subTask(String.format("Uploading: [%s of %s]", tracker, total));
         outputStream.write(buf, 0, count);
         tracker += count;
         if (monitor.isCanceled() == true) {
            break;
         }
      }
      logger.log(Level.INFO, String.format("Uploaded: [%s of %s]", tracker, total));
      inputStream.close();
      outputStream.flush();
      outputStream.close();
   }
}
