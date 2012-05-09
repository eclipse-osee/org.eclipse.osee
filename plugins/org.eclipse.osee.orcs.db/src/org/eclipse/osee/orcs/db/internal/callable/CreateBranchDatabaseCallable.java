/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.concurrent.Callable;
import org.eclipse.osee.database.schema.DatabaseCallable;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.internal.branch.CreateDatabaseBranch;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchDatabaseCallable extends DatabaseCallable<Branch> {

   private static final int NULL_PARENT_BRANCH_ID = -1;
   private static final int NULL_SOURCE_TRANSACTION_ID = -1;
   private static final int NULL_ARTIFACT_ID = -1;

   private final BranchCache branchCache;
   private final TransactionCache txCache;
   private final BranchFactory branchFactory;
   private final TransactionRecordFactory txFactory;
   private final CreateBranchData branchData;

   public CreateBranchDatabaseCallable(Log logger, IOseeDatabaseService service, BranchCache branchCache, TransactionCache txCache, BranchFactory branchFactory, TransactionRecordFactory txFactory, CreateBranchData branchData) {
      super(logger, service);
      this.branchCache = branchCache;
      this.txCache = txCache;
      this.branchFactory = branchFactory;
      this.txFactory = txFactory;
      this.branchData = branchData;
   }

   private TransactionCache getTxCache() {
      return txCache;
   }

   private BranchCache getBranchCache() {
      return branchCache;
   }

   private int toArtId(ReadableArtifact artifact) {
      int result = NULL_ARTIFACT_ID;
      if (artifact != null) {
         result = artifact.getId();
      }
      return result;
   }

   @Override
   public Branch call() throws Exception {
      String branchGuid = branchData.getGuid();
      String branchName = branchData.getName();
      BranchType branchType = branchData.getBranchType();
      String creationComment = branchData.getCreationComment();
      ITransaction txData = branchData.getFromTransaction();

      ReadableArtifact authorArtifact = branchData.getUserArtifact();
      int authorId = toArtId(authorArtifact);

      ReadableArtifact associatedArtifact = branchData.getAssociatedArtifact();
      int associatedArtifactId = toArtId(associatedArtifact);

      int sourceTransactionId = NULL_SOURCE_TRANSACTION_ID;
      int parentBranchId = NULL_PARENT_BRANCH_ID;
      if (BranchType.SYSTEM_ROOT != branchType) {
         TransactionRecord sourceTx = getTxCache().getOrLoad(txData.getGuid());
         sourceTransactionId = sourceTx.getId();
         parentBranchId = sourceTx.getBranchId();
      }

      int mergeAddressingQueryId = branchData.getMergeAddressingQueryId();
      int destinationBranchId = branchData.getMergeDestinationBranchId();

      CreateDatabaseBranch createBranchData =
         new CreateDatabaseBranch(branchType, sourceTransactionId, parentBranchId, branchGuid, branchName,
            associatedArtifactId, authorId, creationComment, mergeAddressingQueryId, destinationBranchId);

      Callable<Branch> callable =
         new CreateBranchDatabaseTxCallable(getLogger(), getDatabaseService(), getBranchCache(), getTxCache(),
            branchFactory, txFactory, createBranchData);
      return callAndCheckForCancel(callable);
   }
}
