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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.cpa.IAtsCpaDecision;
import org.eclipse.osee.ats.api.cpa.IAtsCpaProgram;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.cpa.CpaDecision;
import org.eclipse.osee.ats.core.cpa.CpaFactory;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.util.AtsUtilServer;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.utility.RestUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Services provided for ATS Cross Program Applicability
 * 
 * @author Donald G. Dunne
 */
@Path("cpa")
public final class CpaResource {

   private final OrcsApi orcsApi;
   private final IAtsServer atsServer;
   private final IApplicationServerManager appServerMgr;

   public CpaResource(OrcsApi orcsApi, IAtsServer atsServer, IApplicationServerManager appServerMgr) {
      this.orcsApi = orcsApi;
      this.atsServer = atsServer;
      this.appServerMgr = appServerMgr;
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
      for (IAtsCpaService service : AtsCpaServices.getServices()) {
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
         AtsUtilServer.getQuery(orcsApi).andTypeEquals(AtsArtifactTypes.TeamWorkflow).and(
            AtsAttributeTypes.ProgramUuid, Operator.EQUAL, uuid);
      if (open != null) {
         queryBuilder.and(AtsAttributeTypes.CurrentStateType, Operator.EQUAL,
            (open ? StateType.Working.name() : StateType.Completed.name()));
      }
      for (ArtifactReadable art : queryBuilder.getResults()) {
         IAtsTeamWorkflow teamWf = atsServer.getWorkItemFactory().getTeamWf(art);
         CpaDecision decision = CpaFactory.getDecision(teamWf, null);
         decision.setApplicability(art.getSoleAttributeValue(AtsAttributeTypes.ApplicableToProgram, ""));
         decision.setRationale(art.getSoleAttributeValue(AtsAttributeTypes.Decision, ""));
         decision.setPcrSystem(art.getSoleAttributeValue(AtsAttributeTypes.PcrToolId, ""));
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
         decision.setOrigPcrLocation(getCpaPath().path(origPcrId).queryParam("pcrSystem", decision.getPcrSystem()).build().toString());

         // set location of duplicated pcr (if any)
         String duplicatedLocation = "";
         if (art.getSoleAttributeValue(AtsAttributeTypes.DuplicatedPcrId, null) != null) {
            String duplicatedPcrId = (String) art.getSoleAttributeValue(AtsAttributeTypes.DuplicatedPcrId);
            duplicatedLocation =
               getCpaPath().path(duplicatedPcrId).queryParam("pcrSystem", decision.getPcrSystem()).build().toString();
         }
         decision.setDuplicatedPcrLocation(duplicatedLocation);

         IAtsCpaService service = AtsCpaServices.getService(decision.getPcrSystem());
         decision.setOriginatingProgram(service.getProgramName(origPcrId));

         decisions.add(decision);
      }
      return decisions;
   }

   public UriBuilder getCpaPath() {
      return UriBuilder.fromPath(getCpaBasePath()).path("ats").path("cpa").path("decision");
   }

   public String getCpaBasePath() {
      return OseeInfo.getCachedValue(CpaFactory.CPA_BASEPATH_KEY);
   }

   @GET
   @Path("decision/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getDecision(@PathParam("uuid") String uuid, @QueryParam("pcrSystem") String pcrSystem) throws Exception {
      URI uri = null;
      if (pcrSystem == null) {
         uri =
            UriBuilder.fromUri(appServerMgr.getServerUri()).path("ats").path("action").path(uuid).path("state").build();
      } else {
         IAtsCpaService service = AtsCpaServices.getService(pcrSystem);
         uri = service.getLocation(appServerMgr.getServerUri(), uuid);
      }
      return Response.seeOther(uri).build();
   }

   @GET
   @Path("config")
   @Produces(MediaType.APPLICATION_JSON)
   public String getConfigs() throws Exception {
      JSONArray jsonArray = new JSONArray();
      for (IAtsCpaService service : AtsCpaServices.getServices()) {
         JSONObject tool = new JSONObject();
         tool.put("id", service.getId());
         jsonArray.put(tool);
      }
      return RestUtil.jsonToPretty(jsonArray, false);
   }

   @GET
   @Path("config/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getConfig(@PathParam("id") String id) throws Exception {
      for (IAtsCpaService service : AtsCpaServices.getServices()) {
         if (service.getId().equals(id)) {
            return service.getConfigJson();
         }
      }
      throw new OseeArgumentException("Unknown CPA configuration [%s]", id);
   }
}
