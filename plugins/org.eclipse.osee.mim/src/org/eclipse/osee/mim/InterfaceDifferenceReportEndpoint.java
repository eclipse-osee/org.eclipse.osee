/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.MimDifferenceReport;

/**
 * @author Ryan T. Baldwin
 */
@Path("diff")
public interface InterfaceDifferenceReportEndpoint {

   @GET()
   @Path("{branchId}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets all differences between the branches
    *
    * @return all differences between branches
    */
   MimDifferenceReport getDifferenceReport(@PathParam("branchId") BranchId branch2);

}
