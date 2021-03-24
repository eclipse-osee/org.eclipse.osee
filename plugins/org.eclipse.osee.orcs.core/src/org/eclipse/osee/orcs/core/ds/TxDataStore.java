/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public interface TxDataStore {

   Callable<TransactionResult> commitTransaction(OrcsSession session, TransactionData transaction);

   Callable<Integer> purgeTransactions(OrcsSession session, Collection<? extends TransactionId> transactions);

   int[] purgeUnusedBackingDataAndTransactions();

   Callable<Void> setTransactionComment(OrcsSession session, TransactionId transaction, String comment);

   void setTransactionCommitArtifact(OrcsSession session, TransactionId trans, ArtifactId commitArt);

   Callable<List<ChangeItem>> getArtifactHistory(OrcsSession session, QueryFactory queryFactory, ArtifactId artifact, BranchId branch);

}