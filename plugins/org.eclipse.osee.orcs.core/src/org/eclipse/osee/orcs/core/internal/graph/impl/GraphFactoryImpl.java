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
package org.eclipse.osee.orcs.core.internal.graph.impl;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.graph.GraphFactory;

/**
 * @author Roberto E. Escobar
 */
public class GraphFactoryImpl implements GraphFactory {

   private final BranchCache branchCache;

   public GraphFactoryImpl(BranchCache branchCache) {
      super();
      this.branchCache = branchCache;
   }

   @Override
   public GraphData createGraph(IOseeBranch branch, int transactionId) {
      return new GraphDataImpl(branch, transactionId);
   }

   @Override
   public GraphData createGraphSetToHeadTx(IOseeBranch branch) throws OseeCoreException {
      Branch fullBranch = branchCache.get(branch);
      TransactionRecord headTransaction = branchCache.getHeadTransaction(fullBranch);
      return createGraph(branch, headTransaction.getId());
   }

}
