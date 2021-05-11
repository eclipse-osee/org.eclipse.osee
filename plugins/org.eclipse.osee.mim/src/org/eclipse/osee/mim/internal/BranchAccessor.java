/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.internal;

import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_ACCOUNT_ID;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.PlatformTypesEndpoint;
import org.eclipse.osee.mim.PlatformTypesFilterEndpoint;

/**
 * @author Luciano T. Vaglienti
 */
@Path("branch")
public class BranchAccessor {
   private final MimApi mimApi;

   public BranchAccessor(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   @Path("{branch}/types")
   @Produces(MediaType.APPLICATION_JSON)
   public PlatformTypesEndpoint getPlatformTypes(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new PlatformTypesEndpointImpl(mimApi.getOrcsApi(), branch, accountId);
   }

   @Path("{branch}/types/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public PlatformTypesFilterEndpoint getPlatformTypesFilter(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new PlatformTypesFilterEndpointImpl(mimApi.getOrcsApi(), branch, accountId);
   }
}
