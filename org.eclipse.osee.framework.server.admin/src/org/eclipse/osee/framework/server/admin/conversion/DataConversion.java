/*
 * Created on Apr 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.conversion;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.ResourceLocator;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Andrew M. Finkbeiner
 */
public class DataConversion {

   private static DataConversion instance;

   public static DataConversion getInstance() {
      if (instance == null) {
         instance = new DataConversion();
      }
      return instance;
   }

   private static final String sqlExtensionTypeId =
         "Select attrt1.ATTR_TYPE_ID from osee_define_attribute_type attrt1 where name = 'Extension'";
   private static final String sqlExtensionTypes =
         "SELECT attr1.art_id,  attr1.value FROM osee_define_attribute attr1 WHERE attr1.ATTR_TYPE_ID = ?";
   private static final String sql =
         "SELECT attr1.gamma_id,  attr1.content,  art1.human_readable_id,  attr1.uri,  art1.art_id,  attrt1.name, art1.guid " + "FROM osee_define_attribute attr1,  osee_define_artifact art1,  osee_define_attribute_type attrt1 WHERE attr1.content IS NOT NULL AND attr1.art_id = art1.art_id and attr1.ATTR_TYPE_ID = attrt1.ATTR_TYPE_ID";
   private static final String updateUri = "update osee_define_attribute set uri = ? where gamma_id = ?";

   private volatile boolean runConversion;
   private volatile boolean isRunning = false;
   private volatile boolean verbose = true;

   private Map<String, String> typeToExtension = new HashMap<String, String>();

   private DataConversion() {
      typeToExtension.put("Word Formatted Content", "xml");
      typeToExtension.put("Word Ole Data", "xml");
      typeToExtension.put("XViewer Customization", "xml");
      typeToExtension.put("ats.Log", "xml");

      //toggle these ones
      typeToExtension.put("ats.Description", "txt");
      typeToExtension.put("ats.State Notes", "xml");
      typeToExtension.put("ats.Resolution", "txt");
      typeToExtension.put("ats.Problem", "txt");
      typeToExtension.put("ats.Location", "txt");
   }

   public void convert(CommandInterpreter ci) {

      if (!isRunning) {
         runConversion = true;
         isRunning = true;
         Thread th = new Thread(new Query(ci));
         th.setName("AttributeConversion");
         th.start();
      }
   }

   public void convertStop(CommandInterpreter ci) {
      runConversion = false;
   }

   private class Query implements Runnable {

      private CommandInterpreter ci;
      ExecutorService exec;

      Query(CommandInterpreter cmdi) {
         ci = cmdi;
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Runnable#run()
       */
      @Override
      public void run() {
         long time = System.currentTimeMillis();
         IResourceManager resourceManager = Activator.getInstance().getResourceManager();
         IResourceLocatorManager locatorManager = Activator.getInstance().getResourceLocatorManager();
         Connection connection = null;
         ResultSet rs = null;
         try {
            connection = ConnectionHandler.getConnection();
            Map<Long, String> nativeExtension = buildNativeExtensionMap(connection);
            rs = connection.createStatement().executeQuery(sql);

            List<Object[]> batchParams = new ArrayList<Object[]>();

            int count = 0;
            while (rs.next() && runConversion) {

               String gamma = rs.getString(1);
               String hrId = rs.getString(3);
               String uri = rs.getString(4);
               String typeName = rs.getString(6);
               long artId = rs.getLong(5);
               String guid = rs.getString(7);

               String fileExtension = "";
               if (typeToExtension.containsKey(typeName)) {
                  fileExtension = typeToExtension.get(typeName);
               } else if (nativeExtension.containsKey(artId)) {
                  fileExtension = nativeExtension.get(artId);
               }

               boolean resourceExists = false;
               if (uri != null && uri.length() > 0) {
                  IResourceLocator resourceLocatorFromDb = new ResourceLocator(new URI(uri));
                  resourceExists = resourceManager.exists(resourceLocatorFromDb);
               }

               if (!resourceExists) {
                  IResourceLocator locator = locatorManager.generateResourceLocator("attr", gamma, hrId);
                  IResource resource = new DbResource(rs.getBinaryStream(2), typeName, guid, fileExtension);

                  Options options = new Options();
                  options.put(StandardOptions.CompressOnSave.name(), true);
                  options.put(StandardOptions.Overwrite.name(), true);
                  if (fileExtension != null && fileExtension.length() > 0) {
                     options.put(StandardOptions.Extension.name(), fileExtension);
                  }
                  try {
                     IResourceLocator actuallLocator = resourceManager.save(locator, resource, options);
                     batchParams.add(new Object[] {SQL3DataType.VARCHAR, actuallLocator.getLocation().toString(),
                           SQL3DataType.INTEGER, gamma});

                     if (!typeToExtension.containsKey(typeName) && !nativeExtension.containsKey(artId)) {
                        ci.println(actuallLocator.getLocation());
                        ci.println("\tadd type " + typeName);
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class.getName(), Level.SEVERE, "Unable to save resource from DB.", ex);
                  }
               }
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
            runUpdateBatch(batchParams);
         } catch (Exception ex) {
            ci.printStackTrace(ex);
         } finally {
            if (rs != null) {
               try {
                  rs.close();
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
         long seconds = (System.currentTimeMillis() - time) / 1000;
         long leftOverSeconds = seconds % 60;
         long minutes = seconds / 60;
         long leftOverMinutes = minutes % 60;
         long hours = minutes / 60;
         ci.println(String.format("Done.  Elapsed Time = %d:%02d:%02d.", hours, leftOverMinutes, leftOverSeconds));
         isRunning = false;
      }

      private void runUpdateBatch(List<Object[]> batchParams) throws SQLException {
         if (batchParams.size() > 0) {
            ConnectionHandler.runPreparedUpdateBatch(updateUri, batchParams);
            batchParams.clear();
         }
      }

      private Map<Long, String> buildNativeExtensionMap(Connection connection) throws SQLException {
         Map<Long, String> toReturn = new HashMap<Long, String>();

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
               toReturn.put(rs.getLong(1), rs.getString(2));
            }
            rs.close();
            prepared.close();
         } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (prepared != null) prepared.close();
         }
         return toReturn;
      }

   }

}
