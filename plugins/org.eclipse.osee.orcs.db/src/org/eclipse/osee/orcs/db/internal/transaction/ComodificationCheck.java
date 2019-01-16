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
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TupleData;

/**
 * @author Roberto E. Escobar
 */
public class ComodificationCheck implements TransactionProcessor {

   private final DataLoaderFactory dataLoader;

   public ComodificationCheck(DataLoaderFactory dataLoader) {
      this.dataLoader = dataLoader;
   }

   @Override
   public void process(HasCancellation cancellation, OrcsSession session, TransactionData txData) {
      OnLoadChecker checker = new OnLoadChecker();

      cancellation.checkForCancelled();
      txData.getChangeSet().accept(checker);

      if (!checker.getArtifactIds().isEmpty()) {
         DataLoader loader = dataLoader.newDataLoaderFromIds(session, txData.getBranch(), checker.getArtifactIds());
         loader.withAttributeIds(checker.getAttributeIds());
         loader.withRelationIds(checker.getRelationIds());

         loader.load(cancellation, checker);
      }
   }

   private final class OnLoadChecker extends LoadDataHandlerAdapter implements OrcsVisitor {

      private final Map<Integer, ArtifactData> artifacts = new HashMap<>();
      private final Map<Integer, AttributeData> attributes = new HashMap<>();
      private final Map<Integer, RelationData> relations = new HashMap<>();
      private final Map<Integer, TupleData> tuples = new HashMap<>();

      public Collection<Integer> getArtifactIds() {
         return artifacts.keySet();
      }

      public Collection<Integer> getAttributeIds() {
         return attributes.keySet();
      }

      public Collection<Integer> getRelationIds() {
         return relations.keySet();
      }

      private void checkCoModified(OrcsData was, OrcsData is) {
         if (was != null && is != null) {
            if (was.getVersion().getTransactionId().notEqual(is.getVersion().getTransactionId())) {
               // TX_TODO can collect and then error with all data that was co-modified but for now just exception on first error
               throw new OseeStateException("Comodification error");
            }
         }
      }

      @Override
      public void visit(ArtifactData data) {
         if (data.getVersion().isInStorage()) {
            artifacts.put(data.getLocalId(), data);
         }
      }

      @Override
      public <T> void visit(AttributeData<T> data) {
         if (data.getVersion().isInStorage()) {
            attributes.put(data.getLocalId(), data);
         }
      }

      @Override
      public void visit(RelationData data) {
         if (data.getVersion().isInStorage()) {
            relations.put(data.getLocalId(), data);
         }
      }

      @Override
      public void visit(TupleData data) {
         tuples.put(data.getLocalId(), data);
      }

      @Override
      public void onData(ArtifactData data) {
         ArtifactData modified = artifacts.get(data.getLocalId());
         checkCoModified(data, modified);
      }

      @Override
      public void onData(RelationData data) {
         RelationData modified = relations.get(data.getLocalId());
         checkCoModified(data, modified);
      }

      @Override
      public <T> void onData(AttributeData<T> data) {
         AttributeData<T> modified = attributes.get(data.getLocalId());
         checkCoModified(data, modified);
      }

      @Override
      public void deleteTuple(BranchId branch, TableEnum tupleTable, GammaId gammaId) {
         // tuples do not support modification (only create and delete)
      }
   }
}