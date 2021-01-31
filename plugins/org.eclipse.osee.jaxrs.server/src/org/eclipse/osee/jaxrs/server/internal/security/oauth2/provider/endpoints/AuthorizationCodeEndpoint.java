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

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.OOBAuthorizationResponse;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OOBResponseDeliverer;
import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.ClientLogoUriResolver;

/**
 * @author Roberto E. Escobar
 */
public class AuthorizationCodeEndpoint extends AuthorizationCodeGrantService {

   private boolean canSupportPublicClients;
   private OOBResponseDeliverer oobDeliverer;

   private final ClientLogoUriResolver clientLogoUriResolver;

   public AuthorizationCodeEndpoint(ClientLogoUriResolver clientLogoUriResolver) {
      super();
      this.clientLogoUriResolver = clientLogoUriResolver;
   }

   @Override
   protected Response deliverOOBResponse(OOBAuthorizationResponse response) {
      if (oobDeliverer != null) {
         return oobDeliverer.deliver(response);
      } else {
         return Response.ok(response).build();
      }
   }

   /**
    * <pre>
    * Extra security features:
    *  - only confidential clients should have a client secret
    *  - if they are not confidential they should not have a client secret.
    * 
    *  If desired, add the following:
    * && !c.isConfidential() && c.getClientSecret() == null
    * </pre>
    */
   @Override
   protected boolean canSupportPublicClient(Client c) {
      return canSupportPublicClients;
   }

   @Override
   public void setCanSupportPublicClients(boolean support) {
      this.canSupportPublicClients = support;
   }

   public void setOobDeliverer(OOBResponseDeliverer oobDeliverer) {
      this.oobDeliverer = oobDeliverer;
   }

   /**
    * Override fixes OAuthAuthorizationData creation
    */
   @Override
   protected OAuthAuthorizationData createAuthorizationData(Client client, MultivaluedMap<String, String> params, UserSubject subject, String redirectUri, List<OAuthPermission> perms) {
      OAuthAuthorizationData secData = super.createAuthorizationData(client, params, subject, redirectUri, perms);
      secData.setApplicationName(client.getApplicationName());
      secData.setApplicationCertificates(client.getApplicationCertificates());

      UriInfo uriInfo = getMessageContext().getUriInfo();
      URI clientLogoUri = clientLogoUriResolver.getClientLogoUri(uriInfo, client);
      String applicationLogoUri = clientLogoUri.toASCIIString();

      secData.setApplicationLogoUri(applicationLogoUri);

      return secData;
   }
}