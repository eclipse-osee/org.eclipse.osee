/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.rest.internal;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.eclipse.osee.account.admin.SystemRoles;
import org.eclipse.osee.account.rest.model.AccountActiveData;

/**
 * @author Roberto E. Escobar
 */
public class AccountActiveResource {

   private final AccountOps accountOps;
   private final String accountId;

   public AccountActiveResource(AccountOps accountOps, String accountId) {
      this.accountOps = accountOps;
      this.accountId = accountId;
   }

   /**
    * Get account active status
    * 
    * @return account active information
    */
   @GET
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountActiveData isActive() {
      return accountOps.isActive(accountId);
   }

   /**
    * Set account status to active
    * 
    * @return response
    * @response.representation.200.doc account status set to active
    * @response.representation.304.doc account active status not modified
    */
   @PUT
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   public Response setActive() {
      ResponseBuilder builder;
      boolean modified = accountOps.setAccountActive(accountId, true);
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }

   /**
    * Set account status to inactive
    * 
    * @return response
    * @response.representation.200.doc account status set to inactive
    * @response.representation.304.doc account status not modified
    */
   @DELETE
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   public Response setInactive() {
      ResponseBuilder builder;
      boolean modified = accountOps.setAccountActive(accountId, false);
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }

}
