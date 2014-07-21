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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters;

import javax.ws.rs.core.Response;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OOBAuthorizationResponse;
import org.apache.cxf.rs.security.oauth2.provider.OOBResponseDeliverer;
import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;

/**
 * @author Roberto E. Escobar
 */
public class CxfAuthorizationCodeService extends AuthorizationCodeGrantService {

   private boolean canSupportPublicClients;
   private OOBResponseDeliverer oobDeliverer;

   @Override
   protected Response deliverOOBResponse(OOBAuthorizationResponse response) {
      if (oobDeliverer != null) {
         return oobDeliverer.deliver(response);
      } else {
         return Response.ok(response).build();
      }
   }

   @Override
   protected boolean canSupportPublicClient(Client c) {
      return canSupportPublicClients && !c.isConfidential() && c.getClientSecret() == null;
   }

   @Override
   public void setCanSupportPublicClients(boolean support) {
      this.canSupportPublicClients = support;
   }

   public void setOobDeliverer(OOBResponseDeliverer oobDeliverer) {
      this.oobDeliverer = oobDeliverer;
   }
}