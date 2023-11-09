/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ThreadStats;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Jaden W. Puckett
 */
public class HealthDetails {
   private final RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
   private final OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();

   public HealthDetails(JdbcClient jdbcClient, IApplicationServerManager applicationServerManager, IAuthenticationManager authManager, ActivityLog activityLog) {
      this.uri = applicationServerManager.getServerUri().toString();

      this.startTime = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(
         applicationServerManager.getDateStarted());
      int seconds = (int) (runtimeMxBean.getUptime() / 1000) % 60;
      int minutes = (int) (runtimeMxBean.getUptime() / (1000 * 60) % 60);
      int hours = (int) (runtimeMxBean.getUptime() / (1000 * 60 * 60) % 24);
      int days = (int) (runtimeMxBean.getUptime() / (1000 * 60 * 60 * 24));
      this.upTime = String.format("%s days %s hr %s min %s sec", days, hours, minutes, seconds);

      this.authScheme = authManager.getProtocol();
      this.authSchemeSupported = Arrays.asList(authManager.getProtocols());

      MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
      MemoryUsage heapMem = memoryMXBean.getHeapMemoryUsage();
      this.heapMemAlloc = Lib.toMBytes(heapMem.getCommitted());
      this.heapMemMax = Lib.toMBytes(heapMem.getMax());
      this.heapMemUsed = Lib.toMBytes(heapMem.getUsed());
      MemoryUsage nonHeapMem = memoryMXBean.getNonHeapMemoryUsage();
      this.nonHeapMemAlloc = Lib.toMBytes(nonHeapMem.getCommitted());
      this.nonHeapMemMax = Lib.toMBytes(nonHeapMem.getMax());
      this.nonHeapMemUsed = Lib.toMBytes(nonHeapMem.getUsed());

      this.codeLocation = System.getProperty("user.dir");
      this.systemLoad = String.valueOf(osMxBean.getSystemLoadAverage());
      this.supportedVersions = Arrays.asList(applicationServerManager.getVersions());
      this.serverId = applicationServerManager.getId();
      this.binaryDataPath = OseeServerProperties.getOseeApplicationServerData(null);

      ThreadStats[] thread = activityLog.getThreadActivity();
      try {
         Thread.sleep(2000);
      } catch (InterruptedException ex) {
         activityLog.createThrowableEntry(CoreActivityTypes.OSEE_ERROR, ex);
      }
      for (String threadStr : activityLog.getThreadActivityDelta(thread)) {
         this.threadStats.add(threadStr);
      }
      // Return a simple string array. getGarbageCollectionStats() returns much more information if needed.
      this.garbageCollectorStats.addAll(activityLog.getGarbageCollectionStats());

      String fetchedServerName =
         jdbcClient.fetch("", "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?", "osee.health.curl.server");
      this.serverWithLogs =
         Strings.isInValid(fetchedServerName) ? "osee.health.curl.server not set in osee_info" : fetchedServerName;
   };

   private final String uri;
   private final String startTime;
   private final String upTime;
   private final String authScheme;
   private final List<String> authSchemeSupported;
   private final String heapMemAlloc;
   private final String heapMemMax;
   private final String heapMemUsed;
   private final String nonHeapMemAlloc;
   private final String nonHeapMemMax;
   private final String nonHeapMemUsed;
   private final String codeLocation;
   private final String systemLoad;
   private final List<String> supportedVersions;
   private final String serverId;
   private final String binaryDataPath;
   private List<String> threadStats;
   private final List<String> garbageCollectorStats = new ArrayList<>();
   private final String serverWithLogs;

   public String getUri() {
      return uri;
   }

   public String getStartTime() {
      return startTime;
   }

   public String getUpTime() {
      return upTime;
   }

   public String getAuthScheme() {
      return authScheme;
   }

   public List<String> getAuthSchemeSupported() {
      return authSchemeSupported;
   }

   public String getHeapMemAlloc() {
      return heapMemAlloc;
   }

   public String getHeapMemMax() {
      return heapMemMax;
   }

   public String getHeapMemUsed() {
      return heapMemUsed;
   }

   public String getNonHeapMemAlloc() {
      return nonHeapMemAlloc;
   }

   public String getNonHeapMemMax() {
      return nonHeapMemMax;
   }

   public String getNonHeapMemUsed() {
      return nonHeapMemUsed;
   }

   public String getCodeLocation() {
      return codeLocation;
   }

   public String getSystemLoad() {
      return systemLoad;
   }

   public List<String> getSupportedVersions() {
      return supportedVersions;
   }

   public String getServerId() {
      return serverId;
   }

   public String getBinaryDataPath() {
      return binaryDataPath;
   }

   public List<String> getThreadStats() {
      return threadStats;
   }

   public List<String> getGarbageCollectorStats() {
      return garbageCollectorStats;
   }

   public String getServerWithLogs() {
      return serverWithLogs;
   }
}
