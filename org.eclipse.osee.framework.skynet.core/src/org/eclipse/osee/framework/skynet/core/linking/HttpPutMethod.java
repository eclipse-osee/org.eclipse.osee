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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Roberto E. Escobar
 */
final class HttpPutMethod implements IHttpMethod {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(HttpPutMethod.class);
   private static final DateFormat dateFormat = new SimpleDateFormat("EEE',' dd MMM yyyy HH:mm:ss z");
   private static final String FILENAME_KEY = "filename";

   private HttpFileHandler fileHandler;

   protected HttpPutMethod() {
      String rootPath = OseeProperties.getInstance().getRemoteHttpServerUploadPath();
      fileHandler = new HttpFileHandler(rootPath);
   }

   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      int result = HttpURLConnection.HTTP_BAD_REQUEST;
      File fileReceived = null;
      try {
         String fileName = httpRequest.getParameter(FILENAME_KEY);
         Pair<File, Integer> resultPair = fileHandler.getStorageLocation(fileName);
         result = resultPair.getValue();
         if (result == HttpURLConnection.HTTP_CREATED) {
            fileReceived = resultPair.getKey();
            result = receiveData(httpRequest, fileReceived);
            Date lastModified = new Date(fileReceived.lastModified());
            httpResponse.setReponseHeader("Content-Location", fileHandler.getLocation(fileReceived));
            httpResponse.setReponseHeader("Last-Modified", dateFormat.format(lastModified));
         }
      } catch (Exception ex) {
         result = HttpURLConnection.HTTP_INTERNAL_ERROR;
         String fileName = fileReceived != null ? fileReceived.getAbsolutePath() : "";
         logger.log(Level.SEVERE, String.format("[%s]: File - [%s]", HttpResponse.getStatus(result), fileName), ex);
      } finally {
         if (result != HttpURLConnection.HTTP_CREATED && fileReceived != null) {
            fileReceived.delete();
         }
         try {
            httpResponse.setContentType("text/plain");
            httpResponse.sendResponseHeaders(result, 0);
            httpResponse.getOutputStream().flush();
         } catch (IOException ex1) {
            logger.log(Level.SEVERE, "Error sending response", ex1);
         }
      }
   }

   private int receiveData(HttpRequest httpRequest, File destination) throws Exception {
      int result = HttpURLConnection.HTTP_LENGTH_REQUIRED;
      String headerEntry = httpRequest.getHttpHeaderEntry("Content-Length");
      if (Strings.isValid(headerEntry)) {
         int totalBytes = Integer.parseInt(headerEntry);
         result = fileHandler.receivedUpload(destination, totalBytes, httpRequest);
      }
      return result;
   }
}
