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
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitorAdapter;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public class TransactionIndexer implements TransactionProcessor {

   private final Log logger;
   private final QueryEngineIndexer indexer;
   private final AttributeTypes types;

   public TransactionIndexer(Log logger, QueryEngineIndexer indexer, AttributeTypes types) {
      super();
      this.logger = logger;
      this.indexer = indexer;
      this.types = types;
   }

   @Override
   public void process(final HasCancellation cancellation, OrcsSession session, TransactionData txData) {
      try {
         final Set<Long> datas = new LinkedHashSet<>();
         txData.getChangeSet().accept(new OrcsVisitorAdapter() {
            @Override
            public void visit(AttributeData data) {
               AttributeTypeId type = types.get(data.getTypeUuid());
               if (types.isTaggable(type)) {
                  datas.add(data.getVersion().getGammaId().getId());
               }
            }
         });

         List<Future<?>> futures = indexer.indexResources(session, types, datas).call();
         for (Future<?> future : futures) {
            if (cancellation != null && cancellation.isCancelled()) {
               future.cancel(true);
            } else {
               // Wait for execution to complete
               future.get();
            }
         }
      } catch (Exception ex) {
         logger.error(ex, "Error indexing transaction [%s]", txData);
      }
   }
}