/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface TransactionQuery {

   TransactionQuery andTxId(int... id) throws OseeCoreException;

   TransactionQuery andTxIds(Collection<Integer> ids) throws OseeCoreException;

   TransactionQuery andTxId(Operator op, int id) throws OseeCoreException;

   /***********************************************************************
    * Use for complex queries such as ranges. Translates to transaction_id op1 id1 and transaction_id op2 id2
    * 
    * @param op1 operator for first term
    * @param id1 id for first term
    * @param op2 operator for second term
    * @param id2 id for second term
    * @return the transaction query
    * @throws OseeCoreException
    */
   TransactionQuery andTxId(Operator op1, int id1, Operator op2, int id2) throws OseeCoreException;

   TransactionQuery andCommentEquals(String value) throws OseeCoreException;

   TransactionQuery andCommentPattern(String pattern) throws OseeCoreException;

   TransactionQuery andIs(TransactionDetailsType... types) throws OseeCoreException;

   TransactionQuery andIs(Collection<TransactionDetailsType> types) throws OseeCoreException;

   TransactionQuery andBranch(IOseeBranch... ids) throws OseeCoreException;

   TransactionQuery andBranch(Collection<? extends IOseeBranch> ids) throws OseeCoreException;

   TransactionQuery andBranchIds(int... id) throws OseeCoreException;

   TransactionQuery andBranchIds(Collection<Integer> ids) throws OseeCoreException;

   TransactionQuery andDate(Operator op, Timestamp date) throws OseeCoreException;

   TransactionQuery andDate(Timestamp from, Timestamp to) throws OseeCoreException;

   TransactionQuery andAuthorLocalIds(ArtifactId... id) throws OseeCoreException;

   TransactionQuery andAuthorLocalIds(Collection<ArtifactId> ids) throws OseeCoreException;

   TransactionQuery andAuthorIds(int... id) throws OseeCoreException;

   TransactionQuery andAuthorIds(Collection<Integer> ids) throws OseeCoreException;

   TransactionQuery andCommitIds(Integer... id) throws OseeCoreException;

   TransactionQuery andCommitIds(Collection<Integer> ids) throws OseeCoreException;

   TransactionQuery andNullCommitId() throws OseeCoreException;

   TransactionQuery andIsHead(int branchId) throws OseeCoreException;

   ResultSet<TransactionReadable> getResults() throws OseeCoreException;

   ResultSet<Integer> getResultsAsIds() throws OseeCoreException;

   int getCount() throws OseeCoreException;

   CancellableCallable<Integer> createCount() throws OseeCoreException;

   CancellableCallable<ResultSet<TransactionReadable>> createSearch() throws OseeCoreException;

   CancellableCallable<ResultSet<Integer>> createSearchResultsAsIds() throws OseeCoreException;

}
