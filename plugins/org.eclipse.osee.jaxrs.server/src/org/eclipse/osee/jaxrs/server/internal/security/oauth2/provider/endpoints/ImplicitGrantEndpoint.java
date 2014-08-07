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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.services.ImplicitGrantService;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.ClientLogoUriResolver;

/**
 * @author Roberto E. Escobar
 */
public class ImplicitGrantEndpoint extends ImplicitGrantService {

   private final ClientLogoUriResolver clientLogoUriResolver;

   public ImplicitGrantEndpoint(ClientLogoUriResolver clientLogoUriResolver) {
      super();
      this.clientLogoUriResolver = clientLogoUriResolver;
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