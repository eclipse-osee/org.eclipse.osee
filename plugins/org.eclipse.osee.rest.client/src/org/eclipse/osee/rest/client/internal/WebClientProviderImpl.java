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
import org.eclipse.osee.rest.client.WebClientProvider;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

/**
 * @author Roberto E. Escobar
 */
public class WebClientProviderImpl implements WebClientProvider {

   private Map<String, IProxyData[]> proxiedData;
   private IProxyService proxyService;
   private Client client;

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
   public WebResource createResource(URI uri) {
      Client client = createClient(uri);
      return client.resource(uri);
   }

   @Override
   public AsyncWebResource createAsyncResource(URI uri) {
      Client client = createClient(uri);
      return client.asyncResource(uri);
   }

   private Client createClient(URI uri) {
      if (client == null) {
         DefaultApacheHttpClientConfig clientConfig = new DefaultApacheHttpClientConfig();
         Map<String, Object> properties = clientConfig.getProperties();

         properties.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);

         //      configureProxyData(uri, properties);
         client = ApacheHttpClient.create(clientConfig);
      }
      return client;
   }

   //   private void configureProxyData(URI uri, Map<String, Object> properties) {
   //      boolean proxyBypass = OseeProperties.getOseeProxyBypassEnabled();
   //      if (!proxyBypass) {
   //         String key = String.format("%s_%s", uri.getScheme(), uri.getHost());
   //         IProxyData[] datas = proxiedData.get(key);
   //         if (datas == null) {
   //            datas = proxyService.select(uri);
   //            proxiedData.put(key, datas);
   //         }
   //
   //         for (IProxyData proxyData : datas) {
   //            String type = proxyData.getType();
   //            if (Strings.isValid(type) && type.startsWith("HTTP")) {
   //               String proxyURL =
   //                  String.format("%s://%s:%s", type.toLowerCase(), proxyData.getHost(), proxyData.getPort());
   //               properties.put(ApacheHttpClientConfig.PROPERTY_PROXY_URI, proxyURL);
   //            }
   //         }
   //      }
   //   }

}