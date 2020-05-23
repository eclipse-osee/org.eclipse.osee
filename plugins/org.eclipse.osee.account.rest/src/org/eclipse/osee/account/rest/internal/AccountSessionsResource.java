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
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.account.rest.model.AccountSessionDetailsData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;

/**
 * @author Roberto E. Escobar
 */
public class AccountSessionsResource {

   private final AccountOps accountOps;
   private final ArtifactId accountId;

   public AccountSessionsResource(AccountOps accountOps, ArtifactId accountId) {
      this.accountOps = accountOps;
      this.accountId = accountId;
   }

   /**
    * Get all sessions for this account
    *
    * @return account sessions
    */
   @GET
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountSessionDetailsData[] getAccountSessions() {
      return accountOps.getAccountSessionById(accountId).toArray(new AccountSessionDetailsData[] {});
   }

}
