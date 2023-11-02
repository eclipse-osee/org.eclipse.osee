/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Stephen J. Molaro
 */
@Path("tmo")
@Swagger
public interface TmoEndpoint {

   @Path("{branch}/set")
   @Produces(MediaType.APPLICATION_JSON)
   public ScriptSetEndpoint getScriptSetTypes(@PathParam("branch") BranchId branch);

   @Path("{branch}/batch")
   @Produces(MediaType.APPLICATION_JSON)
   public ScriptBatchEndpoint getScriptBatchEndpoint(@PathParam("branch") BranchId branch);

   @Path("{branch}/def")
   @Produces(MediaType.APPLICATION_JSON)
   public ScriptDefEndpoint getScriptDefinitionTypes(@PathParam("branch") BranchId branch);

   @Path("{branch}/result")
   @Produces(MediaType.APPLICATION_JSON)
   public ScriptResultEndpoint getScriptResultTypes(@PathParam("branch") BranchId branch);

   @Path("{branch}/case")
   @Produces(MediaType.APPLICATION_JSON)
   public TestCaseEndpoint getTestCaseTypes(@PathParam("branch") BranchId branch);

   @Path("{branch}/point")
   @Produces(MediaType.APPLICATION_JSON)
   public TestPointEndpoint getTestPointTypes(@PathParam("branch") BranchId branch);

   @Path("{branch}/import")
   @Produces(MediaType.APPLICATION_JSON)
   public ScriptImportEndpoint getTmoImportEndpoint(@PathParam("branch") BranchId branch);

   @Path("{branch}/download")
   @Produces(MediaType.APPLICATION_JSON)
   public ScriptDownloadEndpoint getTmoDownloadEndpoint(@PathParam("branch") BranchId branch);

}