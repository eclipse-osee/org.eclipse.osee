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
import java.util.Arrays;
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
import org.eclipse.osee.ats.api.cpa.IAtsCpaDecision;
import org.eclipse.osee.ats.api.cpa.IAtsCpaProgram;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.cpa.ICpaPcr;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.cpa.CpaConfig;
import org.eclipse.osee.ats.core.cpa.CpaConfigTool;
import org.eclipse.osee.ats.core.cpa.CpaDecision;
import org.eclipse.osee.ats.core.cpa.CpaFactory;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
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
         atsServer.getQuery().andTypeEquals(AtsArtifactTypes.TeamWorkflow).and(AtsAttributeTypes.ProgramUuid,
            Operator.EQUAL, uuid);
      if (open != null) {
         queryBuilder.and(AtsAttributeTypes.CurrentStateType, Operator.EQUAL,
            (open ? StateType.Working.name() : StateType.Completed.name()));
      }
      for (ArtifactReadable art : queryBuilder.getResults()) {
         IAtsTeamWorkflow teamWf = atsServer.getWorkItemFactory().getTeamWf(art);
         CpaDecision decision = CpaFactory.getDecision(teamWf, null);
         decision.setApplicability(art.getSoleAttributeValue(AtsAttributeTypes.ApplicableToProgram, ""));
         decision.setRationale(art.getSoleAttributeValue(AtsAttributeTypes.Rationale, ""));
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
         String duplicatedPcrId = art.getSoleAttributeValue(AtsAttributeTypes.DuplicatedPcrId, null);
         if (Strings.isValid(duplicatedPcrId)) {
            String duplicatedLocation =
               getCpaPath().path(duplicatedPcrId).queryParam("pcrSystem", decision.getPcrSystem()).build().toString();
            decision.setDuplicatedPcrLocation(duplicatedLocation);
            decision.setDuplicatedPcrId(duplicatedPcrId);
         }

         IAtsCpaService service = cpaRegistry.getServiceById(decision.getPcrSystem());
         ICpaPcr origPcr = service.getPcr(origPcrId);
         decision.setOriginatingPcr(origPcr);

         decisions.add(decision);
      }
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
      ResultSet<ArtifactReadable> results =
         atsServer.getQuery().and(AtsAttributeTypes.AtsId, Operator.EQUAL, update.getUuids()).getResults();
      IAtsChangeSet changes =
         atsServer.getStoreFactory().createAtsChangeSet("Update CPA Decision", AtsCoreUsers.SYSTEM_USER);
      for (ArtifactReadable art : results) {
         IAtsTeamWorkflow teamWf = atsServer.getWorkItemFactory().getTeamWf(art);
         updateRationale(update, changes, teamWf);
         updateDuplicatedPcrId(update, changes, teamWf);
         updateApplicability(update, changes, teamWf);
         updateAssignees(update, changes, teamWf);
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return Response.ok().entity(AHTML.simplePage("Ok")).build();
   }

   private void updateApplicability(final DecisionUpdate update, IAtsChangeSet changes, IAtsTeamWorkflow teamWf) {
      // update applicability - transition
      if (update.getApplicability() != null) {
         String appl = update.getApplicability();
         if (appl.isEmpty()) {
            // transition to analyze
            changes.deleteAttributes(teamWf, AtsAttributeTypes.ApplicableToProgram);

            TransitionHelper helper =
               new TransitionHelper("Transition " + teamWf.getAtsId(), Arrays.asList(teamWf),
                  TeamState.Analyze.getName(), teamWf.getAssignees(), "", changes,
                  TransitionOption.OverrideAssigneeCheck);
            helper.setTransitionUser(AtsCoreUsers.SYSTEM_USER);
            IAtsTransitionManager mgr = TransitionFactory.getTransitionManager(helper);
            TransitionResults results = mgr.handleAll();
            if (!results.isEmpty()) {
               throw new OseeCoreException(results.toString());
            }

         } else {
            // transition to completed
            changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ApplicableToProgram, appl);

            TransitionHelper helper =
               new TransitionHelper("Transition " + teamWf.getAtsId(), Arrays.asList(teamWf),
                  TeamState.Completed.getName(), null, "", changes, TransitionOption.OverrideAssigneeCheck);
            helper.setTransitionUser(AtsCoreUsers.SYSTEM_USER);
            IAtsTransitionManager mgr = TransitionFactory.getTransitionManager(helper);
            TransitionResults results = mgr.handleAll();
            if (!results.isEmpty()) {
               throw new OseeCoreException(results.toString());
            }

         }
      }
   }

   private void updateAssignees(final DecisionUpdate update, IAtsChangeSet changes, IAtsTeamWorkflow teamWf) {
      if (update.getAssignees() != null) {
         List<IAtsUser> assignees = new ArrayList<IAtsUser>();
         for (String userId : update.getAssignees()) {
            IAtsUser user = atsServer.getUserService().getUserById(userId);
            if (user == null) {
               throw new OseeWebApplicationException(Status.BAD_REQUEST, String.format("Invalid userId [%s]", userId));
            }
            assignees.add(user);
         }
         List<IAtsUser> currentAssignees = teamWf.getAssignees();
         if (assignees.isEmpty()) {
            assignees.add(AtsCoreUsers.UNASSIGNED_USER);
         } else if (assignees.size() > 1 && assignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
            assignees.remove(AtsCoreUsers.UNASSIGNED_USER);
         }
         if (!Collections.isEqual(currentAssignees, assignees)) {
            teamWf.getStateMgr().setAssignees(assignees);
            changes.add(teamWf);
         }
      }
   }

   private void updateRationale(final DecisionUpdate update, IAtsChangeSet changes, IAtsTeamWorkflow teamWf) {
      // update rationale
      if (update.getRationale() != null) {
         if (update.getRationale().equals("")) {
            changes.deleteAttributes(teamWf, AtsAttributeTypes.Rationale);
         } else {
            changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.Rationale, update.getRationale());
         }
      }
   }

   private void updateDuplicatedPcrId(final DecisionUpdate update, IAtsChangeSet changes, IAtsTeamWorkflow teamWf) {
      if (update.getRationale() != null) {
         if (update.getRationale().equals("")) {
            changes.deleteAttributes(teamWf, AtsAttributeTypes.DuplicatedPcrId);
         } else {
            changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.DuplicatedPcrId, update.getDuplicatedPcrId());
         }
      }
   }

   @GET
   @Path("config")
   @Produces(MediaType.APPLICATION_JSON)
   public CpaConfig getConfigs() throws Exception {
      CpaConfig config = new CpaConfig();
      config.getApplicabilityOptions().add("Yes");
      config.getApplicabilityOptions().add("No");
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
