/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
@Path("applic")
public interface ApplicabilityEndpoint {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   Collection<ApplicabilityToken> getApplicabilityTokens();

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

   @GET
   @Path("view/{viewId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<ApplicabilityToken> getViewApplicabilityTokens(@PathParam("viewId") ArtifactId view);

   @GET
   @Path("views")
   @Produces(MediaType.APPLICATION_JSON)
   List<BranchViewData> getViews();

   @GET
   @Path("view")
   @Produces({MediaType.APPLICATION_JSON})
   HashMap<String, ArtifactId> getViewMap();

   @GET
   @Path("feature-data")
   @Produces(MediaType.APPLICATION_JSON)
   List<FeatureDefinition> getFeatureDefinitionData();

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
   String getViewTable();

   /**
    * Set the applicability in osee_txs for the given artifacts. This affects whether the artifact is included in a
    * branch view.
    */
   @PUT
   @Path("{applicId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken setApplicability(@PathParam("applicId") ApplicabilityId applicId, List<? extends ArtifactId> artifacts);

   /**
    * Set the applicabilities referenced by the provided artifacts. This is stored in the tuple table which means it
    * does not impact applicability in a branch view.
    */
   @PUT
   @Path("artifact/reference")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken setApplicabilityReference(HashMap<ArtifactId, List<ApplicabilityId>> artifacts);

   @GET
   @Path("versionConfig/{version}")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   ArtifactId getVersionConfig(@PathParam("version") ArtifactId version);

   @POST
   @Path("view")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactToken createView(String viewName);

   @POST
   @Path("view/{sourceView}/copy")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken copyView(@PathParam("sourceView") ArtifactId sourceView, String viewName);

   @POST
   @Path("view/{viewId}/applic")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken createApplicabilityForView(@PathParam("viewId") ArtifactId viewId, String applicability);

   /**
    * Copy missing tuples of type CoreTupleTypes.ViewApplicability from parent branch onto this branch
    */
   @POST
   @Path("update-from-parent")
   void addMissingApplicabilityFromParentBranch();

}