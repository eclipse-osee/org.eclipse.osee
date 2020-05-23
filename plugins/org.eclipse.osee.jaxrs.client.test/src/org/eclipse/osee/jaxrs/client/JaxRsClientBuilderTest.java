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

package org.eclipse.osee.jaxrs.client;

import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.CHUNK_LENGTH_MIN_LIMIT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.CHUNK_THRESHOLD_MIN_LIMIT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_CHUNKING_ALLOWED;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_CHUNKING_THRESHOLD;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_CHUNK_SIZE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_CONNECTION_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_CONNECTION_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_MAX_RETRANSMITS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_NON_PROXY_HOSTS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_AUTHORIZE_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_ENABLED;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_EVICT_TIMEOUT_MILLIS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_MAX_SIZE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_ID;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_SECRET;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_CONFIRM_ACCESS_HANDLER;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_ENCODED_SECRET_KEY;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_FAILS_ON_REFRESH_TOKEN_ERROR;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_REDIRECT_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_SCOPES;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_SECRET_KEY_ALGORITHM;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_PASSWORD;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_PORT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_USERNAME;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_RECEIVE_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_SERVER_PASSWORD;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_SERVER_USERNAME;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_CHUNKING_ALLOWED;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_CHUNKING_THRESHOLD;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_CHUNK_SIZE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_CONNECTION_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_CONNECTION_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_FOLLOW_REDIRECTS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_MAX_RETRANSMITS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_NON_PROXY_HOSTS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_AUTHORIZE_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_CACHE_ENABLED;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_CACHE_EVICT_TIMEOUT_MILLIS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_CACHE_MAX_SIZE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_CLIENT_ID;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_CLIENT_SECRET;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_ENCODED_SECRET_KEY;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_FAILS_ON_REFRESH_TOKEN_ERROR;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_REDIRECT_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_SCOPES;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_SECRET_KEY_ALGORITHM;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_TOKEN_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_PROXY_ADDRESS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_PROXY_PASSWORD;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_PROXY_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_PROXY_USERNAME;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_RECEIVE_TIMEOUT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_SERVER_PASSWORD;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_SERVER_USERNAME;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.RETRANSMIT_MIN_LIMIT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.TIMEOUT_MIN_LIMIT;
import static org.junit.Assert.assertEquals;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.client.WebTarget;
import org.eclipse.osee.jaxrs.client.JaxRsClient.JaxRsClientBuilder;
import org.eclipse.osee.jaxrs.client.JaxRsClient.JaxRsClientFactory;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ConnectionType;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ProxyType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link JaxRsClientBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class JaxRsClientBuilderTest {

   private static final long POSITIVE_TIMEOUT = Long.MAX_VALUE;
   private static final long NEGATIVE_TIMEOUT = Long.MIN_VALUE;
   private static final int POSITIVE_INT = Integer.MAX_VALUE;
   private static final int NEGATIVE_INT = Integer.MIN_VALUE;

   private static final String PROXY_ADDRESS = "proxy-address.com";
   private static final int PROXY_PORT = 78612;

   private static final long ASYNC_EXECUTE_TIMEOUT = 32423L;
   private static final boolean ASYNC_EXECUTE_TIMEOUT_REJECTION = true;
   private static final int CHUNK_SIZE = 864;
   private static final boolean CHUNKING_ALLOWED = true;
   private static final int CHUNKING_THRESHOLD = 9872394;
   private static final long CONNECTION_TIMEOUT = 2327L;
   private static final ConnectionType CONNECTION_TYPE = ConnectionType.CLOSE;
   private static final boolean CREATE_THREADSAFE_PROXY_CLIENTS = true;
   private static final boolean FOLLOW_REDIRECTS = false;
   private static final int MAX_RETRANSMITS = 452;
   private static final String NON_PROXY_HOSTS = "non-proxy-hosts";
   private static final String FULL_PROXY_ADDRESS = "http://" + PROXY_ADDRESS + ":" + PROXY_PORT;
   private static final String PROXY_AUTHORIZATION_TYPE = "proxy-authentication-type";
   private static final boolean PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS = true;
   private static final String PROXY_PASSWORD = "proxy-password";
   private static final ProxyType PROXY_TYPE = ProxyType.SOCKS;
   private static final String PROXY_USERNAME = "proxy-username";
   private static final long RECEIVE_TIMEOUT = 87532L;
   private static final String SERVER_AUTHORIZATION_TYPE = "server-authentication-type";
   private static final String SERVER_PASSWORD = "server-password";
   private static final String SERVER_USERNAME = "server-username";

   private static final String AUTHORIZE_URI = "authorize_uri";
   private static final String TOKEN_URI = "token_uri";
   private static final String VALIDATION_URI = "validation_uri";
   private static final String CLIENT_ID = "client_id";
   private static final String CLIENT_SECRET = "client_secret";
   private static final String SCOPES = "scopes";
   private static final String REDIRECT_URI = "redirect_uri";
   private static final String SECRET_ALGORITHM = "secret_algorithm";
   private static final String SECRET_KEY = "encoded_secret_key";
   private static final boolean FAIL_ON_REFRESH_ERROR = true;
   private static final boolean CACHE_ENABLED = true;
   private static final int CACHE_MAX_SIZE = 7652;
   private static final long CACHE_EVICT_TIMEOUT = 1309L;

   //@formatter:off
   @Mock private JaxRsClientFactory factory;
   @Mock private Map<String, Object> properties;
   @Mock private WebTarget target;
   @Captor private ArgumentCaptor<Map<String, Object>> propCaptor;
   
   @Mock private JaxRsConfirmAccessHandler handler;
   @Mock private JaxRsTokenStore tokenStore;
   //@formatter:on

   private JaxRsClientBuilder builder;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      builder = new JaxRsClientBuilder(factory);
   }

   @Test
   public void testAllowChunking() {
      builder.allowChunking(true);
      assertEquals(true, builder.isChunkingAllowed());

      builder.allowChunking(false);
      assertEquals(false, builder.isChunkingAllowed());
   }

   @Test
   public void testAsyncExecTimeout() {
      builder.asyncExecTimeout(POSITIVE_TIMEOUT);
      assertEquals(POSITIVE_TIMEOUT, builder.getAsyncExecuteTimeout());

      builder.asyncExecTimeout(NEGATIVE_TIMEOUT);
      assertEquals(TIMEOUT_MIN_LIMIT, builder.getAsyncExecuteTimeout());
   }

   @Test
   public void testAsyncTimeoutRejection() {
      builder.asyncExecTimeoutRejection(true);
      assertEquals(true, builder.isAsyncExecuteTimeoutRejection());

      builder.asyncExecTimeoutRejection(false);
      assertEquals(false, builder.isAsyncExecuteTimeoutRejection());
   }

   @Test
   public void testConnectionTimeout() {
      builder.connectionTimeout(POSITIVE_TIMEOUT);
      assertEquals(POSITIVE_TIMEOUT, builder.getConnectionTimeout());

      builder.connectionTimeout(NEGATIVE_TIMEOUT);
      assertEquals(TIMEOUT_MIN_LIMIT, builder.getConnectionTimeout());
   }

   @Test
   public void testReceiveTimeout() {
      builder.receiveTimeout(POSITIVE_TIMEOUT);
      assertEquals(POSITIVE_TIMEOUT, builder.getReceiveTimeout());

      builder.receiveTimeout(NEGATIVE_TIMEOUT);
      assertEquals(TIMEOUT_MIN_LIMIT, builder.getReceiveTimeout());
   }

   @Test
   public void testConnectionType() {
      builder.connectionType(ConnectionType.CLOSE);
      assertEquals(ConnectionType.CLOSE, builder.getConnectionType());

      builder.connectionType(null);
      assertEquals(ConnectionType.KEEP_ALIVE, builder.getConnectionType());
   }

   @Test
   public void testProxyType() {
      builder.proxyType(ProxyType.SOCKS);

      builder.proxyType(ProxyType.SOCKS);
      assertEquals(ProxyType.SOCKS, builder.getProxyType());

      builder.proxyType(null);
      assertEquals(ProxyType.HTTP, builder.getProxyType());
   }

   @Test
   public void testFollowRedirects() {
      builder.followRedirects(true);
      assertEquals(true, builder.isFollowRedirectsAllowed());

      builder.followRedirects(false);
      assertEquals(false, builder.isFollowRedirectsAllowed());
   }

   @Test
   public void testChunkingSize() {
      builder.chunkLength(POSITIVE_INT);
      assertEquals(POSITIVE_INT, builder.getChunkLength());

      builder.chunkLength(NEGATIVE_INT);
      assertEquals(CHUNK_LENGTH_MIN_LIMIT, builder.getChunkLength());
   }

   @Test
   public void testChunkingThreshold() {
      builder.chunkingThreshold(POSITIVE_INT);
      assertEquals(POSITIVE_INT, builder.getChunkingThreshold());

      builder.chunkingThreshold(NEGATIVE_INT);
      assertEquals(CHUNK_THRESHOLD_MIN_LIMIT, builder.getChunkingThreshold());
   }

   @Test
   public void testMaxRetransmit() {
      builder.maxRetransmits(POSITIVE_INT);
      assertEquals(POSITIVE_INT, builder.getMaxRetransmits());

      builder.maxRetransmits(NEGATIVE_INT);
      assertEquals(RETRANSMIT_MIN_LIMIT, builder.getMaxRetransmits());
   }

   @Test
   public void testCreateThreadSafeProxyClients() {
      builder.createThreadSafeProxyClients(true);
      assertEquals(true, builder.isCreateThreadSafeProxyClients());

      builder.createThreadSafeProxyClients(false);
      assertEquals(false, builder.isCreateThreadSafeProxyClients());
   }

   @Test
   public void testProxyClientSubResourcesInheritHeaders() {
      builder.proxyClientSubResourcesInheritHeaders(true);
      assertEquals(true, builder.isProxyClientSubResourcesInheritHeaders());

      builder.proxyClientSubResourcesInheritHeaders(false);
      assertEquals(false, builder.isProxyClientSubResourcesInheritHeaders());
   }

   @Test
   public void testNonProxyHosts() {
      builder.nonProxyHosts(NON_PROXY_HOSTS);
      assertEquals(NON_PROXY_HOSTS, builder.getNonProxyHosts());
   }

   @Test
   public void testProxyAddress() {
      builder.proxyAddress(FULL_PROXY_ADDRESS);

      assertEquals(FULL_PROXY_ADDRESS, builder.getFullProxyAddress());
      assertEquals(PROXY_ADDRESS, builder.getProxyAddress());
      assertEquals(PROXY_PORT, builder.getProxyPort());
   }

   @Test
   public void testProxyAuthorizationType() {
      builder.proxyAuthorizationType(PROXY_AUTHORIZATION_TYPE);
      assertEquals(PROXY_AUTHORIZATION_TYPE, builder.getProxyAuthorizationType());
   }

   @Test
   public void testProxyPassword() {
      builder.proxyPassword(PROXY_PASSWORD);
      assertEquals(PROXY_PASSWORD, builder.getProxyPassword());
   }

   @Test
   public void testProxyUsername() {
      builder.proxyUsername(PROXY_USERNAME);
      assertEquals(PROXY_USERNAME, builder.getProxyUsername());
   }

   @Test
   public void testServerAuthorizationType() {
      builder.authorizationType(SERVER_AUTHORIZATION_TYPE);
      assertEquals(SERVER_AUTHORIZATION_TYPE, builder.getServerAuthorizationType());
   }

   @Test
   public void testServerPassword() {
      builder.password(SERVER_PASSWORD);
      assertEquals(SERVER_PASSWORD, builder.getServerPassword());
   }

   @Test
   public void testServerUsername() {
      builder.username(SERVER_USERNAME);
      assertEquals(SERVER_USERNAME, builder.getServerUsername());
   }

   @Test
   public void testDefaultProperties() {
      Map<String, Object> properties = new HashMap<>();
      builder.properties(properties);

      JaxRsClient actual = builder.build();
      JaxRsClientConfig config = actual.getConfig();

      //@formatter:off
      assertEquals(DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT, config.getAsyncExecuteTimeout());
      assertEquals(DEFAULT_JAXRS_CLIENT_CHUNKING_THRESHOLD, config.getChunkingThreshold());
      assertEquals(DEFAULT_JAXRS_CLIENT_CHUNK_SIZE, config.getChunkLength());
      assertEquals(DEFAULT_JAXRS_CLIENT_CONNECTION_TIMEOUT, config.getConnectionTimeout());
      assertEquals(DEFAULT_JAXRS_CLIENT_CONNECTION_TYPE, config.getConnectionType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS, config.getFullProxyAddress());
      assertEquals(DEFAULT_JAXRS_CLIENT_MAX_RETRANSMITS, config.getMaxRetransmits());
      assertEquals(DEFAULT_JAXRS_CLIENT_NON_PROXY_HOSTS, config.getNonProxyHosts());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE, config.getProxyAuthorizationType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS, config.getProxyAddress());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_PASSWORD, config.getProxyPassword());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_PORT, config.getProxyPort());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_TYPE, config.getProxyType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_USERNAME, config.getProxyUsername());
      assertEquals(DEFAULT_JAXRS_CLIENT_RECEIVE_TIMEOUT, config.getReceiveTimeout());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE, config.getServerAuthorizationType());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_PASSWORD, config.getServerPassword());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_USERNAME, config.getServerUsername());
      assertEquals(DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION, config.isAsyncExecuteTimeoutRejection());
      assertEquals(DEFAULT_JAXRS_CLIENT_CHUNKING_ALLOWED, config.isChunkingAllowed());
      assertEquals(DEFAULT_JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS, config.isCreateThreadSafeProxyClients());
      assertEquals(DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS, config.isFollowRedirectsAllowed());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS, config.isProxyClientSubResourcesInheritHeaders());
      assertEquals(false, config.isProxyAuthorizationRequired());
      assertEquals(false, config.isProxyRequired());
      assertEquals(false, config.isServerAuthorizationRequired());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_AUTHORIZE_URI, config.getOAuthAuthorizeUri());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_URI, config.getOAuthTokenUri());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI, config.getOAuthTokenValidationUri());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_ID, config.getOAuthClientId());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_SECRET, config.getOAuthClientSecret());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_SCOPES, config.getOAuthScopes());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_REDIRECT_URI, config.getOAuthRedirectUri());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_SECRET_KEY_ALGORITHM, config.getOAuthSecretKeyAlgorithm());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_ENCODED_SECRET_KEY, config.getOAuthEncodedSecretKey());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_FAILS_ON_REFRESH_TOKEN_ERROR, config.isOAuthFailsOnRefreshTokenError());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_ENABLED, config.isOAuthTokenCacheEnabled());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_MAX_SIZE, config.getOAuthCacheMaxSize());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_EVICT_TIMEOUT_MILLIS, config.getOAuthCacheEvictTimeoutMillis());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CONFIRM_ACCESS_HANDLER, config.getOAuthTokenHandler());
      assertEquals(null, config.getOAuthTokenStore());
      //@formatter:on
   }

   @Test
   public void testPropertiesDefaultsWithNegatives() {
      Map<String, Object> props = new HashMap<>();
      props.put(JAXRS_CLIENT_CONNECTION_TIMEOUT, NEGATIVE_TIMEOUT);
      props.put(JAXRS_CLIENT_RECEIVE_TIMEOUT, NEGATIVE_TIMEOUT);
      props.put(JAXRS_CLIENT_MAX_RETRANSMITS, NEGATIVE_INT);
      props.put(JAXRS_CLIENT_CHUNKING_THRESHOLD, NEGATIVE_INT);
      props.put(JAXRS_CLIENT_CHUNK_SIZE, NEGATIVE_INT);
      props.put(JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT, NEGATIVE_TIMEOUT);
      builder.properties(props);

      JaxRsClient actual = builder.build();
      JaxRsClientConfig config = actual.getConfig();

      //@formatter:off
      assertEquals(TIMEOUT_MIN_LIMIT, config.getAsyncExecuteTimeout());
      assertEquals(CHUNK_THRESHOLD_MIN_LIMIT, config.getChunkingThreshold());
      assertEquals(CHUNK_LENGTH_MIN_LIMIT, config.getChunkLength());
      assertEquals(TIMEOUT_MIN_LIMIT, config.getConnectionTimeout());
      assertEquals(DEFAULT_JAXRS_CLIENT_CONNECTION_TYPE, config.getConnectionType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS, config.getFullProxyAddress());
      assertEquals(RETRANSMIT_MIN_LIMIT, config.getMaxRetransmits());
      assertEquals(DEFAULT_JAXRS_CLIENT_NON_PROXY_HOSTS, config.getNonProxyHosts());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE, config.getProxyAuthorizationType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS, config.getProxyAddress());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_PASSWORD, config.getProxyPassword());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_PORT, config.getProxyPort());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_TYPE, config.getProxyType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_USERNAME, config.getProxyUsername());
      assertEquals(TIMEOUT_MIN_LIMIT, config.getReceiveTimeout());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE, config.getServerAuthorizationType());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_PASSWORD, config.getServerPassword());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_USERNAME, config.getServerUsername());
      assertEquals(DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION, config.isAsyncExecuteTimeoutRejection());
      assertEquals(DEFAULT_JAXRS_CLIENT_CHUNKING_ALLOWED, config.isChunkingAllowed());
      assertEquals(DEFAULT_JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS, config.isCreateThreadSafeProxyClients());
      assertEquals(DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS, config.isFollowRedirectsAllowed());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS, config.isProxyClientSubResourcesInheritHeaders());
      assertEquals(false, config.isProxyAuthorizationRequired());
      assertEquals(false, config.isProxyRequired());
      assertEquals(false, config.isServerAuthorizationRequired());
      //@formatter:on
   }

   @Test
   public void testProperties() {
      Map<String, Object> props = new HashMap<>();
      props.put(JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT, ASYNC_EXECUTE_TIMEOUT);
      props.put(JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION, ASYNC_EXECUTE_TIMEOUT_REJECTION);
      props.put(JAXRS_CLIENT_CHUNK_SIZE, CHUNK_SIZE);
      props.put(JAXRS_CLIENT_CHUNKING_ALLOWED, CHUNKING_ALLOWED);
      props.put(JAXRS_CLIENT_CHUNKING_THRESHOLD, CHUNKING_THRESHOLD);
      props.put(JAXRS_CLIENT_CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
      props.put(JAXRS_CLIENT_CONNECTION_TYPE, CONNECTION_TYPE);
      props.put(JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS, CREATE_THREADSAFE_PROXY_CLIENTS);
      props.put(JAXRS_CLIENT_FOLLOW_REDIRECTS, FOLLOW_REDIRECTS);
      props.put(JAXRS_CLIENT_MAX_RETRANSMITS, MAX_RETRANSMITS);
      props.put(JAXRS_CLIENT_NON_PROXY_HOSTS, NON_PROXY_HOSTS);
      props.put(JAXRS_CLIENT_PROXY_ADDRESS, FULL_PROXY_ADDRESS);
      props.put(JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE, PROXY_AUTHORIZATION_TYPE);
      props.put(JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS, PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS);
      props.put(JAXRS_CLIENT_PROXY_PASSWORD, PROXY_PASSWORD);
      props.put(JAXRS_CLIENT_PROXY_TYPE, PROXY_TYPE);
      props.put(JAXRS_CLIENT_PROXY_USERNAME, PROXY_USERNAME);
      props.put(JAXRS_CLIENT_RECEIVE_TIMEOUT, RECEIVE_TIMEOUT);
      props.put(JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE, SERVER_AUTHORIZATION_TYPE);
      props.put(JAXRS_CLIENT_SERVER_PASSWORD, SERVER_PASSWORD);
      props.put(JAXRS_CLIENT_SERVER_USERNAME, SERVER_USERNAME);

      props.put(JAXRS_CLIENT_OAUTH_AUTHORIZE_URI, AUTHORIZE_URI);
      props.put(JAXRS_CLIENT_OAUTH_TOKEN_URI, TOKEN_URI);
      props.put(JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI, VALIDATION_URI);
      props.put(JAXRS_CLIENT_OAUTH_CLIENT_ID, CLIENT_ID);
      props.put(JAXRS_CLIENT_OAUTH_CLIENT_SECRET, CLIENT_SECRET);
      props.put(JAXRS_CLIENT_OAUTH_SCOPES, SCOPES);
      props.put(JAXRS_CLIENT_OAUTH_REDIRECT_URI, REDIRECT_URI);
      props.put(JAXRS_CLIENT_OAUTH_SECRET_KEY_ALGORITHM, SECRET_ALGORITHM);
      props.put(JAXRS_CLIENT_OAUTH_ENCODED_SECRET_KEY, SECRET_KEY);
      props.put(JAXRS_CLIENT_OAUTH_FAILS_ON_REFRESH_TOKEN_ERROR, FAIL_ON_REFRESH_ERROR);
      props.put(JAXRS_CLIENT_OAUTH_CACHE_ENABLED, CACHE_ENABLED);
      props.put(JAXRS_CLIENT_OAUTH_CACHE_MAX_SIZE, CACHE_MAX_SIZE);
      props.put(JAXRS_CLIENT_OAUTH_CACHE_EVICT_TIMEOUT_MILLIS, CACHE_EVICT_TIMEOUT);

      builder.properties(props);

      JaxRsClient actual = builder.build();
      JaxRsClientConfig config = actual.getConfig();

      assertEquals(ASYNC_EXECUTE_TIMEOUT, config.getAsyncExecuteTimeout());
      assertEquals(ASYNC_EXECUTE_TIMEOUT_REJECTION, config.isAsyncExecuteTimeoutRejection());
      assertEquals(CHUNK_SIZE, config.getChunkLength());
      assertEquals(CHUNKING_ALLOWED, config.isChunkingAllowed());
      assertEquals(CHUNKING_THRESHOLD, config.getChunkingThreshold());
      assertEquals(CONNECTION_TIMEOUT, config.getConnectionTimeout());
      assertEquals(CONNECTION_TYPE, config.getConnectionType());
      assertEquals(CREATE_THREADSAFE_PROXY_CLIENTS, config.isCreateThreadSafeProxyClients());
      assertEquals(FOLLOW_REDIRECTS, config.isFollowRedirectsAllowed());
      assertEquals(MAX_RETRANSMITS, config.getMaxRetransmits());
      assertEquals(NON_PROXY_HOSTS, config.getNonProxyHosts());
      assertEquals(PROXY_ADDRESS, config.getProxyAddress());
      assertEquals(PROXY_AUTHORIZATION_TYPE, config.getProxyAuthorizationType());
      assertEquals(PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS, config.isProxyClientSubResourcesInheritHeaders());
      assertEquals(PROXY_PASSWORD, config.getProxyPassword());
      assertEquals(PROXY_TYPE, config.getProxyType());
      assertEquals(PROXY_USERNAME, config.getProxyUsername());
      assertEquals(RECEIVE_TIMEOUT, config.getReceiveTimeout());
      assertEquals(SERVER_AUTHORIZATION_TYPE, config.getServerAuthorizationType());
      assertEquals(SERVER_PASSWORD, config.getServerPassword());
      assertEquals(SERVER_USERNAME, config.getServerUsername());
      assertEquals(FULL_PROXY_ADDRESS, config.getFullProxyAddress());
      assertEquals(true, config.isProxyAuthorizationRequired());
      assertEquals(true, config.isProxyRequired());
      assertEquals(true, config.isServerAuthorizationRequired());
      assertEquals(AUTHORIZE_URI, config.getOAuthAuthorizeUri());
      assertEquals(TOKEN_URI, config.getOAuthTokenUri());
      assertEquals(VALIDATION_URI, config.getOAuthTokenValidationUri());
      assertEquals(CLIENT_ID, config.getOAuthClientId());
      assertEquals(CLIENT_SECRET, config.getOAuthClientSecret());
      assertEquals(SCOPES, config.getOAuthScopes());
      assertEquals(REDIRECT_URI, config.getOAuthRedirectUri());
      assertEquals(SECRET_ALGORITHM, config.getOAuthSecretKeyAlgorithm());
      assertEquals(SECRET_KEY, config.getOAuthEncodedSecretKey());
      assertEquals(FAIL_ON_REFRESH_ERROR, config.isOAuthFailsOnRefreshTokenError());
      assertEquals(CACHE_ENABLED, config.isOAuthTokenCacheEnabled());
      assertEquals(CACHE_MAX_SIZE, config.getOAuthCacheMaxSize());
      assertEquals(CACHE_EVICT_TIMEOUT, config.getOAuthCacheEvictTimeoutMillis());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CONFIRM_ACCESS_HANDLER, config.getOAuthTokenHandler());
      assertEquals(null, config.getOAuthTokenStore());
   }

   @Test
   public void testNoChangeAfterBuild() {
      builder.allowChunking(CHUNKING_ALLOWED);
      builder.asyncExecTimeout(ASYNC_EXECUTE_TIMEOUT);
      builder.asyncExecTimeoutRejection(ASYNC_EXECUTE_TIMEOUT_REJECTION);
      builder.chunkingThreshold(CHUNKING_THRESHOLD);
      builder.chunkLength(CHUNK_SIZE);
      builder.connectionTimeout(CONNECTION_TIMEOUT);
      builder.connectionType(CONNECTION_TYPE);
      builder.createThreadSafeProxyClients(CREATE_THREADSAFE_PROXY_CLIENTS);
      builder.followRedirects(FOLLOW_REDIRECTS);
      builder.maxRetransmits(MAX_RETRANSMITS);
      builder.nonProxyHosts(NON_PROXY_HOSTS);
      builder.proxyAddress(FULL_PROXY_ADDRESS);
      builder.proxyAuthorizationType(PROXY_AUTHORIZATION_TYPE);
      builder.proxyClientSubResourcesInheritHeaders(PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS);
      builder.proxyPassword(PROXY_PASSWORD);
      builder.proxyType(PROXY_TYPE);
      builder.proxyUsername(PROXY_USERNAME);
      builder.receiveTimeout(RECEIVE_TIMEOUT);
      builder.authorizationType(SERVER_AUTHORIZATION_TYPE);
      builder.password(SERVER_PASSWORD);
      builder.username(SERVER_USERNAME);

      builder.oAuthAuthorizeUri(AUTHORIZE_URI);
      builder.oAuthTokenUri(TOKEN_URI);
      builder.oAuthTokenValidationUri(VALIDATION_URI);
      builder.oAuthScopes(SCOPES);
      builder.oAuthRedirectUri(REDIRECT_URI);
      builder.oAuthClientId(CLIENT_ID);
      builder.oAuthClientSecret(CLIENT_SECRET);
      builder.oAuthSecretKeyAlgorithm(SECRET_ALGORITHM);
      builder.oAuthEncodedSecretKey(SECRET_KEY);
      builder.oAuthFailOnRefreshTokenError(FAIL_ON_REFRESH_ERROR);
      builder.oAuthCacheEnabled(CACHE_ENABLED);
      builder.oAuthCacheMaxSize(CACHE_MAX_SIZE);
      builder.oAuthCacheEvictTimeoutMillis(CACHE_EVICT_TIMEOUT);
      builder.oAuthConfirmHandler(handler);
      builder.oAuthTokenStore(tokenStore);

      JaxRsClient actual = builder.build();
      JaxRsClientConfig config = actual.getConfig();

      assertEquals(ASYNC_EXECUTE_TIMEOUT, config.getAsyncExecuteTimeout());
      assertEquals(ASYNC_EXECUTE_TIMEOUT_REJECTION, config.isAsyncExecuteTimeoutRejection());
      assertEquals(CHUNK_SIZE, config.getChunkLength());
      assertEquals(CHUNKING_ALLOWED, config.isChunkingAllowed());
      assertEquals(CHUNKING_THRESHOLD, config.getChunkingThreshold());
      assertEquals(CONNECTION_TIMEOUT, config.getConnectionTimeout());
      assertEquals(CONNECTION_TYPE, config.getConnectionType());
      assertEquals(CREATE_THREADSAFE_PROXY_CLIENTS, config.isCreateThreadSafeProxyClients());
      assertEquals(FOLLOW_REDIRECTS, config.isFollowRedirectsAllowed());
      assertEquals(MAX_RETRANSMITS, config.getMaxRetransmits());
      assertEquals(NON_PROXY_HOSTS, config.getNonProxyHosts());
      assertEquals(PROXY_ADDRESS, config.getProxyAddress());
      assertEquals(PROXY_AUTHORIZATION_TYPE, config.getProxyAuthorizationType());
      assertEquals(PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS, config.isProxyClientSubResourcesInheritHeaders());
      assertEquals(PROXY_PASSWORD, config.getProxyPassword());
      assertEquals(PROXY_TYPE, config.getProxyType());
      assertEquals(PROXY_USERNAME, config.getProxyUsername());
      assertEquals(RECEIVE_TIMEOUT, config.getReceiveTimeout());
      assertEquals(SERVER_AUTHORIZATION_TYPE, config.getServerAuthorizationType());
      assertEquals(SERVER_PASSWORD, config.getServerPassword());
      assertEquals(SERVER_USERNAME, config.getServerUsername());
      assertEquals(FULL_PROXY_ADDRESS, config.getFullProxyAddress());
      assertEquals(true, config.isProxyAuthorizationRequired());
      assertEquals(true, config.isProxyRequired());
      assertEquals(true, config.isServerAuthorizationRequired());

      assertEquals(AUTHORIZE_URI, config.getOAuthAuthorizeUri());
      assertEquals(TOKEN_URI, config.getOAuthTokenUri());
      assertEquals(VALIDATION_URI, config.getOAuthTokenValidationUri());
      assertEquals(CLIENT_ID, config.getOAuthClientId());
      assertEquals(CLIENT_SECRET, config.getOAuthClientSecret());
      assertEquals(SCOPES, config.getOAuthScopes());
      assertEquals(REDIRECT_URI, config.getOAuthRedirectUri());
      assertEquals(SECRET_ALGORITHM, config.getOAuthSecretKeyAlgorithm());
      assertEquals(SECRET_KEY, config.getOAuthEncodedSecretKey());
      assertEquals(FAIL_ON_REFRESH_ERROR, config.isOAuthFailsOnRefreshTokenError());
      assertEquals(CACHE_ENABLED, config.isOAuthTokenCacheEnabled());
      assertEquals(CACHE_MAX_SIZE, config.getOAuthCacheMaxSize());
      assertEquals(CACHE_EVICT_TIMEOUT, config.getOAuthCacheEvictTimeoutMillis());
      assertEquals(handler, config.getOAuthTokenHandler());
      assertEquals(tokenStore, config.getOAuthTokenStore());

      builder.properties(Collections.<String, Object> emptyMap());

      actual = builder.build();
      JaxRsClientConfig config2 = actual.getConfig();

      assertEquals(ASYNC_EXECUTE_TIMEOUT, config.getAsyncExecuteTimeout());
      assertEquals(ASYNC_EXECUTE_TIMEOUT_REJECTION, config.isAsyncExecuteTimeoutRejection());
      assertEquals(CHUNK_SIZE, config.getChunkLength());
      assertEquals(CHUNKING_ALLOWED, config.isChunkingAllowed());
      assertEquals(CHUNKING_THRESHOLD, config.getChunkingThreshold());
      assertEquals(CONNECTION_TIMEOUT, config.getConnectionTimeout());
      assertEquals(CONNECTION_TYPE, config.getConnectionType());
      assertEquals(CREATE_THREADSAFE_PROXY_CLIENTS, config.isCreateThreadSafeProxyClients());
      assertEquals(FOLLOW_REDIRECTS, config.isFollowRedirectsAllowed());
      assertEquals(MAX_RETRANSMITS, config.getMaxRetransmits());
      assertEquals(NON_PROXY_HOSTS, config.getNonProxyHosts());
      assertEquals(PROXY_ADDRESS, config.getProxyAddress());
      assertEquals(PROXY_AUTHORIZATION_TYPE, config.getProxyAuthorizationType());
      assertEquals(PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS, config.isProxyClientSubResourcesInheritHeaders());
      assertEquals(PROXY_PASSWORD, config.getProxyPassword());
      assertEquals(PROXY_TYPE, config.getProxyType());
      assertEquals(PROXY_USERNAME, config.getProxyUsername());
      assertEquals(RECEIVE_TIMEOUT, config.getReceiveTimeout());
      assertEquals(SERVER_AUTHORIZATION_TYPE, config.getServerAuthorizationType());
      assertEquals(SERVER_PASSWORD, config.getServerPassword());
      assertEquals(SERVER_USERNAME, config.getServerUsername());
      assertEquals(FULL_PROXY_ADDRESS, config.getFullProxyAddress());
      assertEquals(true, config.isProxyAuthorizationRequired());
      assertEquals(true, config.isProxyRequired());
      assertEquals(true, config.isServerAuthorizationRequired());
      assertEquals(AUTHORIZE_URI, config.getOAuthAuthorizeUri());
      assertEquals(TOKEN_URI, config.getOAuthTokenUri());
      assertEquals(VALIDATION_URI, config.getOAuthTokenValidationUri());
      assertEquals(CLIENT_ID, config.getOAuthClientId());
      assertEquals(CLIENT_SECRET, config.getOAuthClientSecret());
      assertEquals(SCOPES, config.getOAuthScopes());
      assertEquals(REDIRECT_URI, config.getOAuthRedirectUri());
      assertEquals(SECRET_ALGORITHM, config.getOAuthSecretKeyAlgorithm());
      assertEquals(SECRET_KEY, config.getOAuthEncodedSecretKey());
      assertEquals(FAIL_ON_REFRESH_ERROR, config.isOAuthFailsOnRefreshTokenError());
      assertEquals(CACHE_ENABLED, config.isOAuthTokenCacheEnabled());
      assertEquals(CACHE_MAX_SIZE, config.getOAuthCacheMaxSize());
      assertEquals(CACHE_EVICT_TIMEOUT, config.getOAuthCacheEvictTimeoutMillis());
      assertEquals(handler, config.getOAuthTokenHandler());
      assertEquals(tokenStore, config.getOAuthTokenStore());

      //@formatter:off
      assertEquals(DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT, config2.getAsyncExecuteTimeout());
      assertEquals(DEFAULT_JAXRS_CLIENT_CHUNKING_THRESHOLD, config2.getChunkingThreshold());
      assertEquals(DEFAULT_JAXRS_CLIENT_CHUNK_SIZE, config2.getChunkLength());
      assertEquals(DEFAULT_JAXRS_CLIENT_CONNECTION_TIMEOUT, config2.getConnectionTimeout());
      assertEquals(DEFAULT_JAXRS_CLIENT_CONNECTION_TYPE, config2.getConnectionType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS, config2.getFullProxyAddress());
      assertEquals(DEFAULT_JAXRS_CLIENT_MAX_RETRANSMITS, config2.getMaxRetransmits());
      assertEquals(DEFAULT_JAXRS_CLIENT_NON_PROXY_HOSTS, config2.getNonProxyHosts());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE, config2.getProxyAuthorizationType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS, config2.getProxyAddress());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_PASSWORD, config2.getProxyPassword());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_PORT, config2.getProxyPort());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_TYPE, config2.getProxyType());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_USERNAME, config2.getProxyUsername());
      assertEquals(DEFAULT_JAXRS_CLIENT_RECEIVE_TIMEOUT, config2.getReceiveTimeout());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE, config2.getServerAuthorizationType());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_PASSWORD, config2.getServerPassword());
      assertEquals(DEFAULT_JAXRS_CLIENT_SERVER_USERNAME, config2.getServerUsername());
      assertEquals(DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION, config2.isAsyncExecuteTimeoutRejection());
      assertEquals(DEFAULT_JAXRS_CLIENT_CHUNKING_ALLOWED, config2.isChunkingAllowed());
      assertEquals(DEFAULT_JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS, config2.isCreateThreadSafeProxyClients());
      assertEquals(DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS, config2.isFollowRedirectsAllowed());
      assertEquals(DEFAULT_JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS, config2.isProxyClientSubResourcesInheritHeaders());
      assertEquals(false, config2.isProxyAuthorizationRequired());
      assertEquals(false, config2.isProxyRequired());
      assertEquals(false, config2.isServerAuthorizationRequired());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_AUTHORIZE_URI, config2.getOAuthAuthorizeUri());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_URI, config2.getOAuthTokenUri());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI, config2.getOAuthTokenValidationUri());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_ID, config2.getOAuthClientId());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_SECRET, config2.getOAuthClientSecret());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_SCOPES, config2.getOAuthScopes());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_REDIRECT_URI, config2.getOAuthRedirectUri());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_SECRET_KEY_ALGORITHM, config2.getOAuthSecretKeyAlgorithm());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_ENCODED_SECRET_KEY, config2.getOAuthEncodedSecretKey());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_FAILS_ON_REFRESH_TOKEN_ERROR, config2.isOAuthFailsOnRefreshTokenError());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_ENABLED, config2.isOAuthTokenCacheEnabled());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_MAX_SIZE, config2.getOAuthCacheMaxSize());
      assertEquals(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_EVICT_TIMEOUT_MILLIS, config2.getOAuthCacheEvictTimeoutMillis());
      //@formatter:on

      // not reset by empty map
      assertEquals(handler, config2.getOAuthTokenHandler());
      assertEquals(tokenStore, config.getOAuthTokenStore());
   }
}
