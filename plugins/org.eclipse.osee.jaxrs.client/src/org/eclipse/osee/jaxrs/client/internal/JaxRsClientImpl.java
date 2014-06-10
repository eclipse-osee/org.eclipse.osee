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
package org.eclipse.osee.jaxrs.client.internal;

import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_CONNECT_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_READ_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_THREADPOOL_SIZE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_CONNECT_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_FOLLOW_REDIRECTS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_PROXY_SERVER_ADDRESS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_READ_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_SERVER_ADDRESS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_THREADPOOL_SIZE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientUtils.get;
import static org.eclipse.osee.jaxrs.client.JaxRsClientUtils.getBoolean;
import static org.eclipse.osee.jaxrs.client.JaxRsClientUtils.getInt;
import java.net.URI;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.ErrorResponse;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsClientUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

/**
 * @author Roberto E. Escobar
 */
public class JaxRsClientImpl implements JaxRsClient {

   public interface JaxRsClientHolder {
      WebResource target(URI uri);
   }

   private volatile JaxRsClientHolder client;

   @Override
   public WebResource createResource(URI uri) {
      return client.target(uri);
   }

   public void configure(Map<String, Object> properties) {
      client = newJaxRsClient(properties);
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

   private JaxRsClientHolder newJaxRsClient(Map<String, Object> props) {
      DefaultApacheHttpClientConfig clientConfig = new DefaultApacheHttpClientConfig();
      Map<String, Object> properties = clientConfig.getProperties();

      String proxyAddress = get(props, JAXRS_CLIENT_PROXY_SERVER_ADDRESS, "");
      if (Strings.isValid(proxyAddress)) {
         properties.put(ApacheHttpClientConfig.PROPERTY_PROXY_URI, proxyAddress);
      }

      //@formatter:off
      properties.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, getBoolean(props, JAXRS_CLIENT_FOLLOW_REDIRECTS, DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS));
      properties.put(ClientConfig.PROPERTY_THREADPOOL_SIZE, getInt(props, JAXRS_CLIENT_THREADPOOL_SIZE, DEFAULT_JAXRS_CLIENT_THREADPOOL_SIZE));
      properties.put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, minZero(getInt(props, JAXRS_CLIENT_CONNECT_TIMEOUT, DEFAULT_JAXRS_CLIENT_CONNECT_TIMEOUT)));
      properties.put(ClientConfig.PROPERTY_READ_TIMEOUT, minZero(getInt(props, JAXRS_CLIENT_READ_TIMEOUT, DEFAULT_JAXRS_CLIENT_READ_TIMEOUT)));
      //@formatter:on

      Client delegate = ApacheHttpClient.create(clientConfig);
      JaxRsClientHolder toReturn;
      String serverAddress = JaxRsClientUtils.get(props, JAXRS_CLIENT_SERVER_ADDRESS, "");
      if (Strings.isValid(serverAddress)) {
         toReturn = newClient(delegate, serverAddress);
      } else {
         toReturn = newClient(delegate);
      }
      return toReturn;
   }

   private static int minZero(int value) {
      return value < 0 ? 0 : value;
   }

   private JaxRsClientHolder newClient(final Client delegate) {
      return new JaxRsClientHolder() {

         @Override
         public WebResource target(URI uri) {
            return delegate.resource(uri);
         }
      };
   }

   private JaxRsClientHolder newClient(final Client delegate, final String serverAddress) {
      return new JaxRsClientHolder() {

         private boolean isPartialAddress(URI uri) {
            return uri != null && !uri.isAbsolute();
         }

         @Override
         public WebResource target(URI uri) {
            URI uriToContact = uri;
            if (isPartialAddress(uriToContact)) {
               uriToContact = UriBuilder.fromUri(serverAddress).path(uri.toASCIIString()).build();
            }
            return delegate.resource(uriToContact);
         }
      };
   }
}
