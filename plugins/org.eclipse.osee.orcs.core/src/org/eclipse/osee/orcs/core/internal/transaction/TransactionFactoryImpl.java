/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class TransactionFactoryImpl implements TransactionFactory {

   private final OrcsSession session;
   private final TxDataManager txDataManager;
   private final TxCallableFactory txCallableFactory;
   private final QueryModule query;

   public TransactionFactoryImpl(OrcsSession session, TxDataManager txDataManager, TxCallableFactory txCallableFactory, QueryModule query) {
      super();
      this.session = session;
      this.txDataManager = txDataManager;
      this.txCallableFactory = txCallableFactory;
      this.query = query;
   }

   @Override
   public CancellableCallable<Integer> purgeTransaction(Collection<? extends ITransaction> transactions) {
      return txCallableFactory.purgeTransactions(session, transactions);
   }

   @Override
   public TransactionBuilder createTransaction(long uuid, ArtifactReadable userArtifact, String comment) throws OseeCoreException {
      IOseeBranch branch = TokenFactory.createBranch(uuid, "");
      return createTransaction(branch, userArtifact, comment);
   }

   @Override
   public TransactionBuilder createTransaction(IOseeBranch branch, ArtifactReadable author, String comment) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(author, "author");
      Conditions.checkNotNullOrEmpty(comment, "comment");

      TxData txData = txDataManager.createTxData(session, branch);
      TransactionBuilderImpl orcsTxn = new TransactionBuilderImpl(txCallableFactory, txDataManager, txData, query);
      orcsTxn.setComment(comment);
      orcsTxn.setAuthor(author);
      return orcsTxn;
   }

   @Override
   public Callable<Void> setTransactionComment(ITransaction transaction, String comment) {
      return txCallableFactory.setTransactionComment(session, transaction, comment);
   }
}
