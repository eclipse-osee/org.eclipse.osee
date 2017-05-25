/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.search;

import java.sql.Timestamp;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface TxQueryBuilder<T> {

   T andTxId(TransactionId id);

   T andTxIds(Collection<TransactionId> ids);

   T andTxId(Operator op, int id) throws OseeCoreException;

   T andTxId(Operator op1, int id1, Operator op2, int id2) throws OseeCoreException;

   T andCommentEquals(String value) throws OseeCoreException;

   T andCommentPattern(String pattern) throws OseeCoreException;

   T andIs(TransactionDetailsType... types) throws OseeCoreException;

   T andIs(Collection<TransactionDetailsType> types) throws OseeCoreException;

   T andBranch(BranchId... ids) throws OseeCoreException;

   T andBranch(Collection<? extends BranchId> ids) throws OseeCoreException;

   T andBranchIds(long... id) throws OseeCoreException;

   T andBranchIds(Collection<Long> ids) throws OseeCoreException;

   T andDate(Operator op, Timestamp date) throws OseeCoreException;

   T andDate(Timestamp from, Timestamp to) throws OseeCoreException;

   T andAuthorLocalIds(ArtifactId... id) throws OseeCoreException;

   T andAuthorLocalIds(Collection<ArtifactId> ids) throws OseeCoreException;

   T andAuthorIds(int... id) throws OseeCoreException;

   T andAuthorIds(Collection<Integer> ids) throws OseeCoreException;

   T andCommitIds(Integer... id) throws OseeCoreException;

   T andCommitIds(Collection<Integer> ids) throws OseeCoreException;

   T andNullCommitId() throws OseeCoreException;

   T andIsHead(BranchId branch) throws OseeCoreException;

   T andIsPriorTx(TransactionId txId) throws OseeCoreException;
}
