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
package org.eclipse.osee.define.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("branch")
public interface DefineBranchEndpointApi {

   @GET
   @Path("{branch}/validate/arttype/{artType}/all")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public XResultData validateAll(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeId artType);

   @GET
   @Path("{branch}/validate/arttype/{artType}/dupparent")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public XResultData getChildrenWithMultipleParents(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeId artType);

   @GET
   @Path("{branch}/validate/arttype/{artType}/orphan")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   XResultData getOrphans(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeId artType);

   @GET
   @Path("{branch}/validate/proc")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   XResultData getProcFuncTrace(@PathParam("branch") BranchId branch);

   @GET
   @Path("conv")
   @Produces(MediaType.TEXT_HTML)
   @Consumes(MediaType.APPLICATION_JSON)
   public String convertSrs();

}
