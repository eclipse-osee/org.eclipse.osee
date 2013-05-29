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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManagerImpl.TxDataHandlerFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class TransactionFactoryImpl implements TransactionFactory {

   private final Log logger;
   private final SessionContext sessionContext;
   private final BranchDataStore branchDataStore;
   private final ArtifactProxyFactory artifactFactory;
   private final TxDataHandlerFactory handlerF;

   public TransactionFactoryImpl(Log logger, SessionContext sessionContext, BranchDataStore branchDataStore, ArtifactProxyFactory artifactFactory, TxDataHandlerFactory handlerF) {
      this.logger = logger;
      this.sessionContext = sessionContext;
      this.branchDataStore = branchDataStore;
      this.artifactFactory = artifactFactory;
      this.handlerF = handlerF;
   }

   @Override
   public OrcsTransaction createTransaction(IOseeBranch branch, ArtifactReadable author, String comment) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(author, "author");
      Conditions.checkNotNullOrEmpty(comment, "comment");

      TxDataManager manager = new TxDataManagerImpl(artifactFactory, handlerF);
      OrcsTransactionImpl orcsTxn =
         new OrcsTransactionImpl(logger, sessionContext, branchDataStore, artifactFactory, manager, branch);
      orcsTxn.setComment(comment);
      orcsTxn.setAuthor(author);
      return orcsTxn;
   }

   @Override
   public Callable<String> createUnsubscribeTx(ArtifactReadable userArtifact, ArtifactReadable groupArtifact) {
      return branchDataStore.createUnsubscribeTx(userArtifact, groupArtifact);
   }

   @Override
   public Callable<?> purgeTransaction(Collection<? extends ITransaction> transactions) {
      return branchDataStore.purgeTransactions(sessionContext.getSessionId(), transactions);
   }
}
