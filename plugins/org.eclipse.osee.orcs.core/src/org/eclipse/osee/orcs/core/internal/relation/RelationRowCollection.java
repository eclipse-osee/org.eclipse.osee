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
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationRowCollection {

   private final Map<IRelationTypeSide, List<RelationData>> relations =
      new ConcurrentHashMap<IRelationTypeSide, List<RelationData>>();

   private final int parentId;
   private final RelationTypes relationTypeCache;

   RelationRowCollection(int parentId, RelationTypes relationTypeCache) {
      this.parentId = parentId;
      this.relationTypeCache = relationTypeCache;
   }

   private IRelationType getRelationType(RelationData relationRow) throws OseeCoreException {
      long uuid = relationRow.getTypeUuid();
      IRelationType type = relationTypeCache.getByUuid(uuid);
      Conditions.checkNotNull(type, "RelationType", "Unknown relation type.  UUID[%d]", uuid);
      return type;
   }

   private RelationSide getRelationSide(RelationData row) {
      if (row.getArtIdA() == parentId) {
         return RelationSide.SIDE_B;
      } else { //row.getArtIdB() == parentId
         return RelationSide.SIDE_A;
      }
   }

   private IRelationTypeSide getRelationTypeSide(RelationData relationRow) throws OseeCoreException {
      IRelationType type = getRelationType(relationRow);
      RelationSide side = getRelationSide(relationRow);
      IRelationTypeSide relationTypeSide = TokenFactory.createRelationTypeSide(side, type.getGuid(), type.getName());
      return relationTypeSide;
   }

   public void add(RelationData nextRelation) throws OseeCoreException {
      IRelationTypeSide relationTypeSide = getRelationTypeSide(nextRelation);
      List<RelationData> rows = relations.get(relationTypeSide);
      if (rows == null) {
         rows = new CopyOnWriteArrayList<RelationData>();
         relations.put(relationTypeSide, rows);
      }
      rows.add(nextRelation);
   }

   public void getArtifactIds(Collection<Integer> results, IRelationTypeSide relationTypeSide) {
      List<RelationData> rows = relations.get(relationTypeSide);
      if (rows != null) {
         for (RelationData row : rows) {
            Integer artId = row.getArtIdOn(relationTypeSide.getSide());
            results.add(artId);
         }
      }
   }

   public int getArtifactCount(IRelationTypeSide relationTypeSide) {
      List<RelationData> rows = relations.get(relationTypeSide);
      return rows != null ? rows.size() : 0;
   }

   public Set<IRelationTypeSide> getRelationTypes() {
      return relations.keySet();
   }

   public Collection<List<RelationData>> getRelationData() {
      return relations.values();
   }

}
