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
package org.eclipse.osee.ats.rest.internal.cpa;

import java.net.URI;
import java.util.ArrayList;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.cpa.AtsCpaEndpointApi;
import org.eclipse.osee.ats.api.cpa.CpaBuild;
import org.eclipse.osee.ats.api.cpa.CpaConfig;
import org.eclipse.osee.ats.api.cpa.CpaConfigTool;
import org.eclipse.osee.ats.api.cpa.CpaDecision;
import org.eclipse.osee.ats.api.cpa.CpaProgram;
import org.eclipse.osee.ats.api.cpa.DecisionUpdate;
import org.eclipse.osee.ats.api.cpa.DuplicateCpa;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.EnumEntry;

/**
 * Services provided for ATS Cross Program Applicability
 *
 * @author Donald G. Dunne
 */
public final class CpaResource implements AtsCpaEndpointApi {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private final CpaServiceRegistry cpaRegistry;

   public CpaResource(OrcsApi orcsApi, AtsApi atsApi, CpaServiceRegistry cpaRegistry) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.cpaRegistry = cpaRegistry;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   @Override
   public String get() throws Exception {
      return AHTML.simplePage("ATS CPA Resource");
   }

   @GET
   @Path("program")
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public List<CpaProgram> getPrograms() throws Exception {
      List<CpaProgram> programs = new ArrayList<>();
      for (IAtsCpaService service : cpaRegistry.getServices()) {
         programs.addAll(service.getPrograms());
      }
      return programs;
   }

   @GET
   @Path("program/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public List<CpaDecision> getDecisionByProgram(@PathParam("id") String id, @QueryParam("open") Boolean open) throws Exception {
      return new DecisionLoader(cpaRegistry, atsApi, orcsApi).andOpen(open).andProgramId(id).load();
   }

   @GET
   @Path("program/{id}/build")
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public List<CpaBuild> getBuildsByProgram(@PathParam("id") String programId) throws Exception {
      List<CpaBuild> builds = new ArrayList<>();
      for (IAtsCpaService service : cpaRegistry.getServices()) {
         builds.addAll(service.getBuilds(programId));
      }
      return builds;
   }

   @GET
   @Path("decision/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public Response getDecision(@PathParam("id") String id, @QueryParam("pcrSystem") String pcrSystem) throws Exception {
      URI uri = null;
      if (pcrSystem == null) {
         String actionUrl = AtsUtil.getActionUrl(id, atsApi);
         uri = UriBuilder.fromUri(actionUrl).build();
      } else {
         IAtsCpaService service = cpaRegistry.getServiceById(pcrSystem);
         uri = service.getLocation(UriBuilder.fromUri(CpaUtil.getCpaBasePath(atsApi)).build(), id);
      }
      return Response.seeOther(uri).build();
   }

   /**
    * { "ids": ["id1","id2"], "assignees": ["757","457"], "applicability": "Yes", "rationale": "Cause" }
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("decision")
   @Override
   public List<CpaDecision> putDecision(final DecisionUpdate update) throws Exception {
      new DecisionUpdater(update, atsApi).update();
      return new DecisionLoader(cpaRegistry, atsApi, orcsApi).andCpaIds(update.getIds()).load();
   }

   /**
    * { "programId": "3472723", "buildId": "363445", "ids": [ "CPA41337" ], "userId": "727536" }
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("duplicate")
   @Override
   public Response putDuplicate(final DuplicateCpa duplicate) throws Exception {
      XResultData rd = new CpaDuplicator(duplicate, atsApi, cpaRegistry).duplicate();
      if (rd.isErrors()) {
         return Response.status(Status.NOT_ACCEPTABLE).entity(rd.toString()).build();
      }
      CpaDecision decision = new DecisionLoader(cpaRegistry, atsApi, orcsApi).andCpaIds(
         java.util.Collections.singleton(duplicate.getCpaId())).load().iterator().next();
      return Response.ok().entity(decision).build();
   }

   @GET
   @Path("config")
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public CpaConfig getConfigs() throws Exception {
      CpaConfig config = new CpaConfig();
      for (EnumEntry entry : orcsApi.getOrcsTypes().getAttributeTypes().getEnumType(
         AtsAttributeTypes.ApplicableToProgram).values()) {
         config.getApplicabilityOptions().add(entry.getName());
      }
      for (IAtsCpaService service : cpaRegistry.getServices()) {
         config.getTools().add(new CpaConfigTool(service.getId()));
      }
      return config;
   }

   @GET
   @Path("config/tool/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public String getConfig(@PathParam("id") String id) throws Exception {
      for (IAtsCpaService service : cpaRegistry.getServices()) {
         if (service.getId().equals(id)) {
            return service.getConfigJson();
         }
      }
      throw new OseeWebApplicationException(Status.BAD_REQUEST, String.format("Unknown CPA configuration [%s]", id));
   }
}
