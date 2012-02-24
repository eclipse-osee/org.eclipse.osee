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
package org.eclipse.osee.orcs.db.internal.branch;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.internal.callable.CommitBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompareDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CreateBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.DeleteRelationDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeBranchDatabaseCallable;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataStoreImpl implements BranchDataStore {

   private Log logger;
   private IOseeDatabaseService dbService;
   private IOseeCachingService cachingService;
   private IOseeModelFactoryService modelFactory;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDbService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setCachingService(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   public void setModelService(IOseeModelFactoryService modelFactory) {
      this.modelFactory = modelFactory;
   }

   @Override
   public Callable<Branch> createBranch(String sessionId, CreateBranchData branchData) {
      return new CreateBranchDatabaseCallable(logger, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getBranchFactory(), modelFactory.getTransactionFactory(),
         branchData);
   }

   @Override
   public Callable<TransactionRecord> commitBranch(String sessionId, ReadableArtifact committer, Branch source, Branch destination) {
      return new CommitBranchDatabaseCallable(logger, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getTransactionFactory(), committer, source, destination);
   }

   @Override
   public Callable<Branch> purgeBranch(String sessionId, Branch branch) {
      return new PurgeBranchDatabaseCallable(logger, dbService, cachingService.getBranchCache(), branch);
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(String sessionId, TransactionRecord sourceTx, TransactionRecord destinationTx) {
      return new CompareDatabaseCallable(logger, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), sourceTx, destinationTx);
   }

   @Override
   public Callable<Branch> deleteRelationTypeFromBranch(IOseeBranch branch, IRelationTypeSide relationType, int aArtId, int bArtId, int artUserId, String comment) {
      return new DeleteRelationDatabaseCallable(logger, dbService, identityService, cachingService.getBranchCache(),
         branch, relationType, aArtId, bArtId, artUserId, comment);
   }
}
