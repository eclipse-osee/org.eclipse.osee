/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.ds.RelationRow;

public class RelationContainerImpl implements RelationContainer {

   private final RelationRowCollection rows;

   public RelationContainerImpl(int parentId, RelationTypeCache relationTypeCache) {
      this.rows = new RelationRowCollection(parentId, relationTypeCache);
   }

   @Override
   public void add(RelationRow nextRelation) throws OseeCoreException {
      rows.add(nextRelation);
   }

   @Override
   public void getArtifactIds(Collection<Integer> results, IRelationTypeSide relationTypeSide) {
      rows.getArtifactIds(results, relationTypeSide);
   }

   @Override
   public int getRelationCount(IRelationTypeSide relationTypeSide) {
      return rows.getArtifactCount(relationTypeSide);
   }

   @Override
   public Collection<IRelationTypeSide> getAvailableRelationTypes() {
      return rows.getRelationTypes();
   }
}
