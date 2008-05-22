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
package org.eclipse.osee.framework.ui.skynet.artifact.snapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor.AcquireResult;

/**
 * @author Roberto E. Escobar
 */
class RemoteSnapshotManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RemoteSnapshotManager.class);

   protected RemoteSnapshotManager() {
   }

   public ArtifactSnapshot getSnapshot(Pair<String, String> key) {
      ArtifactSnapshot toReturn = null;
      ObjectInputStream objectInputStream = null;
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result = HttpProcessor.acquire(getAcquireURL(key), outputStream);
         if (result.wasSuccessful()) {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
            Object object = objectInputStream.readObject();

            toReturn = (ArtifactSnapshot) object;
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } finally {
         if (objectInputStream != null) {
            try {
               objectInputStream.close();
            } catch (IOException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
         }
      }
      return toReturn;
   }

   public void delete(Pair<String, String> key) {
      try {
         URL url = getDeleteURL(key);
         String response = HttpProcessor.delete(url);
         if (response != null) {
            logger.log(Level.INFO, String.format("[%s]", response));
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public void save(ArtifactSnapshot snapshot) throws Exception {
      InputStream inputStream = null;
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
         outputStream.writeObject(snapshot);
         outputStream.close();

         inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
         URL url = getStorageURL(snapshot);
         HttpProcessor.save(url, inputStream, "application", "ISO-8859-1");
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   private URL getAcquireURL(Pair<String, String> key) throws Exception {
      return generatePathURL(key);
   }

   private URL getDeleteURL(Pair<String, String> key) throws Exception {
      return generatePathURL(key);
   }

   private URL getStorageURL(ArtifactSnapshot snapshot) throws Exception {
      String name = Integer.toString(snapshot.getGamma());
      String seed = snapshot.getNamespace();
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("protocol", "snapshot");
      parameterMap.put("seed", seed);
      parameterMap.put("name", name);
      String extension = "class";
      if (Strings.isValid(extension) != false) {
         parameterMap.put("extension", extension);
      }
      parameterMap.put("is.overwrite.allowed", Boolean.toString(true));
      String urlString = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("resource", parameterMap);
      return new URL(urlString);
   }

   private URL generatePathURL(Pair<String, String> key) throws Exception {
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("uri", generateUriFromKey(key));
      String urlString = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("resource", parameterMap);
      return new URL(urlString);
   }

   private String generateUriFromKey(Pair<String, String> key) throws IOException {
      StringBuilder builder = new StringBuilder("snapshot://");
      String seed = key.getKey();
      String[] values = seed.split("BRANCH");
      String struct = values[0];
      char[] buffer = new char[3];
      int cnt = -1;
      Reader in = new StringReader(struct);
      while ((cnt = in.read(buffer)) != -1) {
         builder.append(buffer, 0, cnt);
         builder.append("/");
      }

      if (values.length == 2) {
         builder.append(values[1]);
         builder.append("/");
      }
      builder.append(key.getValue());
      builder.append(".class");
      return builder.toString();
   }
}
