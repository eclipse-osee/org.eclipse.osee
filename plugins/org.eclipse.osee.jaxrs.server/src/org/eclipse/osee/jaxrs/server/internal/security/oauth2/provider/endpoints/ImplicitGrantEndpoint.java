/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.OAuthRedirectionState;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.services.ImplicitGrantService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.ClientLogoUriResolver;

/**
 * @author Angel Avila
 */
public class ImplicitGrantEndpoint extends ImplicitGrantService {

   private final ClientLogoUriResolver clientLogoUriResolver;
   private boolean useRegisteredRedirectUriIfPossible = true;

   public ImplicitGrantEndpoint(ClientLogoUriResolver clientLogoUriResolver) {
      super();
      this.clientLogoUriResolver = clientLogoUriResolver;
   }

   protected Response createGrant(MultivaluedMap<String, String> params, Client client, String redirectUri, List<String> requestedScope, List<String> approvedScope, UserSubject userSubject, ServerAccessToken preAuthorizedToken) {
      Response response = super.createGrant((OAuthRedirectionState) params, client, requestedScope, approvedScope,
         userSubject, preAuthorizedToken);

      String forwardedServer = OAuthUtil.getForwarderServer();

      if (Strings.isValid(forwardedServer)) {
         URI location = response.getLocation();
         String scheme = location.getScheme();

         URI finalUri = UriBuilder//
            .fromPath(forwardedServer)//
            .scheme(scheme)//
            .path(location.getRawPath())//
            .replaceQuery(location.getRawQuery())//
            .fragment(location.getRawFragment())//
            .buildFromEncoded();

         response = Response.seeOther(finalUri).build();
      }

      return response;
   }

   @Override
   protected Response createErrorResponse(MultivaluedMap<String, String> params, String redirectUri, String error) {
      Response response = super.createErrorResponse(params, redirectUri, error);

      String forwardedServer = OAuthUtil.getForwarderServer();

      if (Strings.isValid(forwardedServer)) {
         URI location = response.getLocation();
         String scheme = location.getScheme();

         URI finalUri = UriBuilder//
            .fromPath(forwardedServer)//
            .scheme(scheme)//
            .path(location.getRawPath())//
            .replaceQuery(location.getRawQuery())//
            .fragment(location.getRawFragment())//
            .buildFromEncoded();

         response = Response.seeOther(finalUri).build();
      }

      return response;
   }

   /**
    * If a client does not include a redirect_uri parameter but has an exactly one pre-registered redirect_uri then use
    * that redirect_uri
    *
    * @param value allows to use a single registered redirect_uri if set to true (default)
    */
   @Override
   public void setUseRegisteredRedirectUriIfPossible(boolean value) {
      this.useRegisteredRedirectUriIfPossible = value;
      super.setUseRegisteredRedirectUriIfPossible(value);
   }

   /**
    * Override fixes OAuthAuthorizationData creation
    */

   protected OAuthAuthorizationData createAuthorizationData(Client client, MultivaluedMap<String, String> params, UserSubject subject, String redirectUri, List<OAuthPermission> perms) {
      OAuthAuthorizationData secData = super.createAuthorizationData(client, params, redirectUri, subject, perms, perms,
         useRegisteredRedirectUriIfPossible);

      String oldReplyTo = secData.getReplyTo();
      URI replyToUri = UriBuilder.fromPath(oldReplyTo).buildFromEncoded();

      String forwardedServer = OAuthUtil.getForwarderServer();

      if (Strings.isValid(forwardedServer)) {
         String scheme = replyToUri.getScheme();

         URI newReplyTo = UriBuilder//
            .fromPath(forwardedServer)//
            .scheme(scheme)//
            .path(replyToUri.getRawPath())//
            .replaceQuery(replyToUri.getRawQuery())//
            .fragment(replyToUri.getRawFragment())//
            .buildFromEncoded();

         secData.setReplyTo(newReplyTo.toString());
      }

      secData.setApplicationName(client.getApplicationName());
      secData.setApplicationCertificates(client.getApplicationCertificates());

      UriInfo uriInfo = getMessageContext().getUriInfo();
      URI clientLogoUri = clientLogoUriResolver.getClientLogoUri(uriInfo, client);

      if (Strings.isValid(forwardedServer)) {
         String scheme = clientLogoUri.getScheme();

         URI newClientLogoUri = UriBuilder//
            .fromPath(forwardedServer)//
            .scheme(scheme)//
            .path(clientLogoUri.getRawPath())//
            .replaceQuery(clientLogoUri.getRawQuery())//
            .fragment(clientLogoUri.getRawFragment())//
            .buildFromEncoded();

         secData.setApplicationLogoUri(newClientLogoUri.toString());
      } else {
         secData.setApplicationLogoUri(clientLogoUri.toString());
      }

      return secData;
   }

   @Override
   protected String validateRedirectUri(Client client, String redirectUri) {
      List<String> uris = client.getRedirectUris();
      if (redirectUri != null) {
         boolean foundMatch = false;
         for (String uriRegex : uris) {
            if (redirectUri.matches(uriRegex)) {
               foundMatch = true;
               break;
            }
         }
         if (!foundMatch) {
            redirectUri = null;
         }
      } else if (uris.size() == 1 && useRegisteredRedirectUriIfPossible) {
         redirectUri = uris.get(0);
      }
      if (redirectUri == null && uris.size() == 0 && !canRedirectUriBeEmpty(client)) {
         reportInvalidRequestError("Client Redirect Uri is invalid");
      }
      return redirectUri;
   }
}