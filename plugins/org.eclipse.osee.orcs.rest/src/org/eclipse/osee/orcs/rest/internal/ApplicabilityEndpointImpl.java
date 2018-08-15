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
package org.eclipse.osee.orcs.rest.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.data.FeatureDefinition;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
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

   public ApplicabilityEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.applicabilityQuery = orcsApi.getQueryFactory().applicabilityQuery();
      this.account = account;
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
         artToApplicToken.add(new Pair<ArtifactId, ApplicabilityToken>(artId, getApplicabilityToken(artId)));
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
   public List<BranchViewData> getViews() {
      return applicabilityQuery.getViews();
   }

   @Override
   public HashMap<String, ArtifactId> getViewMap() {
      HashMap<String, ArtifactId> viewMap = new HashMap<>();
      List<BranchViewData> views = applicabilityQuery.getViews();
      for (BranchViewData view : views) {
         if (branch.equals(view.getBranch())) {
            for (ArtifactId id : view.getBranchViews()) {
               ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(branch).andId(id).getArtifact();
               viewMap.put(artifact.getName(), id);
            }
         }
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
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
         "Set Reference Applicability Ids for Artifacts");
      tx.setApplicabilityReference(artToApplMap);
      return tx.commit();
   }

   @Override
   public ArtifactToken createView(String viewName) {
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
   public String getViewTable() {
      return applicabilityQuery.getViewTable(branch);
   }

}