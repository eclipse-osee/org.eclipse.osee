/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.orcs.rest.internal.applicability;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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

   @Context
   private UriInfo uriInfo;
   private final OrcsApplicability ops;

   public ApplicabilityUiEndpointImpl(OrcsApi orcsApi) {
      ops = orcsApi.getApplicabilityOps();
   }

   @Override
   @GET
   @Produces({MediaType.TEXT_HTML})
   public Response get() {
      try {
         String basePath = uriInfo.getAbsolutePath().toString();
         String url = basePath + "/config/plconfig.html";
         URI uri = new URI(url);
         return Response.seeOther(uri).build();
      } catch (URISyntaxException ex) {
         throw new OseeCoreException("Exception ", ex);
      }
   }

   @Override
   @GET
   @Produces({MediaType.TEXT_HTML})
   public Response getRO() {
      try {
         String basePath = uriInfo.getAbsolutePath().toString();
         String url = basePath + "/config/plconfigro.html";
         URI uri = new URI(url);
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
   @Path("branches/{branchQueryType}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public List<BranchViewToken> getApplicabilityBranchesByType(@PathParam("branchQueryType") String branchQueryType) {
      return ops.getApplicabilityBranchesByType(branchQueryType);
   }

   @Override
   @GET
   @Path("branch/{branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public ApplicabilityBranchConfig getConfig(@PathParam("branch") BranchId branch, @QueryParam("showAll") @DefaultValue("false") Boolean showAll) {
      return ops.getConfig(branch, showAll);
   }

   @Override
   @GET
   @Path("branch/convert/{branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.TEXT_HTML})
   public String convertConfigToArtifact(@PathParam("branch") BranchId branch) {
      return ops.convertConfigToArtifact(branch);
   }

}