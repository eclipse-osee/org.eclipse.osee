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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class ResourceProcessor {

   private ResourceProcessor() {
   }

   public static URI save(URL url, InputStream inputStream, String contentType, String encoding) throws Exception {
      URI toReturn = null;
      try {
         HttpUploader uploader = new HttpUploader(url.toString(), inputStream, contentType, encoding);
         IStatus status = uploader.execute(new NullProgressMonitor());
         if (status.getSeverity() == IStatus.OK) {
            String locator = uploader.getUploadResponse();
            if (locator == null) {
               throw new Exception(status.getMessage(), status.getException());
            } else {
               URI uri = new URI(locator);
               if (uri != null) {
                  toReturn = uri;
               }
            }
         } else {
            throw new Exception(status.getMessage(), status.getException());
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error saving resource [%s]", ex.getLocalizedMessage()), ex);
      }
      return toReturn;
   }

   public static AcquireResult acquire(URL url) throws Exception {
      AcquireResult result = new AcquireResult();
      int code = -1;
      InputStream inputStream = null;
      try {
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.connect();
         // Wait for response
         code = connection.getResponseCode();
         if (code == HttpURLConnection.HTTP_OK) {
            inputStream = (InputStream) connection.getContent();
            result.setContentType(connection.getContentType());
            result.setEncoding(connection.getContentEncoding());
            result.setData(Lib.inputStreamToBytes(inputStream));
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error acquiring resource: [%s] - status code: [%s]", url, code), ex);
      } finally {
         result.setCode(code);
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return result;
   }

   public static String delete(URL url) throws Exception {
      String response = null;
      int code = -1;
      InputStream inputStream = null;
      try {
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("DELETE");
         connection.connect();
         // Wait for response
         code = connection.getResponseCode();
         if (code == HttpURLConnection.HTTP_ACCEPTED) {
            inputStream = (InputStream) connection.getContent();
            response = Lib.inputStreamToString(inputStream);
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error deleting resource: [%s] - status code: [%s]", url, code), ex);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return response;
   }

   public static final class AcquireResult {
      private int code;
      private String encoding;
      private String contentType;
      private byte[] data;

      private AcquireResult() {
         super();
         this.code = -1;
         this.encoding = "";
         this.contentType = "";
         this.data = null;
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

      public byte[] getData() {
         return data;
      }

      private void setData(byte[] data) {
         this.data = data;
      }
   }
}
