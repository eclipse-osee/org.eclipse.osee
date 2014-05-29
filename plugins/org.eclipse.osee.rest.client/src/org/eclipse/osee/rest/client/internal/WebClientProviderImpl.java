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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jaxrs.ErrorResponse;
import org.eclipse.osee.rest.client.WebClientProvider;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

/**
 * @author Roberto E. Escobar
 */
public  class WebClientProviderImpl implements WebClientProvider {

   private Client client;

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

   @Override
   public RuntimeException handleException(UniformInterfaceException ex) {
      String message = null;
      try {
         ClientResponse response = ex.getResponse();
         ErrorResponse error = response.getEntity(ErrorResponse.class);
         message = error != null ? error.toString() : "Error message not available.";
      } catch (Throwable th) {
         message = String.format("Error Response object not available - [%s]", th.getLocalizedMessage());
      }
      return new OseeCoreException(ex, message);
   }

   protected void configure(URI uri, Map<String, Object> properties) {
      properties.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
   }

   private Client createClient(URI uri) {
      if (client == null) {
         DefaultApacheHttpClientConfig clientConfig = new DefaultApacheHttpClientConfig();
         Map<String, Object> properties = clientConfig.getProperties();
         properties.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
         configure(uri, properties);
         client = ApacheHttpClient.create(clientConfig);
      }
      return client;
   }

}
