/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.revision.acquirer;

import java.util.ArrayList;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;

/**
 * @author Jeff C. Phillips
 */
public abstract class ChangeAcquirer {
   private final BranchId sourceBranch;
   private final TransactionToken transactionId;
   private final IProgressMonitor monitor;
   private final Artifact specificArtifact;
   private final Set<Integer> artIds;
   private final ArrayList<ChangeBuilder> changeBuilders;
   private final Set<Integer> newAndDeletedArtifactIds;

   public ChangeAcquirer(BranchId sourceBranch, TransactionToken transactionId, IProgressMonitor monitor, Artifact specificArtifact, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds) {
      super();
      this.sourceBranch = sourceBranch;
      this.transactionId = transactionId;
      this.monitor = monitor;
      this.specificArtifact = specificArtifact;
      this.artIds = artIds;
      this.changeBuilders = changeBuilders;
      this.newAndDeletedArtifactIds = newAndDeletedArtifactIds;
   }

   protected BranchId getSourceBranch() {
      return sourceBranch;
   }

   protected TransactionToken getSourceBaseTransaction() {
      return BranchManager.getBaseTransaction(getSourceBranch());
   }

   protected TransactionToken getTransaction() {
      return transactionId;
   }

   protected IProgressMonitor getMonitor() {
      return monitor;
   }

   protected Artifact getSpecificArtifact() {
      return specificArtifact;
   }

   protected Set<Integer> getArtIds() {
      return artIds;
   }

   protected ArrayList<ChangeBuilder> getChangeBuilders() {
      return changeBuilders;
   }

   protected Set<Integer> getNewAndDeletedArtifactIds() {
      return newAndDeletedArtifactIds;
   }

   public abstract ArrayList<ChangeBuilder> acquireChanges();
}
