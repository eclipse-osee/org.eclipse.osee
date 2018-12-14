/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.TraceData;
import org.eclipse.osee.define.api.TraceabilityEndpoint;
import org.eclipse.osee.define.rest.internal.PublishLowHighReqStreamingOutput;
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
 * @author Ryan D. Brooks
 */
public class TraceabilityEndpointImpl implements TraceabilityEndpoint {
   private final OrcsApi orcsApi;
   private final DefineApi defineApi;
   private final IResourceRegistry resourceRegistry;
   private final ActivityLog activityLog;

   public TraceabilityEndpointImpl(ActivityLog activityLog, IResourceRegistry resourceRegistry, OrcsApi orcsApi, DefineApi defineApi) {
      this.orcsApi = orcsApi;
      this.defineApi = defineApi;
      this.resourceRegistry = resourceRegistry;
      this.activityLog = activityLog;
   }

   @Override
   public Response getLowHighReqReport(BranchId branch, String selectedTypes) {
      Conditions.checkNotNull(branch, "branch query param");
      Conditions.checkNotNull(selectedTypes, "selected_types query param");
      if (branch.getId().equals(8888L) && selectedTypes.equals("8888")) {
         return Response.ok("TEST").build();
      }

      StreamingOutput streamingOutput =
         new PublishLowHighReqStreamingOutput(activityLog, orcsApi, branch, selectedTypes);
      String fileName = "Requirement_Trace_Report.xml";

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Override
   public TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType) {
      return defineApi.getTraceabilityOperations().getSrsToImpd(branch, excludeType);
   }

   @Override
   public String getSinglePageApp() {
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