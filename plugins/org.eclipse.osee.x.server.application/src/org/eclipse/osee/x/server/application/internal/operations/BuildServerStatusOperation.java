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
package org.eclipse.osee.x.server.application.internal.operations;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.x.server.application.internal.model.ServerStatus;
import org.eclipse.osee.x.server.application.internal.model.StatusKey;

/**
 * @author Donald G. Dunne
 */
public class BuildServerStatusOperation {

   private final IApplicationServerManager applicationServerManager;
   private final IAuthenticationManager authManager;

   public BuildServerStatusOperation(IApplicationServerManager applicationServerManager, IAuthenticationManager authManager) {
      this.applicationServerManager = applicationServerManager;
      this.authManager = authManager;
   }

   public ServerStatus get() {
      ServerStatus stat = new ServerStatus();
      stat.set(StatusKey.ServerUri, applicationServerManager.getServerUri().toString());
      stat.set(StatusKey.ServerId, applicationServerManager.getId());
      stat.set(StatusKey.StartTime, DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(
         applicationServerManager.getDateStarted()));
      stat.set(StatusKey.CodeLocation, System.getProperty("user.dir"));
      stat.set(StatusKey.BinaryDataPath, OseeServerProperties.getOseeApplicationServerData(null));
      stat.set(StatusKey.AuthenticationScheme, authManager.getProtocol());
      stat.set(StatusKey.AuthenticationSchemeSupported, Arrays.deepToString(authManager.getProtocols()));
      stat.set(StatusKey.SupportedVersions, Arrays.deepToString(applicationServerManager.getVersions()));
      MemoryUsage heapMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
      stat.set(StatusKey.MemoryUsed, Lib.toMBytes(heapMem.getUsed()));
      stat.set(StatusKey.MemoryAllocated, Lib.toMBytes(heapMem.getCommitted()));
      stat.set(StatusKey.MemoryMax, Lib.toMBytes(heapMem.getMax()));
      stat.set(StatusKey.ServerState, applicationServerManager.isSystemIdle() ? "IDLE" : "BUSY");
      stat.set(StatusKey.ActiveThreads, String.valueOf(applicationServerManager.getNumberOfActiveThreads()));
      List<String> entries = applicationServerManager.getCurrentProcesses();
      stat.set(StatusKey.CurrentTasks,
         entries.isEmpty() ? "NONE" : org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", entries));
      return stat;
   }
}