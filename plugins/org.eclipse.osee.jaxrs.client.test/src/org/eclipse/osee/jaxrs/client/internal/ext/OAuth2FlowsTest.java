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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.cxf.rs.security.oauth2.client.Consumer;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.OOBAuthorizationResponse;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.ConfirmAccessRequest;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.ConfirmAccessResponse;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Flows.AuthFlowResponse;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Flows.OwnerCredentials;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link OAuth2Flows}
 *
 * @author Roberto E. Escobar
 */
public class OAuth2FlowsTest {

   private static final String CLIENT_ID = "client_id";
   private static final String CLIENT_SECRET = "client_secret";

   private static final String USERNAME = "owner_1";
   private static final String PASSWORD = "owner_password";

   private static final String AUTHORIZE_URI = "authorize_uri";
   private static final String TOKEN_URI = "token_uri";
   private static final String VALIDATION_URI = "validation_uri";

   private static final String TOKEN_ID = "token_1";
   private static final String REFRESH_TOKEN = "refresh_token_id_1";
   private static final String SCOPES = "scopes";
   private static final String REDIRECT_URI = "redirect_uri";
   private static final String STATE = "state_1";
   private static final String SESSION_COOKIE = "cookie_1";
   private static final String AUTH_CODE = "authorization_code_1";
   private static final String CONFIRM_URI = "confirm_uri";

   private static final List<String> APP_CERTIFICATE = Arrays.asList("app_cert_1");
   private static final String APP_DESCRIPTION = "app_description_1";
   private static final String APP_LOGO_URI = "app_logo_uri_1";
   private static final String APP_NAME = "app_name_1";
   private static final String APP_WEB_URI = "app_web_uri_1";
   private static final String APP_AUDIENCE = "audience_1";

   private static final String PERMISSION_NAME = "permission_1";
   private static final String PERMISSION_DESCR = "permission_description_1";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private OAuth2Transport transport;
   @Mock private OwnerCredentials owner;
   @Mock private Consumer client;

   @Mock private JaxRsConfirmAccessHandler handler;
   @Captor private ArgumentCaptor<Form> formCaptor;
   @Captor private ArgumentCaptor<Map<String, String>> paramsCaptor;
   @Captor private ArgumentCaptor<URI> authUriCaptor;

   @Mock private Response response1;
   @Mock private OAuthAuthorizationData authData;
   @Mock private OOBAuthorizationResponse oobResponse;
   @Mock private MultivaluedMap<String, Object> headers;

   @Mock private Response response2;
   @Captor private ArgumentCaptor<ConfirmAccessRequest> confirmCaptor;
   @Mock private ConfirmAccessResponse confirmResponse;

   //@formatter:on

   private OAuth2Flows flows;
   private ClientAccessToken token;
   private OAuthPermission permission;
   private Map<String, String> appProperties;
   private AccessTokenValidation tokenValidation;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      flows = new OAuth2Flows(transport, owner, client, AUTHORIZE_URI, TOKEN_URI, VALIDATION_URI);

      token = new ClientAccessToken(OAuthConstants.BEARER_TOKEN_TYPE, TOKEN_ID);
      token.setRefreshToken(REFRESH_TOKEN);
      token.setApprovedScope(SCOPES);

      when(client.getKey()).thenReturn(CLIENT_ID);
      when(client.getSecret()).thenReturn(CLIENT_SECRET);

      when(owner.getUsername()).thenReturn(USERNAME);
      when(owner.getPassword()).thenReturn(PASSWORD);
      permission = new OAuthPermission(PERMISSION_NAME, PERMISSION_DESCR);
      permission.setDefaultPermission(true);
      List<? extends OAuthPermission> permissions = Arrays.asList(permission);

      appProperties = new HashMap<>();
      appProperties.put("prop1", "a");
      appProperties.put("prop2", "b");

      when(authData.getApplicationCertificates()).thenReturn(APP_CERTIFICATE);
      when(authData.getApplicationDescription()).thenReturn(APP_DESCRIPTION);
      when(authData.getApplicationLogoUri()).thenReturn(APP_LOGO_URI);
      when(authData.getApplicationName()).thenReturn(APP_NAME);
      when(authData.getApplicationWebUri()).thenReturn(APP_WEB_URI);
      when(authData.getAudience()).thenReturn(APP_AUDIENCE);
      when(authData.getAuthenticityToken()).thenReturn(SESSION_COOKIE);
      when(authData.getClientId()).thenReturn(CLIENT_ID);
      when(authData.getEndUserName()).thenReturn(USERNAME);
      when(authData.getExtraApplicationProperties()).thenReturn(appProperties);
      when(authData.getPermissions()).thenAnswer(answer(permissions));
      when(authData.getProposedScope()).thenReturn(SCOPES);
      when(authData.getRedirectUri()).thenReturn(REDIRECT_URI);
      when(authData.getReplyTo()).thenReturn(CONFIRM_URI);
      when(authData.getState()).thenReturn(STATE);

      tokenValidation = new AccessTokenValidation();
      tokenValidation.setClientId(CLIENT_ID);
      tokenValidation.setTokenKey(TOKEN_ID);
      tokenValidation.setTokenType(OAuthConstants.BEARER_TOKEN_TYPE);

   }

   @Test
   public void testRefreshFlow() {
      token.getParameters().put("param1", "param1Value");

      flows.refreshFlow(token, STATE);

      verify(transport).sendRefreshToken(eq(owner), eq(client), eq(TOKEN_URI), eq(REFRESH_TOKEN), eq(SCOPES),
         paramsCaptor.capture());

      Map<String, String> params = paramsCaptor.getValue();
      assertEquals(2, params.size());
      assertNotNull(params.get(OAuthConstants.STATE));
      assertEquals("param1Value", params.get("param1"));
   }

   @Test
   public void testAuthorizationGrantErrorResponse() {
      when(transport.sendAuthorizationCodeRequest(eq(owner), any(URI.class))).thenReturn(response1);
      when(response1.getStatusInfo()).thenReturn(Status.FORBIDDEN);

      thrown.expect(ProcessingException.class);
      thrown.expectMessage("Error requesting authorization code - errorValue [N/A]");
      flows.requestAuthorizationGrant(STATE, SCOPES, REDIRECT_URI);
   }

   @Test
   public void testAuthorizationGrantAuthorizationDataResponseStateError() {
      when(transport.sendAuthorizationCodeRequest(eq(owner), any(URI.class))).thenReturn(response1);
      when(response1.getStatusInfo()).thenReturn(Status.OK);
      when(response1.readEntity(OAuthAuthorizationData.class)).thenReturn(authData);
      when(authData.getState()).thenReturn("other state");

      thrown.expect(ProcessingException.class);
      thrown.expectMessage(
         "OAuth Authorization Flow - Expected state [" + STATE + "] did not match response state [other state]");
      flows.requestAuthorizationGrant(STATE, SCOPES, REDIRECT_URI);
   }

   @Test
   public void testAuthorizationGrantAuthorizationDataResponse() {
      when(transport.sendAuthorizationCodeRequest(eq(owner), any(URI.class))).thenReturn(response1);
      when(response1.getStatusInfo()).thenReturn(Status.OK);
      when(response1.readEntity(OAuthAuthorizationData.class)).thenReturn(authData);
      when(response1.getMetadata()).thenReturn(headers);
      when(headers.getFirst("Set-Cookie")).thenReturn(SESSION_COOKIE);

      AuthFlowResponse actual = flows.requestAuthorizationGrant(STATE, SCOPES, REDIRECT_URI);

      assertEquals(authData, actual.getAuthorizationData());
      assertEquals(true, actual.isAuthData());
      assertEquals(false, actual.isOobAuthorization());
      assertNull(actual.getAuthorizationCode());
      assertEquals(SESSION_COOKIE, actual.getAuthenticityCookie());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testAuthorizationGrantOOBAuthorizationResponse() {
      when(transport.sendAuthorizationCodeRequest(eq(owner), any(URI.class))).thenReturn(response1);
      when(response1.getStatusInfo()).thenReturn(Status.OK);
      when(response1.readEntity(OAuthAuthorizationData.class)).thenThrow(Exception.class);
      when(response1.readEntity(OOBAuthorizationResponse.class)).thenReturn(oobResponse);
      when(response1.getMetadata()).thenReturn(headers);
      when(headers.getFirst("Set-Cookie")).thenReturn(SESSION_COOKIE);
      when(oobResponse.getAuthorizationCode()).thenReturn(AUTH_CODE);

      AuthFlowResponse actual = flows.requestAuthorizationGrant(STATE, SCOPES, REDIRECT_URI);
      assertNull(actual.getAuthorizationData());
      assertEquals(false, actual.isAuthData());
      assertEquals(true, actual.isOobAuthorization());
      assertEquals(AUTH_CODE, actual.getAuthorizationCode());
      assertEquals(SESSION_COOKIE, actual.getAuthenticityCookie());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testAuthorizationGrantOOBAuthorizationResponseDataReadError() {
      when(transport.sendAuthorizationCodeRequest(eq(owner), any(URI.class))).thenReturn(response1);
      when(response1.getStatusInfo()).thenReturn(Status.OK);
      when(response1.readEntity(OAuthAuthorizationData.class)).thenThrow(Exception.class);
      when(response1.readEntity(OOBAuthorizationResponse.class)).thenThrow(Exception.class);
      when(response1.getEntity()).thenReturn("entity");

      thrown.expect(ProcessingException.class);
      thrown.expectMessage("Unexpected data type error [entity]");
      flows.requestAuthorizationGrant(STATE, SCOPES, REDIRECT_URI);
   }

   @Test
   public void testConfirmAccessErrorResponse() {
      when(authData.getReplyTo()).thenReturn(CONFIRM_URI);
      when(handler.onConfirmAccess(any(ConfirmAccessRequest.class))).thenReturn(confirmResponse);

      when(
         transport.sendAccessConfirmation(eq(owner), eq(SESSION_COOKIE), eq(CONFIRM_URI), any(Form.class))).thenReturn(
            response2);
      when(response2.getStatusInfo()).thenReturn(Status.FORBIDDEN);

      thrown.expect(ProcessingException.class);
      thrown.expectMessage("Error requesting access confirmation - errorValue [N/A]");
      flows.confirmAccess(handler, authData, SESSION_COOKIE);
   }

   @Test
   public void testConfirmAccessResponse() {
      when(handler.onConfirmAccess(any(ConfirmAccessRequest.class))).thenReturn(confirmResponse);

      when(
         transport.sendAccessConfirmation(eq(owner), eq(SESSION_COOKIE), eq(CONFIRM_URI), any(Form.class))).thenReturn(
            response2);
      when(response2.getStatusInfo()).thenReturn(Status.OK);
      when(response2.getMetadata()).thenReturn(headers);
      when(headers.getFirst("Set-Cookie")).thenReturn(SESSION_COOKIE);

      when(response2.readEntity(OOBAuthorizationResponse.class)).thenReturn(oobResponse);
      when(oobResponse.getAuthorizationCode()).thenReturn(AUTH_CODE);

      AuthFlowResponse actual = flows.confirmAccess(handler, authData, SESSION_COOKIE);

      assertNull(actual.getAuthorizationData());
      assertEquals(false, actual.isAuthData());
      assertEquals(true, actual.isOobAuthorization());
      assertEquals(AUTH_CODE, actual.getAuthorizationCode());
      assertEquals(SESSION_COOKIE, actual.getAuthenticityCookie());

      verify(handler).onConfirmAccess(confirmCaptor.capture());

      ConfirmAccessRequest confirmRequest = confirmCaptor.getValue();

      assertEquals(APP_NAME, confirmRequest.getApplicationName());
      assertEquals(APP_DESCRIPTION, confirmRequest.getApplicationDescription());
      assertEquals(APP_LOGO_URI, confirmRequest.getApplicationLogoUri());
      assertEquals(APP_WEB_URI, confirmRequest.getApplicationWebUri());
      assertEquals(USERNAME, confirmRequest.getEndUserName());
      List<? extends org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.Permission> perms =
         confirmRequest.getPermissionsRequested();
      assertEquals(1, perms.size());
      org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.Permission perm = perms.get(0);
      assertEquals(PERMISSION_NAME, perm.getName());
      assertEquals(PERMISSION_DESCR, perm.getDescription());
      assertEquals(true, perm.isDefault());

      verify(transport).sendAccessConfirmation(eq(owner), eq(SESSION_COOKIE), eq(CONFIRM_URI), formCaptor.capture());
      MultivaluedMap<String, String> form = formCaptor.getValue().asMap();
      assertEquals(7, form.size());
   }

   @Test
   public void testConfirmAccessResponseRedirection() throws URISyntaxException {
      when(handler.onConfirmAccess(any(ConfirmAccessRequest.class))).thenReturn(confirmResponse);

      when(
         transport.sendAccessConfirmation(eq(owner), eq(SESSION_COOKIE), eq(CONFIRM_URI), any(Form.class))).thenReturn(
            response2);
      when(response2.getStatusInfo()).thenReturn(Status.SEE_OTHER);
      when(response2.getMetadata()).thenReturn(headers);
      when(headers.getFirst("Set-Cookie")).thenReturn(SESSION_COOKIE);

      String location = String.format("http://www.hello.com?%s=%s&%s=%s", OAuthConstants.AUTHORIZATION_CODE_VALUE,
         AUTH_CODE, OAuthConstants.STATE, STATE);

      URI locationUri = new URI(location);
      when(response2.getLocation()).thenReturn(locationUri);

      AuthFlowResponse actual = flows.confirmAccess(handler, authData, SESSION_COOKIE);

      assertNull(actual.getAuthorizationData());
      assertEquals(false, actual.isAuthData());
      assertEquals(true, actual.isOobAuthorization());
      assertEquals(AUTH_CODE, actual.getAuthorizationCode());
      assertEquals(SESSION_COOKIE, actual.getAuthenticityCookie());

      verify(handler).onConfirmAccess(confirmCaptor.capture());

      ConfirmAccessRequest confirmRequest = confirmCaptor.getValue();

      assertEquals(APP_NAME, confirmRequest.getApplicationName());
      assertEquals(APP_DESCRIPTION, confirmRequest.getApplicationDescription());
      assertEquals(APP_LOGO_URI, confirmRequest.getApplicationLogoUri());
      assertEquals(APP_WEB_URI, confirmRequest.getApplicationWebUri());
      assertEquals(USERNAME, confirmRequest.getEndUserName());
      List<? extends org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.Permission> perms =
         confirmRequest.getPermissionsRequested();
      assertEquals(1, perms.size());
      org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.Permission perm = perms.get(0);
      assertEquals(PERMISSION_NAME, perm.getName());
      assertEquals(PERMISSION_DESCR, perm.getDescription());
      assertEquals(true, perm.isDefault());
   }

   @Test
   public void testAccessConfirmForm() {
      List<? extends org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.Permission> permissions =
         Collections.singletonList(OAuth2Flows.asPermission(permission));

      when(confirmResponse.isGranted()).thenReturn(true);
      when(confirmResponse.getPermissionsGranted()).thenAnswer(answer(permissions));

      MultivaluedMap<String, String> form = OAuth2Flows.newAccessConfirmForm(authData, confirmResponse).asMap();

      assertEquals(CLIENT_ID, form.getFirst(OAuthConstants.CLIENT_ID));
      assertEquals(APP_AUDIENCE, form.getFirst(OAuthConstants.CLIENT_AUDIENCE));
      assertEquals(SESSION_COOKIE, form.getFirst(OAuthConstants.SESSION_AUTHENTICITY_TOKEN));
      assertEquals(REDIRECT_URI, form.getFirst(OAuthConstants.REDIRECT_URI));
      assertEquals(STATE, form.getFirst(OAuthConstants.STATE));
      assertEquals(SCOPES, form.getFirst(OAuthConstants.SCOPE));
      assertEquals(OAuthConstants.AUTHORIZATION_DECISION_ALLOW, form.getFirst(PERMISSION_NAME + "_status"));
      assertEquals(OAuthConstants.AUTHORIZATION_DECISION_ALLOW,
         form.getFirst(OAuthConstants.AUTHORIZATION_DECISION_KEY));

      when(confirmResponse.isGranted()).thenReturn(false);
      MultivaluedMap<String, String> form2 = OAuth2Flows.newAccessConfirmForm(authData, confirmResponse).asMap();
      assertEquals(OAuthConstants.AUTHORIZATION_DECISION_DENY,
         form2.getFirst(OAuthConstants.AUTHORIZATION_DECISION_KEY));
   }

   @Test
   public void testExchangeCodeForToken() {
      when(transport.sendAuthorizationCodeGrant(eq(owner), eq(client), eq(SESSION_COOKIE), eq(TOKEN_URI), eq(AUTH_CODE),
         eq(REDIRECT_URI), anyMapOf(String.class, String.class))).thenReturn(token);

      ClientAccessToken actual = flows.exchangeCodeForToken(SESSION_COOKIE, AUTH_CODE, STATE, SCOPES, REDIRECT_URI);

      assertEquals(token, actual);
      verify(transport).sendAuthorizationCodeGrant(eq(owner), eq(client), eq(SESSION_COOKIE), eq(TOKEN_URI),
         eq(AUTH_CODE), eq(REDIRECT_URI), paramsCaptor.capture());

      Map<String, String> value = paramsCaptor.getValue();
      assertEquals(2, value.size());
      assertEquals(STATE, value.get(OAuthConstants.STATE));
      assertEquals(SCOPES, value.get(OAuthConstants.SCOPE));
   }

   @Test
   public void testValidateToken() {
      when(transport.sendTokenValidationRequest(eq(owner), eq(client), eq(SESSION_COOKIE), eq(VALIDATION_URI),
         any(Form.class))).thenReturn(tokenValidation);

      long issuedAt = System.currentTimeMillis();
      long expiresIn = 10000L;
      token.setIssuedAt(issuedAt);
      token.setExpiresIn(expiresIn);
      tokenValidation.setTokenIssuedAt(issuedAt);
      tokenValidation.setTokenLifetime(expiresIn);

      flows.validateToken(SESSION_COOKIE, token);

      verify(transport).sendTokenValidationRequest(eq(owner), eq(client), eq(SESSION_COOKIE), eq(VALIDATION_URI),
         formCaptor.capture());
      MultivaluedMap<String, String> form = formCaptor.getValue().asMap();
      assertEquals(OAuthConstants.BEARER_TOKEN_TYPE, form.getFirst(OAuthConstants.AUTHORIZATION_SCHEME_TYPE));
      assertEquals(TOKEN_ID, form.getFirst(OAuthConstants.AUTHORIZATION_SCHEME_DATA));
   }

   @Test
   public void testValidateTokenFails() {
      when(transport.sendTokenValidationRequest(eq(owner), eq(client), eq(SESSION_COOKIE), eq(VALIDATION_URI),
         any(Form.class))).thenReturn(tokenValidation);

      long issuedAt = System.currentTimeMillis();
      long expiresIn = 10000L;
      token.setIssuedAt(issuedAt);
      token.setExpiresIn(expiresIn);
      tokenValidation.setTokenIssuedAt(issuedAt);
      tokenValidation.setTokenLifetime(expiresIn + 6L);

      thrown.expect(ProcessingException.class);
      thrown.expectMessage("Token validation failed");
      flows.validateToken(SESSION_COOKIE, token);

      verify(transport).sendTokenValidationRequest(eq(owner), eq(client), eq(SESSION_COOKIE), eq(VALIDATION_URI),
         formCaptor.capture());
      MultivaluedMap<String, String> form = formCaptor.getValue().asMap();
      assertEquals(OAuthConstants.BEARER_TOKEN_TYPE, form.getFirst(OAuthConstants.AUTHORIZATION_SCHEME_TYPE));
      assertEquals(TOKEN_ID, form.getFirst(OAuthConstants.AUTHORIZATION_SCHEME_DATA));
   }

   @Test
   public void testAuthorizationFlow() {
      when(transport.sendAuthorizationCodeRequest(eq(owner), any(URI.class))).thenReturn(response1);
      when(handler.onConfirmAccess(any(ConfirmAccessRequest.class))).thenReturn(confirmResponse);
      when(
         transport.sendAccessConfirmation(eq(owner), eq(SESSION_COOKIE), eq(CONFIRM_URI), any(Form.class))).thenReturn(
            response2);
      when(transport.sendAuthorizationCodeGrant(eq(owner), eq(client), eq(SESSION_COOKIE), eq(TOKEN_URI), eq(AUTH_CODE),
         eq(REDIRECT_URI), anyMapOf(String.class, String.class))).thenReturn(token);
      when(transport.sendTokenValidationRequest(eq(owner), eq(client), eq(SESSION_COOKIE), eq(VALIDATION_URI),
         any(Form.class))).thenReturn(tokenValidation);

      long issuedAt = System.currentTimeMillis();
      long expiresIn = 10000L;

      token.setIssuedAt(issuedAt);
      token.setExpiresIn(expiresIn);

      tokenValidation.setTokenIssuedAt(issuedAt);
      tokenValidation.setTokenLifetime(expiresIn);

      when(response1.getStatusInfo()).thenReturn(Status.OK);
      when(response1.readEntity(OAuthAuthorizationData.class)).thenReturn(authData);
      when(response1.getMetadata()).thenReturn(headers);
      when(headers.getFirst("Set-Cookie")).thenReturn(SESSION_COOKIE);

      when(response2.getStatusInfo()).thenReturn(Status.OK);
      when(response2.readEntity(OOBAuthorizationResponse.class)).thenReturn(oobResponse);
      when(response2.getMetadata()).thenReturn(headers);
      when(headers.getFirst("Set-Cookie")).thenReturn(SESSION_COOKIE);

      when(oobResponse.getAuthorizationCode()).thenReturn(AUTH_CODE);

      InOrder inOrder = inOrder(transport);

      flows.authorizationCodeFlow(handler, STATE, SCOPES, REDIRECT_URI);

      inOrder.verify(transport).sendAuthorizationCodeRequest(eq(owner), authUriCaptor.capture());
      inOrder.verify(transport).sendAccessConfirmation(eq(owner), eq(SESSION_COOKIE), eq(CONFIRM_URI),
         formCaptor.capture());
      inOrder.verify(transport).sendAuthorizationCodeGrant(eq(owner), eq(client), eq(SESSION_COOKIE), eq(TOKEN_URI),
         eq(AUTH_CODE), eq(REDIRECT_URI), paramsCaptor.capture());
      inOrder.verify(transport).sendTokenValidationRequest(eq(owner), eq(client), eq(SESSION_COOKIE),
         eq(VALIDATION_URI), formCaptor.capture());
   }

   private static <T> Answer<T> answer(final T object) {
      return new Answer<T>() {

         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return object;
         }
      };
   }
}
