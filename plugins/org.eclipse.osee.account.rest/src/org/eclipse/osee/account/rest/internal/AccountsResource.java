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

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.account.admin.UserTokenAccount;
import org.eclipse.osee.account.rest.model.AccountContexts;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Roberto E. Escobar
 */
@Path(AccountContexts.ACCOUNTS)
public class AccountsResource {

   private final AccountOps accountOps;

   public AccountsResource(AccountOps accountOps) {
      this.accountOps = accountOps;
   }

   @GET
   @Path("self")
   @PermitAll
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountInfoData getWhoAmI(@Context OseePrincipal principal) {
      AccountInfoData toReturn = null;
      if (principal != null) {
         toReturn = AccountDataUtil.asAccountInfoData(principal);
      } else {
         toReturn = AccountDataUtil.asAccountData(UserTokenAccount.Anonymous);
      }
      return toReturn;
   }

   @GET
   @Path("preferences/{id}")
   @PermitAll
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response getPreferences(@PathParam("id") Long id) {
      ArtifactId artifactId = ArtifactId.valueOf(id);
      return Response.ok().entity(accountOps.getAccountWebPreferencesData(artifactId)).build();
   }

   @PUT
   @Path("preferences/{id}")
   @PermitAll
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response editPreferences(@PathParam("id") String userUuid, @QueryParam("key") String key, @QueryParam("itemId") String itemId, String newValue) {
      ArtifactId artifactId = ArtifactId.valueOf(userUuid);
      return Response.ok().entity(accountOps.editAccountWebPreferencesData(artifactId, key, itemId, newValue)).build();
   }

   /**
    * Get all Accounts
    *
    * @return All accounts
    */
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountInfoData[] getAccounts() {
      return accountOps.getAllAccounts().toArray(new AccountInfoData[] {});
   }

   /**
    * Creates a new Account - all fields must be unique
    *
    * @param accountInput Account data
    * @return new account info data
    */
   @POST
   @PermitAll
   @Path(AccountContexts.ACCOUNT_ID_TEMPLATE)
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountInfoData createAccount(@PathParam(AccountContexts.ACCOUNT_ID_PARAM) String username, AccountInput accountInput) {
      return accountOps.createAccount(username, accountInput);
   }

   @Path(AccountContexts.ACCOUNT_ID_TEMPLATE)
   public AccountResource getAccount(@PathParam(AccountContexts.ACCOUNT_ID_PARAM) Long accountId) {
      ArtifactId artifactId = ArtifactId.valueOf(accountId);
      return new AccountResource(accountOps, artifactId);
   }
}
