/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.ds.RelationRow;

public class RelationContainerImpl implements RelationContainer {

   private final RelationRowCollection rows;

   public RelationContainerImpl(int parentId) {
      this.rows = new RelationRowCollection(parentId);
   }

   @Override
   public void add(RelationRow nextRelation) {
      rows.add(nextRelation);
   }

   @Override
   public void getArtifactIds(List<Integer> results, int relationTypeId, RelationSide side) {
      rows.getArtifactIds(results, relationTypeId, side);
   }

   @Override
   public int getRelationCount(int relationTypeId, RelationSide side) {
      return rows.getArtifactCount(relationTypeId, side);
   }
}
