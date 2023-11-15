/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Jaden W. Puckett
 */
public class HealthJava {
   private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
   private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
   private final JdbcClient jdbcClient;

   private String vmName = "";
   private String vmVendor = "";
   private String vmVersion = "";
   private String vmSpecVersion = "";
   private String classPath = "";
   private String libraryPath = "";
   private String osName = "";
   private String osVersion = "";
   private String osArch = "";
   private final List<String> processArgs = new ArrayList<>();
   private final List<String> processes = new ArrayList<>();

   public HealthJava(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public void setJavaInfo() {
      this.vmName = runtimeMXBean.getVmName();
      this.vmVendor = runtimeMXBean.getVmVendor();
      this.vmVersion = runtimeMXBean.getVmVersion();
      this.vmSpecVersion = runtimeMXBean.getSpecVersion();
      this.classPath = runtimeMXBean.getClassPath();
      this.libraryPath = runtimeMXBean.getLibraryPath();
      this.osName = osMXBean.getName();
      this.osVersion = osMXBean.getVersion();
      this.osArch = osMXBean.getArch();

      List<String> inputArguments = runtimeMXBean.getInputArguments();
      for (String arg : inputArguments) {
         this.processArgs.add(arg);
      }

      if (!Lib.isWindows()) {
         String psResults = "";
         if (ServerUtils.isCurlServerSet(jdbcClient)) {
            psResults = ServerUtils.runCurlExecFromCurlServer("ps%20-ef", jdbcClient);
         }
         if (Strings.isInValid(psResults)) {
            psResults = "ps -ef command is not available for windows (example below)\n\n" + OseeInf.getResourceContents(
               "web/health/psef.txt", HealthJava.class);
         }
         processes.add(psResults);
      } else {
         ProcessBuilder processBuilder = new ProcessBuilder();
         processBuilder.command("ps", "-ef");
         try {
            Process process = processBuilder.start();
            try (InputStream inputStream = process.getInputStream();
               BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
               String line;
               while ((line = reader.readLine()) != null) {
                  if (line.contains("java")) {
                     processes.add(line);
                  }
               }
            }
            process.waitFor();
            process.destroy();
         } catch (Exception e) {
            processes.add("Error executing ps -ef: " + e.getMessage());
         }
      }
   }

   public String getVmName() {
      return vmName;
   }

   public String getVmVendor() {
      return vmVendor;
   }

   public String getVmVersion() {
      return vmVersion;
   }

   public String getVmSpecVersion() {
      return vmSpecVersion;
   }

   public String getClassPath() {
      return classPath;
   }

   public String getLibraryPath() {
      return libraryPath;
   }

   public String getOsName() {
      return osName;
   }

   public String getOsVersion() {
      return osVersion;
   }

   public String getOsArch() {
      return osArch;
   }

   public List<String> getProcessArgs() {
      return processArgs;
   }

   public List<String> getProcesses() {
      return processes;
   }
}
