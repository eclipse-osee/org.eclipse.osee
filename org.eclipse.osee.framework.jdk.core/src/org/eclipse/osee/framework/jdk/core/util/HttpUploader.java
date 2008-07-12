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
package org.eclipse.osee.framework.jdk.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Roberto E. Escobar
 */
public class HttpUploader {
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
   private String uploadResponse;

   public HttpUploader(String urlRequest, InputStream inputStream, String dataType, String encoding) {
      this.urlRequest = urlRequest;
      this.inputStream = inputStream;
      this.dataType = dataType;
      this.encoding = encoding;
      this.remoteLocation = "";
      this.lastUploaded = "";
      this.uploadResponse = "";
   }

   private HttpURLConnection setupConnection() throws IOException {
      URL url = new URL(urlRequest);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty(CONTENT_LENGTH, Integer.toString(inputStream.available()));
      connection.setRequestProperty(CONTENT_TYPE, dataType);
      connection.setRequestProperty(CONTENT_ENCODING, encoding);
      connection.setRequestMethod("PUT");
      connection.setAllowUserInteraction(true);
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setReadTimeout(CONNECTION_READ_TIMEOUT);
      connection.setConnectTimeout(CONNECTION_TIMEOUT);
      return connection;
   }

   public String getUploadResponse() {
      return uploadResponse;
   }

   public boolean execute() throws Exception {
      HttpURLConnection connection = null;
      boolean toReturn = false;
      try {
         connection = setupConnection();
         connection.connect();

         inputStreamToOutputStream(inputStream, connection.getOutputStream());
         toReturn = handleResponse(connection);
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

   private boolean handleResponse(HttpURLConnection connection) throws Exception {
      InputStream inputStream = null;
      boolean toReturn = false;
      try {
         int responseCode = HttpURLConnection.HTTP_CLIENT_TIMEOUT;
         responseCode = connection.getResponseCode();
         if (responseCode == HttpURLConnection.HTTP_CREATED) {
            lastUploaded = connection.getHeaderField("Last-Modified");
            remoteLocation = connection.getHeaderField("Content-Location");

            inputStream = (InputStream) connection.getContent();
            this.uploadResponse = Lib.inputStreamToString(inputStream);

            toReturn = true;
         }
         if (responseCode != HttpURLConnection.HTTP_CREATED) {
            toReturn = false;
         }
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return toReturn;
   }

   private void inputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      byte[] buf = new byte[8092];
      int count = -1;
      int tracker = 0;
      while ((count = inputStream.read(buf)) != -1) {
         outputStream.write(buf, 0, count);
         tracker += count;
      }
      inputStream.close();
      outputStream.flush();
      outputStream.close();
   }
}
