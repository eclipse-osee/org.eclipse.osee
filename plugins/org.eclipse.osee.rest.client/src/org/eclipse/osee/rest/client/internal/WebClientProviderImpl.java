/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.core.net.proxy.IProxyChangeEvent;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import com.sun.jersey.api.client.config.ClientConfig;

/**
 * @author Roberto E. Escobar
 */
public class WebClientProviderImpl extends AbstractWebClientProvider {

   private Map<String, IProxyData[]> proxiedData;
   private IProxyService proxyService;

   public void setProxyService(IProxyService proxyService) {
      this.proxyService = proxyService;
   }

   public void start() {
      proxiedData = new ConcurrentHashMap<String, IProxyData[]>();
      proxyService.addProxyChangeListener(new IProxyChangeListener() {
         @Override
         public void proxyInfoChanged(IProxyChangeEvent event) {
            proxiedData.clear();
         }
      });
   }

   public void stop() {
      if (proxiedData != null) {
         proxiedData.clear();
      }
   }

   @Override
   protected void configure(URI uri, Map<String, Object> properties) {
      properties.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
   }

}
