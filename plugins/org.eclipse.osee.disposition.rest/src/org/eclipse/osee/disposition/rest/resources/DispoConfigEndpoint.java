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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Angel Avila
 */
@Swagger
public class DispoConfigEndpoint {

   private final DispoApi dispoApi;
   private final BranchId branch;

   public DispoConfigEndpoint(DispoApi dispoApi, BranchId branch) {
      this.dispoApi = dispoApi;
      this.branch = branch;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get Dispo config")
   @Tag(name = "config")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
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
