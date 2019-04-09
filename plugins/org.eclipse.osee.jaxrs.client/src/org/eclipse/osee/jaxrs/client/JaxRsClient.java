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
package org.eclipse.osee.jaxrs.client;

import java.net.URI;
import java.util.Map;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ConnectionType;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ProxyType;
import org.eclipse.osee.jaxrs.client.internal.JaxRsClientRuntime;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsClient {

   public interface JaxRsClientFactory {

      <T> T newClient(JaxRsClientConfig config, String serverAddress, Class<T> clazz);

      JaxRsWebTarget newTarget(JaxRsClientConfig config, String serverAddress);
   }

   public static JaxRsClientBuilder newBuilder() {

      JaxRsClientFactory factory = JaxRsClientRuntime.getClientFactoryInstance();
      return new JaxRsClientBuilder(factory);
   }

   public static JaxRsClientBuilder newBuilder(JaxRsClientConfig config) {
      return newBuilder().withConfig(config);
   }

   public static JaxRsClientBuilder newBuilder(Map<String, Object> properties) {
      return newBuilder().properties(properties);
   }

   public static JaxRsClient newClient() {
      return newBuilder().build();
   }

   public static JaxRsClient fromConfig(JaxRsClientConfig config) {
      return newBuilder(config).build();
   }

   public static JaxRsClient fromProperties(Map<String, Object> properties) {
      return newBuilder(properties).build();
   }

   private final JaxRsClientConfig config;
   private final JaxRsClientFactory factory;
   private static Long accountId;
   private static Long clientId;

   protected JaxRsClient(JaxRsClientFactory factory, JaxRsClientConfig config) {
      super();
      this.config = config;
      this.factory = factory;
   }

   /**
    * Creates a JAX-RS WebTarget
    *
    * @return target
    */
   public JaxRsWebTarget target() {
      return target((String) null);
   }

   /**
    * Creates a JAX-RS WebTarget
    *
    * @param baseAddress
    * @return target
    */
   public JaxRsWebTarget target(URI address) {
      return target(address != null ? address.toString() : null);
   }

   /**
    * Creates a JAX-RS WebTarget
    *
    * @param baseAddress
    * @return target
    */
   public JaxRsWebTarget target(String address) {
      return factory.newTarget(config, address);
   }

   /**
    * Proxy sub-resource methods returning Objects can not be invoked. Prefer to have sub-resource methods returning
    * typed classes: interfaces, abstract classes or concrete implementations.
    *
    * @param endpointAddress - address to the endpoint represented by clazz
    * @param clazz - JAX-RS annotated class used to create a proxy client
    * @return targetProxy
    */
   public <T> T targetProxy(URI address, Class<T> clazz) {
      return targetProxy(address != null ? address.toString() : null, clazz);
   }

   /**
    * Proxy sub-resource methods returning Objects can not be invoked. Prefer to have sub-resource methods returning
    * typed classes: interfaces, abstract classes or concrete implementations.
    *
    * @param endpointAddress - address to the endpoint represented by clazz
    * @param clazz - JAX-RS annotated class used to create a proxy client
    * @return targetProxy
    */
   public <T> T targetProxy(String address, Class<T> clazz) {
      return factory.newClient(config, address, clazz);
   }

   /**
    * JAX-RS Client configuration
    *
    * @return config
    */
   public JaxRsClientConfig getConfig() {
      return config;
   }

   public static final class JaxRsClientBuilder extends JaxRsClientConfig {

      private final JaxRsClientFactory factory;

      protected JaxRsClientBuilder(JaxRsClientFactory factory) {
         super();
         this.factory = factory;
      }

      private JaxRsClientBuilder withConfig(JaxRsClientConfig config) {
         this.copy(config);
         return this;
      }

      public JaxRsClientBuilder properties(Map<String, Object> src) {
         readProperties(src);
         return this;
      }

      public JaxRsClientBuilder asyncExecTimeout(long asyncExecuteTimeout) {
         setAsyncExecuteTimeout(asyncExecuteTimeout);
         return this;
      }

      public JaxRsClientBuilder asyncExecTimeoutRejection(boolean asyncExecuteTimeoutRejection) {
         setAsyncExecuteTimeoutRejection(asyncExecuteTimeoutRejection);
         return this;
      }

      public JaxRsClientBuilder followRedirects(boolean followRedirectsAutomatically) {
         setFollowRedirects(followRedirectsAutomatically);
         return this;
      }

      public JaxRsClientBuilder allowChunking(boolean chunkingAllowed) {
         setChunkingAllowed(chunkingAllowed);
         return this;
      }

      public JaxRsClientBuilder chunkingThreshold(int chunkingThreshold) {
         setChunkingThreshold(chunkingThreshold);
         return this;
      }

      public JaxRsClientBuilder chunkLength(int chunkLength) {
         setChunkLength(chunkLength);
         return this;
      }

      public JaxRsClientBuilder connectionTimeout(long connectionTimeoutInMillis) {
         setConnectionTimeout(connectionTimeoutInMillis);
         return this;
      }

      public JaxRsClientBuilder connectionType(ConnectionType connectionType) {
         setConnectionType(connectionType);
         return this;
      }

      public JaxRsClientBuilder maxRetransmits(int maxRetransmits) {
         setMaxRetransmits(maxRetransmits);
         return this;
      }

      public JaxRsClientBuilder nonProxyHosts(String nonProxyHosts) {
         setNonProxyHosts(nonProxyHosts);
         return this;
      }

      public JaxRsClientBuilder proxyAddress(String proxyAddress) {
         setProxyAddress(proxyAddress);
         return this;
      }

      public JaxRsClientBuilder proxyAuthorizationType(String proxyAuthorizationType) {
         setProxyAuthorizationType(proxyAuthorizationType);
         return this;
      }

      public JaxRsClientBuilder proxyPassword(String proxyPassword) {
         setProxyPassword(proxyPassword);
         return this;
      }

      public JaxRsClientBuilder proxyType(ProxyType proxyType) {
         setProxyType(proxyType);
         return this;
      }

      public JaxRsClientBuilder proxyUsername(String proxyUsername) {
         setProxyUsername(proxyUsername);
         return this;
      }

      public JaxRsClientBuilder receiveTimeout(long receiveTimeoutInMillis) {
         setReceiveTimeout(receiveTimeoutInMillis);
         return this;
      }

      public JaxRsClientBuilder authorizationType(String serverAuthorizationType) {
         setServerAuthorizationType(serverAuthorizationType);
         return this;
      }

      public JaxRsClientBuilder password(String serverPassword) {
         setServerPassword(serverPassword);
         return this;
      }

      public JaxRsClientBuilder username(String serverUsername) {
         setServerUsername(serverUsername);
         return this;
      }

      public JaxRsClientBuilder createThreadSafeProxyClients(boolean createThreadSafeClients) {
         setCreateThreadSafeProxyClients(createThreadSafeClients);
         return this;
      }

      public JaxRsClientBuilder proxyClientSubResourcesInheritHeaders(boolean inheritHeaders) {
         setProxyClientSubResourcesInheritHeaders(inheritHeaders);
         return this;
      }

      public JaxRsClientBuilder oAuthAuthorizeUri(String oauthAuthorizeUri) {
         setOAuthAuthorizeUri(oauthAuthorizeUri);
         return this;
      }

      public JaxRsClientBuilder oAuthTokenUri(String oauthTokenUri) {
         setOAuthTokenUri(oauthTokenUri);
         return this;
      }

      public JaxRsClientBuilder oAuthTokenValidationUri(String oauthTokenValidationUri) {
         setOAuthTokenValidationUri(oauthTokenValidationUri);
         return this;
      }

      public JaxRsClientBuilder oAuthClientId(String oauthClientId) {
         setOAuthClientId(oauthClientId);
         return this;
      }

      public JaxRsClientBuilder oAuthClientSecret(String oauthClientSecret) {
         setOAuthClientSecret(oauthClientSecret);
         return this;
      }

      public JaxRsClientBuilder oAuthRedirectUri(String oauthRedirectUri) {
         setOAuthRedirectUri(oauthRedirectUri);
         return this;
      }

      public JaxRsClientBuilder oAuthScopes(String oauthScopes) {
         setOAuthScopes(oauthScopes);
         return this;
      }

      public JaxRsClientBuilder oAuthSecretKeyAlgorithm(String oauthSecretKeyAlgorithm) {
         setOAuthSecretKeyAlgorithm(oauthSecretKeyAlgorithm);
         return this;
      }

      public JaxRsClientBuilder oAuthEncodedSecretKey(String oauthEncodedSecretKey) {
         setOAuthEncodedSecretKey(oauthEncodedSecretKey);
         return this;
      }

      public JaxRsClientBuilder oAuthFailOnRefreshTokenError(boolean oauthFailsOnRefreshTokenError) {
         setOAuthFailsOnRefreshTokenError(oauthFailsOnRefreshTokenError);
         return this;
      }

      public JaxRsClientBuilder oAuthTokenStore(JaxRsTokenStore oauthTokenStore) {
         setOAuthTokenStore(oauthTokenStore);
         return this;
      }

      public JaxRsClientBuilder oAuthConfirmHandler(JaxRsConfirmAccessHandler oauthTokenHandler) {
         setOAuthConfirmAccessHandler(oauthTokenHandler);
         return this;
      }

      public JaxRsClientBuilder oAuthCacheEnabled(boolean oauthCacheEnabled) {
         setOAuthCacheEnabled(oauthCacheEnabled);
         return this;
      }

      public JaxRsClientBuilder oAuthCacheMaxSize(int oauthCacheMaxSize) {
         setOAuthCacheMaxSize(oauthCacheMaxSize);
         return this;
      }

      public JaxRsClientBuilder oAuthCacheEvictTimeoutMillis(long oauthCacheEvictTimeoutMillis) {
         setOAuthCacheEvictTimeoutMillis(oauthCacheEvictTimeoutMillis);
         return this;
      }

      public JaxRsClient build() {
         return new JaxRsClient(factory, copy());
      }
   }

   public static Long getAccountId() {
      return accountId;
   }

   public static void setAccountId(Long accountId) {
      JaxRsClient.accountId = accountId;
   }

   public static Long getClientId() {
      return clientId;
   }

   public static void setClientId(Long clientId) {
      JaxRsClient.clientId = clientId;
   }
}