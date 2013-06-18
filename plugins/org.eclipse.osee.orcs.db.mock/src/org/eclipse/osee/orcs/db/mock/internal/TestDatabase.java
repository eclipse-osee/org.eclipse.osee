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
package org.eclipse.osee.orcs.db.mock.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.hsqldb.HyperSqlDbServer;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class TestDatabase {

   private final String className;
   private final String methodName;

   private ServiceRegistration<?> registration;
   private File tempFolder;
   private final String connectionId;

   public TestDatabase(String connectionId, String className, String methodName) {
      this.connectionId = connectionId;
      this.className = className;
      this.methodName = methodName;
   }

   private File createTempFolder() {
      String tempDir = System.getProperty("user.home");
      String folderName = String.format("%s_%s_%s", className, methodName, Lib.getDateTimeString());
      File tempFolder = new File(tempDir, folderName);
      tempFolder.mkdir();
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

      int port = PortUtil.getInstance().getConsecutiveValidPorts(2);

      IDatabaseInfo databaseInfo = new DbInfo(connectionId, port, dbPath);
      TestDbProvider provider = new TestDbProvider(databaseInfo);

      System.setProperty("osee.db.embedded.server", "");
      System.setProperty("osee.application.server.data", tempFolder.getAbsolutePath());
      registerProvider(provider);

      IOseeDatabaseService dbService = OsgiUtil.getService(IOseeDatabaseService.class);
      Assert.assertNotNull(dbService);

      HyperSqlDbServer.startServer("0.0.0.0", port, port + 1, databaseInfo);

      OseeConnection connection = dbService.getConnection();
      try {
         Assert.assertNotNull(connection);
      } finally {
         connection.close();
      }
   }

   private void registerProvider(IDatabaseInfoProvider service) {
      BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
      registration = context.registerService(IDatabaseInfoProvider.class, service, null);
   }

   private String getDbHomePath(File tempFolder, String dbFolder) {
      return String.format("file:~/%s/%s/osee.hsql.db", tempFolder.getName(), dbFolder);
   }

   private void checkExist(File tempFolder, String name) {
      File toCheck = new File(tempFolder, name);
      Assert.assertTrue(String.format("%s does not exist", name), toCheck.exists());
   }

   private void addResource(File targetDirectory, Bundle bundle, String resource) throws IOException {
      URL resourceURL = bundle.getResource(resource);
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(resourceURL.openStream());
         Lib.decompressStream(inputStream, targetDirectory);
      } finally {
         Lib.close(inputStream);
      }
   }

   public void cleanup() {
      if (registration != null) {
         registration.unregister();
      }

      System.setProperty("osee.application.server.data", "");
      System.setProperty("osee.db.embedded.server", "");
      boolean isDead = HyperSqlDbServer.stopServerWithWait();
      if (isDead) {
         if (tempFolder != null) {
            Lib.deleteDir(tempFolder);
         }
      } else {
         Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               if (tempFolder != null) {
                  Lib.deleteDir(tempFolder);
               }
            }
         });
      }
   }
}