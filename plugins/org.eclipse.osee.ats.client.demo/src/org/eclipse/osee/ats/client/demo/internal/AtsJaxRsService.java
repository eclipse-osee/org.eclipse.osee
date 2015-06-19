/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo.internal;

import org.eclipse.osee.ats.api.AtsJaxRsApi;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;

/**
 * @author Donald G. Dunne
 */
public class AtsJaxRsService {

   private static AtsJaxRsApi atsEndpoint;

   public static AtsJaxRsApi get() {
      if (atsEndpoint == null) {
         String appServer = OseeClientProperties.getOseeApplicationServer();
         String atsUri = String.format("%s/ats", appServer);

         atsEndpoint = JaxRsClient.newBuilder() //
         .createThreadSafeProxyClients(true) //  if the client needs to be shared between threads 
         .build() //
         .targetProxy(atsUri, AtsJaxRsApi.class);
      }
      return atsEndpoint;
   }

}
