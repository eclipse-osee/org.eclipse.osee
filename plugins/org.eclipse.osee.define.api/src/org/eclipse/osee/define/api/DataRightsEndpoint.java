/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;

/**
 * @author Ryan D. Brooks
 */
@Path("datarights")
public interface DataRightsEndpoint {

   /*
    * This will override the artifacts sent in with the specified Data Rights classification If classification is null
    * or invalid, it will default to the Data Rights specified on the artifact(s)
    */
   @POST
   @Path("artifacts/branch/{branch}/classification/{classification}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public DataRightResult getDataRights(@PathParam("branch") BranchId branch, @PathParam("classification") @DefaultValue("invalid") String overrideClassification, List<ArtifactId> artifacts);

   @POST
   @Path("artifacts/branch/{branch}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public DataRightResult getDataRights(@PathParam("branch") BranchId branch, List<ArtifactId> artifacts);

}
