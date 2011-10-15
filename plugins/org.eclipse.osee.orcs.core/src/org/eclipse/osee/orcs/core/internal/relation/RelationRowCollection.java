/*
 * Created on Oct 1, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.orcs.core.ds.RelationRow;

public class RelationRowCollection {

   private final Map<IRelationTypeSide, List<RelationRow>> relations;
   private final int parentId;
   private final RelationTypeCache relationTypeCache;

   RelationRowCollection(int parentId, RelationTypeCache relationTypeCache) {
      this.parentId = parentId;
      this.relationTypeCache = relationTypeCache;
      relations = new ConcurrentHashMap<IRelationTypeSide, List<RelationRow>>();
   }

   public void add(RelationRow nextRelation) throws OseeCoreException {
      IRelationType type = relationTypeCache.getByGuid(nextRelation.getRelationTypeUUId());
      if (type == null) {
         throw new OseeCoreException("Unknown relation type.  UUID[%d]", nextRelation.getRelationTypeUUId());
      }
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
            if (relationTypeSide.getSide().equals(RelationSide.SIDE_B)) {
               results.add(row.getArtIdB());
            } else {
               results.add(row.getArtIdA());
            }
         }
      }
   }

   public int getArtifactCount(IRelationTypeSide relationTypeSide) {
      List<RelationRow> rows = relations.get(relationTypeSide);
      if (rows == null) {
         return 0;
      } else {
         return rows.size();
      }
   }

   public Set<IRelationTypeSide> getRelationTypes() {
      return relations.keySet();
   }

}
