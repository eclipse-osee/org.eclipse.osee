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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.ViewDefinition;
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
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   ApplicabilityToken getApplicabilityToken(@PathParam("artId") ArtifactId artId);

   @GET
   @Path("artifact/reference/{artId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<ApplicabilityId> getApplicabilitiesReferenced(@PathParam("artId") ArtifactId artifact);

   @GET
   @Path("artifact/reference/token/{artId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<ApplicabilityToken> getApplicabilityReferenceTokens(@PathParam("artId") ArtifactId artifact);

   @PUT
   @Path("view")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData updateView(ViewDefinition view);

   @POST
   @Path("view")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData createView(ViewDefinition view);

   @DELETE
   @Path("view/{viewName}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData deleteView(@PathParam("viewName") String viewName);

   @GET
   @Path("cfggroup")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getCfgGroup();

   @POST
   @Path("cfggroup")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData createCfgGroup(String grpName);

   @PUT
   @Path("cfggroup/{groupName}/relate/{viewName}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData relateCfgGroupToView(@PathParam("groupName") String groupName, @PathParam("viewName") String viewName);

   @PUT
   @Path("cfggroup/{groupName}/unrelate/{viewName}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData unrelateCfgGroupToView(@PathParam("groupName") String groupName, @PathParam("viewName") String viewName);

   @DELETE
   @Path("cfggroup/{groupName}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData deleteCfgGroup(@PathParam("groupName") String viewName);

   @PUT
   @Path("cfggroup/{cfgGroup}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData updateCfgGroup(@PathParam("cfgGroup") String cfgGroup);

   @PUT
   @Path("cfggroup")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData updateCfgGroup();

   @GET
   @Path("view/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<ApplicabilityToken> getViewApplicabilityTokens(@PathParam("id") ArtifactId id);

   @GET
   @Path("view/def/{viewNameOrId}")
   @Produces({MediaType.APPLICATION_JSON})
   ViewDefinition getView(@PathParam("viewNameOrId") String viewNameOrId);

   @GET
   @Path("views")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getViews();

   @GET
   @Path("feature-data")
   @Produces(MediaType.APPLICATION_JSON)
   List<FeatureDefinition> getFeatureDefinitionData();

   @GET
   @Path("feature/{featureNameOrId}")
   @Produces({MediaType.APPLICATION_JSON})
   FeatureDefinition getFeature(@PathParam("featureNameOrId") String featureNameOrId);

   @PUT
   @Path("feature")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData updateFeature(FeatureDefinition feature);

   @POST
   @Path("feature")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData createFeature(FeatureDefinition feature);

   @DELETE
   @Path("feature/{id}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData deleteFeature(@PathParam("id") ArtifactId id);

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
   @Consumes(MediaType.APPLICATION_JSON)
   ArtifactId getVersionConfig(@PathParam("version") ArtifactId version);

   @PUT
   @Path("view/{id}/applic")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData createApplicabilityForView(@PathParam("id") ArtifactId id, String applicability);

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
   @Consumes(MediaType.APPLICATION_JSON)
   XResultData isAccess();

   @GET
   @Path("featureMatrix")
   @Produces(MediaType.APPLICATION_XML)
   public Response getFeatureMatrixExcel(@PathParam("branch") BranchId branchId, @QueryParam("filter") String filter);

}