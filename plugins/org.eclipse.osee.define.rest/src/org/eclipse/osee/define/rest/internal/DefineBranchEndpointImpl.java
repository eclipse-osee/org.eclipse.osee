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
package org.eclipse.osee.define.rest.internal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.define.api.DefineBranchEndpointApi;
import org.eclipse.osee.define.rest.operations.ValidateBranchOperation;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("branch")
public final class DefineBranchEndpointImpl implements DefineBranchEndpointApi {
   private final JdbcClient jdbcClient;
   private final OrcsApi orcsApi;

   public DefineBranchEndpointImpl(JdbcClient jdbcClient, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.orcsApi = orcsApi;
   }

   @Override
   @GET
   @Path("{branch}/validate/arttype/{artType}/all")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public XResultData validateAll(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeId artType) {
      ValidateBranchOperation op = new ValidateBranchOperation(jdbcClient, branch, orcsApi);
      return op.runAll(artType);
   }

   @Override
   @GET
   @Path("{branch}/validate/arttype/{artType}/dupparent")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public XResultData getChildrenWithMultipleParents(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeId artType) {
      ValidateBranchOperation op = new ValidateBranchOperation(jdbcClient, branch, orcsApi);
      return op.getChildrenWithMultipleParents(artType);
   }

   @Override
   @GET
   @Path("{branch}/validate/arttype/{artType}/orphan")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public XResultData getOrphans(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeId artType) {
      ValidateBranchOperation op = new ValidateBranchOperation(jdbcClient, branch, orcsApi);
      return op.getOrphans(artType);
   }
}
