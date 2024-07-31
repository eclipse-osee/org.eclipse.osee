/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.testscript.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.testscript.ScriptApi;
import org.eclipse.osee.testscript.ScriptDatabaseInitApi;
import org.eclipse.osee.testscript.ScriptDemoBranches;

public class ScriptDatabaseInitApiImpl implements ScriptDatabaseInitApi {

   private final ScriptApi scriptApi;

   public ScriptDatabaseInitApiImpl(ScriptApi scriptApi) {
      this.scriptApi = scriptApi;
   }

   @Override
   public Branch createDemoBranch() {
      return scriptApi.getOrcsApi().getBranchOps().createWorkingBranch(ScriptDemoBranches.CI_DEMO, DemoBranches.SAW_PL,
         ArtifactId.SENTINEL);
   }

   @Override
   public TransactionResult populateDemoBranch(BranchId branchId) {
      TransactionResult txResult = new TransactionResult();
      XResultData resultData = new XResultData();
      try {
         TransactionBuilderData txData = getDemoTxData(branchId);
         TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(scriptApi.getOrcsApi());
         TransactionBuilder tx = txBdf.loadFromJson((new ObjectMapper()).writeValueAsString(txData));
         TransactionToken token = tx.commit();
         txResult.setTx(token);
         resultData.setIds(
            tx.getTxDataReadables().stream().map(readable -> readable.getIdString()).collect(Collectors.toList()));
      } catch (JsonProcessingException ex) {
         resultData.error("Error processing tx json");
      }
      return txResult;
   }

   @Override
   public TransactionBuilderData getDemoTxData() {
      return getDemoTxData(ScriptDemoBranches.CI_DEMO);
   }

   private TransactionBuilderData getDemoTxData(BranchId branchId) {
      TransactionBuilderData data = new TransactionBuilderData();
      data.setBranch(branchId.getIdString());
      data.setTxComment("Create CI Dashboard Demo Artifacts");
      data.setCreateArtifacts(new LinkedList<>());
      data.setAddRelations(new LinkedList<>());

      CreateArtifact demoCiSet = new CreateArtifact();
      demoCiSet.setkey(Long.toString(getRandomId()));
      demoCiSet.setName("Demo CI Set");
      demoCiSet.setTypeId(CoreArtifactTypes.ScriptSet.getIdString());
      data.getCreateArtifacts().add(demoCiSet);

      return data;
   }

   private long getRandomId() {
      return Math.abs(UUID.randomUUID().getMostSignificantBits());
   }

}
