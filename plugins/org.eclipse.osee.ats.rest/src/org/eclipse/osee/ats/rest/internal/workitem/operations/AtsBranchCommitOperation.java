/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.rest.internal.workitem.operations;

import java.util.concurrent.Callable;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.core.branch.BranchOperationsUtil;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Audrey Denk
 */
public class AtsBranchCommitOperation {
   private final boolean overrideStateValidation;
   private final BranchId destinationBranch;
   private final IAtsTeamWorkflow teamWf;
   private XResultData rd;
   private final AtsApi atsApi;
   private final AtsUser asUser;
   private final OrcsApi orcsApi;

   public AtsBranchCommitOperation(AtsUser asUser, IAtsTeamWorkflow teamWf, AtsApi atsApi, OrcsApi orcsApi, boolean overrideStateValidation, BranchId destinationBranch, XResultData rd) {
      this.teamWf = teamWf;
      this.overrideStateValidation = overrideStateValidation;
      this.destinationBranch = destinationBranch;
      this.rd = rd;
      this.asUser = asUser;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData run() {
      if (rd == null) {
         rd = new XResultData();
      }

      BranchToken commitToBranch = orcsApi.getQueryFactory().branchQuery().andId(destinationBranch).getOneOrSentinel();

      BranchOperationsUtil.validateBranchCommit(teamWf, commitToBranch, overrideStateValidation, rd, atsApi);
      if (rd.isErrors()) {
         return rd;
      }
      if (!overrideStateValidation) {
         // Check extension points for valid commit
         for (IAtsWorkItemHook item : atsApi.getWorkItemService().getWorkItemHooks()) {
            rd = item.committing(teamWf, rd);
            if (rd.isErrors()) {
               return rd;
            }
         }
      }
      BranchToken workingBranch = atsApi.getBranchService().getBranch(teamWf);
      try {

         rd.logf("Commiting Branch\n");
         rd.logf("Source Branch [%s]\n", workingBranch);
         rd.logf("Destination Branch [%s]\n", commitToBranch);

         try {
            Callable<TransactionToken> op = orcsApi.getBranchOps().commitBranch(asUser, workingBranch, commitToBranch);
            TransactionToken tx = op.call();
            rd.setTxId(tx.getIdString());
         } catch (Exception ex) {
            rd.errorf("Exception commiting branch [%s]", Lib.exceptionToString(ex));
            return rd;
         }
      } catch (Exception ex) {
         rd.errorf("Exception committing branch %s", Lib.exceptionToString(ex));
      }
      return rd;
   }
}