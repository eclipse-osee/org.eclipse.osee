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
package org.eclipse.osee.jaxrs.client.internal.ext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.ThreadLocalClientState;
import org.apache.cxf.transport.common.gzip.GZIPFeature;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jaxrs.JacksonFeature;
import org.eclipse.osee.jaxrs.client.JaxRsClientConfig;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ConnectionType;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ProxyType;
import org.eclipse.osee.jaxrs.client.internal.JaxRsClientConfigurator;

/**
 * @author Roberto E. Escobar
 */
public final class CxfJaxRsClientConfigurator implements JaxRsClientConfigurator {

   private static final String APACHE_CXF_LOGGER = "org.apache.cxf.Logger";
   private static final String JAVAX_WS_RS_CLIENT_BUILDER_PROPERTY = "javax.ws.rs.client.ClientBuilder";

   private static final String DEFAULT_JAXRS_CLIENT_BUILDER_IMPL = "org.apache.cxf.jaxrs.client.spec.ClientBuilderImpl";
   private static final String DEFAULT_CXF_LOGGING_IMPL = "org.apache.cxf.common.logging.Slf4jLogger";

   private List<? extends Object> providers;
   private List<Feature> features;
   private Map<String, Object> properties;

   public List<? extends Object> getProviders() {
      return providers;
   }

   public List<Feature> getFeatures() {
      return features;
   }

   public Map<String, Object> getProperties() {
      return properties;
   }

   @Override
   public void configureJaxRsRuntime() {
      System.setProperty(APACHE_CXF_LOGGER, DEFAULT_CXF_LOGGING_IMPL);

      // Ensure CXF JAX-RS implementation is loaded 
      RuntimeDelegate runtimeDelegate = new org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl();
      RuntimeDelegate.setInstance(runtimeDelegate);

      try {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         if (classLoader == null) {
            classLoader = getClass().getClassLoader();
         }
         classLoader.loadClass(DEFAULT_JAXRS_CLIENT_BUILDER_IMPL);
      } catch (ClassNotFoundException ex) {
         throw new OseeCoreException(ex, "Unable to find JAX-RS Client Builder implementation - [%s]",
            DEFAULT_JAXRS_CLIENT_BUILDER_IMPL);
      }
      System.setProperty(JAVAX_WS_RS_CLIENT_BUILDER_PROPERTY, DEFAULT_JAXRS_CLIENT_BUILDER_IMPL);
   }

   @Override
   public void configureDefaults(Map<String, Object> properties) {
      List<Object> providers = new ArrayList<Object>();
      providers.add(new GenericResponseExceptionMapper());
      providers.addAll(JacksonFeature.getProviders());
      this.providers = providers;

      List<Feature> features = new ArrayList<Feature>(2);
      LoggingFeature loggingFeature = new LoggingFeature();
      loggingFeature.setPrettyLogging(true);
      features.add(loggingFeature);
      features.add(new GZIPFeature());
      this.features = features;
      this.properties = new LinkedHashMap<String, Object>(properties);
   }

   @Override
   public void configureBean(JaxRsClientConfig config, String serverAddress, JAXRSClientFactoryBean bean) {
      Conditions.checkNotNullOrEmpty(serverAddress, "server address");
      bean.setAddress(serverAddress);

      bean.setProviders(getProviders());
      bean.setFeatures(getFeatures());
      bean.setProperties(getProperties());

      /**
       * If threadSafe is true then multiple threads can invoke on the same proxy or WebClient instance.
       */
      boolean threadSafe = config.isCreateThreadSafeProxyClients();
      if (threadSafe) {
         bean.setInitialState(new ThreadLocalClientState(serverAddress));
      }

      /**
       * InheritHeaders, indicates if the headers set by a current proxy will be inherited when a sub-resource proxy is
       * created vice versa.
       */
      boolean inheritHeaders = config.isProxyClientSubResourcesInheritHeaders();
      bean.setInheritHeaders(inheritHeaders);
   }

   @Override
   public void configureClientBuilder(JaxRsClientConfig config, ClientBuilder builder) {
      register(builder, getProviders());
      register(builder, getFeatures());
      register(builder, getProperties());
   }

   @Override
   public void configureConnection(JaxRsClientConfig config, HTTPConduit conduit) {
      HTTPClientPolicy policy1 = getClientPolicy(conduit);

      //@formatter:off
      policy1.setAllowChunking(config.isChunkingAllowed());
      policy1.setAsyncExecuteTimeout(config.getAsyncExecuteTimeout());
      policy1.setAsyncExecuteTimeoutRejection(config.isAsyncExecuteTimeoutRejection());
      policy1.setAutoRedirect(config.isFollowRedirectsAllowed());
      policy1.setChunkingThreshold(config.getChunkingThreshold());
      policy1.setChunkLength(config.getChunkLength());
      policy1.setConnection(asCxfConnectionType(config.getConnectionType()));
      policy1.setConnectionTimeout(config.getConnectionTimeout());
      policy1.setMaxRetransmits(config.getMaxRetransmits());
      policy1.setReceiveTimeout(config.getReceiveTimeout());
      //@formatter:on

      if (config.isServerAuthorizationRequired()) {
         AuthorizationPolicy policy2 = getAuthorizationPolicy(conduit);
         policy2.setUserName(config.getServerUsername());
         policy2.setPassword(config.getServerPassword());
         policy2.setAuthorizationType(config.getServerAuthorizationType());
      }
   }

   @Override
   public void configureProxy(JaxRsClientConfig config, HTTPConduit conduit) {
      if (config.isProxyRequired()) {
         HTTPClientPolicy policy1 = getClientPolicy(conduit);

         policy1.setProxyServer(config.getProxyAddress());
         int proxyPort = config.getProxyPort();
         if (proxyPort > 0) {
            policy1.setProxyServerPort(proxyPort);
         }
         policy1.setNonProxyHosts(config.getNonProxyHosts());
         policy1.setProxyServerType(asProxyServerType(config.getProxyType()));

         if (config.isProxyAuthorizationRequired()) {
            ProxyAuthorizationPolicy policy3 = getProxyAuthorizationPolicy(conduit);
            policy3.setUserName(config.getProxyUsername());
            policy3.setPassword(config.getProxyPassword());
            policy3.setAuthorizationType(config.getProxyAuthorizationType());
         }
      }
   }

   private static void register(ClientBuilder builder, Map<String, Object> properties) {
      for (Entry<String, Object> entry : properties.entrySet()) {
         builder.property(entry.getKey(), entry.getValue());
      }
   }

   private static void register(ClientBuilder builder, Iterable<? extends Object> objects) {
      for (Object object : objects) {
         builder.register(object);
      }
   }

   private static HTTPClientPolicy getClientPolicy(HTTPConduit conduit) {
      HTTPClientPolicy toReturn = conduit.getClient();
      if (toReturn == null) {
         toReturn = new HTTPClientPolicy();
         conduit.setClient(toReturn);
      }
      return toReturn;
   }

   private static AuthorizationPolicy getAuthorizationPolicy(HTTPConduit conduit) {
      AuthorizationPolicy toReturn = conduit.getAuthorization();
      if (toReturn == null) {
         toReturn = new AuthorizationPolicy();
         conduit.setAuthorization(toReturn);
      }
      return toReturn;
   }

   private static ProxyAuthorizationPolicy getProxyAuthorizationPolicy(HTTPConduit conduit) {
      ProxyAuthorizationPolicy toReturn = conduit.getProxyAuthorization();
      if (toReturn == null) {
         toReturn = new ProxyAuthorizationPolicy();
         conduit.setProxyAuthorization(toReturn);
      }
      return toReturn;
   }

   private static ProxyServerType asProxyServerType(ProxyType type) {
      ProxyServerType toReturn = ProxyServerType.HTTP;
      if (ProxyType.SOCKS == type) {
         toReturn = ProxyServerType.SOCKS;
      }
      return toReturn;
   }

   private static org.apache.cxf.transports.http.configuration.ConnectionType asCxfConnectionType(ConnectionType type) {
      org.apache.cxf.transports.http.configuration.ConnectionType toReturn =
         org.apache.cxf.transports.http.configuration.ConnectionType.KEEP_ALIVE;
      if (ConnectionType.CLOSE == type) {
         toReturn = org.apache.cxf.transports.http.configuration.ConnectionType.CLOSE;
      }
      return toReturn;
   }

}
