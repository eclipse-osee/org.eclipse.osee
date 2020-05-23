/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jaxrs.client.internal.ext;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.eclipse.osee.jaxrs.client.JaxRsClient.JaxRsClientFactory;
import org.eclipse.osee.jaxrs.client.JaxRsClientConfig;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.eclipse.osee.jaxrs.client.internal.JaxRsClientConfigurator;

/**
 * @author Roberto E. Escobar
 */
public class CxfJaxRsClientFactory implements JaxRsClientFactory {

   private final JaxRsClientConfigurator configurator;
   private final Client client;
   private final JaxRsClientConfig config;

   public CxfJaxRsClientFactory(JaxRsClientConfigurator configurator) {
      this.configurator = configurator;

      config = new JaxRsClientConfig();
      config.setCreateThreadSafeProxyClients(true);

      ClientBuilder builder = ClientBuilder.newBuilder();
      configurator.configureClientBuilder(config, builder);

      client = builder.build();
   }

   public JaxRsClientConfig copyDefaultConfig() {
      return config.copy();
   }

   /**
    * Creates a JAX-RS WebTarget
    *
    * @param properties - configuration options
    * @param baseAddress - optional base target address
    * @return target
    */
   @Override
   public JaxRsWebTarget newTarget(JaxRsClientConfig config, String serverAddress) {
      return new JaxRsWebTargetImpl(newWebTarget(config, serverAddress));
   }

   @Override
   public WebTarget newWebTarget(JaxRsClientConfig config, String url) {
      WebTarget target = client.target(url);

      // This is here to force a webClient creation so we can configure the conduit
      target.request();

      configureConnection(config, target);
      return target;
   }

   @Override
   public WebTarget newWebTarget(String url) {
      return newWebTarget(config, url);
   }

   /**
    * Proxy sub-resource methods returning Objects can not be invoked. Prefer to have sub-resource methods returning
    * typed classes: interfaces, abstract classes or concrete implementations.
    *
    * @param properties - configuration options
    * @param baseAddress - proxy base address
    * @param clazz - JAX-RS annotated class used to create the client interface
    * @return targetProxy
    */
   @Override
   public <T> T newProxy(JaxRsClientConfig config, String url, Class<T> clazz) {
      JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
      configurator.configureBean(config, url, bean);
      bean.setServiceClass(clazz);
      T client = bean.create(clazz);
      configureConnection(config, client);
      return client;
   }

   @Override
   public <T> T newProxy(String url, Class<T> clazz) {
      return newProxy(config, url, clazz);
   }

   private void configureConnection(JaxRsClientConfig config, Object client) {
      ClientConfiguration clientConfig = WebClient.getConfig(client);
      HTTPConduit conduit = clientConfig.getHttpConduit();
      configurator.configureConnection(config, conduit);
      configurator.configureProxy(config, conduit);
   }
}