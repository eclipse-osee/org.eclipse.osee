/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.ArtifactQuery;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.ds.RelationRow;

public class RelationContainerImpl implements RelationContainer {

   private final int parentId;
   //   private final ArtifactQuery artifactQuery;
   private final RelationRowCollection rows;

   RelationContainerImpl(int parentId, ArtifactQuery artifactQuery) {
      this.parentId = parentId;
      //      this.artifactQuery = artifactQuery;
      this.rows = new RelationRowCollection(parentId);
   }

   @Override
   public void add(RelationRow nextRelation) {
      rows.add(nextRelation);
   }

   @Override
   public int getParentId() {
      return parentId;
   }

   void getArtifactIds(List<Integer> results, int relationTypeId, RelationSide side) {
      rows.getArtifactIds(results, relationTypeId, side);
   }

   int getRelationCount(int relationTypeId, RelationSide side) {
      return rows.getArtifactCount(relationTypeId, side);
   }
}
