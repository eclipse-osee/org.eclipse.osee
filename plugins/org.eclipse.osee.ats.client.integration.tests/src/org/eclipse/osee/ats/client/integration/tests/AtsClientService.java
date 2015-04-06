/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests;

import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;

/**
 * @author Donald G. Dunne
 */
public class AtsClientService {

   private static IAtsClient atsClient;
   private static AgileEndpointApi agile;
   private static JaxRsWebTarget target;

   public void setAtsClient(IAtsClient atsClient) {
      AtsClientService.atsClient = atsClient;
   }

   public static IAtsClient get() {
      return atsClient;
   }

   private static JaxRsWebTarget getTarget() {
      if (target == null) {
         String appServer = OseeClientProperties.getOseeApplicationServer();
         String atsUri = String.format("%s/ats", appServer);
         JaxRsClient jaxRsClient = JaxRsClient.newBuilder().build();
         target = jaxRsClient.target(atsUri);
      }
      return target;
   }

   public static AgileEndpointApi getAgile() {
      if (agile == null) {
         agile = getTarget().newProxy(AgileEndpointApi.class);
      }
      return agile;
   }

}
