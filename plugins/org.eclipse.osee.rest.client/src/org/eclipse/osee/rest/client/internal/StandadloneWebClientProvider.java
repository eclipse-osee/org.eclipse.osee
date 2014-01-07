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

import java.net.URI;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.rest.client.OseeHttpProxyAddress;
import com.google.inject.Inject;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;

/**
 * @author Roberto E. Escobar
 */
public class StandadloneWebClientProvider extends AbstractWebClientProvider {

   private final String proxyAddress;

   @Inject
   public StandadloneWebClientProvider(@OseeHttpProxyAddress String proxyAddress) {
      this.proxyAddress = proxyAddress;
   }

   @Override
   protected void configure(URI uri, Map<String, Object> properties) {
      properties.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
      if (Strings.isValid(proxyAddress)) {
         properties.put(ApacheHttpClientConfig.PROPERTY_PROXY_URI, proxyAddress);
      }
   }

}
