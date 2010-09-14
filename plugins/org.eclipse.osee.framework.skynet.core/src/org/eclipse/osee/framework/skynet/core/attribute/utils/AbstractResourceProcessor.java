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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DataStore;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractResourceProcessor {

   protected abstract URL getAcquireURL(DataStore dataStore) throws OseeCoreException;

   protected abstract URL getDeleteURL(DataStore dataStore) throws OseeCoreException;

   protected abstract URL getStorageURL(int seed, String name, String extension) throws OseeCoreException;

   public abstract String createStorageName() throws OseeCoreException;

   public void saveResource(int seed, String name, DataStore dataStore) throws OseeCoreException {
      URL url = getStorageURL(seed, name, dataStore.getExtension());
      InputStream inputStream = dataStore.getInputStream();
      try {
         URI uri = HttpProcessor.save(url, inputStream, dataStore.getContentType(), dataStore.getEncoding());
         dataStore.setLocator(uri.toASCIIString());
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
   }

   public void acquire(DataStore dataStore) throws OseeCoreException {
      URL url = getAcquireURL(dataStore);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
         AcquireResult result = HttpProcessor.acquire(url, outputStream);
         int code = result.getCode();
         if (code == HttpURLConnection.HTTP_OK) {
            dataStore.setContent(outputStream.toByteArray(), "", result.getContentType(), result.getEncoding());
         } else {
            throw new OseeDataStoreException("Error acquiring resource: [%s] - status code: [%s]; %s",
               dataStore.getLocator(), code, new String(outputStream.toByteArray()));
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public void purge(DataStore dataStore) throws OseeCoreException {
      try {
         URL url = getDeleteURL(dataStore);
         String response = HttpProcessor.delete(url);
         if (response != null && response.equals("Deleted: " + dataStore.getLocator())) {
            dataStore.clear();
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }
}