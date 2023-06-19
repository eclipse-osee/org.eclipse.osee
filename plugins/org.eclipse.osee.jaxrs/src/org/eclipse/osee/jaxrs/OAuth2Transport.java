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

package org.eclipse.osee.jaxrs;

import java.net.URI;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.client.spec.ClientImpl.WebTargetImpl;
import org.apache.cxf.rs.security.oauth2.client.Consumer;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrant;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OAuth2Flows.OwnerCredentials;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2Transport {
   private final JaxRsApi jaxRsApi;

   public OAuth2Transport(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   public ClientAccessToken sendAuthorizationCodeGrant(OwnerCredentials owner, Consumer client, String sessionCookie,
      String tokenUri, String authCode, String redirectUri, Map<String, String> extraParams) {
      AuthorizationCodeGrant grant = new AuthorizationCodeGrant();
      grant.setCode(authCode);
      grant.setRedirectUri(redirectUri);
      boolean setAuthorizationHeader = false;
      WebClient webClient = newWebClient(owner, tokenUri, sessionCookie);
      return OAuthClientUtils.getAccessToken(webClient, client, grant, extraParams, setAuthorizationHeader);
   }

   public ClientAccessToken sendRefreshToken(OwnerCredentials owner, Consumer client, String tokenUri,
      String refreshToken, String scope, Map<String, String> extraParams) {
      RefreshTokenGrant refreshGrant = new RefreshTokenGrant(refreshToken, scope);
      WebClient webClient = newWebClient(owner, tokenUri);
      boolean setAuthorizationHeader = false;
      return OAuthClientUtils.getAccessToken(webClient, client, refreshGrant, extraParams, setAuthorizationHeader);
   }

   public Response sendAuthorizationCodeRequest(OwnerCredentials owner, URI authorizationURI) {
      Response response;
      try {
         response = newTargetBuilder(owner, authorizationURI).accept(MediaType.APPLICATION_JSON).get();
      } catch (WebApplicationException ex) {
         response = ex.getResponse();
      }
      return response;
   }

   public Response sendAccessConfirmation(OwnerCredentials owner, String sessionCookie, String confirmUri, Form form) {
      return newTargetBuilder(owner, confirmUri, sessionCookie).accept(MediaType.APPLICATION_JSON_TYPE).post(
         Entity.form(form));
   }

   public AccessTokenValidation sendTokenValidationRequest(OwnerCredentials owner, Consumer client,
      String sessionCookie, String tokenValidationUri, Form form) {
      return newTargetBuilder(owner, tokenValidationUri, sessionCookie).accept(MediaType.APPLICATION_JSON).post(
         Entity.form(form), AccessTokenValidation.class);
   }

   private WebClient newWebClient(OwnerCredentials credentials, String uri) {
      return newWebClient(credentials, uri, null);
   }

   private Builder newTargetBuilder(OwnerCredentials credentials, String uri) {
      return newTargetBuilder(credentials, uri, null);
   }

   private Builder newTargetBuilder(OwnerCredentials credentials, URI uri) {
      return newTargetBuilder(credentials, uri.toASCIIString());
   }

   private WebClient newWebClient(OwnerCredentials credentials, String url, String sessionCookie) {
      WebTarget target;
      if (Strings.isValid(sessionCookie)) {
         target = jaxRsApi.newTargetUrl(url);
      } else {
         target = jaxRsApi.newTargetUrlPasswd(url, credentials.getUsername(), credentials.getPassword());
      }

      WebClient webClient = null;
      if (target instanceof WebTargetImpl) {
         webClient = ((WebTargetImpl) target).getWebClient();
      }
      if (webClient != null && Strings.isValid(sessionCookie)) {
         webClient.header(HttpHeaders.COOKIE, sessionCookie);
      }
      return webClient;
   }

   private Builder newTargetBuilder(OwnerCredentials credentials, String url, String sessionCookie) {
      if (Strings.isValid(sessionCookie)) {
         return jaxRsApi.newTargetUrl(url).request().header(HttpHeaders.COOKIE, sessionCookie);
      }
      return jaxRsApi.newTargetUrlPasswd(url, credentials.getUsername(), credentials.getPassword()).request();
   }
}