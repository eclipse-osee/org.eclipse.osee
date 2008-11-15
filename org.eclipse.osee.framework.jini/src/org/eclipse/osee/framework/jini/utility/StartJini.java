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
package org.eclipse.osee.framework.jini.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.ServiceInfo;
import org.eclipse.osee.framework.jdk.core.util.CmdLineArgs;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import org.eclipse.osee.framework.jini.service.core.JiniService;
import org.eclipse.osee.framework.jini.service.core.SimpleFormattedEntry;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.config.JiniLookupGroupConfig;
import org.eclipse.osee.framework.plugin.core.config.NonEclipseManifestHeader;

/**
 * @author Andrew M. Finkbeiner
 */
public class StartJini extends JiniService {

   public static final String SPAWNED_REGGIE_SERVICE_ID = "Spawned Reggie Id";
   public static final String SPAWNED_REGGIE_ON_HOST = "On Host";

   private List<Process> jiniProcesses;

   public StartJini(String port, boolean nohup, boolean browser, String jiniHome, InputStream manifestFile) {
      super();

      jiniProcesses = new ArrayList<Process>();

      try {
         String javaHome = System.getProperty("java.home");
         String fs = System.getProperty("file.separator");

         if (jiniHome == null) {
            jiniHome = new File(Lib.getBasePath(StartJini.class).replace("bin", "")).getAbsolutePath();
         }
         jiniHome = jiniHome.replace('\\', '/');

         String[] groups = JiniLookupGroupConfig.getOseeJiniServiceGroups();
         String allowedGroups = "";
         if (groups != null) {
            allowedGroups = StringFormat.commaSeparate(groups);
         } else {
            OseeLog.log(
                  StartJini.class,
                  Level.SEVERE,
                  "[-D" + OseeProperties.getOseeJiniServiceGroups() + "] was not set.\nPlease enter the Group(s) this Lookup Server will register with.");
            return;
         }
         String quote = null;
         if (Lib.isWindows()) {
            quote = "\"";
         } else {
            quote = "";
         }

         String host = InetAddress.getLocalHost().getHostAddress();
         System.out.println("Host Address: " + host);
         String javaexeBigMem = quote + javaHome + fs + "bin" + fs + "java" + quote + " -Xmx512M ";
         String startServices =
               javaexeBigMem + " -Dlookupcomponent " + " -Dosee.jini.lookup.groups=" + allowedGroups + " " + " -Dosee.jini.install=" + quote + jiniHome + "/jini2_1" + quote + " " + " -Dosee.jini.config=" + quote + jiniHome + "/jini_config" + quote + " " + " -Dosee.classserver.host=" + host + " " + " -Dosee.classserver.port=" + port + " " + " -Djava.security.policy=" + quote + jiniHome + "/jini_config/jsk-all.policy" + quote + " " + " -jar " + quote + jiniHome + "/jini2_1/lib/start.jar" + quote + " " + quote + jiniHome + "/jini_config/start-transient-jeri-services.config" + quote;

         OseeLog.log(StartJini.class, Level.INFO, "RUN REGGIE ***************************************************");
         OseeLog.log(StartJini.class, Level.INFO, startServices);
         Process process = Runtime.getRuntime().exec(startServices);
         jiniProcesses.add(process);
         String reggieServiceId = catchTheReggieServiceId(process);
         Lib.handleProcessNoWait(process, new OutputStreamWriter(System.err));

         if (reggieServiceId == null || reggieServiceId.length() == 0) {
            OseeLog.log(StartJini.class, Level.SEVERE, "\n Jini Initialization Failed. \n");
            killProcesses();
            return;
         }

         // Wait for Reggie to come alive before registering Core Jini Service
         try {
            Thread.sleep(7000);
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }

         String model = "CJS";
         String description = "Provides means to shutdown spawned Jini Lookup Services";

         this.registerService(new Entry[] {new ServiceInfo("", "", "", "", model, ""), new Comment(description),
               new SimpleFormattedEntry(SPAWNED_REGGIE_SERVICE_ID, reggieServiceId),
               new SimpleFormattedEntry(SPAWNED_REGGIE_ON_HOST, getHostName())}, getHeaders(manifestFile));

         OseeLog.log(StartJini.class, Level.INFO, "....................Core Jini Service is Alive....................");
         this.stayAlive();

      } catch (IOException ex) {
         ex.printStackTrace();
      }

   }

   private String catchTheReggieServiceId(Process process) {
      Pattern reggieStartPattern = Pattern.compile("INFO: started Reggie: ([a-z|A-Z|0-9|\\-]+).*");
      String reggieFail = "SEVERE: Reggie initialization failed";
      StringBuilder wr = new StringBuilder();
      String toReturn = "";
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      String line = null;
      try {
         while ((line = reader.readLine()) != null) {
            wr.append(line);
            OseeLog.log(StartJini.class, Level.SEVERE, "err: " + line + "\n");
            Matcher reggieStartMatcher = reggieStartPattern.matcher(wr);
            if (!wr.toString().contains(reggieFail)) {
               if (reggieStartMatcher.matches()) {
                  toReturn = reggieStartMatcher.group(1);
                  break;
               }
            } else {
               break;
            }
            wr.delete(0, wr.length());
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return toReturn;
   }

   private String getHostName() {
      String host = "";
      try {
         host = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException ex) {
         host = "Error Obtaining Host";
         ex.printStackTrace();
      }
      return host;
   }

   private static InputStream getManifestFile(String[] args) {
      InputStream toReturn = null;
      CmdLineArgs cmdLineArgs = new CmdLineArgs(args);
      String manifestFileString = cmdLineArgs.get("-manifest");

      if (manifestFileString == null || manifestFileString.length() == 0) {
         manifestFileString = "META-INF/MANIFEST.MF";
      }
      File file = new File(manifestFileString);

      if (file == null || !file.exists()) {
         OseeLog.log(StartJini.class, Level.SEVERE, "The Specified Manifest File does not exist!!");
         System.exit(1);
      }
      try {
         toReturn = new FileInputStream(file);
      } catch (FileNotFoundException ex) {
         OseeLog.log(StartJini.class, Level.SEVERE, "The Specified Manifest File can not be opened!!", ex);
         System.exit(1);
      }
      return toReturn;
   }

   private static Dictionary<Object, Object> getHeaders(InputStream manifestFile) {
      try {
         return NonEclipseManifestHeader.parseManifest(manifestFile);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return null;
   }

   public static void main(String[] args) {
      OseeLog.log(StartJini.class, Level.INFO, "num args + " + args.length);
      InputStream manifestFile = getManifestFile(args);
      if (args.length == 1) {
         new StartJini(args[0], false, false, null, manifestFile);
      } else if (args.length > 1) {
         boolean browser = false, nohup = false;

         for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-nohup")) {
               OseeLog.log(StartJini.class, Level.INFO, "nohup!!");
               nohup = true;
            } else if (args[i].equalsIgnoreCase("-browser")) {
               browser = true;
            }
         }

         new StartJini(args[0], nohup, browser, null, manifestFile);
      } else {
         OseeLog.log(StartJini.class, Level.INFO,
               "USAGE: -Dosee.jini.lookup.groups=<groups> StartJini <port> ?<-nohup> ?<-browser>");
      }

      OseeLog.log(StartJini.class, Level.INFO, "Exiting...");
      Runtime.getRuntime().exit(0);
   }

   private void killProcesses() {
      OseeLog.log(StartJini.class, Level.INFO, "Destroying Spawned Processes...");
      for (Process process : jiniProcesses) {
         if (process != null) {
            process.destroy();
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.service.interfaces.IService#kill()
    */
   public void kill() throws RemoteException {
      OseeLog.log(StartJini.class, Level.INFO, "De-registering Core Jini Service...");
      deregisterService();
      killProcesses();
      ServiceDataStore.getNonEclipseInstance().terminate();
      commitSuicide();
   }
}
