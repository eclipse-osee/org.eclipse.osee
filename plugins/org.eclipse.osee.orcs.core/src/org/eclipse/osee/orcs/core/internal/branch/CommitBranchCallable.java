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

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class CommitBranchCallable extends AbstractBranchCallable<TransactionRecord> {

   private final BranchCache branchCache;
   private final ArtifactReadable committer;
   private final IOseeBranch source;
   private final IOseeBranch destination;

   public CommitBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, BranchCache branchCache, ArtifactReadable committer, IOseeBranch source, IOseeBranch destination) {
      super(logger, session, branchStore);
      this.branchCache = branchCache;
      this.committer = committer;
      this.source = source;
      this.destination = destination;
   }

   private BranchCache getBranchCache() {
      return branchCache;
   }

   @Override
   protected TransactionRecord innerCall() throws Exception {
      Conditions.checkNotNull(branchCache, "branchCache");
      Conditions.checkNotNull(source, "sourceBranch");
      Conditions.checkNotNull(destination, "destinationBranch");

      Branch sourceBranch = getBranchCache().get(source);
      Branch destinationBranch = getBranchCache().get(destination);

      Conditions.checkNotNull(sourceBranch, "sourceBranch");
      Conditions.checkNotNull(destinationBranch, "destinationBranch");

      Callable<TransactionRecord> commitBranchCallable =
         getBranchStore().commitBranch(getSession(), committer, sourceBranch, destinationBranch);
      return callAndCheckForCancel(commitBranchCallable);
   }
}
