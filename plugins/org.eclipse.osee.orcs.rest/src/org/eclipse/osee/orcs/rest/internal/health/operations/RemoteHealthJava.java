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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest request =
         HttpRequest.newBuilder().uri(URI.create("http://" + remoteServerName + "/health/java")).header("Accept",
            "application/json").header("Authorization",
               "Basic " + orcsApi.userService().getUser().getLoginIds().get(0)).build();

      try {
         HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
         if (response.statusCode() >= 200 && response.statusCode() < 300) {
            ObjectMapper objectMapper = new ObjectMapper();
            this.healthJava = objectMapper.readValue(response.body(), HealthJava.class);
         } else {
            this.errorMsg = response.body();
         }
      } catch (Exception e) {
         this.errorMsg = e.getMessage();
      }
   }

   public HealthJava getHealthJava() {
      return healthJava;
   }

   public String getErrorMsg() {
      return errorMsg;
   }
}
