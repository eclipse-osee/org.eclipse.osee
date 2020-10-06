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

package org.eclipse.osee.orcs.rest.internal.applicability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityData;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.ViewDefinition;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.rest.internal.writer.ApplicabilityFeatureMatrixStreamingOutput;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityEndpointImpl implements ApplicabilityEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final ApplicabilityQuery applicabilityQuery;
   private final UserId account;
   private final OrcsApplicability ops;

   public ApplicabilityEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.applicabilityQuery = orcsApi.getQueryFactory().applicabilityQuery();
      this.account = account;
      ops = orcsApi.getApplicabilityOps();
   }

   @Override
   public Collection<ApplicabilityToken> getApplicabilityTokens() {
      return applicabilityQuery.getApplicabilityTokens(branch).values();
   }

   @Override
   public Collection<String> getPossibleApplicabilities() {
      return applicabilityQuery.getPossibleApplicabilities(branch);
   }

   @Override
   public List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(List<? extends ArtifactId> artIds) {
      return applicabilityQuery.getApplicabilityTokens(artIds, branch);
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId) {
      return applicabilityQuery.getApplicabilityToken(artId, branch);
   }

   @Override
   public List<ApplicabilityId> getApplicabilitiesReferenced(ArtifactId artifact) {
      return applicabilityQuery.getApplicabilitiesReferenced(artifact, branch);
   }

   public List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokensForArts(Collection<? extends ArtifactId> artIds) {
      List<Pair<ArtifactId, ApplicabilityToken>> artToApplicToken = new ArrayList<>();
      for (ArtifactId artId : artIds) {
         artToApplicToken.add(new Pair<>(artId, getApplicabilityToken(artId)));
      }
      return artToApplicToken;
   }

   @Override
   public List<ApplicabilityToken> getApplicabilityReferenceTokens(ArtifactId artifact) {
      return applicabilityQuery.getApplicabilityReferenceTokens(artifact, branch);
   }

   @Override
   public List<ApplicabilityToken> getViewApplicabilityTokens(ArtifactId view) {
      return applicabilityQuery.getViewApplicabilityTokens(view, branch);
   }

   @Override
   public List<ArtifactToken> getViews() {
      return applicabilityQuery.getViewsForBranch(branch);
   }

   @Override
   public List<ArtifactToken> getCfgGroup() {
      return applicabilityQuery.getConfigurationGroupsForBranch(branch);
   }

   @Override
   public List<FeatureDefinition> getFeatureDefinitionData() {
      return applicabilityQuery.getFeatureDefinitionData(branch);
   }

   @Override
   public List<BranchId> getAffectedBranches(Long injectDateMs, Long removalDateMs, List<ApplicabilityId> applicabilityIds) {
      return applicabilityQuery.getAffectedBranches(injectDateMs, removalDateMs, applicabilityIds, branch);
   }

   @Override
   public List<BranchId> getAffectedBranches(TransactionId injectionTx, TransactionId removalTx, List<ApplicabilityId> applicabilityIds) {
      return applicabilityQuery.getAffectedBranches(injectionTx, removalTx, applicabilityIds, branch);
   }

   @Override
   public TransactionToken setApplicability(ApplicabilityId applicId, List<? extends ArtifactId> artifacts) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return TransactionToken.SENTINEL;
      }
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "Set Applicability Ids for Artifacts");
      tx.setApplicability(applicId, artifacts);
      return tx.commit();
   }

   @Override
   public TransactionToken setApplicabilityByString(String applicTag, List<? extends ArtifactId> artifacts) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return TransactionToken.SENTINEL;
      }
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "Set Applicability Ids for Artifacts");
      Long putIfAbsent = orcsApi.getKeyValueOps().putIfAbsent(applicTag);
      ApplicabilityToken applicId = new ApplicabilityToken(putIfAbsent, applicTag);
      tx.setApplicability(applicId, artifacts);
      return tx.commit();
   }

   @Override
   public TransactionToken setApplicabilityReference(List<ApplicabilityData> appDatas) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return TransactionToken.SENTINEL;
      }
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
         "Set Reference Applicability Ids for Artifacts");
      HashMap<ArtifactId, List<ApplicabilityId>> artToApplMap = new HashMap<ArtifactId, List<ApplicabilityId>>();
      for (ApplicabilityData data : appDatas) {
         artToApplMap.put(data.getArtifact(), Collections.castAll(data.getApplIds()));
      }
      tx.setApplicabilityReference(artToApplMap);
      return tx.commit();
   }

   @Override
   public XResultData createApplicabilityForView(ArtifactId viewId, String applicability) {
      XResultData results = isAccess();
      UserId user = account;
      if (user == null) {
         user = SystemUser.OseeSystem;
      }
      if (results.isErrors()) {
         return results;
      }
      return ops.createApplicabilityForView(viewId, applicability, user, branch);
   }

   @Override
   public XResultData removeApplicabilityFromView(ArtifactId viewId, String applicability) {
      XResultData results = isAccess();
      UserId user = account;
      if (user == null) {
         user = SystemUser.OseeSystem;
      }
      if (results.isErrors()) {
         return results;
      }

      return ops.removeApplicabilityFromView(branch, viewId, applicability, user);
   }

   @Override
   public void addMissingApplicabilityFromParentBranch() {
      orcsApi.getBranchOps().addMissingApplicabilityFromParentBranch(branch);
   }

   @Override
   public ArtifactId getVersionConfig(ArtifactId version) {
      return applicabilityQuery.getVersionConfig(version, branch);
   }

   @Override
   public String getViewTable(String filter) {
      return applicabilityQuery.getViewTable(branch, filter);
   }

   @Override
   public String getConfigMatrix(String matrixType, String filter) {
      String mType = "all";
      if (matrixType != null) {
         mType = matrixType;
      }
      return applicabilityQuery.getConfigMatrix(branch, mType, filter);
   }

   @Override
   public FeatureDefinition getFeature(String featureNameOrId) {
      return ops.getFeature(featureNameOrId, branch);
   }

   @Override
   public XResultData updateFeature(FeatureDefinition feature) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createUpdateFeature(feature, "edit", branch, account);
   }

   @Override
   public XResultData createFeature(FeatureDefinition feature) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createUpdateFeature(feature, "add", branch, account);
   }

   @Override
   public XResultData deleteFeature(ArtifactId feature) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.deleteFeature(feature, branch, account);
   }

   @Override
   public XResultData updateView(ViewDefinition view) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createUpdateView(view, "edit", branch, account);
   }

   @Override
   public XResultData createView(ViewDefinition view) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createUpdateView(view, "add", branch, account);
   }

   @Override
   public XResultData deleteView(@PathParam("view") String view) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.deleteView(view, branch, account);
   }

   @Override
   public ViewDefinition getView(String viewNameOrId) {
      return ops.getView(viewNameOrId, branch);
   }

   @Override
   public XResultData isAccess() {
      XResultData rd = new XResultData();
      if (OseeProperties.isInTest()) {
         rd.logf("Access granted to branch cause isInTest");
      }
      Branch brch = orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getAtMostOneOrNull();
      if (brch.getBranchType() == BranchType.WORKING) {
         rd.log("Access granted to working branch");
      } else {
         rd.error("Access denied to non-working branch");
      }
      return rd;
   }

   @Override
   public XResultData createCfgGroup(String groupName) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createCfgGroup(groupName, branch, account);
   }

   @Override
   public XResultData relateCfgGroupToView(String groupName, String viewName) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.relateCfgGroupToView(groupName, viewName, branch, account);
   }

   @Override
   public XResultData unrelateCfgGroupToView(String groupName, String viewName) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.unrelateCfgGroupToView(groupName, viewName, branch, account);
   }

   @Override
   public XResultData deleteCfgGroup(String groupName) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.deleteCfgGroup(groupName, branch, account);
   }

   @Override
   public XResultData updateCfgGroup(String cfgGroup) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.updateConfigGroup(branch, cfgGroup, account, null);

   }

   @Override
   public XResultData updateCfgGroup() {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.updateConfigGroup(branch, account);

   }

   @Override
   public Response getFeatureMatrixExcel(@PathParam("branch") BranchId branchId, @QueryParam("filter") String filter) {
      StreamingOutput streamingOutput = new ApplicabilityFeatureMatrixStreamingOutput(orcsApi, branchId, filter);
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "FeatureMatrix.xml");
      return builder.build();
   }
}