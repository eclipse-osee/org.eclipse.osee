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

import static org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Util.newException;
import static org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Util.toException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriBuilder;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils.Consumer;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.apache.cxf.rs.security.oauth2.common.OOBAuthorizationResponse;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.UrlQuery;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.ConfirmAccessRequest;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.ConfirmAccessResponse;
import org.eclipse.osee.jaxrs.client.JaxRsConfirmAccessHandler.Permission;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2Flows {

   public static interface OwnerCredentials {

      String getUsername();

      String getPassword();
   }

   private final OAuth2Transport transport;
   private final OwnerCredentials owner;

   private final Consumer client;

   private final String authorizeUri;
   private final String tokenUri;
   private final String tokenValidationUri;

   public OAuth2Flows(OAuth2Transport transport, OwnerCredentials owner, Consumer client, String authorizeUri, String tokenUri, String tokenValidationUri) {
      super();
      this.transport = transport;
      this.owner = owner;
      this.client = client;
      this.authorizeUri = authorizeUri;
      this.tokenUri = tokenUri;
      this.tokenValidationUri = tokenValidationUri;
   }

   public String generateState() {
      return OAuthUtils.generateRandomTokenKey();
   }

   public ClientAccessToken authorizationCodeFlow(JaxRsConfirmAccessHandler handler, String state, String scope, String redirectUri) {
      String sessionCookie = null;
      AuthFlowResponse response = requestAuthorizationGrant(state, scope, redirectUri);
      if (response.isAuthData()) {
         sessionCookie = response.getAuthenticityCookie();
         response = confirmAccess(handler, response.getAuthorizationData(), sessionCookie);
      }

      if (response.isOobAuthorization()) {
         String authorizationCode = response.getAuthorizationCode();
         if (!Strings.isValid(authorizationCode)) {
            throw newException("Authorization code was null");
         }
         ClientAccessToken token = exchangeCodeForToken(sessionCookie, authorizationCode, state, scope, redirectUri);
         if (Strings.isValid(tokenValidationUri)) {
            validateToken(response.getAuthenticityCookie(), token);
         }
         return token;
      } else {
         // could result in token right away if pre-authorized token exists
         throw newException("Auth flow is broken - invalid state");
      }
   }

   public ClientAccessToken refreshFlow(ClientAccessToken data, String state) {
      Map<String, String> extraParams = new HashMap<>();
      if (data.getParameters() != null) {
         extraParams.putAll(data.getParameters());
      }
      extraParams.put(OAuthConstants.STATE, state);
      return transport.sendRefreshToken(owner, client, tokenUri, data.getRefreshToken(), data.getApprovedScope(),
         extraParams);
   }

   protected AuthFlowResponse requestAuthorizationGrant(String state, String scope, String redirectUri) {
      UriBuilder builder = OAuthClientUtils.getAuthorizationURIBuilder(authorizeUri, client.getKey(), scope);
      if (redirectUri != null) {
         builder.queryParam(OAuthConstants.REDIRECT_URI, redirectUri);
      }
      if (state != null) {
         builder.queryParam(OAuthConstants.STATE, state);
      }
      // confidential clients
      builder.queryParam(OAuthConstants.CLIENT_SECRET, client.getSecret());

      URI authorizationURI = builder.build();
      Response response = transport.sendAuthorizationCodeRequest(owner, authorizationURI);
      StatusType statusType = response.getStatusInfo();
      if (Family.SUCCESSFUL != statusType.getFamily()) {
         throw toException(response, "Error requesting authorization code");
      } else {
         OAuthAuthorizationData authData = null;
         try {
            response.bufferEntity();
            authData = response.readEntity(OAuthAuthorizationData.class);
         } catch (Exception ex) {
            try {
               response.bufferEntity();
               OOBAuthorizationResponse oob = response.readEntity(OOBAuthorizationResponse.class);
               return new AuthFlowResponse(response, oob.getAuthorizationCode());
            } catch (Exception ex1) {
               throw newException(ex1, "Unexpected data type error [%s]", response.getEntity());
            }
         }
         checkState(state, authData.getState());
         return new AuthFlowResponse(response, authData);
      }
   }

   protected AuthFlowResponse confirmAccess(JaxRsConfirmAccessHandler handler, OAuthAuthorizationData authData, String sessionCookie) {
      ConfirmAccessRequest request = newAccessConfirmRequest(authData);
      ConfirmAccessResponse ownerResponse = handler.onConfirmAccess(request);
      Form form = newAccessConfirmForm(authData, ownerResponse);
      String replyTo = authData.getReplyTo();

      Response response = transport.sendAccessConfirmation(owner, sessionCookie, replyTo, form);
      StatusType statusType = response.getStatusInfo();
      if (Family.SUCCESSFUL != statusType.getFamily() && Family.REDIRECTION != statusType.getFamily()) {
         throw toException(response, "Error requesting access confirmation");
      } else {
         String code = null;
         URI location = response.getLocation();
         if (location != null) {
            UrlQuery query = new UrlQuery();
            try {
               query.parse(location.getQuery());
               code = query.getParameter(OAuthConstants.AUTHORIZATION_CODE_VALUE);
               checkState(authData.getState(), query.getParameter(OAuthConstants.STATE));
            } catch (UnsupportedEncodingException ex) {
               throw newException(ex, "Exception while parsing auth code URI [%s]", location);
            }
         } else {
            try {
               response.bufferEntity();
               OOBAuthorizationResponse oob = response.readEntity(OOBAuthorizationResponse.class);
               code = oob.getAuthorizationCode();
            } catch (Exception ex) {
               String value = response.readEntity(String.class);
               throw newException(ex, "Unexpected data type error [%s]", value);
            }
         }
         return new AuthFlowResponse(response, code);
      }
   }

   protected ClientAccessToken exchangeCodeForToken(String sessionCookie, String authCode, String state, String scope, String redirectUri) {
      Map<String, String> extraParams = new HashMap<>();
      if (state != null) {
         extraParams.put(OAuthConstants.STATE, state);
      }
      if (scope != null) {
         extraParams.put(OAuthConstants.SCOPE, scope);
      }
      // client must fully authenticate with token end-point
      try {
         return transport.sendAuthorizationCodeGrant(owner, client, sessionCookie, tokenUri, authCode, redirectUri,
            extraParams);
      } catch (Exception ex) {
         throw newException(ex, "Error exchanging authorization grant for access token");
      }
   }

   protected void validateToken(String sessionCookie, ClientAccessToken token) {
      Form form = new Form();
      form.param(OAuthConstants.AUTHORIZATION_SCHEME_TYPE, token.getTokenType());
      form.param(OAuthConstants.AUTHORIZATION_SCHEME_DATA, token.getTokenKey());
      try {
         AccessTokenValidation validation =
            transport.sendTokenValidationRequest(owner, client, sessionCookie, tokenValidationUri, form);
         assertTokenEquals(token, validation);
      } catch (Exception ex) {
         throw newException(ex, "Token validation failed");
      }
   }

   private static void assertTokenEquals(ClientAccessToken expected, AccessTokenValidation actual) {
      if (!(//
      equals(expected.getTokenKey(), actual.getTokenKey()) //
         && equals(expected.getTokenType(), actual.getTokenType()) //
         && equals(expected.getExpiresIn(), actual.getTokenLifetime()) //
         && equals(expected.getIssuedAt(), actual.getTokenIssuedAt()) //
      )) {
         throw newException("Token validation failed");
      }
   }

   private static void checkState(String expected, String actual) {
      if (!equals(expected, actual)) {
         throw newException("OAuth Authorization Flow - Expected state [%s] did not match response state [%s]",
            expected, actual);
      }
   }

   private static boolean equals(Object a, Object b) {
      if (a == null && b == null) {
         return true;
      } else if (a != null && b != null) {
         return a.equals(b);
      }
      return false;
   }

   protected static Form newAccessConfirmForm(OAuthAuthorizationData data, ConfirmAccessResponse ownerResponse) {
      Form form = new Form();
      form.param(OAuthConstants.CLIENT_ID, data.getClientId());
      form.param(OAuthConstants.CLIENT_AUDIENCE, data.getAudience());
      form.param(OAuthConstants.SESSION_AUTHENTICITY_TOKEN, data.getAuthenticityToken());
      form.param(OAuthConstants.REDIRECT_URI, data.getRedirectUri());
      form.param(OAuthConstants.STATE, data.getState());
      form.param(OAuthConstants.SCOPE, data.getProposedScope());

      List<? extends Permission> permissions = ownerResponse.getPermissionsGranted();
      if (permissions != null) {
         for (Permission permission : permissions) {
            String name = String.format("%s_status", permission.getName());
            form.param(name, OAuthConstants.AUTHORIZATION_DECISION_ALLOW);
         }
      }
      form.param(OAuthConstants.AUTHORIZATION_DECISION_KEY,
         ownerResponse.isGranted() ? OAuthConstants.AUTHORIZATION_DECISION_ALLOW : OAuthConstants.AUTHORIZATION_DECISION_DENY);
      return form;
   }

   protected static ConfirmAccessRequest newAccessConfirmRequest(OAuthAuthorizationData data) {
      return new ConfirmAccessRequestWrapper(data);
   }

   private static final class ConfirmAccessRequestWrapper implements ConfirmAccessRequest {
      private final OAuthAuthorizationData data;
      private List<Permission> permissions;

      public ConfirmAccessRequestWrapper(OAuthAuthorizationData data) {
         super();
         this.data = data;
      }

      @Override
      public List<? extends Permission> getPermissionsRequested() {
         if (permissions == null) {
            List<Permission> permissions = new ArrayList<>();
            for (org.apache.cxf.rs.security.oauth2.common.Permission perm : data.getPermissions()) {
               permissions.add(asPermission(perm));
            }
            this.permissions = Collections.unmodifiableList(permissions);
         }
         return permissions;
      }

      @Override
      public String getEndUserName() {
         return data.getEndUserName();
      }

      @Override
      public String getApplicationWebUri() {
         return data.getApplicationWebUri();
      }

      @Override
      public String getApplicationName() {
         return data.getApplicationName();
      }

      @Override
      public String getApplicationLogoUri() {
         return data.getApplicationLogoUri();
      }

      @Override
      public String getApplicationDescription() {
         return data.getApplicationDescription();
      }

      @Override
      public String toString() {
         return "ConfirmAccessRequest [data=" + data + "]";
      }
   };

   protected static Permission asPermission(final org.apache.cxf.rs.security.oauth2.common.Permission perm) {
      return new PermissionWrapper(perm);
   }

   private static final class PermissionWrapper implements Permission {
      private final org.apache.cxf.rs.security.oauth2.common.Permission perm;

      public PermissionWrapper(org.apache.cxf.rs.security.oauth2.common.Permission perm) {
         super();
         this.perm = perm;
      }

      @Override
      public String getName() {
         return perm.getPermission();
      }

      @Override
      public String getDescription() {
         return perm.getDescription();
      }

      @Override
      public boolean isDefault() {
         return perm.isDefault();
      }

      @Override
      public String toString() {
         return "Permission [name=" + getName() + ", description=" + getDescription() + ", isDefault=" + isDefault() + "]";
      }
   }

   protected static final class AuthFlowResponse {
      private final OAuthAuthorizationData authData;
      private final String authCode;
      private final Response response;
      private final boolean isAuthData;
      private final boolean isOobAuthorization;

      public AuthFlowResponse(Response response, OAuthAuthorizationData authData) {
         super();
         this.response = response;
         this.isAuthData = true;
         this.isOobAuthorization = false;
         this.authData = authData;
         this.authCode = null;
      }

      public AuthFlowResponse(Response response, String authCode) {
         super();
         this.response = response;
         this.authCode = authCode;
         this.isAuthData = false;
         this.isOobAuthorization = true;
         this.authData = null;
      }

      public String getAuthorizationCode() {
         return authCode;
      }

      public String getAuthenticityCookie() {
         return (String) response.getMetadata().getFirst("Set-Cookie");
      }

      public boolean isAuthData() {
         return isAuthData;
      }

      public boolean isOobAuthorization() {
         return isOobAuthorization;
      }

      public OAuthAuthorizationData getAuthorizationData() {
         return authData;
      }

      @SuppressWarnings({"unchecked"})
      public <T> T getData(Class<T> clazz) {
         return (T) response.getEntity();
      }

   }
}