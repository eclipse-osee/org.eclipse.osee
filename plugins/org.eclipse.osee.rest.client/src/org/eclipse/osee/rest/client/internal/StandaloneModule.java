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
package org.eclipse.osee.rest.client.internal;

import org.eclipse.osee.rest.client.OseeClientConfig;
import org.eclipse.osee.rest.client.OseeHttpProxyAddress;
import org.eclipse.osee.rest.client.OseeServerAddress;
import org.eclipse.osee.rest.client.WebClientProvider;
import com.google.inject.AbstractModule;

/**
 * @author Roberto E. Escobar
 */
public class StandaloneModule extends AbstractModule {

   private final OseeClientConfig config;

   public StandaloneModule(OseeClientConfig config) {
      this.config = config;
   }

   @Override
   protected void configure() {
      bindConstant().annotatedWith(OseeServerAddress.class).to(config.getServerAddress());
      bindConstant().annotatedWith(OseeHttpProxyAddress.class).to(config.getProxyAddress());

      bind(WebClientProvider.class).to(StandadloneWebClientProvider.class);
   }
}
