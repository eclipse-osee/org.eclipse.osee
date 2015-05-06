/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.rest.DispoApi;

/**
 * @author Angel Avila
 */
public class DispoConfigResource {

   private final DispoApi dispoApi;
   private final DispoProgram program;

   public DispoConfigResource(DispoApi dispoApi, DispoProgram program) {
      this.dispoApi = dispoApi;
      this.program = program;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getDispoConfig() {
      Response.Status status;
      Response response;
      DispoConfig config = dispoApi.getDispoConfig(program);

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
