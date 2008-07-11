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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

/**
 * @author Roberto E. Escobar
 */
public class OseeApplicationServerLauncher {

   private static OseeApplicationServerLauncher instance = new OseeApplicationServerLauncher();

   private IOConsole console;
   private Process process;
   private IOConsoleOutputStream streamOut = null;
   private IOConsoleOutputStream streamErr = null;

   private OseeApplicationServerLauncher() {
      this.console = null;
      this.process = null;
      this.streamOut = null;
      this.streamErr = null;
   }

   public static OseeApplicationServerLauncher getInstance() {
      return instance;
   }

   public void start() throws Exception {
      String binaryDataPath = OseeProperties.getInstance().getOseeApplicationServerData();
      if (Strings.isValid(binaryDataPath)) {
         File binaryDataDirectory = new File(binaryDataPath);
         if (binaryDataDirectory != null && binaryDataDirectory.exists() && binaryDataDirectory.canRead()) {
            DbInformation information = OseeDb.getDefaultDatabaseService();
            String dbConnection = information.getDatabaseSetupDetails().getId();
            launchProcess(OseeApplicationServer.getApplicationServerPort(), dbConnection,
                  binaryDataDirectory.getAbsolutePath());
         }
      }
   }

   public void stop() {
      if (this.console != null) {
         ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] {this.console});
         this.streamOut = null;
         this.streamErr = null;
      }
      if (this.process != null) {
         this.process.destroy();
      }
   }

   private void launchProcess(int serverPort, String defaultDbConnection, String binaryDataPath) throws URISyntaxException, IOException {
      String javaLocation = System.getProperty("java.home");
      List<String> commands = new ArrayList<String>();

      commands.add(javaLocation);
      commands.add("-Dorg.osgi.service.http.port=" + serverPort);
      commands.add("-Dosgi.compatibility.bootdelegation=true");
      commands.add("-Xmx512m");
      commands.add("-DDefaultDbConnection=" + defaultDbConnection);
      commands.add("-Dequinox.ds.debug=true");
      commands.add("-Dorg.eclipse.osee.framework.resource.provider.attribute.basepath=" + binaryDataPath);
      commands.add("-jar");
      commands.add("org.eclipse.osgi_3.4.0.v20080326.jar");
      commands.add("-console");

      URL url = Activator.getInstance().getBundleContext().getBundle().getResource("/osee_server_bundles/");

      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder.directory(new File(url.toURI()));
      processBuilder.command(commands);

      this.process = processBuilder.start();

      this.console = new IOConsole("Osee Application Server", null);
      console.clearConsole();

      ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {console});

      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            streamOut = console.newOutputStream();
            streamOut.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
            streamErr = console.newOutputStream();
            streamErr.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));

            Lib.handleProcessNoWait(process, new BufferedWriter(new OutputStreamWriter(streamOut)), new BufferedWriter(
                  new OutputStreamWriter(streamErr)), new BufferedReader(
                  new InputStreamReader(console.getInputStream())));
         }
      });

   }
}
