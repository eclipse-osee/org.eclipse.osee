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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTransactionImpl implements OrcsTransaction {

   private final Log logger;
   private final IOseeBranch branch;
   private String txnComment;
   private ReadableArtifact authorArtifact;
   private final BranchDataStore dataStore;

   private final List<Callable<?>> callables = new ArrayList<Callable<?>>();

   public OrcsTransactionImpl(Log logger, BranchDataStore dataStore, IOseeBranch branch) {
      super();
      this.dataStore = dataStore;
      this.logger = logger;
      this.branch = branch;
   }

   public void setComment(String txnComment) {
      this.txnComment = txnComment;
   }

   public void setAuthor(ReadableArtifact authorArtifact) {
      this.authorArtifact = authorArtifact;
   }

   public ReadableArtifact getAuthor() {
      return authorArtifact;
   }

   public String getComment() {
      return txnComment;
   }

   private int getAuthorId() {
      ReadableArtifact author = getAuthor();
      int authorId = author != null ? author.getId() : -1;
      return authorId;
   }

   @Override
   public void deleteRelation(IRelationTypeSide relationType, int aArtId, int bArtId) {
      callables.add(dataStore.deleteRelationTypeFromBranch(branch, relationType, aArtId, bArtId, getAuthorId(),
         getComment()));
   }

   @Override
   public Callable<?> build() {
      return new CompositeCallable(logger, callables);
   }

   private static final class CompositeCallable extends CancellableCallable<Boolean> {

      private final List<Callable<?>> callables;
      private final Log logger;
      private Callable<?> innerWorker;

      public CompositeCallable(Log logger, List<Callable<?>> callables) {
         super();
         this.logger = logger;
         this.callables = callables;
      }

      protected Log getLogger() {
         return logger;
      }

      @Override
      public final Boolean call() throws Exception {
         long startTime = 0;
         if (getLogger().isTraceEnabled()) {
            startTime = System.currentTimeMillis();
         }
         try {
            for (Callable<?> callable : callables) {
               callAndCheckForCancel(callable);
            }
         } finally {
            if (getLogger().isTraceEnabled()) {
               getLogger().trace("Admin [%s] completed in [%s]", getClass().getSimpleName(),
                  Lib.getElapseString(startTime));
            }
         }
         return Boolean.TRUE;
      }

      protected <K> K callAndCheckForCancel(Callable<K> callable) throws Exception {
         checkForCancelled();
         setInnerWorker(callable);
         K result = callable.call();
         setInnerWorker(null);
         return result;
      }

      private synchronized void setInnerWorker(Callable<?> callable) {
         innerWorker = callable;
      }

      @Override
      public void setCancel(boolean isCancelled) {
         super.setCancel(isCancelled);
         final Callable<?> inner = innerWorker;
         if (inner != null) {
            synchronized (inner) {
               if (inner instanceof CancellableCallable) {
                  ((CancellableCallable<?>) inner).setCancel(isCancelled);
               }
            }
         }
      }
   }

}
