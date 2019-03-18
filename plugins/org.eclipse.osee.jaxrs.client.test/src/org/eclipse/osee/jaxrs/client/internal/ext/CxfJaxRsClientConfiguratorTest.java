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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.ThreadLocalClientState;
import org.apache.cxf.rs.security.oauth2.provider.OAuthContextProvider;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJSONProvider;
import org.apache.cxf.transport.common.gzip.GZIPFeature;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jaxrs.client.JaxRsClientConfig;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ConnectionType;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ProxyType;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler;
import org.eclipse.osee.jaxrs.client.JaxRsTokenStore;
import org.eclipse.osee.jaxrs.client.internal.OseeAccountClientRequestFilter;
import org.eclipse.osee.jaxrs.client.internal.ext.CxfJaxRsClientConfigurator.OAuthFactory;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2ClientRequestFilter.ClientAccessTokenCache;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link CxfJaxRsClientConfigurator}
 *
 * @author Roberto E. Escobar
 */
public class CxfJaxRsClientConfiguratorTest {

   private static final String SERVER_ADDRESS = "http://hello.com";

   private static final long ASYNC_EXECUTE_TIMEOUT = 32423L;
   private static final boolean ASYNC_EXECUTE_TIMEOUT_REJECTION = true;
   private static final int CHUNK_SIZE = 864;
   private static final boolean CHUNKING_ALLOWED = true;
   private static final int CHUNKING_THRESHOLD = 9872394;
   private static final long CONNECTION_TIMEOUT = 2327L;
   private static final ConnectionType CONNECTION_TYPE = ConnectionType.CLOSE;
   private static final boolean FOLLOW_REDIRECTS = false;
   private static final int MAX_RETRANSMITS = 452;
   private static final String NON_PROXY_HOSTS = "non-proxy-hosts";
   private static final String PROXY_ADDRESS = "proxy-address.com";
   private static final int PROXY_PORT = 78612;
   private static final String PROXY_AUTHORIZATION_TYPE = "proxy-authentication-type";
   private static final String PROXY_PASSWORD = "proxy-password";
   private static final ProxyType PROXY_TYPE = ProxyType.SOCKS;
   private static final String PROXY_USERNAME = "proxy-username";
   private static final long RECEIVE_TIMEOUT = 87532L;
   private static final String SERVER_AUTHORIZATION_TYPE = "server-authentication-type";
   private static final String SERVER_PASSWORD = "server-password";
   private static final String SERVER_USERNAME = "server-username";

   private static final String AUTHORIZE_URI = "authorize_uri";
   private static final boolean FAIL_ON_REFRESH_ERROR = true;
   private static final boolean CACHE_ENABLED = true;
   private static final long CACHE_EVICT_TIMEOUT = 1309L;
   private static final int CACHE_MAX_SIZE = 7652;
   private static final String CLIENT_ID = "client_id";
   private static final String CLIENT_SECRET = "client_secret";
   private static final String SECRET_KEY = "encoded_secret_key";
   private static final String REDIRECT_URI = "redirect_uri";
   private static final String SCOPES = "scopes";
   private static final String SECRET_ALGORITHM = "secret_algorithm";
   private static final String TOKEN_URI = "token_uri";
   private static final String VALIDATION_URI = "validation_uri";

   private static final ProxyServerType CXF_PROXY_SERVER_TYPE = ProxyServerType.SOCKS;
   private static final org.apache.cxf.transports.http.configuration.ConnectionType CXF_CONNECTION_TYPE =
      org.apache.cxf.transports.http.configuration.ConnectionType.CLOSE;

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private JaxRsClientConfig config;
   @Mock private HTTPConduit conduit;
   @Mock private ClientBuilder builder;
   @Mock private JAXRSClientFactoryBean bean;

   @Mock private HTTPClientPolicy policy1;
   @Mock private AuthorizationPolicy policy2;
   @Mock private ProxyAuthorizationPolicy policy3;
   @Mock private OAuthFactory oauthFactory;
   @Mock private OAuth2ClientRequestFilter filter;
   @Mock private JaxRsConfirmAccessHandler handler;
   @Mock private JaxRsTokenStore store;

   @Captor private ArgumentCaptor<Object> captor;
   @Captor private ArgumentCaptor<HTTPClientPolicy> captor1;
   @Captor private ArgumentCaptor<AuthorizationPolicy> captor2;
   @Captor private ArgumentCaptor<ProxyAuthorizationPolicy> captor3;
   @Captor private ArgumentCaptor<List<Object>> captor4;
   //@formatter:on

   private Map<String, Object> props;
   private CxfJaxRsClientConfigurator configurator;
   private String mapAsString;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      configurator = new CxfJaxRsClientConfigurator(oauthFactory);

      props = new LinkedHashMap<>();
      props.put("a", "1");
      props.put("b", "2");
      props.put("c", "3");
      mapAsString = props.toString();
   }

   @Test
   public void testConfigureJaxRsRuntime() {
      configurator.configureJaxRsRuntime();

      assertEquals(org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl.class, RuntimeDelegate.getInstance().getClass());
      assertEquals("org.apache.cxf.jaxrs.client.spec.ClientBuilderImpl",
         System.getProperty("javax.ws.rs.client.ClientBuilder"));
   }

   @Test
   public void testConfigureDefaults() {
      configurator.configureDefaults(props);

      Iterator<? extends Object> iterator = configurator.getFeatures().iterator();
      assertEquals(LoggingFeature.class, iterator.next().getClass());
      assertEquals(GZIPFeature.class, iterator.next().getClass());

      iterator = configurator.getProviders().iterator();
      assertEquals(GenericResponseExceptionMapper.class, iterator.next().getClass());
      assertEquals(JacksonJaxbJsonProvider.class, iterator.next().getClass());
      assertEquals(JsonParseExceptionMapper.class, iterator.next());
      assertEquals(JsonMappingExceptionMapper.class, iterator.next());

      assertEquals(mapAsString, configurator.getProperties().toString());
   }

   @Test
   public void testConfigureBean() {
      when(config.isCreateThreadSafeProxyClients()).thenReturn(true);
      when(config.isProxyClientSubResourcesInheritHeaders()).thenReturn(true);

      configurator.configureBean(config, SERVER_ADDRESS, bean);

      verify(bean).setProviders(configurator.getProviders());
      verify(bean).setFeatures(configurator.getFeatures());
      verify(bean).setProperties(configurator.getProperties());
      verify(bean).setInheritHeaders(true);
      verify(bean).setInitialState(Matchers.<ThreadLocalClientState> any());

      verify(config).isCreateThreadSafeProxyClients();
      verify(config).isProxyClientSubResourcesInheritHeaders();
   }

   @Test
   public void testConfigureBeanOptionsFalse() {
      when(config.isCreateThreadSafeProxyClients()).thenReturn(false);
      when(config.isProxyClientSubResourcesInheritHeaders()).thenReturn(false);

      configurator.configureBean(config, SERVER_ADDRESS, bean);

      verify(bean).setProviders(configurator.getProviders());
      verify(bean).setFeatures(configurator.getFeatures());
      verify(bean).setProperties(configurator.getProperties());
      verify(bean).setInheritHeaders(false);
      verify(bean, times(0)).setInitialState(Matchers.<ThreadLocalClientState> any());

      verify(config).isCreateThreadSafeProxyClients();
      verify(config).isProxyClientSubResourcesInheritHeaders();
   }

   @Test
   public void testConfigureBeanServerAddressNull() {
      thrown.expect(OseeCoreException.class);
      thrown.expectMessage("server address cannot be null");
      configurator.configureBean(config, null, bean);
   }

   @Test
   public void testConfigureBeanServerAddressEmpty() {
      thrown.expect(OseeCoreException.class);
      thrown.expectMessage("server address cannot be empty");
      configurator.configureBean(config, "", bean);
   }

   @Test
   public void testConfigureClientBuilder() {
      configurator.configureDefaults(props);
      configurator.configureClientBuilder(config, builder);

      verify(builder, times(10)).register(captor.capture());

      List<Object> actual = captor.getAllValues();
      assertEquals(10, actual.size());

      Iterator<Object> iterator = actual.iterator();
      assertEquals(GenericResponseExceptionMapper.class, iterator.next().getClass());
      assertEquals(JacksonJaxbJsonProvider.class, iterator.next().getClass());
      assertEquals(JsonParseExceptionMapper.class, iterator.next());
      assertEquals(JsonMappingExceptionMapper.class, iterator.next());
      assertEquals(OAuthJSONProvider.class, iterator.next().getClass());
      assertEquals(OAuthContextProvider.class, iterator.next().getClass());
      assertEquals(OrcsParamConverterProvider.class, iterator.next().getClass());
      assertEquals(OseeAccountClientRequestFilter.class, iterator.next().getClass());
      assertEquals(LoggingFeature.class, iterator.next().getClass());
      assertEquals(GZIPFeature.class, iterator.next().getClass());

      verify(builder).property("a", "1");
      verify(builder).property("b", "2");
      verify(builder).property("c", "3");
   }

   @Test
   public void testConfigureConduitWithAuthorization() {
      when(conduit.getClient()).thenReturn(policy1);
      when(conduit.getAuthorization()).thenReturn(policy2);

      when(config.isChunkingAllowed()).thenReturn(CHUNKING_ALLOWED);
      when(config.getAsyncExecuteTimeout()).thenReturn(ASYNC_EXECUTE_TIMEOUT);
      when(config.isAsyncExecuteTimeoutRejection()).thenReturn(ASYNC_EXECUTE_TIMEOUT_REJECTION);
      when(config.isFollowRedirectsAllowed()).thenReturn(FOLLOW_REDIRECTS);
      when(config.getChunkingThreshold()).thenReturn(CHUNKING_THRESHOLD);
      when(config.getChunkLength()).thenReturn(CHUNK_SIZE);
      when(config.getConnectionType()).thenReturn(CONNECTION_TYPE);
      when(config.getConnectionTimeout()).thenReturn(CONNECTION_TIMEOUT);
      when(config.getMaxRetransmits()).thenReturn(MAX_RETRANSMITS);
      when(config.getReceiveTimeout()).thenReturn(RECEIVE_TIMEOUT);

      when(config.isServerAuthorizationRequired()).thenReturn(true);
      when(config.getServerUsername()).thenReturn(SERVER_USERNAME);
      when(config.getServerPassword()).thenReturn(SERVER_PASSWORD);
      when(config.getServerAuthorizationType()).thenReturn(SERVER_AUTHORIZATION_TYPE);

      configurator.configureConnection(config, conduit);

      verify(conduit).getClient();
      verify(policy1).setAllowChunking(CHUNKING_ALLOWED);
      verify(policy1).setAsyncExecuteTimeout(ASYNC_EXECUTE_TIMEOUT);
      verify(policy1).setAsyncExecuteTimeoutRejection(ASYNC_EXECUTE_TIMEOUT_REJECTION);
      verify(policy1).setAutoRedirect(FOLLOW_REDIRECTS);
      verify(policy1).setChunkingThreshold(CHUNKING_THRESHOLD);
      verify(policy1).setChunkLength(CHUNK_SIZE);
      verify(policy1).setConnection(CXF_CONNECTION_TYPE);
      verify(policy1).setConnectionTimeout(CONNECTION_TIMEOUT);
      verify(policy1).setMaxRetransmits(MAX_RETRANSMITS);
      verify(policy1).setReceiveTimeout(RECEIVE_TIMEOUT);

      verify(conduit).getAuthorization();
      verify(policy2).setUserName(SERVER_USERNAME);
      verify(policy2).setPassword(SERVER_PASSWORD);
      verify(policy2).setAuthorizationType(SERVER_AUTHORIZATION_TYPE);

      verify(conduit, times(0)).getProxyAuthorization();
      verify(conduit, times(0)).setClient(Matchers.<HTTPClientPolicy> any());
      verify(conduit, times(0)).setAuthorization(Matchers.<AuthorizationPolicy> any());
      verify(conduit, times(0)).setProxyAuthorization(Matchers.<ProxyAuthorizationPolicy> any());
   }

   @Test
   public void testConfigureConduitNoAuthorization() {
      when(conduit.getClient()).thenReturn(policy1);
      when(conduit.getAuthorization()).thenReturn(policy2);

      when(config.isChunkingAllowed()).thenReturn(CHUNKING_ALLOWED);
      when(config.getAsyncExecuteTimeout()).thenReturn(ASYNC_EXECUTE_TIMEOUT);
      when(config.isAsyncExecuteTimeoutRejection()).thenReturn(ASYNC_EXECUTE_TIMEOUT_REJECTION);
      when(config.isFollowRedirectsAllowed()).thenReturn(FOLLOW_REDIRECTS);
      when(config.getChunkingThreshold()).thenReturn(CHUNKING_THRESHOLD);
      when(config.getChunkLength()).thenReturn(CHUNK_SIZE);
      when(config.getConnectionType()).thenReturn(CONNECTION_TYPE);
      when(config.getConnectionTimeout()).thenReturn(CONNECTION_TIMEOUT);
      when(config.getMaxRetransmits()).thenReturn(MAX_RETRANSMITS);
      when(config.getReceiveTimeout()).thenReturn(RECEIVE_TIMEOUT);

      when(config.isServerAuthorizationRequired()).thenReturn(false);
      when(config.getServerUsername()).thenReturn(SERVER_USERNAME);
      when(config.getServerPassword()).thenReturn(SERVER_PASSWORD);
      when(config.getServerAuthorizationType()).thenReturn(SERVER_AUTHORIZATION_TYPE);

      configurator.configureConnection(config, conduit);

      verify(conduit).getClient();
      verify(policy1).setAllowChunking(CHUNKING_ALLOWED);
      verify(policy1).setAsyncExecuteTimeout(ASYNC_EXECUTE_TIMEOUT);
      verify(policy1).setAsyncExecuteTimeoutRejection(ASYNC_EXECUTE_TIMEOUT_REJECTION);
      verify(policy1).setAutoRedirect(FOLLOW_REDIRECTS);
      verify(policy1).setChunkingThreshold(CHUNKING_THRESHOLD);
      verify(policy1).setChunkLength(CHUNK_SIZE);
      verify(policy1).setConnection(CXF_CONNECTION_TYPE);
      verify(policy1).setConnectionTimeout(CONNECTION_TIMEOUT);
      verify(policy1).setMaxRetransmits(MAX_RETRANSMITS);
      verify(policy1).setReceiveTimeout(RECEIVE_TIMEOUT);

      verify(conduit, times(0)).getAuthorization();
      verify(policy2, times(0)).setUserName(anyString());
      verify(policy2, times(0)).setPassword(anyString());
      verify(policy2, times(0)).setAuthorizationType(anyString());

      verify(conduit, times(0)).getProxyAuthorization();
   }

   @Test
   public void testConfigureProxyNotRequired() {
      when(config.isProxyRequired()).thenReturn(false);

      configurator.configureProxy(config, conduit);

      verify(conduit, times(0)).getClient();
      verify(policy1, times(0)).setProxyServer(anyString());
      verify(policy1, times(0)).setProxyServerPort(anyInt());
      verify(policy1, times(0)).setNonProxyHosts(anyString());
      verify(policy1, times(0)).setProxyServerType(Matchers.<ProxyServerType> any());

      verify(conduit, times(0)).getProxyAuthorization();
      verify(policy3, times(0)).setUserName(anyString());
      verify(policy3, times(0)).setPassword(anyString());
      verify(policy3, times(0)).setAuthorizationType(anyString());
   }

   @Test
   public void testConfigureProxyWithAuthorization() {
      when(conduit.getClient()).thenReturn(policy1);
      when(conduit.getProxyAuthorization()).thenReturn(policy3);

      when(config.isProxyRequired()).thenReturn(true);
      when(config.getNonProxyHosts()).thenReturn(NON_PROXY_HOSTS);
      when(config.getProxyType()).thenReturn(PROXY_TYPE);

      when(config.isProxyAuthorizationRequired()).thenReturn(true);
      when(config.getProxyAddress()).thenReturn(PROXY_ADDRESS);
      when(config.getProxyPort()).thenReturn(PROXY_PORT);
      when(config.getProxyUsername()).thenReturn(PROXY_USERNAME);
      when(config.getProxyPassword()).thenReturn(PROXY_PASSWORD);
      when(config.getProxyAuthorizationType()).thenReturn(PROXY_AUTHORIZATION_TYPE);

      configurator.configureProxy(config, conduit);

      verify(conduit).getClient();
      verify(policy1).setProxyServer(PROXY_ADDRESS);
      verify(policy1).setProxyServerPort(PROXY_PORT);
      verify(policy1).setNonProxyHosts(NON_PROXY_HOSTS);
      verify(policy1).setProxyServerType(CXF_PROXY_SERVER_TYPE);

      verify(conduit).getProxyAuthorization();
      verify(policy3).setUserName(PROXY_USERNAME);
      verify(policy3).setPassword(PROXY_PASSWORD);
      verify(policy3).setAuthorizationType(PROXY_AUTHORIZATION_TYPE);

      verify(conduit, times(0)).getAuthorization();
      verify(conduit, times(0)).setClient(Matchers.<HTTPClientPolicy> any());
      verify(conduit, times(0)).setAuthorization(Matchers.<AuthorizationPolicy> any());
      verify(conduit, times(0)).setProxyAuthorization(Matchers.<ProxyAuthorizationPolicy> any());
   }

   @Test
   public void testConfigureProxyNoPortAndNoAuthorization() {
      when(conduit.getClient()).thenReturn(policy1);

      when(config.isProxyRequired()).thenReturn(true);
      when(config.getNonProxyHosts()).thenReturn(NON_PROXY_HOSTS);
      when(config.getProxyType()).thenReturn(PROXY_TYPE);

      when(config.isProxyAuthorizationRequired()).thenReturn(false);
      when(config.getProxyAddress()).thenReturn(PROXY_ADDRESS);
      when(config.getProxyPort()).thenReturn(-1);

      configurator.configureProxy(config, conduit);

      verify(conduit).getClient();
      verify(policy1).setProxyServer(PROXY_ADDRESS);
      verify(policy1, times(0)).setProxyServerPort(anyInt());
      verify(policy1).setNonProxyHosts(NON_PROXY_HOSTS);
      verify(policy1).setProxyServerType(CXF_PROXY_SERVER_TYPE);

      verify(conduit, times(0)).getProxyAuthorization();
      verify(policy3, times(0)).setUserName(anyString());
      verify(policy3, times(0)).setPassword(anyString());
      verify(policy3, times(0)).setAuthorizationType(anyString());
   }

   @Test
   public void testConduitPolicyNull() {
      when(conduit.getClient()).thenReturn(null);
      when(conduit.getAuthorization()).thenReturn(null);
      when(conduit.getProxyAuthorization()).thenReturn(null);

      when(config.isProxyRequired()).thenReturn(true);
      when(config.isProxyAuthorizationRequired()).thenReturn(true);
      when(config.isServerAuthorizationRequired()).thenReturn(true);

      configurator.configureConnection(config, conduit);
      configurator.configureProxy(config, conduit);

      verify(conduit, times(2)).setClient(captor1.capture());
      verify(conduit).setAuthorization(captor2.capture());
      verify(conduit).setProxyAuthorization(captor3.capture());

      assertNotNull(captor1.getValue());
      assertNotNull(captor2.getValue());
      assertNotNull(captor3.getValue());
   }

   @Test
   public void testConfigureOAuthNotEnabled() {
      when(config.getOAuthClientId()).thenReturn(null);

      List<Object> actual = configurator.getOAuthProviders(config);
      assertEquals(true, actual.isEmpty());
   }

   @Test
   public void testConfigureOAuth() {
      when(config.getOAuthAuthorizeUri()).thenReturn(AUTHORIZE_URI);
      when(config.isOAuthFailsOnRefreshTokenError()).thenReturn(FAIL_ON_REFRESH_ERROR);
      when(config.isOAuthTokenCacheEnabled()).thenReturn(CACHE_ENABLED);
      when(config.getOAuthCacheEvictTimeoutMillis()).thenReturn(CACHE_EVICT_TIMEOUT);
      when(config.getOAuthCacheMaxSize()).thenReturn(CACHE_MAX_SIZE);
      when(config.getOAuthClientId()).thenReturn(CLIENT_ID);
      when(config.getOAuthClientSecret()).thenReturn(CLIENT_SECRET);
      when(config.getOAuthEncodedSecretKey()).thenReturn(SECRET_KEY);
      when(config.getOAuthRedirectUri()).thenReturn(REDIRECT_URI);
      when(config.getOAuthScopes()).thenReturn(SCOPES);
      when(config.getOAuthSecretKeyAlgorithm()).thenReturn(SECRET_ALGORITHM);
      when(config.getOAuthTokenUri()).thenReturn(TOKEN_URI);
      when(config.getOAuthTokenValidationUri()).thenReturn(VALIDATION_URI);
      when(config.getOAuthTokenHandler()).thenReturn(handler);
      when(config.getOAuthTokenStore()).thenReturn(store);

      when(config.isServerAuthorizationRequired()).thenReturn(true);
      when(config.getServerUsername()).thenReturn(SERVER_USERNAME);
      when(config.getServerPassword()).thenReturn(SERVER_PASSWORD);
      when(config.getServerAuthorizationType()).thenReturn(SERVER_AUTHORIZATION_TYPE);

      when(oauthFactory.newOAuthClientFilter(SERVER_USERNAME, SERVER_PASSWORD, CLIENT_ID, CLIENT_SECRET, AUTHORIZE_URI,
         TOKEN_URI, VALIDATION_URI)).thenReturn(filter);

      List<Object> actual = configurator.getOAuthProviders(config);
      assertEquals(1, actual.size());
      assertEquals(filter, actual.get(0));

      verify(oauthFactory).newOAuthClientFilter(SERVER_USERNAME, SERVER_PASSWORD, CLIENT_ID, CLIENT_SECRET,
         AUTHORIZE_URI, TOKEN_URI, VALIDATION_URI);
      verify(filter).setClientAccessTokenCache(Matchers.any(ClientAccessTokenCache.class));
      verify(filter).setFailOnRefreshTokenError(FAIL_ON_REFRESH_ERROR);
      verify(filter).setRedirectUri(REDIRECT_URI);
      verify(filter).setScopes(SCOPES);
      verify(filter).setSecretKeyAlgorithm(SECRET_ALGORITHM);
      verify(filter).setTokenHandler(handler);
      verify(filter).setTokenStore(store);

      when(conduit.getAuthorization()).thenReturn(policy2);

      configurator.configureDefaults(props);
      configurator.configureClientBuilder(config, builder);
      verify(builder).register(filter);

      verify(policy2, times(0)).setUserName(Matchers.anyString());
      verify(policy2, times(0)).setPassword(Matchers.anyString());

      configurator.configureBean(config, "http://www.address.com", bean);

      verify(bean, times(2)).setProviders(captor4.capture());

      verify(policy2, times(0)).setUserName(Matchers.anyString());
      verify(policy2, times(0)).setPassword(Matchers.anyString());

      boolean filterFound = false;
      for (List<Object> providers : captor4.getAllValues()) {
         for (Object provider : providers) {
            if (provider.equals(filter)) {
               filterFound = true;
               break;
            }
         }
      }
      assertEquals(true, filterFound);
   }
}
