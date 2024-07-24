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

package org.eclipse.osee.mim;

import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;

public interface MimDatabaseInitApi {

   public NewActionResult createDemoBranches();

   public TransactionResult populateDemoBranch(BranchId branchId);

   public MimImportSummary getDemoImportSummary();

   public TransactionBuilderData getDemoTxData();

}
