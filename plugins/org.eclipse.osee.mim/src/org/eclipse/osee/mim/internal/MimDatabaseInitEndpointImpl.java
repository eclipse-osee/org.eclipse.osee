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

package org.eclipse.osee.mim.internal;

import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimDatabaseInitApi;
import org.eclipse.osee.mim.MimDatabaseInitEndpoint;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;

/**
 * @author Ryan T. Baldwin
 */
public class MimDatabaseInitEndpointImpl implements MimDatabaseInitEndpoint {

   private final MimDatabaseInitApi dbInitApi;

   public MimDatabaseInitEndpointImpl(MimApi mimApi) {
      this.dbInitApi = new MimDatabaseInitApiImpl(mimApi);
   }

   @Override
   public TransactionResult demoDbInit() {
      TransactionResult txResult = new TransactionResult();
      NewActionResult demoBranchResult = this.dbInitApi.createDemoBranches();
      if (demoBranchResult.getResults().isFailed()) {
         txResult.getResults().error(demoBranchResult.getResults().getResults().get(0));
         return txResult;
      }

      return this.dbInitApi.populateDemoBranch(demoBranchResult.getWorkingBranchId());

   }

   @Override
   public TransactionResult createDemoBranches() {
      TransactionResult txResult = new TransactionResult();
      NewActionResult demoBranchResult = this.dbInitApi.createDemoBranches();
      if (demoBranchResult.getResults().isFailed()) {
         txResult.getResults().error(demoBranchResult.getResults().getResults().get(0));
      }
      return txResult;
   }

   @Override
   public TransactionResult populateDemoBranch(BranchId branchId) {
      return this.dbInitApi.populateDemoBranch(branchId);
   }

   @Override
   public MimImportSummary getDemoImportSummary() {
      return this.dbInitApi.getDemoImportSummary();
   }

   @Override
   public TransactionBuilderData getDemoTxData() {
      return this.dbInitApi.getDemoTxData();
   }

}