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
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.core.ds.TransactionData;

/**
 * @author Roberto E. Escobar
 */
public class ComodificationCheck implements TransactionCheck {

   private final DataLoaderFactory dataLoader;

   public ComodificationCheck(DataLoaderFactory dataLoader) {
      this.dataLoader = dataLoader;
   }

   @Override
   public void verify(HasCancellation cancellation, String sessionId, TransactionData txData) throws OseeCoreException {
      OnLoadChecker checker = new OnLoadChecker();

      for (ArtifactTransactionData data : txData.getTxData()) {
         data.accept(checker);
      }

      if (!checker.getArtifactIds().isEmpty()) {
         DataLoader loader =
            dataLoader.fromBranchAndArtifactIds(sessionId, txData.getBranch(), checker.getArtifactIds());
         loader.loadAttributeLocalIds(checker.getAttributeIds());
         loader.loadRelationLocalIds(checker.getRelationIds());

         loader.load(cancellation, checker);
      }
   }

   private final class OnLoadChecker implements LoadDataHandler, OrcsVisitor {

      private final Map<Integer, ArtifactData> artifacts = new HashMap<Integer, ArtifactData>();
      private final Map<Integer, AttributeData> attributes = new HashMap<Integer, AttributeData>();
      private final Map<Integer, RelationData> relations = new HashMap<Integer, RelationData>();

      public Collection<Integer> getArtifactIds() {
         return artifacts.keySet();
      }

      public Collection<Integer> getAttributeIds() {
         return attributes.keySet();
      }

      public Collection<Integer> getRelationIds() {
         return relations.keySet();
      }

      private void checkCoModified(OrcsData was, OrcsData is) throws OseeCoreException {
         if (was != null && is != null) {
            if (was.getVersion().getTransactionId() != is.getVersion().getTransactionId()) {
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
      public void visit(AttributeData data) {
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
      public ArtifactDataHandler getArtifactDataHandler() {
         return new ArtifactDataHandler() {

            @Override
            public void onData(ArtifactData data) throws OseeCoreException {
               ArtifactData modified = artifacts.get(data.getLocalId());
               checkCoModified(data, modified);
            }
         };
      }

      @Override
      public RelationDataHandler getRelationDataHandler() {
         return new RelationDataHandler() {

            @Override
            public void onData(RelationData data) throws OseeCoreException {
               RelationData modified = relations.get(data.getLocalId());
               checkCoModified(data, modified);
            }
         };
      }

      @Override
      public AttributeDataHandler getAttributeDataHandler() {
         return new AttributeDataHandler() {

            @Override
            public void onData(AttributeData data) throws OseeCoreException {
               AttributeData modified = attributes.get(data.getLocalId());
               checkCoModified(data, modified);
            }
         };
      }

      @Override
      public void onLoadStart() {
         // Do Nothing
      }

      @Override
      public void onLoadEnd() {
         // Do Nothing
      }

   }

}
