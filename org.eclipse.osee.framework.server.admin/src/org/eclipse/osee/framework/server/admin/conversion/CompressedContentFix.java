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
package org.eclipse.osee.framework.server.admin.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.resource.provider.common.resources.CompressedResourceBridge;
import org.eclipse.osee.framework.resource.provider.common.resources.Streams;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class CompressedContentFix {

   private static CompressedContentFix instance = null;

   private static final String FIND_ALL_NATIVE_CONTENT_SQL =
         "SELECT art1.art_id, art1.human_readable_id, art1.guid, attr1.uri FROM osee_attribute attr1, osee_attribute_type attyp1, osee_artifact art1 WHERE attyp1.NAME = 'Native Content' AND attyp1.attr_type_id = attr1.attr_type_id AND art1.ART_ID = attr1.ART_ID";

   public static CompressedContentFix getInstance() {
      if (instance == null) {
         instance = new CompressedContentFix();
      }
      return instance;
   }

   private volatile boolean execute;
   private volatile boolean isRunning = false;
   private volatile boolean verbose = true;

   private CompressedContentFix() {
   }

   public void executeStop(CommandInterpreter ci) {
      execute = false;
   }

   public void execute(CommandInterpreter ci) {
      if (!isRunning) {
         execute = true;
         isRunning = true;
         Thread th = new Thread(new Worker(ci));
         th.setName("CompressedContentFix");
         th.start();
      }
   }

   private class Worker implements Runnable {

      private CommandInterpreter ci;
      private IResourceManager resourceManager;
      private IResourceLocatorManager locatorManager;
      private Map<Long, String> nativeExtension;
      private Map<Long, String> nameMap;

      private Worker(CommandInterpreter cmdi) {
         ci = cmdi;
         resourceManager = Activator.getInstance().getResourceManager();
         locatorManager = Activator.getInstance().getResourceLocatorManager();
         nativeExtension = null;
         nameMap = null;
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Runnable#run()
       */
      @Override
      public void run() {
         long time = System.currentTimeMillis();
         try {
            initializeData();
            doWork(time);
         } catch (OseeDataStoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         } finally {
            clear();
         }
         long seconds = (System.currentTimeMillis() - time) / 1000;
         long leftOverSeconds = seconds % 60;
         long minutes = seconds / 60;
         long leftOverMinutes = minutes % 60;
         long hours = minutes / 60;
         ci.println(String.format("Done.  Elapsed Time = %d:%02d:%02d.", hours, leftOverMinutes, leftOverSeconds));
         isRunning = false;
      }

      private void initializeData() throws OseeDataStoreException {
         nativeExtension = Util.getArtIdMap("Extension");
         nameMap = Util.getArtIdMap("Name");
      }

      private void clear() {
         if (nativeExtension != null) {
            nativeExtension.clear();
            nativeExtension = null;
         }

         if (nameMap != null) {
            nameMap.clear();
            nameMap = null;
         }
      }

      private void processEntry(long artId, String hrid, String guid, String uri) throws Exception {
         boolean resourceExists = isResourceAvailable(uri);
         if (resourceExists) {
            IResourceLocator locator = locatorManager.getResourceLocator(uri);
            Options options = new Options();
            IResource resource = resourceManager.acquire(locator, options);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String oldEntryName = getContent(resource, outputStream);
            String extension = nativeExtension.get(artId);
            String newEntryName = generateFileName(nameMap.get(artId), hrid, extension);

            byte[] compressed = Lib.compressStream(new ByteArrayInputStream(outputStream.toByteArray()), newEntryName);
            IResource modifiedResource = new CompressedResourceBridge(compressed, locator.getLocation(), true);
            options.put(StandardOptions.Overwrite.name(), true);
            resourceManager.save(locator, modifiedResource, options);

            ci.println(String.format("hrid [%s] uri [%s] fileName[%s] newName[%s] extension[%s]", hrid, uri,
                  oldEntryName, newEntryName, extension));
         }
      }

      public String generateFileName(String name, String hrid, String fileTypeExtension) {
         StringBuilder builder = new StringBuilder();
         if (name != null && name.length() > 0) {
            try {
               if (name.length() > 60) {
                  name = name.substring(0, 60);
               }
               builder.append(URLEncoder.encode(name, "UTF-8"));
               builder.append(".");
            } catch (Exception ex) {
               // Do Nothing - this is not important
            }
         }
         builder.append(hrid);

         if (fileTypeExtension != null && fileTypeExtension.length() > 0) {
            builder.append(".");
            builder.append(fileTypeExtension);
         }
         return builder.toString();
      }

      private String getContent(IResource resource, OutputStream outputStream) throws IOException {
         String name = null;
         InputStream inputStream = null;
         try {
            inputStream = resource.getContent();
            name = Streams.decompressStream(inputStream, outputStream);
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }
         return name;
      }

      private boolean isResourceAvailable(String uri) throws Exception {
         boolean resourceExists = false;
         if (uri != null && uri.length() > 0) {
            IResourceLocator locator = locatorManager.getResourceLocator(uri);
            resourceExists = resourceManager.exists(locator);
         }
         return resourceExists;
      }

      private void doWork(long time) {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(FIND_ALL_NATIVE_CONTENT_SQL);
            int count = 0;
            while (chStmt.next() && execute) {
               long artId = chStmt.getLong("art_id");
               String hrid = chStmt.getString("human_readable_id");
               String guid = chStmt.getString("guid");
               String uri = chStmt.getString("uri");
               processEntry(artId, hrid, guid, uri);

               count++;
               if (count % 100 == 0) {
                  if (verbose) {
                     long seconds = (System.currentTimeMillis() - time) / 1000;
                     long leftOverSeconds = seconds % 60;
                     long minutes = seconds / 60;
                     long leftOverMinutes = minutes % 60;
                     long hours = minutes / 60;
                     ci.println(String.format("%d files processed, Elapsed Time = %d:%02d:%02d.", count, hours,
                           leftOverMinutes, leftOverSeconds));
                  }
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         } finally {
            chStmt.close();
         }
      }
   }
}
