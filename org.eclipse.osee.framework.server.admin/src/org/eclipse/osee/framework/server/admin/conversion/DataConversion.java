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

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
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

   private static final String sql =
         "SELECT attr1.gamma_id,  attr1.content,  art1.human_readable_id,  attr1.uri,  art1.art_id,  attrt1.name, art1.guid " + "FROM osee_attribute attr1,  osee_artifact art1,  osee_attribute_type attrt1 WHERE attr1.content IS NOT NULL AND attr1.art_id = art1.art_id and attr1.ATTR_TYPE_ID = attrt1.ATTR_TYPE_ID";

   private static final String updateUri = "update osee_attribute set uri = ? where gamma_id = ?";

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
         ResultSet rs = null;
         OseeConnection connection = null;
         try {
            connection = OseeDbConnection.getConnection();
            Map<Long, String> nativeExtension = Util.getArtIdMap("Extension");
            Map<Long, String> nameMap = Util.getArtIdMap("Name");

            rs = connection.createStatement().executeQuery(sql);

            List<Object[]> batchParams = new ArrayList<Object[]>();

            int count = 0;
            while (rs.next() && runConversion) {

               String gamma = rs.getString(1);
               String hrId = rs.getString(3);
               String uri = rs.getString(4);
               String typeName = rs.getString(6);
               long artId = rs.getLong(5);
               //String guid = rs.getString(7);

               String fileExtension = "";
               if (typeToExtension.containsKey(typeName)) {
                  fileExtension = typeToExtension.get(typeName);
               } else if (nativeExtension.containsKey(artId)) {
                  fileExtension = nativeExtension.get(artId);
               }

               String descriptiveName = nameMap.get(artId);
               if (descriptiveName == null || descriptiveName.length() <= 0) {
                  descriptiveName = typeName;
               }

               boolean resourceExists = false;
               if (uri != null && uri.length() > 0) {
                  IResourceLocator resourceLocatorFromDb = new ResourceLocator(new URI(uri));
                  resourceExists = resourceManager.exists(resourceLocatorFromDb);
               }

               if (!resourceExists) {
                  IResourceLocator locator = locatorManager.generateResourceLocator("attr", gamma, hrId);
                  IResource resource = new DbResource(rs.getBinaryStream(2), descriptiveName, hrId, fileExtension);

                  Options options = new Options();
                  options.put(StandardOptions.CompressOnSave.name(), true);
                  options.put(StandardOptions.Overwrite.name(), true);
                  if (fileExtension != null && fileExtension.length() > 0) {
                     options.put(StandardOptions.Extension.name(), fileExtension);
                  }
                  try {
                     IResourceLocator actuallLocator = resourceManager.save(locator, resource, options);
                     batchParams.add(new Object[] {actuallLocator.getLocation().toString(), gamma});

                     if (!typeToExtension.containsKey(typeName) && !nativeExtension.containsKey(artId)) {
                        ci.println(actuallLocator.getLocation());
                        ci.println("\tadd type " + typeName);
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, "Unable to save resource from DB.", ex);
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
            OseeLog.log(Activator.class, Level.SEVERE, "Unable to save resource from DB.", ex);
         } finally {
            if (rs != null) {
               try {
                  rs.close();
               } catch (SQLException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Unable to save resource from DB.", ex);
               }
            }
            if (connection != null) {
               connection.close();
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

      private void runUpdateBatch(List<Object[]> batchParams) throws OseeDataStoreException {
         if (batchParams.size() > 0) {
            ConnectionHandler.runBatchUpdate(updateUri, batchParams);
            batchParams.clear();
         }
      }
   }
}
