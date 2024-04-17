/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.coverage.internal;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.coverage.CoverageApi;
import org.eclipse.osee.coverage.CoverageEndpoint;
import org.eclipse.osee.coverage.CoverageProgramEndpoint;
import org.eclipse.osee.coverage.PartitionChartDataEndpoint;
import org.eclipse.osee.coverage.PartitionDefEndpoint;
import org.eclipse.osee.coverage.PartitionResultEndpoint;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Stephen J. Molaro
 */

@Path("branch")
@Swagger
public class CoverageEndpointImpl implements CoverageEndpoint {
   private final CoverageApi coverageApi;

   public CoverageEndpointImpl(CoverageApi coverageApi) {
      this.coverageApi = coverageApi;
   }

   @Override
   @Path("{branch}/program")
   @Produces(MediaType.APPLICATION_JSON)
   public CoverageProgramEndpoint getCoverageProgram(@PathParam("branch") BranchId branch) {
      return new CoverageProgramEndpointImpl(branch, coverageApi.getCoverageProgramApi());
   }

   @Override
   @Path("{branch}/def")
   @Produces(MediaType.APPLICATION_JSON)
   public PartitionDefEndpoint getPartitionDef(@PathParam("branch") BranchId branch) {
      return new PartitionDefEndpointImpl(branch, coverageApi.getPartitionDefApi());
   }

   @Override
   @Path("{branch}/result")
   @Produces(MediaType.APPLICATION_JSON)
   public PartitionResultEndpoint getPartitionResult(@PathParam("branch") BranchId branch) {
      return new PartitionResultEndpointImpl(branch, coverageApi.getPartitionResultApi());
   }

   @Override
   @Path("{branch}/data")
   @Produces(MediaType.APPLICATION_JSON)
   public PartitionChartDataEndpoint getPartitionChartData(@PathParam("branch") BranchId branch) {
      return new PartitionChartDataEndpointImpl(branch, coverageApi.getPartitionChartDataApi());
   }

   @Override
   @Path("{branch}/item")
   @Produces(MediaType.APPLICATION_JSON)
   public CoverageItemEndpointImpl getCoverageItem(@PathParam("branch") BranchId branch) {
      return new CoverageItemEndpointImpl(branch, coverageApi.getCoverageItemApi());
   }

   @Override
   @Path("{branch}/import")
   @Produces(MediaType.APPLICATION_JSON)
   public CoverageImportEndpointImpl getCoverageImport(@PathParam("branch") BranchId branch) {
      return new CoverageImportEndpointImpl(branch, coverageApi.getCoverageImportApi());
   }

}