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

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import javax.crypto.SecretKey;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler;
import org.eclipse.osee.jaxrs.client.JaxRsTokenStore;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2ClientRequestFilter.ClientAccessTokenCache;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link OAuth2ClientRequestFilter}
 * 
 * @author Roberto E. Escobar
 */
public class OAuth2ClientRequestFilterTest {

   private static final String TOKEN_ID_1 = "token_1";
   private static final String REFRESH_TOKEN_1 = "refresh_token_id_1";
   private static final String SCOPES_1 = "scopes_1";

   private static final String TOKEN_ID_2 = "token_2";
   private static final String REFRESH_TOKEN_2 = "refresh_token_id_2";
   private static final String SCOPES_2 = "scopes_2";

   private static final String REDIRECT_URI = "redirect_uri";
   private static final String SECRET_ALGORITHM = "secret_algorithm";
   private static final String SECRET_KEY = "encoded_secret_key";

   private static final boolean EXCEPTION__ON_REFRESH_ERROR = true;
   private static final boolean CONTINUE__ON_REFRESH_ERROR = false;

   private static final String BEARER_HEADER_TOKEN_1 = OAuthConstants.BEARER_AUTHORIZATION_SCHEME + " " + TOKEN_ID_1;
   private static final String BEARER_HEADER_TOKEN_2 = OAuthConstants.BEARER_AUTHORIZATION_SCHEME + " " + TOKEN_ID_2;

   private static final String HAWK_ALGO = OAuthConstants.HMAC_ALGO_SHA_1;
   private static final String HAWK_KEY = "hawk_key_1";

   private static final String POST_HTTP_METHOD = "POST";
   private static final String STORED_TOKEN = "stored token";

   private static final String STATE_1 = "state_1";
   private static final String STATE_2 = "state_2";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private OAuth2Flows flows;
   @Mock private OAuth2Serializer serializer;
   
   @Mock private ClientRequestContext context;

   @Mock private ClientAccessTokenCache cache;
   @Mock private JaxRsConfirmAccessHandler handler;
   @Mock private JaxRsTokenStore store;
   @Mock private SecretKey secretKey;
   
   @Mock private MultivaluedMap<String, Object> headers;
   @Captor private ArgumentCaptor<String> captor;
   //@formatter:on

   private OAuth2ClientRequestFilter filter;
   private ClientAccessToken token1;
   private ClientAccessToken token2;
   private URI uri;

   @Before
   public void setup() throws URISyntaxException {
      MockitoAnnotations.initMocks(this);

      uri = new URI("http://www.test.com");

      filter = new OAuth2ClientRequestFilter(flows, serializer);

      filter.setClientAccessTokenCache(cache);

      filter.setFailOnRefreshTokenError(EXCEPTION__ON_REFRESH_ERROR);
      filter.setRedirectUri(REDIRECT_URI);
      filter.setScopes(SCOPES_1);

      filter.setSecretKeyAlgorithm(SECRET_ALGORITHM);
      filter.setSecretKeyEncoded(SECRET_KEY);

      filter.setTokenHandler(handler);
      filter.setTokenStore(store);

      token1 = new ClientAccessToken(OAuthConstants.BEARER_TOKEN_TYPE, TOKEN_ID_1);
      token1.setRefreshToken(REFRESH_TOKEN_1);
      token1.setApprovedScope(SCOPES_1);

      token2 = new ClientAccessToken(OAuthConstants.BEARER_TOKEN_TYPE, TOKEN_ID_2);
      token2.setRefreshToken(REFRESH_TOKEN_2);
      token2.setApprovedScope(SCOPES_2);

      when(context.getUri()).thenReturn(uri);
      when(context.getHeaders()).thenReturn(headers);
      when(context.getMethod()).thenReturn(POST_HTTP_METHOD);

      when(serializer.decodeSecretKey(SECRET_KEY, SECRET_ALGORITHM)).thenReturn(secretKey);

      when(flows.generateState()).thenReturn(STATE_1, STATE_2);
   }

   @Test
   public void testHeaderBearerToken() {
      when(cache.get(uri)).thenReturn(token1);

      filter.filter(context);

      verify(headers).addFirst(HttpHeaders.AUTHORIZATION, BEARER_HEADER_TOKEN_1);
      verify(cache).get(uri);
   }

   @Test
   public void testSetHeaderHawkToken() {
      when(cache.get(uri)).thenReturn(token1);
      token1.setTokenType(OAuthConstants.HAWK_TOKEN_TYPE);
      token1.getParameters().put(OAuthConstants.HAWK_TOKEN_ALGORITHM, HAWK_ALGO);
      token1.getParameters().put(OAuthConstants.HAWK_TOKEN_KEY, HAWK_KEY);

      filter.filter(context);

      verify(headers).addFirst(eq(HttpHeaders.AUTHORIZATION), captor.capture());
      assertThat(captor.getValue(), hawkHeader(TOKEN_ID_1));
   }

   @Test
   public void testHeaderUnsupportedTokenType() {
      when(cache.get(uri)).thenReturn(token1);
      token1.setTokenType("OTHER_TYPE");

      thrown.expect(ProcessingException.class);
      thrown.expectMessage("Unsupported token type exception [OTHER_TYPE]");
      filter.filter(context);
   }

   @Test
   public void testTokenInStorageWithEncryption() {
      when(cache.get(uri)).thenReturn(null);
      when(store.getToken(uri)).thenReturn(STORED_TOKEN);

      when(serializer.decryptAccessToken(STORED_TOKEN, secretKey)).thenReturn(token1);

      filter.filter(context);

      verify(headers).addFirst(HttpHeaders.AUTHORIZATION, BEARER_HEADER_TOKEN_1);
      verify(cache).get(uri);
      verify(store).getToken(uri);
      verify(serializer).decryptAccessToken(STORED_TOKEN, secretKey);
   }

   @Test
   public void testTokenInStorageNoEncryption() {
      when(cache.get(uri)).thenReturn(null);
      when(store.getToken(uri)).thenReturn(STORED_TOKEN);

      filter.setSecretKeyAlgorithm(null);
      filter.setSecretKeyEncoded(null);

      when(serializer.fromJson(STORED_TOKEN)).thenReturn(token1);

      filter.filter(context);

      verify(headers).addFirst(HttpHeaders.AUTHORIZATION, BEARER_HEADER_TOKEN_1);
      verify(cache).get(uri);
      verify(store).getToken(uri);
      verify(serializer).fromJson(STORED_TOKEN);
   }

   @Test
   public void testTokenExpiredRefreshToken() {
      token1.setIssuedAt(-1L);
      token1.setExpiresIn(-3L);

      long currentTime = System.currentTimeMillis();
      token2.setExpiresIn(currentTime + 10000L);
      token2.setIssuedAt(currentTime);

      when(cache.get(uri)).thenReturn(token1);

      when(flows.refreshFlow(token1, STATE_1)).thenReturn(token2);
      when(serializer.encryptAccessToken(token2, secretKey)).thenReturn(STORED_TOKEN);

      filter.filter(context);

      verify(cache).get(uri);
      verify(flows).refreshFlow(token1, STATE_1);

      verify(cache).store(uri, token2);

      verify(serializer).encryptAccessToken(token2, secretKey);
      verify(store).storeToken(uri, STORED_TOKEN);

      verify(headers).addFirst(HttpHeaders.AUTHORIZATION, BEARER_HEADER_TOKEN_2);
   }

   @Test
   public void testTokenExpiredRefreshTokenNoEncryption() {
      token1.setIssuedAt(-1L);
      token1.setExpiresIn(-3L);

      long currentTime = System.currentTimeMillis();
      token2.setExpiresIn(currentTime + 10000L);
      token2.setIssuedAt(currentTime);

      when(cache.get(uri)).thenReturn(token1);
      when(flows.refreshFlow(token1, STATE_1)).thenReturn(token2);

      filter.setSecretKeyAlgorithm(null);
      filter.setSecretKeyEncoded(null);

      when(serializer.toJson(token2)).thenReturn(STORED_TOKEN);

      filter.filter(context);

      verify(cache).get(uri);
      verify(flows).refreshFlow(token1, STATE_1);

      verify(cache).store(uri, token2);
      verify(serializer).toJson(token2);
      verify(store).storeToken(uri, STORED_TOKEN);

      verify(headers).addFirst(HttpHeaders.AUTHORIZATION, BEARER_HEADER_TOKEN_2);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testTokenExpiredRefreshTokenFailsOnError() {
      token1.setIssuedAt(-1L);
      token1.setExpiresIn(-3L);

      long currentTime = System.currentTimeMillis();
      token2.setExpiresIn(currentTime + 10000L);
      token2.setIssuedAt(currentTime);

      when(cache.get(uri)).thenReturn(token1);
      when(flows.refreshFlow(token1, STATE_1)).thenThrow(Exception.class);
      filter.setFailOnRefreshTokenError(EXCEPTION__ON_REFRESH_ERROR);

      thrown.expect(ProcessingException.class);
      thrown.expectMessage("Error while attempting to refresh access token");
      filter.filter(context);

      verify(cache).get(uri);
      verify(flows).refreshFlow(token1, STATE_1);
      verify(flows, Mockito.times(0)).authorizationCodeFlow(handler, STATE_2, SCOPES_1, REDIRECT_URI);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testTokenExpiredRefreshTokenNoFailOnError() {
      token1.setIssuedAt(-1L);
      token1.setExpiresIn(-3L);

      long currentTime = System.currentTimeMillis();
      token2.setExpiresIn(currentTime + 10000L);
      token2.setIssuedAt(currentTime);

      when(cache.get(uri)).thenReturn(token1);
      when(flows.refreshFlow(token1, STATE_1)).thenThrow(Exception.class);
      when(flows.authorizationCodeFlow(handler, STATE_2, SCOPES_1, REDIRECT_URI)).thenReturn(token2);
      when(serializer.encryptAccessToken(token2, secretKey)).thenReturn(STORED_TOKEN);

      filter.setFailOnRefreshTokenError(CONTINUE__ON_REFRESH_ERROR);

      InOrder inOrder = Mockito.inOrder(flows);

      filter.filter(context);

      verify(cache).get(uri);

      inOrder.verify(flows).refreshFlow(token1, STATE_1);
      inOrder.verify(flows).authorizationCodeFlow(handler, STATE_2, SCOPES_1, REDIRECT_URI);

      verify(cache).store(uri, token2);
      verify(serializer).encryptAccessToken(token2, secretKey);
      verify(store).storeToken(uri, STORED_TOKEN);

      verify(headers).addFirst(HttpHeaders.AUTHORIZATION, BEARER_HEADER_TOKEN_2);
   }

   @Test
   public void testTokenExpiredNoRefreshToken() {
      token1.setIssuedAt(-1L);
      token1.setExpiresIn(-3L);

      long currentTime = System.currentTimeMillis();
      token2.setExpiresIn(currentTime + 10000L);
      token2.setIssuedAt(currentTime);

      token1.setRefreshToken(null);

      when(cache.get(uri)).thenReturn(token1);

      when(flows.authorizationCodeFlow(handler, STATE_1, SCOPES_1, REDIRECT_URI)).thenReturn(token2);
      when(serializer.encryptAccessToken(token2, secretKey)).thenReturn(STORED_TOKEN);

      filter.setFailOnRefreshTokenError(CONTINUE__ON_REFRESH_ERROR);

      filter.filter(context);

      verify(cache).get(uri);

      verify(flows, times(0)).refreshFlow(eq(token1), anyString());
      verify(flows).authorizationCodeFlow(handler, STATE_1, SCOPES_1, REDIRECT_URI);

      verify(cache).store(uri, token2);
      verify(serializer).encryptAccessToken(token2, secretKey);
      verify(store).storeToken(uri, STORED_TOKEN);

      verify(headers).addFirst(HttpHeaders.AUTHORIZATION, BEARER_HEADER_TOKEN_2);
   }

   private static HawkHeaderMatcher hawkHeader(String tokenId) {
      String regex = String.format("%s id=\"%s\",ts=\"\\d+\",nonce=\".+?\",mac=\".+?\"",
         OAuthConstants.HAWK_AUTHORIZATION_SCHEME, tokenId);
      Pattern pattern = Pattern.compile(regex);
      return new HawkHeaderMatcher(pattern);
   }

   private static class HawkHeaderMatcher extends TypeSafeMatcher<String> {

      private final Pattern regEx;

      private HawkHeaderMatcher(Pattern regEx) {
         this.regEx = regEx;
      }

      @Override
      public boolean matchesSafely(String item) {
         return regEx.matcher(item).find();
      }

      @Override
      public void describeMismatchSafely(String item, Description mismatchDescription) {
         mismatchDescription.appendText("was \"").appendText(item).appendText("\"");
      }

      @Override
      public void describeTo(Description description) {
         description.appendText("did not match").appendValue(regEx);
      }

   }

}
