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
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_STORE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.DEFAULT_JAXRS_CLIENT_PROXY_PASSWORD;
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
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.PORT_MIN_LIMIT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.RETRANSMIT_MIN_LIMIT;
import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.TIMEOUT_MIN_LIMIT;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ConnectionType;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants.ProxyType;

/**
 * @author Roberto E. Escobar
 */
public class JaxRsClientConfig {

   private static final Pattern PORT_ADDRESS_PATTERN = Pattern.compile(":(\\d+)");

   private long asyncExecuteTimeout;
   private boolean asyncExecuteTimeoutRejection;
   private long connectionTimeout;
   private long receiveTimeout;
   private ConnectionType connectionType;
   private int maxRetransmits;
   private String nonProxyHosts;
   private String fullProxyAddress;
   private String proxyAuthorizationType;
   private String proxyPassword;
   private ProxyType proxyType;
   private String proxyUsername;
   private String serverAuthorizationType;
   private String serverPassword;
   private String serverUsername;
   private boolean followRedirects;
   private boolean chunkingAllowed;
   private int chunkingThreshold;
   private int chunkLength;
   private boolean proxyClientSubResourcesInheritHeaders;
   private boolean proxyClientThreadSafe;

   private String oauthAuthorizeUri;
   private String oauthTokenUri;
   private String oauthTokenValidationUri;
   private String oauthClientId;
   private String oauthClientSecret;
   private String oauthRedirectUri;
   private String oauthScopes;
   private JaxRsTokenStore oauthTokenStore;
   private JaxRsConfirmAccessHandler oauthConfirmAccessHandler;
   private String oauthSecretKeyAlgorithm;
   private String oauthEncodedSecretKey;
   private boolean oauthFailsOnRefreshTokenError;
   private boolean oauthCacheEnabled;
   private int oauthCacheMaxSize;
   private long oauthCacheEvictTimeoutMillis;

   public JaxRsClientConfig() {
      reset();
   }

   private void reset() {
      setAsyncExecuteTimeout(DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT);
      setAsyncExecuteTimeoutRejection(DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION);
      setConnectionTimeout(DEFAULT_JAXRS_CLIENT_CONNECTION_TIMEOUT);
      setReceiveTimeout(DEFAULT_JAXRS_CLIENT_RECEIVE_TIMEOUT);
      setConnectionType(DEFAULT_JAXRS_CLIENT_CONNECTION_TYPE);
      setMaxRetransmits(DEFAULT_JAXRS_CLIENT_MAX_RETRANSMITS);
      setNonProxyHosts(DEFAULT_JAXRS_CLIENT_NON_PROXY_HOSTS);
      setProxyAddress(DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS);
      setProxyAuthorizationType(DEFAULT_JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE);
      setProxyPassword(DEFAULT_JAXRS_CLIENT_PROXY_PASSWORD);
      setProxyType(DEFAULT_JAXRS_CLIENT_PROXY_TYPE);
      setProxyUsername(DEFAULT_JAXRS_CLIENT_PROXY_USERNAME);
      setServerAuthorizationType(DEFAULT_JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE);
      setServerPassword(DEFAULT_JAXRS_CLIENT_SERVER_PASSWORD);
      setServerUsername(DEFAULT_JAXRS_CLIENT_SERVER_USERNAME);
      setFollowRedirects(DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS);
      setChunkingAllowed(DEFAULT_JAXRS_CLIENT_CHUNKING_ALLOWED);
      setChunkingThreshold(DEFAULT_JAXRS_CLIENT_CHUNKING_THRESHOLD);
      setChunkLength(DEFAULT_JAXRS_CLIENT_CHUNK_SIZE);
      setCreateThreadSafeProxyClients(DEFAULT_JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS);
      setProxyClientSubResourcesInheritHeaders(DEFAULT_JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS);
      setOAuthAuthorizeUri(DEFAULT_JAXRS_CLIENT_OAUTH_AUTHORIZE_URI);
      setOAuthClientId(DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_ID);
      setOAuthClientSecret(DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_SECRET);
      setOAuthRedirectUri(DEFAULT_JAXRS_CLIENT_OAUTH_REDIRECT_URI);
      setOAuthTokenUri(DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_URI);
      setOAuthTokenValidationUri(DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI);
      setOAuthScopes(DEFAULT_JAXRS_CLIENT_OAUTH_SCOPES);
      setOAuthSecretKeyAlgorithm(DEFAULT_JAXRS_CLIENT_OAUTH_SECRET_KEY_ALGORITHM);
      setOAuthEncodedSecretKey(DEFAULT_JAXRS_CLIENT_OAUTH_ENCODED_SECRET_KEY);
      setOAuthFailsOnRefreshTokenError(DEFAULT_JAXRS_CLIENT_OAUTH_FAILS_ON_REFRESH_TOKEN_ERROR);
      setOAuthCacheEnabled(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_ENABLED);
      setOAuthCacheMaxSize(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_MAX_SIZE);
      setOAuthCacheEvictTimeoutMillis(DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_EVICT_TIMEOUT_MILLIS);
   }

   public boolean isChunkingAllowed() {
      return chunkingAllowed;
   }

   public long getAsyncExecuteTimeout() {
      return limit(TIMEOUT_MIN_LIMIT, asyncExecuteTimeout);
   }

   public boolean isAsyncExecuteTimeoutRejection() {
      return asyncExecuteTimeoutRejection;
   }

   public long getConnectionTimeout() {
      return limit(TIMEOUT_MIN_LIMIT, connectionTimeout);
   }

   public long getReceiveTimeout() {
      return limit(TIMEOUT_MIN_LIMIT, receiveTimeout);
   }

   public ConnectionType getConnectionType() {
      return connectionType != null ? connectionType : DEFAULT_JAXRS_CLIENT_CONNECTION_TYPE;
   }

   public int getMaxRetransmits() {
      return limit(RETRANSMIT_MIN_LIMIT, maxRetransmits);
   }

   public String getNonProxyHosts() {
      return nonProxyHosts;
   }

   public String getProxyAuthorizationType() {
      return proxyAuthorizationType;
   }

   public String getProxyPassword() {
      return proxyPassword;
   }

   public ProxyType getProxyType() {
      return proxyType != null ? proxyType : DEFAULT_JAXRS_CLIENT_PROXY_TYPE;
   }

   public String getProxyUsername() {
      return proxyUsername;
   }

   public String getServerAuthorizationType() {
      return serverAuthorizationType;
   }

   public String getServerPassword() {
      return serverPassword;
   }

   public String getServerUsername() {
      return serverUsername;
   }

   public boolean isFollowRedirectsAllowed() {
      return followRedirects;
   }

   public int getChunkingThreshold() {
      return limit(CHUNK_THRESHOLD_MIN_LIMIT, chunkingThreshold);
   }

   public int getChunkLength() {
      return limit(CHUNK_LENGTH_MIN_LIMIT, chunkLength);
   }

   public String getProxyAddress() {
      return getHost(getFullProxyAddress());
   }

   public int getProxyPort() {
      return getPort(getFullProxyAddress());
   }

   public String getFullProxyAddress() {
      return fullProxyAddress;
   }

   public boolean isServerAuthorizationRequired() {
      return Strings.isValid(getServerUsername()) && Strings.isValid(getServerPassword());
   }

   public boolean isProxyAuthorizationRequired() {
      return Strings.isValid(getProxyUsername()) && Strings.isValid(getProxyPassword());
   }

   public boolean isProxyRequired() {
      return Strings.isValid(getProxyAddress());
   }

   public boolean isProxyClientSubResourcesInheritHeaders() {
      return proxyClientSubResourcesInheritHeaders;
   }

   public boolean isCreateThreadSafeProxyClients() {
      return proxyClientThreadSafe;
   }

   public String getOAuthAuthorizeUri() {
      return oauthAuthorizeUri;
   }

   public String getOAuthTokenUri() {
      return oauthTokenUri;
   }

   public String getOAuthTokenValidationUri() {
      return oauthTokenValidationUri;
   }

   public String getOAuthClientId() {
      return oauthClientId;
   }

   public String getOAuthClientSecret() {
      return oauthClientSecret;
   }

   public String getOAuthRedirectUri() {
      return oauthRedirectUri;
   }

   public String getOAuthScopes() {
      return oauthScopes;
   }

   public String getOAuthSecretKeyAlgorithm() {
      return oauthSecretKeyAlgorithm;
   }

   public String getOAuthEncodedSecretKey() {
      return oauthEncodedSecretKey;
   }

   public boolean isOAuthFailsOnRefreshTokenError() {
      return oauthFailsOnRefreshTokenError;
   }

   public boolean isOAuthTokenCacheEnabled() {
      return oauthCacheEnabled;
   }

   public int getOAuthCacheMaxSize() {
      return oauthCacheMaxSize;
   }

   public long getOAuthCacheEvictTimeoutMillis() {
      return oauthCacheEvictTimeoutMillis;
   }

   public JaxRsTokenStore getOAuthTokenStore() {
      return oauthTokenStore != null ? oauthTokenStore : DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_STORE;
   }

   public JaxRsConfirmAccessHandler getOAuthTokenHandler() {
      return oauthConfirmAccessHandler != null ? oauthConfirmAccessHandler : DEFAULT_JAXRS_CLIENT_OAUTH_CONFIRM_ACCESS_HANDLER;
   }

   void setAsyncExecuteTimeout(long asyncExecuteTimeout) {
      this.asyncExecuteTimeout = asyncExecuteTimeout;
   }

   void setAsyncExecuteTimeoutRejection(boolean asyncExecuteTimeoutRejection) {
      this.asyncExecuteTimeoutRejection = asyncExecuteTimeoutRejection;
   }

   void setConnectionTimeout(long connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
   }

   void setReceiveTimeout(long receiveTimeout) {
      this.receiveTimeout = receiveTimeout;
   }

   void setConnectionType(ConnectionType connectionType) {
      this.connectionType = connectionType;
   }

   void setMaxRetransmits(int maxRetransmits) {
      this.maxRetransmits = maxRetransmits;
   }

   void setNonProxyHosts(String nonProxyHosts) {
      this.nonProxyHosts = nonProxyHosts;
   }

   void setProxyAddress(String fullProxyAddress) {
      this.fullProxyAddress = fullProxyAddress;
   }

   void setProxyAuthorizationType(String proxyAuthorizationType) {
      this.proxyAuthorizationType = proxyAuthorizationType;
   }

   void setProxyPassword(String proxyPassword) {
      this.proxyPassword = proxyPassword;
   }

   void setProxyType(ProxyType proxyType) {
      this.proxyType = proxyType;
   }

   void setProxyUsername(String proxyUsername) {
      this.proxyUsername = proxyUsername;
   }

   void setServerAuthorizationType(String serverAuthorizationType) {
      this.serverAuthorizationType = serverAuthorizationType;
   }

   void setServerPassword(String serverPassword) {
      this.serverPassword = serverPassword;
   }

   void setServerUsername(String serverUsername) {
      this.serverUsername = serverUsername;
   }

   void setFollowRedirects(boolean autoRedirectsAllowed) {
      this.followRedirects = autoRedirectsAllowed;
   }

   void setChunkingAllowed(boolean chunkingAllowed) {
      this.chunkingAllowed = chunkingAllowed;
   }

   void setChunkingThreshold(int chunkingThreshold) {
      this.chunkingThreshold = chunkingThreshold;
   }

   void setChunkLength(int chunkLength) {
      this.chunkLength = chunkLength;
   }

   void setProxyClientSubResourcesInheritHeaders(boolean proxyClientSubResourcesInheritHeaders) {
      this.proxyClientSubResourcesInheritHeaders = proxyClientSubResourcesInheritHeaders;
   }

   public void setCreateThreadSafeProxyClients(boolean proxyClientThreadSafe) {
      this.proxyClientThreadSafe = proxyClientThreadSafe;
   }

   void setOAuthAuthorizeUri(String oauthAuthorizeUri) {
      this.oauthAuthorizeUri = oauthAuthorizeUri;
   }

   void setOAuthTokenUri(String oauthTokenUri) {
      this.oauthTokenUri = oauthTokenUri;
   }

   void setOAuthTokenValidationUri(String oauthTokenValidationUri) {
      this.oauthTokenValidationUri = oauthTokenValidationUri;
   }

   void setOAuthClientId(String oauthClientId) {
      this.oauthClientId = oauthClientId;
   }

   void setOAuthClientSecret(String oauthClientSecret) {
      this.oauthClientSecret = oauthClientSecret;
   }

   void setOAuthRedirectUri(String oauthRedirectUri) {
      this.oauthRedirectUri = oauthRedirectUri;
   }

   void setOAuthScopes(String oauthScopes) {
      this.oauthScopes = oauthScopes;
   }

   void setOAuthSecretKeyAlgorithm(String oauthSecretKeyAlgorithm) {
      this.oauthSecretKeyAlgorithm = oauthSecretKeyAlgorithm;
   }

   void setOAuthEncodedSecretKey(String oauthEncodedSecretKey) {
      this.oauthEncodedSecretKey = oauthEncodedSecretKey;
   }

   void setOAuthFailsOnRefreshTokenError(boolean oauthFailsOnRefreshTokenError) {
      this.oauthFailsOnRefreshTokenError = oauthFailsOnRefreshTokenError;
   }

   void setOAuthTokenStore(JaxRsTokenStore oauthTokenStore) {
      this.oauthTokenStore = oauthTokenStore;
   }

   void setOAuthConfirmAccessHandler(JaxRsConfirmAccessHandler oauthConfirmAccessHandler) {
      this.oauthConfirmAccessHandler = oauthConfirmAccessHandler;
   }

   void setOAuthCacheEnabled(boolean oauthCacheEnabled) {
      this.oauthCacheEnabled = oauthCacheEnabled;
   }

   void setOAuthCacheMaxSize(int oauthCacheMaxSize) {
      this.oauthCacheMaxSize = oauthCacheMaxSize;
   }

   void setOAuthCacheEvictTimeoutMillis(long oauthCacheEvictTimeoutMillis) {
      this.oauthCacheEvictTimeoutMillis = oauthCacheEvictTimeoutMillis;
   }

   void readProperties(Map<String, Object> src) {
      //@formatter:off
      setAsyncExecuteTimeout(getLong(src, JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT, DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT));
      setAsyncExecuteTimeoutRejection(getBoolean(src, JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION, DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION));
      setFollowRedirects(getBoolean(src, JAXRS_CLIENT_FOLLOW_REDIRECTS, DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS));
      setChunkingAllowed(getBoolean(src, JAXRS_CLIENT_CHUNKING_ALLOWED, DEFAULT_JAXRS_CLIENT_CHUNKING_ALLOWED));
      setChunkingThreshold(getInt(src, JAXRS_CLIENT_CHUNKING_THRESHOLD, DEFAULT_JAXRS_CLIENT_CHUNKING_THRESHOLD));
      setChunkLength(getInt(src, JAXRS_CLIENT_CHUNK_SIZE, DEFAULT_JAXRS_CLIENT_CHUNK_SIZE));
      setConnectionTimeout(getLong(src, JAXRS_CLIENT_CONNECTION_TIMEOUT, DEFAULT_JAXRS_CLIENT_CONNECTION_TIMEOUT));
      setConnectionType(getConnectionType(src, JAXRS_CLIENT_CONNECTION_TYPE, DEFAULT_JAXRS_CLIENT_CONNECTION_TYPE));
      setMaxRetransmits(getInt(src, JAXRS_CLIENT_MAX_RETRANSMITS, DEFAULT_JAXRS_CLIENT_MAX_RETRANSMITS));
      setNonProxyHosts(get(src, JAXRS_CLIENT_NON_PROXY_HOSTS, DEFAULT_JAXRS_CLIENT_NON_PROXY_HOSTS));
      setProxyAddress(get(src, JAXRS_CLIENT_PROXY_ADDRESS, DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS));
      setProxyAuthorizationType(get(src, JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE, DEFAULT_JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE));
      setProxyPassword(get(src, JAXRS_CLIENT_PROXY_PASSWORD, DEFAULT_JAXRS_CLIENT_PROXY_PASSWORD));
      setProxyType(getProxyType(src, JAXRS_CLIENT_PROXY_TYPE, DEFAULT_JAXRS_CLIENT_PROXY_TYPE));
      setProxyUsername(get(src, JAXRS_CLIENT_PROXY_USERNAME, DEFAULT_JAXRS_CLIENT_PROXY_USERNAME));
      setReceiveTimeout(getLong(src, JAXRS_CLIENT_RECEIVE_TIMEOUT, DEFAULT_JAXRS_CLIENT_RECEIVE_TIMEOUT));
      setServerAuthorizationType(get(src, JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE, DEFAULT_JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE));
      setServerPassword(get(src, JAXRS_CLIENT_SERVER_PASSWORD, DEFAULT_JAXRS_CLIENT_SERVER_PASSWORD));
      setServerUsername(get(src, JAXRS_CLIENT_SERVER_USERNAME, DEFAULT_JAXRS_CLIENT_SERVER_USERNAME));
      setCreateThreadSafeProxyClients(getBoolean(src, JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS, DEFAULT_JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS));;
      setProxyClientSubResourcesInheritHeaders(getBoolean(src, JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS, DEFAULT_JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS));
      setOAuthAuthorizeUri(get(src, JAXRS_CLIENT_OAUTH_AUTHORIZE_URI, DEFAULT_JAXRS_CLIENT_OAUTH_AUTHORIZE_URI));
      setOAuthClientId(get(src, JAXRS_CLIENT_OAUTH_CLIENT_ID, DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_ID));
      setOAuthClientSecret(get(src, JAXRS_CLIENT_OAUTH_CLIENT_SECRET, DEFAULT_JAXRS_CLIENT_OAUTH_CLIENT_SECRET));
      setOAuthRedirectUri(get(src, JAXRS_CLIENT_OAUTH_REDIRECT_URI, DEFAULT_JAXRS_CLIENT_OAUTH_REDIRECT_URI));
      setOAuthTokenUri(get(src, JAXRS_CLIENT_OAUTH_TOKEN_URI, DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_URI));
      setOAuthTokenValidationUri(get(src, JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI, DEFAULT_JAXRS_CLIENT_OAUTH_TOKEN_VALIDATION_URI));
      setOAuthScopes(get(src, JAXRS_CLIENT_OAUTH_SCOPES, DEFAULT_JAXRS_CLIENT_OAUTH_SCOPES));
      setOAuthSecretKeyAlgorithm(get(src, JAXRS_CLIENT_OAUTH_SECRET_KEY_ALGORITHM, DEFAULT_JAXRS_CLIENT_OAUTH_SECRET_KEY_ALGORITHM));
      setOAuthEncodedSecretKey(get(src, JAXRS_CLIENT_OAUTH_ENCODED_SECRET_KEY, DEFAULT_JAXRS_CLIENT_OAUTH_ENCODED_SECRET_KEY));
      setOAuthFailsOnRefreshTokenError(getBoolean(src, JAXRS_CLIENT_OAUTH_FAILS_ON_REFRESH_TOKEN_ERROR, DEFAULT_JAXRS_CLIENT_OAUTH_FAILS_ON_REFRESH_TOKEN_ERROR));
      setOAuthCacheEnabled(getBoolean(src, JAXRS_CLIENT_OAUTH_CACHE_ENABLED, DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_ENABLED));
      setOAuthCacheMaxSize(getInt(src, JAXRS_CLIENT_OAUTH_CACHE_MAX_SIZE, DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_MAX_SIZE));
      setOAuthCacheEvictTimeoutMillis(getLong(src, JAXRS_CLIENT_OAUTH_CACHE_EVICT_TIMEOUT_MILLIS, DEFAULT_JAXRS_CLIENT_OAUTH_CACHE_EVICT_TIMEOUT_MILLIS));
      //@formatter:on
   }

   private void copy(JaxRsClientConfig src, JaxRsClientConfig dest) {
      dest.asyncExecuteTimeout = src.asyncExecuteTimeout;
      dest.asyncExecuteTimeoutRejection = src.asyncExecuteTimeoutRejection;
      dest.connectionTimeout = src.connectionTimeout;
      dest.receiveTimeout = src.receiveTimeout;
      dest.connectionType = src.connectionType;
      dest.maxRetransmits = src.maxRetransmits;
      dest.nonProxyHosts = src.nonProxyHosts;
      dest.fullProxyAddress = src.fullProxyAddress;
      dest.proxyAuthorizationType = src.proxyAuthorizationType;
      dest.proxyPassword = src.proxyPassword;
      dest.proxyType = src.proxyType;
      dest.proxyUsername = src.proxyUsername;
      dest.serverAuthorizationType = src.serverAuthorizationType;
      dest.serverPassword = src.serverPassword;
      dest.serverUsername = src.serverUsername;
      dest.followRedirects = src.followRedirects;
      dest.chunkingAllowed = src.chunkingAllowed;
      dest.chunkingThreshold = src.chunkingThreshold;
      dest.chunkLength = src.chunkLength;
      dest.proxyClientSubResourcesInheritHeaders = src.proxyClientSubResourcesInheritHeaders;
      dest.proxyClientThreadSafe = src.proxyClientThreadSafe;
      dest.oauthAuthorizeUri = src.oauthAuthorizeUri;
      dest.oauthTokenUri = src.oauthTokenUri;
      dest.oauthTokenValidationUri = src.oauthTokenValidationUri;
      dest.oauthClientId = src.oauthClientId;
      dest.oauthClientSecret = src.oauthClientSecret;
      dest.oauthRedirectUri = src.oauthRedirectUri;
      dest.oauthScopes = src.oauthScopes;
      dest.oauthSecretKeyAlgorithm = src.oauthSecretKeyAlgorithm;
      dest.oauthEncodedSecretKey = src.oauthEncodedSecretKey;
      dest.oauthTokenStore = src.oauthTokenStore;
      dest.oauthConfirmAccessHandler = src.oauthConfirmAccessHandler;
      dest.oauthFailsOnRefreshTokenError = src.oauthFailsOnRefreshTokenError;
      dest.oauthCacheEnabled = src.oauthCacheEnabled;
      dest.oauthCacheMaxSize = src.oauthCacheMaxSize;
      dest.oauthCacheEvictTimeoutMillis = src.oauthCacheEvictTimeoutMillis;
   }

   public JaxRsClientConfig copy() {
      JaxRsClientConfig data = new JaxRsClientConfig();
      copy(this, data);
      return data;
   }

   protected void copy(JaxRsClientConfig src) {
      copy(src, this);
   }

   @Override
   public String toString() {
      return "JaxRsClientConfig [asyncExecuteTimeout=" + asyncExecuteTimeout + ", asyncExecuteTimeoutRejection=" + asyncExecuteTimeoutRejection + ", connectionTimeout=" + connectionTimeout + ", receiveTimeout=" + receiveTimeout + ", connectionType=" + connectionType + ", maxRetransmits=" + maxRetransmits + ", nonProxyHosts=" + nonProxyHosts + ", fullProxyAddress=" + fullProxyAddress + ", proxyAuthorizationType=" + proxyAuthorizationType + ", proxyPassword=" + proxyPassword + ", proxyType=" + proxyType + ", proxyUsername=" + proxyUsername + ", serverAuthorizationType=" + serverAuthorizationType + ", serverPassword=" + serverPassword + ", serverUsername=" + serverUsername + ", followRedirects=" + followRedirects + ", chunkingAllowed=" + chunkingAllowed + ", chunkingThreshold=" + chunkingThreshold + ", chunkLength=" + chunkLength + ", proxyClientSubResourcesInheritHeaders=" + proxyClientSubResourcesInheritHeaders + ", proxyClientThreadSafe=" + proxyClientThreadSafe + ", oauthAuthorizeUri=" + oauthAuthorizeUri + ", oauthTokenUri=" + oauthTokenUri + ", oauthTokenValidationUri=" + oauthTokenValidationUri + ", oauthClientId=" + oauthClientId + ", oauthClientSecret=" + oauthClientSecret + ", oauthRedirectUri=" + oauthRedirectUri + ", oauthScopes=" + oauthScopes + ", oauthTokenStore=" + oauthTokenStore + ", oauthConfirmAccessHandler=" + oauthConfirmAccessHandler + ", oauthSecretKeyAlgorithm=" + oauthSecretKeyAlgorithm + ", oauthEncodedSecretKey=" + oauthEncodedSecretKey + ", oauthFailsOnRefreshTokenError=" + oauthFailsOnRefreshTokenError + "]";
   }

   private static int limit(int minLimit, int value) {
      return value < minLimit ? minLimit : value;
   }

   private static long limit(long minLimit, long value) {
      return value < minLimit ? minLimit : value;
   }

   private static ProxyType getProxyType(Map<String, Object> props, String key, ProxyType defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return ProxyType.parse(toReturn);
   }

   private static ConnectionType getConnectionType(Map<String, Object> props, String key, ConnectionType defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return ConnectionType.parse(toReturn);
   }

   private static long getLong(Map<String, Object> props, String key, long defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Strings.isNumeric(toReturn) ? Long.parseLong(toReturn) : -1L;
   }

   private static int getInt(Map<String, Object> props, String key, int defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Strings.isNumeric(toReturn) ? Integer.parseInt(toReturn) : -1;
   }

   private static boolean getBoolean(Map<String, Object> props, String key, boolean defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Boolean.parseBoolean(toReturn);
   }

   private static String get(Map<String, Object> props, String key, String defaultValue) {
      String toReturn = defaultValue;
      Object object = props != null ? props.get(key) : null;
      if (object != null) {
         toReturn = String.valueOf(object);
      }
      return toReturn;
   }

   private static int getPort(String address) {
      int toReturn = PORT_MIN_LIMIT;
      if (Strings.isValid(address)) {
         Matcher matcher = PORT_ADDRESS_PATTERN.matcher(address);
         if (matcher.find()) {
            String port = matcher.group(1);
            toReturn = limit(PORT_MIN_LIMIT, Integer.parseInt(port));
         }
      }
      return toReturn;
   }

   private static String getHost(String address) {
      String toReturn = address;
      if (Strings.isValid(toReturn)) {
         toReturn = toReturn.replaceAll(".*?//", "");
         Matcher matcher = PORT_ADDRESS_PATTERN.matcher(toReturn);
         if (matcher.find()) {
            int index = matcher.start(1);
            toReturn = toReturn.substring(0, index - 1);
         }
      }
      return toReturn;
   }

}
