/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.mock;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.db.mock.internal.OsgiUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Roberto E. Escobar
 */
public class TestDatabase {

   private final String className;
   private final String methodName;
   private final String[] osgiBindings;

   private File tempFolder;

   private Configuration configuration;
   private JdbcService jdbcService;

   public TestDatabase(String className, String methodName, String... osgiBindings) {
      this.className = className;
      this.methodName = methodName;
      this.osgiBindings = osgiBindings;
   }

   private File createTempFolder() {
      String tempDir = System.getProperty("user.home");
      String folderName = String.format("%s_%s_%s", className, methodName, Lib.getDateTimeString());
      File tempFolder = new File(tempDir, folderName);
      if (tempFolder.mkdir()) {
         System.out.println(
            String.format("Successfully made test database temp folder [%s]", tempFolder.getAbsolutePath()));
      }
      return tempFolder;
   }

   public void initialize() throws Exception {
      Bundle bundle = FrameworkUtil.getBundle(OseeDatabase.class);
      Assert.assertNotNull("Bundle cannot be null", bundle);
      int state = bundle.getState();
      if (state != Bundle.STARTING || state != Bundle.ACTIVE) {
         bundle.start();
      }

      tempFolder = createTempFolder();
      Assert.assertNotNull("TempFolder cannot be null", tempFolder);

      addResource(tempFolder, bundle, "data/hsql.zip");
      addResource(tempFolder, bundle, "data/binary_data.zip");

      checkExist(tempFolder, "hsql");
      checkExist(tempFolder, "attr");

      String dbPath = getDbHomePath(tempFolder, "hsql");

      System.setProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA, tempFolder.getAbsolutePath());

      ConfigurationAdmin configAdmin = OsgiUtil.getConfigAdmin();

      configuration = configAdmin.getConfiguration("org.eclipse.osee.jdbc.internal.osgi.JdbcComponentFactory", null);
      configuration.update(newConfig(dbPath));

      jdbcService = OsgiUtil.getService(JdbcService.class, "(osgi.binding=orcs.jdbc.service)", 30000L);
      Assert.assertNotNull("Unable to get JdbcService", jdbcService);
      boolean isAlive = jdbcService.isServerAlive(10000L);
      Assert.assertEquals("database service is not alive", true, isAlive);
   }

   private Dictionary<String, Object> newConfig(String dbPath) {
      Map<String, Object> config = new LinkedHashMap<>();
      config.put(JdbcConstants.JDBC_SERVICE__ID, Lib.generateUuid());
      config.put(JdbcConstants.JDBC_SERVER__DB_DATA_PATH, dbPath);
      config.put(JdbcConstants.JDBC_SERVER__USE_RANDOM_PORT, true);
      config.put(JdbcConstants.JDBC_POOL__ENABLED, false);
      config.put(JdbcConstants.JDBC_POOL__MAX_ACTIVE_CONNECTIONS, 100);
      config.put(JdbcConstants.JDBC_POOL__MAX_IDLE_CONNECTIONS, 100);

      Set<String> bindings = new HashSet<>();
      for (String binding : osgiBindings) {
         bindings.add(binding);
      }
      config.put(JdbcConstants.JDBC_SERVICE__OSGI_BINDING, bindings);

      JSONArray jsonArray = new JSONArray();
      try {
         jsonArray.put(0, config);
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      Hashtable<String, Object> data = new Hashtable<>();
      data.put("serviceId", "org.eclipse.osee.jdbc.internal.osgi.JdbcComponentFactory");
      data.put(JdbcConstants.JDBC_SERVICE__CONFIGS, jsonArray.toString());
      return data;
   }

   private String getDbHomePath(File tempFolder, String dbFolder) {
      return String.format("file:~/%s/%s/osee.hsql.db", tempFolder.getName(), dbFolder);
   }

   private void checkExist(File tempFolder, String name) {
      File toCheck = new File(tempFolder, name);
      Assert.assertTrue(String.format("directory [%s] does not exist", name), toCheck.exists());
   }

   private void addResource(File targetDirectory, Bundle bundle, String resource) throws IOException {
      URL resourceURL = bundle.getResource(resource);
      InputStream inputStream = null;
      try {
         if (resourceURL != null) {
            inputStream = new BufferedInputStream(resourceURL.openStream());
            Lib.decompressStream(inputStream, targetDirectory);
         }
      } finally {
         Lib.close(inputStream);
      }
   }

   public void cleanup() {
      if (configuration != null) {
         try {
            configuration.delete();
         } catch (IOException ex) {
            throw new OseeCoreException(ex);
         }
      }
      System.setProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA, "");
      boolean isDead = jdbcService != null ? !jdbcService.isServerAlive(2000L) : true;
      if (isDead) {
         if (tempFolder != null) {
            Lib.deleteDir(tempFolder);
         }
      }
      // Add shutdown hook if folder was not deleted - most likely due to a 'busy' file/folder
      if (tempFolder != null && tempFolder.exists()) {
         Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               if (tempFolder != null && tempFolder.exists()) {
                  Lib.deleteDir(tempFolder);
               }
            }
         });
      }
   }
}