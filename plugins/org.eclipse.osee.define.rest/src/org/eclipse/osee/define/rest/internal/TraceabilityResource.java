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
package org.eclipse.osee.define.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.TraceData;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.template.engine.ArtifactTypeOptionsRule;

/**
 * @author Marc Potter
 */

@Path("traceability")
public final class TraceabilityResource {
   private final OrcsApi orcsApi;
   private final DefineApi defineApi;
   private final IResourceRegistry resourceRegistry;
   private final ActivityLog activityLog;

   public TraceabilityResource(ActivityLog activityLog, IResourceRegistry resourceRegistry, OrcsApi orcsApi, DefineApi defineApi) {
      this.orcsApi = orcsApi;
      this.defineApi = defineApi;
      this.resourceRegistry = resourceRegistry;
      this.activityLog = activityLog;
   }

   /**
    * @param branchUuid -- the UUID of the branch to apply the test to
    * @param selectedTypes -- a list of the Low level Artifact types that will be used for the report
    * @return -- An Excel sheet (in XML format) containing the two reports
    */
   @Path("highlowtrace")
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getLowHighReqReport(@QueryParam("branch") BranchId branch, @QueryParam("selected_types") String selectedTypes) {
      Conditions.checkNotNull(branch, "branch query param");
      Conditions.checkNotNull(selectedTypes, "selected_types query param");

      StreamingOutput streamingOutput =
         new PublishLowHighReqStreamingOutput(activityLog, orcsApi, branch, selectedTypes);
      String fileName = "Requirement_Trace_Report.xml";

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Path("srs-impd/{branch}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public TraceData getSrsToImpd(@PathParam("branch") BranchId branch, @DefaultValue("-1") @QueryParam("excludeType") ArtifactTypeId excludeType) {
      return defineApi.getTraceabilityOperations().getSrsToImpd(branch, excludeType);
   }

   /**
    * @return -- the correctly formulated applet string for the report
    */
   @Path("ui")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getApplet() {
      OseeAppletPage pageUtil = new OseeAppletPage(orcsApi.getQueryFactory().branchQuery());

      ArtifactTypeOptionsRule selectRule =
         new ArtifactTypeOptionsRule("artifactTypeSelect", getTypes(), new HashSet<String>());
      return pageUtil.realizeApplet(resourceRegistry, "publishLowHighReport.html", getClass(), selectRule);
   }

   private Set<String> getTypes() {
      OrcsTypes orcsTypes = orcsApi.getOrcsTypes();
      ArtifactTypes artifactTypes = orcsTypes.getArtifactTypes();
      Set<String> toReturn = new HashSet<>();

      for (IArtifactType type : artifactTypes.getAll()) {
         toReturn.add(type.getName());
      }
      return toReturn;
   }
}