/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.account.rest.internal;

import java.net.URI;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.account.rest.model.AccountLoginData;
import org.eclipse.osee.account.rest.model.AccountSessionData;

/**
 * @author Roberto E. Escobar
 */
public class AccountLoginResource {

   private final AccountOps accountOps;

   public AccountLoginResource(AccountOps accountOps) {
      this.accountOps = accountOps;
   }

   /**
    * Logs user into account and creates a new account Session
    * 
    * @param data Login data
    * @return account session information and URL to account info
    */
   @POST
   @PermitAll
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response login(@Context HttpServletRequest request, @Context UriInfo uriInfo, AccountLoginData data) {
      RequestInfo remoteInfo = accountOps.asRequestInfo(request);

      AccountSessionData token = accountOps.doLogin(remoteInfo, data);
      String accountId = String.valueOf(token.getAccountId());
      URI location = UriBuilder.fromPath(uriInfo.getBaseUri().toASCIIString()).path(accountId).build();
      return Response.ok().entity(token).contentLocation(location).build();
   }

}
