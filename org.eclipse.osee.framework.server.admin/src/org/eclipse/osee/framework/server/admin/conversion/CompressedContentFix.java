/*
 * Created on May 30, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.resource.common.io.Streams;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
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

   private static final String sqlExtensionTypeId =
         "Select attrt1.ATTR_TYPE_ID from osee_define_attribute_type attrt1 where name = 'Extension'";

   private static final String sqlExtensionTypes =
         "SELECT attr1.art_id,  attr1.value FROM osee_define_attribute attr1 WHERE attr1.ATTR_TYPE_ID = ?";

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

      private Worker(CommandInterpreter cmdi) {
         ci = cmdi;
         resourceManager = Activator.getInstance().getResourceManager();
         locatorManager = Activator.getInstance().getResourceLocatorManager();
         nativeExtension = new HashMap<Long, String>();
      }

      /* (non-Javadoc)
       * @see java.lang.Runnable#run()
       */
      @Override
      public void run() {
         long time = System.currentTimeMillis();
         doWork();
         long seconds = (System.currentTimeMillis() - time) / 1000;
         long leftOverSeconds = seconds % 60;
         long minutes = seconds / 60;
         long leftOverMinutes = minutes % 60;
         long hours = minutes / 60;
         ci.println(String.format("Done.  Elapsed Time = %d:%02d:%02d.", hours, leftOverMinutes, leftOverSeconds));
         isRunning = false;
      }

      private void processEntry(String artId, String hrid, String guid, String uri) throws Exception {
         boolean resourceExists = isResourceAvailable(uri);
         if (resourceExists) {
            IResourceLocator locator = locatorManager.getResourceLocator(uri);
            Options options = new Options();
            IResource resource = resourceManager.acquire(locator, options);

            String extension = getUriCodedExtension(artId);

            ByteOutputStream outputStream = new ByteOutputStream();
            String entryName = getContent(resource, outputStream);

            ci.println(String.format("hrid [%s] uri [%s] fileName[%s] extension[%s]", hrid, uri, entryName, extension));
         }
      }

      private String getUriCodedExtension(String artId) {
         String toReturn = nativeExtension.get(artId);
         return toReturn != null ? toReturn : "";
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

      private void doWork() {
         Connection connection = null;
         ResultSet resultSet = null;
         try {
            connection = ConnectionHandler.getConnection();
            populateNativeExtensionMap(connection);
            resultSet = connection.createStatement().executeQuery(FIND_ALL_NATIVE_CONTENT_SQL);
            int count = 0;
            while (resultSet.next() && execute) {
               String artId = resultSet.getString("art_id");
               String hrid = resultSet.getString("human_readable_id");
               String guid = resultSet.getString("guid");
               String uri = resultSet.getString("uri");
               processEntry(artId, hrid, guid, uri);
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
            if (connection != null) {
               try {
                  connection.close();
               } catch (SQLException ex) {
                  ci.printStackTrace(ex);
               }
            }
         }
      }

      private void populateNativeExtensionMap(Connection connection) throws SQLException {
         this.nativeExtension.clear();

         Statement stmt = null;
         ResultSet rs = null;
         PreparedStatement prepared = null;
         try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlExtensionTypeId);
            int typeId = -1;
            while (rs.next()) {
               typeId = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            prepared = connection.prepareStatement(sqlExtensionTypes);
            prepared.setInt(1, typeId);
            rs = prepared.executeQuery();

            while (rs.next()) {
               nativeExtension.put(rs.getLong(1), rs.getString(2));
            }
            rs.close();
            prepared.close();
         } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (prepared != null) prepared.close();
         }
      }
   }
}
