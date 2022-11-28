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
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.data.BranchId;
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
   public List<BranchId> getApplicabilityBranches() {
      return ops.getApplicabilityBranches();
   }

   @Override
   public List<BranchId> getApplicabilityBranchesByType(String branchQueryType) {
      return ops.getApplicabilityBranchesByType(branchQueryType);
   }

   @Override
   public ApplicabilityBranchConfig getConfig(BranchId branch) {
      return ops.getConfig(branch);
   }

   @Override
   public ApplicabilityBranchConfig getConfigWithCompoundApplics(BranchId branch) {
      return ops.getConfigWithCompoundApplics(branch);
   }

}