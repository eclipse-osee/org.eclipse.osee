/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.jira.JiraByEpicData;
import org.eclipse.osee.ats.api.agile.jira.JiraDiffData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jaxrs.mvc.IdentityView;

/**
 * @author Donald G. Dunne
 */
@Path("action")
@Swagger
public interface AtsActionEndpointApi {

   @GET
   @Produces(MediaType.TEXT_HTML)
   String get();

   /**
    * @param ids (atsId, artId) of action to display
    * @return html representation of the action
    */
   @Path("{ids}")
   @GET
   @IdentityView
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsWorkItem> getAction(@PathParam("ids") String ids);

   /**
    * @param ids (atsId, artId) of action to display
    * @return html representation of the action
    */
   @Path("{ids}/details")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsWorkItem> getActionDetails(@PathParam("ids") String ids);

   /**
    * @param ids (atsId, artId) of action children to display
    * @return html representation of the action children
    */
   @Path("{ids}/child")
   @GET
   @IdentityView
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsWorkItem> getActionChildren(@PathParam("ids") String ids);

   @Path("{ids}/sibling")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public List<IAtsWorkItem> getSiblings(@PathParam("ids") String ids);

   /**
    * @param ids (ats.Legacy PCR Id) of action to display
    * @return html representation of the stateType;state
    */
   @Path("{ids}/legacy/state")
   @GET
   String getActionStateFromLegacyPcrId(@PathParam("ids") String ids);

   /**
    * @param ids (atsId, artId) of action to display
    * @return html representation of the stateType;state
    */
   @Path("{ids}/state")
   @GET
   String getActionState(@PathParam("ids") String ids);

   /**
    * @query_string <art type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Path("query/lastmod")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public Collection<WorkItemLastMod> queryOpenLastMod(@Context UriInfo uriInfo);

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Path("query")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   Set<IAtsWorkItem> query(@Context UriInfo uriInfo);

   /**
    * @param ids - comma delimited legacy id attribute values
    * @return json representation of the matching workItem(s)
    */
   @Path("query/legacyId")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsWorkItem> query(@QueryParam("ids") String ids);

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewActionResult createAction(NewActionData newActionData);

   @Path("branch")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public NewActionResult createActionAndWorkingBranch(NewActionData newActionData);

   @Path("branch/commit")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData commitWorkingBranch(@QueryParam("teamWfId") String teamWfId,
      @QueryParam("branchId") BranchId destinationBranch);

   @Path("createEmpty")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public String createEmptyAction(@QueryParam("userId") String userId, @QueryParam("ai") String actionItem,
      @QueryParam("title") String title);

   /**
    * @param form containing information to create a new action
    * @param form.ats_title - (required) title of new action
    * @param form.desc - description of the action
    * @param form.actionableItems - (required) actionable item name
    * @param form.changeType - (required) Improvement, Refinement, Problem, Support
    * @param form.priority - (required) 1-5
    * @param form.userId - (required)
    * @return html representation of action created
    */
   @POST
   @Consumes("application/x-www-form-urlencoded")
   Response createAction(MultivaluedMap<String, String> form);

   /**
    * @param actionId (atsId, artId) of workItem
    * @param attrTypeId can be the id of the attrType or one of (Title, Priority, ColorTeam, Assignee, IPT, Originator,
    * Version, State). If State is sent in, it will result in the "transition" of the workflow. Version can be either id
    * or name.
    * @return Attribute containing current values
    */
   @Path("{actionId}/attributeType/{attrTypeId}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   Attribute getActionAttributeByType(@PathParam("actionId") String actionId,
      @PathParam("attrTypeId") AttributeTypeToken attributeType);

   /**
    * @param actionId (atsId, artId) of workItem
    * @param attrTypeId can be the id of the attrType or one of (Title, Priority, ColorTeam, Assignee, IPT, Originator,
    * Version, State). If State is sent in, it will result in the "transition" of the workflow. Version can be either id
    * or name.
    * @return Attribute containing current values after change
    */
   @Path("{actionId}/attributeType/{attrTypeId}")
   @PUT
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   Attribute setActionAttributeByType(@PathParam("actionId") String actionId,
      @PathParam("attrTypeId") String attrTypeId, List<String> values);

   /**
    * Will cancel action if configured to do so and tasks and reviews are completed.
    *
    * @param id (atsId, artId) of action to cancel
    */
   @Path("{id}/cancel")
   @GET
   @Produces({MediaType.TEXT_HTML})
   Response cancelAction(@PathParam("id") String id) throws URISyntaxException;

   /**
    * @param workItemId (atsId, artId)
    * @param changeType: Assignee, Version
    * @param artifacts: Assignee
    * @return artifacts changed to
    */
   @Path("{workItemId}/changeType/{changeType}")
   @PUT
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Collection<ArtifactToken> setByArtifactToken(@PathParam("workItemId") String workItemId,
      @PathParam("changeType") String attrTypeId, Collection<ArtifactToken> artifacts);

   /**
    * @return valid unreleased versions to select
    */
   @GET
   @Path("{id}/UnreleasedVersions")
   @Produces(MediaType.APPLICATION_JSON)
   List<String> getUnreleasedVersionNames(@PathParam("id") String id);

   /**
    * @return valid transition-to states in order of default state, other states and return states
    */
   @GET
   @Path("{id}/TransitionToStates")
   @Produces(MediaType.APPLICATION_JSON)
   List<String> getTransitionToStateNames(@PathParam("id") String id);

   /**
    * @return list of json objects containing artifact ids and names for a related set of requirements
    */
   @GET
   @Path("{id}/assocArt/{attrTypeId}")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getRelatedRequirements(@PathParam("workflowId") ArtifactId workflowId,
      @PathParam("relatedReqs") AttributeTypeToken relatedReqs,
      @QueryParam("versionType") AttributeTypeToken versionType);

   @Path("branch/changes/{branchId}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<ChangeItem> getBranchChangeData(@PathParam("branchId") BranchId branchId);

   @Path("transaction/changes/{transactionId}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<ChangeItem> getTransactionChangeData(@PathParam("transactionId") TransactionId transactionId);

   @Path("transition")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   TransitionResults transition(TransitionData transData);

   @Path("transitionValidate")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   TransitionResults transitionValidate(TransitionData transData);

   @Path("sync/jira")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public XResultData syncJira();

   @Path("sync/jira/persist")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public XResultData syncJiraAndPersist();

   @Path("jira/report/epic")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   JiraByEpicData reportEpicDiffsByEpic(JiraByEpicData data);

   @Path("jira/report/diff")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   JiraDiffData reportEpicDiffs(JiraDiffData data);

   @Path("journal")
   @POST
   @Consumes("application/x-www-form-urlencoded")
   Response journal(MultivaluedMap<String, String> form);

   @Path("{atsId}/journal/text")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getJournalText(@PathParam("atsId") String atsId);

   @Path("{atsId}/journal")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public JournalData addJournal(@PathParam("atsId") String atsId, JournalData journalData);

   @Path("{atsId}/journal")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public JournalData getJournalData(@PathParam("atsId") String atsId);

   @Path("{atsId}/bids")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas updateBids(@PathParam("atsId") String atsId, BuildImpactDatas bids);

   @Path("{atsId}/bids")
   @DELETE
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas deleteBids(@PathParam("atsId") String atsId, BuildImpactDatas bids);

   @Path("{atsId}/bids")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas getBids(@PathParam("atsId") String atsId);

   @Path("points")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<String> getPointValues();

   @Path("{id}/approval")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public boolean checkApproval(@PathParam("id") String atsId);

   @Path("{id}/approval")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public boolean setApproval(@PathParam("id") String atsId);
}
