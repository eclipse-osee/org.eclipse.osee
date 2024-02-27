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
public class RemoteHealthJava {
   private String remoteServerName = "";
   private final OrcsApi orcsApi;
   private HealthJava healthJava;
   private String errorMsg = "";

   public RemoteHealthJava(String remoteServerName, OrcsApi orcsApi) {
      this.remoteServerName = remoteServerName;
      this.orcsApi = orcsApi;
   }

   public void fetchRemoteHealthJava() {
      String javaUrl = remoteServerName + "/health/java";
      this.healthJava = HealthUtils.makeHttpRequest(javaUrl, orcsApi.userService().getUser().getLoginIds().get(0),
         HealthJava.class, new HealthJava());
      this.errorMsg = HealthUtils.getErrorMsg();
   }

   public HealthJava getHealthJava() {
      return healthJava;
   }

   public String getErrorMsg() {
      return errorMsg;
   }
}
