/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.Collection;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Ryan T. Baldwin
 */
@Path("purge")
@Swagger
public interface ScriptPurgeEndpoint {

   /**
    * Purge test results and their TMOs from the database/file system. The number of results that are kept is determined
    * by the Zenith configuration artifact for the given branch.
    *
    * @param branchId - The working branch containing the test results to be purged.
    * @param deleteOnly - If true, test results will be marked as deleted but not purged. If true, results will be
    * deleted and then purged.
    * @return TransactionResult determining success/fail and descriptions of errors if they occur.
    */
   @DELETE
   @Path("results/{branch}")
   @Produces(MediaType.APPLICATION_JSON)
   public TransactionResult purgeTestResults(@PathParam("branch") BranchId branchId,
      @QueryParam("deleteonly") boolean deleteOnly);

   @GET
   @Path("results/deleted/{branch}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<ResultToPurge> getDeletedResults(@PathParam("branch") BranchId branchId);

}