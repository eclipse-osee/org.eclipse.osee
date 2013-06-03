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
package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.Map;

import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

public class WebClientProviderImpl implements WebClientProvider {

   private Client client;
   
   public void start() {
      
   }

   public void stop() {
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

         client = ApacheHttpClient.create(clientConfig);
      }
      return client;
   }

}