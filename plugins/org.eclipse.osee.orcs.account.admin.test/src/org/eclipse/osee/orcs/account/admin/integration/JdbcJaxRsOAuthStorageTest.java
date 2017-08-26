/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthStorage;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;
import org.eclipse.osee.jaxrs.server.security.OAuthCodeGrant;
import org.eclipse.osee.jaxrs.server.security.OAuthToken;
import org.eclipse.osee.jaxrs.server.security.OAuthTokenType;
import org.eclipse.osee.orcs.account.admin.internal.oauth.JdbcJaxRsOAuthStorage;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.mockito.Mock;

/**
 * Test Case for {@link JdbcJaxRsOAuthStorage}
 *
 * @author Roberto E. Escobar
 */
public class JdbcJaxRsOAuthStorageTest {

   private static final String AUTH_CODE_1 = "auth-code-1";

   public static final long CODE_UUID = 5679L;
   public static final long CLIENT_ID = 912371;
   public static final long SUBJECT_ID = 876523L;
   public static final long ISSUED_AT = 1231L;
   public static final long EXPIRES_IN = 9876L;

   public static final String REDIRECT_URI = "http://my.redirect.com/callback";
   public static final String AUDIENCE = "audience-1";
   public static final List<String> APPROVED_SCOPES = Arrays.asList("a-scope", "b-scope", "c-scope");
   public static final String CLIENT_CODE_VERIFIER = "client-code-verifier-1";

   public static final long AT_UUID = 612319L;
   private static final String AT_KEY_1 = "at-key-1";
   private static final String AT_TOKEN_TYPE_1 = "token-type-1";
   private static final String AT_GRANT_TYPE_1 = "grant-type-1";
   private static final OAuthTokenType AT_TYPE_1 = OAuthTokenType.BEARER_TOKEN;

   public static final long RT_UUID = 3219L;
   private static final String RT_KEY_1 = "rt-key-1";
   private static final String RT_TOKEN_TYPE_1 = "rt-type-1";
   private static final String RT_GRANT_TYPE_1 = "rt-grant-type-1";
   private static final OAuthTokenType RT_TYPE_1 = OAuthTokenType.REFRESH_TOKEN;

   public static final List<String> APPLICATION_CERTIFICATE = Arrays.asList("certificate-1");
   public static final String APPLICATION_DESCRIPTION = "description-1";
   public static final String APPLICATION_LOGO_URI = "logo-uri-1";
   public static final String APPLICATION_NAME = "application-name-1";
   public static final String APPLICATION_WEB_URI = "web-uri-1";
   public static final String CLIENT_KEY = "client-key-1";
   public static final String CLIENT_SECRET = "client-secret-1";
   public static final boolean IS_CONFIDENTIAL = true;

   public static final List<String> GRANT_TYPES = Arrays.asList("grant-1", "grant-2", "grant-3");
   public static final List<String> REDIRECT_URIS = Arrays.asList("uri-1", "uri2");
   public static final List<String> ALLOWED_AUDIENCES = Arrays.asList("audience-1");
   public static final List<String> REGISTERED_SCOPES = Arrays.asList("scope-1", "scope-2", "scope-3");

   @Rule
   public TestRule osgi = OrcsIntegrationRule.integrationRule(this);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private JaxRsOAuthStorage storage;

   // @formatter:off
   @Mock private OAuthCodeGrant authCode;
   @Mock private OAuthToken accessToken;
   @Mock private OAuthToken refreshToken;
   @Mock private OseePrincipal principal;
   @Mock private OAuthClient client;
   // @formatter:on

   public static Map<String, String> applicationProperties;

   @Before
   public void testSetup() {
      initMocks(this);

      applicationProperties = new LinkedHashMap<>();
      applicationProperties.put("a", "1");
      applicationProperties.put("b", "2");
      applicationProperties.put("c", "3");

      when(authCode.getUuid()).thenReturn(CODE_UUID);
      when(authCode.getClientId()).thenReturn(CLIENT_ID);
      when(authCode.getSubjectId()).thenReturn(SUBJECT_ID);
      when(authCode.getIssuedAt()).thenReturn(ISSUED_AT);
      when(authCode.getExpiresIn()).thenReturn(EXPIRES_IN);
      when(authCode.getCode()).thenReturn(AUTH_CODE_1);
      when(authCode.getRedirectUri()).thenReturn(REDIRECT_URI);
      when(authCode.getAudience()).thenReturn(AUDIENCE);
      when(authCode.getApprovedScopes()).thenReturn(APPROVED_SCOPES);
      when(authCode.getClientCodeVerifier()).thenReturn(CLIENT_CODE_VERIFIER);

      when(accessToken.getUuid()).thenReturn(AT_UUID);
      when(accessToken.getClientId()).thenReturn(CLIENT_ID);
      when(accessToken.getSubjectId()).thenReturn(SUBJECT_ID);
      when(accessToken.getIssuedAt()).thenReturn(ISSUED_AT);
      when(accessToken.getExpiresIn()).thenReturn(EXPIRES_IN);
      when(accessToken.getTokenKey()).thenReturn(AT_KEY_1);
      when(accessToken.getTokenType()).thenReturn(AT_TOKEN_TYPE_1);
      when(accessToken.getType()).thenReturn(AT_TYPE_1);
      when(accessToken.getGrantType()).thenReturn(AT_GRANT_TYPE_1);
      when(accessToken.getAudience()).thenReturn(AUDIENCE);

      when(refreshToken.getUuid()).thenReturn(RT_UUID);
      when(refreshToken.getClientId()).thenReturn(CLIENT_ID);
      when(refreshToken.getSubjectId()).thenReturn(SUBJECT_ID);
      when(refreshToken.getIssuedAt()).thenReturn(ISSUED_AT);
      when(refreshToken.getExpiresIn()).thenReturn(EXPIRES_IN);
      when(refreshToken.getTokenKey()).thenReturn(RT_KEY_1);
      when(refreshToken.getTokenType()).thenReturn(RT_TOKEN_TYPE_1);
      when(refreshToken.getType()).thenReturn(RT_TYPE_1);
      when(refreshToken.getGrantType()).thenReturn(RT_GRANT_TYPE_1);
      when(refreshToken.getAudience()).thenReturn(AUDIENCE);

      when(principal.getGuid()).thenReturn(-1L);

      when(client.getClientUuid()).thenReturn(CLIENT_ID);
      when(client.getSubjectId()).thenReturn(SUBJECT_ID);
      when(client.getApplicationName()).thenReturn(APPLICATION_NAME);
      when(client.getApplicationDescription()).thenReturn(APPLICATION_DESCRIPTION);
      when(client.getApplicationWebUri()).thenReturn(APPLICATION_WEB_URI);
      when(client.getApplicationLogoUri()).thenReturn(APPLICATION_LOGO_URI);
      when(client.getClientId()).thenReturn(CLIENT_KEY);
      when(client.getClientSecret()).thenReturn(CLIENT_SECRET);
      when(client.getApplicationCertificates()).thenReturn(APPLICATION_CERTIFICATE);
      when(client.isConfidential()).thenReturn(IS_CONFIDENTIAL);

      when(client.getAllowedGrantTypes()).thenReturn(GRANT_TYPES);
      when(client.getRedirectUris()).thenReturn(REDIRECT_URIS);
      when(client.getRegisteredAudiences()).thenReturn(ALLOWED_AUDIENCES);
      when(client.getRegisteredScopes()).thenReturn(REGISTERED_SCOPES);
      when(client.getProperties()).thenReturn(applicationProperties);

      when(client.hasApplicationLogoSupplier()).thenReturn(false);
      when(client.getApplicationLogoSupplier()).thenReturn(null);
   }

   @Test
   public void testClientStorage() {
      ArtifactId createdClientId = storage.storeClient(principal, client);
      long createdClientUuid = Long.valueOf(createdClientId.getUuid());
      when(client.getClientUuid()).thenReturn(createdClientUuid);
      when(authCode.getClientId()).thenReturn(createdClientUuid);
      when(accessToken.getClientId()).thenReturn(createdClientUuid);
      when(refreshToken.getClientId()).thenReturn(createdClientUuid);

      long clientUuid = storage.getClientUuidByKey(CLIENT_KEY);
      assertEquals(createdClientId.getUuid(), Long.valueOf(clientUuid));

      OAuthClient actualClient = storage.getClientByClientUuid(createdClientUuid);

      assertEquals(createdClientUuid, actualClient.getClientUuid());
      assertEquals(SUBJECT_ID, actualClient.getSubjectId());
      assertEquals(APPLICATION_NAME, actualClient.getApplicationName());
      assertEquals(APPLICATION_DESCRIPTION, actualClient.getApplicationDescription());
      assertEquals(APPLICATION_WEB_URI, actualClient.getApplicationWebUri());
      assertEquals(APPLICATION_LOGO_URI, actualClient.getApplicationLogoUri());
      assertEquals(CLIENT_KEY, actualClient.getClientId());
      assertEquals(CLIENT_SECRET, actualClient.getClientSecret());
      assertEquals(APPLICATION_CERTIFICATE, actualClient.getApplicationCertificates());
      assertEquals(IS_CONFIDENTIAL, actualClient.isConfidential());
      assertEquals(GRANT_TYPES, actualClient.getAllowedGrantTypes());
      assertEquals(REDIRECT_URIS, actualClient.getRedirectUris());
      assertEquals(ALLOWED_AUDIENCES, actualClient.getRegisteredAudiences());
      assertEquals(REGISTERED_SCOPES, actualClient.getRegisteredScopes());
      assertEquals(applicationProperties, actualClient.getProperties());

      actualClient = storage.getClientByClientKey(CLIENT_KEY);
      assertEquals(createdClientUuid, actualClient.getClientUuid());
      assertEquals(SUBJECT_ID, actualClient.getSubjectId());
      assertEquals(APPLICATION_NAME, actualClient.getApplicationName());
      assertEquals(APPLICATION_DESCRIPTION, actualClient.getApplicationDescription());
      assertEquals(APPLICATION_WEB_URI, actualClient.getApplicationWebUri());
      assertEquals(APPLICATION_LOGO_URI, actualClient.getApplicationLogoUri());
      assertEquals(CLIENT_KEY, actualClient.getClientId());
      assertEquals(CLIENT_SECRET, actualClient.getClientSecret());
      assertEquals(APPLICATION_CERTIFICATE, actualClient.getApplicationCertificates());
      assertEquals(IS_CONFIDENTIAL, actualClient.isConfidential());
      assertEquals(GRANT_TYPES, actualClient.getAllowedGrantTypes());
      assertEquals(REDIRECT_URIS, actualClient.getRedirectUris());
      assertEquals(ALLOWED_AUDIENCES, actualClient.getRegisteredAudiences());
      assertEquals(REGISTERED_SCOPES, actualClient.getRegisteredScopes());
      assertEquals(applicationProperties, actualClient.getProperties());

      storage.removeClient(principal, actualClient);

      clientUuid = storage.getClientUuidByKey(CLIENT_KEY);
      assertEquals(-1L, clientUuid);

      actualClient = storage.getClientByClientUuid(createdClientUuid);
      assertNull(actualClient);

      actualClient = storage.getClientByClientKey(CLIENT_KEY);
      assertNull(actualClient);
   }

   @Test
   public void testCascadeClientToTokenDeletion() {
      ArtifactId createdClientArtId = storage.storeClient(principal, client);
      long createdClientUuid = Long.valueOf(createdClientArtId.getUuid());
      when(client.getClientUuid()).thenReturn(createdClientUuid);
      when(authCode.getClientId()).thenReturn(createdClientUuid);
      when(accessToken.getClientId()).thenReturn(createdClientUuid);
      when(refreshToken.getClientId()).thenReturn(createdClientUuid);
      storage.storeCodeGrant(authCode);
      storage.storeToken(accessToken, refreshToken);
      storage.relateTokens(refreshToken, accessToken);

      long clientUuid = storage.getClientUuidByKey(CLIENT_KEY);
      assertEquals(createdClientArtId.getUuid(), Long.valueOf(clientUuid));

      OAuthClient actualClient = storage.getClientByClientUuid(createdClientUuid);
      assertNotNull(actualClient);

      OAuthToken accessToken = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, AT_GRANT_TYPE_1);
      assertNotNull(accessToken);

      OAuthToken refresh = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, RT_GRANT_TYPE_1);
      assertNotNull(refresh);

      storage.removeClient(principal, client);

      clientUuid = storage.getClientUuidByKey(CLIENT_KEY);
      assertEquals(-1L, clientUuid);

      actualClient = storage.getClientByClientUuid(createdClientUuid);
      assertNull(actualClient);

      accessToken = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, AT_GRANT_TYPE_1);
      assertNull(accessToken);

      refresh = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, RT_GRANT_TYPE_1);
      assertNull(refresh);
   }

   @Test
   public void testAuthCode() {
      ArtifactId createdClientId = storage.storeClient(principal, client);
      long createdClientUuid = Long.valueOf(createdClientId.getUuid());
      when(client.getClientUuid()).thenReturn(createdClientUuid);
      when(authCode.getClientId()).thenReturn(createdClientUuid);
      when(accessToken.getClientId()).thenReturn(createdClientUuid);
      when(refreshToken.getClientId()).thenReturn(createdClientUuid);

      OAuthCodeGrant actual = storage.getCodeGrant(AUTH_CODE_1);
      assertNull(actual);

      storage.storeCodeGrant(authCode);

      actual = storage.getCodeGrant(AUTH_CODE_1);

      assertEquals(CODE_UUID, actual.getUuid());
      assertEquals(createdClientUuid, actual.getClientId());
      assertEquals(SUBJECT_ID, actual.getSubjectId());
      assertEquals(ISSUED_AT, actual.getIssuedAt());
      assertEquals(EXPIRES_IN, actual.getExpiresIn());
      assertEquals(AUTH_CODE_1, actual.getCode());
      assertEquals(REDIRECT_URI, actual.getRedirectUri());
      assertEquals(AUDIENCE, actual.getAudience());
      assertEquals(APPROVED_SCOPES, actual.getApprovedScopes());
      assertEquals(CLIENT_CODE_VERIFIER, actual.getClientCodeVerifier());

      storage.removeCodeGrant(authCode);

      actual = storage.getCodeGrant(AUTH_CODE_1);
      assertNull(actual);

      long clientUuid = storage.getClientUuidByKey(CLIENT_KEY);
      assertEquals(createdClientUuid, clientUuid);
   }

   @Test
   public void testAccessToken() {
      ArtifactId createdClientId = storage.storeClient(principal, client);
      long createdClientUuid = Long.valueOf(createdClientId.getUuid());
      when(client.getClientUuid()).thenReturn(createdClientUuid);
      when(authCode.getClientId()).thenReturn(createdClientUuid);
      when(accessToken.getClientId()).thenReturn(createdClientUuid);
      when(refreshToken.getClientId()).thenReturn(createdClientUuid);

      storage.storeToken(accessToken);

      OAuthToken actual = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, AT_GRANT_TYPE_1);

      assertEquals(AT_UUID, actual.getUuid());
      assertEquals(createdClientUuid, actual.getClientId());
      assertEquals(SUBJECT_ID, actual.getSubjectId());
      assertEquals(ISSUED_AT, actual.getIssuedAt());
      assertEquals(EXPIRES_IN, actual.getExpiresIn());
      assertEquals(AT_KEY_1, actual.getTokenKey());
      assertEquals(AT_TOKEN_TYPE_1, actual.getTokenType());
      assertEquals(AT_TYPE_1, actual.getType());
      assertEquals(AT_GRANT_TYPE_1, actual.getGrantType());
      assertEquals(AUDIENCE, actual.getAudience());
      assertNull(actual.getRefreshToken());

      storage.removeTokenByKey(AT_KEY_1);

      actual = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, AT_GRANT_TYPE_1);
      assertNull(actual);

      long clientUuid = storage.getClientUuidByKey(CLIENT_KEY);
      assertEquals(createdClientUuid, clientUuid);
   }

   @Test
   public void testAccessTokenWithRefreshToken() {
      ArtifactId createdClientId = storage.storeClient(principal, client);
      long createdClientUuid = Long.valueOf(createdClientId.getUuid());
      when(client.getClientUuid()).thenReturn(createdClientUuid);
      when(authCode.getClientId()).thenReturn(createdClientUuid);
      when(accessToken.getClientId()).thenReturn(createdClientUuid);
      when(refreshToken.getClientId()).thenReturn(createdClientUuid);

      storage.storeToken(accessToken, refreshToken);
      storage.relateTokens(refreshToken, accessToken);

      OAuthToken token1 = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, AT_GRANT_TYPE_1);

      assertEquals(AT_UUID, token1.getUuid());
      assertEquals(createdClientUuid, token1.getClientId());
      assertEquals(SUBJECT_ID, token1.getSubjectId());
      assertEquals(ISSUED_AT, token1.getIssuedAt());
      assertEquals(EXPIRES_IN, token1.getExpiresIn());
      assertEquals(AT_KEY_1, token1.getTokenKey());
      assertEquals(AT_TOKEN_TYPE_1, token1.getTokenType());
      assertEquals(AT_TYPE_1, token1.getType());
      assertEquals(AT_GRANT_TYPE_1, token1.getGrantType());
      assertEquals(AUDIENCE, token1.getAudience());
      assertEquals(RT_KEY_1, token1.getRefreshToken());

      OAuthToken refresh = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, RT_GRANT_TYPE_1);

      assertEquals(RT_UUID, refresh.getUuid());
      assertEquals(createdClientUuid, refresh.getClientId());
      assertEquals(SUBJECT_ID, refresh.getSubjectId());
      assertEquals(ISSUED_AT, refresh.getIssuedAt());
      assertEquals(EXPIRES_IN, refresh.getExpiresIn());
      assertEquals(RT_KEY_1, refresh.getTokenKey());
      assertEquals(RT_TOKEN_TYPE_1, refresh.getTokenType());
      assertEquals(RT_TYPE_1, refresh.getType());
      assertEquals(RT_GRANT_TYPE_1, refresh.getGrantType());
      assertEquals(AUDIENCE, refresh.getAudience());
      assertNull(refresh.getRefreshToken());

      Iterable<OAuthToken> tokens = storage.getAccessTokensByRefreshToken(RT_KEY_1);
      OAuthToken token2 = tokens.iterator().next();

      assertEquals(AT_UUID, token2.getUuid());
      assertEquals(createdClientUuid, token2.getClientId());
      assertEquals(SUBJECT_ID, token2.getSubjectId());
      assertEquals(ISSUED_AT, token2.getIssuedAt());
      assertEquals(EXPIRES_IN, token2.getExpiresIn());
      assertEquals(AT_KEY_1, token2.getTokenKey());
      assertEquals(AT_TOKEN_TYPE_1, token2.getTokenType());
      assertEquals(AT_TYPE_1, token2.getType());
      assertEquals(AT_GRANT_TYPE_1, token2.getGrantType());
      assertEquals(AUDIENCE, token2.getAudience());
      assertEquals(RT_KEY_1, token2.getRefreshToken());

      storage.removeToken(tokens);

      token1 = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, AT_GRANT_TYPE_1);
      assertNull(token1);

      storage.removeTokenByKey(RT_KEY_1);

      refresh = storage.getPreauthorizedToken(createdClientUuid, SUBJECT_ID, RT_GRANT_TYPE_1);
      assertNull(refresh);

      long clientUuid = storage.getClientUuidByKey(CLIENT_KEY);
      assertEquals(createdClientUuid, clientUuid);
   }
}
