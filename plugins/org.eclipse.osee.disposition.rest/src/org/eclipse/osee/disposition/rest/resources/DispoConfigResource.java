/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.disposition.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Angel Avila
 */
public class DispoConfigResource {

   private final DispoApi dispoApi;
   private final BranchId branch;

   public DispoConfigResource(DispoApi dispoApi, BranchId branch) {
      this.dispoApi = dispoApi;
      this.branch = branch;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getDispoConfig() {
      Response.Status status;
      Response response;
      DispoConfig config = dispoApi.getDispoConfig(branch);

      if (config == null) {
         status = Status.NOT_FOUND;
         response = Response.status(status).build();
      } else {
         status = Status.OK;
         response = Response.status(status).entity(config).build();
      }

      return response;
   }
}
