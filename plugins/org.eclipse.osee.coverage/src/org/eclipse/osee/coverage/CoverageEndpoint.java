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

package org.eclipse.osee.coverage;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Stephen J. Molaro
 */
@Path("branch")
@Swagger
public interface CoverageEndpoint {

   @Path("{branch}/program")
   @Produces(MediaType.APPLICATION_JSON)
   public CoverageProgramEndpoint getCoverageProgram(@PathParam("branch") BranchId branch);

   @Path("{branch}/def")
   @Produces(MediaType.APPLICATION_JSON)
   public PartitionDefEndpoint getPartitionDef(@PathParam("branch") BranchId branch);

   @Path("{branch}/result")
   @Produces(MediaType.APPLICATION_JSON)
   public PartitionResultEndpoint getPartitionResult(@PathParam("branch") BranchId branch);

   @Path("{branch}/data")
   @Produces(MediaType.APPLICATION_JSON)
   public PartitionChartDataEndpoint getPartitionChartData(@PathParam("branch") BranchId branch);

   @Path("{branch}/item")
   @Produces(MediaType.APPLICATION_JSON)
   public CoverageItemEndpoint getCoverageItem(@PathParam("branch") BranchId branch);

   @Path("{branch}/import")
   @Produces(MediaType.APPLICATION_JSON)
   public CoverageImportEndpoint getCoverageImport(@PathParam("branch") BranchId branch);

}