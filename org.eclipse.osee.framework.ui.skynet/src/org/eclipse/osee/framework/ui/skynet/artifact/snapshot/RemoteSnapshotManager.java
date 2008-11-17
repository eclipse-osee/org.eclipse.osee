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
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Roberto E. Escobar
 */
class RemoteSnapshotManager {
   private static final String GAMMA = "gamma";
   private static final String GUID = "guid";
   private static final String CREATED_ON = "createdOn";
   private static final String METADATA = "metadata.xml";
   private static final String SNAPSHOT_BODY = "snapshot.html";
   private static final String BINARY_DATA_PREFIX = "bin.data.";

   RemoteSnapshotManager() {
   }

   public ArtifactSnapshot getSnapshot(String guid, String gammaId) {
      ArtifactSnapshot toReturn = null;
      ObjectInputStream objectInputStream = null;
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result = HttpProcessor.acquire(getAcquireURL(guid, gammaId), outputStream);
         if (result.wasSuccessful()) {
            toReturn = loadFromStream(new ByteArrayInputStream(outputStream.toByteArray()));
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      } finally {
         if (objectInputStream != null) {
            try {
               objectInputStream.close();
            } catch (IOException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      }
      return toReturn;
   }

   public void delete(String guid, String gammaId) {
      try {
         URL url = getDeleteURL(guid, gammaId);
         String response = HttpProcessor.delete(url);
         if (response != null) {
            OseeLog.log(SkynetGuiPlugin.class, Level.INFO, String.format("[%s]", response));
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public void save(ArtifactSnapshot snapshot) throws Exception {
      InputStream inputStream = null;
      try {
         inputStream = new ByteArrayInputStream(compressSnapshot(snapshot));
         URL url = getStorageURL(snapshot);
         HttpProcessor.save(url, inputStream, "application/zip", "ISO-8859-1");
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   private byte[] compressSnapshot(ArtifactSnapshot snapshot) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream out = null;
      try {
         out = new ZipOutputStream(bos);

         ByteArrayOutputStream metaOut = new ByteArrayOutputStream();
         Properties properties = new Properties();
         properties.put(GAMMA, Long.toString(snapshot.getGamma()));
         properties.put(GUID, snapshot.getGuid());
         Timestamp creationDate = snapshot.getCreatedOn();
         if (creationDate != null) {
            try {
               properties.put(CREATED_ON, Long.toString(creationDate.getTime()));
            } catch (Exception ex) {
               OseeLog.log(SkynetActivator.class, Level.WARNING, "Error storing artifact creation date for snapshot.",
                     ex);
            }
         }
         properties.storeToXML(metaOut, "UTF-8");

         addZipEntry(out, METADATA, new ByteArrayInputStream(metaOut.toByteArray()));
         addZipEntry(out, SNAPSHOT_BODY, new ByteArrayInputStream(snapshot.getRenderedData().getBytes("UTF-8")));

         for (String key : snapshot.getBinaryDataKeys()) {
            addZipEntry(out, BINARY_DATA_PREFIX + key, new ByteArrayInputStream(snapshot.getBinaryData(key)));
         }
      } finally {
         if (out != null) {
            out.close();
         }
      }
      return bos.toByteArray();
   }

   private void addZipEntry(ZipOutputStream out, String entryName, InputStream inputStream) throws IOException {
      out.putNextEntry(new ZipEntry(entryName));
      try {
         byte[] buf = new byte[1024];
         int count = -1;
         while ((count = inputStream.read(buf)) > 0) {
            out.write(buf, 0, count);
         }
      } finally {
         out.closeEntry();
      }
   }

   private ArtifactSnapshot loadFromStream(InputStream inputStream) throws OseeCoreException {
      ArtifactSnapshot toReturn = null;
      ZipInputStream zipInputStream = null;
      try {
         zipInputStream = new ZipInputStream(inputStream);

         Properties properties = new Properties();
         String renderedData = null;
         Map<String, byte[]> binData = new HashMap<String, byte[]>();

         ZipEntry entry = null;
         while ((entry = zipInputStream.getNextEntry()) != null) {
            String zipEntryName = entry.getName();
            if (zipEntryName.equals(METADATA)) {
               properties.loadFromXML(new ByteArrayInputStream(Lib.inputStreamToString(zipInputStream).getBytes()));
            } else if (zipEntryName.equals(SNAPSHOT_BODY)) {
               renderedData = Lib.inputStreamToString(zipInputStream);
            } else {
               ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
               Lib.inputStreamToOutputStream(zipInputStream, outputStream);
               binData.put(zipEntryName.replace(BINARY_DATA_PREFIX, ""), outputStream.toByteArray());
            }
         }

         if (!properties.isEmpty() && renderedData != null) {
            String gamma = properties.getProperty(GAMMA);
            String guid = properties.getProperty(GUID);
            String createdOn = properties.getProperty(CREATED_ON);
            Timestamp creationDate = null;
            if (Strings.isValid(createdOn)) {
               try {
                  creationDate = new Timestamp(Long.parseLong(createdOn));
               } catch (Exception ex) {
                  OseeLog.log(SkynetActivator.class, Level.WARNING,
                        "Error obtaining artifact creation date from snapshot.", ex);
               }
            }
            toReturn = new ArtifactSnapshot(guid, Long.parseLong(gamma), creationDate);
            toReturn.setRenderedData(renderedData);
            for (String key : binData.keySet()) {
               toReturn.addBinaryData(key, binData.get(key));
            }
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      } finally {
         if (zipInputStream != null) {
            try {
               zipInputStream.close();
            } catch (IOException ex) {
               throw new OseeWrappedException(ex);
            }
         }
      }
      return toReturn;
   }

   private URL getAcquireURL(String guid, String gammaId) throws Exception {
      return generatePathURL(guid, gammaId);
   }

   private URL getDeleteURL(String guid, String gammaId) throws Exception {
      return generatePathURL(guid, gammaId);
   }

   private URL getStorageURL(ArtifactSnapshot snapshot) throws Exception {
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("sessionId", ClientSessionManager.getSessionId());
      parameterMap.put("protocol", "snapshot");
      parameterMap.put("seed", snapshot.getGuid());
      parameterMap.put("name", Long.toString(snapshot.getGamma()));
      parameterMap.put("extension", "zip");
      parameterMap.put("is.overwrite.allowed", Boolean.toString(true));
      String urlString =
            HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT, parameterMap);
      return new URL(urlString);
   }

   private URL generatePathURL(String guid, String gammaId) throws Exception {
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("sessionId", ClientSessionManager.getSessionId());
      parameterMap.put("uri", generateUri(guid, gammaId));
      String urlString =
            HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT, parameterMap);
      return new URL(urlString);
   }

   private String generateUri(String guid, String gammaId) throws OseeWrappedException {
      StringBuilder builder = new StringBuilder("snapshot://");
      char[] buffer = new char[3];
      int cnt = -1;
      Reader in = new StringReader(guid);
      try {
         while ((cnt = in.read(buffer)) != -1) {
            builder.append(buffer, 0, cnt);
            builder.append("/");
         }
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
      builder.append(gammaId);
      builder.append(".zip");
      return builder.toString();
   }
}
