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
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.change.AddArtifactChangeDataCallable;
import org.eclipse.osee.orcs.db.internal.change.ComputeNetChangeCallable;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenBranches;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenTxsOnTheSameBranch;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;

public class CompareDatabaseCallable extends AbstractDatastoreCallable<List<ChangeItem>> {

   private final TransactionCache txCache;
   private final BranchCache branchCache;
   private final TransactionRecord sourceTx;
   private final TransactionRecord destinationTx;
   private final MissingChangeItemFactory missingChangeItemFactory;

   public CompareDatabaseCallable(Log logger, OrcsSession session, IOseeDatabaseService service, BranchCache branchCache, TransactionCache txCache, TransactionRecord sourceTx, TransactionRecord destinationTx, MissingChangeItemFactory missingChangeItemFactory) {
      super(logger, session, service);
      this.branchCache = branchCache;
      this.txCache = txCache;
      this.sourceTx = sourceTx;
      this.destinationTx = destinationTx;
      this.missingChangeItemFactory = missingChangeItemFactory;
   }

   private TransactionCache getTxCache() {
      return txCache;
   }

   private BranchCache getBranchCache() {
      return branchCache;
   }

   @Override
   public List<ChangeItem> call() throws Exception {
      TransactionDelta txDelta = new TransactionDelta(sourceTx, destinationTx);

      Callable<List<ChangeItem>> callable;
      if (txDelta.areOnTheSameBranch()) {
         callable = new LoadDeltasBetweenTxsOnTheSameBranch(getLogger(), getSession(), getDatabaseService(), txDelta);
      } else {
         TransactionRecord mergeTx = getMergeTransaction(sourceTx, destinationTx);
         callable = new LoadDeltasBetweenBranches(getLogger(), getSession(), getDatabaseService(), txDelta, mergeTx);
      }
      List<ChangeItem> changes = callAndCheckForCancel(callable);

      changes.addAll(missingChangeItemFactory.createMissingChanges(this, getSession(), changes, sourceTx, destinationTx));
      Callable<List<ChangeItem>> computeChanges = new ComputeNetChangeCallable(changes);
      changes = callAndCheckForCancel(computeChanges);

      Callable<List<ChangeItem>> addArtifactData = new AddArtifactChangeDataCallable(changes);
      return callAndCheckForCancel(addArtifactData);
   }

   private TransactionRecord getMergeTransaction(TransactionRecord sourceTx, TransactionRecord destinationTx) throws OseeCoreException {
      Branch mergeBranch = getBranchCache().findMergeBranch(sourceTx.getBranch(), destinationTx.getBranch());
      return mergeBranch != null ? getTxCache().getTransaction(mergeBranch, TransactionVersion.HEAD) : null;
   }

}
