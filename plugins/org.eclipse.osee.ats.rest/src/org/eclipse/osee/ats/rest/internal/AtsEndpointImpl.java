/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal;

import org.eclipse.osee.ats.api.AtsJaxRsApi;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.logger.Log;

public class AtsEndpointImpl implements AtsJaxRsApi {

   private final IAtsServer atsServer;
   private final Log logger;
   private final IResourceRegistry registry;
   private AtsNotifyEndpointImpl atsNotifyEndpointImpl;
   private AtsConfigEndpointImpl atsConfigEndpointImpl;

   public AtsEndpointImpl(IAtsServer atsServer, Log logger, IResourceRegistry registry) {
      this.atsServer = atsServer;
      this.logger = logger;
      this.registry = registry;
   }

   @Override
   public AtsNotifyEndpointImpl getNotify() {
      if (atsNotifyEndpointImpl == null) {
         atsNotifyEndpointImpl = new AtsNotifyEndpointImpl(atsServer);
      }
      return atsNotifyEndpointImpl;
   }

   @Override
   public AtsConfigEndpointApi getConfig() {
      if (atsConfigEndpointImpl == null) {
         atsConfigEndpointImpl = new AtsConfigEndpointImpl(atsServer, atsServer.getOrcsApi(), logger, registry);
      }
      return atsConfigEndpointImpl;
   }

}
