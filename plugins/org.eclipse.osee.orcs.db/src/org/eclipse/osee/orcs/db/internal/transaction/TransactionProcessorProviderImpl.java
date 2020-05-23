/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.transaction;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class TransactionProcessorProviderImpl implements TransactionProcessorProvider {

   private final Multimap<TxWritePhaseEnum, TransactionProcessor> processors = newLinkedHashListMultimap();

   public TransactionProcessorProviderImpl() {
      super();
   }

   public void add(TxWritePhaseEnum phase, TransactionProcessor processor) {
      processors.put(phase, processor);
   }

   @Override
   public Iterable<TransactionProcessor> getProcessor(TxWritePhaseEnum phase) {
      return processors.get(phase);
   }

   private static <K, V> ListMultimap<K, V> newLinkedHashListMultimap() {
      Map<K, Collection<V>> map = new LinkedHashMap<>();
      return Multimaps.newListMultimap(map, new Supplier<List<V>>() {
         @Override
         public List<V> get() {
            return Lists.newArrayList();
         }
      });
   }
}
