/*
 * Created on Oct 1, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.core.ds.RelationRow;

public class RelationRowCollection {

   private final Map<Integer, List<RelationRow>> relations;
   private final int parentId;

   RelationRowCollection(int parentId) {
      this.parentId = parentId;
      relations = new ConcurrentHashMap<Integer, List<RelationRow>>();
   }

   public void add(RelationRow nextRelation) {
      List<RelationRow> rows = relations.get(nextRelation.getRelationTypeId());
      if (rows == null) {
         rows = new CopyOnWriteArrayList<RelationRow>();
         relations.put(nextRelation.getRelationTypeId(), rows);
      }
      rows.add(nextRelation);
   }

   public void getArtifactIds(Collection<Integer> results, int relationTypeId, RelationSide side) {
      List<RelationRow> rows = relations.get(relationTypeId);
      if (rows != null) {
         for (RelationRow row : rows) {
            if (side.equals(RelationSide.SIDE_B) && row.getArtIdA() == parentId) {
               results.add(row.getArtIdB());
            } else if (side.equals(RelationSide.SIDE_A) && row.getArtIdB() == parentId) {
               results.add(row.getArtIdA());
            }
         }
      }
   }

   public int getArtifactCount(int relationTypeId, RelationSide side) {
      List<RelationRow> rows = relations.get(relationTypeId);
      int count = 0;
      if (rows != null) {
         for (RelationRow row : rows) {
            if (row.getArtIdA() == parentId && side.equals(RelationSide.SIDE_B)) {
               count++;
            } else if (row.getArtIdB() == parentId && side.equals(RelationSide.SIDE_A)) {
               count++;
            }
         }
      }
      return count;
   }

}
