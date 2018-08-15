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
package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;

/**
 * @author Donald G. Dunne
 */
@Path("applicui")
public interface ApplicabilityUiEndpoint {

   @GET
   @Produces({MediaType.TEXT_HTML})
   Response get(@Context UriInfo uriInfo);

   /**
    * @return Un-archived baseline and working branches available for Product Line configuration
    */
   @GET
   @Path("branches")
   @Produces({MediaType.APPLICATION_JSON})
   List<BranchViewToken> getApplicabilityBranches();

   @GET
   @Path("branch/{branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public ApplicabilityBranchConfig getConfig(@PathParam("branch") BranchId branch);

}