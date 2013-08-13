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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

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
      Map<K, Collection<V>> map = new LinkedHashMap<K, Collection<V>>();
      return Multimaps.newListMultimap(map, new Supplier<List<V>>() {
         @Override
         public List<V> get() {
            return Lists.newArrayList();
         }
      });
   }
}
