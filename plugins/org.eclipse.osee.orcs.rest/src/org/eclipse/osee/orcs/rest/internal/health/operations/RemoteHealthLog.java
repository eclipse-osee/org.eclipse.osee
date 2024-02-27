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

import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Jaden W. Puckett
 */
public class RemoteHealthLog {
   private final String remoteServerName;
   private final String appServerDir;
   private final String serverUri;
   private final OrcsApi orcsApi;
   private HealthLog healthLog;
   private String errorMsg = "";

   public RemoteHealthLog(String remoteServerName, String appServerDir, String serverUri, OrcsApi orcsApi) {
      this.remoteServerName = remoteServerName;
      this.appServerDir = appServerDir;
      this.serverUri = serverUri;
      this.orcsApi = orcsApi;
   }

   public void fetchRemoteHealthLog() {
      String logUrl = remoteServerName + "/health/log?appServerDir=" + appServerDir + "&serverUri=" + serverUri;
      this.healthLog = HealthUtils.makeHttpRequest(logUrl, orcsApi.userService().getUser().getLoginIds().get(0),
         HealthLog.class, new HealthLog());
      this.errorMsg = HealthUtils.getErrorMsg();
   }

   public HealthLog getHealthLog() {
      return this.healthLog;
   }

   public String getErrorMsg() {
      return this.errorMsg;
   }
}
