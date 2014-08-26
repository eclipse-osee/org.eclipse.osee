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
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.data.TransactionReadable;

public class CompareBranchCallable extends AbstractBranchCallable<List<ChangeItem>> {

   private final TransactionReadable sourceTx;
   private final TransactionReadable destinationTx;

   public CompareBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, TransactionReadable sourceTx, TransactionReadable destinationTx) {
      super(logger, session, branchStore);
      this.sourceTx = sourceTx;
      this.destinationTx = destinationTx;
   }

   @Override
   protected List<ChangeItem> innerCall() throws Exception {
      Conditions.checkNotNull(sourceTx, "sourceTx");
      Conditions.checkNotNull(destinationTx, "destinationTx");

      Callable<List<ChangeItem>> callable = getBranchStore().compareBranch(getSession(), sourceTx, destinationTx);
      return callAndCheckForCancel(callable);

   }
}
