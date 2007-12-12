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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.NonEclipseConfigurationFactory;
import org.eclipse.osee.framework.plugin.core.config.NonEclipseManifestHeader;

/**
 * @author Andrew M. Finkbeiner
 */
public class StartJini extends JiniService {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(StartJini.class);

   public static final String SPAWNED_REGGIE_SERVICE_ID = "Spawned Reggie Id";
   public static final String SPAWNED_REGGIE_ON_HOST = "On Host";

   private List<Process> jiniProcesses;

   public StartJini(String port, boolean nohup, boolean browser, String jiniHome, File manifestFile) {
      super();

      jiniProcesses = new ArrayList<Process>();

      try {
         String javaHome = System.getProperty("java.home");
         String fs = System.getProperty("file.separator");

         if (jiniHome == null) {
            jiniHome = new File(Lib.getBasePath(StartJini.class).replace("bin", "")).getAbsolutePath();
         }
         jiniHome = jiniHome.replace('\\', '/');

         String[] groups = OseeProperties.getInstance().getOseeJiniServiceGroups();
         String allowedGroups = "";
         if (groups != null) {
            allowedGroups = StringFormat.commaSeparate(groups);
         } else {
            logger.log(
                  Level.SEVERE,
                  "[-D" + OseeProperties.OSEE_JINI_SERVICE_GROUPS + "] was not set.\nPlease enter the Group(s) this Lookup Server will register with.");
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

         logger.log(Level.INFO, "RUN REGGIE ***************************************************");
         logger.log(Level.INFO, startServices);
         Process process = Runtime.getRuntime().exec(startServices);
         jiniProcesses.add(process);
         String reggieServiceId = catchTheReggieServiceId(process);
         Lib.handleProcessNoWait(process, new OutputStreamWriter(System.err));

         if (reggieServiceId == null || reggieServiceId.length() == 0) {
            logger.log(Level.SEVERE, "\n Jini Initialization Failed. \n");
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

         logger.log(Level.INFO, "....................Core Jini Service is Alive....................");
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
            logger.log(Level.SEVERE, "err: " + line + "\n");
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

   private static File getManifestFile(String[] args) {
      File toReturn = null;
      CmdLineArgs cmdLineArgs = new CmdLineArgs(args);
      String manifestFileString = cmdLineArgs.get("-manifest");

      if (manifestFileString == null || manifestFileString.length() == 0) {
         manifestFileString = "META-INF/MANIFEST.MF";
      }

      toReturn = new File(manifestFileString);
      if (toReturn == null || !toReturn.exists()) {
         logger.log(Level.SEVERE, "The Specified Manifest File does not exist!!");
         System.exit(1);
      }
      return toReturn;
   }

   private static Dictionary<Object, Object> getHeaders(File manifestFile) {
      try {
         return NonEclipseManifestHeader.parseManifest(new FileInputStream(manifestFile));
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return null;
   }

   public static void main(String[] args) {
      System.setProperty(OseeProperties.OSEE_CONFIG_FACTORY, NonEclipseConfigurationFactory.class.getName());
      logger.log(Level.INFO, "num args + " + args.length);
      File manifestFile = getManifestFile(args);
      if (args.length == 1) {
         new StartJini(args[0], false, false, null, manifestFile);
      } else if (args.length > 1) {
         boolean browser = false, nohup = false;

         for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-nohup")) {
               logger.log(Level.INFO, "nohup!!");
               nohup = true;
            } else if (args[i].equalsIgnoreCase("-browser")) {
               browser = true;
            }
         }

         new StartJini(args[0], nohup, browser, null, manifestFile);
      } else {
         logger.log(Level.INFO, "USAGE: -Dosee.jini.lookup.groups=<groups> StartJini <port> ?<-nohup> ?<-browser>");
      }

      logger.log(Level.INFO, "Exiting...");
      Runtime.getRuntime().exit(0);
   }

   private void killProcesses() {
      logger.log(Level.INFO, "Destroying Spawned Processes...");
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
      logger.log(Level.INFO, "De-registering Core Jini Service...");
      deregisterService();
      killProcesses();
      ServiceDataStore.getNonEclipseInstance().terminate();
      commitSuicide();
   }
}
