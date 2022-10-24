/*********************************************************************
 * Copyright (c) 2018 Boeing
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
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.define.api.TraceData;
import org.eclipse.osee.define.api.TraceabilityEndpoint;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.define.rest.internal.PublishLowHighReqStreamingOutput;
import org.eclipse.osee.define.rest.internal.PublishPidsVerificationReport;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
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

   public TraceabilityEndpointImpl(ActivityLog activityLog, IResourceRegistry resourceRegistry, OrcsApi orcsApi, DefineOperations defineOperations) {
      this.orcsApi = orcsApi;
      this.traceOps = defineOperations.getTraceabilityOperations();
      this.gitOps = defineOperations.gitOperations();
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
      Set<String> toReturn = new HashSet<>();

      for (ArtifactTypeToken type : orcsApi.tokenService().getArtifactTypes()) {
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
   public ArtifactId baselineFiles(BranchId branch, String repositoryName, CertBaselineData baselineData) {
      return traceOps.baselineFiles(branch, gitOps.getRepoArtifact(branch, repositoryName), baselineData, null);
   }

   @Override
   public List<CertBaselineData> getBaselineData(BranchId branch, String repositoryName) {
      return traceOps.getBaselineData(branch, gitOps.getRepoArtifact(branch, repositoryName));
   }

   @Override
   public TransactionToken copyCertBaselineData(BranchId destinationBranch, String repositoryName, BranchId sourceBranch) {
      return traceOps.copyCertBaselineData(destinationBranch, repositoryName, sourceBranch);
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