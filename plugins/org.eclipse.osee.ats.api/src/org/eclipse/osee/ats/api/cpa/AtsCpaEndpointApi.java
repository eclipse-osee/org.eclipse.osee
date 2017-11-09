/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.cpa;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Services provided for ATS Cross Program Applicability
 * 
 * @author Donald G. Dunne
 */
@Path("cpa")
public interface AtsCpaEndpointApi {

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() throws Exception;

   @GET
   @Path("program")
   public List<CpaProgram> getPrograms() throws Exception;

   @GET
   @Path("program/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public List<CpaDecision> getDecisionByProgram(@PathParam("id") String id, @QueryParam("open") Boolean open) throws Exception;

   @GET
   @Path("program/{id}/build")
   @Produces(MediaType.APPLICATION_JSON)
   public List<CpaBuild> getBuildsByProgram(@PathParam("id") String programId) throws Exception;

   @GET
   @Path("decision/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getDecision(@PathParam("id") String id, @QueryParam("pcrSystem") String pcrSystem) throws Exception;

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("decision")
   public List<CpaDecision> putDecision(final DecisionUpdate update) throws Exception;

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("duplicate")
   public Response putDuplicate(final DuplicateCpa duplicate) throws Exception;

   @GET
   @Path("config")
   @Produces(MediaType.APPLICATION_JSON)
   public CpaConfig getConfigs() throws Exception;

   @GET
   @Path("config/tool/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getConfig(@PathParam("id") String id) throws Exception;

}
