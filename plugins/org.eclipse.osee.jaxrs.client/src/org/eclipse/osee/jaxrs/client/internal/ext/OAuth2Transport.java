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

import java.net.URI;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils.Consumer;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrant;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsClient.JaxRsClientBuilder;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Flows.OwnerCredentials;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2Transport {

   public ClientAccessToken sendAuthorizationCodeGrant(OwnerCredentials owner, Consumer client, String sessionCookie, String tokenUri, String authCode, String redirectUri, Map<String, String> extraParams) {
      AuthorizationCodeGrant grant = new AuthorizationCodeGrant();
      grant.setCode(authCode);
      grant.setRedirectUri(redirectUri);
      boolean setAuthorizationHeader = false;
      WebClient webClient = newWebClient(owner, tokenUri, sessionCookie);
      return OAuthClientUtils.getAccessToken(webClient, client, grant, extraParams, setAuthorizationHeader);
   }

   public ClientAccessToken sendRefreshToken(OwnerCredentials owner, Consumer client, String tokenUri, String refreshToken, String scope, Map<String, String> extraParams) {
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
      try {
         return newTargetBuilder(owner, confirmUri, sessionCookie).accept(MediaType.APPLICATION_JSON_TYPE).post(
            Entity.form(form));
      } catch (WebApplicationException ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   public AccessTokenValidation sendTokenValidationRequest(OwnerCredentials owner, Consumer client, String sessionCookie, String tokenValidationUri, Form form) {
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

   private WebClient newWebClient(OwnerCredentials credentials, String uri, String sessionCookie) {
      JaxRsWebTarget target;
      if (Strings.isValid(sessionCookie)) {
         target = JaxRsClient.newClient().target(uri);
      } else {
         target = JaxRsClient.newBuilder()//
            .username(credentials.getUsername())//
            .password(credentials.getPassword())//
            .build().target(uri);
      }

      WebClient webClient = null;
      if (target instanceof JaxRsWebTargetImpl) {
         webClient = ((JaxRsWebTargetImpl) target).getWebClient();
      }
      if (webClient != null && Strings.isValid(sessionCookie)) {
         webClient.header(HttpHeaders.COOKIE, sessionCookie);
      }
      return webClient;
   }

   private Builder newTargetBuilder(OwnerCredentials credentials, String uri, String sessionCookie) {
      JaxRsClientBuilder clientBuilder = JaxRsClient.newBuilder().followRedirects(false);
      Builder builder;
      if (Strings.isValid(sessionCookie)) {
         builder = clientBuilder.build().target(uri).request().header(HttpHeaders.COOKIE, sessionCookie);
      } else {
         builder = clientBuilder//
            .username(credentials.getUsername())//
            .password(credentials.getPassword())//
            .build().target(uri).request();
      }
      return builder;
   }

}