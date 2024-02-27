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
/**
 * @author Jaden W. Puckett
 */
package org.eclipse.osee.orcs.rest.internal.health.operations;

import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Jaden W. Puckett
 */
public class RemoteHealthDetails {
   private String remoteServerName = "";
   private final OrcsApi orcsApi;
   private HealthDetails healthDetails;
   private String errorMsg = "";

   public RemoteHealthDetails(String remoteServerName, OrcsApi orcsApi) {
      this.remoteServerName = remoteServerName;
      this.orcsApi = orcsApi;
   }

   public void fetchRemoteHealthDetails() {
      String detailsUrl = remoteServerName + "/health/details";
      this.healthDetails = HealthUtils.makeHttpRequest(detailsUrl, orcsApi.userService().getUser().getLoginIds().get(0),
         HealthDetails.class, new HealthDetails());
      this.errorMsg = HealthUtils.getErrorMsg();
   }

   public HealthDetails getHealthDetails() {
      return healthDetails;
   }

   public String getErrorMsg() {
      return errorMsg;
   }
}
