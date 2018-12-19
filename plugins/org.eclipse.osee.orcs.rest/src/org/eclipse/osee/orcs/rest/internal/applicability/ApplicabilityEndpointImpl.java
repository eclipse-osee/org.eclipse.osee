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
package org.eclipse.osee.orcs.rest.internal.applicability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.PathParam;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.VariantDefinition;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.data.ArtifactReadable;
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
   public List<ArtifactToken> getViewForBranch() {
      return applicabilityQuery.getViewForBranch(branch);
   }

   @Override
   public HashMap<String, ArtifactId> getViewMap() {
      HashMap<String, ArtifactId> viewMap = new HashMap<>();

      for (ArtifactId id : applicabilityQuery.getViewForBranch(branch)) {
         ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(branch).andId(id).getArtifact();
         viewMap.put(artifact.getName(), id);
      }

      return viewMap;
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

   /**
    * TBD: Need to delete tuples that are not in the set. Update this when tx.removeTuple2 is implemented.
    */
   @Override
   public TransactionToken setApplicabilityReference(HashMap<ArtifactId, List<ApplicabilityId>> artToApplMap) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return TransactionToken.SENTINEL;
      }
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
         "Set Reference Applicability Ids for Artifacts");
      tx.setApplicabilityReference(artToApplMap);
      return tx.commit();
   }

   @Override
   public ArtifactToken createView(String viewName) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return ArtifactToken.SENTINEL;
      }
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account, "Create Branch View");
      ArtifactToken view = tx.createView(branch, viewName);
      tx.commit();

      TransactionBuilder tx2 =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, account, "Create Branch View");

      tx2.addTuple2(CoreTupleTypes.BranchView, branch, view);
      tx2.commit();
      return view;
   }

   @Override
   public TransactionToken copyView(ArtifactId sourceView, String viewName) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return TransactionToken.SENTINEL;
      }
      ArtifactToken view = createView(viewName);

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account, "Copy Branch View");
      List<ApplicabilityToken> viewApplicabilityTokens =
         applicabilityQuery.getViewApplicabilityTokens(sourceView, branch);
      for (ApplicabilityToken applicToken : viewApplicabilityTokens) {
         String name = applicToken.getName();
         if (name.contains("Config = ")) {
            tx.addTuple2(CoreTupleTypes.ViewApplicability, view, "Config = " + viewName);
         } else {
            tx.addTuple2(CoreTupleTypes.ViewApplicability, view, name);
         }
      }
      return tx.commit();
   }

   @Override
   public TransactionToken createApplicabilityForView(ArtifactId viewId, String applicability) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return TransactionToken.SENTINEL;
      }
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "Create new applicability");
      tx.createApplicabilityForView(viewId, applicability);
      return tx.commit();
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
   public FeatureDefinition getFeature(String feature) {
      return ops.getFeature(feature, branch);
   }

   @Override
   public XResultData createUpdateFeature(FeatureDefinition feature) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createUpdateFeature(feature, branch, account);
   }

   @Override
   public XResultData deleteFeature(@PathParam("feature") String feature) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.deleteFeature(feature, branch, account);
   }

   @Override
   public XResultData createUpdateVariant(VariantDefinition variant) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createUpdateVariant(variant, branch, account);
   }

   @Override
   public XResultData deleteVariant(@PathParam("variant") String variant) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.deleteVariant(variant, branch, account);
   }

   @Override
   public VariantDefinition getVariant(String variant) {
      return ops.getVariant(variant, branch);
   }

   @Override
   public XResultData setApplicability(ArtifactId variant, ArtifactId feature, String applicability) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.setApplicability(branch, variant, feature, applicability, account);
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

}