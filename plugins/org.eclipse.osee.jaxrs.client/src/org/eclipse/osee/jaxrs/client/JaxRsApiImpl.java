/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.jaxrs.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.WebTarget;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.spec.ClientImpl.WebTargetImpl;
import org.apache.cxf.rs.security.oauth2.client.Consumer;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
 * @author Ryan D. Brooks
 */
public final class JaxRsApiImpl implements JaxRsApi {
   private OrcsTokenService tokenService;
   private ObjectMapper mapper;
   private TypeFactory typeFactory;
   private CxfJaxRsClientFactory factory;
   private String baseUrl;

   public void setOrcsTokenService(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   public void start() {
      SimpleModule module = JsonUtil.createModule();

      JsonUtil.addDeserializer(module, AttributeTypeGeneric.class, tokenService::getAttributeType);
      JsonUtil.addDeserializer(module, AttributeTypeToken.class, tokenService::getAttributeType);
      JsonUtil.addDeserializer(module, AttributeTypeId.class, tokenService::getAttributeType);

      JsonUtil.addDeserializer(module, ArtifactTypeToken.class, tokenService::getArtifactType);
      JsonUtil.addDeserializer(module, ArtifactTypeId.class, tokenService::getArtifactType);

      JsonUtil.addDeserializer(module, RelationTypeToken.class, tokenService::getRelationType);

      mapper = JsonUtil.createStandardDateObjectMapper(module);
      typeFactory = mapper.getTypeFactory();
      factory = getClientFactoryInstance(mapper, tokenService, this);
      baseUrl = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER, OseeClient.DEFAULT_URL);
   }

   @Override
   public JsonNode readTree(String json) {
      return JsonUtil.readTree(mapper, json);
   }

   @Override
   public String toJson(Object object) {
      return JsonUtil.toJson(mapper, object);
   }

   @Override
   public <T> T readValue(String json, Class<T> valueType) {
      return JsonUtil.readValue(mapper, json, valueType);
   }

   @Override
   public <T> T readValue(String json, Class<? extends Collection> collectionClass, Class<?> elementClass) {
      try {
         return mapper.readValue(json, typeFactory.constructCollectionType(collectionClass, elementClass));
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public WebTarget newTarget(String path) {
      return factory.newWebTarget(url(path));
   }

   @Override
   public WebTarget newTargetUrl(String url) {
      return factory.newWebTarget(url);
   }

   @Override
   public WebTarget newTargetNoRedirect(String path) {
      JaxRsClientConfig config = factory.copyDefaultConfig();
      config.setFollowRedirects(false);
      return newTarget(path, config);
   }

   @Override
   public WebTarget newTarget(String... pathSegments) {
      return factory.newWebTarget(Collections.toString(pathSegments, baseUrl + "/", "/", null));
   }

   @Override
   public WebTarget newTargetQuery(String path, String... queryParams) {
      return newTargetUrlQuery(url(path), queryParams);
   }

   @Override
   public WebTarget newTargetUrlQuery(String url, String... queryParams) {
      StringBuilder strB = new StringBuilder(url);
      strB.append("?");

      boolean first = true;
      for (int x = 0; x < queryParams.length; x += 2) {
         if (first) {
            first = false;
         } else {
            strB.append("&");
         }
         strB.append(queryParams[x]);
         strB.append("=");
         strB.append(queryParams[x + 1]);
      }
      return newTargetUrl(strB.toString());
   }

   @Override
   public WebTarget newTargetUrlPasswd(String url, String serverUsername, String serverPassword) {
      JaxRsClientConfig config = factory.copyDefaultConfig();
      config.setServerPassword(serverPassword);
      config.setServerUsername(serverUsername);
      return factory.newWebTarget(config, url);
   }

   @Override
   public WebTarget newTargetPasswd(String path, String serverUsername, String serverPassword) {
      return newTargetUrlPasswd(url(path), serverUsername, serverPassword);
   }

   private WebTarget newTarget(String path, JaxRsClientConfig config) {
      return factory.newWebTarget(config, url(path));
   }

   private String url(String path) {
      return baseUrl + "/" + path;
   }

   @Override
   public <T> T newProxy(WebTarget target, Class<T> clazz) {
      // This is here to force a webClient to store its configuration
      target.request();

      if (target instanceof WebTargetImpl) {
         return JAXRSClientFactory.fromClient(((WebTargetImpl) target).getWebClient(), clazz);
      }
      throw new OseeStateException("%s is of type %s not WebTargetImpl", target, target.getClass());
   }

   @Override
   public ObjectMapper getObjectMapper() {
      return mapper;
   }

   @Override
   public <T> T newProxy(String path, Class<T> clazz) {
      return factory.newProxy(url(path), clazz);
   }

   private static final long MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS = 24L * 60L * 60L * 1000L; // one day

   private CxfJaxRsClientFactory getClientFactoryInstance(ObjectMapper mapper, OrcsTokenService tokenService, JaxRsApi jaxRsApi) {
      OAuthFactory oauthFactory = newOAuthFactory(jaxRsApi);
      CxfJaxRsClientConfigurator configurator = new CxfJaxRsClientConfigurator(oauthFactory, tokenService);
      configurator.configureJaxRsRuntime();
      configurator.configureDefaults(mapper);
      return new CxfJaxRsClientFactory(configurator);
   }

   private static OAuthFactory newOAuthFactory(JaxRsApi jaxRsApi) {
      return new OAuthFactory() {

         @Override
         public OAuth2ClientRequestFilter newOAuthClientFilter(String username, String password, String clientId, String clientSecret, String authorizeUri, String tokenUri, String tokenValidationUri) {
            OwnerCredentials owner = newOwner(username, password);
            Consumer client = new Consumer(clientId, clientSecret);
            OAuth2Transport transport = new OAuth2Transport(jaxRsApi);
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