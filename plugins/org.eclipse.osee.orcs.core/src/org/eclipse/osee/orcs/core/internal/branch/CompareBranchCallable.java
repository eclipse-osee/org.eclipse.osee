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
package org.eclipse.osee.orcs.core.internal.branch;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;

public class CompareBranchCallable extends AbstractBranchCallable<List<ChangeItem>> {

   private final TransactionCache txCache;
   private final ITransaction sourceTx;
   private final ITransaction destinationTx;

   public CompareBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, TransactionCache txCache, ITransaction sourceTx, ITransaction destinationTx) {
      super(logger, session, branchStore);
      this.txCache = txCache;
      this.sourceTx = sourceTx;
      this.destinationTx = destinationTx;
   }

   @Override
   protected List<ChangeItem> innerCall() throws Exception {
      Conditions.checkNotNull(sourceTx, "sourceTx");
      Conditions.checkNotNull(destinationTx, "destinationTx");

      TransactionRecord source = txCache.getOrLoad(sourceTx.getGuid());
      TransactionRecord destination = txCache.getOrLoad(destinationTx.getGuid());

      Conditions.checkNotNull(source, "sourceTx");
      Conditions.checkNotNull(destination, "destinationTx");

      Callable<List<ChangeItem>> callable = getBranchStore().compareBranch(getSession(), source, destination);
      return callAndCheckForCancel(callable);

   }
}
