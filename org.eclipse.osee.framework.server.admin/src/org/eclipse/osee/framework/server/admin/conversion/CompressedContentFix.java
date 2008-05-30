/*
 * Created on May 30, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.conversion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.resource.common.io.Streams;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.resource.provider.common.resources.CompressedResourceBridge;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

/**
 * @author Roberto E. Escobar
 */
public class CompressedContentFix {

   private static CompressedContentFix instance = null;

   private static final String FIND_ALL_NATIVE_CONTENT_SQL =
         "SELECT art1.art_id, art1.human_readable_id, art1.guid, attr1.uri FROM osee_define_attribute attr1, osee_define_attribute_type attyp1, osee_define_artifact art1 WHERE attyp1.NAME = 'Native Content' AND attyp1.attr_type_id = attr1.attr_type_id AND art1.ART_ID = attr1.ART_ID";

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
         Connection connection = null;
         try {
            connection = ConnectionHandler.getConnection();
            initializeData(connection);
            doWork(connection, time);
         } catch (SQLException ex) {
            ci.printStackTrace(ex);
         } finally {
            if (connection != null) {
               try {
                  connection.close();
               } catch (SQLException ex) {
                  ci.printStackTrace(ex);
               }
            }
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

      private void initializeData(Connection connection) throws SQLException {
         nativeExtension = Util.getArtIdMap(connection, "Extension");
         nameMap = Util.getArtIdMap(connection, "Name");
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

            ByteOutputStream outputStream = new ByteOutputStream();
            String oldEntryName = getContent(resource, outputStream);
            String extension = nativeExtension.get(artId);
            String newEntryName = generateFileName(nameMap.get(artId), hrid, extension);

            byte[] compressed = Streams.compressStream(new ByteArrayInputStream(outputStream.getBytes()), newEntryName);
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

      private void doWork(Connection connection, long time) {
         ResultSet resultSet = null;
         try {
            resultSet = connection.createStatement().executeQuery(FIND_ALL_NATIVE_CONTENT_SQL);
            int count = 0;
            while (resultSet.next() && execute) {
               long artId = resultSet.getLong("art_id");
               String hrid = resultSet.getString("human_readable_id");
               String guid = resultSet.getString("guid");
               String uri = resultSet.getString("uri");
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
            ci.printStackTrace(ex);
         } finally {
            if (resultSet != null) {
               try {
                  resultSet.close();
               } catch (SQLException ex) {
                  ci.printStackTrace(ex);
               }
            }
         }
      }
   }
}
