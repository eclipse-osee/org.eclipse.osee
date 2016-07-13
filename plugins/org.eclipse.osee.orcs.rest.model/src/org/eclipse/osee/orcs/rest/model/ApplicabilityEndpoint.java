/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
@Path("applic")
public interface ApplicabilityEndpoint {

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<ApplicabilityToken> getApplicabilityTokens();

   @POST
   @Path("ids")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   Applicabilities getApplicabilities(ArtifactIds artifactIds);

   @POST
   @Path("id")
   @Consumes({MediaType.APPLICATION_JSON})
   void setApplicability(Applicability appl);

   @POST
   @Path("ids")
   @Consumes({MediaType.APPLICATION_JSON})
   void setApplicabilities(Applicabilities applicabilities);

   @POST
   void createDemoApplicability();

   @GET
   @Path("artifact/{artId}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   ApplicabilityToken getApplicabilityToken(@PathParam("artId") ArtifactId artId);

}