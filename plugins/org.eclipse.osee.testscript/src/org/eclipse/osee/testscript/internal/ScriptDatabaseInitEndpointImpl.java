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

import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.testscript.ScriptApi;
import org.eclipse.osee.testscript.ScriptDatabaseInitApi;
import org.eclipse.osee.testscript.ScriptDatabaseInitEndpoint;

public class ScriptDatabaseInitEndpointImpl implements ScriptDatabaseInitEndpoint {

   private final ScriptDatabaseInitApi dbInitApi;

   public ScriptDatabaseInitEndpointImpl(ScriptApi scriptApi) {
      this.dbInitApi = new ScriptDatabaseInitApiImpl(scriptApi);
   }

   @Override
   public TransactionResult demoDbInit() {
      Branch demoBranch = this.dbInitApi.createDemoBranch();
      return this.dbInitApi.populateDemoBranch(demoBranch);

   }

   @Override
   public Branch createDemoBranch() {
      return this.dbInitApi.createDemoBranch();
   }

   @Override
   public TransactionResult populateDemoBranch(BranchId branchId) {
      return this.dbInitApi.populateDemoBranch(branchId);
   }

}
