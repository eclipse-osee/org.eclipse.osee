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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DataStore;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor.AcquireResult;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractResourceProcessor {

   protected abstract URL getAcquireURL(DataStore dataStore) throws OseeDataStoreException;

   protected abstract URL getDeleteURL(DataStore dataStore) throws OseeDataStoreException;

   protected abstract URL getStorageURL(DataStore dataStore) throws OseeDataStoreException;

   public void saveResource(DataStore dataStore) throws OseeDataStoreException {
      try {
         InputStream inputStream = null;
         try {
            URL url = getStorageURL(dataStore);
            inputStream = dataStore.getInputStream();
            URI uri = HttpProcessor.save(url, inputStream, dataStore.getContentType(), dataStore.getEncoding());
            if (uri != null) {
               dataStore.setLocator(uri.toASCIIString());
            }
         } catch (Exception ex) {
            throw new OseeDataStoreException("Error saving resource", ex);
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }
      } catch (Exception ex) {
         throw new OseeDataStoreException("Error saving resource", ex);
      }
   }

   public void acquire(DataStore dataStore) throws OseeDataStoreException {
      int code = -1;
      try {
         URL url = getAcquireURL(dataStore);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result = HttpProcessor.acquire(url, outputStream);
         code = result.getCode();
         if (code == HttpURLConnection.HTTP_OK) {
            dataStore.setContent(outputStream.toByteArray(), "", result.getContentType(), result.getEncoding());
         }
      } catch (Exception ex) {
         throw new OseeDataStoreException(String.format("Error acquiring resource: [%s] - status code: [%s]",
               dataStore.getLocator(), code), ex);
      }
   }

   public void purge(DataStore dataStore) throws OseeDataStoreException {
      int code = -1;
      try {
         URL url = getDeleteURL(dataStore);
         String response = HttpProcessor.delete(url);
         if (response != null && response.equals("Deleted: " + dataStore.getLocator())) {
            dataStore.clear();
         }
      } catch (Exception ex) {
         throw new OseeDataStoreException(String.format("Error deleting resource: [%s] - status code: [%s]",
               dataStore.getLocator(), code), ex);
      }
   }
}
