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
package org.eclipse.osee.framework.application.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbSetupData.ServerInfoFields;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeApplicationServerLauncher {

   private static OseeApplicationServerLauncher instance = new OseeApplicationServerLauncher();

   private Process process;

   private OseeApplicationServerLauncher() {
      this.process = null;
   }

   public static OseeApplicationServerLauncher getInstance() {
      return instance;
   }

   public void start() throws Exception {
      String binaryDataPath = OseeProperties.getInstance().getOseeApplicationServerData();
      if (Strings.isValid(binaryDataPath)) {
         File binaryDataDirectory = new File(binaryDataPath);
         if (binaryDataDirectory != null && binaryDataDirectory.exists() && binaryDataDirectory.canRead()) {
            DbInformation dbInfo = OseeDb.getDefaultDatabaseService();
            String dbConnection = dbInfo.getDatabaseSetupDetails().getId();

            String serverUrl = dbInfo.getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.applicationServer);
            URL url = new URL(serverUrl);
            launchProcess(url.getPort(), dbConnection, binaryDataDirectory.getAbsolutePath());
         }
      }
   }

   public void stop() {
      if (this.process != null) {
         this.process.destroy();
      }
   }

   private void launchProcess(int serverPort, String defaultDbConnection, String binaryDataPath) throws URISyntaxException, IOException {
      String javaLocation = System.getProperty("java.home");
      List<String> commands = new ArrayList<String>();

      commands.add(javaLocation + File.separator + "bin" + File.separator + "java.exe");
      commands.add("-Dorg.osgi.service.http.port=" + serverPort);
      commands.add("-Dosgi.compatibility.bootdelegation=true");
      commands.add("-Xmx512m");
      commands.add("-DDefaultDbConnection=" + defaultDbConnection);
      commands.add("-Dequinox.ds.debug=true");
      commands.add("-Dorg.eclipse.osee.framework.resource.provider.attribute.basepath=" + binaryDataPath);
      commands.add("-jar");
      commands.add("org.eclipse.osgi_3.4.0.v20080326.jar");

      try {
         URL url = Activator.getInstance().getBundleContext().getBundle().getResource("/osee_server_bundles/");
         url = FileLocator.resolve(url);
         ProcessBuilder processBuilder = new ProcessBuilder();
         processBuilder.directory(new File(url.toURI()));
         processBuilder.command(commands);

         this.process = processBuilder.start();
         Lib.handleProcessNoWait(process, new BufferedWriter(new OutputStreamWriter(System.out)), true);
      } catch (Exception ex) {
         if (process != null) {
            stop();
         }
         OseeLog.log(Activator.class.getName(), Level.SEVERE, "Error launching osee application server", ex);
      }
   }
}
