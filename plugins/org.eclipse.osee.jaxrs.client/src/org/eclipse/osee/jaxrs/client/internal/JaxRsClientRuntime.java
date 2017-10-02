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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.client.JaxRsClient.JaxRsClientFactory;
import org.eclipse.osee.jaxrs.client.internal.ext.CxfJaxRsClientConfigurator;
import org.eclipse.osee.jaxrs.client.internal.ext.CxfJaxRsClientConfigurator.OAuthFactory;
import org.eclipse.osee.jaxrs.client.internal.ext.CxfJaxRsClientFactory;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2ClientRequestFilter;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2ClientRequestFilter.ClientAccessTokenCache;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Flows;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Flows.OwnerCredentials;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Serializer;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Transport;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsClientRuntime {

   private JaxRsClientRuntime() {
      //
   }

   public static final long MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS = 24L * 60L * 60L * 1000L; // one day
   private static volatile JaxRsClientFactory instance;

   public static JaxRsClientFactory getClientFactoryInstance() {
      if (instance == null) {
         instance = newClientFactory();
      }
      return instance;
   }

   private static JaxRsClientFactory newClientFactory() {
      OAuthFactory oauthFactory = newOAuthFactory();
      CxfJaxRsClientConfigurator configurator = new CxfJaxRsClientConfigurator(oauthFactory);
      configurator.configureJaxRsRuntime();
      configurator.configureDefaults(Collections.<String, Object> emptyMap());
      return new CxfJaxRsClientFactory(configurator);
   }

   private static OAuthFactory newOAuthFactory() {
      return new OAuthFactory() {

         @Override
         public OAuth2ClientRequestFilter newOAuthClientFilter(String username, String password, String clientId, String clientSecret, String authorizeUri, String tokenUri, String tokenValidationUri) {
            OwnerCredentials owner = newOwner(username, password);
            OAuthClientUtils.Consumer client = new OAuthClientUtils.Consumer(clientId, clientSecret);
            OAuth2Transport transport = new OAuth2Transport();
            OAuth2Flows flowManager =
               new OAuth2Flows(transport, owner, client, authorizeUri, tokenUri, tokenValidationUri);
            OAuth2Serializer serializer = new OAuth2Serializer();
            return new OAuth2ClientRequestFilter(flowManager, serializer);
         }

         @Override
         public ClientAccessTokenCache newClientAccessTokenCache(int cacheMaxSize, long cacheEvictTimeoutMillis) {
            final Cache<URI, ClientAccessToken> cache = newCache(cacheMaxSize, cacheEvictTimeoutMillis);
            return new ClientAccessTokenCache() {

               @Override
               public ClientAccessToken get(URI key) {
                  return cache.getIfPresent(key);
               }

               @Override
               public void store(URI key, ClientAccessToken value) {
                  cache.put(key, value);
               }
            };
         }

      };
   }

   private static <K, V> Cache<K, V> newCache(int cacheMaxSize, long cacheEvictTimeoutMillis) {
      Conditions.checkExpressionFailOnTrue(cacheMaxSize <= 0, "Token Cache max size must be greater than 0");
      Conditions.checkExpressionFailOnTrue(cacheEvictTimeoutMillis > MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS,
         "Token cache evict timeout exceeds max - [%s]", Lib.asTimeString(MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS));
      Conditions.checkExpressionFailOnTrue(cacheEvictTimeoutMillis <= 0,
         "Token cache evict timeout must be greater than 0");

      return CacheBuilder.newBuilder()//
         .maximumSize(cacheMaxSize)//
         .expireAfterWrite(cacheEvictTimeoutMillis, TimeUnit.MILLISECONDS)//
         .build();
   }

   private static OwnerCredentials newOwner(final String username, final String password) {
      return new OwnerCredentials() {

         @Override
         public String getUsername() {
            return username;
         }

         @Override
         public String getPassword() {
            return password;
         }
      };
   }
}