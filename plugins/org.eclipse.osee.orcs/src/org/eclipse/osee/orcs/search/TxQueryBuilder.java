/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.search;

import java.sql.Timestamp;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface TxQueryBuilder<T> {

   T andTxId(TransactionId id);

   T andTxIds(Collection<TransactionId> ids);

   T andTxId(Operator op, int id);

   T andTxId(Operator op1, int id1, Operator op2, int id2);

   T andCommentEquals(String value);

   T andCommentPattern(String pattern);

   T andIs(TransactionDetailsType... types);

   T andIs(Collection<TransactionDetailsType> types);

   T andBranch(BranchId... ids);

   T andBranch(Collection<? extends BranchId> ids);

   T andBranchIds(Collection<? extends BranchId> ids);

   T andDate(Operator op, Timestamp date);

   T andDate(Timestamp from, Timestamp to);

   T andAuthorId(ArtifactId author);

   T andAuthorIds(Collection<ArtifactId> authors);

   T andCommitId(ArtifactId id);

   T andCommitIds(Collection<ArtifactId> ids);

   T andNullCommitId();

   T andIsHead(BranchId branch);

   T andIsPriorTx(TransactionToken txId);
}