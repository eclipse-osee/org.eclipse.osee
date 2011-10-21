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
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.RelationRow;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationRowCollection {

   private final Map<IRelationTypeSide, List<RelationRow>> relations =
      new ConcurrentHashMap<IRelationTypeSide, List<RelationRow>>();

   private final int parentId;
   private final RelationTypeCache relationTypeCache;

   RelationRowCollection(int parentId, RelationTypeCache relationTypeCache) {
      this.parentId = parentId;
      this.relationTypeCache = relationTypeCache;
   }

   public void add(RelationRow nextRelation) throws OseeCoreException {
      IRelationType type = relationTypeCache.getByGuid(nextRelation.getRelationTypeUUId());
      Conditions.checkNotNull(type, "RelationType", "Unknown relation type.  UUID[%d]",
         nextRelation.getRelationTypeUUId());
      IRelationTypeSide relationTypeSide =
         TokenFactory.createRelationTypeSide(getRelationSide(nextRelation), type.getGuid(), type.getName());

      List<RelationRow> rows = relations.get(relationTypeSide);
      if (rows == null) {
         rows = new CopyOnWriteArrayList<RelationRow>();
         relations.put(relationTypeSide, rows);
      }
      rows.add(nextRelation);
   }

   private RelationSide getRelationSide(RelationRow row) {
      if (row.getArtIdA() == parentId) {
         return RelationSide.SIDE_B;
      } else { //row.getArtIdB() == parentId
         return RelationSide.SIDE_A;
      }
   }

   public void getArtifactIds(Collection<Integer> results, IRelationTypeSide relationTypeSide) {
      List<RelationRow> rows = relations.get(relationTypeSide);
      if (rows != null) {
         for (RelationRow row : rows) {
            Integer artId = row.getArtIdOn(relationTypeSide.getSide());
            results.add(artId);
         }
      }
   }

   public int getArtifactCount(IRelationTypeSide relationTypeSide) {
      List<RelationRow> rows = relations.get(relationTypeSide);
      return rows != null ? rows.size() : 0;
   }

   public Set<IRelationTypeSide> getRelationTypes() {
      return relations.keySet();
   }

}
