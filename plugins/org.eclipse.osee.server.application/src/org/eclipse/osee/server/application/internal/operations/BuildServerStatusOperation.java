/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.server.application.internal.operations;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ThreadStats;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.server.application.internal.model.ServerStatus;
import org.eclipse.osee.server.application.internal.model.StatusKey;

/**
 * @author Donald G. Dunne
 */
public class BuildServerStatusOperation {

   private final IApplicationServerManager applicationServerManager;
   private final IAuthenticationManager authManager;
   private final ActivityLog activityLog;
   private final RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
   private final OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
   private final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();

   public BuildServerStatusOperation(IApplicationServerManager applicationServerManager, IAuthenticationManager authManager, ActivityLog activityLog) {
      this.applicationServerManager = applicationServerManager;
      this.authManager = authManager;
      this.activityLog = activityLog;
   }

   public ServerStatus get() {
      ServerStatus stat = new ServerStatus();
      stat.set(StatusKey.ServerUri, applicationServerManager.getServerUri().toString());
      stat.set(StatusKey.ServerId, applicationServerManager.getId());
      stat.set(StatusKey.StartTime, DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(
         applicationServerManager.getDateStarted()));
      int seconds = (int) (runtimeMxBean.getUptime() / 1000) % 60;
      int minutes = (int) (runtimeMxBean.getUptime() / (1000 * 60) % 60);
      int hours = (int) (runtimeMxBean.getUptime() / (1000 * 60 * 60) % 24);
      int days = (int) (runtimeMxBean.getUptime() / (1000 * 60 * 60 * 24));

      stat.set(StatusKey.UpTime, String.format("%s days %s hr %s min %s sec", days, hours, minutes, seconds));
      stat.set(StatusKey.SystemLoad, String.valueOf(osMxBean.getSystemLoadAverage()));
      stat.set(StatusKey.CodeLocation, System.getProperty("user.dir"));
      stat.set(StatusKey.BinaryDataPath, OseeServerProperties.getOseeApplicationServerData(null));
      stat.set(StatusKey.AuthenticationScheme, authManager.getProtocol());
      stat.set(StatusKey.AuthenticationSchemeSupported, Arrays.deepToString(authManager.getProtocols()));
      stat.set(StatusKey.SupportedVersions, Arrays.deepToString(applicationServerManager.getVersions()));
      MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
      MemoryUsage heapMem = memoryMXBean.getHeapMemoryUsage();
      stat.set(StatusKey.HeapMemoryUsed, Lib.toMBytes(heapMem.getUsed()));
      stat.set(StatusKey.HeapMemoryAllocated, Lib.toMBytes(heapMem.getCommitted()));
      stat.set(StatusKey.HeapMemoryMax, Lib.toMBytes(heapMem.getMax()));
      MemoryUsage nonHeapMem = memoryMXBean.getNonHeapMemoryUsage();
      stat.set(StatusKey.NonHeapMemoryUsed, Lib.toMBytes(nonHeapMem.getUsed()));
      stat.set(StatusKey.NonHeapMemoryAllocated, Lib.toMBytes(nonHeapMem.getCommitted()));
      stat.set(StatusKey.NonHeapMemoryMax, Lib.toMBytes(nonHeapMem.getMax()));

      stat.garbageCollectorStats.addAll(activityLog.getGarbageCollectionStats());

      ThreadStats[] threadStats = activityLog.getThreadActivity();
      try {
         Thread.sleep(4000);
      } catch (InterruptedException ex) {
         activityLog.createThrowableEntry(CoreActivityTypes.OSEE_ERROR, ex);
      }
      for (String threadStr : activityLog.getThreadActivityDelta(threadStats)) {
         stat.add(threadStr);
      }
      return stat;
   }
}