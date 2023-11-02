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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Ryan T. Baldwin
 */
@Path("download")
@Swagger
public interface ScriptDownloadEndpoint {

   @GET()
   @Path("{resultId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_XML)
   Response downloadTmo(@PathParam("resultId") ArtifactId resultId);

   @GET()
   @Path("batch/{batchId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces("application/zip")
   Response downloadBatch(@PathParam("batchId") ArtifactId batchId);

}
