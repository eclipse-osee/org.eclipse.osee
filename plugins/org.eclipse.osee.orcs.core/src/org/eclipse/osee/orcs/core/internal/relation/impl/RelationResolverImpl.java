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
package org.eclipse.osee.orcs.core.internal.relation.impl;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.relation.RelationNodeLoader;
import org.eclipse.osee.orcs.core.internal.relation.RelationResolver;

/**
 * @author Roberto E. Escobar
 */
public class RelationResolverImpl implements RelationResolver {

   private final RelationNodeLoader loader;

   public RelationResolverImpl(RelationNodeLoader loader) {
      super();
      this.loader = loader;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends RelationNode> List<T> resolve(OrcsSession session, GraphData graph, List<Relation> links, RelationSide... sides) {
      List<T> toReturn = Collections.emptyList();
      if (!links.isEmpty()) {
         Set<Integer> toLoad = null;
         LinkedHashMap<Integer, T> items = new LinkedHashMap<>();
         for (Relation relation : links) {
            for (RelationSide side : sides) {
               int localId = relation.getLocalIdForSide(side);
               RelationNode node = graph.getNode(localId);
               if (node == null) {
                  if (toLoad == null) {
                     toLoad = new LinkedHashSet<>();
                  }
                  toLoad.add(localId);
               }
               items.put(localId, (T) node);
            }
         }
         if (toLoad != null && !toLoad.isEmpty()) {
            Iterable<T> result = loader.loadNodes(session, graph, toLoad, LoadLevel.ALL);
            for (T item : result) {
               items.put(item.getLocalId(), item);
            }
         }
         toReturn = toList(items.values());
      }
      return toReturn;
   }

   private <T> List<T> toList(Collection<T> values) {
      List<T> list;
      if (values instanceof ArrayList) {
         list = (ArrayList<T>) values;
      } else if (values instanceof LinkedList) {
         list = (LinkedList<T>) values;
      } else if (values == null) {
         list = Collections.emptyList();
      } else {
         list = Lists.newLinkedList(values);
      }
      return list;
   }

   @Override
   public void resolve(OrcsSession session, GraphData graph, RelationNode node) {
      loader.loadNodes(session, graph, Collections.singleton(node.getLocalId()), LoadLevel.RELATION_DATA);
   }

}
