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
public class RemoteHealthTop {
   private final String remoteServerName;
   private final OrcsApi orcsApi;
   private HealthTop healthTop;
   private String errorMsg = "";
   public RemoteHealthTop(String remoteServerName, OrcsApi orcsApi) {
      this.remoteServerName = remoteServerName;
      this.orcsApi = orcsApi;
   }

   public void fetchRemoteHealthTop() {
      String topUrl = remoteServerName + "/health/top";
      this.healthTop = HealthUtils.makeHttpRequest(topUrl, orcsApi.userService().getUser().getLoginIds().get(0),
         HealthTop.class, new HealthTop());
      this.errorMsg = HealthUtils.getErrorMsg();
   }

   public HealthTop getHealthTop() {
      return healthTop;
   }

   public String getErrorMsg() {
      return errorMsg;
   }
}
