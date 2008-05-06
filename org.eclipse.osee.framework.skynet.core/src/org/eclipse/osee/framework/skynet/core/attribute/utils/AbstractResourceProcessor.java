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
package org.eclipse.osee.framework.skynet.core.attribute.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DataStore;
import org.eclipse.osee.framework.skynet.core.linking.HttpUploader;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractResourceProcessor {

   protected abstract URL getAcquireURL(DataStore dataStore) throws Exception;

   protected abstract URL getDeleteURL(DataStore dataStore) throws Exception;

   protected abstract URL getStorageURL(DataStore dataStore) throws Exception;

   public void saveResource(DataStore dataStore) throws Exception {
      InputStream inputStream = null;
      try {
         URL url = getStorageURL(dataStore);
         inputStream = dataStore.getInputStream();
         HttpUploader uploader =
               new HttpUploader(url.toString(), inputStream, dataStore.getContentType(), dataStore.getEncoding());
         IStatus status = uploader.execute(new NullProgressMonitor());
         if (status.getSeverity() == IStatus.OK) {
            String locator = uploader.getUploadResponse();
            if (locator == null) {
               throw new Exception(status.getMessage(), status.getException());
            } else {
               URI uri = new URI(locator);
               if (uri != null) {
                  dataStore.setLocator(uri.toASCIIString());
               }
            }
         } else {
            throw new Exception(status.getMessage(), status.getException());
         }
      } catch (Exception ex) {
         throw new Exception("Error saving resource", ex);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   public void acquire(DataStore dataStore) throws Exception {
      int code = -1;
      InputStream inputStream = null;
      try {
         URL url = getAcquireURL(dataStore);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.connect();
         // Wait for response
         code = connection.getResponseCode();
         if (code == HttpURLConnection.HTTP_OK) {
            inputStream = (InputStream) connection.getContent();
            byte[] bytes = Lib.inputStreamToBytes(inputStream);
            dataStore.setContent(bytes, "", connection.getContentType(), connection.getContentEncoding());
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error acquiring resource: [%s] - status code: [%s]",
               dataStore.getLocator(), code), ex);
      } finally {
         if (inputStream == null) {
            inputStream.close();
         }
      }
   }

   public void purge(DataStore dataStore) throws Exception {
      int code = -1;
      InputStream inputStream = null;
      try {
         URL url = getDeleteURL(dataStore);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("DELETE");
         connection.connect();
         // Wait for response
         code = connection.getResponseCode();
         if (code == HttpURLConnection.HTTP_ACCEPTED) {
            inputStream = (InputStream) connection.getContent();
            String response = Lib.inputStreamToString(inputStream);
            if (response != null && response.equals("Deleted: " + dataStore.getLocator())) {
               dataStore.clear();
            }
         }
      } catch (Exception ex) {
         throw new Exception(String.format("Error deleting resource: [%s] - status code: [%s]", dataStore.getLocator(),
               code), ex);
      } finally {
         if (inputStream == null) {
            inputStream.close();
         }
      }
   }
}
