/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import java.util.Collection;
import java.util.List;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityData;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BlockApplicabilityStageRequest;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.CreateViewDefinition;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
@Path("applic")
public interface ApplicabilityEndpoint {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   Collection<ApplicabilityToken> getApplicabilityTokens();

   /**
    * getPossibleApplicabilities uses Feature artifacts to compute all possible combinations of a feature and its
    * values. The computed applicability tags may or may not have been used and therefore might not return using the
    * getApplicabilityTokens method which only returns applicability tags which have been used at some time and are
    * already stored in osee_key_value table.
    */
   @GET
   @Path("all")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<String> getPossibleApplicabilities();

   @GET
   @Path("artifact/{artId}")
   @Produces(MediaType.APPLICATION_JSON)
   ApplicabilityToken getApplicabilityToken(@PathParam("artId") ArtifactId artId);

   @GET
   @Path("artifact/reference/{artId}")
   @Produces(MediaType.APPLICATION_JSON)
   List<ApplicabilityId> getApplicabilitiesReferenced(@PathParam("artId") ArtifactId artifact);

   @GET
   @Path("artifact/reference/token/{artId}")
   @Produces(MediaType.APPLICATION_JSON)
   List<ApplicabilityToken> getApplicabilityReferenceTokens(@PathParam("artId") ArtifactId artifact);

   @GET
   @Path("cfggroup")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getCfgGroup();

   @POST
   @Path("cfggroup")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   XResultData createCfgGroup(ConfigurationGroupDefinition group);

   @PUT
   @Path("cfggroup")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData updateCfgGroup(ConfigurationGroupDefinition group);

   @POST
   @Path("cfggroup/sync")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData syncCfgGroup();

   @DELETE
   @Path("cfggroup/{id}")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData deleteCfgGroup(@PathParam("id") String groupId);

   @POST
   @Path("cfggroup/sync/{id}")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData syncCfgGroup(@PathParam("id") String id);

   @GET
   @Path("cfggroup/def/{id}")
   @Produces({MediaType.APPLICATION_JSON})
   ConfigurationGroupDefinition getConfigurationGroup(@PathParam("id") String id);

   @PUT
   @Path("cfggroup/{groupId}/relate/{viewId}")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData relateCfgGroupToView(@PathParam("groupId") String groupId, @PathParam("viewId") String viewId);

   @PUT
   @Path("cfggroup/{groupId}/unrelate/{viewId}")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData unrelateCfgGroupToView(@PathParam("groupId") String groupId, @PathParam("viewId") String viewId);

   @GET
   @Path("features")
   @Produces(MediaType.APPLICATION_JSON)
   List<FeatureDefinition> getFeatureDefinitionData();

   @PUT
   @Path("feature")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData updateFeature(FeatureDefinition feature);

   @POST
   @Path("feature")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   XResultData createFeature(FeatureDefinition feature);

   @GET
   @Path("feature/{featureNameOrId}")
   @Produces({MediaType.APPLICATION_JSON})
   FeatureDefinition getFeature(@PathParam("featureNameOrId") String featureNameOrId);

   @DELETE
   @Path("feature/{id}")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData deleteFeature(@PathParam("id") ArtifactId id);

   @GET
   @Path("views")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getViews();

   @PUT
   @Path("view")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   XResultData updateView(CreateViewDefinition view);

   @POST
   @Path("view")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   XResultData createView(CreateViewDefinition view);

   @GET
   @Path("view/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   List<ApplicabilityToken> getViewApplicabilityTokens(@PathParam("id") ArtifactId id);

   @DELETE
   @Path("view/{id}")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData deleteView(@PathParam("id") String id);

   @PUT
   @Path("view/{id}/applic")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData createApplicabilityForView(@PathParam("id") ArtifactId id, String applicability);

   @DELETE
   @Path("view/{id}/applic")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData removeApplicabilityFromView(@PathParam("id") ArtifactId viewId, String applicability);

   @GET
   @Path("view/def/{id}")
   @Produces({MediaType.APPLICATION_JSON})
   CreateViewDefinition getView(@PathParam("id") String id);

   /**
    * @return a list of branches that contain the injected change (prior to removalDate)
    * @param injectDateMs & removalDateMs are relative to the change injection/removal into the root branch.
    * @param removalDateMs if default value of -1 is used, return list of branches after injectionDate
    */
   @GET
   @Path("change")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<BranchId> getAffectedBranches(@QueryParam("injectionDateMs") Long injectDateMs, @QueryParam("removalDateMs") @DefaultValue("-1") Long removalDateMs, List<ApplicabilityId> applicabilityIds);

   @PUT
   @Path("artifacts")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(List<? extends ArtifactId> artIds);

   @GET
   @Path("change")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<BranchId> getAffectedBranches(@QueryParam("injectiontx") TransactionId injectionTx, @QueryParam("removaltx") @DefaultValue("-1") TransactionId removalTx, List<ApplicabilityId> applicabilityIds);

   @GET
   @Path("table")
   @Produces(MediaType.TEXT_HTML)
   String getViewTable(@QueryParam("filter") String filter);

   @GET
   @Path("matrix")
   @Produces(MediaType.TEXT_HTML)
   String getConfigMatrix(@QueryParam("matrixtype") String matrixType, @QueryParam("filter") String filter);

   /**
    * Set the applicability in osee_txs for the given artifacts. This affects whether the artifact is included in a
    * branch view.
    */
   @PUT
   @Path("{applicId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken setApplicability(@PathParam("applicId") ApplicabilityId applicId, List<? extends ArtifactId> artifacts);

   @PUT
   @Path("set/{applicTag}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken setApplicabilityByString(@PathParam("applicTag") String applicTag, List<? extends ArtifactId> artifacts);

   /**
    * Set the applicabilities referenced by the provided artifacts. This is stored in the tuple table which means it
    * does not impact applicability in a branch view.
    */
   @PUT
   @Path("artifact/reference")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken setApplicabilityReference(List<ApplicabilityData> appDatas);

   @GET
   @Path("versionConfig/{version}")
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactId getVersionConfig(@PathParam("version") ArtifactId version);

   /**
    * Copy missing tuples of type CoreTupleTypes.ViewApplicability from parent branch onto this branch
    */
   @PUT
   @Path("update-from-parent")
   void addMissingApplicabilityFromParentBranch();

   /**
    * Server doesn't support checking for branch access. For now, only return true if isInTest or working branch. This
    * should be replaced with the appropriate branch access checks when available.
    */
   @GET
   @Path("access")
   @Produces(MediaType.APPLICATION_JSON)
   XResultData isAccess();

   @GET
   @Path("featureMatrix")
   @Produces(MediaType.APPLICATION_XML)
   public Response getFeatureMatrixExcel(@PathParam("branch") BranchId branchId, @QueryParam("filter") String filter);

   @POST
   @Path("blockVisibility")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   String applyBlockVisibility(BlockApplicabilityStageRequest data);

   @PUT
   @Path("validate")
   @Produces(MediaType.APPLICATION_JSON)
   XResultData validate();

   @GET
   @Path("applicabilityToken/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   ApplicabilityToken getApplicabilityTokenFromId(@PathParam("id") String id);
}