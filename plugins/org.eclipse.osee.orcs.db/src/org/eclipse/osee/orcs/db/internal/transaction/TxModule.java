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
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeTransactionTxCallable;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;

/**
 * @author Roberto E. Escobar
 */
public class TxModule {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final TempCachingService cachingService;

   private final IOseeModelFactoryService modelFactory;
   private final IdFactory idFactory;

   public TxModule(Log logger, IOseeDatabaseService dbService, IdentityService identityService, TempCachingService cachingService, IOseeModelFactoryService modelFactory, IdFactory idFactory) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.cachingService = cachingService;
      this.modelFactory = modelFactory;
      this.idFactory = idFactory;
   }

   public TxDataStore createTransactionStore(final DataLoaderFactory dataLoaderFactory) {
      final TransactionProcessorProviderImpl processors = new TransactionProcessorProviderImpl();
      processors.add(TxWritePhaseEnum.BEFORE_TX_WRITE, new ComodificationCheck(dataLoaderFactory));
      return new TxDataStore() {

         @Override
         public Callable<TransactionResult> commitTransaction(OrcsSession session, TransactionData data) {
            TxSqlBuilderImpl builder = new TxSqlBuilderImpl(dbService, idFactory, identityService);
            TransactionWriter writer = new TransactionWriter(logger, dbService, builder);
            return new CommitTransactionDatabaseTxCallable(logger, session, dbService, cachingService.getBranchCache(),
               cachingService.getTransactionCache(), modelFactory.getTransactionFactory(), processors, writer, data);
         }

         @Override
         public Callable<String> createUnsubscribeTx(ArtifactReadable userArtifact, ArtifactReadable groupArtifact) {
            return new UnsubscribeTransaction(logger, dbService, identityService, userArtifact, groupArtifact);
         }

         @Override
         public Callable<Integer> purgeTransactions(OrcsSession session, Collection<? extends ITransaction> transactionsToPurge) {
            return new PurgeTransactionTxCallable(logger, session, dbService, transactionsToPurge);
         }
      };
   }
}
