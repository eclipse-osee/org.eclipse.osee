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

package org.eclipse.osee.jaxrs;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.client.spec.ClientImpl.WebTargetImpl;
import org.apache.cxf.rs.security.oauth2.client.Consumer;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.transport.http.HTTPConduit;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.CxfJaxRsClientConfigurator.OAuthFactory;
import org.eclipse.osee.jaxrs.OAuth2ClientRequestFilter.ClientAccessTokenCache;
import org.eclipse.osee.jaxrs.OAuth2Flows.OwnerCredentials;

/**
 * @author Ryan D. Brooks
 */
public final class JaxRsApiImpl implements JaxRsApi {
   private OrcsTokenService tokenService;
   private ObjectMapper mapper;
   private TypeFactory typeFactory;
   private String baseUrl;
   private Client client;
   private CxfJaxRsClientConfigurator configurator;
   private JaxRsClientConfig config;

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
      baseUrl = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER, OseeClient.DEFAULT_URL);
   }

   @Override
   public JsonNode readTree(String json) {
      try {
         return mapper.readTree(json);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
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
   public String readValue(String json, String key) {
      JsonNode node = readTree(json).get(key);
      if (node.isValueNode()) {
         return node.textValue();
      }
      return node.toString();
   }

   @Override
   public <T, C extends Collection<T>> C readCollectionValue(String json, Class<? extends Collection> collectionClass, Class<T> elementClass) {
      try {
         return mapper.readValue(json, typeFactory.constructCollectionType(collectionClass, elementClass));
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public <K, V> Map<K, V> readMapValue(String json, Class<K> keyClass, Class<V> valueClass) {
      try {
         return mapper.readValue(json, typeFactory.constructMapType(Map.class, keyClass, valueClass));
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public WebTarget newTarget(String path) {
      return newWebTarget(config, url(path));
   }

   @Override
   public WebTarget newTargetUrl(String url) {
      return newWebTarget(config, url);
   }

   @Override
   public WebTarget newTargetNoRedirect(String path) {
      JaxRsClientConfig modifiedConfig = config.copy();
      modifiedConfig.setFollowRedirects(false);

      return newWebTarget(modifiedConfig, url(path));
   }

   @Override
   public WebTarget newTarget(String... pathSegments) {
      return newWebTarget(config, Collections.toString(pathSegments, baseUrl + "/", "/", null));
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
      JaxRsClientConfig modifiedConfig = config.copy();
      modifiedConfig.setServerPassword(serverPassword);
      modifiedConfig.setServerUsername(serverUsername);
      return newWebTarget(modifiedConfig, url);
   }

   @Override
   public WebTarget newTargetPasswd(String path, String serverUsername, String serverPassword) {
      return newTargetUrlPasswd(url(path), serverUsername, serverPassword);
   }

   public WebTarget newWebTarget(JaxRsClientConfig config, String url) {
      url = url.replaceAll(" ", "%20");
      WebTarget target = client.target(url);

      // This is here to force a webClient creation so we can configure the conduit
      target.request();

      configureConnection(config, target);
      return target;
   }

   private void configureConnection(JaxRsClientConfig config, Object client) {
      ClientConfiguration clientConfig = WebClient.getConfig(client);
      HTTPConduit conduit = clientConfig.getHttpConduit();
      configurator.configureConnection(config, conduit);
      configurator.configureProxy(config, conduit);
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
      return newProxy(config, url(path), clazz);
   }

   @Override
   public JsonFactory getFactory() {
      return mapper.getFactory();
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
   private <T> T newProxy(JaxRsClientConfig config, String url, Class<T> clazz) {
      JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
      configurator.configureBean(config, url, bean);
      bean.setServiceClass(clazz);
      T client = bean.create(clazz);
      configureConnection(config, client);
      return client;
   }

   private static final long MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS = 24L * 60L * 60L * 1000L; // one day

   /**
    * Must only be called once on the client during startup. May be called by the server with a null UserService.
    *
    * @param userService May be null if running on server
    */
   @Override
   public void createClientFactory(UserService userService) {
      OAuthFactory oauthFactory = newOAuthFactory();
      configurator = new CxfJaxRsClientConfigurator(oauthFactory);
      configurator.configureJaxRsRuntime();
      configurator.configureDefaults(mapper, tokenService, userService);

      config = new JaxRsClientConfig();
      config.setCreateThreadSafeProxyClients(true);

      ClientBuilder builder = ClientBuilder.newBuilder();
      configurator.configureClientBuilder(config, builder);
      client = builder.build();
   }

   private OAuthFactory newOAuthFactory() {
      return new OAuthFactory() {

         @Override
         public OAuth2ClientRequestFilter newOAuthClientFilter(String username, String password, String clientId, String clientSecret, String authorizeUri, String tokenUri, String tokenValidationUri) {
            OwnerCredentials owner = newOwner(username, password);
            Consumer client = new Consumer(clientId, clientSecret);
            OAuth2Transport transport = new OAuth2Transport(JaxRsApiImpl.this);
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