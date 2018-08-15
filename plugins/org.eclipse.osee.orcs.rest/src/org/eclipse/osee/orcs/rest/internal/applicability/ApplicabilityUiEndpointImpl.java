/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.applicability;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.rest.model.ApplicabilityUiEndpoint;

/**
 * @author Donald G. Dunne
 */
@Path("applicui")
public class ApplicabilityUiEndpointImpl implements ApplicabilityUiEndpoint {

   private final OrcsApplicability ops;

   public ApplicabilityUiEndpointImpl(OrcsApi orcsApi) {
      ops = orcsApi.getApplicabilityOps();
   }

   @Override
   @GET
   @Produces({MediaType.TEXT_HTML})
   public Response get() {
      URI uri;
      try {
         uri = new URI("/applicui/config/main.html");
         return Response.seeOther(uri).build();
      } catch (URISyntaxException ex) {
         throw new OseeCoreException("Exception ", ex);
      }
   }

   @Override
   @GET
   @Path("branches")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public List<BranchViewToken> getApplicabilityBranches() {
      return ops.getApplicabilityBranches();
   }

   @Override
   @GET
   @Path("branch/{branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public ApplicabilityBranchConfig getConfig(@PathParam("branch") BranchId branch) {
      return ops.getConfig(branch);
   }

}