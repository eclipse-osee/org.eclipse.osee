/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.transaction;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.dto.ChangeReportRowDto;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionFactory {

   TransactionBuilder createTransaction(BranchId branch, UserId userArtifact, String comment);

   TransactionBuilder createTransaction(BranchId branch, String comment);

   Callable<Integer> purgeTransaction(Collection<? extends TransactionId> transactions);

   int[] purgeUnusedBackingDataAndTransactions();

   Callable<Void> setTransactionComment(TransactionId transaction, String comment);

   List<ChangeItem> compareTxs(TransactionId txId1, TransactionId txId2);

   List<ChangeItem> comparedToParent(BranchId branch);

   List<ChangeItem> comparedToPreviousTx(TransactionToken txId);

   ResultSet<TransactionReadable> getAllTxs();

   TransactionReadable getTx(TransactionId txId);

   boolean setTxComment(TransactionId txId, String comment);

   boolean replaceWithBaselineTxVersion(BranchId branchId, TransactionId txId, ArtifactId artId, String comment);

   boolean purgeTxs(String txIds);

   boolean setTransactionCommitArtifact(TransactionId trans, ArtifactId commitArt);

   List<ChangeItem> getArtifactHistory(ArtifactId artifact, BranchId branch);

   List<ChangeReportRowDto> getTxChangeReport(BranchId branch, BranchId branch2, TransactionId txId1, TransactionId txId2);
}