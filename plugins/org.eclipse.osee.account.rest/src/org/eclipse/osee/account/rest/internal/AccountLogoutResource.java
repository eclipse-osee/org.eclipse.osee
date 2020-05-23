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
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;

/**
 * @author Roberto E. Escobar
 */
public class AccountLogoutResource {

   private final AccountOps accountOps;

   public AccountLogoutResource(AccountOps accountOps) {
      this.accountOps = accountOps;
   }

   /**
    * Logs user out of the system
    * 
    * @return response
    * @response.representation.200.doc successfully logged out
    * @response.representation.304.doc session not modified
    */
   @POST
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response logout(AccountSessionData data) {
      ResponseBuilder builder;
      boolean modified = accountOps.doLogout(data.getToken());
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }

}
