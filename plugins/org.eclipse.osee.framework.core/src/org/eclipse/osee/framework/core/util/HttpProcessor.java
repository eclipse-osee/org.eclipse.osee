/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class HttpProcessor {

   private HttpProcessor() {
      // Static class
   }

   public static AcquireResult acquire(URL url, OutputStream outputStream, int soTimeout) throws Exception {
      AcquireResult result = new AcquireResult();
      int statusCode = -1;

      InputStream inputStream = null;
      try {
         Client client = ClientBuilder.newClient();
         WebTarget target = client.target(url.toString());
         Response response = target.request().get();
         statusCode = response.getStatus();

         result.setEncoding("UTF-8");

         result.setContentType("text/html");
         if (statusCode == 200 || statusCode == 202) {

            inputStream = response.readEntity(InputStream.class);
            Lib.inputStreamToOutputStream(inputStream, outputStream);
         } else {
            String responseString = response.readEntity(String.class);
            result.setResult(responseString);
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error acquiring resource: [%s] - status code: [%s]", url, statusCode), ex);
      } finally {
         Lib.close(inputStream);
         result.setCode(statusCode);
      }
      return result;
   }

   public static final class AcquireResult {
      private int code;
      private String encoding;
      private String contentType;
      private String result;

      private AcquireResult() {
         super();
         this.code = -1;
         this.encoding = "";
         this.contentType = "";
      }

      public boolean wasSuccessful() {
         return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_ACCEPTED;
      }

      public int getCode() {
         return code;
      }

      private void setCode(int code) {
         this.code = code;
      }

      public String getEncoding() {
         return encoding;
      }

      private void setEncoding(String encoding) {
         this.encoding = encoding;
      }

      public String getContentType() {
         return contentType;
      }

      private void setContentType(String contentType) {
         this.contentType = contentType;
      }

      public String getResult() {
         return result;
      }

      public void setResult(String result) {
         this.result = result;
      }
   }
}
