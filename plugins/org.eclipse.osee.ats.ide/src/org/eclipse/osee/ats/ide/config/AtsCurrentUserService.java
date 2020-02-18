/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.config;

import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsCurrentUserService;
import org.eclipse.osee.ats.ide.util.AtsServerEndpointProviderImpl;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;

/**
 * @author Donald G. Dunne
 */
public class AtsCurrentUserService implements IAtsCurrentUserService {

   private AtsUser currentUser;
   private AtsConfigEndpointApi configEp;

   public AtsCurrentUserService() {
      // for jax-rs
   }

   @Override
   public AtsUser getCurrentUser() {
      if (currentUser == null) {
         configEp = getConfigEndpoint();
         currentUser = configEp.getUserByLogin(System.getProperty("user.name"));
      }
      return currentUser;
   }

   private AtsConfigEndpointApi getConfigEndpoint() {
      if (configEp == null) {
         JaxRsWebTarget target = AtsServerEndpointProviderImpl.getAtsTargetSt();
         configEp = target.newProxy(AtsConfigEndpointApi.class);
      }
      return configEp;
   }

}
