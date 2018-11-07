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
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.define.api.CertBaselineData;
import org.eclipse.osee.define.api.CertFileData;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.define.api.TraceData;
import org.eclipse.osee.define.api.TraceabilityEndpoint;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.define.rest.internal.PublishLowHighReqStreamingOutput;
import org.eclipse.osee.define.rest.internal.PublishPidsVerificationReport;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.template.engine.ArtifactTypeOptionsRule;

/**
 * @author Ryan D. Brooks
 */
public final class TraceabilityEndpointImpl implements TraceabilityEndpoint {
   private final OrcsApi orcsApi;
   private final TraceabilityOperations traceOps;
   private final GitOperations gitOps;
   private final IResourceRegistry resourceRegistry;
   private final ActivityLog activityLog;
   private final QueryFactory queryFactory;

   public TraceabilityEndpointImpl(ActivityLog activityLog, IResourceRegistry resourceRegistry, OrcsApi orcsApi, DefineApi defineApi) {
      this.orcsApi = orcsApi;
      this.traceOps = defineApi.getTraceabilityOperations();
      this.gitOps = defineApi.gitOperations();
      this.resourceRegistry = resourceRegistry;
      this.activityLog = activityLog;
      queryFactory = orcsApi.getQueryFactory();
   }

   @Override
   public Response getLowHighReqReport(BranchId branch, String selectedTypes) {
      Conditions.checkNotNull(branch, "branch query param");
      Conditions.checkNotNull(selectedTypes, "selected_types query param");

      StreamingOutput streamingOutput =
         new PublishLowHighReqStreamingOutput(activityLog, orcsApi, branch, selectedTypes);
      String fileName = "Requirement_Trace_Report.xml";

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Override
   public TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType) {
      return traceOps.getSrsToImpd(branch, excludeType);
   }

   @Override
   public String getSinglePageApp() {
      OseeAppletPage pageUtil = new OseeAppletPage(queryFactory.branchQuery());

      ArtifactTypeOptionsRule selectRule =
         new ArtifactTypeOptionsRule("artifactTypeSelect", getTypes(), new HashSet<String>());
      return pageUtil.realizeApplet(resourceRegistry, "publishLowHighReport.html", getClass(), selectRule);
   }

   private Set<String> getTypes() {
      OrcsTypes orcsTypes = orcsApi.getOrcsTypes();
      ArtifactTypes artifactTypes = orcsTypes.getArtifactTypes();
      Set<String> toReturn = new HashSet<>();

      for (ArtifactTypeToken type : artifactTypes.getAll()) {
         toReturn.add(type.getName());
      }
      return toReturn;
   }

   @Override
   public Response getPidsVerificationReport(BranchId branch, ArtifactId rootArtifact) {
      StreamingOutput streamingOutput = new PublishPidsVerificationReport(activityLog, orcsApi, branch, rootArtifact);
      String fileName = "PidsVerificationReport.xml";

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Override
   public ArtifactId baselineFiles(BranchId branch, String repositoryName, UserId account, CertBaselineData baselineData) {
      return traceOps.baselineFiles(branch, gitOps.getRepoArtifact(branch, repositoryName), baselineData, account,
         null);
   }

   @Override
   public List<CertBaselineData> getBaselineData(BranchId branch, String repositoryName) {
      return traceOps.getBaselineData(branch, gitOps.getRepoArtifact(branch, repositoryName));
   }

   @Override
   public TransactionToken copyCertBaselineData(UserId account, BranchId destinationBranch, String repositoryName, BranchId sourceBranch) {
      return traceOps.copyCertBaselineData(account, destinationBranch, repositoryName, sourceBranch);
   }

   @Override
   public List<CertFileData> getCertFileData(BranchId branch, String repositoryName) {
      return traceOps.getCertFileData(branch, gitOps.getRepoArtifact(branch, repositoryName));
   }

   @Override
   public CertBaselineData getBaselineData(BranchId branch, ArtifactId certBaselineData) {
      ArtifactReadable baselineArtifact = queryFactory.fromBranch(branch).andId(certBaselineData).getArtifact();
      return traceOps.getBaselineData(baselineArtifact);
   }
}