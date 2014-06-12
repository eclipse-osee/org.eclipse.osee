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

import java.net.URI;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.account.admin.SystemRoles;
import org.eclipse.osee.account.rest.model.AccountContexts;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;

/**
 * @author Roberto E. Escobar
 */
public class AccountResource {

   private final AccountOps accountOps;
   private final String accountId;

   public AccountResource(AccountOps accountOps, String accountId) {
      this.accountOps = accountOps;
      this.accountId = accountId;
   }

   /**
    * Creates a new Account - all fields must be unique
    * 
    * @param accountInput Account data
    * @return new account info data
    */
   @POST
   @PermitAll
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountInfoData createAccount(AccountInput accountInput) {
      return accountOps.createAccount(accountId, accountInput);
   }

   /**
    * Deletes the account
    * 
    * @return response
    * @response.representation.200.doc account status set to active
    * @response.representation.304.doc account active status not modified
    */
   @DELETE
   @RolesAllowed(SystemRoles.ROLES_ADMINISTRATOR)
   public Response deleteAccount() {
      ResponseBuilder builder;
      boolean modified = accountOps.deleteAccount(accountId);
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }

   /**
    * Get account details
    * 
    * @return account details
    */
   @GET
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountDetailsData getAccountDetailsData() {
      return accountOps.getAccountDetailsData(accountId);
   }

   /**
    * Get All account subscriptions
    * 
    * @return accountSubscriptions
    */
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Path("subscriptions")
   @GET
   public Response getSubscriptions(@Context UriInfo uriInfo) {
      URI requestUri = uriInfo.getRequestUri();
      URI uri =
         UriBuilder.fromUri(requestUri).path("../../../").path("subscriptions").path("for-account").path("{account-id}").build(
            accountId);
      return Response.seeOther(uri).build();
   }

   @Path(AccountContexts.ACCOUNT_PREFERENCES)
   public AccountPreferencesResource getAccountSettingsData() {
      return new AccountPreferencesResource(accountOps, accountId);
   }

   @Path(AccountContexts.ACCOUNT_ACTIVE)
   public AccountActiveResource active() {
      return new AccountActiveResource(accountOps, accountId);
   }

   @Path(AccountContexts.ACCOUNT_SESSSIONS)
   public AccountSessionsResource sessions() {
      return new AccountSessionsResource(accountOps, accountId);
   }

}