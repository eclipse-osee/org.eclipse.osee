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

import static org.eclipse.osee.framework.core.data.ApplicabilityToken.BASE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityEndpointImpl implements ApplicabilityEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final TupleQuery tupleQuery;
   private final UserId account;

   public ApplicabilityEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.tupleQuery = orcsApi.getQueryFactory().tupleQuery();
      this.account = account;
   }

   @Override
   public void createDemoApplicability() {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem, "Create Demo Applicability");

      ArtifactId config1 = tx.createArtifact(CoreArtifactTypes.BranchView, "PL Config 1");
      ArtifactId config2 = tx.createArtifact(CoreArtifactTypes.BranchView, "PL Config 2");
      ArtifactId folder = tx.createArtifact(CoreArtifactTypes.Folder, "Product Line");
      ArtifactId featureDefinition =
         tx.createArtifact(CoreArtifactTypes.FeatureDefinition, "Feature Definition_SAW_Bld_1");

      orcsApi.getKeyValueOps().putByKey(BASE.getId(), BASE.getName());

      tx.addChildren(CoreArtifactTokens.DefaultHierarchyRoot, folder);
      tx.addChildren(folder, config1, config2, featureDefinition);

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "Base");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "Base");

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "Config = Config1");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "Config = Config2");

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "A = Included");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "A = Excluded");

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "B = Choice1");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "B = Choice2");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "B = Choice3");

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "C = Included");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "C = Excluded");

      String featureDefJson = "[{" + "\"name\": \"A\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"Test A\"" + //
         "}, {" + //
         "\"name\": \"B\"," + //
         "\"type\": \"multiple\"," + //
         "\"values\": [\"Choice1\", \"Choice2\", \"Choice3\"]," + //
         "\"defaultValue\": \"\"," + //
         "\"description\": \"Test B\"" + //
         "},{" + //
         "\"name\": \"C\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"Test C\"" + //
         "}" + //
         "]";

      tx.createAttribute(featureDefinition, CoreAttributeTypes.GeneralStringData, featureDefJson);

      tx.commit();

      setView(config1);
      setView(config2);
   }

   @Override
   public List<ApplicabilityToken> getApplicabilityTokens() {
      List<ApplicabilityToken> toReturn = new LinkedList<>();
      tupleQuery.getTuple2UniqueE2Pair(CoreTupleTypes.ViewApplicability, branch,
         (id, name) -> toReturn.add(new ApplicabilityToken(id, name)));
      return toReturn;
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId) {
      return orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artId, branch);
   }

   @Override
   public Collection<ApplicabilityToken> getApplicabilityTokenMap() {
      return orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).values();
   }

   @Override
   public List<FeatureDefinitionData> getFeatureDefinitionData() {
      List<ArtifactReadable> featureDefinitionArts = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.FeatureDefinition).getResults().getList();
      return orcsApi.getQueryFactory().applicabilityQuery().getFeatureDefinitionData(featureDefinitionArts);
   }

   @Override
   public Response setApplicability(ApplicabilityId applicId, List<? extends ArtifactId> artifacts) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "Set Applicability Ids for Artifacts");
      for (ArtifactId artId : artifacts) {
         tx.setApplicability(artId, applicId);
      }
      tx.commit();
      return Response.ok().build();
   }

   @Override
   public List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokensForArts(Collection<? extends ArtifactId> artIds) {
      return orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(artIds, branch);
   }

   @Override
   public List<ApplicabilityId> getApplicabilitiesReferenced(ArtifactId artifact) {
      List<ApplicabilityId> appIds = new LinkedList<>();
      for (ApplicabilityId tuple2 : orcsApi.getQueryFactory().tupleQuery().getTuple2(
         CoreTupleTypes.ArtifactReferenceApplicabilityType, branch, artifact)) {
         appIds.add(tuple2);
      }
      return appIds;
   }

   @Override
   public List<ApplicabilityToken> getApplicabilityReferenceTokens(ArtifactId artifact) {
      List<ApplicabilityToken> tokens = new LinkedList<>();
      orcsApi.getQueryFactory().tupleQuery().getTuple2NamedId(CoreTupleTypes.ArtifactReferenceApplicabilityType, branch,
         artifact, (e2, value) -> tokens.add(ApplicabilityToken.create(e2, value)));
      return tokens;
   }

   /**
    * TBD: Need to delete tuples that are not in the set. Update this when tx.removeTuple2 is implemented.
    */
   @Override
   public Response setApplicabilityReference(HashMap<ArtifactId, List<ApplicabilityId>> artToApplMap) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
         "Set Reference Applicability Ids for Artifacts");
      for (Entry<? extends ArtifactId, List<ApplicabilityId>> entry : artToApplMap.entrySet()) {
         for (ApplicabilityId appId : entry.getValue()) {
            if (!orcsApi.getQueryFactory().tupleQuery().doesTuple2Exist(
               CoreTupleTypes.ArtifactReferenceApplicabilityType, entry.getKey(), appId)) {
               tx.addTuple2(CoreTupleTypes.ArtifactReferenceApplicabilityType, entry.getKey(), appId);
            }
         }
      }
      tx.commit();
      return Response.ok().build();
   }

   @Override
   public List<ApplicabilityToken> getViewApplicabilityTokens(ArtifactId view) {
      return orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(view, branch);
   }

   @Override
   public List<BranchViewData> getViews() {
      return orcsApi.getQueryFactory().applicabilityQuery().getViews();
   }

   @Override
   public void setView(ArtifactId branchView) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         SystemUser.OseeSystem, "Create Branch View");
      tx.addTuple2(CoreTupleTypes.BranchView, branch.getId(), branchView.getId());
      tx.commit();
   }

   @Override
   public void createNewApplicabilityForView(ArtifactId viewId, String applicability) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem, "Create new applicability");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, viewId, applicability);
      tx.commit();
   }

   @Override
   public List<BranchId> getAffectedBranches(Long injectDateMs, Long removalDateMs, List<ApplicabilityId> applicabilityIds) {
      ArrayList<BranchId> toReturn = new ArrayList<>();
      Date injection = new Date(injectDateMs);
      Date removal = new Date(removalDateMs);

      // Get all Branch Views
      List<BranchViewData> branchViews = orcsApi.getQueryFactory().applicabilityQuery().getViews();

      HashMap<Long, BranchReadable> childBaselineBranchIds = new HashMap<>();
      for (BranchReadable childBranch : orcsApi.getQueryFactory().branchQuery().andIsChildOf(branch).getResults()) {
         if (childBranch.getBranchType().equals(BranchType.BASELINE)) {
            childBaselineBranchIds.put(childBranch.getId(), childBranch);
         }
      }

      HashMap<Long, ApplicabilityId> applicabilityIdsMap = new HashMap<>();
      for (ApplicabilityId applicId : applicabilityIds) {
         applicabilityIdsMap.put(applicId.getId(), applicId);
      }

      for (BranchViewData branchView : branchViews) {
         BranchReadable baseBranch = childBaselineBranchIds.get(branchView.getBranch().getId());
         if (baseBranch != null) {
            // Check Dates on baseBranch
            Date baseDate = orcsApi.getTransactionFactory().getTx(baseBranch.getBaseTransaction()).getDate();
            if (baseDate.after(injection) && (removalDateMs == -1 || baseDate.before(removal))) {
               // now determine what views of this branch are applicable
               for (ArtifactId view : branchView.getBranchViews()) {
                  // Get all applicability tokens for the view of this branch
                  List<ApplicabilityToken> viewApplicabilityTokens =
                     orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(view,
                        branchView.getBranch());
                  // Cross check applicabilityTokens with valid ApplicabilityIds sent in
                  for (ApplicabilityToken applicToken : viewApplicabilityTokens) {
                     // If applictoken is found, add toReturn list
                     if (applicabilityIdsMap.containsKey(applicToken.getId())) {
                        toReturn.add(BranchId.create(branchView.getBranch().getId(), view));
                        break;
                     }
                  }
               }
            }
         }
      }

      return toReturn;
   }

   @Override
   public List<BranchId> getAffectedBranches(TransactionId injectionTx, TransactionId removalTx, List<ApplicabilityId> applicabilityIds) {
      long timeInjectionMs = orcsApi.getTransactionFactory().getTx(injectionTx).getDate().getTime();
      long timeRemovalMs =
         removalTx.isInvalid() ? -1 : orcsApi.getTransactionFactory().getTx(removalTx).getDate().getTime();

      return getAffectedBranches(timeInjectionMs, timeRemovalMs, applicabilityIds);
   }
}