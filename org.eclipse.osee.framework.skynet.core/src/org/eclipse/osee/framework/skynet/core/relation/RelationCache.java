/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.relation;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class RelationCache {
   private final DoubleKeyHashMap<Integer, Integer, RelationLink> historicalRelationCache =
         new DoubleKeyHashMap<Integer, Integer, RelationLink>();

   private final CompositeKeyHashMap<Integer, Branch, RelationLink> relationBranchCache =
         new CompositeKeyHashMap<Integer, Branch, RelationLink>(2000);

   private static final RelationCache instance = new RelationCache();

   private RelationCache() {
   }

   /**
    * Cache the newly created link so we can ensure we don't create it more than once
    * 
    * @param relation
    */
   public static void cache(RelationLink relation) {
      instance.relationBranchCache.put(relation.getPersistenceMemo().getLinkId(), relation.getBranch(), relation);
   }

   /**
    * Cache the newly created link so we can ensure we don't create it more than once
    * 
    * @param relation
    * @param transactionId
    */
   public static void cache(RelationLink relation, Integer transactionId) {
      instance.historicalRelationCache.put(relation.getPersistenceMemo().getLinkId(), transactionId, relation);
   }

   public static void deCache(RelationLink relation, Integer transactionId) {
      instance.historicalRelationCache.remove(relation.getPersistenceMemo().getLinkId(), transactionId);
   }

   /**
    * @param relation
    */
   public static void deCache(RelationLink relation) {
      instance.relationBranchCache.remove(relation.getPersistenceMemo().getLinkId(), relation.getBranch());
   }

   public static RelationLink getRelation(Integer relationId, Branch branch) {
      return instance.relationBranchCache.get(relationId, branch);
   }

   public static RelationLink getRelation(Integer relationId, Integer transactionId) {
      return instance.historicalRelationCache.get(relationId, transactionId);
   }
}