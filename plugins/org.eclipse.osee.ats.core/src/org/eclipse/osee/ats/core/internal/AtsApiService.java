/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.internal;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;

/**
 * @author Donald G. Dunne
 */
public class AtsApiService {

   private static JaxRsWebTarget target;
   private static AtsApi atsApi;
   private static AtsConfigEndpointApi configEp;

   public void setAtsApi(AtsApi atsApi) {
      AtsApiService.atsApi = atsApi;
   }

   public static AtsApi get() {
      return atsApi;
   }

   private static JaxRsWebTarget getAtsTarget() {
      if (target == null) {
         String appServer = atsApi.getApplicationServerBase();
         String atsUri = String.format("%s/ats", appServer);
         JaxRsClient jaxRsClient = JaxRsClient.newBuilder().createThreadSafeProxyClients(true).build();
         //         target = jaxRsClient.target(atsUri).register(WorkItemJsonReader.class);
         target = jaxRsClient.target(atsUri);
      }
      return target;
   }

   public static AtsConfigEndpointApi getConfigEndpoint() {
      if (configEp == null) {
         configEp = getAtsTarget().newProxy(AtsConfigEndpointApi.class);
      }
      return configEp;
   }

}