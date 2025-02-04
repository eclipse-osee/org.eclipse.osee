/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.testscript.ResultToPurge;
import org.eclipse.osee.testscript.ScriptApi;
import org.eclipse.osee.testscript.ScriptPurgeEndpoint;

public class ScriptPurgeEndpointImpl implements ScriptPurgeEndpoint {

   private final ScriptApi scriptApi;

   public ScriptPurgeEndpointImpl(ScriptApi scriptApi) {
      this.scriptApi = scriptApi;
   }

   @Override
   public TransactionResult purgeTestResults(BranchId branchId, boolean deleteOnly) {
      if (branchId == null || branchId.isInvalid()) {
         TransactionResult result = new TransactionResult();
         result.getResults().error("Invalid branch ID");
         return result;
      }
      return this.scriptApi.getScriptPurgeApi().purgeResults(branchId, deleteOnly);
   }

   @Override
   public Collection<ResultToPurge> getDeletedResults(BranchId branchId) {
      return this.scriptApi.getScriptPurgeApi().getDeletedResults(branchId);
   }

}
