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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import org.eclipse.osee.ats.api.task.track.TaskTrackingData;
import org.eclipse.osee.ats.api.util.RecentlyVisitedItems;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
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

   @Path("query/workitems/count")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   int queryOpenWorkItemsCount(@QueryParam("artType") ArtifactTypeToken artType,
      @DefaultValue("-1") @QueryParam("orderBy") AttributeTypeToken orderBy,
      @DefaultValue("20010101010101") @QueryParam("maxTime") String maxTime,
      @DefaultValue("") @QueryParam("nameFilter") String nameFilter);

   @Path("query/workitems")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   Collection<WorkItemLastMod> queryOpenWorkItems(@QueryParam("artType") ArtifactTypeToken artType,
      @DefaultValue("0") @QueryParam("pageSize") int pageSize,
      @DefaultValue("0") @QueryParam("pageNumber") int pageNumber,
      @DefaultValue("-1") @QueryParam("orderBy") AttributeTypeToken orderBy,
      @DefaultValue("DESC") @QueryParam("orderDirection") String orderDirection,
      @DefaultValue("20010101010101") @QueryParam("maxTime") String maxTime,
      @DefaultValue("") @QueryParam("nameFilter") String nameFilter);

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
   public NewActionData createAction(NewActionData newActionData);

   @POST
   @Path("multi")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewActionDataMulti createActions(NewActionDataMulti newActionDatas);

   @Path("branch")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
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
    * @param id (ATS id or art id) of action to cancel
    */
   @Path("{id}/cancel")
   @GET
   @Produces({MediaType.TEXT_HTML})
   Response cancelAction(@PathParam("id") String id);

   /**
    * @param workItemId (atsId, artId)
    * @param changeType: Assignee, Version
    * @param valueArts Assignee(s) art ids or Version art id
    */
   @Path("{workItemId}/changeType/{changeType}")
   @PUT
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Collection<ArtifactToken> setByArtifactToken(@PathParam("workItemId") String workItemId,
      @PathParam("changeType") String attrTypeId, Collection<ArtifactToken> valueArts);

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

   @Path("{prTwId}/bids")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas updateBids(@PathParam("prTwId") ArtifactId pwTwId, BuildImpactDatas bids);

   @Path("{prTwId}/bids")
   @DELETE
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas deleteBids(@PathParam("prTwId") ArtifactId prTwId, BuildImpactDatas bids);

   @Path("{prTwId}/bids")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public BuildImpactDatas getBidsById(@PathParam("prTwId") ArtifactId prTwId);

   @Path("{twId}/bidParents")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public BuildImpactDatas getBidParents(@PathParam("twId") ArtifactId twId);

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

   /**
    * See AtsTaskTrackingDesign.md for design and use
    */
   @Path("tasktrack")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public TaskTrackingData createUpdateTaskTrack(TaskTrackingData taskTrackingData);

   @Path("visited/{userArtId}")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void storeVisited(@PathParam("userArtId") ArtifactId userArtId, RecentlyVisitedItems visitedItems);

   @Path("visited/{userArtId}")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public RecentlyVisitedItems getVisited(@PathParam("userArtId") ArtifactId userArtId);

}
