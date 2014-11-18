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
import org.eclipse.osee.ats.api.cpa.IAtsCpaBuild;
import org.eclipse.osee.ats.api.cpa.IAtsCpaDecision;
import org.eclipse.osee.ats.api.cpa.IAtsCpaProgram;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.cpa.CpaConfig;
import org.eclipse.osee.ats.core.cpa.CpaConfigTool;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.EnumEntry;

/**
 * Services provided for ATS Cross Program Applicability
 * 
 * @author Donald G. Dunne
 */
@Path("cpa")
public final class CpaResource {

   private final OrcsApi orcsApi;
   private final IAtsServer atsServer;
   private final CpaServiceRegistry cpaRegistry;

   public CpaResource(OrcsApi orcsApi, IAtsServer atsServer, CpaServiceRegistry cpaRegistry) {
      this.orcsApi = orcsApi;
      this.atsServer = atsServer;
      this.cpaRegistry = cpaRegistry;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() throws Exception {
      return AHTML.simplePage("ATS CPA Resource");
   }

   @GET
   @Path("program")
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsCpaProgram> getPrograms() throws Exception {
      List<IAtsCpaProgram> programs = new ArrayList<IAtsCpaProgram>();
      for (IAtsCpaService service : cpaRegistry.getServices()) {
         programs.addAll(service.getPrograms());
      }
      return programs;
   }

   @GET
   @Path("program/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsCpaDecision> getDecisionByProgram(@PathParam("uuid") String uuid, @QueryParam("open") Boolean open) throws Exception {
      return new DecisionProgramLoader(uuid, open, cpaRegistry, atsServer).load();
   }

   @GET
   @Path("program/{uuid}/build")
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsCpaBuild> getBuildsByProgram(@PathParam("uuid") String programUuid) throws Exception {
      List<IAtsCpaBuild> builds = new ArrayList<IAtsCpaBuild>();
      for (IAtsCpaProgram program : getPrograms()) {
         for (IAtsCpaService service : cpaRegistry.getServices()) {
            builds.addAll(service.getBuilds(programUuid));
         }
      }
      return builds;
   }

   @GET
   @Path("decision/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getDecision(@PathParam("uuid") String uuid, @QueryParam("pcrSystem") String pcrSystem) throws Exception {
      URI uri = null;
      if (pcrSystem == null) {
         String actionUrl = atsServer.getConfigValue("ActionUrl");
         actionUrl = actionUrl.replaceFirst("UUID", uuid);
         uri = UriBuilder.fromUri(actionUrl).build();
      } else {
         IAtsCpaService service = cpaRegistry.getServiceById(pcrSystem);
         uri = service.getLocation(UriBuilder.fromUri(CpaUtil.getCpaBasePath(atsServer)).build(), uuid);
      }
      return Response.seeOther(uri).build();
   }

   /**
    * { "uuids": ["id1","id2"], "assignees": ["757","457"], "applicability": "Yes", "rationale": "Cause" }
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("decision")
   public Response putDecision(final DecisionUpdate update) throws Exception {
      return new DecisionUpdater(update, atsServer).update();
   }

   @GET
   @Path("config")
   @Produces(MediaType.APPLICATION_JSON)
   public CpaConfig getConfigs() throws Exception {
      CpaConfig config = new CpaConfig();
      for (EnumEntry entry : orcsApi.getOrcsTypes(null).getAttributeTypes().getEnumType(
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
   public String getConfig(@PathParam("id") String id) throws Exception {
      for (IAtsCpaService service : cpaRegistry.getServices()) {
         if (service.getId().equals(id)) {
            return service.getConfigJson();
         }
      }
      throw new OseeWebApplicationException(Status.BAD_REQUEST, String.format("Unknown CPA configuration [%s]", id));
   }
}
