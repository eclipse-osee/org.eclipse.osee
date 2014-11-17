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
import java.util.Map.Entry;
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
import org.eclipse.osee.ats.api.cpa.IAtsCpaDecision;
import org.eclipse.osee.ats.api.cpa.IAtsCpaProgram;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.cpa.ICpaPcr;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.cpa.CpaConfig;
import org.eclipse.osee.ats.core.cpa.CpaConfigTool;
import org.eclipse.osee.ats.core.cpa.CpaDecision;
import org.eclipse.osee.ats.core.cpa.CpaFactory;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.EnumEntry;
import org.eclipse.osee.orcs.search.QueryBuilder;

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
   private String cpaBasepath;

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
      List<IAtsCpaDecision> decisions = new ArrayList<IAtsCpaDecision>();
      QueryBuilder queryBuilder =
         atsServer.getQuery().andTypeEquals(AtsArtifactTypes.TeamWorkflow).and(AtsAttributeTypes.ApplicabilityWorkflow,
            "true").and(AtsAttributeTypes.ProgramUuid, uuid);
      if (open != null) {
         queryBuilder.and(AtsAttributeTypes.CurrentStateType,
            (open ? StateType.Working.name() : StateType.Completed.name()));
      }
      HashCollection<String, IAtsCpaDecision> origPcrIdToDecision = new HashCollection<String, IAtsCpaDecision>();
      String pcrToolId = null;
      ElapsedTime time = new ElapsedTime("load cpa workflows");
      ResultSet<ArtifactReadable> results = queryBuilder.getResults();
      time.end(Units.SEC);
      time = new ElapsedTime("process cpa workflows");
      for (ArtifactReadable art : results) {
         IAtsTeamWorkflow teamWf = atsServer.getWorkItemFactory().getTeamWf(art);
         CpaDecision decision = CpaFactory.getDecision(teamWf, null);
         decision.setApplicability(art.getSoleAttributeValue(AtsAttributeTypes.ApplicableToProgram, ""));
         decision.setRationale(art.getSoleAttributeValue(AtsAttributeTypes.Rationale, ""));
         String pcrToolIdValue = art.getSoleAttributeValue(AtsAttributeTypes.PcrToolId, "");
         if (pcrToolId == null) {
            pcrToolId = pcrToolIdValue;
         }
         decision.setPcrSystem(pcrToolIdValue);
         boolean completed =
            art.getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(StateType.Completed.name());
         decision.setComplete(completed);
         decision.setAssignees(teamWf.getStateMgr().getAssigneesStr());
         if (completed) {
            decision.setCompletedBy(teamWf.getCompletedBy().getName());
            decision.setCompletedDate(DateUtil.getMMDDYY(teamWf.getCompletedDate()));
         }

         // set location of decision workflow
         decision.setDecisionLocation(getCpaPath().path(teamWf.getAtsId()).build().toString());

         // set location of originating pcr
         String origPcrId = art.getSoleAttributeValue(AtsAttributeTypes.OriginatingPcrId);
         origPcrIdToDecision.put(origPcrId, decision);
         decision.setOrigPcrLocation(getCpaPath().path(origPcrId).queryParam("pcrSystem", decision.getPcrSystem()).build().toString());

         // set location of duplicated pcr (if any)
         String duplicatedPcrId = art.getSoleAttributeValue(AtsAttributeTypes.DuplicatedPcrId, null);
         if (Strings.isValid(duplicatedPcrId)) {
            String duplicatedLocation =
               getCpaPath().path(duplicatedPcrId).queryParam("pcrSystem", decision.getPcrSystem()).build().toString();
            decision.setDuplicatedPcrLocation(duplicatedLocation);
            decision.setDuplicatedPcrId(duplicatedPcrId);
         }

         decisions.add(decision);
      }
      time.end();

      time = new ElapsedTime("load issues");
      IAtsCpaService service = cpaRegistry.getServiceById(pcrToolId);
      for (Entry<String, ICpaPcr> entry : service.getPcrsByIds(origPcrIdToDecision.keySet()).entrySet()) {
         for (IAtsCpaDecision decision : origPcrIdToDecision.getValues(entry.getKey())) {
            ((CpaDecision) decision).setOriginatingPcr(entry.getValue());
         }
      }
      time.end();

      return decisions;
   }

   public UriBuilder getCpaPath() {
      return UriBuilder.fromPath(getCpaBasePath()).path("ats").path("cpa").path("decision");
   }

   public String getCpaBasePath() {
      if (cpaBasepath == null) {
         cpaBasepath = atsServer.getConfigValue(CpaFactory.CPA_BASEPATH_KEY);
      }
      return cpaBasepath;
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
         uri = service.getLocation(UriBuilder.fromUri(getCpaBasePath()).build(), uuid);
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
