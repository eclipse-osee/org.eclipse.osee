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

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.account.rest.model.AccountPreferencesInput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;

/**
 * @author Roberto E. Escobar
 */
public class AccountPreferencesResource {

   private final AccountOps accountOps;
   private final ArtifactId accountId;

   public AccountPreferencesResource(AccountOps accountOps, ArtifactId accountId) {
      this.accountOps = accountOps;
      this.accountId = accountId;
   }

   /**
    * Get account preferences
    *
    * @return account preferences
    */
   @GET
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountPreferencesData getAccountPreferences() {
      return accountOps.getAccountPreferencesDataById(accountId);
   }

   /**
    * Sets all preferences to match the incoming preferences.
    *
    * @return response
    * @response.representation.200.doc successfully logged out
    * @response.representation.304.doc session not modified
    */
   @PUT
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response setAccountPreferences(AccountPreferencesInput input) {
      ResponseBuilder builder;
      boolean modified = accountOps.setAccountPreferences(accountId, input);
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }
}
