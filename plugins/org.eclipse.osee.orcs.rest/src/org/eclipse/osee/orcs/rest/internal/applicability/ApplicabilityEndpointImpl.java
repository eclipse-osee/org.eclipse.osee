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
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.PathParam;
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
import org.eclipse.osee.framework.core.data.VariantDefinition;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
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
   public List<ArtifactToken> getViews() {
      return applicabilityQuery.getViewForBranch(branch);
   }

   @Override
   public List<FeatureDefinition> getFeatureDefinitionData() {
      return ops.getFeatureDefinitionData(branch);
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
         if (!applicabilityQuery.applicabilityExistsOnBranchView(branch, view, name) && !name.startsWith("Config = ")) {
            tx.createApplicabilityForView(view, name);
         }
      }
      return tx.commit();
   }

   @Override
   public XResultData createApplicabilityForView(ArtifactId viewId, String applicability) {
      XResultData results = isAccess();
      if (results.isErrors()) {
         return results;
      }
      if (!applicabilityQuery.viewExistsOnBranch(branch, viewId)) {
         results.error("View is invalid.");
         return results;
      }
      if (applicabilityQuery.applicabilityExistsOnBranchView(branch, viewId, applicability)) {
         results.error("Applicability already exists.");

      }
      if (applicability.startsWith("Config =")) {
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
            "Create " + applicability + " applicability");
         tx.createApplicabilityForView(viewId, applicability);
         tx.commit();
         return results;
      }
      if (applicability.contains("|")) {
         boolean validApplicability = true;
         for (String value : applicability.split("|")) {
            /**
             * loop through existing applicabilities for view and see if new applicability exists if so, stop else check
             * that at least one of the | separated applicability exists
             **/
            Iterable<String> existingApps =
               orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
            for (String appl : existingApps) {
               if (appl.equals(value)) {
                  validApplicability = true;
               }
            }
         }
         if (validApplicability) {
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
               "Apply " + applicability + " applicability");
            tx.createApplicabilityForView(viewId, applicability);
            tx.commit();
         }

      } else {
         String featureName = applicability.substring(0, applicability.indexOf("=") - 1);
         String featureValue = applicability.substring(applicability.indexOf("=") + 2);
         if (applicabilityQuery.featureExistsOnBranch(branch,
            featureName) && applicabilityQuery.featureValueIsValid(branch, featureName, featureValue)) {
            List<String> existingValues = new LinkedList<>();
            Iterable<String> existingApps =
               orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
            for (String appl : existingApps) {
               if (appl.startsWith(featureName + " = ") || appl.contains("| " + featureName + "=")) {
                  existingValues.add(appl);
               }
            }
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
               "Apply " + applicability + " applicability");
            String valueType = "single";
            if (existingValues.size() > 0) {
               List<FeatureDefinition> featureDefinitionData = getFeatureDefinitionData();
               for (FeatureDefinition feat : featureDefinitionData) {
                  if (feat.getName().toUpperCase().equals(featureName)) {
                     valueType = feat.getValueType();
                     break;
                  }
               }
            }
            if (valueType.equals("single")) {
               for (String existingValue : existingValues) {
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, existingValue);
               }
            }
            tx.createApplicabilityForView(viewId, applicability);
            tx.commit();

         } else {
            results.error("Feature is not defined or Value is invalid.");
         }

      }

      return results;
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
   public XResultData createUpdateFeature(FeatureDefinition feature, @PathParam("action") String action) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createUpdateFeature(feature, action, branch, account);
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
   public XResultData createUpdateVariant(VariantDefinition variant, @PathParam("action") String action) {
      XResultData access = isAccess();
      if (access.isErrors()) {
         return access;
      }
      return ops.createUpdateVariant(variant, action, branch, account);
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