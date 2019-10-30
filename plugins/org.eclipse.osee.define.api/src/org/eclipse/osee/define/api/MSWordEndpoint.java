/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author David W. Miller
 */
@Path("word")
public interface MSWordEndpoint {
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   @Path("update")
   WordUpdateChange updateWordArtifacts(WordUpdateData data);

   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   @Path("render")
   Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data);

   @GET
   @Path("publishWithNestedTemplates/{branch}/{master}/{slave}/{artifact}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML})
   Response publishWithNestedTemplates(@PathParam("branch") BranchId branch, @PathParam("master") ArtifactId masterTemplate, @PathParam("slave") ArtifactId slaveTemplate, @PathParam("artifact") ArtifactId headArtifact);

   @GET
   @Path("getDocumentNumbers/{branchId}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   String getDocumentNumbers(@PathParam("branchId") BranchId branchId);

}
