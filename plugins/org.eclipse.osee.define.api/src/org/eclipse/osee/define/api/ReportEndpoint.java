/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.define.api;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author David W. Miller
 */
@Path("report")
public interface ReportEndpoint {

   @GET
   @Path("{branch}/view/{view}/template/{template}")
   @Produces({MediaType.APPLICATION_XML})
   Response getReportFromTemplate(@PathParam("branch") BranchId branch, @DefaultValue("-1") @PathParam("view") ArtifactId view, @DefaultValue("-1") @PathParam("templateArt") ArtifactId templateArt);

}
