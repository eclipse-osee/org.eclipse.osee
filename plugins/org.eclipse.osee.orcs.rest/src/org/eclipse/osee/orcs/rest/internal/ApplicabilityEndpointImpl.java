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
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
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
   private final ArtifactId account;

   public ApplicabilityEndpointImpl(OrcsApi orcsApi, BranchId branch, ArtifactId account) {
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

      orcsApi.getKeyValueOps().putByKey(BASE.getId(), BASE.getName());

      tx.addChildren(CoreArtifactTokens.DefaultHierarchyRoot, folder);
      tx.addChildren(folder, config1, config2);

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "Base");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "Base");

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "Feature A = Included");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "Feature A = Excluded");

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "Feature B = Choice 1");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "Feature B = Choice 2");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "Feature B = Choice 3");

      tx.addTuple2(CoreTupleTypes.ViewApplicability, config1, "Feature C = Included");
      tx.addTuple2(CoreTupleTypes.ViewApplicability, config2, "Feature C = Excluded");

      tx.commit();
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
   public List<ApplicabilityToken> getApplicabilityTokensForArts(List<ArtifactId> artIds) {
      return orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(artIds, branch);
   }
}