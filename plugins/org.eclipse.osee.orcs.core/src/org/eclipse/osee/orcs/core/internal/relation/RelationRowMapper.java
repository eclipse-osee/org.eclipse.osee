/*
 * Created on Sep 28, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.ds.RelationRow;
import org.eclipse.osee.orcs.core.ds.RelationRowHandler;

public class RelationRowMapper implements RelationRowHandler {

   private final RelationFactory factory;
   private final RelationContainer container;
   private final Log logger;

   public RelationRowMapper(Log logger, RelationContainer container, RelationFactory factory) {
      this.logger = logger;
      this.container = container;
      this.factory = factory;
   }

   @Override
   public void onRow(RelationRow nextRelation) {
      if (container == null) {
         return; // If the artifact is null, it means the relations are orphaned.
      }
      factory.loadRelation(container, nextRelation);
   }
}
