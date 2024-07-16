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

package org.eclipse.osee.define.rest.internal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.define.api.DefineBranchEndpointApi;
import org.eclipse.osee.define.rest.operations.ValidateBranchOperation;
import org.eclipse.osee.define.rest.operations.ValidateProcFuncCalls;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
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
   public XResultData validateAll(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeToken artType) {
      ValidateBranchOperation op = new ValidateBranchOperation(jdbcClient, branch, orcsApi);
      return op.runAll(artType);
   }

   @Override
   @GET
   @Path("{branch}/validate/arttype/{artType}/dupparent")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public XResultData getChildrenWithMultipleParents(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeToken artType) {
      ValidateBranchOperation op = new ValidateBranchOperation(jdbcClient, branch, orcsApi);
      return op.getChildrenWithMultipleParents(artType);
   }

   @Override
   @GET
   @Path("{branch}/validate/arttype/{artType}/orphan")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public XResultData getOrphans(@PathParam("branch") BranchId branch, @PathParam("artType") ArtifactTypeToken artType) {
      ValidateBranchOperation op = new ValidateBranchOperation(jdbcClient, branch, orcsApi);
      return op.getOrphans(artType);
   }

   @Override
   @GET
   @Path("{branch}/validate/proc")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public XResultData getProcFuncTrace(@PathParam("branch") BranchId branch) {
      ValidateProcFuncCalls op = new ValidateProcFuncCalls(jdbcClient, branch, orcsApi);
      return op.get();
   }

   @Override
   @GET
   @Path("conv")
   @Produces(MediaType.TEXT_HTML)
   @Consumes(MediaType.APPLICATION_JSON)
   public String convertSrs() {
      ValidateProcFuncCalls op = new ValidateProcFuncCalls(jdbcClient, null, orcsApi);
      op.searchAndReplace();
      return AHTML.simplePage("Done");
   }
}