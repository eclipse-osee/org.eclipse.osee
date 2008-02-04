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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Roberto E. Escobar
 */
public class HttpDeleteMethod implements IHttpMethod {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(HttpDeleteMethod.class);

   private static final String FILENAME_KEY = "filename";

   protected HttpDeleteMethod() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpMethod#processRequest(org.eclipse.osee.framework.skynet.core.linking.HttpRequest, org.eclipse.osee.framework.skynet.core.linking.HttpResponse)
    */
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      String toDelete = httpRequest.getParameter(FILENAME_KEY);

      // TODO: need some kind of authentication here ?
      logger.log(Level.INFO, String.format("Request to Delete: [%s] Received", toDelete));
      int result = HttpURLConnection.HTTP_UNAVAILABLE;
      try {
         String rootPath = OseeProperties.getInstance().getRemoteHttpServerUploadPath();
         if (Strings.isValid(rootPath) != false) {
            File file = new File(rootPath + File.separator + toDelete);
            if (file != null && file.exists()) {
               if (file.isFile() && file.canWrite()) {
                  if (file.delete() == true) {
                     logger.log(Level.INFO, String.format("Successfully deleted: [%s]", toDelete));
                     result = HttpURLConnection.HTTP_ACCEPTED;
                  } else {
                     result = HttpURLConnection.HTTP_BAD_REQUEST;
                  }
               } else {
                  result = HttpURLConnection.HTTP_FORBIDDEN;
               }
            } else {
               result = HttpURLConnection.HTTP_NOT_FOUND;
            }
         } else {
            result = HttpURLConnection.HTTP_NOT_FOUND;
         }
      } catch (Exception ex) {
         result = HttpURLConnection.HTTP_INTERNAL_ERROR;
         logger.log(Level.SEVERE, String.format("Error deleting: [%s]", toDelete), ex);
      } finally {
         try {
            httpResponse.sendResponseHeaders(result, 0);
            logger.log(Level.INFO, String.format("Delete Response for: [%s] was [%s]", toDelete, result));
         } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error sending delete response.", ex);
         }
      }
   }
}
